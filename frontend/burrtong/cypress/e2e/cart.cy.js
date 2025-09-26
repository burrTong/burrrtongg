describe('ขั้นตอนตะกร้าสินค้า (Shopping Cart)', () => {

  beforeEach(() => {
    // ก่อนการเทสแต่ละครั้ง ให้ทำการล็อกอินเข้าระบบก่อนเสมอ
    cy.visit('/');

    // กรอกข้อมูลล็อกอิน
    cy.get('input#email').type('customer@customer.com');
    cy.get('input#password').type('CUSTOMER');
    cy.get('button[type="submit"]').click();

    // รอจนกว่าจะแน่ใจว่าล็อกอินสำเร็จและไปที่หน้า home
    cy.url({ timeout: 10000 }).should('include', '/home');
  });

  it('ควรจะเพิ่มสินค้า, ชำระเงิน, และทำให้ตะกร้าว่างได้', () => {
    // 1. เริ่มจากหน้า home, ไปยังหน้า products ก่อน
    cy.get('a[href="/home/products"]').first().click();
    cy.url().should('include', '/home/products');

    // 2. คลิกที่สินค้าชิ้นแรกเพื่อไปยังหน้ารายละเอียด
    cy.get('.product-card', { timeout: 10000 }).first().click();
    cy.url().should('match', /\/home\/products\/\d+/);

    // 3. กดปุ่ม "เพิ่มใส่ตะกร้า"
    cy.contains('button', 'เพิ่มใส่ตะกร้า').should('be.visible').click();

    // 4. ตรวจสอบว่า URL เปลี่ยนไปเป็น /home/cart และมีสินค้า 1 ชิ้น
    cy.url({ timeout: 10000 }).should('include', '/home/cart');
    cy.get('.cart-item').should('have.length', 1);

    // 5. กดปุ่ม "ชำระเงิน"
    cy.contains('button', 'ชำระเงิน').click();

    // 6. ตรวจสอบว่าปุ่มเปลี่ยนข้อความเป็น "Order Placed!"
    cy.contains('.checkout-button', 'Order Placed!').should('be.visible');

    // 7. ตรวจสอบว่าตะกร้าว่างหลังจากผ่านไปสักครู่ (component มี setTimeout 3 วิ)
    cy.get('.cart-item', { timeout: 4000 }).should('not.exist');
  });
});
