INSERT INTO menu_items
(id, restaurant_id, name, description, image_url, price, category, is_veg, available, created_at, updated_at)
VALUES
('22222222-2222-2222-2222-222222222221',
 '11111111-1111-1111-1111-111111111111',
 'Paneer Tikka',
 'Tandoori paneer with onions and peppers',
 NULL, 249.00, 'STARTERS', TRUE, TRUE, NOW(), NOW()),
('22222222-2222-2222-2222-222222222222',
 '11111111-1111-1111-1111-111111111111',
 'Veg Biryani',
 'Basmati rice with vegetables and aromatic spices',
 NULL, 229.00, 'MAIN COURSE', TRUE, TRUE, NOW(), NOW()),
('22222222-2222-2222-2222-222222222223',
 '11111111-1111-1111-1111-111111111111',
 'Chicken Biryani',
 'Basmati rice with chicken and aromatic spices',
 NULL, 299.00, 'MAIN COURSE', FALSE, TRUE, NOW(), NOW());
