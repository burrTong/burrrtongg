describe('ลบสินค้าที่ชื่อมี Cypress (เลือกอันสุดท้าย)', () => {
  beforeEach(() => {
    // ล็อกอินแอดมิน
    cy.visit('/admin/login');
    cy.get('input#email').type('admin@admin.com');
    cy.get('input#password').type('admin');
    cy.get('button[type="submit"]').click();
    cy.url({ timeout: 10000 }).should('include', '/admin/products');
  });

  it('ควรลบสินค้าที่ชื่อมี Cypress (อันสุดท้าย) ได้สำเร็จ', () => {
    // รอให้ตารางแสดงผล
    cy.get('.product-table tbody tr', { timeout: 10000 }).should('have.length.greaterThan', 0);

    // เก็บ "จำนวนแถวที่มีคำว่า Cypress" ปัจจุบัน และเลือก "แถวสุดท้าย" ของกลุ่มนั้น
    cy.get('.product-table tbody tr').then(($rows) => {
      const rows = Cypress.$($rows).filter((_, tr) =>
        tr.innerText.toLowerCase().includes('cypress')
      );

      const countBefore = rows.length;
      expect(countBefore, 'ต้องมีสินค้าที่มีคำว่า Cypress อย่างน้อย 1 ชิ้น').to.be.greaterThan(0);

      const $lastRow = rows.last();

      // ดึงชื่อสินค้า (คอลัมน์ที่ 3 เป็น Name ตาม UI ตอน render)
      const productName = $lastRow.find('td').eq(2).text().trim();

      // คลิกปุ่มลบของ "แถวสุดท้าย" นั้น
      cy.wrap($lastRow)
        .scrollIntoView({ block: 'center' })
        .within(() => {
          cy.get('.delete-product-btn').should('be.visible').click();
        });

      // กดยืนยันใน window.confirm
      cy.on('window:confirm', (txt) => {
        // ไม่ผูกกับ id ที่แน่นอน แค่เช็คข้อความหลัก ๆ
        expect(txt.toLowerCase()).to.include('are you sure you want to delete product');
        return true; // กดยืนยัน
      });

      // จับ alert ความสำเร็จ
      cy.on('window:alert', (txt) => {
        expect(txt.toLowerCase()).to.include('product deleted successfully');
      });

      // ตรวจว่าจำนวนรายการที่มีคำว่า Cypress ลดลง 1
      cy.get('.product-table tbody tr', { timeout: 10000 }).then(($rowsAfter) => {
        const rowsAfter = Cypress.$($rowsAfter).filter((_, tr) =>
          tr.innerText.toLowerCase().includes('cypress')
        );
        expect(rowsAfter.length).to.equal(countBefore - 1);

        // และชื่อสินค้าที่เพิ่งลบ ไม่ควรอยู่ในตารางแล้ว
        const stillExists = rowsAfter.toArray().some(tr =>
          tr.innerText.includes(productName)
        );
        expect(stillExists, `ไม่ควรเหลือแถวชื่อ: ${productName}`).to.be.false;
      });
    });
  });
});
