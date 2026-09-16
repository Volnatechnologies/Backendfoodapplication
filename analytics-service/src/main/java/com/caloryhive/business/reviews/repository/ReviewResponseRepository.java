package com.caloryhive.business.reviews.repository;

import com.caloryhive.business.reviews.entity.ReviewResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReviewResponseRepository extends JpaRepository<ReviewResponse, UUID> {
    Optional<ReviewResponse> findByReviewId(UUID reviewId);
    Optional<ReviewResponse> findByReviewIdAndBusinessId(UUID reviewId, UUID businessId);
}
