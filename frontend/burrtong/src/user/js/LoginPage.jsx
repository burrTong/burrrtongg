import React from 'react';
import { useNavigate } from 'react-router-dom';
import { Link } from 'react-router-dom';
import '../css/LoginPage.css';

const LoginPage = () => {
  const navigate = useNavigate();

  const handleSubmit = (e) => {
    e.preventDefault();
    // TODO: check login logic ...
    navigate('/home'); // ✅ ไปหน้า Home หลัง login
  };

  return (
    <div className="login-page">
      <div className="login-container">
        <div className="logo-container">
          <img src="/vite.svg" alt="BURTONG Logo" className="logo" />
          <h1>BURTONG</h1>
        </div>
        <h2>Login</h2>
        <form onSubmit={handleSubmit}>
          <div className="input-group">
            <label htmlFor="email">Email</label>
            <input type="email" id="email" />
          </div>
          <div className="input-group">
            <label htmlFor="password">Password</label>
            <input type="password" id="password" />
          </div>
          <div className="options">
            <label>
              <input type="checkbox" /> Remember me
            </label>
            <a href="#">Forgot Password</a>
          </div>
          <button type="submit" className="login-btn">Login</button>
        </form>
        <div className="signup-link">
         <p>Not a member? <Link to="/signup">SIGN UP!</Link></p>
        </div>
      </div>
    </div>
  );
};

export default LoginPage;