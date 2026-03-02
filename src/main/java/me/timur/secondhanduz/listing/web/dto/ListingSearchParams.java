package me.timur.secondhanduz.listing.web.dto;

import me.timur.secondhanduz.listing.domain.ListingCategory;

import java.math.BigDecimal;

/** Query parameters for listing search and filter. */
public record ListingSearchParams(
        String query,
        String brand,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        String size,
        ListingCategory category
) {}
