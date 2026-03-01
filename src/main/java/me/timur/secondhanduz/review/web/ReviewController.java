package me.timur.secondhanduz.review.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import me.timur.secondhanduz.common.security.SecurityUtils;
import me.timur.secondhanduz.review.application.port.in.ReviewService;
import me.timur.secondhanduz.review.web.dto.CreateReviewRequest;
import me.timur.secondhanduz.review.web.dto.ReviewResponse;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for seller review operations.
 */
@RestController
@RequestMapping("/api/v1/reviews")
@Tag(name = "Reviews", description = "Seller reviews and ratings")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    @Operation(summary = "Submit a review for a completed order",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ReviewResponse> createReview(@RequestBody @Valid CreateReviewRequest request) {
        Long reviewerId = SecurityUtils.getCurrentUser().getId();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reviewService.createReview(request, reviewerId));
    }

    @GetMapping("/seller/{sellerId}")
    @Operation(summary = "Get all reviews for a seller (public)")
    public ResponseEntity<Page<ReviewResponse>> getSellerReviews(
            @PathVariable Long sellerId,
            @PageableDefault(size = 20) @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(reviewService.getSellerReviews(sellerId, pageable));
    }
}
