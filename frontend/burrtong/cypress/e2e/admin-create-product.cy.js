describe('ขั้นตอนการสร้างสินค้าใหม่ (Admin)', () => {

  beforeEach(() => {
    // ก่อนการเทสแต่ละครั้ง ให้ทำการล็อกอินเข้าระบบด้วยบัญชี Admin
    cy.visit('/');
    cy.get('input#email').type('admin@admin.com');
    cy.get('input#password').type('ADMIN');
    cy.get('button[type="submit"]').click();
    cy.url({ timeout: 10000 }).should('include', '/admin');
  });

  it('ควรจะสามารถสร้างสินค้าใหม่ได้สำเร็จ', () => {
    // สร้างชื่อสินค้าที่ไม่ซ้ำกันในแต่ละครั้งที่รันเทส
    const uniqueProductName = `Cypress Test Product ${Date.now()}`;

    // 1. คลิกปุ่ม "+ New Product"
    cy.get('.new-product-btn').click();

    // 2. ตรวจสอบว่า Modal เปิดขึ้นมา
    cy.get('.modal-overlay').should('be.visible');

    // 3. กรอกข้อมูลสินค้าใหม่ในฟอร์ม
    cy.get('input#name').type(uniqueProductName);
    cy.get('textarea#description').type('This is a product created by a Cypress test.');
    cy.get('input#price').type('999');
    cy.get('input#stock').type('100');

    // 4. คลิกปุ่ม "Create Product"
    cy.get('.modal-submit-btn').click();

    // 5. ตรวจสอบว่า Modal ปิดไปแล้ว
    cy.get('.modal-overlay').should('not.exist');

    // 6. ตรวจสอบว่าสินค้าใหม่ที่เราเพิ่งสร้าง แสดงขึ้นมาเป็นแถวแรกในตาราง
    // ใช้ timeout เพื่อรอให้ UI อัปเดตหลังจากสร้างเสร็จ
    cy.get('.product-table tbody tr', { timeout: 10000 })
      .first()
      .should('contain', uniqueProductName);
  });
});
