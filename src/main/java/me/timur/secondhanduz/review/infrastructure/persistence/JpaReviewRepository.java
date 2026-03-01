package me.timur.secondhanduz.review.infrastructure.persistence;

import me.timur.secondhanduz.review.application.port.out.ReviewRepository;
import me.timur.secondhanduz.review.domain.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * JPA adapter fulfilling the {@link ReviewRepository} output port.
 */
@Repository
public interface JpaReviewRepository extends JpaRepository<Review, Long>, ReviewRepository {

    @Override
    Page<Review> findByReviewedUserId(Long reviewedUserId, Pageable pageable);

    @Override
    boolean existsByOrderIdAndReviewerId(Long orderId, Long reviewerId);

    @Override
    @Query("SELECT CAST(r.rating AS double) FROM Review r WHERE r.reviewedUserId = :userId")
    List<Double> findRatingsByReviewedUserId(@Param("userId") Long userId);
}
