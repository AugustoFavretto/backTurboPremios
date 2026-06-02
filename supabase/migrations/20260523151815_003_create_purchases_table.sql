/*
  # Create purchases table

  1. New Tables
    - `purchases` - tracks all ticket purchase transactions
      - Supports anonymous purchases (user_id nullable)
      - Stores PIX payment data
      - Links to campaigns and optionally to users
      - Tracks affiliate code for commission

  2. Security
    - RLS enabled
    - Authenticated users can read their own purchases
    - Anonymous purchases readable by service role
*/

CREATE TABLE IF NOT EXISTS purchases (
  id             VARCHAR(36)   PRIMARY KEY DEFAULT gen_random_uuid()::VARCHAR,
  campaign_id    VARCHAR(36)   NOT NULL REFERENCES campaigns(id),
  user_id        VARCHAR(36)   REFERENCES users(id),
  user_phone     VARCHAR(20),
  user_email     VARCHAR(255),
  user_name      VARCHAR(255),
  affiliate_code VARCHAR(20),
  quantity       INTEGER       NOT NULL,
  total          DECIMAL(10,2) NOT NULL,
  payment_method VARCHAR(20)   NOT NULL DEFAULT 'pix',
  payment_status VARCHAR(20)   NOT NULL DEFAULT 'pending',
  pix_code       TEXT,
  pix_qr_code    TEXT,
  pix_expires_at TIMESTAMP,
  paid_at        TIMESTAMP,
  created_at     TIMESTAMP     NOT NULL DEFAULT NOW(),
  updated_at     TIMESTAMP     NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_purchases_user_id ON purchases(user_id);
CREATE INDEX IF NOT EXISTS idx_purchases_campaign_id ON purchases(campaign_id);
CREATE INDEX IF NOT EXISTS idx_purchases_payment_status ON purchases(payment_status);
CREATE INDEX IF NOT EXISTS idx_purchases_user_phone ON purchases(user_phone);
CREATE INDEX IF NOT EXISTS idx_purchases_user_email ON purchases(user_email);

ALTER TABLE purchases ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Users can read own purchases"
  ON purchases FOR SELECT
  TO authenticated
  USING (user_id = auth.uid()::VARCHAR);

CREATE POLICY "Service role full access to purchases"
  ON purchases FOR SELECT
  TO service_role
  USING (true);

CREATE POLICY "Service role insert purchases"
  ON purchases FOR INSERT
  TO service_role
  WITH CHECK (true);

CREATE POLICY "Service role update purchases"
  ON purchases FOR UPDATE
  TO service_role
  USING (true)
  WITH CHECK (true);
