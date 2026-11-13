import React, { useEffect, useState } from 'react';
import {
  BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend,
  PieChart, Pie, Cell, ResponsiveContainer
} from 'recharts';
import api from '../api/axios';
import Navbar from '../components/Navbar';

// Colors for the category pie chart slices
const PIE_COLORS = ['#3b82f6', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6', '#06b6d4', '#f97316'];

/**
 * Dashboard page — the first page the user sees after login.
 *
 * Displays:
 * 1. Summary cards: Total Income, Total Expense, Balance
 * 2. Bar chart: Monthly income vs expense (last 6 months)
 * 3. Pie chart: Spending by category
 *
 * All data comes from /api/analytics endpoints.
 */
export default function Dashboard() {
  const [summary, setSummary]     = useState(null);
  const [monthly, setMonthly]     = useState([]);
  const [categories, setCategories] = useState([]);
  const [loading, setLoading]     = useState(true);
  const [error, setError]         = useState('');

  useEffect(() => {
    // Fetch all dashboard data in parallel for speed
    const fetchAll = async () => {
      try {
        const [sumRes, monRes, catRes] = await Promise.all([
          api.get('/api/analytics/summary'),
          api.get('/api/analytics/monthly?months=6'),
          api.get('/api/analytics/categories'),
        ]);
        setSummary(sumRes.data);
        setMonthly(monRes.data);
        setCategories(catRes.data);
      } catch (err) {
        setError('Could not load dashboard data. Make sure all services are running.');
      } finally {
        setLoading(false);
      }
    };
    fetchAll();
  }, []);

  // Format numbers as Indian Rupees (₹1,00,000 format)
  const formatINR = (amount) =>
    new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR', maximumFractionDigits: 0 }).format(amount);

  if (loading) return <><Navbar /><div className="p-10 text-center text-gray-500">Loading dashboard...</div></>;
  if (error)   return <><Navbar /><div className="p-10 text-center text-red-500">{error}</div></>;

  const balance = (summary?.totalIncome || 0) - (summary?.totalExpense || 0);

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />
      <main className="max-w-7xl mx-auto px-4 py-6">
        <h2 className="text-2xl font-bold text-gray-800 mb-6">📊 Dashboard</h2>

        {/* ── Summary Cards ── */}
        <div className="grid grid-cols-1 sm:grid-cols-3 gap-4 mb-8">
          <SummaryCard
            title="Total Income"
            value={formatINR(summary?.totalIncome || 0)}
            color="green"
            icon="📈"
          />
          <SummaryCard
            title="Total Expense"
            value={formatINR(summary?.totalExpense || 0)}
            color="red"
            icon="📉"
          />
          <SummaryCard
            title="Balance"
            value={formatINR(balance)}
            color={balance >= 0 ? 'blue' : 'orange'}
            icon="💰"
          />
        </div>

        {/* ── Charts Row ── */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          {/* Monthly Bar Chart */}
          <div className="bg-white rounded-xl shadow p-5">
            <h3 className="font-semibold text-gray-700 mb-4">Monthly Income vs Expense</h3>
            {monthly.length === 0 ? (
              <p className="text-gray-400 text-sm text-center py-8">No monthly data yet. Add transactions!</p>
            ) : (
              <ResponsiveContainer width="100%" height={260}>
                <BarChart data={monthly} margin={{ top: 5, right: 10, left: 10, bottom: 5 }}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="month" tick={{ fontSize: 12 }} />
                  <YAxis tick={{ fontSize: 11 }} tickFormatter={(v) => `₹${(v/1000).toFixed(0)}k`} />
                  <Tooltip formatter={(v) => formatINR(v)} />
                  <Legend />
                  <Bar dataKey="income"  name="Income"  fill="#10b981" radius={[4,4,0,0]} />
                  <Bar dataKey="expense" name="Expense" fill="#ef4444" radius={[4,4,0,0]} />
                </BarChart>
              </ResponsiveContainer>
            )}
          </div>

          {/* Category Pie Chart */}
          <div className="bg-white rounded-xl shadow p-5">
            <h3 className="font-semibold text-gray-700 mb-4">Spending by Category</h3>
            {categories.length === 0 ? (
              <p className="text-gray-400 text-sm text-center py-8">No expense data yet.</p>
            ) : (
              <ResponsiveContainer width="100%" height={260}>
                <PieChart>
                  <Pie
                    data={categories}
                    dataKey="amount"
                    nameKey="category"
                    cx="50%"
                    cy="50%"
                    outerRadius={100}
                    label={({ category, percent }) => `${category} ${(percent * 100).toFixed(0)}%`}
                  >
                    {categories.map((_, i) => (
                      <Cell key={i} fill={PIE_COLORS[i % PIE_COLORS.length]} />
                    ))}
                  </Pie>
                  <Tooltip formatter={(v) => formatINR(v)} />
                </PieChart>
              </ResponsiveContainer>
            )}
          </div>
        </div>
      </main>
    </div>
  );
}

/** Reusable summary card component */
function SummaryCard({ title, value, color, icon }) {
  const colorMap = {
    green:  'bg-green-50  text-green-700  border-green-200',
    red:    'bg-red-50    text-red-700    border-red-200',
    blue:   'bg-blue-50   text-blue-700   border-blue-200',
    orange: 'bg-orange-50 text-orange-700 border-orange-200',
  };
  return (
    <div className={`rounded-xl border p-5 ${colorMap[color]}`}>
      <div className="text-3xl mb-2">{icon}</div>
      <div className="text-sm font-medium opacity-70">{title}</div>
      <div className="text-2xl font-bold mt-1">{value}</div>
    </div>
  );
}

