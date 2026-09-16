package com.caloryhive.business.reviews.service.impl;

import com.caloryhive.business.reviews.entity.ReviewSentiment;
import com.caloryhive.business.reviews.service.SentimentService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;

@Service
public class RuleBasedSentimentServiceImpl implements SentimentService {

    private static final Set<String> POSITIVE_WORDS = Set.of(
            "excellent", "amazing", "great", "best", "delicious", "fresh",
            "perfect", "loved", "love", "wonderful", "exceptional", "good",
            "fast", "friendly", "stellar", "clean", "tasty", "superb"
    );

    private static final Set<String> NEGATIVE_WORDS = Set.of(
            "terrible", "horrible", "bad", "cold", "late", "worst", "dirty",
            "slow", "rude", "poor", "lukewarm", "disappointed", "soggy",
            "awful", "undercooked", "overcooked", "bland", "stale"
    );

    @Override
    public SentimentResult analyzeSentiment(int rating, String text) {
        double baseScore = switch (rating) {
            case 5 -> 95.0;
            case 4 -> 80.0;
            case 3 -> 50.0;
            case 2 -> 30.0;
            case 1 -> 15.0;
            default -> 70.0;
        };

        if (text != null && !text.isBlank()) {
            String lower = text.toLowerCase();
            long posCount = POSITIVE_WORDS.stream().filter(lower::contains).count();
            long negCount = NEGATIVE_WORDS.stream().filter(lower::contains).count();

            baseScore += (posCount * 3.0);
            baseScore -= (negCount * 5.0);
        }

        baseScore = Math.max(5.0, Math.min(99.0, baseScore));
        BigDecimal finalScore = BigDecimal.valueOf(baseScore).setScale(2, RoundingMode.HALF_UP);

        ReviewSentiment sentiment;
        if (finalScore.compareTo(BigDecimal.valueOf(70.0)) >= 0) {
            sentiment = ReviewSentiment.POSITIVE;
        } else if (finalScore.compareTo(BigDecimal.valueOf(40.0)) >= 0) {
            sentiment = ReviewSentiment.NEUTRAL;
        } else {
            sentiment = ReviewSentiment.NEGATIVE;
        }

        return new SentimentResult(sentiment, finalScore);
    }
}
