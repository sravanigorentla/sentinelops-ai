from pydantic import BaseModel
from typing import List, Optional

class LogEntry(BaseModel):
    id: Optional[int] = None
    timestamp: Optional[str] = None
    logLevel: Optional[str] = None
    message: Optional[str] = None
    requestId: Optional[str] = None
    metadata: Optional[str] = None

class MetricEntry(BaseModel):
    cpuUsage: Optional[float] = None
    memoryUsage: Optional[float] = None
    requestRate: Optional[float] = None
    latencyMs: Optional[float] = None
    errorRate: Optional[float] = None
    timestamp: Optional[str] = None

class DeploymentEntry(BaseModel):
    version: Optional[str] = None
    commitSha: Optional[str] = None
    branch: Optional[str] = None
    environment: Optional[str] = None
    deployedBy: Optional[str] = None
    deploymentTime: Optional[str] = None
    status: Optional[str] = None

class IncidentAnalysisRequest(BaseModel):
    incidentId: int
    title: str
    description: Optional[str] = None
    severity: str
    serviceName: str
    environment: str
    recentLogs: Optional[List[LogEntry]] = []
    recentMetrics: Optional[List[MetricEntry]] = []
    recentDeployment: Optional[DeploymentEntry] = None

class IncidentAnalysisResponse(BaseModel):
    summary: str
    probableRootCause: str
    evidence: List[str]
    recommendedActions: List[str]
    relatedSignals: List[str]
    confidence: float
