/*
  # Create affiliates and commissions tables

  1. New Tables
    - `affiliates` - affiliate profile linked 1:1 to users
      - Tracks clicks, sales, revenue, commissions
      - conversion_rate = (total_sales / total_clicks) * 100
    - `commissions` - individual commission records per purchase
      - Status: pending | approved | paid
      - amount = purchase.total * (rate / 100)

  2. Security
    - RLS enabled
    - Affiliates can read their own data
    - Service role for writes
*/

CREATE TABLE IF NOT EXISTS affiliates (
  id                 VARCHAR(36)   PRIMARY KEY DEFAULT gen_random_uuid()::VARCHAR,
  user_id            VARCHAR(36)   NOT NULL UNIQUE REFERENCES users(id),
  code               VARCHAR(20)   NOT NULL UNIQUE,
  referral_link      VARCHAR(500)  NOT NULL,
  total_clicks       INTEGER       NOT NULL DEFAULT 0,
  total_sales        INTEGER       NOT NULL DEFAULT 0,
  total_revenue      DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  pending_commission DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  paid_commission    DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  conversion_rate    DECIMAL(5,2)  NOT NULL DEFAULT 0.00,
  created_at         TIMESTAMP     NOT NULL DEFAULT NOW(),
  updated_at         TIMESTAMP     NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_affiliates_code ON affiliates(code);
CREATE INDEX IF NOT EXISTS idx_affiliates_user_id ON affiliates(user_id);

ALTER TABLE affiliates ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Affiliates can read own profile"
  ON affiliates FOR SELECT
  TO authenticated
  USING (user_id = auth.uid()::VARCHAR);

CREATE POLICY "Service role full access to affiliates"
  ON affiliates FOR SELECT
  TO service_role
  USING (true);

CREATE POLICY "Service role insert affiliates"
  ON affiliates FOR INSERT
  TO service_role
  WITH CHECK (true);

CREATE POLICY "Service role update affiliates"
  ON affiliates FOR UPDATE
  TO service_role
  USING (true)
  WITH CHECK (true);

-- Commissions table
CREATE TABLE IF NOT EXISTS commissions (
  id           VARCHAR(36)   PRIMARY KEY DEFAULT gen_random_uuid()::VARCHAR,
  affiliate_id VARCHAR(36)   NOT NULL REFERENCES affiliates(id),
  purchase_id  VARCHAR(36)   NOT NULL REFERENCES purchases(id),
  amount       DECIMAL(10,2) NOT NULL,
  rate         DECIMAL(5,2)  NOT NULL DEFAULT 10.00,
  status       VARCHAR(20)   NOT NULL DEFAULT 'pending',
  buyer_name   VARCHAR(255),
  created_at   TIMESTAMP     NOT NULL DEFAULT NOW(),
  updated_at   TIMESTAMP     NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_commissions_affiliate_id ON commissions(affiliate_id);
CREATE INDEX IF NOT EXISTS idx_commissions_status ON commissions(status);

ALTER TABLE commissions ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Affiliates can read own commissions"
  ON commissions FOR SELECT
  TO authenticated
  USING (
    EXISTS (
      SELECT 1 FROM affiliates
      WHERE affiliates.id = commissions.affiliate_id
      AND affiliates.user_id = auth.uid()::VARCHAR
    )
  );

CREATE POLICY "Service role full access to commissions"
  ON commissions FOR SELECT
  TO service_role
  USING (true);

CREATE POLICY "Service role insert commissions"
  ON commissions FOR INSERT
  TO service_role
  WITH CHECK (true);

CREATE POLICY "Service role update commissions"
  ON commissions FOR UPDATE
  TO service_role
  USING (true)
  WITH CHECK (true);
