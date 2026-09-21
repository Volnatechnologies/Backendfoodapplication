CREATE TABLE orders (
 id UUID PRIMARY KEY,
 customer_id UUID NOT NULL,
 restaurant_id UUID NOT NULL,
 address_id UUID NOT NULL,
 status VARCHAR(40) NOT NULL,
 subtotal NUMERIC(12,2) NOT NULL,
 tax NUMERIC(12,2) NOT NULL,
 delivery_fee NUMERIC(12,2) NOT NULL,
 packaging_fee NUMERIC(12,2) NOT NULL,
 discount NUMERIC(12,2) NOT NULL,
 total_amount NUMERIC(12,2) NOT NULL,
 payment_status VARCHAR(20) NOT NULL,
 idempotency_key VARCHAR(100) UNIQUE,
 created_at TIMESTAMPTZ NOT NULL,
 updated_at TIMESTAMPTZ NOT NULL
);
CREATE INDEX idx_orders_customer ON orders(customer_id);
CREATE INDEX idx_orders_restaurant ON orders(restaurant_id);

CREATE TABLE order_items (
 id UUID PRIMARY KEY,
 order_id UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
 menu_item_id UUID NOT NULL,
 item_name_snapshot VARCHAR(200) NOT NULL,
 unit_price NUMERIC(12,2) NOT NULL,
 quantity INTEGER NOT NULL CHECK (quantity > 0),
 total NUMERIC(12,2) NOT NULL
);
CREATE INDEX idx_order_items_order ON order_items(order_id);

CREATE TABLE order_status_history (
 id UUID PRIMARY KEY,
 order_id UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
 old_status VARCHAR(40),
 new_status VARCHAR(40) NOT NULL,
 reason VARCHAR(500),
 changed_by VARCHAR(100),
 created_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE payments (
 id UUID PRIMARY KEY,
 order_id UUID NOT NULL UNIQUE REFERENCES orders(id) ON DELETE CASCADE,
 amount NUMERIC(12,2) NOT NULL,
 status VARCHAR(20) NOT NULL,
 provider_reference VARCHAR(200),
 idempotency_key VARCHAR(100),
 created_at TIMESTAMPTZ NOT NULL,
 updated_at TIMESTAMPTZ NOT NULL
);
