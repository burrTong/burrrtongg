import React, { useEffect, useState } from "react";
import { getOrdersByCustomerId } from "../api/orderApi";
import { getProductById } from "../../admin/api/productApi";
import { useNavigate } from "react-router-dom";
import "../css/OrderHistory.css";

function OrderHistory({ cart, setCart }) {
  const [orders, setOrders] = useState([]);
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(true);
  const customerId = localStorage.getItem("userId");
  const navigate = useNavigate();

  useEffect(() => {
    if (customerId) {
      getOrdersByCustomerId(customerId)
        .then(setOrders)
        .catch((err) => setError(err.message))
        .finally(() => setLoading(false));
    } else {
      setLoading(false);
    }
  }, [customerId]);

  const handleReorder = async (order) => {
    let allItemsAvailable = true;
    const itemsToAdd = [];
    let unavailableItems = [];

    for (const item of order.orderItems) {
      try {
        const product = await getProductById(item.product.id);
        if (product.stock >= item.quantity) {
          itemsToAdd.push({ ...product, quantity: item.quantity });
        } else {
          allItemsAvailable = false;
          unavailableItems.push({ name: product.name, requested: item.quantity, available: product.stock });
        }
      } catch (error) {
        console.error("Error fetching product details:", error);
        allItemsAvailable = false;
        unavailableItems.push({ name: item.product.name, requested: item.quantity, available: 'unknown' });
      }
    }

    if (allItemsAvailable) {
      setCart(itemsToAdd);
      navigate("/home/cart");
    } else {
      const warningMessage = unavailableItems.map(item => 
        `${item.name} (requested: ${item.requested}, available: ${item.available})`
      ).join('\n');
      alert(`Some items are not available in the desired quantity:\n${warningMessage}`);
    }
  };

  const handleDownloadPdf = async (orderId) => {
    const token = localStorage.getItem('authToken');
    if (!token) {
      alert("Please log in to download PDF reports.");
      return;
    }

    try {
      const response = await fetch(`http://localhost:8080/api/reports/orders/${orderId}/pdf`, {
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

  if (loading) {
    return (
      <div className="order-history-container">
        <div className="loading-message">Loading your orders...</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="order-history-container">
        <div className="error-message">Error: {error}</div>
      </div>
    );
  }

  if (!customerId) {
    return (
      <div className="order-history-container">
        <div className="login-message">Please log in to see your orders.</div>
      </div>
    );
  }

  return (
    <div className="order-history-container">
      <div className="page-header">
        <h1 className="page-title">My Orders</h1>
        <div className="orders-count">{orders.length} order{orders.length !== 1 ? 's' : ''}</div>
      </div>
      
      {orders.length === 0 ? (
        <div className="empty-state">
          <div className="empty-icon">📦</div>
          <h3>No orders yet</h3>
          <p>When you place your first order, it will appear here.</p>
        </div>
      ) : (
        <div className="orders-grid">
          {orders.map((order) => {
            const subtotal = order.orderItems.reduce((acc, item) => acc + item.price * item.quantity, 0);
            const discount = order.coupon ? subtotal - order.totalPrice : 0;

            return (
              <div key={order.id} className="order-card">
                <div className="order-header">
                  <div className="order-info">
                    <span className="order-id">#{order.id}</span>
                    <span className="order-date">{new Date(order.orderDate).toLocaleDateString()}</span>
                  </div>
                  <div className="header-right">
                    <div className={`order-history-status order-history-status-${order.status ? order.status.toLowerCase() : 'unknown'}`}>
                      {order.status || 'UNKNOWN'}
                    </div>
                    <div className="order-actions">
                      <button className="action-btn print-btn" onClick={() => handleDownloadPdf(order.id)}>
                        Print PDF
                      </button>
                      <button className="action-btn reorder-btn" onClick={() => handleReorder(order)}>
                        Re-order
                      </button>
                    </div>
                  </div>
                </div>

                <div className="order-items">
                  {order.orderItems.map((item) => (
                    <div key={item.product.id} className="order-item">
                      <div className="item-info">
                        <div className="item-name">{item.product.name}</div>
                        <div className="item-quantity">Qty: {item.quantity}</div>
                      </div>
                      <div className="item-price">${item.price.toFixed(2)}</div>
                    </div>
                  ))}
                </div>

                <div className="order-footer">
                  <div className="price-breakdown">
                    <div className="price-row">
                      <span>Subtotal:</span>
                      <span>${subtotal.toFixed(2)}</span>
                    </div>
                    {order.coupon && (
                      <div className="price-row discount">
                        <span>Discount ({order.coupon.code}):</span>
                        <span>-${discount.toFixed(2)}</span>
                      </div>
                    )}
                    <div className="price-row total">
                      <span>Total:</span>
                      <span>${order.totalPrice.toFixed(2)}</span>
                    </div>
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
}

export default OrderHistory;
