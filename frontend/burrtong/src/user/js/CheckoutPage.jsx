import React from 'react';

const CheckoutPage = ({ cart }) => {
  const getTotal = () => {
    return cart.reduce((total, item) => total + item.price * item.quantity, 0);
  };

  return (
    <div data-test="order-summary">
      <h2>Order Summary</h2>
      {cart.map(item => (
        <div key={item.id}>
          <span>{item.name}</span>
          <span>{item.quantity} x {item.price}</span>
        </div>
      ))}
      <hr />
      <div data-test="summary-total">Total: {getTotal()}</div>
    </div>
  );
};

export default CheckoutPage;
