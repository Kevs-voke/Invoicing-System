CREATE EXTENSION IF NOT EXISTS pgcrypto;

ALTER TABLE invoice
ADD column amount_paid NUMERIC(10,2) DEFAULT 0.00;

CREATE TABLE IF NOT EXISTS payments(
    invoice_id UUID NOT NULL,
    customer_id UUID NOT NULL,
    amount NUMERIC(10,2) NOT NULL,
    transaction_ref VARCHAR(50),
    payment_method VARCHAR(30),
    payment_at TIMESTAMP,

    PRIMARY KEY(invoice_id, customer_id),
    FOREIGN KEY (customer_id) REFERENCES users(id),
    FOREIGN KEY (invoice_id) REFERENCES invoice(id)


)