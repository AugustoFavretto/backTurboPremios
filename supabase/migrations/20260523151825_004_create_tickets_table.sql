/*
  # Create tickets table

  1. New Tables
    - `tickets` - individual raffle tickets
      - Unique number per campaign (00000-99999)
      - Can be anonymous (user_id nullable)
      - Status: active | winner | expired

  2. Constraints
    - UNIQUE on (number, campaign_id) to prevent duplicate ticket numbers per campaign

  3. Security
    - RLS enabled
    - Authenticated users read own tickets
    - Anonymous ticket lookup via phone/email handled by service role
*/

CREATE TABLE IF NOT EXISTS tickets (
  id           VARCHAR(36)   PRIMARY KEY DEFAULT gen_random_uuid()::VARCHAR,
  number       VARCHAR(5)    NOT NULL,
  campaign_id  VARCHAR(36)   NOT NULL REFERENCES campaigns(id),
  purchase_id  VARCHAR(36)   NOT NULL REFERENCES purchases(id),
  user_id      VARCHAR(36)   REFERENCES users(id),
  user_phone   VARCHAR(20),
  user_email   VARCHAR(255),
  status       VARCHAR(20)   NOT NULL DEFAULT 'active',
  price        DECIMAL(10,2) NOT NULL,
  purchased_at TIMESTAMP     NOT NULL DEFAULT NOW(),
  created_at   TIMESTAMP     NOT NULL DEFAULT NOW()
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_tickets_number_campaign ON tickets(number, campaign_id);
CREATE INDEX IF NOT EXISTS idx_tickets_user_id ON tickets(user_id);
CREATE INDEX IF NOT EXISTS idx_tickets_campaign_id ON tickets(campaign_id);
CREATE INDEX IF NOT EXISTS idx_tickets_user_phone ON tickets(user_phone);
CREATE INDEX IF NOT EXISTS idx_tickets_user_email ON tickets(user_email);
CREATE INDEX IF NOT EXISTS idx_tickets_purchase_id ON tickets(purchase_id);

ALTER TABLE tickets ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Users can read own tickets"
  ON tickets FOR SELECT
  TO authenticated
  USING (user_id = auth.uid()::VARCHAR);

CREATE POLICY "Service role full access to tickets"
  ON tickets FOR SELECT
  TO service_role
  USING (true);

CREATE POLICY "Service role insert tickets"
  ON tickets FOR INSERT
  TO service_role
  WITH CHECK (true);

CREATE POLICY "Service role update tickets"
  ON tickets FOR UPDATE
  TO service_role
  USING (true)
  WITH CHECK (true);
