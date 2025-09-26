import React from 'react';

const SellerProfile = () => {
  return (
    <div>
      <h2>Seller Profile</h2>
      <span data-test="profile-store-name-display">My Store</span>
      <button data-test="edit-profile-button">Edit Profile</button>
      <input data-test="profile-store-name-input" />
      <button data-test="save-profile-button">Save</button>
    </div>
  );
};

export default SellerProfile;
