describe('E2E Seller flows', () => {
  beforeEach(() => {
    cy.intercept('POST', '/api/auth/login', {
      statusCode: 200,
      body: { token: 'mock-token', role: 'SELLER' },
    }).as('loginRequestSeller');

    cy.intercept('GET', '/api/products', { statusCode: 200, body: [{ id: 1, name: 'Test Product', price: 1200, size: 10, stock: 50 }] }).as('getProductsSeller');
    cy.intercept('POST', '/api/products', { statusCode: 201 }).as('createProduct');
    cy.intercept('PUT', '/api/products/1', { statusCode: 200 }).as('updateProduct');
    cy.intercept('DELETE', '/api/products/1', { statusCode: 200 }).as('deleteProduct');
    cy.intercept('GET', '/api/orders', { statusCode: 200, body: [{ id: 1, status: 'รอดำเนินการ' }] }).as('getOrdersSeller');
    cy.intercept('PUT', '/api/orders/1', { statusCode: 200 }).as('updateOrder');
    cy.intercept('GET', '/api/profile', { statusCode: 200, body: { storeName: 'My Store' } }).as('getProfile');
    cy.intercept('PUT', '/api/profile', { statusCode: 200 }).as('updateProfile');

    // Login as a seller before each test
    cy.visit('/login');
    cy.login('seller', 'password');
    cy.wait('@loginRequestSeller');
    cy.visit('/seller/dashboard');
  });
  const email = 'seller@example.com';
  const password = 'password123';

  it('US-S01: Seller can sign up', () => {
    cy.intercept('POST', '/api/auth/signup', {
      statusCode: 200,
      body: { message: 'Signup successful' },
    }).as('sellerSignupRequest');

    cy.visit('/signup');
    cy.get('[data-test="signup-username"]').type('newSeller@example.com').trigger('change');
    cy.get('[data-test="signup-password"]').type('password123').trigger('change');
    cy.get('[data-test="signup-submit"]').click();
    cy.wait('@sellerSignupRequest');
    cy.url().should('include', '/login');
  });

  it('US-S02: Add/Edit/Delete product', () => {
    cy.visit('/seller/products');
    cy.get('button[data-test="add-product"]').click();
    cy.get('input[name="name"]').type('New Shoe');
    cy.get('input[name="price"]').type('2000');
    cy.get('input[name="stock"]').type('10');
    cy.get('button[type="submit"]').click();
    cy.contains('New Shoe').should('exist');

    cy.get('[data-test="edit-product"]').first().click();
    cy.get('input[name="price"]').clear().type('2100');
    cy.get('button[type="submit"]').click();
    cy.contains('2100').should('exist');

    cy.get('[data-test="delete-product"]').first().click();
    cy.on('window:confirm', () => true);
    cy.contains('New Shoe').should('not.exist');
  });

  it('US-S03 & US-S04: View and update orders', () => {
    cy.visit('/seller/orders');
    cy.get('.order-item').first().within(() => {
      cy.get('select[name="status"]').select('Shipped');
      cy.get('select[name="status"]').should('have.value', 'Shipped');
    });
  });

  it('US-S05: View sales summary', () => {
    cy.visit('/seller/summary');
    cy.contains('Total Sales').should('exist');
  });

  it('US-S06: Edit profile', () => {
    cy.visit('/seller/profile');
    cy.get('input[name="shopName"]').clear().type('Updated Shop');
    cy.get('button[type="submit"]').click();
    cy.contains('Profile updated').should('exist');
  });
});
