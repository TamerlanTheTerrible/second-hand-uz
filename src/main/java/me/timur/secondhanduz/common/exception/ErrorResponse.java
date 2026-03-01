package me.timur.secondhanduz.common.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;

/**
 * Standardized error response returned by the API.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        int status,
        String errorCode,
        String message,
        List<FieldError> fieldErrors,
        String traceId,
        Instant timestamp
) {
    public record FieldError(String field, String message) {}

    public static ErrorResponse of(int status, String errorCode, String message, String traceId) {
        return new ErrorResponse(status, errorCode, message, null, traceId, Instant.now());
    }

    public static ErrorResponse ofValidation(int status, String errorCode, String message,
                                             List<FieldError> fieldErrors, String traceId) {
        return new ErrorResponse(status, errorCode, message, fieldErrors, traceId, Instant.now());
    }
}
