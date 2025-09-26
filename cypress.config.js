const { defineConfig } = require("cypress");

module.exports = defineConfig({
  e2e: {
    setupNodeEvents(on, config) {
      // implement node event listeners here
    },
  },
});

module.exports = {
  e2e: {
    baseUrl: 'http://localhost:5173', // เปลี่ยนตามโปรเจ็กต์ของคุณ
    supportFile: 'cypress/support/e2e.js',
    specPattern: 'cypress/e2e/**/*.cy.{js,ts}'
  }
}