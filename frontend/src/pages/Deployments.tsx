import React, { useEffect, useState } from 'react';
import { api } from '../services/api';
import { Deployment } from '../types';
import { StatusBadge } from '../components/StatusBadge';
import { GitCommit, Plus, RefreshCw, GitBranch } from 'lucide-react';

export const Deployments: React.FC = () => {
  const [deployments, setDeployments] = useState<Deployment[]>([]);
  const [loading, setLoading] = useState(true);

  const fetchDeployments = async () => {
    try {
      setLoading(true);
      const res = await api.getDeployments();
      setDeployments(res || []);
    } catch (err) {
      console.error('Failed to fetch deployments', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchDeployments();
  }, []);

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-extrabold text-white tracking-tight">Deployment & Release History</h1>
          <p className="text-sm text-gray-400 mt-1">Software release correlation with anomaly detection and automated rollbacks.</p>
        </div>
        <button
          onClick={fetchDeployments}
          className="flex items-center gap-2 px-3.5 py-2 rounded-lg bg-gray-800 text-xs font-medium text-gray-200 hover:bg-gray-700"
        >
          <RefreshCw className="w-3.5 h-3.5" /> Refresh Releases
        </button>
      </div>

      {loading ? (
        <div className="flex items-center justify-center h-64 text-indigo-400">
          <RefreshCw className="w-8 h-8 animate-spin" />
        </div>
      ) : (
        <div className="glass-card rounded-2xl overflow-hidden text-xs">
          <div className="overflow-x-auto">
            <table className="w-full text-left">
              <thead className="bg-gray-900/80 text-gray-400 uppercase font-semibold border-b border-gray-800">
                <tr>
                  <th className="p-4">Version</th>
                  <th className="p-4">Service</th>
                  <th className="p-4">Commit SHA</th>
                  <th className="p-4">Branch</th>
                  <th className="p-4">Environment</th>
                  <th className="p-4">Deployed By</th>
                  <th className="p-4">Deployment Time</th>
                  <th className="p-4 text-right">Status</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-800/60">
                {deployments.length === 0 ? (
                  <tr>
                    <td colSpan={8} className="p-8 text-center text-gray-500">No software deployments recorded yet.</td>
                  </tr>
                ) : (
                  deployments.map((dep) => (
                    <tr key={dep.id} className="hover:bg-gray-900/40 transition-colors">
                      <td className="p-4 font-mono font-bold text-indigo-400">{dep.version}</td>
                      <td className="p-4 font-bold text-white">{dep.serviceName}</td>
                      <td className="p-4 font-mono text-gray-300">{dep.commitSha.substring(0, 7)}</td>
                      <td className="p-4 font-mono text-gray-400 flex items-center gap-1">
                        <GitBranch className="w-3 h-3 text-gray-500" /> {dep.branch}
                      </td>
                      <td className="p-4 uppercase text-[11px] font-mono font-bold text-gray-300">{dep.environment}</td>
                      <td className="p-4 text-gray-300">{dep.deployedBy}</td>
                      <td className="p-4 text-gray-400 font-mono">{new Date(dep.deploymentTime).toLocaleString()}</td>
                      <td className="p-4 text-right">
                        <StatusBadge status={dep.rolledBack ? 'FAILED' : dep.status} />
                        {dep.rolledBack && (
                          <span className="block text-[10px] text-rose-400 font-mono mt-0.5">Rolled back</span>
                        )}
                      </td>
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
