import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import api from '../api/axios';
import Navbar from '../components/Navbar';

/**
 * TransactionForm — handles both "Add New" and "Edit Existing" transactions.
 *
 * - If route is /transactions/new  → creates via POST /api/transactions
 * - If route is /transactions/edit/:id → loads the existing transaction,
 *   then updates via PUT /api/transactions/:id
 *
 * Fields: type (INCOME/EXPENSE), amount (₹), category, description, date.
 */
export default function TransactionForm() {
  const { id } = useParams();      // id is set when editing
  const isEdit  = !!id;
  const navigate = useNavigate();

  const [form, setForm] = useState({
    type: 'EXPENSE', amount: '', category: 'Food',
    description: '', date: new Date().toISOString().slice(0, 10),
  });
  const [loading, setLoading] = useState(false);
  const [error, setError]     = useState('');

  const CATEGORIES = {
    INCOME:  ['Salary', 'Freelance', 'Business', 'Investment', 'Other'],
    EXPENSE: ['Housing', 'Groceries', 'Transport', 'Entertainment', 'Food',
              'Utilities', 'Healthcare', 'Education', 'Other'],
  };

  // If editing, load existing transaction data
  useEffect(() => {
    if (isEdit) {
      api.get(`/api/transactions/${id}`)
        .then(res => setForm(res.data))
        .catch(() => setError('Could not load transaction.'));
    }
  }, [id, isEdit]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm(prev => ({
      ...prev,
      [name]: value,
      // Reset category when type changes so the dropdown shows valid options
      ...(name === 'type' ? { category: CATEGORIES[value][0] } : {}),
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      const payload = { ...form, amount: parseFloat(form.amount) };
      if (isEdit) {
        await api.put(`/api/transactions/${id}`, payload);
      } else {
        await api.post('/api/transactions', payload);
      }
      navigate('/transactions');
    } catch (err) {
      setError(err.response?.data?.message || 'Save failed.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />
      <main className="max-w-lg mx-auto px-4 py-8">
        <h2 className="text-2xl font-bold text-gray-800 mb-6">
          {isEdit ? '✏️ Edit Transaction' : '➕ Add Transaction'}
        </h2>

        {error && (
          <div className="bg-red-50 border border-red-300 text-red-700 rounded-lg px-4 py-3 mb-4 text-sm">
            {error}
          </div>
        )}

        <div className="bg-white rounded-xl shadow p-6">
          <form onSubmit={handleSubmit} className="space-y-4">
            {/* Type toggle */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Type</label>
              <div className="flex gap-3">
                {['INCOME', 'EXPENSE'].map(t => (
                  <label key={t} className="flex items-center gap-2 cursor-pointer">
                    <input
                      type="radio" name="type" value={t}
                      checked={form.type === t} onChange={handleChange}
                      className="accent-blue-600"
                    />
                    <span className={`text-sm font-medium ${
                      t === 'INCOME' ? 'text-green-700' : 'text-red-700'
                    }`}>
                      {t === 'INCOME' ? '📈 Income' : '📉 Expense'}
                    </span>
                  </label>
                ))}
              </div>
            </div>

            {/* Amount */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Amount (₹)</label>
              <input
                type="number" name="amount" value={form.amount} onChange={handleChange}
                required min="0.01" step="0.01" placeholder="e.g. 5000"
                className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-400"
              />
            </div>

            {/* Category */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Category</label>
              <select
                name="category" value={form.category} onChange={handleChange}
                className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-400"
              >
                {CATEGORIES[form.type].map(c => <option key={c}>{c}</option>)}
              </select>
            </div>

            {/* Description */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Description</label>
              <input
                type="text" name="description" value={form.description} onChange={handleChange}
                required placeholder="e.g. Monthly rent"
                className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-400"
              />
            </div>

            {/* Date */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Date</label>
              <input
                type="date" name="date" value={form.date} onChange={handleChange} required
                className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-400"
              />
            </div>

            <div className="flex gap-3 pt-2">
              <button
                type="submit" disabled={loading}
                className="flex-1 bg-blue-700 hover:bg-blue-800 text-white font-semibold py-2.5 rounded-lg transition-colors disabled:opacity-60"
              >
                {loading ? 'Saving...' : (isEdit ? 'Update Transaction' : 'Add Transaction')}
              </button>
              <button
                type="button" onClick={() => navigate('/transactions')}
                className="flex-1 bg-gray-100 hover:bg-gray-200 text-gray-700 font-semibold py-2.5 rounded-lg transition-colors"
              >
                Cancel
              </button>
            </div>
          </form>
        </div>
      </main>
    </div>
  );
}

