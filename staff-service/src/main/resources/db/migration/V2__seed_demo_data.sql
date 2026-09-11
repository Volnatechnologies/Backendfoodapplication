-- =============================================================================
-- Migration V2: Seed UI Sample Data for Staff Management & Shift Planning
-- =============================================================================

-- 1. Insert Staff Members matching UI screenshots
INSERT INTO staff_members (id, name, role, status, performance, avatar_initials, email, phone, station_area, hourly_rate, weekly_budget_hours, active)
VALUES
    ('11111111-1111-1111-1111-111111111101', 'Jane Smith', 'Head Chef', 'CLOCKED_IN', 4.90, 'JS', 'jane.smith@caloryhive.com', '+1-555-0101', 'Kitchen', 35.00, 40.00, TRUE),
    ('11111111-1111-1111-1111-111111111102', 'Marcus Reed', 'Sous Chef', 'OFF_DUTY', 4.70, 'MR', 'marcus.reed@caloryhive.com', '+1-555-0102', 'Line', 28.00, 40.00, TRUE),
    ('11111111-1111-1111-1111-111111111103', 'Alex Reed', 'Sous Chef', 'OFF_DUTY', 4.75, 'AR', 'alex.reed@caloryhive.com', '+1-555-0103', 'Line', 28.00, 40.00, TRUE),
    ('11111111-1111-1111-1111-111111111104', 'Maria Garcia', 'Server', 'ACTIVE', 4.85, 'MG', 'maria.garcia@caloryhive.com', '+1-555-0104', 'Dining Area', 20.00, 35.00, TRUE);

-- 2. Insert Shifts matching UI calendar grid (Week of Oct 23 - Oct 29, 2023)
-- Jane S. (Head Chef):
-- Mon 23: 08:00 - 16:00 (Prep)
INSERT INTO shifts (id, staff_id, shift_date, start_time, end_time, shift_role, station_area, shift_type, shift_category, status, notes)
VALUES
    ('22222222-2222-2222-2222-222222222201', '11111111-1111-1111-1111-111111111101', '2023-10-23', '08:00:00', '16:00:00', 'Head Chef', 'Prep', 'REGULAR', 'MORNING', 'PUBLISHED', 'Morning kitchen prep'),
    ('22222222-2222-2222-2222-222222222202', '11111111-1111-1111-1111-111111111101', '2023-10-24', '08:00:00', '16:00:00', 'Head Chef', 'Prep', 'REGULAR', 'MORNING', 'PUBLISHED', 'Morning kitchen prep'),
    ('22222222-2222-2222-2222-222222222203', '11111111-1111-1111-1111-111111111101', '2023-10-25', '12:00:00', '20:00:00', 'Head Chef', 'Service', 'REGULAR', 'AFTERNOON', 'PUBLISHED', 'Afternoon/dinner service');

-- Alex R. (Sous Chef):
-- Wed 25: 10:00 - 18:00 (Line), Thu 26: 10:00 - 18:00 (Line), Fri 27: 14:00 - 22:00 (Closing)
INSERT INTO shifts (id, staff_id, shift_date, start_time, end_time, shift_role, station_area, shift_type, shift_category, status, notes)
VALUES
    ('22222222-2222-2222-2222-222222222204', '11111111-1111-1111-1111-111111111103', '2023-10-25', '10:00:00', '18:00:00', 'Sous Chef', 'Line', 'REGULAR', 'MORNING', 'PUBLISHED', 'Lunch and line service'),
    ('22222222-2222-2222-2222-222222222205', '11111111-1111-1111-1111-111111111103', '2023-10-26', '10:00:00', '18:00:00', 'Sous Chef', 'Line', 'REGULAR', 'MORNING', 'PUBLISHED', 'Line service and prep'),
    ('22222222-2222-2222-2222-222222222206', '11111111-1111-1111-1111-111111111103', '2023-10-27', '14:00:00', '22:00:00', 'Sous Chef', 'Closing', 'REGULAR', 'AFTERNOON', 'PUBLISHED', 'Closing kitchen supervision');

-- Maria Garcia Friday Shift (for swap request)
INSERT INTO shifts (id, staff_id, shift_date, start_time, end_time, shift_role, station_area, shift_type, shift_category, status, notes)
VALUES
    ('22222222-2222-2222-2222-222222222207', '11111111-1111-1111-1111-111111111104', '2023-10-27', '18:00:00', '22:00:00', 'Server', 'Dining Area', 'REGULAR', 'EVENING', 'PUBLISHED', 'Friday evening dinner shift');

-- 3. Insert 2 Pending Shift Requests matching UI Right Panel
INSERT INTO shift_requests (id, staff_id, request_type, shift_id, target_staff_id, requested_date, reason, status)
VALUES
    ('33333333-3333-3333-3333-333333333301', '11111111-1111-1111-1111-111111111104', 'SHIFT_SWAP', '22222222-2222-2222-2222-222222222207', '11111111-1111-1111-1111-111111111103', '2023-10-27', 'Wants to swap Friday Evening (Oct 27) with Alex R.', 'PENDING'),
    ('33333333-3333-3333-3333-333333333302', '11111111-1111-1111-1111-111111111101', 'TIME_OFF', NULL, NULL, '2023-11-02', 'Requesting Nov 2nd - Nov 4th off for family event.', 'PENDING');
UPDATE shift_requests SET end_date = '2023-11-04' WHERE id = '33333333-3333-3333-3333-333333333302';
