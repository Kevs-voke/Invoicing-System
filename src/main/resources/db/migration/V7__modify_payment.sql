ALTER TABLE payments
ADD COLUMN IF NOT EXISTS status VARCHAR(20) NOT NULL DEFAULT 'pending';
ALTER TABLE payments
ADD CONSTRAINT chk_payments_status CHECK (
        LOWER(status) IN ('pending', 'confirmed', 'failed')
    );
CREATE INDEX IF NOT EXISTS idx_payments_status ON payments(status);
CREATE SEQUENCE IF NOT EXISTS payment_no_seq START WITH 100000;
ALTER TABLE payments
ADD COLUMN IF NOT EXISTS payment_no BIGINT UNIQUE NOT NULL DEFAULT nextval('payment_no_seq');
ALTER SEQUENCE payment_no_seq OWNED BY payments.payment_no;