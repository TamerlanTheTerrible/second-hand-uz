package me.timur.secondhanduz.listing.infrastructure.persistence;

import me.timur.secondhanduz.listing.application.port.out.ListingRepository;
import me.timur.secondhanduz.listing.domain.Listing;
import me.timur.secondhanduz.listing.domain.ListingCategory;
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
            AND (cast(:query as String)    IS NULL OR lower(l.title) LIKE cast(:query as String) OR lower(l.brand) LIKE cast(:query as String))
            AND (cast(:brand as String)    IS NULL OR lower(l.brand) LIKE cast(:brand as String))
            AND (:minPrice IS NULL OR l.price >= :minPrice)
            AND (:maxPrice IS NULL OR l.price <= :maxPrice)
            AND (cast(:size as String)     IS NULL OR l.size = cast(:size as String))
            AND (:category IS NULL OR l.category = :category)
            ORDER BY l.createdAt DESC
            """)
    Page<Listing> searchListings(
            @Param("query")    String query,
            @Param("brand")    String brand,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("size")     String size,
            @Param("category") ListingCategory category,
            @Param("status")   ListingStatus status,
            Pageable pageable
    );

    @Override
    Page<Listing> findBySellerIdAndStatus(Long sellerId, ListingStatus status, Pageable pageable);
}
