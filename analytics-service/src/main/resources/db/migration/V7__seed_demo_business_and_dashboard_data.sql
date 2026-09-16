-- ====================================================================
-- V7: Seed Demo Business and Dashboard Data Matching UI References
-- ====================================================================

-- 1. Roles
INSERT INTO roles (id, name) VALUES
('11111111-1111-1111-1111-111111111111', 'ROLE_BUSINESS_OWNER'),
('22222222-2222-2222-2222-222222222222', 'ROLE_BUSINESS_ADMIN'),
('33333333-3333-3333-3333-333333333333', 'ROLE_MANAGER'),
('44444444-4444-4444-4444-444444444444', 'ROLE_STAFF'),
('55555555-5555-5555-5555-555555555555', 'ROLE_FINANCE_ADMIN')
ON CONFLICT (id) DO NOTHING;

-- 2. Demo Business
INSERT INTO businesses (id, owner_id, name, description, phone, email, address, cuisine_type, status, created_at, updated_at)
VALUES (
    '00000000-0000-0000-0000-000000000001',
    '00000000-0000-0000-0000-000000000002',
    'Calorye Hive Central',
    'Fine Casual Healthy Dining & Artisan Kitchen',
    '+1 (555) 234-5678',
    'owner@caloryehive.com',
    '100 Market Street, Suite 400, San Francisco, CA 94105',
    'Artisan Healthy / Fusion',
    'ACTIVE',
    CURRENT_TIMESTAMP - INTERVAL '180 days',
    CURRENT_TIMESTAMP
)
ON CONFLICT (id) DO NOTHING;

-- 3. Demo User (password: Password123! hashed with BCrypt)
INSERT INTO users (id, business_id, first_name, last_name, email, password, enabled, created_at, updated_at)
VALUES (
    '00000000-0000-0000-0000-000000000002',
    '00000000-0000-0000-0000-000000000001',
    'Sarah',
    'Jenkins',
    'owner@caloryehive.com',
    '$2a$10$w3j/Y7k62e4aQh/6e93rkuqR17q2iTqjFh.E3167198u3NfeV43yW',
    TRUE,
    CURRENT_TIMESTAMP - INTERVAL '180 days',
    CURRENT_TIMESTAMP
)
ON CONFLICT (id) DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
VALUES
('00000000-0000-0000-0000-000000000002', '11111111-1111-1111-1111-111111111111'),
('00000000-0000-0000-0000-000000000002', '55555555-5555-5555-5555-555555555555')
ON CONFLICT DO NOTHING;

-- 4. Seed UI Orders
INSERT INTO orders (id, order_number, business_id, customer_name, customer_phone, order_type, delivery_address, total_amount, status, payment_status, satisfaction_rating, fulfillment_time_minutes, created_at)
VALUES
('a0000001-0000-0000-0000-000000000001', 'ORD-9021', '00000000-0000-0000-0000-000000000001', 'Eleanor V.', '+1-555-0101', 'DINE_IN', NULL, 165.00, 'COMPLETED', 'PAID', 5.0, 18, CURRENT_TIMESTAMP - INTERVAL '2 hours'),
('a0000001-0000-0000-0000-000000000002', 'ORD-9020', '00000000-0000-0000-0000-000000000001', 'Marcus T.', '+1-555-0102', 'DELIVERY', '742 Evergreen Terrace', 64.50, 'PENDING', 'PENDING', 4.5, 25, CURRENT_TIMESTAMP - INTERVAL '3 hours'),
('a0000001-0000-0000-0000-000000000003', 'ORD-9019', '00000000-0000-0000-0000-000000000001', 'Sophia L.', '+1-555-0103', 'DINE_IN', NULL, 210.00, 'COMPLETED', 'PAID', 5.0, 20, CURRENT_TIMESTAMP - INTERVAL '5 hours'),
('a0000001-0000-0000-0000-000000000004', 'ORD-9018', '00000000-0000-0000-0000-000000000001', 'David K.', '+1-555-0104', 'DINE_IN', NULL, 145.20, 'COMPLETED', 'PAID', 4.8, 22, CURRENT_TIMESTAMP - INTERVAL '1 day'),
('a0000001-0000-0000-0000-000000000005', 'ORD-9017', '00000000-0000-0000-0000-000000000001', 'Rachel Green', '+1-555-0105', 'CATERING', 'Corporate Plaza #4', 580.00, 'COMPLETED', 'PAID', 5.0, 45, CURRENT_TIMESTAMP - INTERVAL '2 days'),
('a0000001-0000-0000-0000-000000000006', 'ORD-9016', '00000000-0000-0000-0000-000000000001', 'James Wilson', '+1-555-0106', 'DELIVERY', '124 Conch St', 82.50, 'COMPLETED', 'PAID', 4.7, 24, CURRENT_TIMESTAMP - INTERVAL '3 days'),
('a0000001-0000-0000-0000-000000000007', 'ORD-9015', '00000000-0000-0000-0000-000000000001', 'Amelia Earhart', '+1-555-0107', 'DINE_IN', NULL, 95.00, 'COMPLETED', 'PAID', 4.9, 19, CURRENT_TIMESTAMP - INTERVAL '4 days')
ON CONFLICT (id) DO NOTHING;

