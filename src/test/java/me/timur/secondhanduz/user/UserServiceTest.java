package me.timur.secondhanduz.user;

import me.timur.secondhanduz.common.exception.ApiException;
import me.timur.secondhanduz.common.logging.AuditLogger;
import me.timur.secondhanduz.common.security.TokenProvider;
import me.timur.secondhanduz.user.application.port.out.UserRepository;
import me.timur.secondhanduz.user.application.service.ReviewAverageProvider;
import me.timur.secondhanduz.user.application.service.UserServiceImpl;
import me.timur.secondhanduz.user.domain.Role;
import me.timur.secondhanduz.user.domain.User;
import me.timur.secondhanduz.user.web.dto.AuthResponse;
import me.timur.secondhanduz.user.web.dto.LoginRequest;
import me.timur.secondhanduz.user.web.dto.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private TokenProvider jwtTokenProvider;
    @Mock private AuditLogger auditLogService;
    @Mock private ReviewAverageProvider reviewAverageProvider;

    @InjectMocks
    private UserServiceImpl userService;

    private User existingUser;

    @BeforeEach
    void setUp() {
        existingUser = new User("test@example.com", "hashedPass", Role.BUYER);
    }

    @Test
    void should_registerUser_when_emailIsNew() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPass");
        when(userRepository.save(any())).thenReturn(existingUser);
        when(jwtTokenProvider.generateToken(any())).thenReturn("token123");

        AuthResponse response = userService.register(
                new RegisterRequest("new@example.com", "Password1", Role.BUYER));

        assertThat(response.token()).isEqualTo("token123");
        assertThat(response.email()).isEqualTo("test@example.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void should_throwConflict_when_emailAlreadyRegistered() {
        when(userRepository.existsByEmail("taken@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.register(
                new RegisterRequest("taken@example.com", "Password1", Role.BUYER)))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("already registered");
    }

    @Test
    void should_returnToken_when_credentialsAreValid() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("password", "hashedPass")).thenReturn(true);
        when(jwtTokenProvider.generateToken(any())).thenReturn("jwt");

        AuthResponse response = userService.login(new LoginRequest("test@example.com", "password"));

        assertThat(response.token()).isEqualTo("jwt");
    }

    @Test
    void should_throwUnauthorized_when_passwordIsWrong() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThatThrownBy(() -> userService.login(
                new LoginRequest("test@example.com", "wrong")))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Invalid credentials");
    }

    @Test
    void should_throwUnauthorized_when_userNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.login(
                new LoginRequest("ghost@example.com", "pass")))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Invalid credentials");
    }

    @Test
    void should_throwNotFound_when_profileIdDoesNotExist() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getProfile(999L))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("not found");
    }
}
