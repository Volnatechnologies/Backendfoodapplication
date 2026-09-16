-- ====================================================================
-- V5: Create Partnership Rewards, Loyalty, Catalog, and Ledger Tables
-- ====================================================================

CREATE TABLE IF NOT EXISTS reward_tiers (
    id UUID PRIMARY KEY,
    tier_name VARCHAR(50) NOT NULL UNIQUE,
    display_name VARCHAR(100) NOT NULL,
    min_points INTEGER NOT NULL,
    next_tier_name VARCHAR(50),
    next_tier_points INTEGER,
    commission_discount_percent NUMERIC(5,2) DEFAULT 0.00,
    tagline VARCHAR(200)
);

CREATE TABLE IF NOT EXISTS business_reward_accounts (
    id UUID PRIMARY KEY,
    business_id UUID NOT NULL UNIQUE,
    current_tier_id UUID NOT NULL,
    total_points_earned INTEGER NOT NULL DEFAULT 0,
    current_points_balance INTEGER NOT NULL DEFAULT 0,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_reward_accounts_business FOREIGN KEY (business_id) REFERENCES businesses(id) ON DELETE CASCADE,
    CONSTRAINT fk_reward_accounts_tier FOREIGN KEY (current_tier_id) REFERENCES reward_tiers(id)
);

CREATE TABLE IF NOT EXISTS reward_perks (
    id UUID PRIMARY KEY,
    tier_id UUID NOT NULL,
    title VARCHAR(100) NOT NULL,
    description VARCHAR(500) NOT NULL,
    icon_name VARCHAR(50) NOT NULL DEFAULT 'CheckCircle',
    is_upcoming BOOLEAN NOT NULL DEFAULT FALSE,
    sort_order INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT fk_reward_perks_tier FOREIGN KEY (tier_id) REFERENCES reward_tiers(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS reward_objectives (
    id UUID PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    description VARCHAR(500) NOT NULL,
    icon_name VARCHAR(50) NOT NULL DEFAULT 'Star',
    points_reward INTEGER NOT NULL,
    target_type VARCHAR(50) NOT NULL,
    target_value NUMERIC(10,2) NOT NULL,
    unit VARCHAR(50) NOT NULL,
    sort_order INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS business_objective_progress (
    id UUID PRIMARY KEY,
    business_id UUID NOT NULL,
    objective_id UUID NOT NULL,
    current_value NUMERIC(10,2) NOT NULL DEFAULT 0.00,
    target_value NUMERIC(10,2) NOT NULL,
    progress_percentage INTEGER NOT NULL DEFAULT 0,
    progress_label VARCHAR(100),
    is_completed BOOLEAN NOT NULL DEFAULT FALSE,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_obj_progress_business FOREIGN KEY (business_id) REFERENCES businesses(id) ON DELETE CASCADE,
    CONSTRAINT fk_obj_progress_objective FOREIGN KEY (objective_id) REFERENCES reward_objectives(id) ON DELETE CASCADE,
    CONSTRAINT uq_business_objective UNIQUE (business_id, objective_id)
);

CREATE TABLE IF NOT EXISTS reward_catalog_items (
    id UUID PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    description VARCHAR(500) NOT NULL,
    points_cost INTEGER NOT NULL CHECK (points_cost > 0),
    category VARCHAR(50) NOT NULL DEFAULT 'MARKETING',
    image_url VARCHAR(500),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS reward_redemptions (
    id UUID PRIMARY KEY,
    business_id UUID NOT NULL,
    catalog_item_id UUID NOT NULL,
    points_spent INTEGER NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'COMPLETED',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_redemptions_business FOREIGN KEY (business_id) REFERENCES businesses(id) ON DELETE CASCADE,
    CONSTRAINT fk_redemptions_catalog FOREIGN KEY (catalog_item_id) REFERENCES reward_catalog_items(id)
);

CREATE INDEX IF NOT EXISTS idx_redemptions_business_id ON reward_redemptions(business_id);

CREATE TABLE IF NOT EXISTS reward_transactions (
    id UUID PRIMARY KEY,
    business_id UUID NOT NULL,
    type VARCHAR(30) NOT NULL,
    points INTEGER NOT NULL,
    reference_type VARCHAR(50),
    reference_id VARCHAR(100),
    balance_after INTEGER NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_reward_tx_business FOREIGN KEY (business_id) REFERENCES businesses(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_reward_tx_business_id ON reward_transactions(business_id);
CREATE INDEX IF NOT EXISTS idx_reward_tx_created_at ON reward_transactions(business_id, created_at);
