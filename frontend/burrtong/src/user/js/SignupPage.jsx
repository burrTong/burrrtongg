import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import '../css/SignupPage.css';

function SignupPage() {
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    email: '',
    name: '',
    password: '',
    confirmPassword: '',
    phone: ''
  });

  const [errors, setErrors] = useState({}); // ✅ เก็บ error ของแต่ละช่อง

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
    setErrors({ ...errors, [e.target.name]: '' }); // ลบ error ทันทีที่พิมพ์ใหม่
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    let newErrors = {};

    if (!formData.email) newErrors.email = "Please enter your email";
    if (!formData.name) newErrors.name = "Please enter your name";
    if (!formData.password) newErrors.password = "Please enter your password";
    if (!formData.confirmPassword) {
      newErrors.confirmPassword = "Please enter your confirm password";
    } else if (formData.password !== formData.confirmPassword) {
      newErrors.confirmPassword = "Password and Confirm Password do not match";
    }
    if (!formData.phone) newErrors.phone = "Please enter your phone";

    setErrors(newErrors);

    if (Object.keys(newErrors).length === 0) {
      // (TODO: ส่งข้อมูลไป backend ถ้ามี)
      navigate("/login"); // ไปหน้า Login
    }
  };

  return (
    <div className="signup-container">
      <div className="signup-form">
        <img src="/src/assets/burrtong_logo.png" alt="Burrtong" className="logo" />
        <h2>Sign Up</h2>
        <form onSubmit={handleSubmit}>
          <div className="input-group">
            <label>Email</label>
            <input type="email" name="email" value={formData.email} onChange={handleChange} data-test="signup-username" />
            {errors.email && <p className="error">{errors.email}</p>}
          </div>
          <div className="input-group">
            <label>Name</label>
            <input type="text" name="name" value={formData.name} onChange={handleChange} />
            {errors.name && <p className="error">{errors.name}</p>}
          </div>
          <div className="input-group">
            <label>Password</label>
            <input type="password" name="password" value={formData.password} onChange={handleChange} data-test="signup-password" />
            {errors.password && <p className="error">{errors.password}</p>}
          </div>
          <div className="input-group">
            <label>Confirm Password</label>
            <input type="password" name="confirmPassword" value={formData.confirmPassword} onChange={handleChange} />
            {errors.confirmPassword && <p className="error">{errors.confirmPassword}</p>}
          </div>
          <div className="input-group">
            <label>Phone</label>
            <input type="tel" name="phone" value={formData.phone} onChange={handleChange} />
            {errors.phone && <p className="error">{errors.phone}</p>}
          </div>
          <button type="submit" className="signup-button" data-test="signup-submit">Sign up</button>
        </form>
        <p className="login-link">
          Already have an account? <Link to="/">Login</Link>
        </p>
      </div>
    </div>
  );
}

export default SignupPage;