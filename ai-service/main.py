import logging
from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from models import IncidentAnalysisRequest, IncidentAnalysisResponse
from providers.llm_provider import get_llm_provider

logging.basicConfig(level=logging.INFO, format="%(asctime)s [%(levelname)s] %(name)s: %(message)s")
logger = logging.getLogger("sentinelops-ai")

app = FastAPI(
    title="SentinelOps AI Service",
    description="FastAPI AI Service for Automated Incident Root Cause Analysis and Signal Correlation",
    version="1.0.0"
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

@app.get("/health")
def health_check():
    return {
        "status": "UP",
        "service": "sentinelops-ai",
        "llm_provider": get_llm_provider().__class__.__name__
    }

@app.post("/api/v1/analyze-incident", response_model=IncidentAnalysisResponse)
async def analyze_incident(request: IncidentAnalysisRequest):
    try:
        logger.info(f"Received AI analysis request for incident #{request.incidentId}")
        provider = get_llm_provider()
        analysis = await provider.analyze(request)
        return analysis
    except Exception as e:
        logger.error(f"Error during incident analysis: {str(e)}", exc_info=True)
        raise HTTPException(status_code=500, detail=f"AI analysis failed: {str(e)}")

if __name__ == "__main__":
    import uvicorn
    uvicorn.run("main:app", host="0.0.0.0", port=8000, reload=True)
