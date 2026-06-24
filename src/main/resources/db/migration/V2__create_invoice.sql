
CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE SEQUENCE IF NOT EXISTS inv_no_seq
START WITH 100000;

CREATE TABLE  IF NOT EXISTS invoice(
   id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    invoice_no BIGINT UNIQUE NOT NULL DEFAULT nextval('inv_no_seq'),
    status VARCHAR(30) NOT NULL DEFAULT 'DRAFT',
    created_at TIMESTAMP DEFAULT NOW(),
    due_date TIMESTAMP,
    total_tax NUMERIC(10,2),
    total NUMERIC(10,2),
    cust_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE
    );

CREATE TABLE  IF NOT EXISTS invoice_items(
     id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    invoice_id UUID NOT NULL REFERENCES invoice(id) ON DELETE CASCADE,
    item_name VARCHAR(50) NOT NULL,
    unit_price NUMERIC(10,2) NOT NULL,
    quantity NUMERIC NOT NULL,
    tax NUMERIC(10,2) NOT NULL DEFAULT 0,
    tax_subtotal NUMERIC(10,2),
    sub_total NUMERIC(10,2)
    );




