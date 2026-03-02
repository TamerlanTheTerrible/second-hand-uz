package me.timur.secondhanduz.listing.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import me.timur.secondhanduz.listing.domain.Gender;
import me.timur.secondhanduz.listing.domain.ListingCategory;
import me.timur.secondhanduz.listing.domain.ListingCondition;

import java.math.BigDecimal;

/** Request body for creating a new listing. */
public record CreateListingRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 255, message = "Title must be at most 255 characters")
        String title,

        @Size(max = 5000, message = "Description must be at most 5000 characters")
        String description,

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.01", message = "Price must be greater than 0")
        BigDecimal price,

        @Size(max = 50)
        String size,

        @Size(max = 100, message = "Brand must be at most 100 characters")
        String brand,

        @NotNull(message = "Condition is required")
        ListingCondition condition,

        Gender gender,

        @NotNull(message = "Category is required")
        ListingCategory category
) {}
