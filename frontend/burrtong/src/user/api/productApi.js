import mockProducts from '../data/mock-products.js';
import Product from '../models/product.js';

// Simulate API delay
const delay = (ms) => new Promise(resolve => setTimeout(resolve, ms));

export const getAllProducts = async () => {
  await delay(200); // Simulate network delay
  return mockProducts.map(p => new Product(p));
};

export const getProductById = async (id) => {
  await delay(200);
  const productData = mockProducts.find(p => p.id === id);
  if (productData) {
    return new Product(productData);
  }
  return null;
};
