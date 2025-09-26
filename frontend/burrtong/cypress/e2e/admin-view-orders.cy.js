describe('ขั้นตอนการดูรายการ Order (Admin)', () => {

  beforeEach(() => {
    // ก่อนการเทสแต่ละครั้ง ให้ทำการล็อกอินเข้าระบบด้วยบัญชี Admin
    cy.visit('/');
    cy.get('input#email').type('admin@admin.com');
    cy.get('input#password').type('ADMIN');
    cy.get('button[type="submit"]').click();
    cy.url({ timeout: 10000 }).should('include', '/admin');
  });

  it('ควรจะสามารถเข้าไปที่หน้ารายการ Order ได้สำเร็จ', () => {
    // 1. คลิกที่ Link "Orders’ List" ใน Sidebar
    // เราใช้ { force: true } ในกรณีที่อาจมี element อื่นบัง แต่ไม่จำเป็นเสมอไป
    cy.get('a[href="/admin/orders"]').click({ force: true });

    // 2. ตรวจสอบว่า URL เปลี่ยนไปเป็น /admin/orders
    cy.url().should('include', '/admin/orders');

    // 3. ตรวจสอบว่าหัวข้อ "Orders’ List" แสดงผลอย่างถูกต้อง
    cy.contains('h1', 'Orders’ List').should('be.visible');

    // 4. (Optional) ตรวจสอบว่าตารางแสดงรายการ Order แสดงขึ้นมา
    cy.get('.product-table').should('be.visible');
  });
});
