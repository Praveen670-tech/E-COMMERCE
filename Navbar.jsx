import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext.jsx';

const Navbar = () => {
  const { token, role, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <nav className="navbar">
      <Link to="/" className="nav-brand">
        🛒 E-Commerce Hub
      </Link>
      <ul className="nav-links">
        <li><Link to="/" className="nav-link">Home</Link></li>
        {!token ? (
          <>
            <li><Link to="/login" className="nav-link">Login</Link></li>
            <li><Link to="/signup" className="nav-link">Sign Up</Link></li>
          </>
        ) : (
          <>
            {role === 'VENDOR' && <li><Link to="/vendor" className="nav-link">Dashboard</Link></li>}
            {role === 'USER' && <li><Link to="/user" className="nav-link">Shop</Link></li>}
            {role === 'ADMIN' && <li><Link to="/admin" className="nav-link">Admin Panel</Link></li>}
            <li><Link to="/orders" className="nav-link">Orders</Link></li>
            <li><Link to="/settings" className="nav-link">Settings</Link></li>
            <li>
              <button onClick={handleLogout} className="nav-button">Logout</button>
            </li>
          </>
        )}
      </ul>
    </nav>
  );
};

export default Navbar;
