-- =============================================================
-- Expense & Budget Tracker — Seed Data
-- =============================================================
-- NOTE: The application uses H2 in-memory databases by default.
-- This script is for reference and for MySQL migration.
--
-- To use with MySQL:
--   1. Create three databases: user_db, transaction_db, budget_db
--   2. Run each section in the respective database
--
-- Demo user password: "password123"
-- BCrypt hash generated with strength 10
-- =============================================================

-- ─────────────────────────────────────────────────────────────
-- DATABASE: user_db
-- ─────────────────────────────────────────────────────────────
CREATE DATABASE IF NOT EXISTS user_db;
USE user_db;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Demo user: adhiraj@example.com / password123
INSERT INTO users (name, email, password) VALUES (
    'Adhiraj Sharma',
    'adhiraj@example.com',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh/S'
);

-- ─────────────────────────────────────────────────────────────
-- DATABASE: transaction_db
-- ─────────────────────────────────────────────────────────────
CREATE DATABASE IF NOT EXISTS transaction_db;
USE transaction_db;

CREATE TABLE IF NOT EXISTS transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    type ENUM('INCOME','EXPENSE') NOT NULL,
    amount DECIMAL(12,2) NOT NULL,
    category VARCHAR(50) NOT NULL,
    description VARCHAR(255),
    date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 6 months of data for user_id=1 (Adhiraj)
-- ── May 2026 ──
INSERT INTO transactions (user_id, type, amount, category, description, date) VALUES
(1,'INCOME', 55000.00,'Salary',     'Monthly salary - May',          '2026-05-01'),
(1,'EXPENSE',15000.00,'Housing',    'Apartment rent - May',          '2026-05-02'),
(1,'EXPENSE', 8500.00,'Groceries',  'Monthly groceries - BigBasket',  '2026-05-05'),
(1,'EXPENSE', 2500.00,'Transport',  'Fuel + Ola rides - May',         '2026-05-10'),
(1,'EXPENSE', 3200.00,'Food',       'Restaurants and Zomato - May',   '2026-05-15'),
(1,'EXPENSE', 1800.00,'Utilities',  'Electricity + internet - May',   '2026-05-20'),
(1,'EXPENSE', 1500.00,'Entertainment','Netflix + movies - May',       '2026-05-25'),
(1,'INCOME',  5000.00,'Freelance',  'Web design project - May',       '2026-05-28');

-- ── June 2026 ──
INSERT INTO transactions (user_id, type, amount, category, description, date) VALUES
(1,'INCOME', 55000.00,'Salary',     'Monthly salary - June',         '2026-06-01'),
(1,'EXPENSE',15000.00,'Housing',    'Apartment rent - June',         '2026-06-02'),
(1,'EXPENSE', 7800.00,'Groceries',  'Monthly groceries - June',      '2026-06-05'),
(1,'EXPENSE', 3200.00,'Transport',  'Fuel + metro - June',           '2026-06-10'),
(1,'EXPENSE', 4100.00,'Food',       'Restaurants and Swiggy - June', '2026-06-15'),
(1,'EXPENSE', 1900.00,'Utilities',  'Electricity + internet - June', '2026-06-20'),
(1,'EXPENSE', 2200.00,'Entertainment','Concerts + OTT - June',       '2026-06-25'),
(1,'EXPENSE', 1200.00,'Healthcare', 'Doctor visit + medicines',      '2026-06-27');

-- ── July 2026 ──
INSERT INTO transactions (user_id, type, amount, category, description, date) VALUES
(1,'INCOME', 55000.00,'Salary',     'Monthly salary - July',         '2026-07-01'),
(1,'INCOME',  8000.00,'Freelance',  'Mobile app project - July',     '2026-07-03'),
(1,'EXPENSE',15000.00,'Housing',    'Apartment rent - July',         '2026-07-02'),
(1,'EXPENSE', 9200.00,'Groceries',  'Monthly groceries + Big Bazaar','2026-07-05'),
(1,'EXPENSE', 2800.00,'Transport',  'Fuel + cab rides - July',       '2026-07-10'),
(1,'EXPENSE', 3500.00,'Food',       'Restaurants - July',            '2026-07-15'),
(1,'EXPENSE', 1750.00,'Utilities',  'Bills - July',                  '2026-07-20'),
(1,'EXPENSE', 5000.00,'Education',  'Online course - Udemy',         '2026-07-22');

