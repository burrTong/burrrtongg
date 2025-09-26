describe('E2E Admin flows', () => {
  beforeEach(() => {
    cy.intercept('POST', '/api/auth/login', {
      statusCode: 200,
      body: { token: 'mock-token', role: 'ADMIN' },
    }).as('loginRequest');

    cy.intercept('GET', '/api/seller-applications', { statusCode: 200, body: [{ id: 1, name: 'Test Seller' }] }).as('getApps');
    cy.intercept('POST', '/api/seller-applications/1/approve', { statusCode: 200 }).as('approveApp');
    cy.intercept('POST', '/api/seller-applications/1/reject', { statusCode: 200 }).as('rejectApp');
    cy.intercept('GET', '/api/products', { statusCode: 200, body: [{ id: 1, name: 'Test Product' }] }).as('getProducts');
    cy.intercept('DELETE', '/api/products/1', { statusCode: 200 }).as('deleteProduct');
    cy.intercept('GET', '/api/orders', { statusCode: 200, body: [{ id: 1, customer: 'Test Customer' }] }).as('getOrders');
    cy.intercept('GET', '/api/users', { statusCode: 200, body: [{ id: 1, name: 'Test User', role: 'Customer', status: 'Active' }] }).as('getUsers');
    cy.intercept('POST', '/api/users/1/disable', { statusCode: 200 }).as('disableUser');
    cy.intercept('GET', '/api/support-tickets', { statusCode: 200, body: [{ id: 1, subject: 'Test Ticket' }] }).as('getTickets');
    cy.intercept('GET', '/api/support-tickets/1', { statusCode: 200, body: { id: 1, subject: 'Test Ticket', messages: [] } }).as('getTicket1');
    cy.intercept('POST', '/api/support-tickets/1/reply', { statusCode: 200 }).as('replyTicket');

    // Login as an admin before each test
    cy.visit('/login');
    cy.login('admin', 'adminpass');
    cy.wait('@loginRequest');
    cy.visit('/admin/dashboard');
  });
  const email = 'admin@example.com';
  const password = 'admin123';

  it('US-A01: Admin dashboard', () => {
    cy.visit('/admin/login');
    cy.get('input[name="email"]').type(email);
    cy.get('input[name="password"]').type(password);
    cy.get('button[type="submit"]').click();
    cy.url().should('include', '/admin/dashboard');

    cy.contains('Total Sellers').should('exist');
    cy.contains('Total Sales').should('exist');
  });

  it('US-A02: Approve seller', () => {
    cy.visit('/admin/seller-approvals');
    cy.get('[data-test="seller-approve-btn"]').first().click();
    cy.on('window:confirm', () => true);
  });

  it('US-A03: Manage products', () => {
    cy.visit('/admin/products');
    cy.get('[data-test="delete-product"]').first().click();
    cy.on('window:confirm', () => true);
  });

  it('US-A04: View all orders', () => {
    cy.visit('/admin/orders');
    cy.get('.order-item').should('exist');
  });

  it('US-A05: Manage users', () => {
    cy.visit('/admin/users');
    cy.contains('Customers').should('exist');
    cy.contains('Sellers').should('exist');
  });

  it('US-A06: Handle complaints', () => {
    cy.visit('/admin/complaints');
    cy.contains('Complaints').should('exist');
  });
});
