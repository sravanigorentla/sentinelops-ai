import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { api } from '../services/api';
import { Service } from '../types';
import { StatusBadge } from '../components/StatusBadge';
import { Server, Plus, ExternalLink, Activity, Shield, RefreshCw } from 'lucide-react';

export const Services: React.FC = () => {
  const [services, setServices] = useState<Service[]>([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [formData, setFormData] = useState({
    name: '',
    description: '',
    environment: 'production',
    owner: '',
    repository: '',
    healthEndpoint: '/health'
  });

  const fetchServices = async () => {
    try {
      setLoading(true);
      const res = await api.getServices();
      setServices(res);
    } catch (err) {
      console.error('Failed to fetch services', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchServices();
  }, []);

  const handleCreate = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await api.createService(formData);
      setShowModal(false);
      setFormData({ name: '', description: '', environment: 'production', owner: '', repository: '', healthEndpoint: '/health' });
      fetchServices();
    } catch (err: any) {
      alert(err.message || 'Failed to create service');
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-extrabold text-white tracking-tight">Monitored Services</h1>
          <p className="text-sm text-gray-400 mt-1">Manage cloud microservices, environment bounds, and live health metrics.</p>
        </div>
        <button
          onClick={() => setShowModal(true)}
          className="flex items-center gap-2 px-4 py-2 rounded-lg bg-indigo-600 hover:bg-indigo-500 text-xs font-semibold text-white shadow-lg shadow-indigo-600/20 transition-all"
        >
          <Plus className="w-4 h-4" />
          Add Service
        </button>
      </div>

      {loading ? (
        <div className="flex items-center justify-center h-64 text-indigo-400">
          <RefreshCw className="w-8 h-8 animate-spin" />
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {services.map((srv) => (
            <div key={srv.id} className="glass-card p-6 rounded-2xl glass-card-hover flex flex-col justify-between">
              <div>
                <div className="flex items-start justify-between gap-3 mb-3">
                  <div>
                    <span className="text-[10px] font-mono uppercase tracking-widest text-indigo-400 font-bold bg-indigo-500/10 px-2 py-0.5 rounded border border-indigo-500/20">
                      {srv.environment}
                    </span>
                    <h3 className="text-lg font-extrabold text-white mt-2">
                      <Link to={`/services/${srv.id}`} className="hover:text-indigo-400 transition-colors">
                        {srv.name}
                      </Link>
                    </h3>
                  </div>
                  <StatusBadge status={srv.status} />
                </div>

                <p className="text-xs text-gray-400 line-clamp-2 mb-4">{srv.description || 'No description provided.'}</p>

                <div className="space-y-2 text-xs text-gray-300 pt-3 border-t border-gray-800/60">
                  <div className="flex items-center justify-between">
                    <span className="text-gray-500">Owner:</span>
                    <span className="font-medium text-gray-200">{srv.owner}</span>
                  </div>
                  <div className="flex items-center justify-between">
                    <span className="text-gray-500">Version:</span>
                    <span className="font-mono text-indigo-400 font-semibold">{srv.version || 'v1.0.0'}</span>
                  </div>
                  {srv.repository && (
                    <div className="flex items-center justify-between">
                      <span className="text-gray-500">Repo:</span>
                      <a href={`https://${srv.repository}`} target="_blank" rel="noreferrer" className="text-indigo-400 hover:underline flex items-center gap-1 font-mono text-[11px]">
                        {srv.repository.replace('github.com/', '')} <ExternalLink className="w-3 h-3" />
                      </a>
                    </div>
                  )}
                </div>
              </div>

              <div className="mt-6 pt-4 border-t border-gray-800/60 flex items-center justify-between">
                <Link
                  to={`/services/${srv.id}`}
                  className="text-xs font-semibold text-indigo-400 hover:text-indigo-300 flex items-center gap-1.5"
                >
                  <Activity className="w-3.5 h-3.5" />
                  View Telemetry
                </Link>
                <span className="text-[10px] text-gray-500 font-mono">Updated {new Date(srv.updatedAt).toLocaleTimeString()}</span>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Add Service Modal */}
      {showModal && (
        <div className="fixed inset-0 bg-black/70 backdrop-blur-sm flex items-center justify-center p-4 z-50">
          <div className="glass-card w-full max-w-lg p-6 rounded-2xl border border-gray-700">
            <h2 className="text-lg font-bold text-white mb-4">Add Monitored Microservice</h2>
            <form onSubmit={handleCreate} className="space-y-4 text-xs">
              <div>
                <label className="block text-gray-400 mb-1">Service Name</label>
                <input
                  type="text"
                  required
                  value={formData.name}
                  onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                  className="w-full bg-gray-900 border border-gray-800 rounded-lg p-2.5 text-white focus:border-indigo-500"
                  placeholder="e.g. notification-service"
                />
              </div>

              <div>
                <label className="block text-gray-400 mb-1">Description</label>
                <textarea
                  value={formData.description}
                  onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                  className="w-full bg-gray-900 border border-gray-800 rounded-lg p-2.5 text-white focus:border-indigo-500 h-20"
                  placeholder="Handles email & SMS delivery queues..."
                />
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-gray-400 mb-1">Environment</label>
                  <select
                    value={formData.environment}
                    onChange={(e) => setFormData({ ...formData, environment: e.target.value })}
                    className="w-full bg-gray-900 border border-gray-800 rounded-lg p-2.5 text-white"
                  >
                    <option value="production">production</option>
                    <option value="staging">staging</option>
                    <option value="development">development</option>
                  </select>
                </div>

                <div>
                  <label className="block text-gray-400 mb-1">Owner / Team</label>
                  <input
                    type="text"
                    required
                    value={formData.owner}
                    onChange={(e) => setFormData({ ...formData, owner: e.target.value })}
                    className="w-full bg-gray-900 border border-gray-800 rounded-lg p-2.5 text-white"
                    placeholder="Platform Eng"
                  />
                </div>
              </div>

              <div>
                <label className="block text-gray-400 mb-1">Git Repository</label>
                <input
                  type="text"
                  value={formData.repository}
                  onChange={(e) => setFormData({ ...formData, repository: e.target.value })}
                  className="w-full bg-gray-900 border border-gray-800 rounded-lg p-2.5 text-white"
                  placeholder="github.com/sentinelops/notification-service"
                />
              </div>

              <div className="flex justify-end gap-3 pt-4 border-t border-gray-800">
                <button
                  type="button"
                  onClick={() => setShowModal(false)}
                  className="px-4 py-2 rounded-lg bg-gray-800 text-gray-300 font-medium"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 rounded-lg bg-indigo-600 text-white font-semibold"
                >
                  Save Service
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
