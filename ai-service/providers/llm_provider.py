import os
import json
import logging
from abc import ABC, abstractmethod
import httpx
from models import IncidentAnalysisRequest, IncidentAnalysisResponse

logger = logging.getLogger("sentinelops-ai")

class BaseLLMProvider(ABC):
    @abstractmethod
    async def analyze(self, request: IncidentAnalysisRequest) -> IncidentAnalysisResponse:
        pass

class MockLLMProvider(BaseLLMProvider):
    async def analyze(self, req: IncidentAnalysisRequest) -> IncidentAnalysisResponse:
        logger.info(f"MockLLMProvider analyzing incident #{req.incidentId} for service '{req.serviceName}'")
        
        evidence = []
        actions = []
        signals = []
        
        # Analyze Logs
        error_logs = [l.message for l in (req.recentLogs or []) if l.logLevel in ("ERROR", "FATAL", "WARN") and l.message]
        if error_logs:
            for log_msg in error_logs[:3]:
                evidence.append(f"Log Error: {log_msg}")
        else:
            evidence.append(f"Telemetry metric anomaly detected on service {req.serviceName}")

        # Analyze Deployment Correlation
        if req.recentDeployment:
            dep = req.recentDeployment
            signals.append(f"Recent Release: Version {dep.version} (Commit {dep.commitSha[:7] if dep.commitSha else 'N/A'}) by {dep.deployedBy or 'CI/CD'}")
            actions.append(f"Trigger automated deployment rollback for version {dep.version}")
        
        # Analyze Telemetry Metrics
        if req.recentMetrics:
            latest = req.recentMetrics[0]
            signals.append(f"Latest Telemetry: CPU {latest.cpuUsage}%, Memory {latest.memoryUsage}%, Latency {latest.latencyMs}ms, Error Rate {latest.errorRate}%")
            if latest.latencyMs and latest.latencyMs > 1000:
                evidence.append(f"Latency spike ({latest.latencyMs}ms) exceeds 1000ms SLA limit")
            if latest.errorRate and latest.errorRate > 5.0:
                evidence.append(f"Elevated error rate ({latest.errorRate}%) above 5% threshold")

        summary = f"Root cause analysis for {req.severity} incident #{req.incidentId} on service '{req.serviceName}' ({req.environment}). Correlated with telemetry spikes and error logs."
        
        probable_cause = f"Resource degradation or database connection saturation in {req.serviceName}"
        if any("Hikari" in e or "connection" in e.lower() for e in evidence):
            probable_cause = "Database connection pool exhaustion caused by unindexed query or thread block."
        elif any("OutOfMemory" in e or "heap" in e.lower() for e in evidence):
            probable_cause = "Java Heap space memory leak triggered during batch processing."
        elif req.recentDeployment:
            probable_cause = f"Regression bug or missing database migration introduced in release {req.recentDeployment.version}."

        actions.extend([
            f"Inspect thread pools and active DB connections for {req.serviceName}",
            "Verify database index availability and slow query logs",
            "Monitor CPU/Memory telemetry for recovery after remediation"
        ])

        return IncidentAnalysisResponse(
            summary=summary,
            probableRootCause=probable_cause,
            evidence=evidence,
            recommendedActions=actions,
            relatedSignals=signals,
            confidence=0.92
        )

class OpenAILLMProvider(BaseLLMProvider):
    def __init__(self, api_key: str):
        self.api_key = api_key
        self.api_url = os.getenv("OPENAI_API_BASE", "https://api.openai.com/v1/chat/completions")
        self.model = os.getenv("OPENAI_MODEL", "gpt-4o-mini")

    async def analyze(self, req: IncidentAnalysisRequest) -> IncidentAnalysisResponse:
        system_prompt = (
            "You are an expert AIOps Cloud Reliability Engineer. Analyze the provided cloud incident, "
            "metrics, log traces, and deployment release history. Return structured JSON matching the exact schema."
        )
        
        user_prompt = f"""
        Incident Title: {req.title}
        Description: {req.description}
        Severity: {req.severity}
        Service Name: {req.serviceName}
        Environment: {req.environment}
        Recent Deployment: {req.recentDeployment.model_dump_json() if req.recentDeployment else 'None'}
        Recent Logs: {[l.model_dump_json() for l in (req.recentLogs or [])[:5]]}
        Recent Metrics: {[m.model_dump_json() for m in (req.recentMetrics or [])[:5]]}
        """

        headers = {
            "Authorization": f"Bearer {self.api_key}",
            "Content-Type": "application/json"
        }

        payload = {
            "model": self.model,
            "messages": [
                {"role": "system", "content": system_prompt},
                {"role": "user", "content": user_prompt}
            ],
            "response_format": {"type": "json_object"},
            "temperature": 0.2
        }

        async with httpx.AsyncClient(timeout=30.0) as client:
            resp = await client.post(self.api_url, headers=headers, json=payload)
            resp.raise_for_status()
            data = resp.json()
            content = json.loads(data["choices"][0]["message"]["content"])
            return IncidentAnalysisResponse(**content)

def get_llm_provider() -> BaseLLMProvider:
    api_key = os.getenv("OPENAI_API_KEY")
    if api_key and api_key.strip():
        logger.info("Using OpenAILLMProvider with API key")
        return OpenAILLMProvider(api_key.strip())
    logger.info("Using MockLLMProvider (No OPENAI_API_KEY set)")
    return MockLLMProvider()
