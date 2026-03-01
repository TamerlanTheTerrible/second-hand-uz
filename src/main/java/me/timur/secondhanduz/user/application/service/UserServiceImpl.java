package me.timur.secondhanduz.user.application.service;

import me.timur.secondhanduz.common.exception.ApiException;
import me.timur.secondhanduz.common.exception.ErrorCode;
import me.timur.secondhanduz.common.logging.AuditLogger;
import org.springframework.context.annotation.Lazy;
import me.timur.secondhanduz.common.security.TokenProvider;
import me.timur.secondhanduz.common.security.UserPrincipal;
import me.timur.secondhanduz.user.application.port.in.UserService;
import me.timur.secondhanduz.user.application.port.out.UserRepository;
import me.timur.secondhanduz.user.domain.Role;
import me.timur.secondhanduz.user.domain.User;
import me.timur.secondhanduz.user.web.dto.AuthResponse;
import me.timur.secondhanduz.user.web.dto.LoginRequest;
import me.timur.secondhanduz.user.web.dto.RegisterRequest;
import me.timur.secondhanduz.user.web.dto.UserProfileResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.OptionalDouble;

/**
 * Implementation of {@link UserService} use cases.
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider jwtTokenProvider;
    private final AuditLogger auditLogService;
    private final ReviewAverageProvider reviewAverageProvider;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           TokenProvider jwtTokenProvider,
                           AuditLogger auditLogService,
                           @Lazy ReviewAverageProvider reviewAverageProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.auditLogService = auditLogService;
        this.reviewAverageProvider = reviewAverageProvider;
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ApiException(HttpStatus.CONFLICT, ErrorCode.EMAIL_ALREADY_EXISTS,
                    "Email already registered: " + request.email());
        }
        Role role = request.role() != null ? request.role() : Role.BUYER;
        var user = new User(request.email(), passwordEncoder.encode(request.password()), role);
        var saved = userRepository.save(user);
        auditLogService.log("USER_REGISTERED", saved.getId(), "email=" + saved.getEmail());
        return buildAuthResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        var user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED,
                        ErrorCode.INVALID_CREDENTIALS, "Invalid credentials"));
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED,
                    ErrorCode.INVALID_CREDENTIALS, "Invalid credentials");
        }
        auditLogService.log("USER_LOGIN", user.getId(), "email=" + user.getEmail());
        return buildAuthResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getProfile(Long userId) {
        return toResponse(findUserById(userId));
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getMyProfile(String email) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,
                        ErrorCode.USER_NOT_FOUND, "User not found: " + email));
        return toResponse(user);
    }

    @Override
    @Transactional
    public void updateSellerRating(Long sellerId) {
        var user = findUserById(sellerId);
        List<Double> ratings = reviewAverageProvider.getRatingsForUser(sellerId);
        OptionalDouble avg = ratings.stream().mapToDouble(Double::doubleValue).average();
        user.updateRating(avg.orElse(0.0));
        userRepository.save(user);
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,
                        ErrorCode.USER_NOT_FOUND, "User not found: " + userId));
    }

    private AuthResponse buildAuthResponse(User user) {
        var principal = new UserPrincipal(user.getId(), user.getEmail(),
                user.getPasswordHash(), user.getRole().name());
        return new AuthResponse(jwtTokenProvider.generateToken(principal),
                user.getId(), user.getEmail(), user.getRole());
    }

    private UserProfileResponse toResponse(User user) {
        return new UserProfileResponse(user.getId(), user.getEmail(),
                user.getRole(), user.getRating(), user.getCreatedAt());
    }
}
