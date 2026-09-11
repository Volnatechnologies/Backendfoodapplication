CREATE TABLE IF NOT EXISTS payments (
    id UUID PRIMARY KEY,
    booking_id UUID NOT NULL,
    amount NUMERIC(19,4) NOT NULL CHECK (amount >= 0),
    payment_type VARCHAR(30) NOT NULL,
    payment_status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    transaction_reference VARCHAR(100),
    paid_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_payment_booking FOREIGN KEY (booking_id) REFERENCES catering_bookings(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_payment_booking_id ON payments(booking_id);
CREATE INDEX IF NOT EXISTS idx_payment_status ON payments(payment_status);
CREATE INDEX IF NOT EXISTS idx_payment_type ON payments(payment_type);
