package me.timur.secondhanduz.user.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import me.timur.secondhanduz.user.domain.Role;

/** Request body for user registration. */
public record RegisterRequest(
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$",
                message = "Password must contain at least one lowercase letter, one uppercase letter, and one digit"
        )
        String password,

        Role role
) {
    public RegisterRequest {
        if (role == null) {
            role = Role.BUYER;
        }
    }
}
