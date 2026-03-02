package me.timur.secondhanduz.listing.application.service;

import me.timur.secondhanduz.common.exception.ApiException;
import me.timur.secondhanduz.common.exception.ErrorCode;
import me.timur.secondhanduz.common.logging.AuditLogger;
import me.timur.secondhanduz.common.util.InputSanitizer;
import me.timur.secondhanduz.listing.application.port.in.ListingService;
import me.timur.secondhanduz.listing.application.port.out.ListingRepository;
import me.timur.secondhanduz.listing.domain.Listing;
import me.timur.secondhanduz.listing.domain.ListingStatus;
import me.timur.secondhanduz.listing.web.dto.CreateListingRequest;
import me.timur.secondhanduz.listing.web.dto.ListingResponse;
import me.timur.secondhanduz.listing.web.dto.ListingSearchParams;
import me.timur.secondhanduz.listing.web.dto.UpdateListingRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Implementation of {@link ListingService} use cases.
 */
@Service
public class ListingServiceImpl implements ListingService {

    private final ListingRepository listingRepository;
    private final AuditLogger auditLogService;
    private final InputSanitizer contentSanitizer;

    @Value("${app.upload.path}")
    private String uploadPath;

    public ListingServiceImpl(ListingRepository listingRepository,
                              AuditLogger auditLogService,
                              InputSanitizer contentSanitizer) {
        this.listingRepository = listingRepository;
        this.auditLogService = auditLogService;
        this.contentSanitizer = contentSanitizer;
    }

    @Override
    @Transactional
    public ListingResponse createListing(CreateListingRequest request, Long sellerId) {
        var listing = new Listing(
                sellerId,
                contentSanitizer.sanitize(request.title()),
                contentSanitizer.sanitize(request.description()),
                request.price(),
                request.size(),
                contentSanitizer.sanitize(request.brand()),
                request.condition(),
                request.gender(),
                request.category()
        );
        var saved = listingRepository.save(listing);
        auditLogService.log("LISTING_CREATED", sellerId, saved.getId(),
                "title=" + saved.getTitle());
        return toResponse(saved);
    }

    @Override
    @Transactional
    public ListingResponse updateListing(Long listingId, UpdateListingRequest request, Long userId) {
        var listing = requireActiveListing(listingId);
        requireOwnership(listing, userId);
        listing.update(
                contentSanitizer.sanitize(request.title()),
                contentSanitizer.sanitize(request.description()),
                request.price(),
                request.size(),
                contentSanitizer.sanitize(request.brand()),
                request.condition(),
                request.gender(),
                request.category()
        );
        var saved = listingRepository.save(listing);
        auditLogService.log("LISTING_UPDATED", userId, listingId, "title=" + saved.getTitle());
        return toResponse(saved);
    }

    @Override
    @Transactional
    public void deleteListing(Long listingId, Long userId) {
        var listing = requireActiveListing(listingId);
        requireOwnership(listing, userId);
        listing.markDeleted();
        listingRepository.save(listing);
        auditLogService.log("LISTING_DELETED", userId, listingId, "");
    }

    @Override
    @Transactional(readOnly = true)
    public ListingResponse getListing(Long listingId) {
        return toResponse(findById(listingId));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ListingResponse> getListings(ListingSearchParams params, Pageable pageable) {
        String queryPattern = params.query() != null ? "%" + params.query().toLowerCase() + "%" : null;
        String brandPattern = params.brand() != null ? "%" + params.brand().toLowerCase() + "%" : null;
        return listingRepository.searchListings(
                queryPattern, brandPattern, params.minPrice(),
                params.maxPrice(), params.size(), params.category(), ListingStatus.ACTIVE, pageable
        ).map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ListingResponse> getSellerListings(Long sellerId, Pageable pageable) {
        return listingRepository.findBySellerIdAndStatus(sellerId, ListingStatus.ACTIVE, pageable)
                .map(this::toResponse);
    }

    @Override
    @Transactional
    public String uploadImage(Long listingId, MultipartFile file, Long userId) {
        var listing = requireActiveListing(listingId);
        requireOwnership(listing, userId);
        try {
            Path dir = Paths.get(uploadPath, "listings", listingId.toString());
            Files.createDirectories(dir);
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            file.transferTo(dir.resolve(filename).toFile());
            String url = "/uploads/listings/" + listingId + "/" + filename;
            listing.addImageUrl(url);
            listingRepository.save(listing);
            return url;
        } catch (IOException ex) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL_ERROR,
                    "Failed to upload image", ex);
        }
    }

    // --- helpers ---

    private Listing findById(Long listingId) {
        return listingRepository.findById(listingId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,
                        ErrorCode.LISTING_NOT_FOUND, "Listing not found: " + listingId));
    }

    private Listing requireActiveListing(Long listingId) {
        var listing = findById(listingId);
        if (listing.getStatus() == ListingStatus.SOLD) {
            throw new ApiException(HttpStatus.CONFLICT, ErrorCode.LISTING_SOLD,
                    "Cannot modify a sold listing");
        }
        if (listing.getStatus() == ListingStatus.DELETED) {
            throw new ApiException(HttpStatus.NOT_FOUND, ErrorCode.LISTING_NOT_FOUND,
                    "Listing not found: " + listingId);
        }
        return listing;
    }

    private void requireOwnership(Listing listing, Long userId) {
        if (!listing.isOwnedBy(userId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, ErrorCode.LISTING_NOT_OWNED,
                    "You do not own this listing");
        }
    }

    private ListingResponse toResponse(Listing listing) {
        return new ListingResponse(
                listing.getId(), listing.getSellerId(), listing.getTitle(),
                listing.getDescription(), listing.getPrice(), listing.getSize(),
                listing.getBrand(), listing.getCondition(), listing.getStatus(),
                listing.getGender(), listing.getCategory(), listing.getImageUrls(), listing.getCreatedAt()
        );
    }
}
