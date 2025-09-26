import React from 'react';

const AdminUsers = () => {
  return (
    <div>
      <h2>Admin Users</h2>
      <div data-test="user-row">
        <span data-test="user-role">Customer</span>
        <span data-test="user-status-display">Active</span>
        <button data-test="disable-user-button">Disable</button>
      </div>
    </div>
  );
};

export default AdminUsers;
