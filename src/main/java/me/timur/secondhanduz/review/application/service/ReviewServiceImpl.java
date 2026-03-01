package me.timur.secondhanduz.review.application.service;

import me.timur.secondhanduz.common.exception.ApiException;
import me.timur.secondhanduz.common.exception.ErrorCode;
import me.timur.secondhanduz.common.logging.AuditLogger;
import me.timur.secondhanduz.common.util.InputSanitizer;
import me.timur.secondhanduz.order.application.port.in.OrderService;
import me.timur.secondhanduz.review.application.port.in.ReviewService;
import me.timur.secondhanduz.review.application.port.out.ReviewRepository;
import me.timur.secondhanduz.review.domain.Review;
import me.timur.secondhanduz.review.web.dto.CreateReviewRequest;
import me.timur.secondhanduz.review.web.dto.ReviewResponse;
import me.timur.secondhanduz.user.application.port.in.UserService;
import me.timur.secondhanduz.user.application.service.ReviewAverageProvider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of {@link ReviewService} use cases.
 * Also implements {@link ReviewAverageProvider} to allow the User module to
 * recalculate seller ratings without a circular dependency.
 */
@Service
public class ReviewServiceImpl implements ReviewService, ReviewAverageProvider {

    private final ReviewRepository reviewRepository;
    private final OrderService orderService;
    private final UserService userService;
    private final AuditLogger auditLogService;
    private final InputSanitizer contentSanitizer;

    public ReviewServiceImpl(ReviewRepository reviewRepository,
                             OrderService orderService,
                             UserService userService,
                             AuditLogger auditLogService,
                             InputSanitizer contentSanitizer) {
        this.reviewRepository = reviewRepository;
        this.orderService = orderService;
        this.userService = userService;
        this.auditLogService = auditLogService;
        this.contentSanitizer = contentSanitizer;
    }

    @Override
    @Transactional
    public ReviewResponse createReview(CreateReviewRequest request, Long reviewerId) {
        var order = orderService.findByIdInternal(request.orderId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,
                        ErrorCode.ORDER_NOT_FOUND, "Order not found: " + request.orderId()));

        if (!order.isCompleted()) {
            throw new ApiException(HttpStatus.CONFLICT, ErrorCode.ORDER_NOT_COMPLETED,
                    "Reviews are only allowed for completed orders");
        }
        if (!order.getBuyerId().equals(reviewerId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, ErrorCode.REVIEW_NOT_ALLOWED,
                    "You can only review sellers from your own completed orders");
        }
        if (reviewRepository.existsByOrderIdAndReviewerId(request.orderId(), reviewerId)) {
            throw new ApiException(HttpStatus.CONFLICT, ErrorCode.REVIEW_ALREADY_EXISTS,
                    "You have already reviewed this order");
        }

        var review = new Review(
                reviewerId,
                request.sellerId(),
                request.orderId(),
                request.rating(),
                contentSanitizer.sanitize(request.comment())
        );
        var saved = reviewRepository.save(review);
        userService.updateSellerRating(request.sellerId());
        auditLogService.log("REVIEW_CREATED", reviewerId, saved.getId(),
                "sellerId=" + request.sellerId() + " rating=" + request.rating());
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewResponse> getSellerReviews(Long sellerId, Pageable pageable) {
        return reviewRepository.findByReviewedUserId(sellerId, pageable).map(this::toResponse);
    }

    @Override
    public List<Double> getRatingsForUser(Long userId) {
        return reviewRepository.findRatingsByReviewedUserId(userId);
    }

    private ReviewResponse toResponse(Review review) {
        return new ReviewResponse(review.getId(), review.getReviewerId(), review.getReviewedUserId(),
                review.getOrderId(), review.getRating(), review.getComment(), review.getCreatedAt());
    }
}
