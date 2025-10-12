import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { getAllOrders, updateOrderStatus } from '../api/orderApi'; // Import updateOrderStatus

function OrderList() {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  const username = localStorage.getItem('username') || 'Admin';

  const fetchOrders = async () => {
    try {
      setLoading(true);
      const data = await getAllOrders(); 
      setOrders(data);
      setError(null);
    } catch (err) {
      setError(err.message);
      setOrders([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchOrders();
  }, []);

  const handleLogout = () => {
    localStorage.clear();
    navigate('/admin/login');
  };

  const handleUpdateStatus = async (orderId, newStatus) => {
    try {
      await updateOrderStatus(orderId, newStatus);
      // Refresh the order list after successful update
      fetchOrders(); 
    } catch (err) {
      console.error(`Failed to update order ${orderId} to ${newStatus}:`, err);
      alert(`Failed to update order status: ${err.message}`);
    }
  };

  return (
    <div className="dashboard">
      <aside className="sidebar">
        <div>
          <h2>Burtong</h2>
          <ul>
            <li><Link to="/admin/products">📦Products’ List</Link></li>
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
          <h1 className="main-title">Orders’ List</h1>
          <p className="breadcrumb">Dashboard / Orders’ List</p>
        </header>

        {loading && <p>Loading orders...</p>}
        {error && <p className="error-message">Error: {error}</p>}

        {!loading && !error && (
          <table className="product-table"> {/* Reusing product-table style */}
            <thead>
              <tr>
                <th>Order ID</th>
                <th>Customer</th>
                <th>Date</th>
                <th>Total</th>
                <th>Status</th>
                <th>Actions</th> {/* New column for buttons */}
              </tr>
            </thead>
            <tbody>
              {orders.map((order) => (
                <tr key={order.id}>
                  <td>#{order.id}</td>
                  <td>{order.customer ? order.customer.username : 'N/A'}</td>
                  <td>{new Date(order.orderDate).toLocaleDateString()}</td>
                  <td>${order.totalPrice ? order.totalPrice.toFixed(2) : '0.00'}</td>
                  <td>
                    <span className={`status status-${order.status ? order.status.toLowerCase() : 'unknown'}`}>
                      {order.status || 'UNKNOWN'}
                    </span>
                  </td>
                  <td>
                    {order.status === 'PENDING' && (
                      <>
                        <button 
                          onClick={() => handleUpdateStatus(order.id, 'DELIVERED')}
                          className="action-btn btn-confirm"
                        >
                          Accept
                        </button>
                        <button 
                          onClick={() => handleUpdateStatus(order.id, 'CANCELED')}
                          className="action-btn btn-cancel"
                        >
                          Deny
                        </button>
                      </>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </main>
    </div>
  );
}

export default OrderList;