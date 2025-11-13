from fastapi import FastAPI, HTTPException, Query
from fastapi.middleware.cors import CORSMiddleware
from fastapi.staticfiles import StaticFiles
from fastapi.responses import HTMLResponse
from typing import List, Dict
import logging
import uvicorn

from models import TranslationRequest, TranslationResult, AnalysisRequest, AnalysisResult
from services import TranslationService, AnalysisService
from ollama_client import OllamaClient
from config import SERVER_HOST, SERVER_PORT

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

app = FastAPI(title="AG2 Colosseo", description="Multilingual Translation and Grammar Analysis with AG2", version="1.0.0")

app.add_middleware(CORSMiddleware, allow_origins=["*"], allow_credentials=True, allow_methods=["*"], allow_headers=["*"])

ollama_client = OllamaClient()
translation_service = TranslationService(ollama_client)
analysis_service = AnalysisService(ollama_client)

@app.get("/health")
async def health_check():
    try:
        ok = ollama_client.health_check()
        return {"status": "healthy" if ok else "degraded", "ollama": "connected" if ok else "disconnected"}
    except Exception as e:
        logger.error(f"Health check failed: {e}")
        return {"status": "unhealthy", "error": str(e)}

@app.post("/api/translateText", response_model=TranslationResult)
async def translate_text(request: TranslationRequest):
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
async def translate_multiple(text: str = Query(...), source_language: str = Query(...), target_languages: List[str] = Query(...)):
    try:
        return await translation_service.translate_to_multiple_languages(text, source_language, target_languages)
    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"Multiple translation error: {e}")
        raise HTTPException(status_code=500, detail=f"Multiple translation failed: {str(e)}")

@app.get("/api/languages")
async def get_supported_languages() -> Dict[str, str]:
    return translation_service.get_supported_languages()

@app.post("/api/analyzeGrammar", response_model=AnalysisResult)
async def analyze_grammar(request: AnalysisRequest):
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
async def analyze_and_translate(request: AnalysisRequest, target_language: str = Query(None)):
    try:
        analysis_result = await analysis_service.analyze_grammar(request)
        if analysis_result.error:
            raise HTTPException(status_code=400, detail=analysis_result.error)
        if target_language and target_language != request.language:
            tr = await translation_service.translate_text(TranslationRequest(text=request.text, source_language=request.language, target_language=target_language))
            return {"analysis": analysis_result, "translation": tr}
        return {"analysis": analysis_result}
    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"Combined analysis error: {e}")
        raise HTTPException(status_code=500, detail=f"Combined analysis failed: {str(e)}")

app.mount("/static", StaticFiles(directory="static"), name="static")

@app.get("/", response_class=HTMLResponse)
async def serve_index():
    try:
        with open("static/index.html", "r", encoding="utf-8") as f:
            return HTMLResponse(content=f.read())
    except FileNotFoundError:
        return HTMLResponse(content="<h1>Web interface not found</h1>", status_code=404)

if __name__ == "__main__":
    uvicorn.run(app, host=SERVER_HOST, port=SERVER_PORT)
