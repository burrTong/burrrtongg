import React from 'react';

const SellerOrders = () => {
  return (
    <div>
      <h2>Seller Orders</h2>
      <div data-test="order-row">
        <span data-test="order-id">Order #1</span>
        <select data-test="order-status-select">
          <option>รอดำเนินการ</option>
          <option>กำลังจัดส่ง</option>
        </select>
        <button data-test="update-status-button">Update</button>
        <span data-test="order-status-display">รอดำเนินการ</span>
      </div>
    </div>
  );
};

export default SellerOrders;
