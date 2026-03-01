package me.timur.secondhanduz.review.application.port.in;

import me.timur.secondhanduz.review.web.dto.CreateReviewRequest;
import me.timur.secondhanduz.review.web.dto.ReviewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Input port: use cases for the Review module.
 */
public interface ReviewService {

    ReviewResponse createReview(CreateReviewRequest request, Long reviewerId);

    Page<ReviewResponse> getSellerReviews(Long sellerId, Pageable pageable);
}
