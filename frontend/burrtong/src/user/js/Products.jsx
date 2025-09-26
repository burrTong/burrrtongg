import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { getAllProducts } from '../api/productApi.js';
import '../css/Products.css';

const Products = () => {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);

  const [filteredProducts, setFilteredProducts] = useState([]);
  const [selectedCategory, setSelectedCategory] = useState('All');

  useEffect(() => {
    const fetchProducts = async () => {
      try {
        const fetchedProducts = await getAllProducts();
        setProducts(fetchedProducts);
        setFilteredProducts(fetchedProducts);
      } catch (error) {
        console.error("Failed to fetch products:", error);
      } finally {
        setLoading(false);
      }
    };
    fetchProducts();
  }, []);

  useEffect(() => {
    if (selectedCategory === 'All') {
      setFilteredProducts(products);
    } else {
      setFilteredProducts(products.filter(p => p.category === selectedCategory));
    }
  }, [selectedCategory, products]);

  const categories = ['All', 'Sneakers', 'Boots', 'Sandals'];

  if (loading) return <div>Loading...</div>;

  return (
    <div className="products-page-container" data-test="products-page">
      <div className="filter-container">
        {categories.map(category => (
          <button key={category} onClick={() => setSelectedCategory(category)} data-test={`filter-category-${category.toLowerCase()}`}>
            {category}
          </button>
        ))}
      </div>
      <div className="products-grid">
        {filteredProducts.map((product) => (
          <div
            key={product.id}
            className="product-card"
            data-test="product-card"
          >
            <Link to={`${product.id}`} data-test={`product-link-${product.id}`}>
              <div className="product-image-container">
                <img src={product.mainImage} alt={product.name}
                     className="product-image"
                     data-test="product-image" />
              </div>
              <div className="product-info-card">
                <h3 data-test="product-name">{product.name}</h3>
                <div className="product-footer">
                  <p className="product-price" data-test="product-price">
                    {product.getFormattedPrice()}
                  </p>
                  <p
                    className={`product-status ${product.stock === 0 ? 'out-of-stock' : ''}`}
                    data-test="product-stock"
                  >
                    {product.stock > 0 ? `Stock: ${product.stock}` : 'Out of Stock'}
                  </p>
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
