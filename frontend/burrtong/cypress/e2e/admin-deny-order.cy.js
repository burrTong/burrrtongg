describe('ขั้นตอนการจัดการ Order (Admin)', () => {

  beforeEach(() => {
    // 1. (SETUP) สร้าง Order ใหม่ผ่าน API โดย Customer
    // คำสั่งนี้จะยิง API ไปสร้าง Order ที่มีสถานะเป็น PENDING รอไว้
    // cy.createOrderViaApi();

    // 2. (LOGIN) ล็อกอินเข้าสู่ระบบด้วยบัญชี Admin ผ่าน UI
    cy.visit('/admin/login');
    cy.get('input#email').type('admin@admin.com');
    cy.get('input#password').type('admin');
    cy.get('button[type="submit"]').click();
    cy.url({ timeout: 10000 }).should('include', '/admin/products');
  });

  it('ควรจะสามารถกด Deny Order และเปลี่ยนสถานะเป็น CANCELED ได้', () => {
    // 3. (ACTION) เข้าไปที่หน้า Order List
    cy.get('a[href="/admin/orders"]').click();
    cy.url().should('include', '/admin/orders');

    // 4. (ACTION) ค้นหา Order แรกสุดที่มีสถานะ PENDING แล้วกดปุ่ม Deny
    // เราจะหาแถว (tr) ที่มีข้อความ 'PENDING' แล้วหาปุ่ม 'Deny' ที่อยู่ในแถวนั้น
    cy.contains('tr', 'PENDING', { timeout: 10000 })
      .first()
      .within(() => {
        cy.contains('button', 'Deny').click();
      });

    // 5. (VERIFY) ตรวจสอบว่าสถานะของ Order นั้นเปลี่ยนเป็น CANCELED
    // เราจะหาแถวเดิม (ซึ่งตอนนี้ควรจะมีสถานะเป็น CANCELED) และเช็คว่าปุ่ม Accept/Deny หายไปแล้ว
    cy.contains('tr', 'CANCELED', { timeout: 10000 })
      .first()
      .within(() => {
        // ตรวจสอบว่าสถานะเปลี่ยนเป็น CANCELED จริง
        cy.get('.status-canceled').should('be.visible');
        // และปุ่ม Deny ควรจะหายไป
        cy.contains('button', 'Deny').should('not.exist');
      });
  });
});
