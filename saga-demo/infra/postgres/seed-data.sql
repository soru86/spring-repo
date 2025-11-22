-- Create logical databases (executed by superuser)
CREATE DATABASE saga_order;
CREATE DATABASE saga_inventory;
CREATE DATABASE saga_payment;
CREATE DATABASE saga_auth;

\\connect saga_inventory
CREATE TABLE IF NOT EXISTS inventory_items (
    id SERIAL PRIMARY KEY,
    sku VARCHAR(50) UNIQUE NOT NULL,
    available_quantity INT NOT NULL
);
INSERT INTO inventory_items (sku, available_quantity)
VALUES ('SKU-BOOK-001', 50),
       ('SKU-HEADSET-002', 30),
       ('SKU-LAPTOP-003', 10)
ON CONFLICT (sku) DO UPDATE SET available_quantity = EXCLUDED.available_quantity;

\\connect saga_auth
CREATE TABLE IF NOT EXISTS user_accounts (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(200) NOT NULL
);
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT REFERENCES user_accounts(id),
    role VARCHAR(50) NOT NULL
);

