ALTER TABLE payments
    ADD COLUMN id UUID DEFAULT gen_random_uuid() NOT NULL;

ALTER TABLE payments
DROP CONSTRAINT IF EXISTS payments_pkey;

ALTER TABLE payments
    ADD PRIMARY KEY (id);

CREATE INDEX IF NOT EXISTS idx_payments_invoice ON payments(invoice_id);
CREATE INDEX IF NOT EXISTS idx_payments_customer ON payments(customer_id);
CREATE INDEX IF NOT EXISTS idx_payments_date ON payments(payment_at);


ALTER TABLE payments
    ADD CONSTRAINT uq_payments_transaction_ref UNIQUE (transaction_ref);

ALTER TABLE payments
    ADD COLUMN IF NOT EXISTS notes TEXT;