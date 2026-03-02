package me.timur.secondhanduz.listing.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import me.timur.secondhanduz.common.security.SecurityUtils;
import me.timur.secondhanduz.listing.application.port.in.ListingService;
import me.timur.secondhanduz.listing.domain.ListingCategory;
import me.timur.secondhanduz.listing.web.dto.CreateListingRequest;
import me.timur.secondhanduz.listing.web.dto.ListingResponse;
import me.timur.secondhanduz.listing.web.dto.ListingSearchParams;
import me.timur.secondhanduz.listing.web.dto.UpdateListingRequest;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

/**
 * REST controller for marketplace listing CRUD and search.
 */
@RestController
@RequestMapping("/api/v1/listings")
@Tag(name = "Listings", description = "Listing CRUD, search, and image upload")
public class ListingController {

    private final ListingService listingService;

    public ListingController(ListingService listingService) {
        this.listingService = listingService;
    }

    @GetMapping
    @Operation(summary = "Browse and search listings (public)")
    public ResponseEntity<Page<ListingResponse>> getListings(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String size,
            @RequestParam(required = false) ListingCategory category,
            @PageableDefault(size = 20, sort = "createdAt") @ParameterObject Pageable pageable) {
        var params = new ListingSearchParams(query, brand, minPrice, maxPrice, size, category);
        return ResponseEntity.ok(listingService.getListings(params, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a single listing (public)")
    public ResponseEntity<ListingResponse> getListing(@PathVariable Long id) {
        return ResponseEntity.ok(listingService.getListing(id));
    }

    @GetMapping("/seller/{sellerId}")
    @Operation(summary = "Get active listings by seller (public)")
    public ResponseEntity<Page<ListingResponse>> getSellerListings(
            @PathVariable Long sellerId,
            @PageableDefault(size = 20) @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(listingService.getSellerListings(sellerId, pageable));
    }

    @PostMapping
    @Operation(summary = "Create a listing",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ListingResponse> createListing(@RequestBody @Valid CreateListingRequest request) {
        Long sellerId = SecurityUtils.getCurrentUser().getId();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(listingService.createListing(request, sellerId));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a listing (owner only)",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ListingResponse> updateListing(@PathVariable Long id,
                                                          @RequestBody @Valid UpdateListingRequest request) {
        Long userId = SecurityUtils.getCurrentUser().getId();
        return ResponseEntity.ok(listingService.updateListing(id, request, userId));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a listing (owner only)",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> deleteListing(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUser().getId();
        listingService.deleteListing(id, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{id}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload an image for a listing",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<String> uploadImage(@PathVariable Long id,
                                               @RequestParam("file") MultipartFile file) {
        Long userId = SecurityUtils.getCurrentUser().getId();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(listingService.uploadImage(id, file, userId));
    }
}
