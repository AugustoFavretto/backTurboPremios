ALTER TABLE purchases
ADD COLUMN gateway_payment_id VARCHAR(255);

CREATE INDEX idx_purchases_gateway_payment_id
ON purchases(gateway_payment_id);