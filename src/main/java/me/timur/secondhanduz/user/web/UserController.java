package me.timur.secondhanduz.user.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import me.timur.secondhanduz.common.security.SecurityUtils;
import me.timur.secondhanduz.user.application.port.in.UserService;
import me.timur.secondhanduz.user.web.dto.AuthResponse;
import me.timur.secondhanduz.user.web.dto.LoginRequest;
import me.timur.secondhanduz.user.web.dto.RegisterRequest;
import me.timur.secondhanduz.user.web.dto.UserProfileResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for authentication and user profile endpoints.
 */
@RestController
@RequestMapping("/api/v1")
@Tag(name = "Users", description = "Registration, authentication and user profiles")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/auth/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<AuthResponse> register(@RequestBody @Valid RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.register(request));
    }

    @PostMapping("/auth/login")
    @Operation(summary = "Login and receive a JWT token")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    @GetMapping("/users/me")
    @Operation(summary = "Get current authenticated user's profile",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<UserProfileResponse> getMyProfile() {
        return ResponseEntity.ok(userService.getMyProfile(SecurityUtils.getCurrentUserEmail()));
    }

    @GetMapping("/users/{id}")
    @Operation(summary = "Get user profile by ID",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<UserProfileResponse> getProfile(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getProfile(id));
    }
}
