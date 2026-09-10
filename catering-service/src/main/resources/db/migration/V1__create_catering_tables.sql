CREATE TABLE catering_packages (
    id BIGSERIAL PRIMARY KEY,
    owner_id VARCHAR(100) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price_per_guest NUMERIC(12,2) NOT NULL,
    max_guests INTEGER,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE catering_events (
    id BIGSERIAL PRIMARY KEY,
    owner_id VARCHAR(100) NOT NULL,
    booking_reference VARCHAR(40) NOT NULL UNIQUE,
    event_name VARCHAR(255) NOT NULL,
    event_type VARCHAR(100) NOT NULL,
    event_date DATE NOT NULL,
    event_time TIME NOT NULL,
    guest_count INTEGER NOT NULL,
    venue_address TEXT,
    special_instructions TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    package_id BIGINT NOT NULL,
    subtotal NUMERIC(12,2) NOT NULL DEFAULT 0,
    custom_options_total NUMERIC(12,2) NOT NULL DEFAULT 0,
    service_fee NUMERIC(12,2) NOT NULL DEFAULT 0,
    tax_amount NUMERIC(12,2) NOT NULL DEFAULT 0,
    total_amount NUMERIC(12,2) NOT NULL DEFAULT 0,
    deposit_amount NUMERIC(12,2) NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_catering_event_package
        FOREIGN KEY (package_id) REFERENCES catering_packages(id)
);

CREATE TABLE catering_event_custom_options (
    id BIGSERIAL PRIMARY KEY,
    event_id BIGINT NOT NULL,
    option_name VARCHAR(255) NOT NULL,
    amount NUMERIC(12,2) NOT NULL DEFAULT 0,
    CONSTRAINT fk_custom_option_event
        FOREIGN KEY (event_id) REFERENCES catering_events(id)
        ON DELETE CASCADE
);

CREATE TABLE catering_inquiries (
    id BIGSERIAL PRIMARY KEY,
    owner_id VARCHAR(100) NOT NULL,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    phone VARCHAR(50),
    event_type VARCHAR(100),
    event_date DATE,
    guest_count INTEGER,
    message TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'NEW',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_catering_events_owner
    ON catering_events(owner_id);

CREATE INDEX idx_catering_events_owner_date
    ON catering_events(owner_id, event_date);

CREATE INDEX idx_catering_packages_owner
    ON catering_packages(owner_id);

CREATE INDEX idx_catering_inquiries_owner
    ON catering_inquiries(owner_id);

CREATE INDEX idx_catering_inquiries_owner_status
    ON catering_inquiries(owner_id, status);
