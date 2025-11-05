import React, { useState, useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";
import { getAllCoupons, createCoupon, updateCoupon, deleteCoupon } from "../api/couponApi";
import NewCouponModal from "./NewCouponModal";
import EditCouponModal from "./EditCouponModal";
import '../css/ProductList.css'; // Reusing the same CSS for consistency
import BurtongLogo from '../../assets/Burtong_logo.png';

function CouponList() {
  const [coupons, setCoupons] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [isNewModalOpen, setIsNewModalOpen] = useState(false);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [editingCoupon, setEditingCoupon] = useState(null);
  const navigate = useNavigate();

  const username = localStorage.getItem('username') || 'Admin';

  const fetchCoupons = async () => {
    try {
      setLoading(true);
      const data = await getAllCoupons();
      setCoupons(data);
      setError(null);
    } catch (err) {
      setError(err.message);
      setCoupons([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchCoupons();
  }, []);

  const handleLogout = () => {
    localStorage.clear();
    navigate('/admin/login');
  };

  const handleCreateCoupon = async (couponData) => {
    try {
      const newCoupon = await createCoupon(couponData);
      setCoupons([newCoupon, ...coupons]);
      setIsNewModalOpen(false);
    } catch (err) {
      console.error("Failed to create coupon:", err);
      alert(err.message);
    }
  };

  const handleEditCoupon = (coupon) => {
    setEditingCoupon(coupon);
    setIsEditModalOpen(true);
  };

  const handleUpdateCoupon = async (couponId, couponData) => {
    try {
      const updatedCoupon = await updateCoupon(couponId, couponData);
      setCoupons(coupons.map(c => c.id === couponId ? updatedCoupon : c));
      setIsEditModalOpen(false);
      setEditingCoupon(null);
    } catch (err) {
      console.error("Failed to update coupon:", err);
      alert(err.message);
    }
  };

  const handleDeleteCoupon = async (couponId) => {
    if (window.confirm(`Are you sure you want to delete coupon with ID: ${couponId}?`)) {
      try {
        await deleteCoupon(couponId);
        setCoupons(coupons.filter(coupon => coupon.id !== couponId));
        alert('Coupon deleted successfully!');
      } catch (err) {
        console.error("Failed to delete coupon:", err);
        alert(err.message);
      }
    }
  };

  const renderCouponRow = (coupon) => (
    <tr key={coupon.id}>
      <td>{coupon.id}</td>
      <td>{coupon.code}</td>
      <td>{coupon.discountType}</td>
      <td>{coupon.discountType === 'FIXED' ? `${coupon.discountValue}.-` : `${coupon.discountValue}%`}</td>
      <td>{new Date(coupon.expirationDate).toLocaleDateString()}</td>
      <td>
        <span className={`status ${coupon.active ? 'status-available' : 'status-out-of-stock'}`}>
          {coupon.active ? 'Active' : 'Inactive'}
        </span>
      </td>
      <td>
        <button className="edit-product-btn" onClick={() => handleEditCoupon(coupon)}>Edit</button>
        <button className="delete-product-btn" onClick={() => handleDeleteCoupon(coupon.id)}>Delete</button>
      </td>
    </tr>
  );

  return (
    <div className="dashboard">
      <aside className="sidebar">
        <div>
          <div className="sidebar-logo">
            <Link to="/admin/products">
              <img src={BurtongLogo} alt="Burtong Logo" />
            </Link>
          </div>
          <ul>
            <li><Link to="/admin/products">📦Products’ List</Link></li>
            <li><Link to="/admin/orders">📋Orders’ List</Link></li>
            <li><Link to="/admin/coupons">🎟️Coupons’ List</Link></li>
          </ul>
        </div>
        <div className="sidebar-footer">
          <div className="user-info">
            <span className="username">{username}</span>
          </div>
          <button onClick={handleLogout} className="logout-btn">Logout</button>
        </div>
      </aside>

      <main className="main">
        <header className="main-header">
          <div className="title-group">
            <h1 className="main-title">Coupons’ List</h1>
            <p className="breadcrumb">Dashboard / Coupons’ List</p>
          </div>
          <button onClick={() => setIsNewModalOpen(true)} className="new-product-btn">+ New Coupon</button>
        </header>

        {loading && <p>Loading coupons...</p>}
        {error && <p className="error-message">Error: {error}</p>}

        {!loading && !error && (
          <table className="product-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Code</th>
                <th>Type</th>
                <th>Value</th>
                <th>Expires</th>
                <th>Status</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {coupons.map(renderCouponRow)}
            </tbody>
          </table>
        )}
      </main>

      <NewCouponModal 
        isOpen={isNewModalOpen} 
        onClose={() => setIsNewModalOpen(false)} 
        onCreateCoupon={handleCreateCoupon}
      />

      {editingCoupon && (
        <EditCouponModal
          isOpen={isEditModalOpen}
          onClose={() => {
            setIsEditModalOpen(false);
            setEditingCoupon(null);
          }}
          coupon={editingCoupon}
          onUpdateCoupon={handleUpdateCoupon}
        />
      )}
    </div>
  );
}

export default CouponList;
