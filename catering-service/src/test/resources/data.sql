INSERT INTO roles (id, name) VALUES
    ('11111111-1111-1111-1111-111111111111', 'BUSINESS_OWNER'),
    ('22222222-2222-2222-2222-222222222222', 'MANAGER'),
    ('33333333-3333-3333-3333-333333333333', 'STAFF'),
    ('44444444-4444-4444-4444-444444444444', 'ADMIN');

INSERT INTO catering_event_types (id, name, description, active, created_at, updated_at) VALUES
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Corporate Buffet', 'Business catering service', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Wedding Reception', 'Elegant wedding reception service', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO catering_menu_packages (id, name, description, price_per_guest, image_url, active, created_at, updated_at) VALUES
    ('11111111-2222-3333-4444-555555555555', 'Corporate Bites', 'Casual business buffet', 45.00, NULL, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('11111111-2222-3333-4444-666666666666', 'Premium Plated', 'Premium plated dining', 120.00, NULL, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO catering_custom_options (id, name, description, price, active, created_at, updated_at) VALUES
    ('cccccccc-1111-2222-3333-444444444444', 'Beverage Station', 'Unlimited drinks and refreshments', 8.00, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('cccccccc-2222-3333-4444-555555555555', 'Dessert Platter', 'Assorted gourmet sweets and mini pastries', 5.00, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);