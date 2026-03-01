package me.timur.secondhanduz.review.web.dto;

import java.time.LocalDateTime;

/** Review response DTO. */
public record ReviewResponse(
        Long id,
        Long reviewerId,
        Long reviewedUserId,
        Long orderId,
        Integer rating,
        String comment,
        LocalDateTime createdAt
) {}
