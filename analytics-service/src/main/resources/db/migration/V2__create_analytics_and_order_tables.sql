-- ====================================================================
-- V2: Create Analytics and Order Tables
-- ====================================================================

CREATE TABLE IF NOT EXISTS orders (
    id UUID PRIMARY KEY,
    order_number VARCHAR(50) NOT NULL,
    business_id UUID NOT NULL,
    customer_id UUID,
    customer_name VARCHAR(150) NOT NULL,
    customer_phone VARCHAR(30),
    order_type VARCHAR(30) NOT NULL DEFAULT 'DINE_IN',
    delivery_address VARCHAR(500),
    total_amount NUMERIC(12,2) NOT NULL DEFAULT 0.00,
    status VARCHAR(30) NOT NULL DEFAULT 'COMPLETED',
    payment_status VARCHAR(30) NOT NULL DEFAULT 'PAID',
    satisfaction_rating NUMERIC(3,2),
    fulfillment_time_minutes INTEGER,
    rejection_reason VARCHAR(500),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_orders_business FOREIGN KEY (business_id) REFERENCES businesses(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_orders_business_id ON orders(business_id);
CREATE INDEX IF NOT EXISTS idx_orders_business_created_at ON orders(business_id, created_at);
CREATE INDEX IF NOT EXISTS idx_orders_business_status ON orders(business_id, status);
CREATE INDEX IF NOT EXISTS idx_orders_business_order_type ON orders(business_id, order_type);
CREATE INDEX IF NOT EXISTS idx_orders_order_number ON orders(order_number);

CREATE TABLE IF NOT EXISTS order_items (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL,
    menu_item_name VARCHAR(200) NOT NULL,
    category_name VARCHAR(100) NOT NULL DEFAULT 'Main Course',
    quantity INTEGER NOT NULL DEFAULT 1,
    price NUMERIC(12,2) NOT NULL DEFAULT 0.00,
    image_url VARCHAR(500),
    CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_order_items_order_id ON order_items(order_id);
CREATE INDEX IF NOT EXISTS idx_order_items_menu_item_name ON order_items(menu_item_name);
