describe('ขั้นตอนการล็อกอินของ Admin', () => {

  it('Admin ควรจะล็อกอินสำเร็จและถูกพาไปยังหน้า /admin', () => {
    // 1. เข้าไปที่หน้าแรก
    cy.visit('/');

    // 2. กรอกอีเมลและรหัสผ่านของ Admin
    cy.get('input#email').type('admin@admin.com');
    cy.get('input#password').type('ADMIN');

    // 3. กดปุ่มล็อกอิน
    cy.get('button[type="submit"]').click();

    // 4. ตรวจสอบว่า URL เปลี่ยนไปเป็น /admin
    cy.url({ timeout: 10000 }).should('include', '/admin');
  });
});
