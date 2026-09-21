CREATE TABLE meal_plans (
    id UUID PRIMARY KEY,
    owner_id UUID NOT NULL,
    name VARCHAR(150) NOT NULL,
    description VARCHAR(1000),
    pricing_model VARCHAR(30) NOT NULL,
    base_price NUMERIC(12,2) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'USD',
    meals_per_day INTEGER NOT NULL DEFAULT 1,
    first_delivery_date DATE,
    lifecycle VARCHAR(30) NOT NULL DEFAULT 'ONGOING',
    status VARCHAR(30) NOT NULL DEFAULT 'DRAFT',
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT chk_meal_plan_price CHECK (base_price >= 0),
    CONSTRAINT chk_meal_plan_meals_per_day CHECK (meals_per_day BETWEEN 1 AND 10)
);
CREATE INDEX idx_meal_plans_owner ON meal_plans(owner_id);
CREATE INDEX idx_meal_plans_owner_status ON meal_plans(owner_id, status);

CREATE TABLE meal_plan_delivery_days (
    meal_plan_id UUID NOT NULL,
    day_of_week VARCHAR(12) NOT NULL,
    PRIMARY KEY (meal_plan_id, day_of_week),
    CONSTRAINT fk_plan_days_plan FOREIGN KEY (meal_plan_id)
        REFERENCES meal_plans(id) ON DELETE CASCADE
);

CREATE TABLE meal_plan_items (
    id UUID PRIMARY KEY,
    meal_plan_id UUID NOT NULL,
    name VARCHAR(150) NOT NULL,
    description VARCHAR(500),
    image_url VARCHAR(1000),
    dietary_type VARCHAR(40) NOT NULL DEFAULT 'GENERAL',
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_plan_items_plan FOREIGN KEY (meal_plan_id)
        REFERENCES meal_plans(id) ON DELETE CASCADE
);
CREATE INDEX idx_meal_plan_items_plan ON meal_plan_items(meal_plan_id);

CREATE TABLE meal_subscriptions (
    id UUID PRIMARY KEY,
    meal_plan_id UUID NOT NULL,
    customer_id UUID NOT NULL,
    customer_name VARCHAR(150) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    started_at DATE NOT NULL,
    cancelled_at DATE,
    cancellation_reason VARCHAR(500),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_subscriptions_plan FOREIGN KEY (meal_plan_id)
        REFERENCES meal_plans(id) ON DELETE CASCADE
);
CREATE INDEX idx_subscriptions_plan_status ON meal_subscriptions(meal_plan_id, status);

CREATE TABLE meal_delivery_records (
    id UUID PRIMARY KEY,
    meal_plan_id UUID NOT NULL,
    subscription_id UUID,
    scheduled_date DATE NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'SCHEDULED',
    delivered_at TIMESTAMP WITH TIME ZONE,
    failure_reason VARCHAR(500),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_delivery_plan FOREIGN KEY (meal_plan_id)
        REFERENCES meal_plans(id) ON DELETE CASCADE,
    CONSTRAINT fk_delivery_subscription FOREIGN KEY (subscription_id)
        REFERENCES meal_subscriptions(id) ON DELETE SET NULL
);
CREATE INDEX idx_delivery_plan_date ON meal_delivery_records(meal_plan_id, scheduled_date);
CREATE INDEX idx_delivery_status_date ON meal_delivery_records(status, scheduled_date);
