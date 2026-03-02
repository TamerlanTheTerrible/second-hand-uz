package me.timur.secondhanduz.payment.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.timur.secondhanduz.common.exception.ApiException;
import me.timur.secondhanduz.common.exception.ErrorCode;
import me.timur.secondhanduz.common.logging.AuditLogService;
import me.timur.secondhanduz.order.application.port.in.OrderService;
import me.timur.secondhanduz.order.domain.OrderStatus;
import me.timur.secondhanduz.order.web.dto.UpdateOrderStatusRequest;
import me.timur.secondhanduz.payment.application.port.in.PaymentService;
import me.timur.secondhanduz.payment.web.dto.AtmosWebhookPayload;
import me.timur.secondhanduz.payment.web.dto.PaymentSessionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Map;

/**
 * ATMOS payment provider integration.
 * Reference: https://docs.atmos.uz/
 */
@Service
@Profile("prod")
public class AtmosPaymentServiceImpl implements PaymentService {

    private static final Logger log = LoggerFactory.getLogger(AtmosPaymentServiceImpl.class);
    private static final String HMAC_SHA256 = "HmacSHA256";

    private final WebClient webClient;
    private final OrderService orderService;
    private final AuditLogService auditLogService;
    private final ObjectMapper objectMapper;

    @Value("${app.atmos.store-id}")
    private String storeId;

    @Value("${app.atmos.consumer-key}")
    private String consumerKey;

    @Value("${app.atmos.consumer-secret}")
    private String consumerSecret;

    @Value("${app.atmos.webhook-secret}")
    private String webhookSecret;

    public AtmosPaymentServiceImpl(
            @Value("${app.atmos.base-url}") String atmosBaseUrl,
            OrderService orderService,
            AuditLogService auditLogService,
            ObjectMapper objectMapper) {
        this.webClient = WebClient.builder().baseUrl(atmosBaseUrl).build();
        this.orderService = orderService;
        this.auditLogService = auditLogService;
        this.objectMapper = objectMapper;
    }

    @Override
    public PaymentSessionResponse createPaymentSession(Long orderId, Long userId) {
        var order = orderService.findByIdInternal(orderId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,
                        ErrorCode.ORDER_NOT_FOUND, "Order not found: " + orderId));

        if (!order.getBuyerId().equals(userId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, ErrorCode.ORDER_ACCESS_DENIED, "Access denied");
        }

        try {
            String token = fetchAccessToken();
            // Amount in tiyin (1 UZS = 100 tiyin)
            long amountTiyin = order.getTotalPrice()
                    .multiply(BigDecimal.valueOf(100)).longValue();

            @SuppressWarnings("unchecked")
            Map<String, Object> resp = webClient.post()
                    .uri("/partner/api/process/pay/mcommerces/create-transaction")
                    .header("Authorization", "Bearer " + token)
                    .bodyValue(Map.of(
                            "amount",    amountTiyin,
                            "store_id",  storeId,
                            "account",   orderId.toString()))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (resp == null) {
                throw new ApiException(HttpStatus.BAD_GATEWAY, ErrorCode.PAYMENT_SESSION_FAILED,
                        "Empty response from ATMOS");
            }

            String sessionId  = String.valueOf(resp.getOrDefault("transaction_id", ""));
            String paymentUrl = String.valueOf(resp.getOrDefault("payment_url", ""));

            auditLogService.log("PAYMENT_SESSION_CREATED", userId, orderId,
                    "sessionId=" + sessionId);
            return new PaymentSessionResponse(sessionId, paymentUrl, orderId);

        } catch (ApiException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Failed to create ATMOS payment session for order {}", orderId, ex);
            throw new ApiException(HttpStatus.BAD_GATEWAY, ErrorCode.PAYMENT_SESSION_FAILED,
                    "Failed to create payment session", ex);
        }
    }

    @Override
    public void handleWebhook(String rawPayload, String signature) {
        verifySignature(rawPayload, signature);

        AtmosWebhookPayload payload;
        try {
            payload = objectMapper.readValue(rawPayload, AtmosWebhookPayload.class);
        } catch (JsonProcessingException ex) {
            throw new ApiException(HttpStatus.BAD_REQUEST, ErrorCode.PAYMENT_WEBHOOK_INVALID,
                    "Invalid webhook payload", ex);
        }

        if (!"success".equalsIgnoreCase(payload.status())) {
            log.info("ATMOS webhook: non-success status={}", payload.status());
            return;
        }

        try {
            Long orderId = Long.parseLong(payload.storeTransaction());
            orderService.findByIdInternal(orderId).ifPresent(order -> {
                if (!order.isPaid()) {
                    orderService.updateStatus(orderId,
                            new UpdateOrderStatusRequest(OrderStatus.PAID), order.getBuyerId());
                    auditLogService.log("PAYMENT_CONFIRMED", order.getBuyerId(), orderId,
                            "transactionId=" + payload.transactionId());
                }
            });
        } catch (NumberFormatException ex) {
            log.warn("Cannot parse orderId from ATMOS webhook storeTransaction: {}",
                    payload.storeTransaction());
        }
    }

    private String fetchAccessToken() {
        String encoded = Base64.getEncoder()
                .encodeToString((consumerKey + ":" + consumerSecret)
                        .getBytes(StandardCharsets.UTF_8));
        @SuppressWarnings("unchecked")
        Map<String, Object> resp = webClient.post()
                .uri("/partner/api/token")
                .header("Authorization", "Basic " + encoded)
                .bodyValue(Map.of("grant_type", "client_credentials"))
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (resp == null || !resp.containsKey("access_token")) {
            throw new ApiException(HttpStatus.BAD_GATEWAY, ErrorCode.PAYMENT_SESSION_FAILED,
                    "Failed to obtain ATMOS access token");
        }
        return (String) resp.get("access_token");
    }

    private void verifySignature(String payload, String signature) {
        try {
            Mac mac = Mac.getInstance(HMAC_SHA256);
            mac.init(new SecretKeySpec(
                    webhookSecret.getBytes(StandardCharsets.UTF_8), HMAC_SHA256));
            String computed = HexFormat.of()
                    .formatHex(mac.doFinal(payload.getBytes(StandardCharsets.UTF_8)));
            if (!computed.equals(signature)) {
                throw new ApiException(HttpStatus.UNAUTHORIZED, ErrorCode.PAYMENT_WEBHOOK_INVALID,
                        "Invalid webhook signature");
            }
        } catch (NoSuchAlgorithmException | InvalidKeyException ex) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL_ERROR,
                    "Signature verification error", ex);
        }
    }
}
