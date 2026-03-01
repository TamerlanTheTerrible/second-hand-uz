package me.timur.secondhanduz.order.web.dto;

import jakarta.validation.constraints.NotNull;

/** Request body for creating an order. */
public record CreateOrderRequest(
        @NotNull(message = "Listing ID is required")
        Long listingId
) {}
