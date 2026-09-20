import React, { useEffect, useState } from 'react';
import { api } from '../services/api';
import { AlertRule, Notification } from '../types';
import { StatusBadge } from '../components/StatusBadge';
import { Bell, ShieldAlert, Send, RefreshCw } from 'lucide-react';

export const Alerts: React.FC = () => {
  const [rules, setRules] = useState<AlertRule[]>([]);
  const [notifications, setNotifications] = useState<Notification[]>([]);
  const [loading, setLoading] = useState(true);

  const fetchAlerts = async () => {
    try {
      setLoading(true);
      const [rData, nData] = await Promise.all([
        api.getAlertRules(),
        api.getNotifications(),
      ]);
      setRules(rData || []);
      setNotifications(nData || []);
    } catch (err) {
      console.error('Failed to fetch alert data', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchAlerts();
  }, []);

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-extrabold text-white tracking-tight">Alert Rules & Multi-Channel Notifications</h1>
          <p className="text-sm text-gray-400 mt-1">Configurable telemetry thresholds, Slack webhooks, and SMTP email abstractions.</p>
        </div>
        <button
          onClick={fetchAlerts}
          className="flex items-center gap-2 px-3.5 py-2 rounded-lg bg-gray-800 text-xs font-medium text-gray-200 hover:bg-gray-700"
        >
          <RefreshCw className="w-3.5 h-3.5" /> Refresh Notifications
        </button>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Alert Rules Card */}
        <div className="glass-card p-6 rounded-2xl">
          <h2 className="text-base font-bold text-white mb-4 flex items-center gap-2">
            <Bell className="w-4 h-4 text-indigo-400" /> Active Alert Rules ({rules.length})
          </h2>

          {loading ? (
            <div className="flex items-center justify-center h-48 text-indigo-400">
              <RefreshCw className="w-6 h-6 animate-spin" />
            </div>
          ) : (
            <div className="space-y-3 text-xs">
              {rules.map((r) => (
                <div key={r.id} className="p-3.5 rounded-xl bg-gray-900/60 border border-gray-800 flex items-center justify-between">
                  <div>
                    <span className="font-bold text-white">{r.serviceName}</span>
                    <p className="text-gray-400 mt-0.5">
                      Trigger if <strong className="text-indigo-400">{r.metricName}</strong> {r.comparisonOperator} {r.thresholdValue}
                    </p>
                  </div>
                  <StatusBadge status={r.severity} />
                </div>
              ))}
            </div>
          )}
        </div>

        {/* Notifications Dispatch History Card */}
        <div className="glass-card p-6 rounded-2xl">
          <h2 className="text-base font-bold text-white mb-4 flex items-center gap-2">
            <Send className="w-4 h-4 text-indigo-400" /> Dispatched Notification Log
          </h2>

          {loading ? (
            <div className="flex items-center justify-center h-48 text-indigo-400">
              <RefreshCw className="w-6 h-6 animate-spin" />
            </div>
          ) : (
            <div className="space-y-3 text-xs">
              {notifications.length === 0 ? (
                <p className="text-gray-500 py-4 text-center">No alert notifications sent yet.</p>
              ) : (
                notifications.map((n) => (
                  <div key={n.id} className="p-3 rounded-xl bg-gray-900/60 border border-gray-800 space-y-1">
                    <div className="flex items-center justify-between">
                      <span className="font-bold text-indigo-300 font-mono">[{n.channel}] {n.recipient}</span>
                      <StatusBadge status={n.status} />
                    </div>
                    <p className="text-gray-300 line-clamp-2">{n.message}</p>
                    <span className="block text-[10px] text-gray-500 font-mono pt-1">{new Date(n.sentAt).toLocaleString()}</span>
                  </div>
                ))
              )}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};