-- ── August 2026 ──
INSERT INTO transactions (user_id, type, amount, category, description, date) VALUES
(1,'INCOME', 55000.00,'Salary',     'Monthly salary - August',       '2026-08-01'),
(1,'EXPENSE',15000.00,'Housing',    'Apartment rent - August',       '2026-08-02'),
(1,'EXPENSE', 8800.00,'Groceries',  'Monthly groceries - August',    '2026-08-05'),
(1,'EXPENSE', 3100.00,'Transport',  'Fuel + Ola - August',           '2026-08-10'),
(1,'EXPENSE', 2900.00,'Food',       'Restaurants - August',          '2026-08-15'),
(1,'EXPENSE', 2000.00,'Utilities',  'Bills - August',                '2026-08-20'),
(1,'EXPENSE', 3500.00,'Entertainment','Gaming + OTT - August',       '2026-08-25'),
(1,'EXPENSE', 2500.00,'Healthcare', 'Annual health checkup',         '2026-08-27');

-- ── September 2026 ──
INSERT INTO transactions (user_id, type, amount, category, description, date) VALUES
(1,'INCOME', 60000.00,'Salary',     'Monthly salary + increment',    '2026-09-01'),
(1,'EXPENSE',15000.00,'Housing',    'Apartment rent - September',    '2026-09-02'),
(1,'EXPENSE', 9500.00,'Groceries',  'Monthly groceries - September', '2026-09-05'),
(1,'EXPENSE', 2600.00,'Transport',  'Fuel - September',              '2026-09-10'),
(1,'EXPENSE', 4200.00,'Food',       'Festive dining out - September','2026-09-15'),
(1,'EXPENSE', 1900.00,'Utilities',  'Bills - September',             '2026-09-20'),
(1,'EXPENSE', 8000.00,'Entertainment','Dussehra travel + events',    '2026-09-28');

-- ── October 2026 ──
INSERT INTO transactions (user_id, type, amount, category, description, date) VALUES
(1,'INCOME', 60000.00,'Salary',     'Monthly salary - October',      '2026-10-01'),
(1,'INCOME', 12000.00,'Freelance',  'Dashboard design contract',     '2026-10-05'),
(1,'EXPENSE',15000.00,'Housing',    'Apartment rent - October',      '2026-10-02'),
(1,'EXPENSE',10200.00,'Groceries',  'Diwali groceries + sweets',     '2026-10-05'),
(1,'EXPENSE', 3800.00,'Transport',  'Fuel + Diwali travel',          '2026-10-10'),
(1,'EXPENSE', 5500.00,'Food',       'Festive restaurants - October', '2026-10-15'),
(1,'EXPENSE', 2100.00,'Utilities',  'Bills - October',               '2026-10-20'),
(1,'EXPENSE',12000.00,'Entertainment','Diwali shopping + gifts',     '2026-10-25');

-- ─────────────────────────────────────────────────────────────
-- DATABASE: budget_db
-- ─────────────────────────────────────────────────────────────
CREATE DATABASE IF NOT EXISTS budget_db;
USE budget_db;

CREATE TABLE IF NOT EXISTS budgets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    category VARCHAR(50) NOT NULL,
    budget_limit DECIMAL(12,2) NOT NULL,
    month INT NOT NULL,
    year INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_cat_month_year (user_id, category, month, year)
);

-- Budgets for Adhiraj (user_id=1) for October 2026
INSERT INTO budgets (user_id, category, budget_limit, month, year) VALUES
(1, 'Housing',       15000.00, 10, 2026),
(1, 'Groceries',      9000.00, 10, 2026),
(1, 'Transport',      3500.00, 10, 2026),
(1, 'Food',           4000.00, 10, 2026),
(1, 'Utilities',      2500.00, 10, 2026),
(1, 'Entertainment',  8000.00, 10, 2026);

