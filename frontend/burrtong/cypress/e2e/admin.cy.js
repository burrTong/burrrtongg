describe('ขั้นตอนการล็อกอินของ Admin', () => {

  it('Admin ควรจะล็อกอินสำเร็จและถูกพาไปยังหน้า /admin/products', () => {
    // 1. เข้าไปที่หน้า Admin Login
    cy.visit('/admin/login');

    // 2. กรอกอีเมลและรหัสผ่านของ Admin
    cy.get('input#email').type('admin@admin.com');
    cy.get('input#password').type('admin');

    // 3. กดปุ่มล็อกอิน
    cy.get('button[type="submit"]').click();

    // 4. ตรวจสอบว่า URL เปลี่ยนไปเป็น /admin/products
    cy.url({ timeout: 10000 }).should('include', '/admin/products');
  });
});
