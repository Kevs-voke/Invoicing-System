ALTER TABLE invoice
ADD column amount_paid NUMERIC(10,2) DEFAULT 0.00;

CREATE TABLE IF NOT EXISTS payments(

)