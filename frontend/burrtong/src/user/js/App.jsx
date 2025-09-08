import React from "react";
import { Routes, Route } from "react-router-dom";
import Navbar from "./Navbar.jsx";
import HomePage from "./HomePage.jsx";
import Products from "./Products.jsx";
import ProductDetail from "./ProductDetail.jsx";
import "../css/App.css"; // Corrected path

function App() {
  return (
    <div className="container">
      <Navbar />
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/products" element={<Products />} />
        <Route path="/products/:productId" element={<ProductDetail />} />
      </Routes>
    </div>
  );
}

export default App;