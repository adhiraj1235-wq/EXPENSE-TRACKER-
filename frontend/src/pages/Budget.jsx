import React, { useEffect, useState } from 'react';
import api from '../api/axios';
import Navbar from '../components/Navbar';

/**
 * Budget page — set a monthly spending limit per category and see how much is used.
 *
 * Shows a progress bar for each budget:
 *   - Green  (< 80% used)  → "On track"
 *   - Orange (≥ 80% used)  → Warning: "₹X remaining"
 *   - Red    (> 100% used) → "Budget exceeded by ₹X"
 *
 * Data comes from GET /api/budgets?month=M&year=Y which also returns
 * the spent amount (fetched from transaction-service via OpenFeign).
 */
export default function Budget() {
  const now = new Date();
  const [year,  setYear]  = useState(now.getFullYear());
  const [month, setMonth] = useState(now.getMonth() + 1); // 1-12

  const [budgets, setBudgets]       = useState([]);
  const [loading, setLoading]       = useState(true);
  const [showForm, setShowForm]     = useState(false);
  const [form, setForm]             = useState({ category: 'Food', budgetLimit: '', month, year });
  const [saving, setSaving]         = useState(false);
  const [formError, setFormError]   = useState('');

  const CATEGORIES = ['Housing','Groceries','Transport','Entertainment',
                      'Food','Utilities','Healthcare','Education','Other'];
  const MONTHS = ['Jan','Feb','Mar','Apr','May','Jun',
                  'Jul','Aug','Sep','Oct','Nov','Dec'];

  const fetchBudgets = async () => {
    setLoading(true);
    try {
      const res = await api.get('/api/budgets', { params: { month, year } });
      setBudgets(res.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchBudgets(); }, [month, year]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setFormError('');
    setSaving(true);
    try {
      await api.post('/api/budgets', { ...form, month, year, budgetLimit: parseFloat(form.budgetLimit) });
      setShowForm(false);
      setForm({ category: 'Food', budgetLimit: '', month, year });
      fetchBudgets();
    } catch (err) {
      setFormError(err.response?.data?.message || 'Failed to save budget.');
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Delete this budget?')) return;
    try {
      await api.delete(`/api/budgets/${id}`);
      fetchBudgets();
    } catch (err) {
      alert('Delete failed.');
    }
  };

  const formatINR = (v) =>
    new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR', maximumFractionDigits: 0 }).format(v);

  // Map budget status to Tailwind color classes
  const statusColors = {
    OK:       { bar: 'bg-green-500',  text: 'text-green-700', bg: 'bg-green-50' },
    WARNING:  { bar: 'bg-orange-400', text: 'text-orange-700', bg: 'bg-orange-50' },
    EXCEEDED: { bar: 'bg-red-500',    text: 'text-red-700',   bg: 'bg-red-50'  },
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />
      <main className="max-w-4xl mx-auto px-4 py-6">
        <div className="flex flex-wrap justify-between items-center mb-5 gap-3">
          <h2 className="text-2xl font-bold text-gray-800">💼 Monthly Budgets</h2>

          {/* Month / Year selector */}
          <div className="flex items-center gap-2">
            <select value={month} onChange={e => setMonth(Number(e.target.value))}
              className="border border-gray-300 rounded-lg px-3 py-1.5 text-sm">
              {MONTHS.map((m, i) => <option key={i} value={i+1}>{m}</option>)}
            </select>
            <input type="number" value={year} onChange={e => setYear(Number(e.target.value))}
              className="border border-gray-300 rounded-lg px-3 py-1.5 text-sm w-20" min="2020" max="2099"
            />
            <button
              onClick={() => setShowForm(!showForm)}
              className="bg-blue-700 hover:bg-blue-800 text-white text-sm font-medium px-4 py-1.5 rounded-lg"
            >
              {showForm ? 'Cancel' : '+ Set Budget'}
            </button>
          </div>
        </div>

        {/* Add Budget Form */}
        {showForm && (
          <div className="bg-white rounded-xl shadow p-5 mb-5">
            <h3 className="font-semibold text-gray-700 mb-4">Set Budget for {MONTHS[month-1]} {year}</h3>
            {formError && (
              <div className="bg-red-50 border border-red-300 text-red-700 rounded-lg px-4 py-2 mb-3 text-sm">{formError}</div>
            )}
            <form onSubmit={handleSubmit} className="flex flex-wrap gap-3 items-end">
              <div>
                <label className="block text-sm text-gray-600 mb-1">Category</label>
                <select value={form.category} onChange={e => setForm({ ...form, category: e.target.value })}
                  className="border border-gray-300 rounded-lg px-3 py-2 text-sm">
                  {CATEGORIES.map(c => <option key={c}>{c}</option>)}
                </select>
              </div>
              <div>
                <label className="block text-sm text-gray-600 mb-1">Limit (₹)</label>
                <input type="number" value={form.budgetLimit}
                  onChange={e => setForm({ ...form, budgetLimit: e.target.value })}
                  required min="1" placeholder="e.g. 10000"
                  className="border border-gray-300 rounded-lg px-3 py-2 text-sm w-32"
                />
              </div>
              <button type="submit" disabled={saving}
                className="bg-green-600 hover:bg-green-700 text-white text-sm font-medium px-4 py-2 rounded-lg disabled:opacity-60">
                {saving ? 'Saving...' : 'Save'}
              </button>
            </form>
          </div>
        )}

        {/* Budget Cards */}
        {loading ? (
          <div className="text-center py-10 text-gray-400">Loading budgets...</div>
        ) : budgets.length === 0 ? (
          <div className="text-center py-10 text-gray-400">
            No budgets set for {MONTHS[month-1]} {year}. Click "+ Set Budget" to start!
          </div>
        ) : (
          <div className="space-y-4">
            {budgets.map((b) => {
              const pct    = Math.min(b.percentUsed || 0, 100);
              const colors = statusColors[b.status] || statusColors.OK;
              return (
                <div key={b.id} className={`rounded-xl shadow p-5 ${colors.bg}`}>
                  <div className="flex justify-between items-start mb-2">
                    <div>
                      <span className="font-semibold text-gray-800 text-lg">{b.category}</span>
                      <span className={`ml-3 text-xs font-bold px-2 py-0.5 rounded-full ${colors.text} bg-white border`}>
                        {b.status}
                      </span>
                    </div>
                    <button onClick={() => handleDelete(b.id)}
                      className="text-xs text-red-400 hover:text-red-600">✕</button>
                  </div>

                  {/* Progress bar */}
                  <div className="w-full bg-gray-200 rounded-full h-3 mb-2">
                    <div
                      className={`h-3 rounded-full transition-all ${colors.bar}`}
                      style={{ width: `${pct}%` }}
                    />
                  </div>

                  <div className="flex justify-between text-sm">
                    <span className="text-gray-600">
                      Spent: <strong>{formatINR(b.spent || 0)}</strong>
                    </span>
                    <span className="text-gray-600">
                      Limit: <strong>{formatINR(b.budgetLimit)}</strong>
                    </span>
                    <span className={`font-semibold ${colors.text}`}>
                      {b.percentUsed?.toFixed(1)}% used
                    </span>
                  </div>

                  {/* Alert message */}
                  {b.message && (
                    <div className={`mt-2 text-sm font-medium ${colors.text}`}>
                      ⚠️ {b.message}
                    </div>
                  )}
                </div>
              );
            })}
          </div>
        )}
      </main>
    </div>
  );
}

