-- V1: Initial schema for Turbo Premios
-- Creates all core tables: users, campaigns, purchases, tickets, affiliates, commissions, winners, withdraw_requests

CREATE TABLE IF NOT EXISTS users (
  id               VARCHAR(36)    PRIMARY KEY DEFAULT gen_random_uuid()::VARCHAR,
  name             VARCHAR(255)   NOT NULL,
  email            VARCHAR(255)   NOT NULL UNIQUE,
  phone            VARCHAR(20),
  password_hash    VARCHAR(255)   NOT NULL,
  role             VARCHAR(20)    NOT NULL DEFAULT 'user',
  affiliate_code   VARCHAR(20)    UNIQUE,
  balance          DECIMAL(10,2)  NOT NULL DEFAULT 0.00,
  total_commission DECIMAL(10,2)  NOT NULL DEFAULT 0.00,
  created_at       TIMESTAMP      NOT NULL DEFAULT NOW(),
  updated_at       TIMESTAMP      NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_affiliate_code ON users(affiliate_code);

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
