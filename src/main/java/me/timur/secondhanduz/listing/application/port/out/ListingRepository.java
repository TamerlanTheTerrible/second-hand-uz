package me.timur.secondhanduz.listing.application.port.out;

import me.timur.secondhanduz.listing.domain.Listing;
import me.timur.secondhanduz.listing.domain.ListingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Output port: persistence contract for {@link Listing} entities.
 */
public interface ListingRepository {

    Listing save(Listing listing);

    Optional<Listing> findById(Long id);

    Page<Listing> searchListings(String query, String brand, BigDecimal minPrice,
                                  BigDecimal maxPrice, String size, ListingStatus status,
                                  Pageable pageable);

    Page<Listing> findBySellerIdAndStatus(Long sellerId, ListingStatus status, Pageable pageable);
}
