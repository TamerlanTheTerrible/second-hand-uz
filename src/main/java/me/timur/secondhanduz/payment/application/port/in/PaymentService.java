package me.timur.secondhanduz.payment.application.port.in;

import me.timur.secondhanduz.payment.web.dto.PaymentSessionResponse;

/**
 * Input port: payment provider abstraction.
 * Implement this interface per provider to keep business logic decoupled from payment APIs.
 */
public interface PaymentService {

    /**
     * Create a payment session for the given order.
     *
     * @param orderId the order to pay for
     * @param userId  the authenticated user making the payment
     * @return response with the redirect URL and session ID
     */
    PaymentSessionResponse createPaymentSession(Long orderId, Long userId);

    /**
     * Process a payment provider webhook. Validates the signature and updates the order status.
     *
     * @param rawPayload raw request body bytes (used for signature computation)
     * @param signature  provider-supplied HMAC signature header
     */
    void handleWebhook(String rawPayload, String signature);
}
