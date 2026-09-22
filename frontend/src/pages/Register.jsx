import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Bot, Mail, Lock, UserPlus } from 'lucide-react';

export const Register = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const { register } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    if (password !== confirmPassword) {
      setError('Passwords do not match');
      return;
    }

    if (password.length < 6) {
      setError('Password must be at least 6 characters long');
      return;
    }

    setLoading(true);

    try {
      await register(email, password);
      navigate('/');
    } catch (err) {
      if (err.response) {
        if (err.response.status === 405) {
          setError('Backend API service is not accessible at this address (HTTP 405). Please ensure the backend is running.');
        } else {
          setError(err.response.data?.message || `Server returned error (${err.response.status}): Registration failed.`);
        }
      } else if (err.request) {
        setError('Cannot connect to backend server. Please verify the backend service is running.');
      } else {
        setError(err.message || 'Registration failed. Please try again.');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ maxWidth: 440, margin: '60px auto', padding: '0 16px' }}>
      <div className="glass-card" style={{ padding: 36 }}>
        <div style={{ textAlign: 'center', marginBottom: 32 }}>
          <div style={{ display: 'inline-flex', padding: 12, borderRadius: '50%', background: 'rgba(6, 182, 212, 0.15)', marginBottom: 12 }}>
            <Bot size={36} color="#06b6d4" />
          </div>
          <h2>Create an Account</h2>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem', marginTop: 4 }}>
            Start practicing AI technical interviews in minutes
          </p>
        </div>

        {error && <div className="alert alert-error">{error}</div>}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label className="form-label" htmlFor="register-email">Email Address</label>
            <div style={{ position: 'relative' }}>
              <Mail size={18} color="var(--text-dim)" style={{ position: 'absolute', top: 14, left: 14 }} />
              <input
                id="register-email"
                type="email"
                className="form-input"
                style={{ paddingLeft: 42 }}
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="name@example.com"
                required
              />
            </div>
          </div>

          <div className="form-group">
            <label className="form-label" htmlFor="register-password">Password</label>
            <div style={{ position: 'relative' }}>
              <Lock size={18} color="var(--text-dim)" style={{ position: 'absolute', top: 14, left: 14 }} />
              <input
                id="register-password"
                type="password"
                className="form-input"
                style={{ paddingLeft: 42 }}
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="Minimum 6 characters"
                required
              />
            </div>
          </div>

          <div className="form-group">
            <label className="form-label" htmlFor="register-confirm-password">Confirm Password</label>
            <div style={{ position: 'relative' }}>
              <Lock size={18} color="var(--text-dim)" style={{ position: 'absolute', top: 14, left: 14 }} />
              <input
                id="register-confirm-password"
                type="password"
                className="form-input"
                style={{ paddingLeft: 42 }}
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                placeholder="Confirm password"
                required
              />
            </div>
          </div>

          <button
            type="submit"
            className="btn btn-primary btn-lg"
            style={{ width: '100%', marginTop: 10 }}
            disabled={loading}
          >
            {loading ? 'Creating Account...' : 'Register'}
            <UserPlus size={18} />
          </button>
        </form>

        <p style={{ textAlign: 'center', fontSize: '0.9rem', color: 'var(--text-muted)', marginTop: 24 }}>
          Already have an account?{' '}
          <Link to="/login" style={{ fontWeight: 600 }}>Sign in here</Link>
        </p>
      </div>
    </div>
  );
};
