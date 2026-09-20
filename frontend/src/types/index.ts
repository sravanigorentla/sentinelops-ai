export type RoleName = 'ROLE_ADMIN' | 'ROLE_DEVELOPER' | 'ROLE_VIEWER';

export type ServiceStatus = 'HEALTHY' | 'DEGRADED' | 'DOWN';

export type IncidentSeverity = 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';

export type IncidentStatus = 'OPEN' | 'ACKNOWLEDGED' | 'INVESTIGATING' | 'RESOLVED' | 'CLOSED';

export type LogLevel = 'INFO' | 'WARN' | 'ERROR' | 'DEBUG';

export type DeploymentStatus = 'SUCCESS' | 'FAILED' | 'IN_PROGRESS';

export type RollbackStatus = 'NONE' | 'PENDING' | 'IN_PROGRESS' | 'COMPLETED' | 'FAILED';

export interface User {
  id: number;
  email: string;
  fullName: string;
  role: RoleName;
  createdAt?: string;
}

export interface AuthResponse {
  token: string;
  tokenType: string;
  user: User;
}

export interface Service {
  id: number;
  name: string;
  description?: string;
  environment: string;
  owner: string;
  repository?: string;
  version?: string;
  healthEndpoint?: string;
  status: ServiceStatus;
  createdAt: string;
  updatedAt: string;
}

export interface Metric {
  id: number;
  serviceId: number;
  serviceName: string;
  cpuUsage: number;
  memoryUsage: number;
  requestRate: number;
  latencyMs: number;
  errorRate: number;
  timestamp: string;
}

export interface Log {
  id: number;
  timestamp: string;
  serviceId: number;
  serviceName: string;
  environment: string;
  logLevel: LogLevel;
  message: string;
  requestId?: string;
  metadata?: string;
}

export interface Deployment {
  id: number;
  serviceId: number;
  serviceName: string;
  version: string;
  commitSha: string;
  branch: string;
  environment: string;
  deployedBy: string;
  deploymentTime: string;
  status: DeploymentStatus;
  rolledBack: boolean;
  rolledBackAt?: string;
}

export interface AIAnalysis {
  id: number;
  incidentId: number;
  summary: string;
  probableRootCause: string;
  evidence: string[];
  recommendedActions: string[];
  relatedSignals: string[];
  confidence: number;
  generatedAt: string;
  verifiedBy?: User;
}

export interface Incident {
  id: number;
  title: string;
  description?: string;
  severity: IncidentSeverity;
  status: IncidentStatus;
  serviceId: number;
  serviceName: string;
  detectedAt: string;
  acknowledgedAt?: string;
  resolvedAt?: string;
  assignedTo?: User;
  rootCause?: string;
  resolution?: string;
  deployment?: Deployment;
  rollbackStatus?: RollbackStatus;
  aiAnalysis?: AIAnalysis;
}

export interface IncidentComment {
  id: number;
  incidentId: number;
  user: User;
  comment: string;
  createdAt: string;
}

export interface AlertRule {
  id: number;
  serviceId: number;
  serviceName: string;
  metricName: string;
  comparisonOperator: string;
  thresholdValue: number;
  severity: IncidentSeverity;
  enabled: boolean;
}

export interface Notification {
  id: number;
  incidentId?: number;
  channel: string;
  recipient: string;
  status: string;
  message: string;
  sentAt: string;
}

export interface AuditLog {
  id: number;
  userId?: number;
  userEmail?: string;
  action: string;
  targetType: string;
  targetId?: string;
  details?: string;
  ipAddress?: string;
  timestamp: string;
}

export interface DashboardSummary {
  totalServices: number;
  healthyServices: number;
  degradedServices: number;
  downServices: number;
  activeIncidents: number;
  criticalIncidents: number;
  avgCpuUsage: number;
  avgMemoryUsage: number;
  totalRequestRate: number;
  avgLatencyMs: number;
  avgErrorRate: number;
  recentServices: Service[];
  recentIncidents: Incident[];
  recentDeployments: Deployment[];
  incidentTrends: Record<string, number>;
}
