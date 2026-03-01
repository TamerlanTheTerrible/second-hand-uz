package me.timur.secondhanduz.listing.application.port.in;

import me.timur.secondhanduz.listing.web.dto.CreateListingRequest;
import me.timur.secondhanduz.listing.web.dto.ListingResponse;
import me.timur.secondhanduz.listing.web.dto.ListingSearchParams;
import me.timur.secondhanduz.listing.web.dto.UpdateListingRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

/**
 * Input port: use cases for the Listing module.
 */
public interface ListingService {

    ListingResponse createListing(CreateListingRequest request, Long sellerId);

    ListingResponse updateListing(Long listingId, UpdateListingRequest request, Long userId);

    void deleteListing(Long listingId, Long userId);

    ListingResponse getListing(Long listingId);

    Page<ListingResponse> getListings(ListingSearchParams params, Pageable pageable);

    Page<ListingResponse> getSellerListings(Long sellerId, Pageable pageable);

    String uploadImage(Long listingId, MultipartFile file, Long userId);
}
