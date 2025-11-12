-- V2: seed data (use insert ... on conflict where appropriate)

INSERT INTO categories (id, name)
VALUES (1, 'Nike'), (2, 'Puma')
ON CONFLICT (id) DO NOTHING;

INSERT INTO users (user_id, password, role, username)
VALUES
  (1, '$2a$10$XO.jCOhHz/jxwfLbK/wFZevv675r6CCPOfRY2S4A8Otrjbp9XcFLO', 'CUSTOMER', 'customer@customer.com'),
  (2, '$2a$10$CxTNXiDZq81M50wtuG6XcucBtuE7PpPweCatB9/yLVXSOooCaYFrW', 'ADMIN', 'admin@admin.com')
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO products (id, description, image_url, name, price, size, stock, category_id, seller_id)
VALUES
  (1, '', '/uploads/images/nike-air-force1.png', 'Nike Air Force1', 3500, '42', 5, 1, 2),
  (2, '', '/uploads/images/nike-shoe-1998-limited.jpg', 'Nike Shoe 1998 Limited Edition', 2000, '42', 10, 1, 2),
  (3, '', '/uploads/images/puma-shoe-alpha.png', 'Puma Shoe Alpha', 1800, '39', 19, 2, 2),
  (4, '', '/uploads/images/puma-x-travis.jpg', 'Puma X Travis', 1800, '39', 18, 2, 2)
ON CONFLICT (id) DO NOTHING;

-- Update sequences to match the inserted data
SELECT setval('categories_id_seq', (SELECT MAX(id) FROM categories));
SELECT setval('users_user_id_seq', (SELECT MAX(user_id) FROM users));
SELECT setval('products_id_seq', (SELECT MAX(id) FROM products));
