import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { getAllProducts } from '../api/productApi.js';
import '../css/Products.css';

const Products = () => {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchProducts = async () => {
      try {
        const fetchedProducts = await getAllProducts();
        setProducts(fetchedProducts);
      } catch (error) {
        console.error("Failed to fetch products:", error);
      } finally {
        setLoading(false);
      }
    };
    fetchProducts();
  }, []);

  if (loading) return <div>Loading...</div>;

  return (
    <div className="products-page-container">
      <div className="products-grid">
        {products.map((product) => (
          <div key={product.id} className="product-card">
            <Link to={`${product.id}`}>
              <div className="product-image-container">
                <img src={product.mainImage} alt={product.name} className="product-image" />
              </div>
              <div className="product-info-card">
                <h3>{product.name}</h3>
                <div className="product-footer">
                  <p className="product-price">{product.getFormattedPrice()}</p>
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
