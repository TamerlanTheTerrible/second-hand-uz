package me.timur.secondhanduz.common.exception;

/**
 * Application-wide error codes for consistent error identification.
 */
public final class ErrorCode {

    private ErrorCode() {}

    // Auth
    public static final String INVALID_CREDENTIALS    = "AUTH_001";
    public static final String TOKEN_EXPIRED          = "AUTH_002";
    public static final String TOKEN_INVALID          = "AUTH_003";
    public static final String ACCESS_DENIED          = "AUTH_004";
    public static final String EMAIL_ALREADY_EXISTS   = "AUTH_005";

    // User
    public static final String USER_NOT_FOUND         = "USER_001";

    // Listing
    public static final String LISTING_NOT_FOUND      = "LISTING_001";
    public static final String LISTING_NOT_OWNED      = "LISTING_002";
    public static final String LISTING_SOLD           = "LISTING_003";
    public static final String LISTING_NOT_AVAILABLE  = "LISTING_004";

    // Order
    public static final String ORDER_NOT_FOUND        = "ORDER_001";
    public static final String ORDER_CANNOT_CANCEL    = "ORDER_002";
    public static final String ORDER_NOT_COMPLETED    = "ORDER_003";
    public static final String ORDER_ACCESS_DENIED    = "ORDER_004";
    public static final String LISTING_ALREADY_ORDERED = "ORDER_005";

    // Review
    public static final String REVIEW_NOT_ALLOWED     = "REVIEW_001";
    public static final String REVIEW_ALREADY_EXISTS  = "REVIEW_002";

    // Payment
    public static final String PAYMENT_SESSION_FAILED  = "PAYMENT_001";
    public static final String PAYMENT_WEBHOOK_INVALID = "PAYMENT_002";

    // Generic
    public static final String VALIDATION_ERROR       = "GENERIC_001";
    public static final String INTERNAL_ERROR         = "GENERIC_002";
}
