import React from 'react';

const orders = [
  { id: 1, status: 'รอดำเนินการ' },
  { id: 2, status: 'จัดส่ง' },
];

const OrderHistoryPage = () => {
  return (
    <div>
      <h2>Order History</h2>
      {orders.map(order => (
        <div key={order.id} data-test="order-item">
          <span data-test="order-id">Order #{order.id}</span>
          <span data-test="order-status">{order.status}</span>
        </div>
      ))}
    </div>
  );
};

export default OrderHistoryPage;
