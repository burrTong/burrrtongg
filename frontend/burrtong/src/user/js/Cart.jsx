import React, { useState } from 'react';
import '../css/Cart.css';
import { Link } from 'react-router-dom';
import Product from '../models/product.js';
import { createOrder } from '../api/orderApi.js';

const API_BASE_URL = 'http://localhost:8080';

const Cart = ({ cart, setCart }) => {
  const [checkoutStatus, setCheckoutStatus] = useState('idle'); // idle, success, error

  const getTotal = () => {
    return cart.reduce((total, item) => {
      const price = item.price || 0;
      const quantity = item.quantity || 0;
      return total + price * quantity;
    }, 0);
  };

  const handleQuantityChange = (productId, newQuantity) => {
    if (newQuantity < 1) {
      setCart(cart.filter(item => item.id !== productId));
    } else {
      setCart(
        cart.map(item =>
          item.id === productId ? { ...item, quantity: newQuantity } : item
        )
      );
    }
  };

  const handleCheckout = async () => {
    const customerId = localStorage.getItem('userId');

    if (!customerId) {
      alert("Please log in to proceed with the checkout.");
      return;
    }

    if (cart.length === 0) {
      alert("Your cart is empty.");
      return;
    }

    const orderRequest = {
      customerId: parseInt(customerId, 10),
      items: cart.map(item => ({
        productId: item.id,
        quantity: item.quantity,
      })),
    };

    try {
      await createOrder(orderRequest);
      setCheckoutStatus('success');
      setTimeout(() => {
        setCart([]);
        setCheckoutStatus('idle');
      }, 3000); // Reset after 3 seconds
    } catch (error) {
      console.error("Failed to create order:", error);
      setCheckoutStatus('error');
      alert(`Failed to create order: ${error.message}`);
    }
  };

  return (
    <div className="cart-container">
      <h1>Shopping Cart</h1>
      <div className="cart-content">
        <div className="cart-items">
          <div className="cart-header">
            <div></div>
            <div>Price</div>
            <div>Quantity</div>
          </div>
          {cart.map(item => {
            const product = new Product(item);
            return (
              <div className="cart-item" key={item.id}>
                <div className="cart-item-details">
                  <img src={`${API_BASE_URL}${product.imageUrl || '/assets/product.png'}`} alt={product.name} />
                  <p>{product.name}</p>
                </div>
                <div className="cart-item-price">{product.getFormattedPrice()}</div>
                <div className="cart-item-quantity">
                  <button onClick={() => handleQuantityChange(item.id, item.quantity - 1)}>-</button>
                  <span>{item.quantity}</span>
                  <button onClick={() => handleQuantityChange(item.id, item.quantity + 1)}>+</button>
                </div>
              </div>
            )
          })}
        </div>
        <div className="cart-summary">
          <h2>Order Summary</h2>
          <div className="summary-row">
            <span>subtotal</span>
            <span>{getTotal().toLocaleString()}.-</span>
          </div>
          <div className="summary-row">
            <span>shipping</span>
            <span>Free</span>
          </div>
          <hr />
          <div className="summary-total">
            <span>{getTotal().toLocaleString()}.-</span>
          </div>
          <button 
            className={`checkout-button ${checkoutStatus === 'success' ? 'success' : ''}`}
            onClick={handleCheckout} 
            disabled={checkoutStatus === 'success'}
          >
            {checkoutStatus === 'success' ? 'Order Placed!' : 'Checkout'}
          </button>
        </div>
      </div>
      <div className="back-link">
        {/* Update link back to products page */}
        <Link to="/home/products">←</Link>
      </div>
    </div>
  );
};

export default Cart;
