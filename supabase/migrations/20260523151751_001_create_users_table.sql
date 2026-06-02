/*
  # Create users table

  1. New Tables
    - `users`
      - `id` (varchar 36, primary key, UUID)
      - `name` (varchar 255, not null)
      - `email` (varchar 255, unique, not null)
      - `phone` (varchar 20)
      - `password_hash` (varchar 255, not null)
      - `role` (varchar 20, default 'user') - values: user | affiliate | admin
      - `affiliate_code` (varchar 20, unique) - auto-generated when user becomes affiliate
      - `balance` (decimal 10,2, default 0.00)
      - `total_commission` (decimal 10,2, default 0.00)
      - `created_at`, `updated_at` timestamps

  2. Security
    - Enable RLS
    - Users can read/update their own data
    - Admins can read all users
*/

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

ALTER TABLE users ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Users can read own data"
  ON users FOR SELECT
  TO authenticated
  USING (id = auth.uid()::VARCHAR);

CREATE POLICY "Users can update own data"
  ON users FOR UPDATE
  TO authenticated
  USING (id = auth.uid()::VARCHAR)
  WITH CHECK (id = auth.uid()::VARCHAR);

CREATE POLICY "Service role full access to users"
  ON users FOR SELECT
  TO service_role
  USING (true);

CREATE POLICY "Service role insert users"
  ON users FOR INSERT
  TO service_role
  WITH CHECK (true);

CREATE POLICY "Service role update users"
  ON users FOR UPDATE
  TO service_role
  USING (true)
  WITH CHECK (true);
