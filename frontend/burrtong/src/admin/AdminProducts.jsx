import React from 'react';

const AdminProducts = () => {
  return (
    <div>
      <h2>Admin Products</h2>
      <div data-test="product-row">
        <span>Product Name</span>
        <button data-test="delete-product-button">Delete</button>
      </div>
      <button data-test="confirm-delete-button">Confirm Delete</button>
    </div>
  );
};

export default AdminProducts;
