import React from "react";
import "./App.css";

function App() {
  return (
    <div className="container">
      {/* Navbar */}
      <nav className="navbar">
        <div className="logo">Burtong</div>
        <div className="nav-links">
          <a href="#">Home</a>
          <a href="#">Products</a>
          <a href="#">Shopping Cart</a>
          <a href="#">Sign in</a>
          <button className="signup-btn">Sign Up</button>
        </div>
      </nav>

      {/* Search Bar */}
      <div className="search-container">
        <input type="text" placeholder="search" className="search-box" />
      </div>

      {/* Hero Section */}
      <section className="hero">
        <div className="box red"></div>
        <div className="box blue"></div>
        <div className="box green"></div>
      </section>

      {/* Hot Products */}
      <section className="hot-products">
        <h2>Hot Products</h2>
        <div className="product-grid">
          <div className="product purple">Coming Soon</div>
          <div className="product yellow">Nike Team Hustle D11</div>
          <div className="product pink">NANYANG</div>
        </div>
      </section>
    </div>
  );
}

export default App;
