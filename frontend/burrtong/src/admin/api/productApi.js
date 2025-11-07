const API_BASE_URL = 'http://localhost:8080';

export const getAllProducts = async () => {
  const response = await fetch(`${API_BASE_URL}/api/products`);
  if (!response.ok) {
    throw new Error('Failed to fetch products');
  }
  const data = await response.json();
  return data;
};

export const createProduct = async (formData) => {
  const token = localStorage.getItem('authToken');
  const response = await fetch(`${API_BASE_URL}/api/products`, {
    method: 'POST',
    headers: {
      // 'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`,
    },
    body: formData,
  });

  if (!response.ok) {
    const errorBody = await response.json();
    throw new Error(errorBody.message || 'Failed to create product');
  }

  return await response.json();
};

export const updateProduct = async (productId, formData) => {
  const token = localStorage.getItem('authToken');
  const response = await fetch(`${API_BASE_URL}/api/products/${productId}`, {
    method: 'PUT',
    headers: {
      'Authorization': `Bearer ${token}`,
    },
    body: formData,
  });

  if (!response.ok) {
    const errorBody = await response.json();
    throw new Error(errorBody.message || 'Failed to update product');
  }

  return await response.json();
};



export const deleteProduct = async (productId) => {
  const token = localStorage.getItem('authToken');
  const response = await fetch(`${API_BASE_URL}/api/products/${productId}`, {
    method: 'DELETE',
    headers: {
      'Authorization': `Bearer ${token}`,
    },
  });

  if (!response.ok) {
    const errorBody = await response.json();
    throw new Error(errorBody.message || 'Failed to delete product');
  }

  return true; // Indicate successful deletion
};

export const getWeeklyStockReport = async () => {
  const token = localStorage.getItem('authToken');
  const response = await fetch(`${API_BASE_URL}/api/products/stock-report`, {
    headers: {
      'Authorization': `Bearer ${token}`,
    },
  });

  if (!response.ok) {
    const errorBody = await response.json();
    throw new Error(errorBody.message || 'Failed to fetch weekly stock report');
  }

  return await response.json();
};

export const getProductById = async (productId) => {
  const response = await fetch(`${API_BASE_URL}/api/products/${productId}`);
  if (!response.ok) {
    throw new Error('Failed to fetch product');
  }
  return await response.json();
};

export const searchProducts = async (query) => {
  const response = await fetch(`${API_BASE_URL}/api/products/search?name=${query}`);
  if (!response.ok) {
    throw new Error('Failed to search products');
  }
  return await response.json();
};