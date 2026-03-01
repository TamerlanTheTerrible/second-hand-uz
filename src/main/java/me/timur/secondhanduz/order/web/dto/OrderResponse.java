package me.timur.secondhanduz.order.web.dto;

import me.timur.secondhanduz.order.domain.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Order response DTO. */
public record OrderResponse(
        Long id,
        Long buyerId,
        Long listingId,
        BigDecimal totalPrice,
        OrderStatus status,
        LocalDateTime createdAt
) {}
