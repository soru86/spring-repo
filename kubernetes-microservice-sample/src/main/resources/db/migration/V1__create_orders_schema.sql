CREATE SCHEMA IF NOT EXISTS orderservice;

CREATE TABLE IF NOT EXISTS orderservice.orders (
    id UUID PRIMARY KEY,
    customer_name VARCHAR(255) NOT NULL,
    customer_email VARCHAR(255) NOT NULL,
    order_date TIMESTAMP WITH TIME ZONE NOT NULL,
    total_amount NUMERIC(19, 2) NOT NULL,
    status VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS orderservice.order_items (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL REFERENCES orderservice.orders(id) ON DELETE CASCADE,
    sku VARCHAR(255) NOT NULL,
    quantity INTEGER NOT NULL,
    unit_price NUMERIC(19, 2) NOT NULL,
    line_amount NUMERIC(19, 2) NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_order_items_order_id ON orderservice.order_items(order_id);

