package me.timur.secondhanduz.common.exception;

import me.timur.secondhanduz.common.logging.TraceIdFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/**
 * Central exception handler for all REST controllers.
 * Translates exceptions into structured {@link ErrorResponse} bodies.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(ApiException ex) {
        log.warn("ApiException [{}]: {}", ex.getErrorCode(), ex.getMessage());
        var body = ErrorResponse.of(ex.getStatus().value(), ex.getErrorCode(), ex.getMessage(), traceId());
        return ResponseEntity.status(ex.getStatus()).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        List<ErrorResponse.FieldError> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> new ErrorResponse.FieldError(fe.getField(), fe.getDefaultMessage()))
                .toList();
        var body = ErrorResponse.ofValidation(
                HttpStatus.BAD_REQUEST.value(),
                ErrorCode.VALIDATION_ERROR,
                "Validation failed",
                fieldErrors,
                traceId()
        );
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler({AuthenticationException.class, BadCredentialsException.class})
    public ResponseEntity<ErrorResponse> handleAuthException(RuntimeException ex) {
        log.warn("Authentication failed: {}", ex.getMessage());
        var body = ErrorResponse.of(
                HttpStatus.UNAUTHORIZED.value(), ErrorCode.INVALID_CREDENTIALS,
                "Authentication failed", traceId());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        log.warn("Access denied: {}", ex.getMessage());
        var body = ErrorResponse.of(
                HttpStatus.FORBIDDEN.value(), ErrorCode.ACCESS_DENIED,
                "Access denied", traceId());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Unexpected error", ex);
        var body = ErrorResponse.of(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), ErrorCode.INTERNAL_ERROR,
                "An unexpected error occurred", traceId());
        return ResponseEntity.internalServerError().body(body);
    }

    private String traceId() {
        return MDC.get(TraceIdFilter.TRACE_ID_KEY);
    }
}
