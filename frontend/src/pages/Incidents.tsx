import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { api } from '../services/api';
import { Incident, IncidentSeverity, IncidentStatus } from '../types';
import { StatusBadge } from '../components/StatusBadge';
import { AlertTriangle, Filter, ArrowUpRight, RefreshCw, CheckCircle2 } from 'lucide-react';

export const Incidents: React.FC = () => {
  const [incidents, setIncidents] = useState<Incident[]>([]);
  const [loading, setLoading] = useState(true);
  const [statusFilter, setStatusFilter] = useState<string>('');
  const [severityFilter, setSeverityFilter] = useState<string>('');

  const fetchIncidents = async () => {
    try {
      setLoading(true);
      const res = await api.getIncidents({
        status: statusFilter || undefined,
        severity: severityFilter || undefined,
        size: 50,
      });
      setIncidents(res.content || []);
    } catch (err) {
      console.error('Failed to fetch incidents', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchIncidents();
  }, [statusFilter, severityFilter]);

  const handleUpdateStatus = async (id: number, newStatus: IncidentStatus) => {
    try {
      await api.updateIncident(id, { status: newStatus });
      fetchIncidents();
    } catch (err: any) {
      alert(err.message || 'Failed to update incident status');
    }
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-extrabold text-white tracking-tight">Incident Management</h1>
          <p className="text-sm text-gray-400 mt-1">Lifecycle tracking, AIOps root cause verification, and developer response.</p>
        </div>
        <button
          onClick={fetchIncidents}
          className="flex items-center gap-2 px-3.5 py-2 rounded-lg bg-gray-800 text-xs font-medium text-gray-200 hover:bg-gray-700"
        >
          <RefreshCw className="w-3.5 h-3.5" /> Refresh List
        </button>
      </div>

      {/* Filters Bar */}
      <div className="glass-card p-4 rounded-xl flex flex-wrap items-center gap-4 text-xs">
        <div className="flex items-center gap-2 text-gray-400 font-semibold">
          <Filter className="w-4 h-4 text-indigo-400" /> Filter Incidents:
        </div>

        <select
          value={statusFilter}
          onChange={(e) => setStatusFilter(e.target.value)}
          className="bg-gray-900 border border-gray-800 rounded-lg px-3 py-1.5 text-white focus:border-indigo-500"
        >
          <option value="">All Statuses</option>
          <option value="OPEN">OPEN</option>
          <option value="ACKNOWLEDGED">ACKNOWLEDGED</option>
          <option value="INVESTIGATING">INVESTIGATING</option>
          <option value="RESOLVED">RESOLVED</option>
        </select>

        <select
          value={severityFilter}
          onChange={(e) => setSeverityFilter(e.target.value)}
          className="bg-gray-900 border border-gray-800 rounded-lg px-3 py-1.5 text-white focus:border-indigo-500"
        >
          <option value="">All Severities</option>
          <option value="CRITICAL">CRITICAL</option>
          <option value="HIGH">HIGH</option>
          <option value="MEDIUM">MEDIUM</option>
          <option value="LOW">LOW</option>
        </select>
      </div>

      {/* Incident List Table */}
      {loading ? (
        <div className="flex items-center justify-center h-64 text-indigo-400">
          <RefreshCw className="w-8 h-8 animate-spin" />
        </div>
      ) : (
        <div className="glass-card rounded-2xl overflow-hidden">
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs">
              <thead className="bg-gray-900/80 text-gray-400 uppercase font-semibold border-b border-gray-800">
                <tr>
                  <th className="p-4">ID & Title</th>
                  <th className="p-4">Service</th>
                  <th className="p-4">Severity</th>
                  <th className="p-4">Status</th>
                  <th className="p-4">Detected</th>
                  <th className="p-4">Assigned To</th>
                  <th className="p-4 text-right">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-800/60">
                {incidents.length === 0 ? (
                  <tr>
                    <td colSpan={7} className="p-8 text-center text-gray-500">No incidents match the selected criteria.</td>
                  </tr>
                ) : (
                  incidents.map((inc) => (
                    <tr key={inc.id} className="hover:bg-gray-900/40 transition-colors">
                      <td className="p-4">
                        <Link to={`/incidents/${inc.id}`} className="font-bold text-white hover:text-indigo-400 transition-colors">
                          #{inc.id} {inc.title}
                        </Link>
                        {inc.aiAnalysis && (
                          <span className="ml-2 inline-flex items-center text-[10px] bg-indigo-500/10 text-indigo-300 px-1.5 py-0.5 rounded border border-indigo-500/20 font-mono">
                            AI Analyzed
                          </span>
                        )}
                      </td>
                      <td className="p-4 font-semibold text-gray-300">{inc.serviceName}</td>
                      <td className="p-4"><StatusBadge status={inc.severity} /></td>
                      <td className="p-4"><StatusBadge status={inc.status} /></td>
                      <td className="p-4 text-gray-400 font-mono">{new Date(inc.detectedAt).toLocaleString()}</td>
                      <td className="p-4 text-gray-300">{inc.assignedTo?.fullName || 'Unassigned'}</td>
                      <td className="p-4 text-right">
                        <div className="flex items-center justify-end gap-2">
                          {inc.status === 'OPEN' && (
                            <button
                              onClick={() => handleUpdateStatus(inc.id, 'ACKNOWLEDGED')}
                              className="px-2.5 py-1 rounded bg-amber-500/10 text-amber-400 border border-amber-500/20 hover:bg-amber-500/20 text-[11px] font-semibold"
                            >
                              Acknowledge
                            </button>
                          )}
                          {inc.status !== 'RESOLVED' && (
                            <button
                              onClick={() => handleUpdateStatus(inc.id, 'RESOLVED')}
                              className="px-2.5 py-1 rounded bg-emerald-500/10 text-emerald-400 border border-emerald-500/20 hover:bg-emerald-500/20 text-[11px] font-semibold"
                            >
                              Resolve
                            </button>
                          )}
                          <Link
                            to={`/incidents/${inc.id}`}
                            className="p-1.5 text-indigo-400 hover:text-white rounded bg-indigo-500/10 hover:bg-indigo-500/20 transition-colors"
                          >
                            <ArrowUpRight className="w-4 h-4" />
                          </Link>
                        </div>
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
