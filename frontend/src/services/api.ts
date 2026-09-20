const API_BASE_URL = '/api/v1';

async function fetchWithAuth(url: string, options: RequestInit = {}) {
  const token = localStorage.getItem('token');
  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
    ...(options.headers as Record<string, string>),
  };

  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }

  const response = await fetch(`${API_BASE_URL}${url}`, {
    ...options,
    headers,
  });

  if (!response.ok) {
    let errorMsg = `HTTP Error ${response.status}: ${response.statusText}`;
    try {
      const errorData = await response.json();
      if (errorData.message) errorMsg = errorData.message;
      else if (errorData.error) errorMsg = errorData.error;
    } catch (e) {
      // Ignore JSON parse error
    }
    throw new Error(errorMsg);
  }

  if (response.status === 24) return null;
  return response.json();
}

export const api = {
  // Auth
  login: (data: any) => fetchWithAuth('/auth/login', { method: 'POST', body: JSON.stringify(data) }),
  register: (data: any) => fetchWithAuth('/auth/register', { method: 'POST', body: JSON.stringify(data) }),

  // Dashboard
  getDashboardSummary: () => fetchWithAuth('/dashboard/summary'),

  // Services
  getServices: () => fetchWithAuth('/services'),
  getServiceById: (id: number) => fetchWithAuth(`/services/${id}`),
  createService: (data: any) => fetchWithAuth('/services', { method: 'POST', body: JSON.stringify(data) }),
  updateService: (id: number, data: any) => fetchWithAuth(`/services/${id}`, { method: 'PUT', body: JSON.stringify(data) }),
  deleteService: (id: number) => fetchWithAuth(`/services/${id}`, { method: 'DELETE' }),

  // Metrics & Logs
  getServiceMetrics: (serviceId: number, limit = 30) => fetchWithAuth(`/metrics/service/${serviceId}?limit=${limit}`),
  searchLogs: (params: { serviceId?: number; level?: string; search?: string; page?: number; size?: number }) => {
    const query = new URLSearchParams();
    if (params.serviceId) query.append('serviceId', params.serviceId.toString());
    if (params.level) query.append('level', params.level);
    if (params.search) query.append('search', params.search);
    if (params.page !== undefined) query.append('page', params.page.toString());
    if (params.size !== undefined) query.append('size', params.size.toString());
    return fetchWithAuth(`/logs?${query.toString()}`);
  },

  // Incidents
  getIncidents: (params: { serviceId?: number; status?: string; severity?: string; page?: number; size?: number }) => {
    const query = new URLSearchParams();
    if (params.serviceId) query.append('serviceId', params.serviceId.toString());
    if (params.status) query.append('status', params.status);
    if (params.severity) query.append('severity', params.severity);
    if (params.page !== undefined) query.append('page', params.page.toString());
    if (params.size !== undefined) query.append('size', params.size.toString());
    return fetchWithAuth(`/incidents?${query.toString()}`);
  },
  getIncidentById: (id: number) => fetchWithAuth(`/incidents/${id}`),
  updateIncident: (id: number, data: any) => fetchWithAuth(`/incidents/${id}`, { method: 'PATCH', body: JSON.stringify(data) }),
  triggerAIAnalysis: (id: number) => fetchWithAuth(`/incidents/${id}/ai-analysis`, { method: 'POST' }),
  triggerRollback: (id: number) => fetchWithAuth(`/incidents/${id}/rollback`, { method: 'POST' }),
  getIncidentComments: (id: number) => fetchWithAuth(`/incidents/${id}/comments`),
  addIncidentComment: (id: number, comment: string) => fetchWithAuth(`/incidents/${id}/comments`, { method: 'POST', body: JSON.stringify({ comment }) }),

  // Deployments
  getDeployments: () => fetchWithAuth('/deployments'),
  getServiceDeployments: (serviceId: number) => fetchWithAuth(`/deployments/service/${serviceId}`),
  createDeployment: (data: any) => fetchWithAuth('/deployments', { method: 'POST', body: JSON.stringify(data) }),

  // Alerts
  getAlertRules: () => fetchWithAuth('/alerts/rules'),
  getNotifications: () => fetchWithAuth('/alerts/notifications'),

  // Simulator
  triggerSimulation: (serviceId: number, simulationType: string) =>
    fetchWithAuth('/simulator/trigger', { method: 'POST', body: JSON.stringify({ serviceId, simulationType }) }),

  // Audit Logs
  getAuditLogs: (page = 0, size = 50) => fetchWithAuth(`/audit-logs?page=${page}&size=${size}`),

  // Users
  getUsers: () => fetchWithAuth('/users'),
};
