import React from 'react';

const SellerDashboard = () => {
  return (
    <div>
      <h2>Seller Dashboard</h2>
      <div data-test="total-sales-widget">Total Sales: $1000</div>
      <div data-test="inventory-summary-widget">Inventory Summary</div>
    </div>
  );
};

export default SellerDashboard;
