const { defineConfig } = require('cypress');

module.exports = defineConfig({
  e2e: {
    baseUrl: 'http://localhost:5173', // URL ของ frontend
    supportFile: false,               // ไม่ใช้ support file
    viewportWidth: 1366,
    viewportHeight: 768,
    defaultCommandTimeout: 8000,
  },
});
