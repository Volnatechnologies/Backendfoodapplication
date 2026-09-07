-- =========================================================
-- Migration V3: Create Menu Management Tables and Seed Data
-- =========================================================

-- 1. Menu Categories
CREATE TABLE menu_categories (
    id BIGSERIAL PRIMARY KEY,
    restaurant_id UUID REFERENCES restaurants(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    sort_order INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_category_name ON menu_categories(name);
CREATE INDEX idx_category_is_active ON menu_categories(is_active);
CREATE INDEX idx_category_restaurant_id ON menu_categories(restaurant_id);

-- 2. Menu Items
CREATE TABLE menu_items (
    id BIGSERIAL PRIMARY KEY,
    restaurant_id UUID REFERENCES restaurants(id) ON DELETE CASCADE,
    name VARCHAR(150) NOT NULL,
    description TEXT,
    internal_code VARCHAR(50),
    price DECIMAL(10, 2) NOT NULL CHECK (price >= 0),
    discounted_price DECIMAL(10, 2),
    protein DECIMAL(6, 2),
    carbohydrates DECIMAL(6, 2),
    fats DECIMAL(6, 2),
    category_id BIGINT NOT NULL REFERENCES menu_categories(id) ON DELETE CASCADE,
    image_url VARCHAR(255),
    calories INT CHECK (calories >= 0),
    spicy_level VARCHAR(20) NOT NULL DEFAULT 'NONE',
    stock_quantity INT NOT NULL DEFAULT 0 CHECK (stock_quantity >= 0),
    status VARCHAR(20) NOT NULL DEFAULT 'IN_STOCK',
    is_visible BOOLEAN NOT NULL DEFAULT TRUE,
    is_available BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_menu_item_name ON menu_items(name);
CREATE INDEX idx_menu_item_category_id ON menu_items(category_id);
CREATE INDEX idx_menu_item_status ON menu_items(status);
CREATE INDEX idx_menu_item_is_visible ON menu_items(is_visible);
CREATE INDEX idx_menu_item_internal_code ON menu_items(internal_code);
CREATE INDEX idx_menu_item_restaurant_id ON menu_items(restaurant_id);

-- 3. Element Collections for Dietary Tags and Allergens
CREATE TABLE menu_item_dietary_tags (
    menu_item_id BIGINT NOT NULL REFERENCES menu_items(id) ON DELETE CASCADE,
    dietary_tag VARCHAR(50) NOT NULL,
    PRIMARY KEY (menu_item_id, dietary_tag)
);

CREATE TABLE menu_item_allergens (
    menu_item_id BIGINT NOT NULL REFERENCES menu_items(id) ON DELETE CASCADE,
    allergen VARCHAR(50) NOT NULL,
    PRIMARY KEY (menu_item_id, allergen)
);

-- 4. Happy Hour Rules
CREATE TABLE happy_hour_rules (
    id BIGSERIAL PRIMARY KEY,
    restaurant_id UUID REFERENCES restaurants(id) ON DELETE CASCADE,
    title VARCHAR(100) NOT NULL,
    discount_percentage DECIMAL(5, 2) NOT NULL CHECK (discount_percentage >= 0),
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 5. Combos
CREATE TABLE combos (
    id BIGSERIAL PRIMARY KEY,
    restaurant_id UUID REFERENCES restaurants(id) ON DELETE CASCADE,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    combo_price DECIMAL(10, 2) NOT NULL CHECK (combo_price >= 0),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE combo_items (
    combo_id BIGINT NOT NULL REFERENCES combos(id) ON DELETE CASCADE,
    menu_item_id BIGINT NOT NULL REFERENCES menu_items(id) ON DELETE CASCADE,
    PRIMARY KEY (combo_id, menu_item_id)
);

-- Initial Seed Data
INSERT INTO menu_categories (name, description, is_active, sort_order) VALUES
('Breakfast', 'Morning favorites and fresh items', true, 1),
('Lunch', 'Hearty lunch bowls and savories', true, 2),
('Dinner', 'Evening gourmet main courses', true, 3),
('Healthy Bowls', 'Nutritional salad bowls and power foods', true, 4)
ON CONFLICT (name) DO NOTHING;

INSERT INTO menu_items (name, description, price, category_id, image_url, calories, spicy_level, stock_quantity, status, is_visible, is_available) VALUES
('Mediterranean Salad Bowl', 'Fresh veggies, olives, feta cheese & Mediterranean vinaigrette', 14.50, 4, 'https://images.unsplash.com/photo-1540420773420-3366772f4999?auto=format&fit=crop&w=600&q=80', 450, 'NONE', 45, 'IN_STOCK', true, true),
('Artisan Avocado Toast', 'Crusty sourdough bread topped with fresh avocado and egg', 12.00, 1, 'https://images.unsplash.com/photo-1525351484163-7529414344d8?auto=format&fit=crop&w=600&q=80', 380, 'NONE', 5, 'LOW_STOCK', true, true),
('Spicy BBQ Pulled Pork', 'Slow cooked pulled pork with smoked BBQ glaze', 16.50, 2, 'https://images.unsplash.com/photo-1544025162-d76694265947?auto=format&fit=crop&w=600&q=80', 650, 'MILD', 0, 'OUT_OF_STOCK', false, false);

INSERT INTO menu_item_dietary_tags (menu_item_id, dietary_tag) VALUES
(1, 'Veg'),
(2, 'Veg'),
(3, 'Non-Veg');

INSERT INTO menu_item_allergens (menu_item_id, allergen) VALUES
(2, 'Contains Egg');