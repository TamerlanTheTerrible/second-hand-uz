package me.timur.secondhanduz.listing.web.dto;

import me.timur.secondhanduz.listing.domain.Gender;
import me.timur.secondhanduz.listing.domain.ListingCategory;
import me.timur.secondhanduz.listing.domain.ListingCondition;
import me.timur.secondhanduz.listing.domain.ListingStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/** Listing response DTO. */
public record ListingResponse(
        Long id,
        Long sellerId,
        String title,
        String description,
        BigDecimal price,
        String size,
        String brand,
        ListingCondition condition,
        ListingStatus status,
        Gender gender,
        ListingCategory category,
        List<String> imageUrls,
        LocalDateTime createdAt
) {}
