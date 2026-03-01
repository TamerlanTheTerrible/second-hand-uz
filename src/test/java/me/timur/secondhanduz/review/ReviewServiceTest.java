package me.timur.secondhanduz.review;

import me.timur.secondhanduz.common.exception.ApiException;
import me.timur.secondhanduz.common.logging.AuditLogger;
import me.timur.secondhanduz.common.util.InputSanitizer;
import me.timur.secondhanduz.order.application.port.in.OrderService;
import me.timur.secondhanduz.order.domain.Order;
import me.timur.secondhanduz.review.application.port.out.ReviewRepository;
import me.timur.secondhanduz.review.application.service.ReviewServiceImpl;
import me.timur.secondhanduz.review.domain.Review;
import me.timur.secondhanduz.review.web.dto.CreateReviewRequest;
import me.timur.secondhanduz.review.web.dto.ReviewResponse;
import me.timur.secondhanduz.user.application.port.in.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock private ReviewRepository reviewRepository;
    @Mock private OrderService orderService;
    @Mock private UserService userService;
    @Mock private AuditLogger auditLogService;
    @Mock private InputSanitizer contentSanitizer;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private Order completedOrder;

    @BeforeEach
    void setUp() {
        completedOrder = new Order(1L, 10L, BigDecimal.valueOf(100));
        completedOrder.markPaid();
        completedOrder.markShipped();
        completedOrder.markCompleted();
    }

    @Test
    void should_createReview_when_orderIsCompleted() {
        var saved = new Review(1L, 2L, 1L, 5, "Great!");
        when(orderService.findByIdInternal(1L)).thenReturn(Optional.of(completedOrder));
        when(reviewRepository.existsByOrderIdAndReviewerId(1L, 1L)).thenReturn(false);
        when(contentSanitizer.sanitize(anyString())).thenAnswer(i -> i.getArgument(0));
        when(reviewRepository.save(any())).thenReturn(saved);

        ReviewResponse response = reviewService.createReview(
                new CreateReviewRequest(1L, 2L, 5, "Great!"), 1L);

        assertThat(response.rating()).isEqualTo(5);
        verify(userService).updateSellerRating(2L);
    }

    @Test
    void should_throwConflict_when_orderNotCompleted() {
        var incomplete = new Order(1L, 10L, BigDecimal.valueOf(100));
        when(orderService.findByIdInternal(1L)).thenReturn(Optional.of(incomplete));

        assertThatThrownBy(() -> reviewService.createReview(
                new CreateReviewRequest(1L, 2L, 4, "ok"), 1L))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("completed orders");
    }

    @Test
    void should_throwConflict_when_reviewAlreadyExists() {
        when(orderService.findByIdInternal(1L)).thenReturn(Optional.of(completedOrder));
        when(reviewRepository.existsByOrderIdAndReviewerId(1L, 1L)).thenReturn(true);

        assertThatThrownBy(() -> reviewService.createReview(
                new CreateReviewRequest(1L, 2L, 3, "ok"), 1L))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("already reviewed");
    }

    @Test
    void should_throwForbidden_when_reviewerIsNotOrderBuyer() {
        when(orderService.findByIdInternal(1L)).thenReturn(Optional.of(completedOrder));

        assertThatThrownBy(() -> reviewService.createReview(
                new CreateReviewRequest(1L, 2L, 5, "ok"), 99L))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("own completed orders");
    }
}
