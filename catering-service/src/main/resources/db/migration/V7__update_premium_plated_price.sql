-- Safe corrective migration to update Premium Plated package price to 120.00 to match UI
UPDATE catering_menu_packages
SET price_per_guest = 120.00
WHERE name = 'Premium Plated';
