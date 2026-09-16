package com.caloryhive.business.reviews.repository;

import com.caloryhive.business.reviews.entity.Review;
import com.caloryhive.business.reviews.entity.ReviewSentiment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {

    Optional<Review> findByIdAndBusinessId(UUID id, UUID businessId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.businessId = :businessId")
    long countByBusinessId(@Param("businessId") UUID businessId);

    @Query("SELECT COALESCE(AVG(r.rating), 4.8) FROM Review r WHERE r.businessId = :businessId")
    Double avgRatingByBusinessId(@Param("businessId") UUID businessId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.businessId = :businessId AND r.rating = :rating")
    long countByBusinessIdAndRating(@Param("businessId") UUID businessId, @Param("rating") Integer rating);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.businessId = :businessId AND r.answered = true")
    long countAnsweredByBusinessId(@Param("businessId") UUID businessId);

    @Query("SELECT COALESCE(AVG(r.sentimentScore), 92.0) FROM Review r WHERE r.businessId = :businessId")
    Double avgSentimentScoreByBusinessId(@Param("businessId") UUID businessId);

    @Query("SELECT r FROM Review r WHERE r.businessId = :businessId " +
           "AND (:hasPhotos IS NULL OR r.hasPhotos = :hasPhotos) " +
           "AND (:answered IS NULL OR r.answered = :answered) " +
           "AND (:maxRating IS NULL OR r.rating <= :maxRating) " +
           "AND (:exactRating IS NULL OR r.rating = :exactRating) " +
           "AND (:sentiment IS NULL OR r.sentiment = :sentiment) " +
           "AND (:search IS NULL OR LOWER(r.comment) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(r.customerName) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Review> findFilteredReviews(
            @Param("businessId") UUID businessId,
            @Param("hasPhotos") Boolean hasPhotos,
            @Param("answered") Boolean answered,
            @Param("maxRating") Integer maxRating,
            @Param("exactRating") Integer exactRating,
            @Param("sentiment") ReviewSentiment sentiment,
            @Param("search") String search,
            Pageable pageable
    );
}
