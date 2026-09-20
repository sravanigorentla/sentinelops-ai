import React, { useEffect, useState } from 'react';
import { api } from '../services/api';
import { AuditLog } from '../types';
import { ShieldCheck, RefreshCw, User, Activity } from 'lucide-react';

export const AuditLogs: React.FC = () => {
  const [logs, setLogs] = useState<AuditLog[]>([]);
  const [loading, setLoading] = useState(true);

  const fetchAuditLogs = async () => {
    try {
      setLoading(true);
      const res = await api.getAuditLogs();
      setLogs(res.content || []);
    } catch (err) {
      console.error('Failed to fetch audit logs', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchAuditLogs();
  }, []);

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-extrabold text-white tracking-tight">Security & Operation Audit Logs</h1>
          <p className="text-sm text-gray-400 mt-1">Immutable security audit trail logging user actions, rollbacks, and AI triggers.</p>
        </div>
        <button
          onClick={fetchAuditLogs}
          className="flex items-center gap-2 px-3.5 py-2 rounded-lg bg-gray-800 text-xs font-medium text-gray-200 hover:bg-gray-700"
        >
          <RefreshCw className="w-3.5 h-3.5" /> Refresh Audit Trail
        </button>
      </div>

      {loading ? (
        <div className="flex items-center justify-center h-64 text-indigo-400">
          <RefreshCw className="w-8 h-8 animate-spin" />
        </div>
      ) : (
        <div className="glass-card rounded-2xl overflow-hidden text-xs font-mono">
          <div className="overflow-x-auto">
            <table className="w-full text-left">
              <thead className="bg-gray-900/80 text-gray-400 uppercase font-semibold border-b border-gray-800">
                <tr>
                  <th className="p-4">Timestamp</th>
                  <th className="p-4">Action</th>
                  <th className="p-4">User</th>
                  <th className="p-4">Target Type</th>
                  <th className="p-4">Target ID</th>
                  <th className="p-4">Details</th>
                  <th className="p-4">IP Address</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-800/60">
                {logs.length === 0 ? (
                  <tr>
                    <td colSpan={7} className="p-8 text-center text-gray-500">No audit logs recorded yet.</td>
                  </tr>
                ) : (
                  logs.map((al) => (
                    <tr key={al.id} className="hover:bg-gray-900/40 transition-colors">
                      <td className="p-4 text-gray-400 whitespace-nowrap">{new Date(al.timestamp).toLocaleString()}</td>
                      <td className="p-4">
                        <span className="inline-block px-2 py-0.5 rounded bg-indigo-500/10 text-indigo-400 border border-indigo-500/20 font-bold">
                          {al.action}
                        </span>
                      </td>
                      <td className="p-4 text-gray-200 font-sans font-semibold">{al.userEmail || 'System'}</td>
                      <td className="p-4 text-gray-400">{al.targetType}</td>
                      <td className="p-4 text-gray-400">{al.targetId || '-'}</td>
                      <td className="p-4 text-gray-300 font-sans">{al.details}</td>
                      <td className="p-4 text-gray-500">{al.ipAddress || '127.0.0.1'}</td>
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
