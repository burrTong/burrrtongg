-- V2: seed data (use insert ... on conflict where appropriate)

INSERT INTO categories (id, name)
VALUES (1, 'Electronics')
ON CONFLICT (id) DO NOTHING;

INSERT INTO users (user_id, password, role, username)
VALUES
  (34, '$2a$10$CxTNXiDZq81M50wtuG6XcucBtuE7PpPweCatB9/yLVXSOooCaYFrW', 'ADMIN', 'admin@admin.com')
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO products (id, description, image_url, name, price, size, stock, category_id, seller_id)
VALUES
  (107, 'Imported product 1', '/uploads/images/6b198341-b558-4561-b2ce-c90ed7d64ba8_images.jpg', 'Product 107', 111111, '42', 11111110, 1, 34),
  (108, 'Imported product 2', '/uploads/images/dddcc039-9556-4938-b15f-08220eaaf14d_png.png', 'Product 108', 2222, '39', 22205, 1, 34)
ON CONFLICT (id) DO NOTHING;