-- Seed Order Items matching Top Selling Items
INSERT INTO order_items (id, order_id, menu_item_name, category_name, quantity, price, image_url)
VALUES
('b0000001-0000-0000-0000-000000000001', 'a0000001-0000-0000-0000-000000000001', 'Truffle Mushroom Burger', 'Main Course', 342, 16.00, 'https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=100'),
('b0000001-0000-0000-0000-000000000002', 'a0000001-0000-0000-0000-000000000002', 'Harvest Quinoa Bowl', 'Salads', 289, 12.00, 'https://images.unsplash.com/photo-1540420773420-3366772f4999?w=100'),
('b0000001-0000-0000-0000-000000000003', 'a0000001-0000-0000-0000-000000000003', 'Sweet Potato Fries', 'Sides', 416, 7.00, 'https://images.unsplash.com/photo-1576107232684-1279f3908594?w=100')
ON CONFLICT (id) DO NOTHING;

-- 5. Seed Reviews & Responses
INSERT INTO reviews (id, business_id, customer_name, customer_avatar, rating, comment, sentiment, sentiment_score, answered, has_photos, order_reference, created_at)
VALUES
('c0000001-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', 'Victoria Sterling', 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=100', 5, 'The Truffle Mushroom Burger is the best burger I have ever tasted! Extraordinary truffle aroma and perfectly toasted brioche.', 'POSITIVE', 98.00, TRUE, TRUE, 'ORD-9021', CURRENT_TIMESTAMP - INTERVAL '1 day'),
('c0000001-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000001', 'Liam Henderson', 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=100', 5, 'Exceptional catering service for our corporate luncheon. Arrived exactly on time and hot.', 'POSITIVE', 95.00, TRUE, FALSE, 'ORD-9017', CURRENT_TIMESTAMP - INTERVAL '2 days'),
('c0000001-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000001', 'Chloe Zhao', 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=100', 4, 'Very fresh ingredients in the Quinoa bowl, loved the citrus vinaigrette dressing!', 'POSITIVE', 88.00, FALSE, TRUE, 'ORD-9019', CURRENT_TIMESTAMP - INTERVAL '3 days'),
('c0000001-0000-0000-0000-000000000004', '00000000-0000-0000-0000-000000000001', 'Brandon Cole', 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=100', 2, 'Delivery arrived 15 minutes late and fries were a bit lukewarm.', 'NEGATIVE', 25.00, FALSE, FALSE, 'ORD-9020', CURRENT_TIMESTAMP - INTERVAL '4 days')
ON CONFLICT (id) DO NOTHING;

INSERT INTO review_photos (id, review_id, photo_url)
VALUES
('d0000001-0000-0000-0000-000000000001', 'c0000001-0000-0000-0000-000000000001', 'https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=600'),
('d0000001-0000-0000-0000-000000000002', 'c0000001-0000-0000-0000-000000000003', 'https://images.unsplash.com/photo-1540420773420-3366772f4999?w=600')
ON CONFLICT (id) DO NOTHING;

INSERT INTO review_responses (id, review_id, business_id, responder_id, responder_name, response_text, created_at)
VALUES
('e0000001-0000-0000-0000-000000000001', 'c0000001-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000002', 'Sarah Jenkins (General Manager)', 'Thank you so much Victoria! Our culinary team puts immense love into our truffle glaze. Looking forward to welcoming you back soon!', CURRENT_TIMESTAMP - INTERVAL '20 hours'),
('e0000001-0000-0000-0000-000000000002', 'c0000001-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000002', 'Sarah Jenkins (General Manager)', 'Liam, it was our absolute pleasure catering for your corporate group! Delighted to hear everything was on point.', CURRENT_TIMESTAMP - INTERVAL '36 hours')
ON CONFLICT (id) DO NOTHING;

-- 6. Financial Overview Setup
INSERT INTO business_financial_accounts (id, business_id, available_balance, pending_balance, monthly_goal, monthly_goal_achieved, currency, next_payout_date, verified_by)
VALUES (
    'f0000001-0000-0000-0000-000000000001',
    '00000000-0000-0000-0000-000000000001',
    14285.50,
    3412.00,
    42500.00,
    27625.00,
    'USD',
    CURRENT_DATE + INTERVAL '5 days',
    'Partner Central Finance'
)
ON CONFLICT (business_id) DO NOTHING;

INSERT INTO bank_accounts (id, business_id, bank_name, account_holder_name, account_type, masked_account_number, routing_number, is_default, is_verified)
VALUES
('f1000001-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', 'Chase Business Platinum', 'Calorye Hive Central LLC', 'Checking Account', '**** 4210', '021000021', TRUE, TRUE),
('f1000001-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000001', 'Wells Fargo Commercial', 'Calorye Hive Central LLC', 'Savings Account', '**** 8820', '121000244', FALSE, TRUE)
ON CONFLICT (id) DO NOTHING;

INSERT INTO financial_transactions (id, business_id, type, amount, status, description, reference_id, created_at)
VALUES
('f2000001-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', 'WITHDRAWAL', -5000.00, 'COMPLETED', 'Withdrawal to Bank (...4210)', 'WTH-88219', CURRENT_TIMESTAMP - INTERVAL '5 days'),
('f2000001-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000001', 'DAILY_SALES', 2841.20, 'SETTLED', 'Daily Sales Revenue', 'STL-74912', CURRENT_TIMESTAMP - INTERVAL '7 days'),
('f2000001-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000001', 'WITHDRAWAL', -3500.00, 'COMPLETED', 'Withdrawal to Bank (...4210)', 'WTH-88102', CURRENT_TIMESTAMP - INTERVAL '12 days')
ON CONFLICT (id) DO NOTHING;

-- 7. Partnership Rewards & Loyalty
INSERT INTO reward_tiers (id, tier_name, display_name, min_points, next_tier_name, next_tier_points, commission_discount_percent, tagline)
VALUES
('80000001-0000-0000-0000-000000000001', 'SILVER', 'Silver Partner', 0, 'Gold', 5000, 0.00, 'Emerging Culinary Partner'),
('80000001-0000-0000-0000-000000000002', 'GOLD', 'Gold Partner', 5000, 'Platinum', 10000, 2.00, 'Top 15% of CaloryeHive Restaurants'),
('80000001-0000-0000-0000-000000000003', 'PLATINUM', 'Platinum Partner', 10000, 'Diamond', 15000, 5.00, 'Top 5% of CaloryeHive Restaurants'),
('80000001-0000-0000-0000-000000000004', 'DIAMOND', 'Diamond Partner', 15000, NULL, NULL, 10.00, 'Top 1% Elite Culinary Leader')
ON CONFLICT (id) DO NOTHING;

INSERT INTO reward_perks (id, tier_id, title, description, icon_name, is_upcoming, sort_order)
VALUES
('81000001-0000-0000-0000-000000000001', '80000001-0000-0000-0000-000000000003', 'Priority Support', 'Direct line to senior account managers with under 5-minute response time.', 'Headphones', FALSE, 1),
('81000001-0000-0000-0000-000000000002', '80000001-0000-0000-0000-000000000003', 'Advanced Analytics', 'Unlock deep demographic insights and predictive demand forecasting.', 'TrendingUp', FALSE, 2),
('81000001-0000-0000-0000-000000000003', '80000001-0000-0000-0000-000000000003', 'Verified Badge', 'Distinguished icon on consumer app increasing conversion by ~12%.', 'CheckCircle2', FALSE, 3),
('81000001-0000-0000-0000-000000000004', '80000001-0000-0000-0000-000000000003', 'Upcoming: Diamond Status', 'Reach 15,000 pts to unlock 0% commission on pickup orders.', 'Lock', TRUE, 4)
ON CONFLICT (id) DO NOTHING;

INSERT INTO business_reward_accounts (id, business_id, current_tier_id, total_points_earned, current_points_balance)
VALUES (
    '82000001-0000-0000-0000-000000000001',
    '00000000-0000-0000-0000-000000000001',
    '80000001-0000-0000-0000-000000000003',
    12450,
    12450
)
ON CONFLICT (business_id) DO NOTHING;

INSERT INTO reward_objectives (id, title, description, icon_name, points_reward, target_type, target_value, unit, sort_order)
VALUES
('83000001-0000-0000-0000-000000000001', 'Maintain 4.8 Rating', 'Sustain a stellar average customer rating across all orders for 3 consecutive months.', 'Star', 500, 'RATING_CONSISTENCY', 3.00, 'months', 1),
('83000001-0000-0000-0000-000000000002', 'Volume Excellence', 'Successfully prepare and dispatch 500 orders within the current calendar month.', 'ShoppingBag', 1200, 'ORDER_VOLUME', 500.00, 'Orders', 2)
ON CONFLICT (id) DO NOTHING;

INSERT INTO business_objective_progress (id, business_id, objective_id, current_value, target_value, progress_percentage, progress_label, is_completed)
VALUES
('84000001-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', '83000001-0000-0000-0000-000000000001', 2.00, 3.00, 66, '2 of 3 months', FALSE),
('84000001-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000001', '83000001-0000-0000-0000-000000000002', 342.00, 500.00, 68, '342 / 500 Orders', FALSE)
ON CONFLICT (id) DO NOTHING;

INSERT INTO reward_catalog_items (id, title, description, points_cost, category, image_url, is_active)
VALUES
('85000001-0000-0000-0000-000000000001', 'Pro Photoshoot', '10 menu items styled & shot by a professional food photographer.', 5000, 'MARKETING', 'https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=300', TRUE),
('85000001-0000-0000-0000-000000000002', 'Sponsored Listing', 'Top placement on the consumer app discovery page for 7 full days.', 8500, 'PROMOTION', 'https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=300', TRUE),
('85000001-0000-0000-0000-000000000003', 'Kitchen Flow Consultation', '1-on-1 operational audit with a certified Michelin-starred kitchen consultant.', 3500, 'OPERATIONS', 'https://images.unsplash.com/photo-1556910103-1c02745aae4d?w=300', TRUE)
ON CONFLICT (id) DO NOTHING;

INSERT INTO reward_transactions (id, business_id, type, points, reference_type, reference_id, balance_after, description, created_at)
VALUES
('86000001-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', 'EARN', 5000, 'ORDER_MILESTONE', 'MLS-1000', 5000, 'Reached 1,000 lifetime completed orders', CURRENT_TIMESTAMP - INTERVAL '60 days'),
('86000001-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000001', 'BONUS', 3000, 'TIER_UPGRADE', 'TIER-GOLD', 8000, 'Gold Tier promotion bonus', CURRENT_TIMESTAMP - INTERVAL '45 days'),
('86000001-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000001', 'EARN', 4450, 'PERFORMANCE_OCT', 'MONTHLY-10', 12450, 'Consistent 4.8+ rating performance incentive', CURRENT_TIMESTAMP - INTERVAL '10 days')
ON CONFLICT (id) DO NOTHING;
