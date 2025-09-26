describe('E2E Customer flows', () => {
  const email = 'customer@example.com';
  const password = 'cust123';

  beforeEach(() => {
    cy.visit('/login');
    cy.get('input[name="email"]').type(email);
    cy.get('input[name="password"]').type(password);
    cy.get('button[type="submit"]').click();

    cy.url().should('include', '/home');
  });

  it('US-C01: Login/Register', () => {
    cy.contains('Welcome').should('exist');
  });

  it('US-C02: Browse products', () => {
    cy.visit('/products');
    cy.get('.product-card').should('exist');
    cy.get('.product-card').first().within(() => {
      cy.get('.product-name').should('exist');
      cy.get('.product-price').should('exist');
      cy.get('.product-image').should('exist');
    });
  });

  it('US-C03: Search and filter', () => {
    cy.get('input[placeholder="Search"]').type('sneakers{enter}');
    cy.get('.product-card').should('exist');

    cy.get('select[name="category"]').select('Sneakers');
    cy.get('.product-card').should('exist');
  });

  it('US-C04: Add to cart & edit', () => {
    cy.get('.product-card').first().within(() => {
      cy.get('button.add-to-cart').click();
    });

    cy.visit('/cart');
    cy.get('.cart-item').first().within(() => {
      cy.get('input.quantity').clear().type('2');
      cy.get('select.size').select('42');
    });
    cy.contains('Update').click();
  });

  it('US-C05: Review order before payment', () => {
    cy.visit('/checkout');
    cy.contains('Order Summary').should('exist');
  });

  it('US-C06: Track order status', () => {
    cy.visit('/orders');
    cy.get('.order-item').first().within(() => {
      cy.contains('Status').should('exist');
    });
  });

  it('US-C07: Contact support', () => {
    cy.visit('/contact');
    cy.get('textarea[name="message"]').type('Test message');
    cy.get('button[type="submit"]').click();
    cy.contains('Sent').should('exist');
  });
});
