package me.timur.secondhanduz.order.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import me.timur.secondhanduz.common.security.SecurityUtils;
import me.timur.secondhanduz.order.application.port.in.OrderService;
import me.timur.secondhanduz.order.web.dto.CreateOrderRequest;
import me.timur.secondhanduz.order.web.dto.OrderResponse;
import me.timur.secondhanduz.order.web.dto.UpdateOrderStatusRequest;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for order placement and management.
 */
@RestController
@RequestMapping("/api/v1/orders")
@Tag(name = "Orders", description = "Order placement and status management")
@SecurityRequirement(name = "bearerAuth")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @Operation(summary = "Place an order (buy a listing)")
    public ResponseEntity<OrderResponse> createOrder(@RequestBody @Valid CreateOrderRequest request) {
        Long buyerId = SecurityUtils.getCurrentUser().getId();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.createOrder(request, buyerId));
    }

    @GetMapping
    @Operation(summary = "List my orders")
    public ResponseEntity<Page<OrderResponse>> getMyOrders(
            @PageableDefault(size = 20) @ParameterObject Pageable pageable) {
        Long buyerId = SecurityUtils.getCurrentUser().getId();
        return ResponseEntity.ok(orderService.getMyOrders(buyerId, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order by ID")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUser().getId();
        return ResponseEntity.ok(orderService.getOrder(id, userId));
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel an order (only if CREATED)")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUser().getId();
        return ResponseEntity.ok(orderService.cancelOrder(id, userId));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update order status (SHIPPED / COMPLETED)")
    public ResponseEntity<OrderResponse> updateStatus(@PathVariable Long id,
                                                       @RequestBody @Valid UpdateOrderStatusRequest request) {
        Long userId = SecurityUtils.getCurrentUser().getId();
        return ResponseEntity.ok(orderService.updateStatus(id, request, userId));
    }
}
