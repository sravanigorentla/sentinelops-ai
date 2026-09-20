import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { api } from '../services/api';
import { DashboardSummary } from '../types';
import { StatusBadge } from '../components/StatusBadge';
import {
  Server,
  AlertTriangle,
  Activity,
  Cpu,
  Database,
  Clock,
  ArrowUpRight,
  GitCommit,
  Zap,
  TrendingUp,
  RefreshCw
} from 'lucide-react';
import { AreaChart, Area, XAxis, YAxis, Tooltip, ResponsiveContainer, CartesianGrid } from 'recharts';

export const Dashboard: React.FC = () => {
  const [data, setData] = useState<DashboardSummary | null>(null);
  const [loading, setLoading] = useState(true);

  const fetchSummary = async () => {
    try {
      setLoading(true);
      const res = await api.getDashboardSummary();
      setData(res);
    } catch (err) {
      console.error('Failed to fetch dashboard summary', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchSummary();
  }, []);

  const trendData = data?.incidentTrends
    ? Object.entries(data.incidentTrends).map(([day, count]) => ({ day, incidents: count }))
    : [];

  if (loading && !data) {
    return (
      <div className="flex items-center justify-center h-64 text-indigo-400">
        <RefreshCw className="w-8 h-8 animate-spin" />
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Top Header Title & Actions */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-extrabold text-white tracking-tight">System Overview & Incident Ops</h1>
          <p className="text-sm text-gray-400 mt-1">Real-time cloud telemetry, anomaly signals, and automated AIOps correlation.</p>
        </div>
        <div className="flex items-center gap-3">
          <button
            onClick={fetchSummary}
            className="flex items-center gap-2 px-3.5 py-2 rounded-lg bg-gray-800/80 hover:bg-gray-700/80 border border-gray-700/60 text-xs font-medium text-gray-200 transition-colors"
          >
            <RefreshCw className="w-3.5 h-3.5 text-gray-400" />
            Refresh Telemetry
          </button>
          <Link
            to="/simulator"
            className="flex items-center gap-2 px-4 py-2 rounded-lg bg-gradient-to-r from-indigo-600 to-violet-600 hover:from-indigo-500 hover:to-violet-500 text-xs font-semibold text-white shadow-lg shadow-indigo-600/20 transition-all"
          >
            <Zap className="w-3.5 h-3.5" />
            Simulate Failure
          </Link>
        </div>
      </div>

      {/* KPI Cards Grid */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <div className="glass-card p-5 rounded-2xl glass-card-hover">
          <div className="flex items-center justify-between">
            <span className="text-xs font-semibold text-gray-400 uppercase tracking-wider">Monitored Services</span>
            <div className="p-2 rounded-xl bg-indigo-500/10 border border-indigo-500/20 text-indigo-400">
              <Server className="w-5 h-5" />
            </div>
          </div>
          <div className="mt-4 flex items-baseline justify-between">
            <span className="text-3xl font-extrabold text-white">{data?.totalServices || 0}</span>
            <span className="text-xs text-emerald-400 font-medium">{data?.healthyServices || 0} Healthy</span>
          </div>
          <div className="mt-3 w-full bg-gray-800 rounded-full h-1.5 overflow-hidden flex">
            <div style={{ width: `${((data?.healthyServices || 0) / (data?.totalServices || 1)) * 100}%` }} className="bg-emerald-500 h-full"></div>
            <div style={{ width: `${((data?.degradedServices || 0) / (data?.totalServices || 1)) * 100}%` }} className="bg-amber-500 h-full"></div>
            <div style={{ width: `${((data?.downServices || 0) / (data?.totalServices || 1)) * 100}%` }} className="bg-rose-500 h-full"></div>
          </div>
        </div>

        <div className="glass-card p-5 rounded-2xl glass-card-hover">
          <div className="flex items-center justify-between">
            <span className="text-xs font-semibold text-gray-400 uppercase tracking-wider">Active Incidents</span>
            <div className="p-2 rounded-xl bg-amber-500/10 border border-amber-500/20 text-amber-400">
              <AlertTriangle className="w-5 h-5" />
            </div>
          </div>
          <div className="mt-4 flex items-baseline justify-between">
            <span className="text-3xl font-extrabold text-white">{data?.activeIncidents || 0}</span>
            <span className="text-xs text-rose-400 font-semibold">{data?.criticalIncidents || 0} Critical</span>
          </div>
          <p className="mt-2 text-xs text-gray-400">Pending developer resolution</p>
        </div>

        <div className="glass-card p-5 rounded-2xl glass-card-hover">
          <div className="flex items-center justify-between">
            <span className="text-xs font-semibold text-gray-400 uppercase tracking-wider">Avg Latency (SLA)</span>
            <div className="p-2 rounded-xl bg-violet-500/10 border border-violet-500/20 text-violet-400">
              <Clock className="w-5 h-5" />
            </div>
          </div>
          <div className="mt-4 flex items-baseline justify-between">
            <span className="text-3xl font-extrabold text-white">{data?.avgLatencyMs?.toFixed(0) || 0}<span className="text-lg text-gray-400 font-normal">ms</span></span>
            <span className="text-xs text-gray-400">Error: {(data?.avgErrorRate || 0).toFixed(2)}%</span>
          </div>
          <p className="mt-2 text-xs text-gray-400">Aggregated across all endpoints</p>
        </div>

        <div className="glass-card p-5 rounded-2xl glass-card-hover">
          <div className="flex items-center justify-between">
            <span className="text-xs font-semibold text-gray-400 uppercase tracking-wider">Cloud Resources</span>
            <div className="p-2 rounded-xl bg-emerald-500/10 border border-emerald-500/20 text-emerald-400">
              <Cpu className="w-5 h-5" />
            </div>
          </div>
          <div className="mt-4 flex items-baseline justify-between">
            <span className="text-2xl font-extrabold text-white">CPU {(data?.avgCpuUsage || 0).toFixed(1)}%</span>
            <span className="text-xs text-gray-300 font-semibold">RAM {(data?.avgMemoryUsage || 0).toFixed(1)}%</span>
          </div>
          <p className="mt-2 text-xs text-gray-400">Throughput: {(data?.totalRequestRate || 0).toFixed(0)} req/s</p>
        </div>
      </div>

      {/* Incident Trends Chart & Telemetry Summary */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-2 glass-card p-6 rounded-2xl">
          <div className="flex items-center justify-between mb-6">
            <div>
              <h2 className="text-base font-bold text-white flex items-center gap-2">
                <TrendingUp className="w-4 h-4 text-indigo-400" />
                7-Day Incident Frequency Trend
              </h2>
              <p className="text-xs text-gray-400 mt-0.5">Anomaly triggers & developer response volume</p>
            </div>
          </div>
          <div className="h-64">
            <ResponsiveContainer width="100%" height="100%">
              <AreaChart data={trendData}>
                <defs>
                  <linearGradient id="colorIncidents" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="5%" stopColor="#6366F1" stopOpacity={0.4}/>
                    <stop offset="95%" stopColor="#6366F1" stopOpacity={0}/>
                  </linearGradient>
                </defs>
                <CartesianGrid strokeDasharray="3 3" stroke="#1F2937" />
                <XAxis dataKey="day" stroke="#6B7280" fontSize={12} />
                <YAxis stroke="#6B7280" fontSize={12} allowDecimals={false} />
                <Tooltip
                  contentStyle={{ backgroundColor: '#111827', borderColor: '#374151', borderRadius: '0.5rem', color: '#fff' }}
                />
                <Area type="monotone" dataKey="incidents" stroke="#6366F1" strokeWidth={3} fillOpacity={1} fill="url(#colorIncidents)" />
              </AreaChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* Microservice Health Snapshot */}
        <div className="glass-card p-6 rounded-2xl">
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-base font-bold text-white flex items-center gap-2">
              <Server className="w-4 h-4 text-indigo-400" />
              Service Status
            </h2>
            <Link to="/services" className="text-xs text-indigo-400 hover:underline flex items-center gap-1 font-medium">
              View All <ArrowUpRight className="w-3 h-3" />
            </Link>
          </div>
          <div className="space-y-3">
            {data?.recentServices.slice(0, 4).map((srv) => (
              <div key={srv.id} className="p-3 rounded-xl bg-gray-900/60 border border-gray-800/80 flex items-center justify-between">
                <div>
                  <Link to={`/services/${srv.id}`} className="text-sm font-semibold text-white hover:text-indigo-400 transition-colors">
                    {srv.name}
                  </Link>
                  <p className="text-[11px] text-gray-400">{srv.environment} • {srv.owner}</p>
                </div>
                <StatusBadge status={srv.status} />
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* Active Incidents & Deployments Row */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Active Incidents */}
        <div className="glass-card p-6 rounded-2xl">
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-base font-bold text-white flex items-center gap-2">
              <AlertTriangle className="w-4 h-4 text-amber-400" />
              Active System Incidents
            </h2>
            <Link to="/incidents" className="text-xs text-indigo-400 hover:underline flex items-center gap-1 font-medium">
              View All <ArrowUpRight className="w-3 h-3" />
            </Link>
          </div>

          <div className="space-y-3">
            {data?.recentIncidents.length === 0 ? (
              <div className="p-6 text-center text-gray-400 text-xs">No active incidents reported.</div>
            ) : (
              data?.recentIncidents.map((inc) => (
                <div key={inc.id} className="p-4 rounded-xl bg-gray-900/60 border border-gray-800 hover:border-indigo-500/40 transition-colors">
                  <div className="flex items-start justify-between gap-3">
                    <div>
                      <Link to={`/incidents/${inc.id}`} className="text-sm font-bold text-white hover:text-indigo-400 transition-colors">
                        #{inc.id} {inc.title}
                      </Link>
                      <p className="text-xs text-gray-400 mt-1 line-clamp-1">{inc.description}</p>
                    </div>
                    <StatusBadge status={inc.severity} />
                  </div>
                  <div className="mt-3 flex items-center justify-between text-xs text-gray-400 pt-2 border-t border-gray-800/60">
                    <span>Service: <strong className="text-gray-200">{inc.serviceName}</strong></span>
                    <span className="font-mono text-[11px]">{new Date(inc.detectedAt).toLocaleTimeString()}</span>
                  </div>
                </div>
              ))
            )}
          </div>
        </div>

        {/* Recent Deployments Release Timeline */}
        <div className="glass-card p-6 rounded-2xl">
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-base font-bold text-white flex items-center gap-2">
              <GitCommit className="w-4 h-4 text-indigo-400" />
              Recent Software Releases
            </h2>
            <Link to="/deployments" className="text-xs text-indigo-400 hover:underline flex items-center gap-1 font-medium">
              View All <ArrowUpRight className="w-3 h-3" />
            </Link>
          </div>

          <div className="space-y-3">
            {data?.recentDeployments.slice(0, 4).map((dep) => (
              <div key={dep.id} className="p-3.5 rounded-xl bg-gray-900/60 border border-gray-800 flex items-center justify-between">
                <div className="flex items-center gap-3">
                  <div className="w-8 h-8 rounded-lg bg-indigo-500/10 border border-indigo-500/20 flex items-center justify-center text-indigo-400 font-mono text-xs">
                    {dep.version}
                  </div>
                  <div>
                    <h3 className="text-xs font-bold text-white">{dep.serviceName}</h3>
                    <p className="text-[11px] text-gray-400 font-mono">Commit: {dep.commitSha.substring(0, 7)} • {dep.deployedBy}</p>
                  </div>
                </div>
                <div className="text-right">
                  <StatusBadge status={dep.rolledBack ? 'FAILED' : dep.status} />
                  <span className="block text-[10px] text-gray-500 mt-1 font-mono">{new Date(dep.deploymentTime).toLocaleTimeString()}</span>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
};
