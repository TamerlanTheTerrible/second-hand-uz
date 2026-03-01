package me.timur.secondhanduz.payment.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import me.timur.secondhanduz.common.security.SecurityUtils;
import me.timur.secondhanduz.payment.application.port.in.PaymentService;
import me.timur.secondhanduz.payment.web.dto.PaymentSessionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for payment operations.
 */
@RestController
@RequestMapping("/api/v1/payments")
@Tag(name = "Payments", description = "Payment session creation and ATMOS webhook handling")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/session/{orderId}")
    @Operation(summary = "Create a payment session for an order",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<PaymentSessionResponse> createSession(@PathVariable Long orderId) {
        Long userId = SecurityUtils.getCurrentUser().getId();
        return ResponseEntity.ok(paymentService.createPaymentSession(orderId, userId));
    }

    @PostMapping("/webhook")
    @Operation(summary = "ATMOS payment webhook (signature verified internally, public endpoint)")
    public ResponseEntity<Void> handleWebhook(
            @RequestBody String rawPayload,
            @RequestHeader(value = "X-Atmos-Signature", defaultValue = "") String signature) {
        paymentService.handleWebhook(rawPayload, signature);
        return ResponseEntity.ok().build();
    }
}
