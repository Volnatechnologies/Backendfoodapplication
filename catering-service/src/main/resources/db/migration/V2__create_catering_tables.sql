CREATE TABLE IF NOT EXISTS catering_event_types (
    id UUID PRIMARY KEY,
    name VARCHAR(150) NOT NULL UNIQUE,
    description TEXT,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS catering_menu_packages (
    id UUID PRIMARY KEY,
    name VARCHAR(150) NOT NULL UNIQUE,
    description TEXT,
    price_per_guest NUMERIC(19,4) NOT NULL CHECK (price_per_guest >= 0),
    image_url VARCHAR(500),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS catering_custom_options (
    id UUID PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    description TEXT,
    price NUMERIC(19,4) NOT NULL CHECK (price >= 0),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS catering_inquiries (
    id UUID PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL,
    phone VARCHAR(50),
    event_type VARCHAR(150),
    message TEXT,
    guest_count INTEGER,
    event_date DATE,
    status VARCHAR(30) NOT NULL DEFAULT 'NEW',
    read_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_menu_package_active ON catering_menu_packages(active);
CREATE INDEX IF NOT EXISTS idx_menu_package_name ON catering_menu_packages(name);
CREATE INDEX IF NOT EXISTS idx_custom_option_active ON catering_custom_options(active);
CREATE INDEX IF NOT EXISTS idx_custom_option_name ON catering_custom_options(name);
CREATE INDEX IF NOT EXISTS idx_inquiry_status ON catering_inquiries(status);
CREATE INDEX IF NOT EXISTS idx_inquiry_created_at ON catering_inquiries(created_at);
