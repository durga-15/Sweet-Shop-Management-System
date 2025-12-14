-- Database setup script for Sweet Shop Management System
-- Run this script to create the database (if needed)

-- Create database (run as postgres user)
-- CREATE DATABASE sweetshop;

-- The tables will be created automatically by Hibernate
-- However, you can manually verify with:
-- \dt (in psql)

-- To create an admin user after registration:
-- UPDATE users SET role = 'ADMIN' WHERE username = 'your_username';

-- Example: Insert sample sweets (optional)
-- INSERT INTO sweets (name, category, price, quantity) VALUES
-- ('Gulab Jamun', 'Indian', 50.00, 100),
-- ('Rasgulla', 'Indian', 45.00, 150),
-- ('Barfi', 'Indian', 60.00, 80),
-- ('Ladoo', 'Indian', 55.00, 120);

