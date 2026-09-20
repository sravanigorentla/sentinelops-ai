import React from 'react';
import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { RoleName } from '../types';

interface ProtectedRouteProps {
  requiredRole?: RoleName;
}

export const ProtectedRoute: React.FC<ProtectedRouteProps> = ({ requiredRole }) => {
  const { user, isAuthenticated, isLoading } = useAuth();

  if (isLoading) {
    return (
      <div className="min-h-screen bg-[#0B0F19] flex items-center justify-center text-indigo-400">
        <div className="animate-spin rounded-full h-10 w-10 border-b-2 border-indigo-500"></div>
      </div>
    );
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  if (requiredRole && user?.role !== requiredRole && user?.role !== 'ROLE_ADMIN') {
    return (
      <div className="p-8 text-center text-rose-400">
        <h2 className="text-xl font-bold">Access Denied</h2>
        <p className="mt-2 text-gray-400">You do not have permission to view this page ({requiredRole} required).</p>
      </div>
    );
  }

  return <Outlet />;
};
