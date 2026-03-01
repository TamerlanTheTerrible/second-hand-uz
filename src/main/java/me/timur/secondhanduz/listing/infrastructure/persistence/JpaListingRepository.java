package me.timur.secondhanduz.listing.infrastructure.persistence;

import me.timur.secondhanduz.listing.application.port.out.ListingRepository;
import me.timur.secondhanduz.listing.domain.Listing;
import me.timur.secondhanduz.listing.domain.ListingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

/**
 * JPA adapter fulfilling the {@link ListingRepository} output port.
 */
@Repository
public interface JpaListingRepository extends JpaRepository<Listing, Long>, ListingRepository {

    @Override
    @Query("""
            SELECT l FROM Listing l
            WHERE l.status = :status
            AND (:query   IS NULL OR lower(l.title) LIKE lower(concat('%', :query, '%'))
                                  OR lower(l.brand) LIKE lower(concat('%', :query, '%')))
            AND (:brand    IS NULL OR lower(l.brand) LIKE lower(concat('%', :brand, '%')))
            AND (:minPrice IS NULL OR l.price >= :minPrice)
            AND (:maxPrice IS NULL OR l.price <= :maxPrice)
            AND (:size     IS NULL OR l.size = :size)
            ORDER BY l.createdAt DESC
            """)
    Page<Listing> searchListings(
            @Param("query")    String query,
            @Param("brand")    String brand,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("size")     String size,
            @Param("status")   ListingStatus status,
            Pageable pageable
    );

    @Override
    Page<Listing> findBySellerIdAndStatus(Long sellerId, ListingStatus status, Pageable pageable);
}
