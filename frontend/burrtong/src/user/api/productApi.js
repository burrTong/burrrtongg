import Product from '../models/product.js';

export const getAllProducts = async () => {
  const response = await fetch('/api/products');
  if (!response.ok) {
    throw new Error('Failed to fetch products');
  }
  const data = await response.json();
  return data.map(p => new Product(p));
};

export const getProductById = async (id) => {
  const response = await fetch(`/api/products/${id}`);
  if (!response.ok) {
    throw new Error(`Failed to fetch product with id ${id}`);
  }
  const data = await response.json();
  return new Product(data);
};

export const createProduct = async (productData) => {
  const token = localStorage.getItem('authToken');
  const response = await fetch('/api/products', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`,
    },
    body: JSON.stringify(productData),
  });

  if (!response.ok) {
    const errorBody = await response.json();
    throw new Error(errorBody.message || 'Failed to create product');
  }

  return await response.json();
};
