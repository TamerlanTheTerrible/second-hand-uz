package me.timur.secondhanduz.review.application.port.out;

import me.timur.secondhanduz.review.domain.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Output port: persistence contract for {@link Review} entities.
 */
public interface ReviewRepository {

    Review save(Review review);

    Page<Review> findByReviewedUserId(Long reviewedUserId, Pageable pageable);

    List<Double> findRatingsByReviewedUserId(Long reviewedUserId);

    boolean existsByOrderIdAndReviewerId(Long orderId, Long reviewerId);
}
