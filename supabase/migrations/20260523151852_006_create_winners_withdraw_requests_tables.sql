/*
  # Create winners and withdraw_requests tables

  1. New Tables
    - `winners` - records of raffle winners per campaign
      - Links to campaign, ticket, optionally user
      - Stores denormalized name/prize/campaign_title for display
    - `withdraw_requests` - affiliate withdrawal requests
      - Status: processing | completed | failed

  2. Security
    - RLS enabled
    - Winners are publicly readable
    - Withdraw requests only visible to owning affiliate
*/

CREATE TABLE IF NOT EXISTS winners (
  id             VARCHAR(36)   PRIMARY KEY DEFAULT gen_random_uuid()::VARCHAR,
  campaign_id    VARCHAR(36)   NOT NULL REFERENCES campaigns(id),
  ticket_id      VARCHAR(36)   NOT NULL REFERENCES tickets(id),
  user_id        VARCHAR(36)   REFERENCES users(id),
  name           VARCHAR(255)  NOT NULL,
  prize          VARCHAR(255)  NOT NULL,
  campaign_title VARCHAR(255)  NOT NULL,
  ticket_number  VARCHAR(5)    NOT NULL,
  photo_url      VARCHAR(500),
  draw_date      DATE          NOT NULL,
  created_at     TIMESTAMP     NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_winners_campaign_id ON winners(campaign_id);

ALTER TABLE winners ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Winners are publicly readable"
  ON winners FOR SELECT
  TO anon, authenticated
  USING (true);

CREATE POLICY "Service role insert winners"
  ON winners FOR INSERT
  TO service_role
  WITH CHECK (true);

-- Withdraw requests table
CREATE TABLE IF NOT EXISTS withdraw_requests (
  id           VARCHAR(36)   PRIMARY KEY DEFAULT gen_random_uuid()::VARCHAR,
  affiliate_id VARCHAR(36)   NOT NULL REFERENCES affiliates(id),
  amount       DECIMAL(10,2) NOT NULL,
  status       VARCHAR(20)   NOT NULL DEFAULT 'processing',
  pix_key      VARCHAR(255),
  processed_at TIMESTAMP,
  created_at   TIMESTAMP     NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_withdraw_requests_affiliate_id ON withdraw_requests(affiliate_id);

ALTER TABLE withdraw_requests ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Affiliates can read own withdraw requests"
  ON withdraw_requests FOR SELECT
  TO authenticated
  USING (
    EXISTS (
      SELECT 1 FROM affiliates
      WHERE affiliates.id = withdraw_requests.affiliate_id
      AND affiliates.user_id = auth.uid()::VARCHAR
    )
  );

CREATE POLICY "Service role full access to withdraw_requests"
  ON withdraw_requests FOR SELECT
  TO service_role
  USING (true);

CREATE POLICY "Service role insert withdraw_requests"
  ON withdraw_requests FOR INSERT
  TO service_role
  WITH CHECK (true);

CREATE POLICY "Service role update withdraw_requests"
  ON withdraw_requests FOR UPDATE
  TO service_role
  USING (true)
  WITH CHECK (true);
