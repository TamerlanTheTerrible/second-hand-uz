package me.timur.secondhanduz.review.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDateTime;

/**
 * Review domain entity for seller feedback after completed orders.
 */
@Entity
@Table(name = "reviews",
       uniqueConstraints = @UniqueConstraint(
               name = "uq_reviews_order_reviewer",
               columnNames = {"order_id", "reviewer_id"}))
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reviewer_id", nullable = false)
    private Long reviewerId;

    @Column(name = "reviewed_user_id", nullable = false)
    private Long reviewedUserId;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(nullable = false)
    private Integer rating;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected Review() {}

    public Review(Long reviewerId, Long reviewedUserId, Long orderId, Integer rating, String comment) {
        this.reviewerId = reviewerId;
        this.reviewedUserId = reviewedUserId;
        this.orderId = orderId;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Long getReviewerId() { return reviewerId; }
    public Long getReviewedUserId() { return reviewedUserId; }
    public Long getOrderId() { return orderId; }
    public Integer getRating() { return rating; }
    public String getComment() { return comment; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
