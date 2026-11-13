import React, { useEffect, useState } from 'react';
import api from '../api/axios';
import Navbar from '../components/Navbar';

/**
 * Reports page — yearly income/expense/savings summary.
 *
 * Shows a table with one row per month, including:
 * - Month name, Income, Expense, Net Savings, Savings Rate %
 *
 * Also shows totals row at the bottom.
 * Data comes from GET /api/analytics/report?year=YYYY
 */
export default function Reports() {
  const [year, setYear]     = useState(new Date().getFullYear());
  const [report, setReport] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError]   = useState('');

  useEffect(() => {
    const fetchReport = async () => {
      setLoading(true);
      setError('');
      try {
        const res = await api.get(`/api/analytics/report?year=${year}`);
        setReport(res.data);
      } catch (err) {
        setError('Could not load report.');
      } finally {
        setLoading(false);
      }
    };
    fetchReport();
  }, [year]);

  const formatINR = (v) =>
    new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR', maximumFractionDigits: 0 }).format(v || 0);

  const MONTHS = ['Jan','Feb','Mar','Apr','May','Jun',
                  'Jul','Aug','Sep','Oct','Nov','Dec'];

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />
      <main className="max-w-5xl mx-auto px-4 py-6">
        <div className="flex flex-wrap justify-between items-center mb-5 gap-3">
          <h2 className="text-2xl font-bold text-gray-800">📅 Yearly Report</h2>
          <div className="flex items-center gap-2">
            <label className="text-sm text-gray-600">Year:</label>
            <input
              type="number" value={year} onChange={e => setYear(Number(e.target.value))}
              min="2020" max="2099"
              className="border border-gray-300 rounded-lg px-3 py-1.5 text-sm w-24 focus:outline-none focus:ring-2 focus:ring-blue-400"
            />
          </div>
        </div>

        {/* Summary cards */}
        {report && (
          <div className="grid grid-cols-1 sm:grid-cols-4 gap-4 mb-6">
            <div className="bg-green-50 border border-green-200 rounded-xl p-4">
              <div className="text-xs text-green-600 font-medium">Total Income {year}</div>
              <div className="text-xl font-bold text-green-700 mt-1">{formatINR(report.totalIncome)}</div>
            </div>
            <div className="bg-red-50 border border-red-200 rounded-xl p-4">
              <div className="text-xs text-red-600 font-medium">Total Expense {year}</div>
              <div className="text-xl font-bold text-red-700 mt-1">{formatINR(report.totalExpense)}</div>
            </div>
            <div className="bg-blue-50 border border-blue-200 rounded-xl p-4">
              <div className="text-xs text-blue-600 font-medium">Net Savings {year}</div>
              <div className="text-xl font-bold text-blue-700 mt-1">{formatINR(report.totalSavings)}</div>
            </div>
            <div className="bg-purple-50 border border-purple-200 rounded-xl p-4">
              <div className="text-xs text-purple-600 font-medium">Savings Rate</div>
              <div className="text-xl font-bold text-purple-700 mt-1">{report.savingsRate?.toFixed(1)}%</div>
            </div>
          </div>
        )}

        {/* Monthly breakdown table */}
        {loading ? (
          <div className="text-center py-10 text-gray-400">Loading report...</div>
        ) : error ? (
          <div className="text-center py-10 text-red-500">{error}</div>
        ) : !report || report.monthlyBreakdown?.length === 0 ? (
          <div className="text-center py-10 text-gray-400">No data for {year}.</div>
        ) : (
          <div className="bg-white rounded-xl shadow overflow-hidden">
            <table className="w-full text-sm">
              <thead className="bg-gray-100 text-gray-600 text-left">
                <tr>
                  <th className="px-4 py-3">Month</th>
                  <th className="px-4 py-3 text-right">Income</th>
                  <th className="px-4 py-3 text-right">Expense</th>
                  <th className="px-4 py-3 text-right">Savings</th>
                  <th className="px-4 py-3 text-right">Savings Rate</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-100">
                {report.monthlyBreakdown.map((row) => {
                  const savings = (row.income || 0) - (row.expense || 0);
                  const rate = row.income > 0 ? ((savings / row.income) * 100).toFixed(1) : '0.0';
                  return (
                    <tr key={row.month} className="hover:bg-gray-50 transition-colors">
                      <td className="px-4 py-3 font-medium">{MONTHS[(row.month || 1) - 1]} {year}</td>
                      <td className="px-4 py-3 text-right text-green-600">{formatINR(row.income)}</td>
                      <td className="px-4 py-3 text-right text-red-600">{formatINR(row.expense)}</td>
                      <td className={`px-4 py-3 text-right font-semibold ${savings >= 0 ? 'text-blue-600' : 'text-orange-600'}`}>
                        {formatINR(savings)}
                      </td>
                      <td className={`px-4 py-3 text-right ${parseFloat(rate) >= 20 ? 'text-green-600' : 'text-orange-600'}`}>
                        {rate}%
                      </td>
                    </tr>
                  );
                })}
              </tbody>
              {/* Totals row */}
              <tfoot className="bg-gray-50 font-semibold text-gray-700">
                <tr className="border-t-2 border-gray-200">
                  <td className="px-4 py-3">Total {year}</td>
                  <td className="px-4 py-3 text-right text-green-700">{formatINR(report.totalIncome)}</td>
                  <td className="px-4 py-3 text-right text-red-700">{formatINR(report.totalExpense)}</td>
                  <td className="px-4 py-3 text-right text-blue-700">{formatINR(report.totalSavings)}</td>
                  <td className="px-4 py-3 text-right text-purple-700">{report.savingsRate?.toFixed(1)}%</td>
                </tr>
              </tfoot>
            </table>
          </div>
        )}
      </main>
    </div>
  );
}

