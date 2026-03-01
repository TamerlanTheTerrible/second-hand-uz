package me.timur.secondhanduz.user.web.dto;

import me.timur.secondhanduz.user.domain.Role;

import java.time.LocalDateTime;

/** Public user profile response. */
public record UserProfileResponse(
        Long id,
        String email,
        Role role,
        Double rating,
        LocalDateTime createdAt
) {}
