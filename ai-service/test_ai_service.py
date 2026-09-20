import pytest
from fastapi.testclient import TestClient
from main import app

client = TestClient(app)

def test_health_check():
    response = client.get("/health")
    assert response.status_code == 200
    data = response.json()
    assert data["status"] == "UP"
    assert data["service"] == "sentinelops-ai"
    assert "llm_provider" in data

def test_analyze_incident_mock_provider():
    payload = {
        "incidentId": 1,
        "title": "Database Connection Pool Saturation",
        "description": "Payment service latency high",
        "severity": "HIGH",
        "serviceName": "payment-service",
        "environment": "production",
        "recentLogs": [
            {
                "id": 10,
                "logLevel": "ERROR",
                "message": "Database query timeout exceeded 5000ms: SELECT * FROM transactions",
                "requestId": "req-123"
            }
        ],
        "recentMetrics": [
            {
                "cpuUsage": 85.0,
                "memoryUsage": 90.0,
                "requestRate": 200.0,
                "latencyMs": 1250.0,
                "errorRate": 12.5
            }
        ],
        "recentDeployment": {
            "version": "v2.4.1",
            "commitSha": "a1b2c3d4e5f6",
            "branch": "main",
            "environment": "production",
            "deployedBy": "Sarah Connor"
        }
    }

    response = client.post("/api/v1/analyze-incident", json=payload)
    assert response.status_code == 200
    data = response.json()
    assert "summary" in data
    assert "probableRootCause" in data
    assert len(data["evidence"]) > 0
    assert len(data["recommendedActions"]) > 0
    assert data["confidence"] >= 0.0

def test_analyze_incident_minimal_payload():
    payload = {
        "incidentId": 2,
        "title": "CPU Spike Alert",
        "description": "CPU above 95%",
        "severity": "CRITICAL",
        "serviceName": "auth-service",
        "environment": "production"
    }

    response = client.post("/api/v1/analyze-incident", json=payload)
    assert response.status_code == 200
    data = response.json()
    assert "summary" in data
    assert "probableRootCause" in data

def test_analyze_incident_invalid_request():
    payload = {
        "title": "Missing incidentId"
    }

    response = client.post("/api/v1/analyze-incident", json=payload)
    assert response.status_code == 422
