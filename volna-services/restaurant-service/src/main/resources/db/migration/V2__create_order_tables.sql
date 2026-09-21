CREATE TABLE orders (
    id UUID PRIMARY KEY,

    restaurant_id UUID NOT NULL,

    customer_id UUID,

    customer_name VARCHAR(150) NOT NULL,

    customer_phone VARCHAR(30),

    order_type VARCHAR(30) NOT NULL,

    delivery_address VARCHAR(500),

    total_amount NUMERIC(12,2) NOT NULL,

    status VARCHAR(30) NOT NULL,

    payment_status VARCHAR(30) NOT NULL,

    rejection_reason VARCHAR(500),

    created_at TIMESTAMP WITH TIME ZONE NOT NULL,

    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT fk_orders_restaurant
        FOREIGN KEY (restaurant_id)
        REFERENCES restaurants(id)
        ON DELETE CASCADE
);


CREATE INDEX idx_orders_restaurant_id
    ON orders(restaurant_id);


CREATE INDEX idx_orders_restaurant_status
    ON orders(restaurant_id, status);


CREATE INDEX idx_orders_created_at
    ON orders(created_at);


CREATE TABLE order_items (
    id UUID PRIMARY KEY,

    order_id UUID NOT NULL,

    name VARCHAR(200) NOT NULL,

    quantity INTEGER NOT NULL,

    price NUMERIC(12,2) NOT NULL,

    CONSTRAINT fk_order_items_order
        FOREIGN KEY (order_id)
        REFERENCES orders(id)
        ON DELETE CASCADE
);


CREATE INDEX idx_order_items_order_id
    ON order_items(order_id);