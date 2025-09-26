import React from "react";
import ReactDOM from "react-dom/client";
import { BrowserRouter, Routes, Route } from "react-router-dom";

// Page Components
import App from "./App.jsx";
import LoginPage from "./LoginPage.jsx";
import SignupPage from "./SignupPage.jsx";
import ProductList from "../../admin/ProductList.jsx";
import OrderList from "../../admin/OrderList.jsx";

// CSS
import "../css/index.css";

ReactDOM.createRoot(document.getElementById("root")).render(
  <React.StrictMode>
    <BrowserRouter>
      <Routes>
        {/* Public Routes */}
        <Route path="/" element={<LoginPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/signup" element={<SignupPage />} />

        {/* User Protected Routes */}
        <Route path="/home/*" element={<App />} />

        {/* Admin Protected Routes */}
        <Route path="/admin" element={<ProductList />} />
        <Route path="/admin/products" element={<ProductList />} />
        <Route path="/admin/orders" element={<OrderList />} />
      </Routes>
    </BrowserRouter>
  </React.StrictMode>
);
