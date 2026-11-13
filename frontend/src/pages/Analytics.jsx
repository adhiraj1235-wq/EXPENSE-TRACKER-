import React, { useEffect, useState } from 'react';
import {
  BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer,
  LineChart, Line
} from 'recharts';
import api from '../api/axios';
import Navbar from '../components/Navbar';

/**
 * Analytics page — deeper spending analysis.
 *
 * Sections:
 * 1. Month-over-month category comparison table
 * 2. 3-month moving average spending prediction chart
 *
 * Data comes from:
 *   GET /api/analytics/compare  → { category, currentMonth, previousMonth, change }[]
 *   GET /api/analytics/predict  → { category, predictedAmount }[]
 */
export default function Analytics() {
  const [comparison, setComparison] = useState([]);
  const [predictions, setPredictions] = useState([]);
  const [loading, setLoading]   = useState(true);
  const [error, setError]       = useState('');

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [compRes, predRes] = await Promise.all([
          api.get('/api/analytics/compare'),
          api.get('/api/analytics/predict'),
        ]);
        setComparison(compRes.data);
        setPredictions(predRes.data);
      } catch (err) {
        setError('Could not load analytics. Make sure services are running.');
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, []);

  const formatINR = (v) =>
    new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR', maximumFractionDigits: 0 }).format(v || 0);

  if (loading) return <><Navbar /><div className="p-10 text-center text-gray-400">Loading analytics...</div></>;
  if (error)   return <><Navbar /><div className="p-10 text-center text-red-500">{error}</div></>;

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />
      <main className="max-w-7xl mx-auto px-4 py-6">
        <h2 className="text-2xl font-bold text-gray-800 mb-6">🔍 Analytics</h2>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          {/* Month-over-month comparison */}
          <div className="bg-white rounded-xl shadow p-5">
            <h3 className="font-semibold text-gray-700 mb-4">Month-over-Month Category Comparison</h3>
            {comparison.length === 0 ? (
              <p className="text-gray-400 text-sm py-6 text-center">Need data from at least 2 months.</p>
            ) : (
              <>
                <ResponsiveContainer width="100%" height={260}>
                  <BarChart data={comparison} margin={{ top: 5, right: 10, left: 10, bottom: 5 }}>
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis dataKey="category" tick={{ fontSize: 11 }} />
                    <YAxis tick={{ fontSize: 11 }} tickFormatter={(v) => `₹${(v/1000).toFixed(0)}k`} />
                    <Tooltip formatter={(v) => formatINR(v)} />
                    <Legend />
                    <Bar dataKey="previousMonth" name="Last Month" fill="#94a3b8" radius={[4,4,0,0]} />
                    <Bar dataKey="currentMonth"  name="This Month" fill="#3b82f6" radius={[4,4,0,0]} />
                  </BarChart>
                </ResponsiveContainer>

                {/* Change table */}
                <table className="w-full text-sm mt-4">
                  <thead>
                    <tr className="text-left text-gray-500 border-b">
                      <th className="pb-2">Category</th>
                      <th className="pb-2 text-right">Last Month</th>
                      <th className="pb-2 text-right">This Month</th>
                      <th className="pb-2 text-right">Change</th>
                    </tr>
                  </thead>
                  <tbody>
                    {comparison.map((row, i) => (
                      <tr key={i} className="border-b last:border-0">
                        <td className="py-2">{row.category}</td>
                        <td className="py-2 text-right text-gray-500">{formatINR(row.previousMonth)}</td>
                        <td className="py-2 text-right">{formatINR(row.currentMonth)}</td>
                        <td className={`py-2 text-right font-semibold ${row.change > 0 ? 'text-red-500' : 'text-green-500'}`}>
                          {row.change > 0 ? '+' : ''}{formatINR(row.change)}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </>
            )}
          </div>

          {/* Spending Prediction */}
          <div className="bg-white rounded-xl shadow p-5">
            <h3 className="font-semibold text-gray-700 mb-1">Spending Prediction (Next Month)</h3>
            <p className="text-xs text-gray-400 mb-4">Based on 3-month moving average of your expenses.</p>
            {predictions.length === 0 ? (
              <p className="text-gray-400 text-sm py-6 text-center">Need at least 3 months of expense data.</p>
            ) : (
              <>
                <ResponsiveContainer width="100%" height={260}>
                  <BarChart data={predictions} layout="vertical" margin={{ top: 5, right: 60, left: 60, bottom: 5 }}>
                    <CartesianGrid strokeDasharray="3 3" horizontal={false} />
                    <XAxis type="number" tick={{ fontSize: 11 }} tickFormatter={(v) => `₹${(v/1000).toFixed(0)}k`} />
                    <YAxis type="category" dataKey="category" tick={{ fontSize: 11 }} width={80} />
                    <Tooltip formatter={(v) => formatINR(v)} />
                    <Bar dataKey="predictedAmount" name="Predicted" fill="#8b5cf6" radius={[0,4,4,0]} />
                  </BarChart>
                </ResponsiveContainer>

                <div className="mt-4 text-sm text-gray-500 bg-purple-50 rounded-lg p-3">
                  <strong>Total predicted expense next month:</strong>{' '}
                  <span className="text-purple-700 font-bold">
                    {formatINR(predictions.reduce((sum, p) => sum + (p.predictedAmount || 0), 0))}
                  </span>
                </div>
              </>
            )}
          </div>
        </div>
      </main>
    </div>
  );
}

