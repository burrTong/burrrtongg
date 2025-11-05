
CREATE TABLE coupons (
    id SERIAL PRIMARY KEY,
    code VARCHAR(255) UNIQUE NOT NULL,
    discount_type VARCHAR(50) NOT NULL, -- e.g., 'FIXED' or 'PERCENTAGE'
    discount_value DECIMAL(10, 2) NOT NULL,
    expiration_date TIMESTAMPTZ,
    max_uses INT,
    times_used INT DEFAULT 0,
    min_purchase_amount DECIMAL(10, 2),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

ALTER TABLE orders
ADD COLUMN coupon_id BIGINT,
ADD CONSTRAINT fk_coupon
    FOREIGN KEY(coupon_id) 
    REFERENCES coupons(id);
