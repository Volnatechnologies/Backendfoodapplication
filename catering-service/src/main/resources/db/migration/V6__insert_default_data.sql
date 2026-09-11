INSERT INTO roles (id, name)
SELECT '11111111-1111-1111-1111-111111111111'::uuid, 'BUSINESS_OWNER'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'BUSINESS_OWNER');

INSERT INTO roles (id, name)
SELECT '22222222-2222-2222-2222-222222222222'::uuid, 'MANAGER'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'MANAGER');

INSERT INTO roles (id, name)
SELECT '33333333-3333-3333-3333-333333333333'::uuid, 'STAFF'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'STAFF');

INSERT INTO roles (id, name)
SELECT '44444444-4444-4444-4444-444444444444'::uuid, 'ADMIN'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ADMIN');

INSERT INTO catering_event_types (id, name, description, active)
SELECT 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa'::uuid, 'Corporate Buffet', 'Business catering service', TRUE
WHERE NOT EXISTS (SELECT 1 FROM catering_event_types WHERE name = 'Corporate Buffet');

INSERT INTO catering_event_types (id, name, description, active)
SELECT 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb'::uuid, 'Wedding Reception', 'Elegant wedding reception service', TRUE
WHERE NOT EXISTS (SELECT 1 FROM catering_event_types WHERE name = 'Wedding Reception');

INSERT INTO catering_event_types (id, name, description, active)
SELECT 'cccccccc-cccc-cccc-cccc-cccccccccccc'::uuid, 'Birthday', 'Birthday celebration menu', TRUE
WHERE NOT EXISTS (SELECT 1 FROM catering_event_types WHERE name = 'Birthday');

INSERT INTO catering_event_types (id, name, description, active)
SELECT 'dddddddd-dddd-dddd-dddd-dddddddddddd'::uuid, 'Private Party', 'Private party catering', TRUE
WHERE NOT EXISTS (SELECT 1 FROM catering_event_types WHERE name = 'Private Party');

INSERT INTO catering_event_types (id, name, description, active)
SELECT 'eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee'::uuid, 'Charity Luncheon', 'Charity luncheon setup', TRUE
WHERE NOT EXISTS (SELECT 1 FROM catering_event_types WHERE name = 'Charity Luncheon');

INSERT INTO catering_event_types (id, name, description, active)
SELECT 'ffffffff-ffff-ffff-ffff-ffffffffffff'::uuid, 'Conference', 'Conference and seminar catering', TRUE
WHERE NOT EXISTS (SELECT 1 FROM catering_event_types WHERE name = 'Conference');

INSERT INTO catering_event_types (id, name, description, active)
SELECT '12121212-1212-1212-1212-121212121212'::uuid, 'Other', 'Other event catering', TRUE
WHERE NOT EXISTS (SELECT 1 FROM catering_event_types WHERE name = 'Other');

INSERT INTO catering_menu_packages (id, name, description, price_per_guest, image_url, active)
SELECT '11111111-2222-3333-4444-555555555555'::uuid, 'Corporate Bites', 'Casual business buffet', 45.00, NULL, TRUE
WHERE NOT EXISTS (SELECT 1 FROM catering_menu_packages WHERE name = 'Corporate Bites');

INSERT INTO catering_menu_packages (id, name, description, price_per_guest, image_url, active)
SELECT '11111111-2222-3333-4444-666666666666'::uuid, 'Premium Plated', 'Premium plated dining', 72.50, NULL, TRUE
WHERE NOT EXISTS (SELECT 1 FROM catering_menu_packages WHERE name = 'Premium Plated');

INSERT INTO catering_custom_options (id, name, description, price, active)
SELECT 'aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee'::uuid, 'Add Vegan Dessert Platter', 'Vegan dessert platter with seasonal fruit', 250.00, TRUE
WHERE NOT EXISTS (SELECT 1 FROM catering_custom_options WHERE name = 'Add Vegan Dessert Platter');

INSERT INTO catering_custom_options (id, name, description, price, active)
SELECT 'bbbbbbbb-cccc-dddd-eeee-ffffffffffff'::uuid, 'Premium Beverage Upgrade', 'Premium beverage station with sparkling water and juices', 400.00, TRUE
WHERE NOT EXISTS (SELECT 1 FROM catering_custom_options WHERE name = 'Premium Beverage Upgrade');

INSERT INTO catering_inquiries (id, name, email, phone, event_type, message, guest_count, event_date, status)
SELECT '55555555-5555-5555-5555-555555555555'::uuid, 'Alicia Brown', 'alicia@example.com', '5551112233', 'Corporate Buffet', 'Need catering for 100 guests next month.', 100, CURRENT_DATE + INTERVAL '10 day', 'NEW'
WHERE NOT EXISTS (SELECT 1 FROM catering_inquiries WHERE email = 'alicia@example.com');

INSERT INTO catering_inquiries (id, name, email, phone, event_type, message, guest_count, event_date, status)
SELECT '66666666-6666-6666-6666-666666666666'::uuid, 'Liam Smith', 'liam@example.com', '5554445566', 'Wedding Reception', 'Looking for wedding reception pricing.', 200, CURRENT_DATE + INTERVAL '25 day', 'READ'
WHERE NOT EXISTS (SELECT 1 FROM catering_inquiries WHERE email = 'liam@example.com');
