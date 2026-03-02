package me.timur.secondhanduz.payment.application.service;

import me.timur.secondhanduz.common.exception.ApiException;
import me.timur.secondhanduz.common.exception.ErrorCode;
import me.timur.secondhanduz.order.application.port.in.OrderService;
import me.timur.secondhanduz.payment.application.port.in.PaymentService;
import me.timur.secondhanduz.payment.web.dto.PaymentSessionResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

/**
 * Mock payment service for development and testing.
 * Generates a local payment confirmation page URL instead of calling a real payment provider.
 */
@Service
@Profile("!prod")
public class MockPaymentServiceImpl implements PaymentService {

    private final OrderService orderService;

    @Value("${APP_FRONTEND_URL:http://localhost:5173}")
    private String frontendUrl;

    public MockPaymentServiceImpl(OrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    public PaymentSessionResponse createPaymentSession(Long orderId, Long userId) {
        // Verify the order exists and belongs to the user
        orderService.findByIdInternal(orderId)
                .filter(o -> o.getBuyerId().equals(userId))
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,
                        ErrorCode.ORDER_NOT_FOUND, "Order not found: " + orderId));

        String paymentUrl = frontendUrl + "/mock-payment/" + orderId;
        return new PaymentSessionResponse("mock-" + orderId, paymentUrl, orderId);
    }

    @Override
    public void handleWebhook(String rawPayload, String signature) {
        // No-op in mock mode — payments are confirmed via /mock-confirm endpoint
    }
}
