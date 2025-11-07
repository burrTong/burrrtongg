import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { getAllOrders, updateOrderStatus, denyOrder } from '../api/orderApi'; // Import denyOrder
import '../css/ProductList.css'; // Import shared CSS
import BurtongLogo from '../../assets/Burtong_logo.png'; // Import logo

const API_BASE_URL = 'http://localhost:8080'; // Define API base URL

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

  const handleDenyOrder = async (orderId) => {
    try {
      await denyOrder(orderId);
      // Refresh the order list after successful denial
      fetchOrders();
    } catch (err) {
      console.error(`Failed to deny order ${orderId}:`, err);
      alert(`Failed to deny order: ${err.message}`);
    }
  };

  const handleDownloadPdf = async (orderId) => {
    const token = localStorage.getItem('authToken');
    if (!token) {
      alert("Please log in to download PDF reports.");
      return;
    }

    try {
      const response = await fetch(`${API_BASE_URL}/api/reports/orders/${orderId}/pdf`, {
        headers: {
          'Authorization': `Bearer ${token}`,
        },
      });

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(`Failed to download PDF: ${response.status} ${response.statusText} - ${errorText}`);
      }

      const blob = await response.blob();
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `order_${orderId}.pdf`;
      document.body.appendChild(a);
      a.click();
      a.remove();
      window.URL.revokeObjectURL(url);
    } catch (error) {
      console.error("Error downloading PDF:", error);
      alert(`Failed to download PDF report: ${error.message}`);
    }
  };

  return (
    <div className="dashboard">
      <aside className="sidebar">
        <div>
          <div className="sidebar-logo">
            <Link to="/admin/products">
              <img src={BurtongLogo} alt="Burtong Logo" />
            </Link>
          </div>
          <ul>
            <li><Link to="/admin/products">📦Products' List</Link></li>
            <li><Link to="/admin/orders">📋Orders' List</Link></li>
            <li><Link to="/admin/coupons">🎟️Coupons' List</Link></li>
            <li><Link to="/admin/categories">📂Categories' List</Link></li>

            <li><Link to="/admin/stock-report">📊Weekly Stock Report</Link></li>
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
          <h1 className="main-title">Orders' List</h1>
        </header>

        {loading && <p>Loading orders...</p>}
        {error && <p className="error-message">Error: {error}</p>}

        {!loading && !error && (
          <table className="product-table"> {/* Reusing product-table style */}
            <thead>
              <tr>
                <th>Order ID</th>
                <th>Print</th>
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
                  <td>
                    <button className="print-pdf-btn" onClick={() => handleDownloadPdf(order.id)}>
                      Print PDF
                    </button>
                  </td>
                  <td>{order.customer ? order.customer.username : 'N/A'}</td>
                  <td>{new Date(order.orderDate).toLocaleDateString()}</td>
                  <td>
                    <div>
                      <div>Subtotal: ${order.orderItems.reduce((acc, item) => acc + item.price * item.quantity, 0).toFixed(2)}</div>
                      {order.coupon ? (
                        <div className="discount-details">
                          Discount ({order.coupon.code}): -${(order.orderItems.reduce((acc, item) => acc + item.price * item.quantity, 0) - order.totalPrice).toFixed(2)}
                        </div>
                      ) : (
                        <div className="discount-details">
                          Discount: $0.00
                        </div>
                      )}
                      <div><strong>Total: ${order.totalPrice ? order.totalPrice.toFixed(2) : '0.00'}</strong></div>
                    </div>
                  </td>
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
                          onClick={() => handleDenyOrder(order.id)}
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