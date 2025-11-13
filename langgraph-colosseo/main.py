from fastapi import FastAPI, HTTPException, Query
from fastapi.middleware.cors import CORSMiddleware
from fastapi.staticfiles import StaticFiles
from fastapi.responses import HTMLResponse
from typing import List, Dict
import asyncio
import logging
import uvicorn

from models import TranslationRequest, TranslationResult, AnalysisRequest, AnalysisResult
from services import TranslationService, AnalysisService
from ollama_client import OllamaClient
from config import SERVER_HOST, SERVER_PORT, SUPPORTED_LANGUAGES

# Configure logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# Initialize FastAPI app
app = FastAPI(
    title="LangGraph Colosseo",
    description="Multilingual Translation and Grammar Analysis with LangGraph",
    version="1.0.0"
)

# Add CORS middleware
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Initialize services
ollama_client = OllamaClient()
translation_service = TranslationService(ollama_client)
analysis_service = AnalysisService(ollama_client)

# Health check endpoint
@app.get("/health")
async def health_check():
    """Check if the service is healthy"""
    try:
        ollama_healthy = ollama_client.health_check()
        return {
            "status": "healthy" if ollama_healthy else "degraded",
            "ollama": "connected" if ollama_healthy else "disconnected"
        }
    except Exception as e:
        logger.error(f"Health check failed: {e}")
        return {"status": "unhealthy", "error": str(e)}

# Translation endpoints
@app.post("/api/translateText", response_model=TranslationResult)
async def translate_text(request: TranslationRequest):
    """Translate text from one language to another"""
    try:
        result = await translation_service.translate_text(request)
        if result.error:
            raise HTTPException(status_code=400, detail=result.error)
        return result
    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"Translation error: {e}")
        raise HTTPException(status_code=500, detail=f"Translation failed: {str(e)}")

@app.post("/api/translateMultiple")
async def translate_multiple(
    text: str = Query(..., description="Text to translate"),
    source_language: str = Query(..., description="Source language code"),
    target_languages: List[str] = Query(..., description="List of target language codes")
):
    """Translate text to multiple languages"""
    try:
        # Validate target languages
        invalid_languages = [lang for lang in target_languages if lang not in SUPPORTED_LANGUAGES]
        if invalid_languages:
            raise HTTPException(
                status_code=400, 
                detail=f"Unsupported languages: {', '.join(invalid_languages)}. Supported: {', '.join(SUPPORTED_LANGUAGES)}"
            )
        
        results = await translation_service.translate_to_multiple_languages(
            text, source_language, target_languages
        )
        return results
    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"Multiple translation error: {e}")
        raise HTTPException(status_code=500, detail=f"Multiple translation failed: {str(e)}")

@app.get("/api/languages")
async def get_supported_languages() -> Dict[str, str]:
    """Get supported languages"""
    return translation_service.get_supported_languages()

# Analysis endpoints
@app.post("/api/analyzeGrammar", response_model=AnalysisResult)
async def analyze_grammar(request: AnalysisRequest):
    """Analyze grammatical components of text"""
    try:
        result = await analysis_service.analyze_grammar(request)
        if result.error:
            raise HTTPException(status_code=400, detail=result.error)
        return result
    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"Grammar analysis error: {e}")
        raise HTTPException(status_code=500, detail=f"Grammar analysis failed: {str(e)}")

@app.post("/api/analyzeAndTranslate")
async def analyze_and_translate(
    request: AnalysisRequest,
    target_language: str = Query(None, description="Optional target language for translation")
):
    """Combined grammar analysis and translation"""
    try:
        # First analyze grammar
        analysis_result = await analysis_service.analyze_grammar(request)
        if analysis_result.error:
            raise HTTPException(status_code=400, detail=analysis_result.error)
        
        # If target language is provided, also translate
        if target_language and target_language != request.language:
            from models import TranslationRequest
            translation_request = TranslationRequest(
                text=request.text,
                source_language=request.language,
                target_language=target_language
            )
            translation_result = await translation_service.translate_text(translation_request)
            
            return {
                "analysis": analysis_result,
                "translation": translation_result
            }
        
        return {"analysis": analysis_result}
    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"Combined analysis error: {e}")
        raise HTTPException(status_code=500, detail=f"Combined analysis failed: {str(e)}")

# Serve static files (web interface)
app.mount("/static", StaticFiles(directory="static"), name="static")

@app.get("/", response_class=HTMLResponse)
async def serve_index():
    """Serve the main web interface"""
    try:
        with open("static/index.html", "r", encoding="utf-8") as f:
            return HTMLResponse(content=f.read())
    except FileNotFoundError:
        logger.error("index.html not found in static directory")
        return HTMLResponse(content="<h1>Web interface not found</h1>", status_code=404)

if __name__ == "__main__":
    logger.info(f"Starting LangGraph Colosseo on {SERVER_HOST}:{SERVER_PORT}")
    uvicorn.run(app, host=SERVER_HOST, port=SERVER_PORT)