package me.timur.secondhanduz.user.application.port.in;

import me.timur.secondhanduz.user.web.dto.AuthResponse;
import me.timur.secondhanduz.user.web.dto.LoginRequest;
import me.timur.secondhanduz.user.web.dto.RegisterRequest;
import me.timur.secondhanduz.user.web.dto.UserProfileResponse;

/**
 * Input port: use cases for the User module.
 */
public interface UserService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    UserProfileResponse getProfile(Long userId);

    UserProfileResponse getMyProfile(String email);

    /** Recalculate and persist the seller's average rating. */
    void updateSellerRating(Long sellerId);
}
