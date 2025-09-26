import React, { useState } from "react";
import { Routes, Route } from "react-router-dom";
import Navbar from "./Navbar.jsx";
import HomePage from "./HomePage.jsx";
import Products from "./Products.jsx";
import ProductDetail from "./ProductDetail.jsx";
import Cart from "./Cart.jsx";
import CheckoutPage from "./CheckoutPage.jsx";
import OrderHistoryPage from "./OrderHistoryPage.jsx";
import ContactPage from "./ContactPage.jsx";
import Dashboard from "../../admin/Dashboard.jsx";
import SellerApplications from "../../admin/SellerApplications.jsx";
import AdminProducts from "../../admin/AdminProducts.jsx";
import AdminOrders from "../../admin/AdminOrders.jsx";
import AdminUsers from "../../admin/AdminUsers.jsx";
import AdminSupportTickets from "../../admin/AdminSupportTickets.jsx";
import SellerDashboard from "../../seller/SellerDashboard.jsx";
import SellerProducts from "../../seller/SellerProducts.jsx";
import SellerOrders from "../../seller/SellerOrders.jsx";
import SellerInventory from "../../seller/SellerInventory.jsx";
import SellerProfile from "../../seller/SellerProfile.jsx";

// สไตล์: ให้ Navbar.css มาทีหลังสุดเพื่อครอบสไตล์
import "../css/index.css";   // base reset
import "../css/App.css";     // layout/section
import "../css/Navbar.css";  // ควรมาทีหลัง

function App() {
  const [cart, setCart] = useState([]);

  return (
    <>
      {/* Navbar อยู่นอก container เพื่อคงที่ทุกหน้าใน /home */}
      <Navbar />

      <div className="container">
        <Routes>
          {/* User Routes */}
          <Route path="" element={<HomePage />} />
          <Route path="products" element={<Products />} />
          <Route
            path="products/:productId"
            element={<ProductDetail cart={cart} setCart={setCart} />}
          />
          <Route
            path="cart"
            element={<Cart cart={cart} setCart={setCart} />}
          />
          <Route path="checkout" element={<CheckoutPage cart={cart} />} />
          <Route path="orders" element={<OrderHistoryPage />} />
          <Route path="contact" element={<ContactPage />} />

          {/* Admin Routes */}
          <Route path="/admin/dashboard" element={<Dashboard />} />
          <Route path="/admin/seller-applications" element={<SellerApplications />} />
          <Route path="/admin/products" element={<AdminProducts />} />
          <Route path="/admin/orders" element={<AdminOrders />} />
          <Route path="/admin/users" element={<AdminUsers />} />
          <Route path="/admin/support-tickets" element={<AdminSupportTickets />} />

          {/* Seller Routes */}
          <Route path="/seller/dashboard" element={<SellerDashboard />} />
          <Route path="/seller/products" element={<SellerProducts />} />
          <Route path="/seller/orders" element={<SellerOrders />} />
          <Route path="/seller/inventory" element={<SellerInventory />} />
          <Route path="/seller/profile" element={<SellerProfile />} />
        </Routes>
      </div>
    </>
  );
}

export default App;
