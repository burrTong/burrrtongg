import React, { useState, useEffect } from 'react';
import '../css/Cart.css';
import { Link } from 'react-router-dom';
import Product from '../models/product.js';
import { createOrder, getCouponByCode } from '../api/orderApi.js';

const API_BASE_URL = 'http://localhost:8080';

const Cart = ({ cart, setCart }) => {
  const [checkoutStatus, setCheckoutStatus] = useState('idle'); // idle, success, error
  const [couponCode, setCouponCode] = useState('');
  const [appliedCoupon, setAppliedCoupon] = useState(null);
  const [discountAmount, setDiscountAmount] = useState(0);

  const calculateTotal = () => {
    return cart.reduce((total, item) => {
      const price = item.price || 0;
      const quantity = item.quantity || 0;
      return total + price * quantity;
    }, 0);
  };

  const getTotalWithDiscount = () => {
    let total = calculateTotal();
    if (appliedCoupon) {
      if (appliedCoupon.discountType === 'FIXED') {
        total -= appliedCoupon.discountValue;
      } else if (appliedCoupon.discountType === 'PERCENTAGE') {
        total -= total * (appliedCoupon.discountValue / 100);
      }
    }
    return Math.max(0, total); // Ensure total doesn't go below zero
  };

  useEffect(() => {
    if (appliedCoupon) {
      let total = calculateTotal();
      let discount = 0;
      if (appliedCoupon.discountType === 'FIXED') {
        discount = appliedCoupon.discountValue;
      } else if (appliedCoupon.discountType === 'PERCENTAGE') {
        discount = total * (appliedCoupon.discountValue / 100);
      }
      setDiscountAmount(discount);
    } else {
      setDiscountAmount(0);
    }
  }, [cart, appliedCoupon]);

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

  const handleApplyCoupon = async () => {
    if (!couponCode) {
      alert('Please enter a coupon code.');
      return;
    }
    try {
      const coupon = await getCouponByCode(couponCode);
      // Basic client-side validation (backend does full validation)
      if (!coupon.active || (coupon.expirationDate && new Date(coupon.expirationDate) < new Date())) {
        alert('Coupon is not active or has expired.');
        setAppliedCoupon(null);
        setDiscountAmount(0);
        return;
      }
      if (coupon.maxUses !== null && coupon.timesUsed >= coupon.maxUses) {
        alert('Coupon has reached its usage limit.');
        setAppliedCoupon(null);
        setDiscountAmount(0);
        return;
      }
      if (coupon.minPurchaseAmount && calculateTotal() < coupon.minPurchaseAmount) {
        alert(`Minimum purchase amount of ${coupon.minPurchaseAmount} not met.`);
        setAppliedCoupon(null);
        setDiscountAmount(0);
        return;
      }

      setAppliedCoupon(coupon);
      alert('Coupon applied successfully!');
    } catch (error) {
      console.error('Error applying coupon:', error);
      alert(error.message || 'Failed to apply coupon.');
      setAppliedCoupon(null);
      setDiscountAmount(0);
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
      couponCode: appliedCoupon ? appliedCoupon.code : null,
    };

    try {
      await createOrder(orderRequest);
      setCheckoutStatus('success');
      setTimeout(() => {
        setCart([]);
        setAppliedCoupon(null);
        setDiscountAmount(0);
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
            <span>Subtotal</span>
            <span>{calculateTotal().toLocaleString()}.-</span>
          </div>
          <div className="coupon-section">
            <input
              type="text"
              placeholder="Enter coupon code"
              value={couponCode}
              onChange={(e) => setCouponCode(e.target.value)}
              disabled={appliedCoupon !== null}
            />
            <button onClick={handleApplyCoupon} disabled={appliedCoupon !== null}>Apply</button>
          </div>
          {appliedCoupon && (
            <div className="summary-row discount-row">
              <span>Discount ({appliedCoupon.code})</span>
              <span>-{discountAmount.toLocaleString()}.-</span>
            </div>
          )}
          <div className="summary-row">
            <span>Shipping</span>
            <span>Free</span>
          </div>
          <hr />
          <div className="summary-total">
            <span>Total</span>
            <span>{getTotalWithDiscount().toLocaleString()}.-</span>
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
        <Link to="/home/products">←</Link>
      </div>
    </div>
  );
};

export default Cart;
