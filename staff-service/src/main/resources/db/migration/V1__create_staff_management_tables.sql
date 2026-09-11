-- =============================================================================
-- Migration V1: Create Staff Members, Shifts, and Shift Requests Tables
-- =============================================================================

CREATE TABLE IF NOT EXISTS staff_members (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    role VARCHAR(100) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'OFF_DUTY',
    performance NUMERIC(3, 2) DEFAULT 5.00,
    avatar_initials VARCHAR(10),
    email VARCHAR(150),
    phone VARCHAR(30),
    station_area VARCHAR(100),
    hourly_rate NUMERIC(10, 2),
    weekly_budget_hours NUMERIC(5, 2) DEFAULT 40.00,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS shifts (
    id UUID PRIMARY KEY,
    staff_id UUID NOT NULL,
    shift_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    shift_role VARCHAR(100) NOT NULL,
    station_area VARCHAR(100) NOT NULL,
    shift_type VARCHAR(30) NOT NULL DEFAULT 'REGULAR',
    shift_category VARCHAR(30),
    status VARCHAR(30) NOT NULL DEFAULT 'DRAFT',
    notes VARCHAR(1000),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_shifts_staff FOREIGN KEY (staff_id) REFERENCES staff_members(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS shift_requests (
    id UUID PRIMARY KEY,
    staff_id UUID NOT NULL,
    request_type VARCHAR(30) NOT NULL,
    shift_id UUID,
    target_staff_id UUID,
    requested_date DATE,
    end_date DATE,
    reason VARCHAR(500),
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    admin_comment VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_requests_staff FOREIGN KEY (staff_id) REFERENCES staff_members(id) ON DELETE CASCADE,
    CONSTRAINT fk_requests_shift FOREIGN KEY (shift_id) REFERENCES shifts(id) ON DELETE SET NULL,
    CONSTRAINT fk_requests_target_staff FOREIGN KEY (target_staff_id) REFERENCES staff_members(id) ON DELETE SET NULL
);

-- Performance Indexes
CREATE INDEX IF NOT EXISTS idx_staff_status ON staff_members(status);
CREATE INDEX IF NOT EXISTS idx_staff_role ON staff_members(role);
CREATE INDEX IF NOT EXISTS idx_staff_active ON staff_members(active);

CREATE INDEX IF NOT EXISTS idx_shifts_staff_id ON shifts(staff_id);
CREATE INDEX IF NOT EXISTS idx_shifts_date ON shifts(shift_date);
CREATE INDEX IF NOT EXISTS idx_shifts_staff_date ON shifts(staff_id, shift_date);
CREATE INDEX IF NOT EXISTS idx_shifts_status ON shifts(status);
CREATE INDEX IF NOT EXISTS idx_shifts_category ON shifts(shift_category);

CREATE INDEX IF NOT EXISTS idx_requests_staff ON shift_requests(staff_id);
CREATE INDEX IF NOT EXISTS idx_requests_shift ON shift_requests(shift_id);
CREATE INDEX IF NOT EXISTS idx_requests_target_staff ON shift_requests(target_staff_id);
CREATE INDEX IF NOT EXISTS idx_requests_status ON shift_requests(status);
CREATE INDEX IF NOT EXISTS idx_requests_type ON shift_requests(request_type);
CREATE INDEX IF NOT EXISTS idx_requests_date ON shift_requests(requested_date);
