CREATE TABLE IF NOT EXISTS catering_bookings (
    id UUID PRIMARY KEY,
    booking_code VARCHAR(20) UNIQUE,
    event_name VARCHAR(200) NOT NULL,
    event_type_id UUID,
    event_date DATE,
    event_time TIME,
    guest_count INTEGER CHECK (guest_count > 0),
    venue_address TEXT,
    special_instructions TEXT,
    menu_package_id UUID,
    base_price NUMERIC(19,4) NOT NULL DEFAULT 0,
    custom_options_total NUMERIC(19,4) NOT NULL DEFAULT 0,
    subtotal NUMERIC(19,4) NOT NULL DEFAULT 0,
    service_fee NUMERIC(19,4) NOT NULL DEFAULT 0,
    tax NUMERIC(19,4) NOT NULL DEFAULT 0,
    final_total NUMERIC(19,4) NOT NULL DEFAULT 0,
    deposit_percentage NUMERIC(5,4) NOT NULL DEFAULT 0.30,
    deposit_required NUMERIC(19,4) NOT NULL DEFAULT 0,
    status VARCHAR(40) NOT NULL DEFAULT 'DRAFT',
    created_by_id UUID,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_booking_event_type FOREIGN KEY (event_type_id) REFERENCES catering_event_types(id),
    CONSTRAINT fk_booking_menu_package FOREIGN KEY (menu_package_id) REFERENCES catering_menu_packages(id),
    CONSTRAINT fk_booking_created_by FOREIGN KEY (created_by_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS booking_menu_packages (
    id UUID PRIMARY KEY,
    booking_id UUID NOT NULL,
    menu_package_id UUID NOT NULL,
    CONSTRAINT fk_booking_menu_booking FOREIGN KEY (booking_id) REFERENCES catering_bookings(id) ON DELETE CASCADE,
    CONSTRAINT fk_booking_menu_package FOREIGN KEY (menu_package_id) REFERENCES catering_menu_packages(id)
);

CREATE TABLE IF NOT EXISTS booking_custom_options (
    id UUID PRIMARY KEY,
    booking_id UUID NOT NULL,
    custom_option_id UUID NOT NULL,
    CONSTRAINT fk_booking_custom_booking FOREIGN KEY (booking_id) REFERENCES catering_bookings(id) ON DELETE CASCADE,
    CONSTRAINT fk_booking_custom_option FOREIGN KEY (custom_option_id) REFERENCES catering_custom_options(id)
);

CREATE TABLE IF NOT EXISTS booking_status_history (
    id UUID PRIMARY KEY,
    booking_id UUID NOT NULL,
    old_status VARCHAR(40),
    new_status VARCHAR(40) NOT NULL,
    changed_by_id UUID,
    changed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    comment TEXT,
    CONSTRAINT fk_status_history_booking FOREIGN KEY (booking_id) REFERENCES catering_bookings(id) ON DELETE CASCADE,
    CONSTRAINT fk_status_history_user FOREIGN KEY (changed_by_id) REFERENCES users(id)
);

CREATE INDEX IF NOT EXISTS idx_booking_code ON catering_bookings(booking_code);
CREATE INDEX IF NOT EXISTS idx_booking_event_date ON catering_bookings(event_date);
CREATE INDEX IF NOT EXISTS idx_booking_status ON catering_bookings(status);
CREATE INDEX IF NOT EXISTS idx_booking_created_at ON catering_bookings(created_at);
CREATE INDEX IF NOT EXISTS idx_booking_event_name ON catering_bookings(event_name);
