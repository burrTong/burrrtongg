import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Link } from 'react-router-dom';
import { login } from '../api/authApi';
import '../css/LoginPage.css';

const LoginPage = () => {
  const navigate = useNavigate();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    if (!email || !password) {
      setError('Please enter both email and password.');
      return;
    }

    try {
      const userData = await login(email, password);
      localStorage.setItem('username', email);

      if (userData.role === 'ADMIN') {
        navigate('/admin');
      } else {
        navigate('/home');
      }
    } catch (err) {
      setError(err.message || 'An error occurred during login.');
    }
  };

  return (
    <div className="login-page" data-test="login-page">
      <div className="login-container">
        <div className="logo-container">
          <img src="/vite.svg" alt="BURTONG Logo" className="logo" />
          <h1>BURTONG</h1>
        </div>
        <h2>Login</h2>
        <form onSubmit={handleSubmit} data-test="login-form">
          {error && <p className="error-message" data-test="login-error">{error}</p>}
          <div className="input-group">
            <label htmlFor="email">Email</label>
            <input 
              type="email" 
              id="email" 
              data-test="login-username"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
            />
          </div>
          <div className="input-group">
            <label htmlFor="password">Password</label>
            <input 
              type="password" 
              id="password"
              data-test="login-password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />
          </div>
          <div className="options">
            <label>
              <input type="checkbox" /> Remember me
            </label>
            <a href="#">Forgot Password</a>
          </div>
          <button type="submit" className="login-btn" data-test="login-submit">Login</button>
        </form>
        <div className="signup-link">
         <p>Not a member? <Link to="/signup">SIGN UP!</Link></p>
        </div>
      </div>
    </div>
  );
};

export default LoginPage;
