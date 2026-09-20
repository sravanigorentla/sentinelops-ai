import React, { useEffect, useState } from 'react';
import { api } from '../services/api';
import { Log } from '../types';
import { StatusBadge } from '../components/StatusBadge';
import { Terminal, Search, Filter, RefreshCw } from 'lucide-react';

export const Logs: React.FC = () => {
  const [logs, setLogs] = useState<Log[]>([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [levelFilter, setLevelFilter] = useState('');

  const fetchLogs = async () => {
    try {
      setLoading(true);
      const res = await api.searchLogs({
        search: search || undefined,
        level: levelFilter || undefined,
        size: 50,
      });
      setLogs(res.content || []);
    } catch (err) {
      console.error('Failed to fetch logs', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchLogs();
  }, [levelFilter]);

  const handleSearchSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    fetchLogs();
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-extrabold text-white tracking-tight">Centralized Application Logs</h1>
          <p className="text-sm text-gray-400 mt-1">Cross-microservice log aggregation with Request ID tracing.</p>
        </div>
        <button
          onClick={fetchLogs}
          className="flex items-center gap-2 px-3.5 py-2 rounded-lg bg-gray-800 text-xs font-medium text-gray-200 hover:bg-gray-700"
        >
          <RefreshCw className="w-3.5 h-3.5" /> Refresh Logs
        </button>
      </div>

      {/* Filter and Search Bar */}
      <div className="glass-card p-4 rounded-xl flex flex-wrap items-center gap-4 text-xs">
        <form onSubmit={handleSearchSubmit} className="flex-1 min-w-[240px] relative">
          <Search className="w-4 h-4 text-gray-500 absolute left-3 top-2.5" />
          <input
            type="text"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            className="w-full bg-gray-900 border border-gray-800 rounded-lg pl-9 pr-4 py-2 text-white focus:border-indigo-500"
            placeholder="Search log message or Request ID (e.g. req-pay-8845)..."
          />
        </form>

        <select
          value={levelFilter}
          onChange={(e) => setLevelFilter(e.target.value)}
          className="bg-gray-900 border border-gray-800 rounded-lg px-3 py-2 text-white focus:border-indigo-500"
        >
          <option value="">All Log Levels</option>
          <option value="INFO">INFO</option>
          <option value="WARN">WARN</option>
          <option value="ERROR">ERROR</option>
        </select>
      </div>

      {/* Log Feed Table */}
      {loading ? (
        <div className="flex items-center justify-center h-64 text-indigo-400">
          <RefreshCw className="w-8 h-8 animate-spin" />
        </div>
      ) : (
        <div className="glass-card rounded-2xl overflow-hidden font-mono text-xs">
          <div className="overflow-x-auto">
            <table className="w-full text-left">
              <thead className="bg-gray-900/80 text-gray-400 uppercase font-semibold border-b border-gray-800">
                <tr>
                  <th className="p-3.5">Timestamp</th>
                  <th className="p-3.5">Level</th>
                  <th className="p-3.5">Service</th>
                  <th className="p-3.5">Request ID</th>
                  <th className="p-3.5">Message</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-800/60">
                {logs.length === 0 ? (
                  <tr>
                    <td colSpan={5} className="p-8 text-center text-gray-500">No logs found matching search filter.</td>
                  </tr>
                ) : (
                  logs.map((l) => (
                    <tr key={l.id} className="hover:bg-gray-900/40 transition-colors">
                      <td className="p-3.5 text-gray-400 whitespace-nowrap">{new Date(l.timestamp).toLocaleString()}</td>
                      <td className="p-3.5"><StatusBadge status={l.logLevel} /></td>
                      <td className="p-3.5 font-bold text-indigo-400">{l.serviceName}</td>
                      <td className="p-3.5 text-gray-300">{l.requestId || '-'}</td>
                      <td className="p-3.5 text-gray-200 max-w-xl truncate">{l.message}</td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </div>
      )}
    </div>
  );
};
