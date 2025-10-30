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
    return <div>Loading...</div>;
  }

  if (error) {
    return <div className="order-history-error">Error: {error}</div>;
  }

  if (!customerId) {
    return <div>Please log in to see your orders.</div>;
  }

  return (
    <div className="order-history-container">
      <h1>My Orders</h1>
      {orders.length === 0 ? (
        <p>You have no orders.</p>
      ) : (
        <ul className="order-history-list">
          {orders.map((order) => (
            <li key={order.id} className="order-history-item">
              <div className="order-history-item-header">
                <span>Order ID: {order.id}</span>
                <span>Date: {new Date(order.orderDate).toLocaleDateString()}</span>
                <span>Status: {order.status}</span>
                <span>Total: ${order.totalPrice.toFixed(2)}</span>
              </div>
              <ul className="order-history-item-products">
                {order.orderItems.map((item) => (
                  <li key={item.product.id}>
                    <span className="product-name">{item.product.name}</span>
                    <span className="product-quantity">Quantity: {item.quantity}</span>
                    <span className="product-price">${item.price.toFixed(2)}</span>
                  </li>
                ))}
              </ul>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}

export default OrderHistory;
