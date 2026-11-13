import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

/**
 * ProtectedRoute wraps pages that require the user to be logged in.
 * If no token exists, it redirects to /login automatically.
 * Usage: <Route element={<ProtectedRoute />}> ... </Route>
 */
export default function ProtectedRoute({ children }) {
  const { isAuthenticated } = useAuth();

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  return children;
}

