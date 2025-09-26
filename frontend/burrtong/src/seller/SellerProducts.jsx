import React from 'react';

const SellerProducts = () => {
  return (
    <div>
      <h2>Seller Products</h2>
      <button data-test="add-product-button">Add Product</button>
      <div data-test="product-list">
        <div data-test="product-row">
          <span data-test="product-price-cell">$100</span>
          <button data-test="edit-product-button">Edit</button>
          <button data-test="delete-product-button">Delete</button>
        </div>
      </div>
      <form>
        <input data-test="product-form-name" />
        <input data-test="product-form-price" />
        <input data-test="product-form-size" />
        <input data-test="product-form-stock" />
        <button data-test="product-form-save">Save</button>
      </form>
      <button data-test="confirm-delete-button">Confirm Delete</button>
    </div>
  );
};

export default SellerProducts;
