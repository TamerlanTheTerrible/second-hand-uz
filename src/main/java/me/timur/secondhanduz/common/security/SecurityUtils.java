package me.timur.secondhanduz.common.security;

import me.timur.secondhanduz.common.exception.ApiException;
import me.timur.secondhanduz.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Utility class for retrieving authenticated user information from the security context.
 */
public final class SecurityUtils {

    private SecurityUtils() {}

    /**
     * Get the email of the currently authenticated user.
     */
    public static String getCurrentUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, ErrorCode.TOKEN_INVALID, "Not authenticated");
        }
        return auth.getName();
    }

    /**
     * Get the currently authenticated user as a {@link UserPrincipal}.
     */
    public static UserPrincipal getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UserPrincipal principal)) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, ErrorCode.TOKEN_INVALID, "Not authenticated");
        }
        return principal;
    }
}
