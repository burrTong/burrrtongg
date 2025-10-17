describe('ขั้นตอนการสร้างสินค้าใหม่ (Admin)', () => {

  beforeEach(() => {
    // ล็อกอินก่อนเทสแต่ละครั้ง
    cy.visit('/admin/login');
    cy.get('input#email').type('admin@admin.com');
    cy.get('input#password').type('admin');
    cy.get('button[type="submit"]').click();
    cy.url({ timeout: 10000 }).should('include', '/admin/products');
  });

  it('ควรจะสามารถสร้างสินค้าใหม่ได้สำเร็จ', () => {
    const uniqueProductName = `Cypress Test Product ${Date.now()}`;

    // 1. เปิดโมดัล
    cy.get('.new-product-btn').click();
    cy.get('.modal-overlay').should('be.visible');

    // 2. กรอกข้อมูลฟอร์ม
    cy.get('input#name').type(uniqueProductName);
    cy.get('textarea#description').type('This is a product created by a Cypress test.');
    cy.get('input#price').type('999');
    cy.get('input#stock').type('100');
    cy.get('input#size').type('49'); // ✅ เพิ่มบรรทัดนี้

    cy.get('#category option', { timeout: 10000 })
      .its('length').should('be.gte', 1);

    // ✅ ส่งฟอร์มโดยตรง (ไม่ต้องคลิกปุ่ม)
    cy.get('.modal-body').submit();

  });
});
