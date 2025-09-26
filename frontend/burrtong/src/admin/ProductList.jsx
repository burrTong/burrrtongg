import React, { useState, useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";
import { getAllProducts, createProduct } from "../user/api/productApi";
import NewProductModal from "./NewProductModal";
import "./ProductList.css";

function ProductList() {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const navigate = useNavigate();

  const username = localStorage.getItem('username') || 'Admin';

  const fetchProducts = async () => {
    try {
      setLoading(true);
      const data = await getAllProducts();
      setProducts(data);
      setError(null);
    } catch (err) {
      setError(err.message);
      setProducts([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchProducts();
  }, []);

  const handleLogout = () => {
    localStorage.removeItem('authToken');
    localStorage.removeItem('userRole');
    localStorage.removeItem('username');
    navigate('/login');
  };

  const handleCreateProduct = async (productData) => {
    try {
      const newProduct = await createProduct(productData);
      setProducts([newProduct, ...products]);
      setIsModalOpen(false);
    } catch (err) {
      console.error("Failed to create product:", err);
      alert(err.message); 
    }
  };

  const renderProductRow = (product) => {
    const productModel = {
        id: product.id || 'N/A',
        name: product.name || 'No Name',
        stock: product.stock || 0, // Use actual stock
        price: product.price || 0,
        category: product.category ? product.category.name : 'Uncategorized',
        createdAt: product.createdAt || new Date().toISOString(),
    };

    return (
        <tr key={productModel.id}>
            <td>{productModel.id}</td>
            <td>{productModel.name}</td>
            <td>
                <span className={`status ${productModel.stock > 0 ? 'status-available' : 'status-out-of-stock'}`}>
                    {productModel.stock} {/* Display stock number */}
                </span>
            </td>
            <td>{productModel.price.toLocaleString()}</td>
            <td>{productModel.category}</td>
            <td>{new Date(productModel.createdAt).toLocaleDateString()}</td>
        </tr>
    );
  };

  return (
    <div className="dashboard">
      <aside className="sidebar">
        <div>
          <h2>Burtong</h2>
          <ul>
            <li><Link to="/admin">📦Products’ List</Link></li>
            <li><Link to="/admin/orders">📋Orders’ List</Link></li>
            {/* <li><Link to="/admin/analytics">📊Analytics</Link></li> */}
          </ul>
        </div>
        <div className="sidebar-footer">
          <div className="user-info">
            <span className="username">{username}</span>
          </div>
          <button onClick={handleLogout} className="logout-btn">Logout</button>
        </div>
      </aside>

      <main className="main">
        <header className="main-header">
          <div className="title-group">
            <h1 className="main-title">Products’ List</h1>
            <p className="breadcrumb">Dashboard / Products’ List</p>
          </div>
          <button onClick={() => setIsModalOpen(true)} className="new-product-btn">
            + New Product
          </button>
        </header>

        {loading && <p>Loading products...</p>}
        {error && <p className="error-message">Error: {error}</p>}

        {!loading && !error && (
          <table className="product-table">
            <thead>
              <tr>
                <th>#</th>
                <th>Name</th>
                <th>Stock</th>
                <th>Price</th>
                <th>Type</th>
                <th>Date</th>
              </tr>
            </thead>
            <tbody>
              {products.map(renderProductRow)}
            </tbody>
          </table>
        )}
      </main>

      <NewProductModal 
        isOpen={isModalOpen} 
        onClose={() => setIsModalOpen(false)} 
        onCreateProduct={handleCreateProduct}
      />
    </div>
  );
}

export default ProductList;