
import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import '../css/ProductList.css'; // Reusing the same CSS for consistency
import BurtongLogo from '../../assets/Burtong_logo.png';
import { getWeeklyStockReport } from '../api/productApi';

function WeeklyStockReport() {
  const [reportData, setReportData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  const username = localStorage.getItem('username') || 'Admin';

  useEffect(() => {
    const fetchWeeklyStockReport = async () => {
      try {
        setLoading(true);
        const data = await getWeeklyStockReport();
        setReportData(data);
        setError(null);
      } catch (err) {
        setError(err.message);
        setReportData([]);
      } finally {
        setLoading(false);
      }
    };

    fetchWeeklyStockReport();
  }, []);

  const handleLogout = () => {
    localStorage.clear();
    navigate('/admin/login');
  };

  return (
    <div className="dashboard">
      <aside className="sidebar">
        <div>
          <div className="sidebar-logo">
            <Link to="/admin/products">
              <img src={BurtongLogo} alt="Burtong Logo" />
            </Link>
          </div>
          <ul>
            <li><Link to="/admin/products">📦Products' List</Link></li>
            <li><Link to="/admin/orders">📋Orders' List</Link></li>
            <li><Link to="/admin/coupons">🎟️Coupons' List</Link></li>
            <li><Link to="/admin/categories">📂Categories' List</Link></li>
            <li><Link to="/admin/stock-report">📊Weekly Stock Report</Link></li>
          </ul>
        </div>
        <div className="sidebar-footer">
          <div className="user-info">
            <span className="username">{username}</span>
          </div>
          <button onClick={handleLogout} className="logout-btn">Logout</button>
        </div>
      </aside>

      <main className="main">
        <header className="main-header">
          <h1 className="main-title">Weekly Stock Report</h1>
        </header>

        {loading && <p>Loading report...</p>}
        {error && <p className="error-message">Error: {error}</p>}

        {!loading && !error && (
          <table className="product-table"> {/* Reusing product-table style */}
            <thead>
              <tr>
                <th>Product ID</th>
                <th>Product Name</th>
                <th>Initial Stock</th>
                <th>Sold This Week</th>
                <th>Remaining Stock</th>
              </tr>
            </thead>
            <tbody>
              {reportData.map((item) => (
                <tr key={item.id}>
                  <td>{item.id}</td>
                  <td>{item.productName}</td>
                  <td>{item.initialStock}</td>
                  <td>{item.sold}</td>
                  <td>{item.remainingStock}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </main>
    </div>
  );
}

export default WeeklyStockReport;
