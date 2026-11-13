import React, { createContext, useContext, useState, useEffect } from 'react';
import api from '../api/axios';

/**
 * AuthContext provides global authentication state to the whole app.
 * It stores the JWT token and user profile in localStorage so the user
 * stays logged in even after a browser refresh.
 *
 * Usage: const { user, token, login, logout } = useAuth();
 */
const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  // Load token and user from localStorage on first render
  const [token, setToken] = useState(() => localStorage.getItem('token'));
  const [user, setUser] = useState(() => {
    try {
      return JSON.parse(localStorage.getItem('user'));
    } catch {
      return null;
    }
  });

  /**
   * login() is called after a successful POST /api/auth/login.
   * It saves the token + user to state AND localStorage.
   */
  const login = (tokenValue, userData) => {
    setToken(tokenValue);
    setUser(userData);
    localStorage.setItem('token', tokenValue);
    localStorage.setItem('user', JSON.stringify(userData));
  };

  /** logout() clears everything and redirects to /login */
  const logout = () => {
    setToken(null);
    setUser(null);
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  };

  return (
    <AuthContext.Provider value={{ token, user, login, logout, isAuthenticated: !!token }}>
      {children}
    </AuthContext.Provider>
  );
}

/** Custom hook for easy access to auth state in any component */
export function useAuth() {
  return useContext(AuthContext);
}

