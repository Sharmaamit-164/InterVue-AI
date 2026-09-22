import React from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Bot, FileText, Briefcase, PlusCircle, LayoutDashboard, LogOut, User } from 'lucide-react';

export const Navbar = () => {
  const { user, logout, isAuthenticated } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  if (!isAuthenticated) {
    return (
      <nav className="navbar">
        <div className="navbar-inner">
          <Link to="/" className="brand-logo">
            <Bot size={28} color="#6366f1" />
            <span>InterVue AI</span>
          </Link>
          <div className="nav-links">
            <Link to="/login" className="btn btn-secondary btn-sm">Login</Link>
            <Link to="/register" className="btn btn-primary btn-sm">Get Started</Link>
          </div>
        </div>
      </nav>
    );
  }

  const isActive = (path) => location.pathname === path;

  return (
    <nav className="navbar">
      <div className="navbar-inner">
        <Link to="/" className="brand-logo">
          <Bot size={28} color="#6366f1" />
          <span>InterVue AI</span>
        </Link>

        <div className="nav-links">
          <Link to="/" className={`nav-link ${isActive('/') ? 'active' : ''}`}>
            <LayoutDashboard size={18} style={{ display: 'inline', verticalAlign: 'middle', marginRight: 6 }} />
            Dashboard
          </Link>
          <Link to="/resumes" className={`nav-link ${isActive('/resumes') ? 'active' : ''}`}>
            <FileText size={18} style={{ display: 'inline', verticalAlign: 'middle', marginRight: 6 }} />
            Resumes
          </Link>
          <Link to="/jobs" className={`nav-link ${isActive('/jobs') ? 'active' : ''}`}>
            <Briefcase size={18} style={{ display: 'inline', verticalAlign: 'middle', marginRight: 6 }} />
            Jobs
          </Link>
          <Link to="/interviews/new" className="btn btn-primary btn-sm">
            <PlusCircle size={16} />
            Start Interview
          </Link>

          <div style={{ display: 'flex', alignItems: 'center', gap: 12, marginLeft: 16, borderLeft: '1px solid var(--border-subtle)', paddingLeft: 16 }}>
            <span style={{ fontSize: '0.85rem', color: 'var(--text-muted)', display: 'flex', alignItems: 'center', gap: 6 }}>
              <User size={16} />
              {user?.email}
            </span>
            <button onClick={handleLogout} className="btn btn-secondary btn-sm" title="Log out">
              <LogOut size={16} />
            </button>
          </div>
        </div>
      </div>
    </nav>
  );
};
