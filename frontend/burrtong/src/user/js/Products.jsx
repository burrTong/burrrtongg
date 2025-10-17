import React, { useState, useEffect } from 'react';
import { getAllProducts } from '../api/productApi.js';
import ProductCard from './ProductCard.jsx'; // Add this line
import '../css/Products.css';

// const API_BASE_URL = 'http://localhost:8080'; // Removed as it's now in ProductCard

const Products = () => {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchProducts = async () => {
      try {
        const data = await getAllProducts();
        setProducts(data);
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };

    fetchProducts();
  }, []);

  if (loading) {
    return <div>Loading products...</div>;
  }

  if (error) {
    return <div>Error: {error}</div>;
  }

  return (
    <div className="products-grid"> {/* Changed class name */}
      {products.map(product => (
        <ProductCard key={product.id} product={product} /> // Use ProductCard component
      ))}
    </div>
  );
};

export default Products;
