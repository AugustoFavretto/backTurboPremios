/*
  # Seed sample campaigns for development/demo

  Inserts 3 sample campaigns so the frontend has data to display immediately.
  These can be safely removed in production.
*/

INSERT INTO campaigns (id, title, description, image_url, prize_value, ticket_price, total_tickets, sold_tickets, draw_date, status, prize, category, featured)
VALUES
(
  'cmp_001',
  'iPhone 16 Pro Max',
  'Concorra a um iPhone 16 Pro Max 256GB cor Titânio Natural. O aparelho mais avançado da Apple com chip A18 Pro, câmera de 48MP e tela Super Retina XDR.',
  'https://images.pexels.com/photos/788946/pexels-photo-788946.jpeg',
  9999.00,
  5.00,
  10000,
  7843,
  NOW() + INTERVAL '23 days',
  'active',
  'iPhone 16 Pro Max 256GB',
  'Eletrônicos',
  true
),
(
  'cmp_002',
  'PlayStation 5 + 5 Jogos',
  'PlayStation 5 edição padrão com leitor de disco, acompanha 5 jogos exclusivos: Spider-Man 2, God of War Ragnarök, Gran Turismo 7, Horizon e Demon''s Souls.',
  'https://images.pexels.com/photos/3945659/pexels-photo-3945659.jpeg',
  5499.00,
  3.00,
  8000,
  5240,
  NOW() + INTERVAL '18 days',
  'active',
  'PlayStation 5 + 5 Jogos',
  'Games',
  true
),
(
  'cmp_003',
  'Notebook Dell XPS 15',
  'Dell XPS 15 com Intel Core i7 13ª Geração, 32GB RAM DDR5, SSD NVMe 1TB, tela OLED 3.5K touch. O notebook ideal para criadores de conteúdo e profissionais.',
  'https://images.pexels.com/photos/18105/pexels-photo.jpg',
  12999.00,
  8.00,
  5000,
  1120,
  NOW() + INTERVAL '30 days',
  'active',
  'Dell XPS 15 i7 32GB 1TB',
  'Eletrônicos',
  false
)
ON CONFLICT (id) DO NOTHING;
