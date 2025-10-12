describe('ขั้นตอนการดูสินค้า (ต้องล็อกอิน)', () => {

  beforeEach(() => {
    // ก่อนการเทสแต่ละครั้ง ให้ทำการล็อกอินเข้าระบบก่อนเสมอ
    cy.visit('/');

    // กรอกข้อมูลล็อกอิน (อาจจะต้องเปลี่ยนให้ตรงกับ user ในระบบ)
    cy.get('input#email').type('customer@customer.com');
    cy.get('input#password').type('customer');
    cy.get('button[type="submit"]').click();

    // รอจนกว่าจะแน่ใจว่าล็อกอินสำเร็จและไปที่หน้า home
    cy.url({ timeout: 10000 }).should('include', '/home');
  });

  it('ควรจะแสดงรายการสินค้าได้สำเร็จ (หลังล็อกอิน)', () => {
    // 1. ตอนนี้เราอยู่ที่หน้า home แล้ว, หา Link ไปยังหน้า Products แล้วคลิก
    cy.get('a[href="/home/products"]').first().click();

    // 2. ตรวจสอบว่า URL เปลี่ยนเป็น /home/products
    cy.url().should('include', '/home/products');

    // 3. ตรวจสอบว่ามีสินค้าแสดงขึ้นมาอย่างน้อย 1 ชิ้น
    cy.get('.product-card', { timeout: 10000 }).should('have.length.gt', 0);

    // 4. ตรวจสอบว่าสินค้าชิ้นแรกมีชื่อและราคา
    cy.get('.product-card').first().within(() => {
      cy.get('h3').should('not.be.empty'); // แก้จาก .product-name เป็น h3
      cy.get('.product-price').should('not.be.empty');
    });
  });

  it('ควรจะเข้าไปดูรายละเอียดสินค้าได้ (หลังล็อกอิน)', () => {
    // 1. เริ่มจากหน้า home, ไปยังหน้า products ก่อน
    cy.get('a[href="/home/products"]').first().click();
    cy.url().should('include', '/home/products');

    // 2. รอให้สินค้าโหลดเสร็จ และเก็บชื่อของสินค้าชิ้นแรกไว้
    cy.get('.product-card h3', { timeout: 10000 }).first().invoke('text').then((productName) => {
      
      // 3. คลิกที่สินค้าชิ้นแรก
      cy.get('.product-card').first().click();

      // 4. ตรวจสอบว่า URL เปลี่ยนไป (เช่น /home/products/1)
      cy.url().should('match', /\/home\/products\/\d+/);

      // 5. ตรวจสอบว่าหน้ารายละเอียดแสดงชื่อสินค้าตรงกับที่คลิกเข้ามา
      cy.get('h1').should('have.text', productName); // แก้จาก h1.product-detail-name เป็น h1

      // 6. ตรวจสอบว่ามีปุ่ม "เพิ่มใส่ตะกร้า"
      cy.contains('button', 'เพิ่มใส่ตะกร้า').should('be.visible'); // แก้จาก 'Add to Cart'
    });
  });
});