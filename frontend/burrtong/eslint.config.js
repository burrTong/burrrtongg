import js from '@eslint/js'
import globals from 'globals'
import reactHooks from 'eslint-plugin-react-hooks'
import reactRefresh from 'eslint-plugin-react-refresh'
import { defineConfig, globalIgnores } from 'eslint/config'
import cypress from 'eslint-plugin-cypress/flat'

export default defineConfig([
  globalIgnores(['dist']),
  {
    files: ['**/*.{js,jsx}'],
    ignores: ['cypress/**'], // Ignore cypress files in the main config
    extends: [
      js.configs.recommended,
      reactHooks.configs['recommended-latest'],
      reactRefresh.configs.vite,
    ],
    languageOptions: {
      ecmaVersion: 2020,
      globals: globals.browser,
      parserOptions: {
        ecmaVersion: 'latest',
        ecmaFeatures: { jsx: true },
        sourceType: 'module',
      },
    },
    rules: {
      'no-unused-vars': ['error', { varsIgnorePattern: '^[A-Z_]' }],
    },
  },
  // Config for Cypress test files
  {
    files: ['cypress/**/*.cy.js'],
    ...cypress.configs.recommended,
    languageOptions: {
      globals: {
        ...cypress.environments.globals.globals,
      },
    },
  },
  // Config for cypress.config.js
  {
    files: ['cypress.config.js'],
    languageOptions: {
      globals: {
        module: 'writable',
        require: 'readonly',
      },
    },
  },
])
