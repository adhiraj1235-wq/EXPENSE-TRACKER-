import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import ProtectedRoute from './components/ProtectedRoute';

// Pages
import Login           from './pages/Login';
import Register        from './pages/Register';
import Dashboard       from './pages/Dashboard';
import Transactions    from './pages/Transactions';
import TransactionForm from './pages/TransactionForm';
import Budget          from './pages/Budget';
import Analytics       from './pages/Analytics';
import Reports         from './pages/Reports';

/**
 * App — root component.
 *
 * Sets up:
 * - AuthProvider: makes login/logout state available globally
 * - React Router: maps URL paths to page components
 * - ProtectedRoute: wraps pages that need authentication
 *
 * Public routes:  /login, /register
 * Protected routes: /, /transactions, /transactions/new, /transactions/edit/:id,
 *                   /budget, /analytics, /reports
 */
function App() {
  return (
    <AuthProvider>
      <Router>
        <Routes>
          {/* Public routes */}
          <Route path="/login"    element={<Login />} />
          <Route path="/register" element={<Register />} />

          {/* Protected routes — redirect to /login if not authenticated */}
          <Route path="/" element={
            <ProtectedRoute><Dashboard /></ProtectedRoute>
          } />
          <Route path="/transactions" element={
            <ProtectedRoute><Transactions /></ProtectedRoute>
          } />
          <Route path="/transactions/new" element={
            <ProtectedRoute><TransactionForm /></ProtectedRoute>
          } />
          <Route path="/transactions/edit/:id" element={
            <ProtectedRoute><TransactionForm /></ProtectedRoute>
          } />
          <Route path="/budget" element={
            <ProtectedRoute><Budget /></ProtectedRoute>
          } />
          <Route path="/analytics" element={
            <ProtectedRoute><Analytics /></ProtectedRoute>
          } />
          <Route path="/reports" element={
            <ProtectedRoute><Reports /></ProtectedRoute>
          } />

          {/* Catch-all: redirect unknown paths to dashboard */}
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </Router>
    </AuthProvider>
  );
}

export default App;
