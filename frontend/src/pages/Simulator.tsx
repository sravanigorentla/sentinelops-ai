import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { api } from '../services/api';
import { Service } from '../types';
import { Zap, Database, Cpu, Clock, AlertOctagon, ArrowRight, RefreshCw } from 'lucide-react';

export const Simulator: React.FC = () => {
  const [services, setServices] = useState<Service[]>([]);
  const [selectedServiceId, setSelectedServiceId] = useState<number>(0);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    api.getServices().then((res) => {
      setServices(res || []);
      if (res && res.length > 0) setSelectedServiceId(res[0].id);
    });
  }, []);

  const handleSimulate = async (type: string) => {
    if (!selectedServiceId) return;
    try {
      setLoading(true);
      const incident = await api.triggerSimulation(selectedServiceId, type);
      if (incident && incident.id) {
        navigate(`/incidents/${incident.id}`);
      } else {
        navigate('/incidents');
      }
    } catch (err: any) {
      alert(err.message || 'Simulation failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="space-y-6 max-w-5xl mx-auto">
      <div>
        <h1 className="text-2xl font-extrabold text-white tracking-tight flex items-center gap-2">
          <Zap className="w-6 h-6 text-amber-400" /> Development Incident Simulator
        </h1>
        <p className="text-sm text-gray-400 mt-1">
          Trigger live cloud failure scenarios to test metric spikes, anomaly detection, incident creation, and AI analysis.
        </p>
      </div>

      {/* Select Target Service */}
      <div className="glass-card p-6 rounded-2xl">
        <label className="block text-xs font-semibold text-gray-400 uppercase tracking-wider mb-2">Target Microservice</label>
        <select
          value={selectedServiceId}
          onChange={(e) => setSelectedServiceId(parseInt(e.target.value, 10))}
          className="w-full bg-gray-900 border border-gray-800 rounded-xl p-3 text-sm text-white focus:border-indigo-500 font-medium"
        >
          {services.map((srv) => (
            <option key={srv.id} value={srv.id}>
              {srv.name} ({srv.environment}) - Current Status: {srv.status}
            </option>
          ))}
        </select>
      </div>

      {/* Simulation Scenario Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        {/* Scenario 1: DB Failure */}
        <div className="glass-card p-6 rounded-2xl glass-card-hover flex flex-col justify-between">
          <div>
            <div className="w-10 h-10 rounded-xl bg-rose-500/10 border border-rose-500/20 flex items-center justify-center text-rose-400 mb-4">
              <Database className="w-5 h-5" />
            </div>
            <h3 className="text-lg font-bold text-white">Database Connection Failure</h3>
            <p className="text-xs text-gray-400 mt-1.5 leading-relaxed">
              Simulates Hikari pool connection exhaustion, slow unindexed queries, 3800ms response latency, and 42% database error rate.
            </p>

            <div className="mt-4 p-3 rounded-lg bg-gray-900/60 border border-gray-800 text-[11px] font-mono text-rose-300 space-y-1">
              <p>▶ Error: HikariPool-1 connection timeout after 30000ms</p>
              <p>▶ Metric: Latency 3800ms (SLA: 500ms)</p>
            </div>
          </div>

          <button
            onClick={() => handleSimulate('DB_FAILURE')}
            disabled={loading}
            className="mt-6 w-full py-2.5 rounded-xl bg-rose-600 hover:bg-rose-500 text-white font-semibold text-xs flex items-center justify-center gap-2 shadow-lg shadow-rose-600/20 transition-all disabled:opacity-50"
          >
            {loading ? <RefreshCw className="w-4 h-4 animate-spin" /> : <>Simulate DB Failure <ArrowRight className="w-4 h-4" /></>}
          </button>
        </div>

        {/* Scenario 2: Memory Leak */}
        <div className="glass-card p-6 rounded-2xl glass-card-hover flex flex-col justify-between">
          <div>
            <div className="w-10 h-10 rounded-xl bg-amber-500/10 border border-amber-500/20 flex items-center justify-center text-amber-400 mb-4">
              <Cpu className="w-5 h-5" />
            </div>
            <h3 className="text-lg font-bold text-white">Java OutOfMemoryError</h3>
            <p className="text-xs text-gray-400 mt-1.5 leading-relaxed">
              Triggers rapid RAM consumption (98.8% Heap usage), GC overhead limit warnings, and batch processing memory leaks.
            </p>

            <div className="mt-4 p-3 rounded-lg bg-gray-900/60 border border-gray-800 text-[11px] font-mono text-amber-300 space-y-1">
              <p>▶ Error: java.lang.OutOfMemoryError: Java heap space</p>
              <p>▶ Metric: RAM 98.8%, GC overhead 4800ms</p>
            </div>
          </div>

          <button
            onClick={() => handleSimulate('MEMORY_LEAK')}
            disabled={loading}
            className="mt-6 w-full py-2.5 rounded-xl bg-amber-600 hover:bg-amber-500 text-white font-semibold text-xs flex items-center justify-center gap-2 shadow-lg shadow-amber-600/20 transition-all disabled:opacity-50"
          >
            {loading ? <RefreshCw className="w-4 h-4 animate-spin" /> : <>Simulate Memory Leak <ArrowRight className="w-4 h-4" /></>}
          </button>
        </div>

        {/* Scenario 3: High Latency */}
        <div className="glass-card p-6 rounded-2xl glass-card-hover flex flex-col justify-between">
          <div>
            <div className="w-10 h-10 rounded-xl bg-violet-500/10 border border-violet-500/20 flex items-center justify-center text-violet-400 mb-4">
              <Clock className="w-5 h-5" />
            </div>
            <h3 className="text-lg font-bold text-white">API Gateway Latency Spike</h3>
            <p className="text-xs text-gray-400 mt-1.5 leading-relaxed">
              Simulates upstream timeout, worker thread pool exhaustion (200/200 active worker threads), and 4200ms API response latency.
            </p>

            <div className="mt-4 p-3 rounded-lg bg-gray-900/60 border border-gray-800 text-[11px] font-mono text-violet-300 space-y-1">
              <p>▶ Error: Gateway upstream timeout after 5000ms</p>
              <p>▶ Metric: Latency 4200ms, CPU 92.1%</p>
            </div>
          </div>

          <button
            onClick={() => handleSimulate('HIGH_LATENCY')}
            disabled={loading}
            className="mt-6 w-full py-2.5 rounded-xl bg-violet-600 hover:bg-violet-500 text-white font-semibold text-xs flex items-center justify-center gap-2 shadow-lg shadow-violet-600/20 transition-all disabled:opacity-50"
          >
            {loading ? <RefreshCw className="w-4 h-4 animate-spin" /> : <>Simulate High Latency <ArrowRight className="w-4 h-4" /></>}
          </button>
        </div>

        {/* Scenario 4: High Error Rate */}
        <div className="glass-card p-6 rounded-2xl glass-card-hover flex flex-col justify-between">
          <div>
            <div className="w-10 h-10 rounded-xl bg-rose-500/10 border border-rose-500/20 flex items-center justify-center text-rose-400 mb-4">
              <AlertOctagon className="w-5 h-5" />
            </div>
            <h3 className="text-lg font-bold text-white">High Error Rate (85% 500s)</h3>
            <p className="text-xs text-gray-400 mt-1.5 leading-relaxed">
              Generates HTTP 500 Internal Server Errors, NullPointerExceptions, and payload deserialization failures across endpoints.
            </p>

            <div className="mt-4 p-3 rounded-lg bg-gray-900/60 border border-gray-800 text-[11px] font-mono text-rose-300 space-y-1">
              <p>▶ Error: HTTP 500 NullPointerException</p>
              <p>▶ Metric: Error Rate 85.0%</p>
            </div>
          </div>

          <button
            onClick={() => handleSimulate('HIGH_ERROR_RATE')}
            disabled={loading}
            className="mt-6 w-full py-2.5 rounded-xl bg-rose-600 hover:bg-rose-500 text-white font-semibold text-xs flex items-center justify-center gap-2 shadow-lg shadow-rose-600/20 transition-all disabled:opacity-50"
          >
            {loading ? <RefreshCw className="w-4 h-4 animate-spin" /> : <>Simulate High Error Rate <ArrowRight className="w-4 h-4" /></>}
          </button>
        </div>
      </div>
    </div>
  );
};
