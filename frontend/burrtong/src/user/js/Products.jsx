import React from 'react';
import { Link } from 'react-router-dom';
import { products } from './data.js'; // Import data
import '../css/Products.css'; // Corrected path

const Products = () => {
  return (
    <div className="products-page-container">
      <div className="search-container-products">
        <input type="text" placeholder="search" className="search-box-products" />
      </div>
      <div className="products-grid">
        {products.map(product => (
          <div key={product.id} className="product-card">
            <Link to={`/products/${product.id}`}>
              <div className="product-image-container">
                <img src={product.image} alt={product.name} className="product-image" />
              </div>
              <div className="product-info-card">
                <h3>{product.name}</h3>
                <div className="product-footer">
                  <p className="product-price">{product.price}</p>
                  <p className="product-status">{product.status}</p>
                </div>
              </div>
            </Link>
          </div>
        ))}
      </div>
    </div>
  );
};

export default Products;