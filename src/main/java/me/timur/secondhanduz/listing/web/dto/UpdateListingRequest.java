package me.timur.secondhanduz.listing.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import me.timur.secondhanduz.listing.domain.ListingCondition;

import java.math.BigDecimal;

/** Request body for updating an existing listing. */
public record UpdateListingRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 255)
        String title,

        @Size(max = 5000)
        String description,

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.01")
        BigDecimal price,

        @Size(max = 50)
        String size,

        @Size(max = 100)
        String brand,

        @NotNull(message = "Condition is required")
        ListingCondition condition
) {}
