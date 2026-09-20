import React, { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { api } from '../services/api';
import { Incident, IncidentComment } from '../types';
import { StatusBadge } from '../components/StatusBadge';
import {
  AlertTriangle,
  Bot,
  GitCommit,
  RotateCcw,
  CheckCircle,
  MessageSquare,
  ArrowLeft,
  Sparkles,
  RefreshCw,
  Send
} from 'lucide-react';

export const IncidentDetails: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const incidentId = parseInt(id || '0', 10);

  const [incident, setIncident] = useState<Incident | null>(null);
  const [comments, setComments] = useState<IncidentComment[]>([]);
  const [newComment, setNewComment] = useState('');
  const [loading, setLoading] = useState(true);
  const [aiLoading, setAiLoading] = useState(false);
  const [rollbackLoading, setRollbackLoading] = useState(false);

  const loadData = async () => {
    try {
      setLoading(true);
      const [inc, cList] = await Promise.all([
        api.getIncidentById(incidentId),
        api.getIncidentComments(incidentId),
      ]);
      setIncident(inc);
      setComments(cList || []);
    } catch (err) {
      console.error('Failed to load incident details', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (incidentId) loadData();
  }, [incidentId]);

  const handleTriggerAI = async () => {
    try {
      setAiLoading(true);
      await api.triggerAIAnalysis(incidentId);
      await loadData();
    } catch (err: any) {
      alert(err.message || 'AI Analysis trigger failed.');
    } finally {
      setAiLoading(false);
    }
  };

  const handleTriggerRollback = async () => {
    if (!confirm('Are you sure you want to execute a deployment rollback for this incident?')) return;
    try {
      setRollbackLoading(true);
      await api.triggerRollback(incidentId);
      await loadData();
    } catch (err: any) {
      alert(err.message || 'Rollback failed.');
    } finally {
      setRollbackLoading(false);
    }
  };

  const handleAddComment = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!newComment.trim()) return;
    try {
      await api.addIncidentComment(incidentId, newComment);
      setNewComment('');
      const cList = await api.getIncidentComments(incidentId);
      setComments(cList || []);
    } catch (err: any) {
      alert(err.message || 'Failed to post comment');
    }
  };

  if (loading || !incident) {
    return (
      <div className="flex items-center justify-center h-64 text-indigo-400">
        <RefreshCw className="w-8 h-8 animate-spin" />
      </div>
    );
  }

  const ai = incident.aiAnalysis;

  return (
    <div className="space-y-6 max-w-6xl mx-auto">
      {/* Back Button & Top Navigation */}
      <div>
        <Link to="/incidents" className="inline-flex items-center gap-1.5 text-xs text-indigo-400 hover:underline mb-2">
          <ArrowLeft className="w-3.5 h-3.5" /> Back to Incidents
        </Link>
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
          <div>
            <div className="flex items-center gap-3">
              <h1 className="text-2xl font-extrabold text-white">Incident #{incident.id}: {incident.title}</h1>
              <StatusBadge status={incident.severity} />
              <StatusBadge status={incident.status} />
            </div>
            <p className="text-xs text-gray-400 mt-1">Service: <strong className="text-gray-200">{incident.serviceName}</strong> • Detected {new Date(incident.detectedAt).toLocaleString()}</p>
          </div>

          <div className="flex items-center gap-3">
            {/* Developer-Triggered AI Analysis Button */}
            <button
              onClick={handleTriggerAI}
              disabled={aiLoading}
              className="flex items-center gap-2 px-4 py-2 rounded-lg bg-gradient-to-r from-violet-600 to-indigo-600 hover:from-violet-500 hover:to-indigo-500 text-xs font-semibold text-white shadow-lg shadow-indigo-600/25 transition-all disabled:opacity-50"
            >
              <Sparkles className={`w-4 h-4 ${aiLoading ? 'animate-spin' : ''}`} />
              {aiLoading ? 'Running AI Engine...' : 'Trigger AI Analysis'}
            </button>

            {/* Simulated Rollback Button */}
            {incident.deployment && incident.status !== 'RESOLVED' && (
              <button
                onClick={handleTriggerRollback}
                disabled={rollbackLoading}
                className="flex items-center gap-2 px-4 py-2 rounded-lg bg-rose-600/20 hover:bg-rose-600/30 border border-rose-500/30 text-rose-300 text-xs font-semibold transition-all disabled:opacity-50"
              >
                <RotateCcw className={`w-4 h-4 ${rollbackLoading ? 'animate-spin' : ''}`} />
                {rollbackLoading ? 'Rolling back...' : 'Trigger Rollback'}
              </button>
            )}
          </div>
        </div>
      </div>

      {/* Incident Overview Card */}
      <div className="glass-card p-6 rounded-2xl space-y-4">
        <h2 className="text-sm font-bold text-white uppercase tracking-wider">Incident Overview</h2>
        <p className="text-xs text-gray-300 leading-relaxed">{incident.description || 'No detailed description provided.'}</p>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-4 pt-4 border-t border-gray-800 text-xs">
          <div>
            <span className="text-gray-500">Assigned Developer:</span>
            <p className="font-semibold text-gray-200 mt-0.5">{incident.assignedTo?.fullName || 'Unassigned'}</p>
          </div>
          <div>
            <span className="text-gray-500">Acknowledged At:</span>
            <p className="font-mono text-gray-300 mt-0.5">{incident.acknowledgedAt ? new Date(incident.acknowledgedAt).toLocaleString() : 'Not Acknowledged'}</p>
          </div>
          <div>
            <span className="text-gray-500">Rollback Status:</span>
            <div className="mt-0.5"><StatusBadge status={incident.rollbackStatus || 'NONE'} /></div>
          </div>
        </div>
      </div>

      {/* AI INCIDENT ANALYSIS CARD */}
      <div className="glass-card p-6 rounded-2xl border-indigo-500/40 relative overflow-hidden">
        <div className="absolute top-0 right-0 px-4 py-1.5 bg-gradient-to-l from-indigo-600 to-violet-600 text-[10px] font-bold text-white uppercase tracking-widest rounded-bl-xl shadow-lg">
          AI-Generated Incident Analysis
        </div>

        <div className="flex items-center gap-3 mb-4">
          <div className="w-10 h-10 rounded-xl bg-indigo-500/10 border border-indigo-500/30 flex items-center justify-center text-indigo-400">
            <Bot className="w-6 h-6" />
          </div>
          <div>
            <h2 className="text-base font-extrabold text-white">Root Cause & Evidence Analysis</h2>
            <p className="text-xs text-gray-400">
              Confidence Score: <strong className="text-indigo-400">{( (ai?.confidence || 0) * 100).toFixed(0)}%</strong> • Requires Developer Verification
            </p>
          </div>
        </div>

        {!ai ? (
          <div className="p-8 text-center bg-gray-900/40 rounded-xl border border-gray-800">
            <Sparkles className="w-8 h-8 text-indigo-400 mx-auto mb-2 opacity-60" />
            <p className="text-xs text-gray-300 font-semibold">No AI Root Cause Analysis generated yet for this incident.</p>
            <p className="text-xs text-gray-500 mt-1">Click the <strong className="text-indigo-400">"Trigger AI Analysis"</strong> button above to run the AIOps engine.</p>
          </div>
        ) : (
          <div className="space-y-6 text-xs">
            {/* Executive Summary */}
            <div className="p-4 rounded-xl bg-gray-900/80 border border-gray-800">
              <h3 className="font-bold text-indigo-300 mb-1">Executive Summary</h3>
              <p className="text-gray-300 leading-relaxed">{ai.summary}</p>
            </div>

            {/* Probable Root Cause */}
            <div className="p-4 rounded-xl bg-rose-500/10 border border-rose-500/20">
              <h3 className="font-bold text-rose-400 mb-1">Probable Root Cause</h3>
              <p className="text-rose-200 font-medium">{ai.probableRootCause}</p>
            </div>

            {/* Log & Metric Evidence */}
            <div>
              <h3 className="font-bold text-gray-200 mb-2">Evidence & Anomaly Traces ({ai.evidence.length})</h3>
              <ul className="space-y-2 font-mono text-[11px]">
                {ai.evidence.map((ev, idx) => (
                  <li key={idx} className="p-2.5 rounded bg-gray-900/60 border border-gray-800 text-amber-300 flex items-start gap-2">
                    <span className="text-amber-500">▶</span>
                    <span>{ev}</span>
                  </li>
                ))}
              </ul>
            </div>

            {/* Recommended Action Checklist */}
            <div>
              <h3 className="font-bold text-gray-200 mb-2">Recommended Mitigation Plan</h3>
              <div className="space-y-2">
                {ai.recommendedActions.map((act, idx) => (
                  <div key={idx} className="p-3 rounded-lg bg-emerald-500/5 border border-emerald-500/20 flex items-start gap-3">
                    <CheckCircle className="w-4 h-4 text-emerald-400 shrink-0 mt-0.5" />
                    <span className="text-gray-200">{act}</span>
                  </div>
                ))}
              </div>
            </div>
          </div>
        )}
      </div>

      {/* Correlated Deployment Banner */}
      {incident.deployment && (
        <div className="glass-card p-6 rounded-2xl border-indigo-500/20 flex items-center justify-between">
          <div className="flex items-center gap-3">
            <GitCommit className="w-6 h-6 text-indigo-400" />
            <div>
              <h3 className="text-sm font-bold text-white">Correlated Release: {incident.deployment.version}</h3>
              <p className="text-xs text-gray-400 font-mono">Commit {incident.deployment.commitSha.substring(0, 7)} by {incident.deployment.deployedBy}</p>
            </div>
          </div>

          <button
            onClick={handleTriggerRollback}
            disabled={rollbackLoading || incident.status === 'RESOLVED'}
            className="flex items-center gap-2 px-3.5 py-2 rounded-lg bg-rose-600/20 hover:bg-rose-600/30 text-rose-300 border border-rose-500/30 text-xs font-semibold"
          >
            <RotateCcw className="w-3.5 h-3.5" />
            Rollback Commit
          </button>
        </div>
      )}

      {/* Developer Comments Feed */}
      <div className="glass-card p-6 rounded-2xl space-y-4">
        <h2 className="text-base font-bold text-white flex items-center gap-2">
          <MessageSquare className="w-4 h-4 text-indigo-400" /> Developer Comments & Incident Timeline
        </h2>

        <div className="space-y-3">
          {comments.length === 0 ? (
            <p className="text-xs text-gray-500 py-2">No comments posted yet.</p>
          ) : (
            comments.map((c) => (
              <div key={c.id} className="p-3.5 rounded-xl bg-gray-900/60 border border-gray-800 text-xs">
                <div className="flex items-center justify-between mb-1.5">
                  <span className="font-bold text-indigo-400">{c.user.fullName} ({c.user.role.replace('ROLE_', '')})</span>
                  <span className="text-[10px] text-gray-500 font-mono">{new Date(c.createdAt).toLocaleTimeString()}</span>
                </div>
                <p className="text-gray-300">{c.comment}</p>
              </div>
            ))
          )}
        </div>

        <form onSubmit={handleAddComment} className="flex gap-3 pt-2">
          <input
            type="text"
            value={newComment}
            onChange={(e) => setNewComment(e.target.value)}
            className="flex-1 bg-gray-900 border border-gray-800 rounded-lg px-3.5 py-2 text-xs text-white focus:outline-none focus:border-indigo-500"
            placeholder="Add investigation comment..."
          />
          <button
            type="submit"
            className="px-4 py-2 rounded-lg bg-indigo-600 hover:bg-indigo-500 text-white font-semibold text-xs flex items-center gap-1.5"
          >
            <Send className="w-3.5 h-3.5" /> Post
          </button>
        </form>
      </div>
    </div>
  );
};
