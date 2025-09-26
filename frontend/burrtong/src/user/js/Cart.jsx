import React, { useState } from 'react';
import '../css/Cart.css';
import { Link } from 'react-router-dom';
import Product from '../models/product.js';
import { createOrder } from '../api/orderApi.js';

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
    if (cart.length === 0) {
      alert("Your cart is empty.");
      return;
    }

    const orderRequest = {
      customerId: 1,
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
      }, 3000);
    } catch (error) {
      console.error("Failed to create order:", error);
      setCheckoutStatus('error');
      alert(`Failed to create order: ${error.message}`);
    }
  };

  return (
    <div className="cart-container" data-test="cart-page">
      <h1>Shopping Cart</h1>
      <div className="cart-content">
        <div className="cart-items">
          <div className="cart-header">
            <div></div>
            <div>ราคาสินค้า</div>
            <div>จำนวน</div>
          </div>
          {cart.map(item => {
            const product = new Product(item);
            return (
              <div className="cart-item" key={item.id} data-test={`cart-item-${item.id}`}>
                <div className="cart-item-details">
                  <img src={product.mainImage} alt={product.name} data-test={`cart-item-img-${item.id}`} />
                  <p data-test={`cart-item-name-${item.id}`}>{product.name}</p>
                </div>
                <div className="cart-item-price" data-test={`cart-item-price-${item.id}`}>
                  {product.getFormattedPrice()}
                </div>
                <div className="cart-item-quantity">
                  <button data-test={`cart-qty-dec-${item.id}`} onClick={() => handleQuantityChange(item.id, item.quantity - 1)}>-</button>
                  <input type="number" value={item.quantity} onChange={(e) => handleQuantityChange(item.id, parseInt(e.target.value, 10))} data-test="cart-item-quantity" />
                  <button data-test={`cart-qty-inc-${item.id}`} onClick={() => handleQuantityChange(item.id, item.quantity + 1)}>+</button>
                  <button data-test="update-cart-button">Update</button>
                </div>
              </div>
            )
          })}
        </div>
        <div className="cart-summary" data-test="order-summary">
          <h2>ราคารวมสินค้า</h2>
          <div className="summary-row">
            <span>subtotal</span>
            <span data-test="cart-total">{getTotal().toLocaleString()}.-</span>
          </div>
          <div className="summary-row">
            <span>shipping</span>
            <span>Free</span>
          </div>
          <hr />
          <div className="summary-total">
            <span data-test="summary-total">{getTotal().toLocaleString()}.-</span>
          </div>
          <button 
            data-test="checkout-button"
            className={`checkout-button ${checkoutStatus === 'success' ? 'success' : ''}`}
            onClick={handleCheckout} 
            disabled={checkoutStatus === 'success'}
          >
            {checkoutStatus === 'success' ? 'Order Placed!' : 'ชำระเงิน'}
          </button>
        </div>
      </div>
      <div className="back-link">
        <Link to="/home/products">←</Link>
      </div>
    </div>
  );
};

export default Cart;
