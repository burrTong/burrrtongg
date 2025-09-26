describe('ขั้นตอนการจัดการ Order (Admin)', () => {

  beforeEach(() => {
    // 1. (SETUP) สร้าง Order ใหม่ผ่าน API โดย Customer
    // คำสั่งนี้จะยิง API ไปสร้าง Order ที่มีสถานะเป็น PENDING รอไว้
    cy.createOrderViaApi();

    // 2. (LOGIN) ล็อกอินเข้าสู่ระบบด้วยบัญชี Admin ผ่าน UI
    cy.visit('/');
    cy.get('input#email').type('admin@admin.com');
    cy.get('input#password').type('ADMIN');
    cy.get('button[type="submit"]').click();
    cy.url({ timeout: 10000 }).should('include', '/admin');
  });

  it('ควรจะสามารถกด Accept Order และเปลี่ยนสถานะเป็น DELIVERED ได้', () => {
    // 3. (ACTION) เข้าไปที่หน้า Order List
    cy.get('a[href="/admin/orders"]').click();
    cy.url().should('include', '/admin/orders');

    // 4. (ACTION) ค้นหา Order แรกสุดที่มีสถานะ PENDING แล้วกดปุ่ม Accept
    // เราจะหาแถว (tr) ที่มีข้อความ 'PENDING' แล้วหาปุ่ม 'Accept' ที่อยู่ในแถวนั้น
    cy.contains('tr', 'PENDING', { timeout: 10000 })
      .first()
      .within(() => {
        cy.contains('button', 'Accept').click();
      });

    // 5. (VERIFY) ตรวจสอบว่าสถานะของ Order นั้นเปลี่ยนเป็น DELIVERED
    // เราจะหาแถวเดิม (ซึ่งตอนนี้ควรจะมีสถานะเป็น DELIVERED) และเช็คว่าปุ่ม Accept/Deny หายไปแล้ว
    cy.contains('tr', 'DELIVERED', { timeout: 10000 })
      .first()
      .within(() => {
        // ตรวจสอบว่าสถานะเปลี่ยนเป็น DELIVERED จริง
        cy.get('.status-delivered').should('be.visible');
        // และปุ่ม Accept ควรจะหายไป
        cy.contains('button', 'Accept').should('not.exist');
      });
  });
});
