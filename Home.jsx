import React from 'react';
import { Link } from 'react-router-dom';

const Home = () => {
  return (
    <div className="home-page">
      <section className="hero">
        <h1>Welcome to E-Commerce Hub</h1>
        <p>
          Experience the future of online shopping. Discover thousands of products from verified vendors 
          worldwide with secure role-based authentication and multiple payment options.
        </p>
        <div className="hero-btns">
          <Link to="/signup" className="btn-white">Get Started</Link>
          <Link to="/login" className="btn-primary">Learn More</Link>
        </div>
      </section>

      <section className="features-section">
        <h2>Why Choose Our E-Commerce Hub?</h2>
        <div className="features-grid">
          <div className="feature-card">
            <span className="feature-icon">📊</span>
            <h3>Advanced Analysis</h3>
            <p>Our platform uses cutting-edge algorithms to recommend products based on your shopping patterns and preferences.</p>
          </div>
          <div className="feature-card">
            <span className="feature-icon">⚡</span>
            <h3>Instant Results</h3>
            <p>Get real-time product availability, instant checkout options, and lightning-fast search results across all categories.</p>
          </div>
          <div className="feature-card">
            <span className="feature-icon">🛡️</span>
            <h3>Secure & Private</h3>
            <p>Your data is encrypted and protected with role-based access control. We never share your personal information with third parties.</p>
          </div>
          <div className="feature-card">
            <span className="feature-icon">📱</span>
            <h3>Easy to Use</h3>
            <p>Simple interface designed for everyone. No technical knowledge required to start buying or selling products.</p>
          </div>
        </div>
      </section>
    </div>
  );
};

export default Home;
