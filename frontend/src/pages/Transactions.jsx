import React, { useEffect, useState, useCallback } from 'react';
import { Link } from 'react-router-dom';
import api from '../api/axios';
import Navbar from '../components/Navbar';

/**
 * Transactions page — lists all transactions with search, filter, and pagination.
 *
 * Filter options: free-text search, type (INCOME/EXPENSE), category, month (YYYY-MM),
 * min/max amount. All filters are sent as query params to GET /api/transactions.
 *
 * Shows an Edit button (links to /transactions/edit/:id) and a Delete button.
 */
export default function Transactions() {
  const [transactions, setTransactions] = useState([]);
  const [totalPages, setTotalPages]     = useState(0);
  const [loading, setLoading]           = useState(true);

  // Filter state — each field maps to a query parameter
  const [filters, setFilters] = useState({
    search: '', type: '', category: '', month: '',
    minAmount: '', maxAmount: '', page: 0, size: 10,
  });

  const CATEGORIES = ['Salary','Freelance','Housing','Groceries','Transport',
                      'Entertainment','Food','Utilities','Healthcare','Education','Other'];

  const fetchTransactions = useCallback(async () => {
    setLoading(true);
    try {
      // Build query string from non-empty filters only
      const params = {};
      Object.entries(filters).forEach(([k, v]) => { if (v !== '') params[k] = v; });
      const res = await api.get('/api/transactions', { params });
      setTransactions(res.data.content || []);
      setTotalPages(res.data.totalPages || 0);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  }, [filters]);

  useEffect(() => { fetchTransactions(); }, [fetchTransactions]);

  const handleFilterChange = (e) => {
    setFilters({ ...filters, [e.target.name]: e.target.value, page: 0 });
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Delete this transaction?')) return;
    try {
      await api.delete(`/api/transactions/${id}`);
      fetchTransactions(); // Refresh the list
    } catch (err) {
      alert('Delete failed: ' + (err.response?.data?.message || err.message));
    }
  };

  const formatINR = (amount) =>
    new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR', maximumFractionDigits: 0 }).format(amount);

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />
      <main className="max-w-7xl mx-auto px-4 py-6">
        <div className="flex justify-between items-center mb-5">
          <h2 className="text-2xl font-bold text-gray-800">📋 Transactions</h2>
          <Link
            to="/transactions/new"
            className="bg-blue-700 hover:bg-blue-800 text-white text-sm font-medium px-4 py-2 rounded-lg transition-colors"
          >
            + Add Transaction
          </Link>
        </div>

        {/* ── Filter Bar ── */}
        <div className="bg-white rounded-xl shadow p-4 mb-5 grid grid-cols-2 md:grid-cols-4 lg:grid-cols-6 gap-3">
          <input
            name="search"
            value={filters.search}
            onChange={handleFilterChange}
            placeholder="Search..."
            className="col-span-2 border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-400"
          />
          <select name="type" value={filters.type} onChange={handleFilterChange}
            className="border border-gray-300 rounded-lg px-3 py-2 text-sm">
            <option value="">All Types</option>
            <option value="INCOME">Income</option>
            <option value="EXPENSE">Expense</option>
          </select>
          <select name="category" value={filters.category} onChange={handleFilterChange}
            className="border border-gray-300 rounded-lg px-3 py-2 text-sm">
            <option value="">All Categories</option>
            {CATEGORIES.map(c => <option key={c}>{c}</option>)}
          </select>
          <input
            type="month" name="month" value={filters.month} onChange={handleFilterChange}
            className="border border-gray-300 rounded-lg px-3 py-2 text-sm"
          />
          <input
            name="minAmount" value={filters.minAmount} onChange={handleFilterChange}
            placeholder="Min ₹" type="number"
            className="border border-gray-300 rounded-lg px-3 py-2 text-sm"
          />
        </div>

        {/* ── Table ── */}
        {loading ? (
          <div className="text-center py-10 text-gray-400">Loading...</div>
        ) : transactions.length === 0 ? (
          <div className="text-center py-10 text-gray-400">
            No transactions found.{' '}
            <Link to="/transactions/new" className="text-blue-600 hover:underline">Add one!</Link>
          </div>
        ) : (
          <div className="bg-white rounded-xl shadow overflow-hidden">
            <table className="w-full text-sm">
              <thead className="bg-gray-100 text-gray-600 text-left">
                <tr>
                  <th className="px-4 py-3">Date</th>
                  <th className="px-4 py-3">Description</th>
                  <th className="px-4 py-3">Category</th>
                  <th className="px-4 py-3">Type</th>
                  <th className="px-4 py-3 text-right">Amount</th>
                  <th className="px-4 py-3 text-center">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-100">
                {transactions.map((t) => (
                  <tr key={t.id} className="hover:bg-gray-50 transition-colors">
                    <td className="px-4 py-3 text-gray-500">{t.date}</td>
                    <td className="px-4 py-3 font-medium">{t.description}</td>
                    <td className="px-4 py-3">
                      <span className="bg-gray-100 text-gray-700 rounded-full px-2 py-0.5 text-xs">{t.category}</span>
                    </td>
                    <td className="px-4 py-3">
                      <span className={`text-xs font-semibold px-2 py-0.5 rounded-full ${
                        t.type === 'INCOME' ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'
                      }`}>
                        {t.type}
                      </span>
                    </td>
                    <td className={`px-4 py-3 text-right font-semibold ${
                      t.type === 'INCOME' ? 'text-green-600' : 'text-red-600'
                    }`}>
                      {t.type === 'INCOME' ? '+' : '-'}{formatINR(t.amount)}
                    </td>
                    <td className="px-4 py-3 text-center space-x-2">
                      <Link
                        to={`/transactions/edit/${t.id}`}
                        className="text-blue-600 hover:underline text-xs font-medium"
                      >
                        Edit
                      </Link>
                      <button
                        onClick={() => handleDelete(t.id)}
                        className="text-red-500 hover:underline text-xs font-medium"
                      >
                        Delete
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>

            {/* Pagination */}
            {totalPages > 1 && (
              <div className="px-4 py-3 border-t flex gap-2 justify-center">
                {[...Array(totalPages)].map((_, i) => (
                  <button
                    key={i}
                    onClick={() => setFilters({ ...filters, page: i })}
                    className={`w-8 h-8 rounded-full text-sm font-medium ${
                      filters.page === i ? 'bg-blue-700 text-white' : 'bg-gray-100 hover:bg-gray-200'
                    }`}
                  >
                    {i + 1}
                  </button>
                ))}
              </div>
            )}
          </div>
        )}
      </main>
    </div>
  );
}

