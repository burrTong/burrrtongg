import React from "react";
import Navbar from "./Navbar.jsx";
import "../css/App.css"; // Corrected path

function HomePage() {
  return (
    <>

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
    </>
  );
}

export default HomePage;