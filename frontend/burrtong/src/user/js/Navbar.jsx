import React, { useEffect, useRef, useState } from "react";
import "../css/Navbar.css";
import { Link, useNavigate } from "react-router-dom";

function Navbar() {
  const [open, setOpen] = useState(false);
  const menuRef = useRef(null);
  const navigate = useNavigate();

  useEffect(() => {
    const handleClickOutside = (e) => {
      if (menuRef.current && !menuRef.current.contains(e.target)) {
        setOpen(false);
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  const handleLogout = () => {
    // TODO: เคลียร์ token/session ถ้ามี
    navigate("/"); // กลับไปหน้า Login
  };

  return (
    <nav className="navbar">
      {/* Logo */}
      <div className="logo">Burtong</div>

      {/* Search Bar */}
      <div className="search-container">
        <button className="search-btn" aria-label="Search">
          <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" fill="currentColor" viewBox="0 0 16 16">
            <path d="M11.742 10.344a6.5 6.5 0 1 0-1.397 
                     1.398h-.001l3.85 3.85a1 1 0 0 0 
                     1.415-1.414l-3.85-3.85zm-5.242 
                     1.656a5.5 5.5 0 1 1 0-11 
                     5.5 5.5 0 0 1 0 11z" />
          </svg>
        </button>
        <input type="text" placeholder="search" className="search-box" />
      </div>

      {/* Navigation Links */}
      <div className="nav-links">
        <Link to="/home">Home</Link>
        <Link to="/home/products">Products</Link>
        <Link to="/home/cart">Shopping Cart</Link>
      </div>

      {/* Avatar */}
      <div className="avatar-wrapper" ref={menuRef}>
        <div
          className="avatar"
          role="button"
          tabIndex={0}
          onClick={() => setOpen((v) => !v)}
          onKeyDown={(e) => {
            if (e.key === "Enter" || e.key === " ") setOpen((v) => !v);
          }}
        >
          <span>B</span>
        </div>

        {open && (
          <div className="dropdown" role="menu">
            <div className="dropdown-item dropdown-label">Acc</div>
            <button className="dropdown-item danger" onClick={handleLogout}>
              Log out
            </button>
          </div>
        )}
      </div>
    </nav>
  );
}

export default Navbar;
