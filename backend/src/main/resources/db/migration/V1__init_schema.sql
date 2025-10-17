-- V1: create tables (minimal schema aligns with app entities)

CREATE TABLE IF NOT EXISTS categories (
  id bigint PRIMARY KEY,
  name varchar(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS users (
  user_id bigint PRIMARY KEY,
  password varchar(255) NOT NULL,
  role varchar(255) NOT NULL,
  username varchar(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS products (
  id bigint PRIMARY KEY,
  description varchar(255),
  image_url varchar(255),
  name varchar(255) NOT NULL,
  price double precision,
  size varchar(255),
  stock integer,
  category_id bigint,
  seller_id bigint NOT NULL
);

CREATE TABLE IF NOT EXISTS orders (
  id bigint PRIMARY KEY,
  order_date timestamp without time zone NOT NULL,
  status varchar(255) NOT NULL,
  total_price double precision,
  customer_id bigint NOT NULL
);

CREATE TABLE IF NOT EXISTS order_items (
  id bigint PRIMARY KEY,
  price double precision,
  quantity integer NOT NULL,
  order_id bigint NOT NULL,
  product_id bigint NOT NULL
);

CREATE TABLE IF NOT EXISTS payments (
  id bigint PRIMARY KEY,
  amount double precision,
  payment_date timestamp without time zone,
  status varchar(255),
  order_id bigint NOT NULL
);
