import React from "react";

function Navbar() {
  return (
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
  );
}

export default Navbar;
