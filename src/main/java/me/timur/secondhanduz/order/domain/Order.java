package me.timur.secondhanduz.order.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Order domain entity representing a purchase transaction.
 */
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "buyer_id", nullable = false)
    private Long buyerId;

    @Column(name = "listing_id", nullable = false)
    private Long listingId;

    @Column(name = "total_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected Order() {}

    public Order(Long buyerId, Long listingId, BigDecimal totalPrice) {
        this.buyerId = buyerId;
        this.listingId = listingId;
        this.totalPrice = totalPrice;
        this.status = OrderStatus.CREATED;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Long getBuyerId() { return buyerId; }
    public Long getListingId() { return listingId; }
    public BigDecimal getTotalPrice() { return totalPrice; }
    public OrderStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public boolean canBeCanceled() {
        return this.status == OrderStatus.CREATED;
    }

    public boolean isPaid() {
        return this.status == OrderStatus.PAID;
    }

    public boolean isCompleted() {
        return this.status == OrderStatus.COMPLETED;
    }

    public void markPaid()      { this.status = OrderStatus.PAID; }
    public void markShipped()   { this.status = OrderStatus.SHIPPED; }
    public void markCompleted() { this.status = OrderStatus.COMPLETED; }
    public void cancel()        { this.status = OrderStatus.CANCELED; }

    public void transitionTo(OrderStatus newStatus) {
        this.status = newStatus;
    }
}
