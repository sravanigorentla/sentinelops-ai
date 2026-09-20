import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { AuthProvider } from './context/AuthContext';
import { ProtectedRoute } from './components/ProtectedRoute';
import { Layout } from './components/Layout';

import { Login } from './pages/Login';
import { Register } from './pages/Register';
import { Dashboard } from './pages/Dashboard';
import { Services } from './pages/Services';
import { ServiceDetails } from './pages/ServiceDetails';
import { Incidents } from './pages/Incidents';
import { IncidentDetails } from './pages/IncidentDetails';
import { Logs } from './pages/Logs';
import { Deployments } from './pages/Deployments';
import { Alerts } from './pages/Alerts';
import { Simulator } from './pages/Simulator';
import { AuditLogs } from './pages/AuditLogs';
import { Users } from './pages/Users';

const queryClient = new QueryClient();

export const App: React.FC = () => {
  return (
    <QueryClientProvider client={queryClient}>
      <AuthProvider>
        <Router>
          <Routes>
            {/* Public Auth Routes */}
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />

            {/* Protected App Routes */}
            <Route element={<ProtectedRoute />}>
              <Route
                path="/*"
                element={
                  <Layout>
                    <Routes>
                      <Route path="/dashboard" element={<Dashboard />} />
                      <Route path="/services" element={<Services />} />
                      <Route path="/services/:id" element={<ServiceDetails />} />
                      <Route path="/incidents" element={<Incidents />} />
                      <Route path="/incidents/:id" element={<IncidentDetails />} />
                      <Route path="/logs" element={<Logs />} />
                      <Route path="/deployments" element={<Deployments />} />
                      <Route path="/alerts" element={<Alerts />} />
                      <Route path="/simulator" element={<Simulator />} />
                      <Route path="/audit-logs" element={<AuditLogs />} />
                      
                      {/* Admin Only Route */}
                      <Route element={<ProtectedRoute requiredRole="ROLE_ADMIN" />}>
                        <Route path="/users" element={<Users />} />
                      </Route>

                      <Route path="*" element={<Navigate to="/dashboard" replace />} />
                    </Routes>
                  </Layout>
                }
              />
            </Route>
          </Routes>
        </Router>
      </AuthProvider>
    </QueryClientProvider>
  );
};
