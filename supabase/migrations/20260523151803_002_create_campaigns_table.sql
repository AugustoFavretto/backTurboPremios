/*
  # Create campaigns table

  1. New Tables
    - `campaigns`
      - `id` (varchar 36, primary key)
      - `title`, `description`, `image_url` - display info
      - `prize_value`, `ticket_price` - pricing
      - `total_tickets`, `sold_tickets` - ticket counts
      - `draw_date` - when the draw happens
      - `status` - active | ended | upcoming
      - `prize`, `category` - prize info
      - `featured` - boolean for homepage highlight
      - `winner_ticket` - filled after draw

  2. Security
    - RLS enabled
    - Public read access (campaigns are public)
    - Service role for writes
*/

CREATE TABLE IF NOT EXISTS campaigns (
  id            VARCHAR(36)    PRIMARY KEY DEFAULT gen_random_uuid()::VARCHAR,
  title         VARCHAR(255)   NOT NULL,
  description   TEXT,
  image_url     VARCHAR(500),
  prize_value   DECIMAL(10,2)  NOT NULL,
  ticket_price  DECIMAL(10,2)  NOT NULL,
  total_tickets INTEGER        NOT NULL,
  sold_tickets  INTEGER        NOT NULL DEFAULT 0,
  draw_date     TIMESTAMP      NOT NULL,
  status        VARCHAR(20)    NOT NULL DEFAULT 'upcoming',
  prize         VARCHAR(255)   NOT NULL,
  category      VARCHAR(100)   NOT NULL,
  featured      BOOLEAN        NOT NULL DEFAULT FALSE,
  winner_ticket VARCHAR(10),
  created_at    TIMESTAMP      NOT NULL DEFAULT NOW(),
  updated_at    TIMESTAMP      NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_campaigns_status ON campaigns(status);
CREATE INDEX IF NOT EXISTS idx_campaigns_featured ON campaigns(featured, status);

ALTER TABLE campaigns ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Campaigns are publicly readable"
  ON campaigns FOR SELECT
  TO anon, authenticated
  USING (true);

CREATE POLICY "Service role can insert campaigns"
  ON campaigns FOR INSERT
  TO service_role
  WITH CHECK (true);

CREATE POLICY "Service role can update campaigns"
  ON campaigns FOR UPDATE
  TO service_role
  USING (true)
  WITH CHECK (true);
