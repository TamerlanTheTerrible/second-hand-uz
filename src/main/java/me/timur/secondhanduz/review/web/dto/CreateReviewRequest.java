package me.timur.secondhanduz.review.web.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Request body for submitting a review. */
public record CreateReviewRequest(
        @NotNull(message = "Order ID is required")
        Long orderId,

        @NotNull(message = "Seller ID is required")
        Long sellerId,

        @NotNull(message = "Rating is required")
        @Min(value = 1, message = "Rating must be at least 1")
        @Max(value = 5, message = "Rating must be at most 5")
        Integer rating,

        @Size(max = 1000, message = "Comment must be at most 1000 characters")
        String comment
) {}
