package com.caloryhive.business.reviews.service;

import com.caloryhive.business.reviews.entity.ReviewSentiment;

import java.math.BigDecimal;

public interface SentimentService {

    SentimentResult analyzeSentiment(int rating, String text);

    record SentimentResult(ReviewSentiment sentiment, BigDecimal score) {}
}
