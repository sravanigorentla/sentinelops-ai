import React, { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { api } from '../services/api';
import { Service, Metric, Log, Deployment, Incident } from '../types';
import { StatusBadge } from '../components/StatusBadge';
import { Server, Activity, Terminal, GitCommit, AlertTriangle, ArrowLeft, RefreshCw, Cpu, Clock } from 'lucide-react';
import { LineChart, Line, XAxis, YAxis, Tooltip, ResponsiveContainer, CartesianGrid } from 'recharts';

export const ServiceDetails: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const serviceId = parseInt(id || '0', 10);

  const [service, setService] = useState<Service | null>(null);
  const [metrics, setMetrics] = useState<Metric[]>([]);
  const [logs, setLogs] = useState<Log[]>([]);
  const [deployments, setDeployments] = useState<Deployment[]>([]);
  const [incidents, setIncidents] = useState<Incident[]>([]);
  const [loading, setLoading] = useState(true);

  const loadDetails = async () => {
    try {
      setLoading(true);
      const [srv, mList, lData, dList, iData] = await Promise.all([
        api.getServiceById(serviceId),
        api.getServiceMetrics(serviceId, 20),
        api.searchLogs({ serviceId, size: 10 }),
        api.getServiceDeployments(serviceId),
        api.getIncidents({ serviceId, size: 5 }),
      ]);

      setService(srv);
      setMetrics(mList.reverse());
      setLogs(lData.content || []);
      setDeployments(dList || []);
      setIncidents(iData.content || []);
    } catch (err) {
      console.error('Error fetching service details', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (serviceId) loadDetails();
  }, [serviceId]);

  if (loading || !service) {
    return (
      <div className="flex items-center justify-center h-64 text-indigo-400">
        <RefreshCw className="w-8 h-8 animate-spin" />
      </div>
    );
  }

  const chartData = metrics.map((m) => ({
    time: new Date(m.timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
    cpu: m.cpuUsage,
    memory: m.memoryUsage,
    latency: m.latencyMs,
    errors: m.errorRate,
  }));

  return (
    <div className="space-y-6">
      {/* Back Header */}
      <div>
        <Link to="/services" className="inline-flex items-center gap-1.5 text-xs text-indigo-400 hover:underline mb-2">
          <ArrowLeft className="w-3.5 h-3.5" /> Back to Services
        </Link>
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
          <div>
            <div className="flex items-center gap-3">
              <h1 className="text-2xl font-extrabold text-white">{service.name}</h1>
              <StatusBadge status={service.status} />
            </div>
            <p className="text-xs text-gray-400 mt-1">{service.description || 'No description provided.'}</p>
          </div>
          <button
            onClick={loadDetails}
            className="flex items-center gap-2 px-3 py-1.5 rounded-lg bg-gray-800 text-xs text-gray-300 hover:bg-gray-700"
          >
            <RefreshCw className="w-3.5 h-3.5" /> Refresh Telemetry
          </button>
        </div>
      </div>

      {/* Metric Telemetry Cards */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4 text-xs">
        <div className="glass-card p-4 rounded-xl">
          <span className="text-gray-400 font-semibold uppercase">Environment</span>
          <p className="text-base font-extrabold text-white mt-1 uppercase font-mono text-indigo-400">{service.environment}</p>
        </div>
        <div className="glass-card p-4 rounded-xl">
          <span className="text-gray-400 font-semibold uppercase">Owner</span>
          <p className="text-base font-bold text-white mt-1">{service.owner}</p>
        </div>
        <div className="glass-card p-4 rounded-xl">
          <span className="text-gray-400 font-semibold uppercase">Version</span>
          <p className="text-base font-mono font-bold text-indigo-400 mt-1">{service.version || 'v1.0.0'}</p>
        </div>
        <div className="glass-card p-4 rounded-xl">
          <span className="text-gray-400 font-semibold uppercase">Health Endpoint</span>
          <p className="text-xs font-mono text-gray-300 mt-1 truncate">{service.healthEndpoint || '/health'}</p>
        </div>
      </div>

      {/* Recharts CPU/Memory & Latency Trends */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className="glass-card p-6 rounded-2xl">
          <h2 className="text-sm font-bold text-white mb-4 flex items-center gap-2">
            <Cpu className="w-4 h-4 text-indigo-400" /> CPU & Memory Usage (%)
          </h2>
          <div className="h-56">
            <ResponsiveContainer width="100%" height="100%">
              <LineChart data={chartData}>
                <CartesianGrid strokeDasharray="3 3" stroke="#1F2937" />
                <XAxis dataKey="time" stroke="#6B7280" fontSize={11} />
                <YAxis stroke="#6B7280" fontSize={11} domain={[0, 100]} />
                <Tooltip contentStyle={{ backgroundColor: '#111827', borderColor: '#374151', borderRadius: '0.5rem' }} />
                <Line type="monotone" dataKey="cpu" name="CPU %" stroke="#6366F1" strokeWidth={2} dot={false} />
                <Line type="monotone" dataKey="memory" name="Memory %" stroke="#10B981" strokeWidth={2} dot={false} />
              </LineChart>
            </ResponsiveContainer>
          </div>
        </div>

        <div className="glass-card p-6 rounded-2xl">
          <h2 className="text-sm font-bold text-white mb-4 flex items-center gap-2">
            <Clock className="w-4 h-4 text-amber-400" /> Latency (ms) & Error Rate (%)
          </h2>
          <div className="h-56">
            <ResponsiveContainer width="100%" height="100%">
              <LineChart data={chartData}>
                <CartesianGrid strokeDasharray="3 3" stroke="#1F2937" />
                <XAxis dataKey="time" stroke="#6B7280" fontSize={11} />
                <YAxis stroke="#6B7280" fontSize={11} />
                <Tooltip contentStyle={{ backgroundColor: '#111827', borderColor: '#374151', borderRadius: '0.5rem' }} />
                <Line type="monotone" dataKey="latency" name="Latency (ms)" stroke="#F59E0B" strokeWidth={2} dot={false} />
                <Line type="monotone" dataKey="errors" name="Error Rate %" stroke="#EF4444" strokeWidth={2} dot={false} />
              </LineChart>
            </ResponsiveContainer>
          </div>
        </div>
      </div>

      {/* Recent Logs & Deployments */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className="glass-card p-6 rounded-2xl">
          <h2 className="text-sm font-bold text-white mb-4 flex items-center gap-2">
            <Terminal className="w-4 h-4 text-indigo-400" /> Recent Application Logs
          </h2>
          <div className="space-y-2 font-mono text-xs">
            {logs.length === 0 ? (
              <p className="text-gray-500 py-4 text-center">No logs recorded for this service.</p>
            ) : (
              logs.map((l) => (
                <div key={l.id} className="p-2.5 rounded bg-gray-900/80 border border-gray-800/80 flex items-start gap-2">
                  <StatusBadge status={l.logLevel} />
                  <div className="truncate flex-1">
                    <p className="text-gray-300 truncate">{l.message}</p>
                    <span className="text-[10px] text-gray-500">{new Date(l.timestamp).toLocaleTimeString()} • {l.requestId || 'no-req-id'}</span>
                  </div>
                </div>
              ))
            )}
          </div>
        </div>

        <div className="glass-card p-6 rounded-2xl">
          <h2 className="text-sm font-bold text-white mb-4 flex items-center gap-2">
            <GitCommit className="w-4 h-4 text-indigo-400" /> Release Deployments
          </h2>
          <div className="space-y-2.5 text-xs">
            {deployments.length === 0 ? (
              <p className="text-gray-500 py-4 text-center">No deployments recorded.</p>
            ) : (
              deployments.map((dep) => (
                <div key={dep.id} className="p-3 rounded-lg bg-gray-900/60 border border-gray-800 flex items-center justify-between">
                  <div>
                    <span className="font-bold text-white">{dep.version}</span>
                    <span className="text-gray-500 ml-2 font-mono">({dep.commitSha.substring(0, 7)})</span>
                    <p className="text-[11px] text-gray-400">By {dep.deployedBy}</p>
                  </div>
                  <StatusBadge status={dep.rolledBack ? 'FAILED' : dep.status} />
                </div>
              ))
            )}
          </div>
        </div>
      </div>
    </div>
  );
};
