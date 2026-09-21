CREATE TABLE IF NOT EXISTS user_profiles (
    id UUID PRIMARY KEY,
    auth_user_id UUID NOT NULL UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE,
    phone_number VARCHAR(15) NOT NULL UNIQUE,
    avatar_url VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS user_addresses (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES user_profiles(id) ON DELETE CASCADE,
    address_type VARCHAR(20) NOT NULL, -- HOME, WORK, OTHER
    house_number VARCHAR(100) NOT NULL,
    street_address TEXT NOT NULL,
    landmark VARCHAR(100),
    city VARCHAR(50) NOT NULL,
    state VARCHAR(50) NOT NULL,
    pincode VARCHAR(10) NOT NULL,
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    is_default BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_user_addresses_user_id ON user_addresses(user_id);
-- Enforce partial unique index so a user only has one default address
CREATE UNIQUE INDEX idx_user_addresses_single_default
ON user_addresses (user_id)
WHERE is_default = TRUE;