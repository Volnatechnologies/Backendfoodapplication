-- ====================================================================
-- V3: Create Reviews, Photos, and Review Responses Tables
-- ====================================================================

CREATE TABLE IF NOT EXISTS reviews (
    id UUID PRIMARY KEY,
    business_id UUID NOT NULL,
    customer_name VARCHAR(150) NOT NULL,
    customer_avatar VARCHAR(500),
    rating INTEGER NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment TEXT NOT NULL,
    sentiment VARCHAR(20) NOT NULL DEFAULT 'POSITIVE',
    sentiment_score NUMERIC(5,2) NOT NULL DEFAULT 90.00,
    answered BOOLEAN NOT NULL DEFAULT FALSE,
    has_photos BOOLEAN NOT NULL DEFAULT FALSE,
    order_reference VARCHAR(50),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_reviews_business FOREIGN KEY (business_id) REFERENCES businesses(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_reviews_business_id ON reviews(business_id);
CREATE INDEX IF NOT EXISTS idx_reviews_business_rating ON reviews(business_id, rating);
CREATE INDEX IF NOT EXISTS idx_reviews_business_sentiment ON reviews(business_id, sentiment);
CREATE INDEX IF NOT EXISTS idx_reviews_business_answered ON reviews(business_id, answered);
CREATE INDEX IF NOT EXISTS idx_reviews_business_has_photos ON reviews(business_id, has_photos);
CREATE INDEX IF NOT EXISTS idx_reviews_business_created_at ON reviews(business_id, created_at);

CREATE TABLE IF NOT EXISTS review_photos (
    id UUID PRIMARY KEY,
    review_id UUID NOT NULL,
    photo_url VARCHAR(1000) NOT NULL,
    CONSTRAINT fk_review_photos_review FOREIGN KEY (review_id) REFERENCES reviews(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_review_photos_review_id ON review_photos(review_id);

CREATE TABLE IF NOT EXISTS review_responses (
    id UUID PRIMARY KEY,
    review_id UUID NOT NULL UNIQUE,
    business_id UUID NOT NULL,
    responder_id UUID NOT NULL,
    responder_name VARCHAR(150) NOT NULL DEFAULT 'Restaurant Manager',
    response_text TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_review_responses_review FOREIGN KEY (review_id) REFERENCES reviews(id) ON DELETE CASCADE,
    CONSTRAINT fk_review_responses_business FOREIGN KEY (business_id) REFERENCES businesses(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_review_responses_business_id ON review_responses(business_id);
