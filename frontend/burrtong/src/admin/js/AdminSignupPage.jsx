import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import '../../user/css/SignupPage.css';

function AdminSignupPage() {
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    email: '',
    password: '',
    confirmPassword: ''
  });

  const [errors, setErrors] = useState({});

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
    setErrors({ ...errors, [e.target.name]: '' });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    let newErrors = {};

    if (!formData.email) newErrors.email = "Please enter your email";
    if (!formData.password) newErrors.password = "Please enter your password";
    if (!formData.confirmPassword) {
      newErrors.confirmPassword = "Please enter your confirm password";
    } else if (formData.password !== formData.confirmPassword) {
      newErrors.confirmPassword = "Passwords do not match";
    }

    setErrors(newErrors);

    if (Object.keys(newErrors).length === 0) {
      // TODO: Implement admin registration logic
      console.log("Admin registration submitted:", formData);
      alert("Admin registration request sent!");
      navigate("/admin/login");
    }
  };

  return (
    <div className="signup-container">
      <div className="signup-form">
        <img src="/vite.svg" alt="Burrtong" className="logo" />
        <h2>Admin Sign Up</h2>
        <form onSubmit={handleSubmit}>
          <div className="input-group">
            <label>Email</label>
            <input type="email" name="email" value={formData.email} onChange={handleChange} />
            {errors.email && <p className="error">{errors.email}</p>}
          </div>
          <div className="input-group">
            <label>Password</label>
            <input type="password" name="password" value={formData.password} onChange={handleChange} />
            {errors.password && <p className="error">{errors.password}</p>}
          </div>
          <div className="input-group">
            <label>Confirm Password</label>
            <input type="password" name="confirmPassword" value={formData.confirmPassword} onChange={handleChange} />
            {errors.confirmPassword && <p className="error">{errors.confirmPassword}</p>}
          </div>
          <button type="submit" className="signup-button">Sign up</button>
        </form>
        <p className="login-link">
          Already have an admin account? <Link to="/admin/login">Login</Link>
        </p>
      </div>
    </div>
  );
}

export default AdminSignupPage;
