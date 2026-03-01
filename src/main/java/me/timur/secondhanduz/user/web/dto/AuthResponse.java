package me.timur.secondhanduz.user.web.dto;

import me.timur.secondhanduz.user.domain.Role;

/** Authentication response containing JWT token and basic user info. */
public record AuthResponse(
        String token,
        Long userId,
        String email,
        Role role
) {}
