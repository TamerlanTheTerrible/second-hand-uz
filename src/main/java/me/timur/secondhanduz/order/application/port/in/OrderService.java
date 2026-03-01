package me.timur.secondhanduz.order.application.port.in;

import me.timur.secondhanduz.order.domain.Order;
import me.timur.secondhanduz.order.web.dto.CreateOrderRequest;
import me.timur.secondhanduz.order.web.dto.OrderResponse;
import me.timur.secondhanduz.order.web.dto.UpdateOrderStatusRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Input port: use cases for the Order module.
 */
public interface OrderService {

    OrderResponse createOrder(CreateOrderRequest request, Long buyerId);

    OrderResponse cancelOrder(Long orderId, Long userId);

    OrderResponse getOrder(Long orderId, Long userId);

    Page<OrderResponse> getMyOrders(Long buyerId, Pageable pageable);

    OrderResponse updateStatus(Long orderId, UpdateOrderStatusRequest request, Long userId);

    /** Internal use: retrieve an order entity (used by payment and review modules). */
    Optional<Order> findByIdInternal(Long orderId);
}
