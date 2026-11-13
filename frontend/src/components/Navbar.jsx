import React from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

/**
 * Navbar — top navigation bar shown on all protected pages.
 * Shows the app name, navigation links, and a logout button.
 * Active link is highlighted using React Router's useLocation hook.
 */
export default function Navbar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  // Helper: returns "font-semibold text-white" if the path matches current route
  const isActive = (path) =>
    location.pathname === path ? 'bg-blue-700 text-white' : 'text-blue-100 hover:bg-blue-600';

  const navLinks = [
    { to: '/',             label: 'Dashboard'    },
    { to: '/transactions', label: 'Transactions' },
    { to: '/budget',       label: 'Budget'       },
    { to: '/analytics',    label: 'Analytics'    },
    { to: '/reports',      label: 'Reports'      },
  ];

  return (
    <nav className="bg-blue-800 shadow-md">
      <div className="max-w-7xl mx-auto px-4 py-3 flex flex-wrap items-center justify-between gap-2">
        {/* Brand */}
        <Link to="/" className="text-white text-xl font-bold tracking-tight">
          💰 Expense Tracker
        </Link>

        {/* Navigation Links */}
        <div className="flex flex-wrap gap-1">
          {navLinks.map(({ to, label }) => (
            <Link
              key={to}
              to={to}
              className={`px-3 py-1.5 rounded-md text-sm font-medium transition-colors ${isActive(to)}`}
            >
              {label}
            </Link>
          ))}
        </div>

        {/* User info + logout */}
        <div className="flex items-center gap-3">
          <span className="text-blue-200 text-sm hidden sm:block">
            👤 {user?.name || user?.email}
          </span>
          <button
            onClick={handleLogout}
            className="bg-red-500 hover:bg-red-600 text-white text-sm px-3 py-1.5 rounded-md transition-colors"
          >
            Logout
          </button>
        </div>
      </div>
    </nav>
  );
}

