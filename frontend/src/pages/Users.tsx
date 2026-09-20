import React, { useEffect, useState } from 'react';
import { api } from '../services/api';
import { User } from '../types';
import { Users as UsersIcon, Shield, RefreshCw } from 'lucide-react';

export const Users: React.FC = () => {
  const [users, setUsers] = useState<User[]>([]);
  const [loading, setLoading] = useState(true);

  const fetchUsers = async () => {
    try {
      setLoading(true);
      const res = await api.getUsers();
      setUsers(res || []);
    } catch (err) {
      console.error('Failed to fetch users', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchUsers();
  }, []);

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-extrabold text-white tracking-tight">Platform User Management</h1>
          <p className="text-sm text-gray-400 mt-1">Role-based access control (RBAC) user list.</p>
        </div>
        <button
          onClick={fetchUsers}
          className="flex items-center gap-2 px-3.5 py-2 rounded-lg bg-gray-800 text-xs font-medium text-gray-200 hover:bg-gray-700"
        >
          <RefreshCw className="w-3.5 h-3.5" /> Refresh Users
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
                  <th className="p-4">ID</th>
                  <th className="p-4">Full Name</th>
                  <th className="p-4">Email</th>
                  <th className="p-4">Role</th>
                  <th className="p-4">Joined Date</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-800/60">
                {users.map((u) => (
                  <tr key={u.id} className="hover:bg-gray-900/40 transition-colors">
                    <td className="p-4 font-mono text-gray-400">#{u.id}</td>
                    <td className="p-4 font-bold text-white">{u.fullName}</td>
                    <td className="p-4 text-gray-300 font-mono">{u.email}</td>
                    <td className="p-4">
                      <span className="inline-block px-2.5 py-1 rounded-full text-xs font-bold font-mono bg-indigo-500/10 text-indigo-400 border border-indigo-500/20 uppercase">
                        {u.role.replace('ROLE_', '')}
                      </span>
                    </td>
                    <td className="p-4 text-gray-400 font-mono">{u.createdAt ? new Date(u.createdAt).toLocaleDateString() : 'N/A'}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}
    </div>
  );
};
