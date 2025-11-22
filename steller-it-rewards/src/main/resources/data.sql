-- Seed data for testing the rewards service
-- This file will be executed automatically on application startup

-- Insert Customers
INSERT INTO customers (first_name, last_name, email, created_at) VALUES
('John', 'Doe', 'john.doe@example.com', '2024-01-15 10:00:00'),
('Jane', 'Smith', 'jane.smith@example.com', '2024-01-16 11:00:00'),
('Bob', 'Johnson', 'bob.johnson@example.com', '2024-01-17 12:00:00'),
('Alice', 'Williams', 'alice.williams@example.com', '2024-01-18 13:00:00'),
('Charlie', 'Brown', 'charlie.brown@example.com', '2024-01-19 14:00:00')
ON CONFLICT DO NOTHING;

-- Insert Transactions for John Doe (Customer ID: 1)
-- January 2024 transactions
INSERT INTO transactions (customer_id, amount, transaction_date, description) VALUES
(1, 120.00, '2024-01-05 09:30:00', 'Grocery shopping'),
(1, 75.50, '2024-01-12 14:20:00', 'Electronics purchase'),
(1, 200.00, '2024-01-20 16:45:00', 'Furniture purchase'),
(1, 45.00, '2024-01-25 10:15:00', 'Gas station'),
(1, 150.00, '2024-01-28 11:30:00', 'Clothing store')
ON CONFLICT DO NOTHING;

-- February 2024 transactions
INSERT INTO transactions (customer_id, amount, transaction_date, description) VALUES
(1, 90.00, '2024-02-03 08:45:00', 'Restaurant'),
(1, 110.00, '2024-02-10 15:20:00', 'Home improvement'),
(1, 60.00, '2024-02-15 12:00:00', 'Pharmacy'),
(1, 180.00, '2024-02-22 17:30:00', 'Electronics'),
(1, 95.00, '2024-02-25 09:15:00', 'Grocery shopping')
ON CONFLICT DO NOTHING;

-- March 2024 transactions
INSERT INTO transactions (customer_id, amount, transaction_date, description) VALUES
(1, 130.00, '2024-03-05 10:00:00', 'Shopping mall'),
(1, 85.00, '2024-03-12 14:30:00', 'Bookstore'),
(1, 220.00, '2024-03-18 16:00:00', 'Furniture'),
(1, 55.00, '2024-03-22 11:45:00', 'Coffee shop'),
(1, 105.00, '2024-03-28 13:20:00', 'Department store')
ON CONFLICT DO NOTHING;

-- Insert Transactions for Jane Smith (Customer ID: 2)
-- January 2024 transactions
INSERT INTO transactions (customer_id, amount, transaction_date, description) VALUES
(2, 80.00, '2024-01-08 10:30:00', 'Clothing'),
(2, 140.00, '2024-01-15 15:00:00', 'Electronics'),
(2, 50.00, '2024-01-22 09:00:00', 'Grocery'),
(2, 95.00, '2024-01-28 14:15:00', 'Restaurant')
ON CONFLICT DO NOTHING;

-- February 2024 transactions
INSERT INTO transactions (customer_id, amount, transaction_date, description) VALUES
(2, 160.00, '2024-02-05 11:00:00', 'Furniture'),
(2, 70.00, '2024-02-12 13:30:00', 'Pharmacy'),
(2, 125.00, '2024-02-18 16:45:00', 'Shopping'),
(2, 100.00, '2024-02-25 10:20:00', 'Electronics')
ON CONFLICT DO NOTHING;

-- March 2024 transactions
INSERT INTO transactions (customer_id, amount, transaction_date, description) VALUES
(2, 90.00, '2024-03-03 09:15:00', 'Grocery'),
(2, 175.00, '2024-03-10 15:30:00', 'Home goods'),
(2, 65.00, '2024-03-17 12:00:00', 'Restaurant'),
(2, 115.00, '2024-03-24 14:00:00', 'Clothing')
ON CONFLICT DO NOTHING;

-- Insert Transactions for Bob Johnson (Customer ID: 3)
-- January 2024 transactions
INSERT INTO transactions (customer_id, amount, transaction_date, description) VALUES
(3, 110.00, '2024-01-10 08:00:00', 'Electronics'),
(3, 45.00, '2024-01-18 12:30:00', 'Gas'),
(3, 190.00, '2024-01-25 17:00:00', 'Furniture')
ON CONFLICT DO NOTHING;

-- February 2024 transactions
INSERT INTO transactions (customer_id, amount, transaction_date, description) VALUES
(3, 85.00, '2024-02-08 10:45:00', 'Grocery'),
(3, 135.00, '2024-02-15 14:20:00', 'Shopping'),
(3, 70.00, '2024-02-22 11:15:00', 'Restaurant')
ON CONFLICT DO NOTHING;

-- March 2024 transactions
INSERT INTO transactions (customer_id, amount, transaction_date, description) VALUES
(3, 155.00, '2024-03-07 09:30:00', 'Electronics'),
(3, 100.00, '2024-03-14 13:00:00', 'Clothing'),
(3, 60.00, '2024-03-21 15:45:00', 'Pharmacy')
ON CONFLICT DO NOTHING;

-- Insert Transactions for Alice Williams (Customer ID: 4)
-- January 2024 transactions
INSERT INTO transactions (customer_id, amount, transaction_date, description) VALUES
(4, 95.00, '2024-01-12 10:00:00', 'Shopping'),
(4, 120.00, '2024-01-20 14:30:00', 'Electronics'),
(4, 50.00, '2024-01-27 11:00:00', 'Grocery')
ON CONFLICT DO NOTHING;

-- February 2024 transactions
INSERT INTO transactions (customer_id, amount, transaction_date, description) VALUES
(4, 145.00, '2024-02-10 09:15:00', 'Furniture'),
(4, 80.00, '2024-02-17 13:45:00', 'Restaurant'),
(4, 110.00, '2024-02-24 16:00:00', 'Clothing')
ON CONFLICT DO NOTHING;

-- March 2024 transactions
INSERT INTO transactions (customer_id, amount, transaction_date, description) VALUES
(4, 75.00, '2024-03-05 10:30:00', 'Grocery'),
(4, 165.00, '2024-03-12 15:00:00', 'Electronics'),
(4, 90.00, '2024-03-19 12:15:00', 'Shopping')
ON CONFLICT DO NOTHING;

-- Insert Transactions for Charlie Brown (Customer ID: 5)
-- January 2024 transactions
INSERT INTO transactions (customer_id, amount, transaction_date, description) VALUES
(5, 105.00, '2024-01-14 08:30:00', 'Electronics'),
(5, 55.00, '2024-01-21 12:00:00', 'Grocery'),
(5, 130.00, '2024-01-28 15:30:00', 'Furniture')
ON CONFLICT DO NOTHING;

-- February 2024 transactions
INSERT INTO transactions (customer_id, amount, transaction_date, description) VALUES
(5, 70.00, '2024-02-11 09:45:00', 'Restaurant'),
(5, 150.00, '2024-02-18 14:00:00', 'Shopping'),
(5, 65.00, '2024-02-25 11:30:00', 'Pharmacy')
ON CONFLICT DO NOTHING;

-- March 2024 transactions
INSERT INTO transactions (customer_id, amount, transaction_date, description) VALUES
(5, 115.00, '2024-03-08 10:00:00', 'Clothing'),
(5, 85.00, '2024-03-15 13:15:00', 'Grocery'),
(5, 140.00, '2024-03-22 16:45:00', 'Electronics')
ON CONFLICT DO NOTHING;

