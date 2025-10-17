describe('ขั้นตอนการดูสินค้า (ต้องล็อกอิน)', () => {

  beforeEach(() => {
    // ก่อนการเทสแต่ละครั้ง ให้ทำการล็อกอินเข้าระบบก่อนเสมอ
    cy.visit('/');

    cy.get('input#email').type('customer@customer.com');
    cy.get('input#password').type('customer');
    cy.get('button[type="submit"]').click();

    cy.url({ timeout: 10000 }).should('include', '/home');
  });

  it('ควรจะแสดงรายการสินค้าได้สำเร็จ (หลังล็อกอิน)', () => {
    // ไปหน้า Products โดยคลิกลิงก์ "อันสุดท้าย"
    cy.get('a[href="/home/products"]').last().click();

    cy.url().should('include', '/home/products');

    // มีสินค้าอย่างน้อย 1 ชิ้น
    cy.get('.product-card', { timeout: 10000 }).should('have.length.gt', 0);

    // ตรวจสินค้าชิ้น "สุดท้าย"
    cy.get('.product-card').last().within(() => {
      cy.get('h3').should('not.be.empty');        // ชื่อสินค้า
      cy.get('.product-price').should('not.be.empty'); // ราคา
    });
  });

  it('ควรจะเข้าไปดูรายละเอียดสินค้าได้ (หลังล็อกอิน)', () => {
    // ไปหน้า Products โดยคลิกลิงก์ "อันสุดท้าย"
    cy.get('a[href="/home/products"]').last().click();
    cy.url().should('include', '/home/products');

    // รอโหลด แล้วเก็บชื่อสินค้าชิ้น "สุดท้าย"
    cy.get('.product-card h3', { timeout: 10000 })
      .last()
      .invoke('text')
      .then((name) => name.trim())
      .then((productName) => {

        // คลิกสินค้าชิ้น "สุดท้าย"
        cy.get('.product-card').last().scrollIntoView({ block: 'center' }).click();

        // URL ควรเปลี่ยนไปเป็นรายละเอียดสินค้า
        cy.url().should('match', /\/home\/products\/\d+$/);

        // ชื่อบนหน้า detail ต้องตรงกับที่คลิกมา
        cy.get('h1').invoke('text').then((t) => t.trim()).should('eq', productName);

        // มีปุ่ม "เพิ่มใส่ตะกร้า"
        cy.contains('button', 'Add to Cart').should('be.visible').click();

        // 4. ตรวจสอบว่า URL เปลี่ยนไปเป็น /home/cart และมีสินค้า 1 ชิ้น
        cy.url({ timeout: 10000 }).should('include', '/home/cart');
        cy.get('.cart-item').should('have.length', 1);

        // 5. กดปุ่ม "ชำระเงิน"
        cy.contains('button', 'Checkout').click();

        // 6. ตรวจสอบว่าปุ่มเปลี่ยนข้อความเป็น "Order Placed!"
        cy.contains('.checkout-button', 'Order Placed!').should('be.visible');

        // 7. ตรวจสอบว่าตะกร้าว่างหลังจากผ่านไปสักครู่ (component มี setTimeout 3 วิ)
        cy.get('.cart-item', { timeout: 4000 }).should('not.exist');
      });
  });
});
