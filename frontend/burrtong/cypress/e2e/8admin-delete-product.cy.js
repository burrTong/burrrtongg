describe('ลบสนคาทชอม Cypress (เลอกอนสดทาย)', () => {
  let testProductName;

  beforeEach(() => {
    testProductName = `Cypress Test Product ${Date.now()}`;

    cy.visit('/admin/login');
    cy.get('input#email').type('admin@admin.com');
    cy.get('input#password').type('admin');
    cy.get('button[type="submit"]').click();
    cy.url({ timeout: 10000 }).should('include', '/admin/products');

    // Create test product
    cy.intercept('POST', '**/api/products').as('createProduct');
    cy.get('.new-product-btn').click();
    cy.get('.modal-overlay').should('be.visible');
    cy.get('input#name').type(testProductName);
    cy.get('textarea#description').type('Test product');
    cy.get('input#price').type('999');
    cy.get('input#stock').type('100');
    cy.get('input#size').type('49');
    cy.get('#category option').its('length').should('be.gte', 1);
    cy.get('.modal-body').submit();
    cy.wait('@createProduct').its('response.statusCode').should('eq', 200);
    
    // Wait for table to update and verify the product appears
    cy.get('.product-table tbody tr', { timeout: 10000 })
      .filter(`:contains("${testProductName}")`)
      .should('have.length', 1);
  });

  it('ควรลบสนคาทชอม Cypress (อนสดทาย) ไดสำเรจ', () => {
    cy.intercept('DELETE', '**/api/products/*').as('deleteProduct');
    
    cy.get('.product-table tbody tr')
      .filter(`:contains("${testProductName}")`)
      .should('have.length', 1)
      .first()
      .within(() => {
        cy.get('.delete-product-btn').should('be.visible').click();
      });

    cy.on('window:confirm', () => true);
    cy.wait('@deleteProduct').its('response.statusCode').should('eq', 200);
    
    cy.get('.product-table tbody tr')
      .filter(`:contains("${testProductName}")`)
      .should('have.length', 0);
  });
});
