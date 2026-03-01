package me.timur.secondhanduz.order.web.dto;

import jakarta.validation.constraints.NotNull;
import me.timur.secondhanduz.order.domain.OrderStatus;

/** Request for updating an order's status (admin / seller action). */
public record UpdateOrderStatusRequest(
        @NotNull(message = "Status is required")
        OrderStatus status
) {}
