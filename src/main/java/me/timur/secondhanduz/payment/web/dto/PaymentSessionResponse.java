package me.timur.secondhanduz.payment.web.dto;

/** Payment session response containing the URL to redirect the buyer to. */
public record PaymentSessionResponse(
        String sessionId,
        String paymentUrl,
        Long orderId
) {}
