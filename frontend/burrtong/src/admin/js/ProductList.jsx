
import React, { useState, useEffect } from "react";

import { Link, useNavigate } from "react-router-dom";

import { getAllProducts, createProduct, deleteProduct, updateProduct } from "../api/productApi";

import NewProductModal from "./NewProductModal";

import EditProductModal from "./EditProductModal";

import '../css/ProductList.css';



const API_BASE_URL = 'http://localhost:8080';



function ProductList() {

  const [products, setProducts] = useState([]);

  const [loading, setLoading] = useState(true);

  const [error, setError] = useState(null);

  const [isNewModalOpen, setIsNewModalOpen] = useState(false);

  const [isEditModalOpen, setIsEditModalOpen] = useState(false);

  const [editingProduct, setEditingProduct] = useState(null);

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

    navigate('/admin/login');

  };



  const handleCreateProduct = async (productData) => {

    try {

      const newProduct = await createProduct(productData);

      setProducts([newProduct, ...products]);

      setIsNewModalOpen(false);

    } catch (err) {

      console.error("Failed to create product:", err);

      alert(err.message);

    }

  };



  const handleEditProduct = (product) => {

    setEditingProduct(product);

    setIsEditModalOpen(true);

  };



  const handleUpdateProduct = async (productId, productData) => {

    try {

      const updatedProduct = await updateProduct(productId, productData);

      setProducts(products.map(p => p.id === productId ? updatedProduct : p));

      setIsEditModalOpen(false);

      setEditingProduct(null);

    } catch (err) {

      console.error("Failed to update product:", err);

      alert(err.message);

    }

  };



  const handleDeleteProduct = async (productId) => {

    if (window.confirm(`Are you sure you want to delete product with ID: ${productId}? This will also delete any associated order items.`)) {

      try {

        await deleteProduct(productId);

        setProducts(products.filter(product => product.id !== productId));

        alert('Product deleted successfully!');

      } catch (err) {

        console.error("Failed to delete product:", err);

        alert(err.message);

      }

    }

  };



  const renderProductRow = (product) => {

    const productModel = {

        id: product.id || 'N/A',

        name: product.name || 'No Name',

        stock: product.stock || 0,

        price: product.price || 0,

        category: product.category ? product.category.name : 'Uncategorized',

        createdAt: product.createdAt || new Date().toISOString(),

        imageUrl: product.imageUrl || '/assets/product.png',

    };



    return (

        <tr key={productModel.id}>

            <td>{productModel.id}</td>

            <td>

              <img 

                src={`${API_BASE_URL}${productModel.imageUrl}`}

                alt={productModel.name}

                className="product-thumbnail"

              />

            </td>

            <td>{productModel.name}</td>

            <td>

                <span className={`status ${productModel.stock > 0 ? 'status-available' : 'status-out-of-stock'}`}>

                    {productModel.stock}

                </span>

            </td>

            <td>{productModel.price.toLocaleString()}</td>

            <td>{productModel.category}</td>

            <td>{new Date(productModel.createdAt).toLocaleDateString()}</td>

            <td>

                <button 

                    className="edit-product-btn" 

                    onClick={() => handleEditProduct(product)}

                >

                    Edit

                </button>

                <button 

                    className="delete-product-btn" 

                    onClick={() => handleDeleteProduct(productModel.id)}

                >

                    Delete

                </button>

            </td>

        </tr>

    );

  };



  return (

    <div className="dashboard">

      <aside className="sidebar">

        <div>

          <h2>Burtong</h2>

          <ul>

            <li><Link to="/admin/products">📦Products’ List</Link></li>

            <li><Link to="/admin/orders">📋Orders’ List</Link></li>

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

          <button onClick={() => setIsNewModalOpen(true)} className="new-product-btn">

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

                            <th>Image</th>

                            <th>Name</th>

                            <th>Stock</th>

                            <th>Price</th>

                            <th>Type</th>

                            <th>Date</th>

                            <th>Actions</th>

                          </tr>

                        </thead>

            <tbody>

              {products.map(renderProductRow)}

            </tbody>

          </table>

        )}

      </main>



      <NewProductModal 

        isOpen={isNewModalOpen} 

        onClose={() => setIsNewModalOpen(false)} 

        onCreateProduct={handleCreateProduct}

      />



      {editingProduct && (

        <EditProductModal

          isOpen={isEditModalOpen}

          onClose={() => {

            setIsEditModalOpen(false);

            setEditingProduct(null);

          }}

          product={editingProduct}

          onUpdateProduct={handleUpdateProduct}

        />

      )}

    </div>

  );

}



export default ProductList;