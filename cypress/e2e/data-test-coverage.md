# Data-Test Attribute Coverage for E-commerce Platform

This document outlines the `data-test` attributes implemented across the E-commerce Platform to support end-to-end testing, mapped to their corresponding User Stories.

## 📌 User Stories

### ฝั่งลูกค้า (Customer Side)

#### US-C01: ในฐานะลูกค้า ฉันต้องการสมัครสมาชิก/เข้าสู่ระบบด้วยบัญชีผู้ใช้
- `data-test="signup-username"`
- `data-test="signup-password"`
- `data-test="signup-submit"`
- `data-test="login-username"`
- `data-test="login-password"`
- `data-test="login-submit"`
- `data-test="user-avatar"`

#### US-C02: ในฐานะลูกค้า ฉันต้องการเรียกดูรองเท้าพร้อมรายละเอียด
- `data-test="product-card"`
- `data-test="product-name"`
- `data-test="product-price"`
- `data-test="product-image"`
- `data-test="product-detail-name"`
- `data-test="product-detail-price"`
- `data-test="product-detail-size"`
- `data-test="product-detail-stock"`
- `data-test="product-detail-image"`

#### US-C03: ในฐานะลูกค้า ฉันต้องการค้นหาและกรองรองเท้าตามประเภท
- `data-test="search-input"`
- `data-test="search-button"`
- `data-test="filter-category-all"`
- `data-test="filter-category-sneakers"`
- `data-test="filter-category-boots"`
- `data-test="filter-category-sandals"`
- `data-test="product-category"` (Used in test for assertion, not directly on component)

#### US-C04: ในฐานะลูกค้า ฉันต้องการเพิ่มรองเท้าใส่ตะกร้าและแก้ไขจำนวน/ไซส์
- `data-test="size-select"`
- `data-test="quantity-input"`
- `data-test="add-to-cart-button"`
- `data-test="cart-icon"`
- `data-test="cart-item-quantity"`
- `data-test="update-cart-button"`

#### US-C05: ในฐานะลูกค้า ฉันต้องการดูสรุปรายการสั่งซื้อก่อนยืนยันการชำระเงิน
- `data-test="cart-total"`
- `data-test="checkout-button"`
- `data-test="order-summary"`
- `data-test="summary-total"`

#### US-C06: ในฐานะลูกค้า ฉันต้องการดูรายละเอียดและสถานะคำสั่งซื้อ
- `data-test="order-item"`
- `data-test="order-id"`
- `data-test="order-status"`
- `data-test="order-detail-status"`

#### US-C07: ในฐานะลูกค้า ฉันต้องการติดต่อผู้ขายหรือแพลตฟอร์มผ่านหน้า Contact/Chat
- `data-test="contact-form"`
- `data-test="contact-subject"`
- `data-test="contact-message"`
- `data-test="contact-submit"`
- `data-test="contact-success-message"`

### ฝั่งผู้ขาย (Seller Side)

#### US-S01: ในฐานะผู้ขาย ฉันต้องการสมัครและสร้างร้านค้าของตัวเองบนแพลตฟอร์ม
- (No specific `data-test` attributes were implemented for this story in the provided code, as the test was skipped. Signup attributes would be similar to US-C01.)

#### US-S02: ในฐานะผู้ขาย ฉันต้องการเพิ่ม แก้ไข ลบ รายการรองเท้า
- `data-test="add-product-button"`
- `data-test="product-form-name"`
- `data-test="product-form-price"`
- `data-test="product-form-size"`
- `data-test="product-form-stock"`
- `data-test="product-form-save"`
- `data-test="product-list"`
- `data-test="product-row"`
- `data-test="edit-product-button"`
- `data-test="product-price-cell"`
- `data-test="delete-product-button"`
- `data-test="confirm-delete-button"`

#### US-S03: ในฐานะผู้ขาย ฉันต้องการดูออเดอร์ที่ลูกค้าสั่งจากร้านฉัน
- `data-test="order-row"`
- `data-test="order-id"`

#### US-S04: ในฐานะผู้ขาย ฉันต้องการอัปเดตสถานะคำสั่งซื้อ
- `data-test="order-status-select"`
- `data-test="update-status-button"`
- `data-test="order-status-display"`

#### US-S05: ในฐานะผู้ขาย ฉันต้องการดูสรุปยอดขายและสินค้าคงเหลือ
- `data-test="total-sales-widget"`
- `data-test="inventory-summary-widget"`
- `data-test="inventory-list"`

#### US-S06: ในฐานะผู้ขาย ฉันต้องการแก้ไขข้อมูลร้าน/โปรไฟล์
- `data-test="edit-profile-button"`
- `data-test="profile-store-name-input"`
- `data-test="save-profile-button"`
- `data-test="profile-store-name-display"`

### ฝั่งผู้ดูแลระบบ (Admin Side)

#### US-A01: ในฐานะแอดมิน ฉันต้องการดูแดชบอร์ดรวม
- `data-test="total-sellers-widget"`
- `data-test="total-customers-widget"`
- `data-test="total-sales-widget"`
- `data-test="top-products-widget"`

#### US-A02: ในฐานะแอดมิน ฉันต้องการอนุมัติ/ปฏิเสธการสมัครของผู้ขาย
- `data-test="application-row"`
- `data-test="approve-seller-button"`
- `data-test="reject-seller-button"`

#### US-A03: ในฐานะแอดมิน ฉันต้องการดูและจัดการข้อมูลสินค้าจากผู้ขาย
- `data-test="product-row"`
- `data-test="delete-product-button"`
- `data-test="confirm-delete-button"`

#### US-A04: ในฐานะแอดมิน ฉันต้องการดูรายการสั่งซื้อทั้งหมดของระบบ
- `data-test="order-row"`
- `data-test="order-id"`

#### US-A05: ในฐานะแอดมิน ฉันต้องการดูและจัดการผู้ใช้งาน
- `data-test="user-row"`
- `data-test="user-role"`
- `data-test="disable-user-button"`
- `data-test="user-status-display"`

#### US-A06: ในฐานะแอดมิน ฉันต้องการจัดการการแจ้งปัญหาหรือข้อร้องเรียน
- `data-test="ticket-row"`
- `data-test="ticket-details"`
- `data-test="ticket-reply-textarea"`
- `data-test="ticket-reply-button"`
- `data-test="reply-sent-confirmation"`
