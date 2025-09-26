// ***********************************************************
// This example support/e2e.js is processed and
// loaded automatically before your test files.
//
// This is a great place to put global configuration and
// behavior that modifies Cypress.
//
// You can change the location of this file or turn off
// automatically serving support files with the
// 'supportFile' configuration option.
//
// You can read more here:
// https://on.cypress.io/configuration
// ***********************************************************

// Import commands.js using ES2015 syntax:
import './commands'

Cypress.on('uncaught:exception', (err, runnable) => {
  // returning false here prevents Cypress from
  // failing the test
  return false
})

// Customer Intercepts
cy.intercept('GET', '/api/products', {
  statusCode: 200,
  body: [
    { id: 1, name: 'Test Sneaker', price: 100, stock: 10, category: 'Sneakers', mainImage: '' },
    { id: 2, name: 'Test Boot', price: 150, stock: 5, category: 'Boots', mainImage: '' },
  ],
}).as('getProducts');

cy.intercept('GET', '/api/products/1', {
  statusCode: 200,
  body: { id: 1, name: 'Test Sneaker', price: 100, stock: 10, category: 'Sneakers', mainImage: '', thumbnails: [] },
}).as('getProduct1');

cy.intercept('POST', '/api/auth/login', {
  statusCode: 200,
  body: { token: 'mock-token', role: 'CUSTOMER' },
}).as('loginRequestCustomer');

cy.intercept('POST', '/api/orders', {
  statusCode: 201,
  body: { id: 1, status: 'รอดำเนินการ' },
}).as('createOrder');

cy.intercept('GET', '/api/orders', {
  statusCode: 200,
  body: [{ id: 1, status: 'รอดำเนินการ' }],
}).as('getOrdersCustomer');

cy.intercept('POST', '/api/contact', {
  statusCode: 200,
}).as('contact');