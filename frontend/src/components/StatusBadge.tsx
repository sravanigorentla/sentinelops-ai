import React from 'react';

interface StatusBadgeProps {
  status: string;
  type?: 'service' | 'incident' | 'severity' | 'rollback' | 'level';
}

export const StatusBadge: React.FC<StatusBadgeProps> = ({ status, type = 'service' }) => {
  const getColors = () => {
    switch (status.toUpperCase()) {
      // Service & Deployment Status
      case 'HEALTHY':
      case 'SUCCESS':
      case 'COMPLETED':
      case 'RESOLVED':
      case 'CLOSED':
        return 'bg-emerald-500/10 text-emerald-400 border-emerald-500/20';

      case 'DEGRADED':
      case 'ACKNOWLEDGED':
      case 'INVESTIGATING':
      case 'MEDIUM':
      case 'PENDING':
      case 'WARN':
        return 'bg-amber-500/10 text-amber-400 border-amber-500/20';

      case 'DOWN':
      case 'CRITICAL':
      case 'FAILED':
      case 'ERROR':
      case 'HIGH':
        return 'bg-rose-500/10 text-rose-400 border-rose-500/20';

      case 'OPEN':
      case 'LOW':
      case 'INFO':
      default:
        return 'bg-blue-500/10 text-blue-400 border-blue-500/20';
    }
  };

  return (
    <span className={`inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-semibold border ${getColors()}`}>
      <span className="w-1.5 h-1.5 rounded-full bg-current animate-pulse"></span>
      {status}
    </span>
  );
};
