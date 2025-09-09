import React from 'react';
import '../css/Cart.css';
import { Link } from 'react-router-dom';
import Product from '../models/product.js';

const Cart = ({ cart, setCart }) => {
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

  return (
    <div className="cart-container">
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
              <div className="cart-item" key={item.id}>
                <div className="cart-item-details">
                  <img src={product.mainImage} alt={product.name} />
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
          <h2>ราคารวมสินค้า</h2>
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
          <button className="checkout-button">ชำระเงิน</button>
        </div>
      </div>
      <div className="back-link">
        {/* อัปเดตลิงก์กลับไปหน้า products */}
        <Link to="/home/products">←</Link>
      </div>
    </div>
  );
};

export default Cart;
