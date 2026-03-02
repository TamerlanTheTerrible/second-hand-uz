package me.timur.secondhanduz.payment.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import me.timur.secondhanduz.common.security.SecurityUtils;
import me.timur.secondhanduz.order.application.port.in.OrderService;
import me.timur.secondhanduz.order.domain.OrderStatus;
import me.timur.secondhanduz.order.web.dto.OrderResponse;
import me.timur.secondhanduz.order.web.dto.UpdateOrderStatusRequest;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Mock payment controller — only active in non-production profiles.
 * Simulates payment confirmation without calling a real payment provider.
 */
@RestController
@Profile("!prod")
@RequestMapping("/api/v1/payments")
@Tag(name = "Payments (Mock)", description = "Mock payment confirmation for development")
public class MockPaymentController {

    private final OrderService orderService;

    public MockPaymentController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/mock-confirm/{orderId}")
    @Operation(summary = "Confirm mock payment — marks order PAID and listing SOLD",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<OrderResponse> confirmPayment(@PathVariable Long orderId) {
        Long userId = SecurityUtils.getCurrentUser().getId();
        OrderResponse response = orderService.updateStatus(
                orderId, new UpdateOrderStatusRequest(OrderStatus.PAID), userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/mock-info/{orderId}")
    @Operation(summary = "Get order info for mock payment page",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<OrderResponse> getOrderInfo(@PathVariable Long orderId) {
        Long userId = SecurityUtils.getCurrentUser().getId();
        return ResponseEntity.ok(orderService.getOrder(orderId, userId));
    }
}
