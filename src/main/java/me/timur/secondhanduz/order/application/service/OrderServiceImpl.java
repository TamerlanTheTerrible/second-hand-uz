package me.timur.secondhanduz.order.application.service;

import me.timur.secondhanduz.common.exception.ApiException;
import me.timur.secondhanduz.common.exception.ErrorCode;
import me.timur.secondhanduz.common.logging.AuditLogger;
import me.timur.secondhanduz.listing.application.port.out.ListingRepository;
import me.timur.secondhanduz.order.application.port.in.OrderService;
import me.timur.secondhanduz.order.application.port.out.OrderRepository;
import me.timur.secondhanduz.order.domain.Order;
import me.timur.secondhanduz.order.web.dto.CreateOrderRequest;
import me.timur.secondhanduz.order.web.dto.OrderResponse;
import me.timur.secondhanduz.order.web.dto.UpdateOrderStatusRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Implementation of {@link OrderService} use cases.
 */
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ListingRepository listingRepository;
    private final AuditLogger auditLogService;

    public OrderServiceImpl(OrderRepository orderRepository,
                            ListingRepository listingRepository,
                            AuditLogger auditLogService) {
        this.orderRepository = orderRepository;
        this.listingRepository = listingRepository;
        this.auditLogService = auditLogService;
    }

    @Override
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request, Long buyerId) {
        var listing = listingRepository.findById(request.listingId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,
                        ErrorCode.LISTING_NOT_FOUND, "Listing not found: " + request.listingId()));

        if (!listing.isActive()) {
            throw new ApiException(HttpStatus.CONFLICT, ErrorCode.LISTING_NOT_AVAILABLE,
                    "Listing is not available for purchase");
        }
        // Only count non-canceled orders — canceled orders free the listing for re-ordering
        if (orderRepository.existsByListingIdAndStatusNot(request.listingId(), me.timur.secondhanduz.order.domain.OrderStatus.CANCELED)) {
            throw new ApiException(HttpStatus.CONFLICT, ErrorCode.LISTING_ALREADY_ORDERED,
                    "This listing has already been ordered");
        }

        try {
            var order = new Order(buyerId, request.listingId(), listing.getPrice());
            var saved = orderRepository.save(order);
            auditLogService.log("ORDER_CREATED", buyerId, saved.getId(),
                    "listingId=" + request.listingId());
            return toResponse(saved);
        } catch (DataIntegrityViolationException ex) {
            // Concurrent request raced past the soft check — partial unique index caught it
            throw new ApiException(HttpStatus.CONFLICT, ErrorCode.LISTING_ALREADY_ORDERED,
                    "This listing has already been ordered");
        }
    }

    @Override
    @Transactional
    public OrderResponse cancelOrder(Long orderId, Long userId) {
        var order = requireAccess(orderId, userId);
        if (!order.canBeCanceled()) {
            throw new ApiException(HttpStatus.CONFLICT, ErrorCode.ORDER_CANNOT_CANCEL,
                    "Order cannot be canceled in its current state");
        }
        order.cancel();
        var saved = orderRepository.save(order);
        auditLogService.log("ORDER_CANCELED", userId, orderId, "");
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrder(Long orderId, Long userId) {
        return toResponse(requireAccess(orderId, userId));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getMyOrders(Long buyerId, Pageable pageable) {
        return orderRepository.findByBuyerId(buyerId, pageable).map(this::toResponse);
    }

    @Override
    @Transactional
    public OrderResponse updateStatus(Long orderId, UpdateOrderStatusRequest request, Long userId) {
        var order = requireAccess(orderId, userId);
        order.transitionTo(request.status());
        if (request.status() == me.timur.secondhanduz.order.domain.OrderStatus.PAID) {
            listingRepository.findById(order.getListingId()).ifPresent(listing -> {
                listing.markSold();
                listingRepository.save(listing);
            });
        }
        var saved = orderRepository.save(order);
        auditLogService.log("ORDER_STATUS_UPDATED", userId, orderId,
                "status=" + request.status());
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Order> findByIdInternal(Long orderId) {
        return orderRepository.findById(orderId);
    }

    private Order requireAccess(Long orderId, Long userId) {
        var order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,
                        ErrorCode.ORDER_NOT_FOUND, "Order not found: " + orderId));
        if (!order.getBuyerId().equals(userId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, ErrorCode.ORDER_ACCESS_DENIED,
                    "Access denied to order: " + orderId);
        }
        return order;
    }

    private OrderResponse toResponse(Order order) {
        return new OrderResponse(order.getId(), order.getBuyerId(), order.getListingId(),
                order.getTotalPrice(), order.getStatus(), order.getCreatedAt());
    }
}
