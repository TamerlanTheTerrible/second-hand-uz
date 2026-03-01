package me.timur.secondhanduz.listing.web.dto;

import java.math.BigDecimal;

/** Query parameters for listing search and filter. */
public record ListingSearchParams(
        String query,
        String brand,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        String size
) {}
