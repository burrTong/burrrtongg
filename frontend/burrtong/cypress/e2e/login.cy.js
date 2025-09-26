describe('Login Flow', () => {
  it('should allow a user to log in and redirect to the home page', () => {
    // 1. เข้าไปที่หน้าแรก
    cy.visit('/');

    // 2. กรอกอีเมลและรหัสผ่าน
    // หมายเหตุ: คุณอาจจะต้องเปลี่ยนค่า email/password ให้ตรงกับ user ที่มีอยู่จริงในฐานข้อมูลของคุณ
    cy.get('input#email').type('customer@customer.com');
    cy.get('input#password').type('CUSTOMER');

    // 3. กดปุ่มล็อกอิน
    cy.get('button[type="submit"]').click();

    // 4. ตรวจสอบว่า URL เปลี่ยนไปเป็น /home
    // เราใช้ { timeout: 10000 } เพื่อให้ Cypress รอสักครู่เผื่อ API ตอบสนองช้า
    cy.url({ timeout: 10000 }).should('include', '/home');

    // 5. (Optional) ตรวจสอบว่ามี element ที่บ่งบอกว่าล็อกอินสำเร็จ
    // เช่น หากมี Navbar ที่แสดงชื่อ user หรือปุ่ม Logout
    cy.get('nav').should('be.visible');
  });
});
