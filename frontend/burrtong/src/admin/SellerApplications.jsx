import React from 'react';

const SellerApplications = () => {
  return (
    <div>
      <h2>Seller Applications</h2>
      <div data-test="application-row">
        <span>Seller Name</span>
        <button data-test="approve-seller-button">Approve</button>
        <button data-test="reject-seller-button">Reject</button>
      </div>
    </div>
  );
};

export default SellerApplications;
