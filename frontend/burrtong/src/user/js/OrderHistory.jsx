import React, { useEffect, useState } from "react";
import { getOrdersByCustomerId } from "../api/orderApi";
import "../css/OrderHistory.css";

function OrderHistory() {
  const [orders, setOrders] = useState([]);
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(true);
  const customerId = localStorage.getItem("userId");

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
                      <button className="action-btn print-btn" disabled>
                        Print PDF
                      </button>
                      <button className="action-btn reorder-btn" disabled>
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
