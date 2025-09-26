// @ts-check
const { defineConfig } = require('@playwright/test');

module.exports = defineConfig({
  timeout: 30_000,
  use: {
    baseURL: process.env.BASE_URL || 'http://localhost:4173'
  },
  reporter: [['html', { outputFolder: 'report' }]]
});
