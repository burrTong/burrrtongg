import React from 'react';

const Dashboard = () => {
  return (
    <div>
      <h2>Admin Dashboard</h2>
      <div data-test="total-sellers-widget">Total Sellers: 10</div>
      <div data-test="total-customers-widget">Total Customers: 100</div>
      <div data-test="total-sales-widget">Total Sales: $5000</div>
      <div data-test="top-products-widget">Top Products</div>
    </div>
  );
};

export default Dashboard;
