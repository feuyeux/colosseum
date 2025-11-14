"""
Agno Colosseo - FastAPI application for multilingual translation and grammar analysis.

This module implements the main FastAPI application with REST API endpoints
for translation, grammar analysis, and health checking.
"""
import logging
from pathlib import Path
from typing import Dict
from fastapi import FastAPI, HTTPException, status, Request
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import HTMLResponse, JSONResponse
from fastapi.staticfiles import StaticFiles
from fastapi.exceptions import RequestValidationError
import uvicorn

from models import (
    TranslationRequest,
    TranslationResult,
    AnalysisRequest,
    AnalysisResult
)
from ollama_client import OllamaClient
from services import TranslationService, AnalysisService
from config import (
    OLLAMA_BASE_URL,
    OLLAMA_MODEL_NAME,
    OLLAMA_TIMEOUT,
    SERVER_HOST,
    SERVER_PORT
)

# Configure logging at INFO level
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)

# Initialize FastAPI app with title, description, and version
app = FastAPI(
    title="Agno Colosseo",
    description="Multilingual translation and grammar analysis service using the agno framework",
    version="1.0.0"
)

# Configure CORS middleware allowing all origins, methods, and headers
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Add exception handler for validation errors
@app.exception_handler(RequestValidationError)
async def validation_exception_handler(request: Request, exc: RequestValidationError):
    """Handle validation errors and log details."""
    logger.error(f"Validation error on {request.url}: {exc.errors()}")
    logger.error(f"Request body: {await request.body()}")
    return JSONResponse(
        status_code=status.HTTP_422_UNPROCESSABLE_ENTITY,
        content={"detail": exc.errors(), "body": str(await request.body())}
    )

# Initialize OllamaClient, TranslationService, and AnalysisService instances
logger.info(f"Initializing Ollama client with base URL: {OLLAMA_BASE_URL}, model: {OLLAMA_MODEL_NAME}")
ollama_client = OllamaClient(
    base_url=OLLAMA_BASE_URL,
    model_name=OLLAMA_MODEL_NAME,
    timeout=OLLAMA_TIMEOUT
)

translation_service = TranslationService(ollama_client)
analysis_service = AnalysisService(ollama_client)

# Static files directory
STATIC_DIR = Path(__file__).parent / "static"


# Implement GET /health endpoint that checks Ollama connectivity and returns status
@app.get("/health", tags=["Health"])
async def health_check() -> Dict[str, str]:
    """
    Health check endpoint that verifies Ollama service connectivity.
    
    Returns:
        dict: Status information with one of:
            - "healthy": All services operational
            - "degraded": Ollama service unavailable
            - "unhealthy": Critical error occurred
    """
    try:
        # Verify Ollama Client connectivity
        is_connected = ollama_client.health_check()
        
        if is_connected:
            logger.info("Health check: healthy - Ollama connected")
            return {
                "status": "healthy",
                "ollama": "connected",
                "message": "All services operational"
            }
        else:
            logger.warning("Health check: degraded - Ollama disconnected")
            return {
                "status": "degraded",
                "ollama": "disconnected",
                "message": "Ollama service unavailable"
            }
    except Exception as e:
        error_msg = f"Health check failed: {str(e)}"
        logger.error(error_msg)
        return {
            "status": "unhealthy",
            "error": error_msg
        }


# Implement POST /api/translateText endpoint accepting TranslationRequest and returning TranslationResult
@app.post("/api/translateText", response_model=TranslationResult, tags=["Translation"])
async def translate_text(request: TranslationRequest) -> TranslationResult:
    """
    Translate text to one or more target languages.
    
    Args:
        request: TranslationRequest containing source text and target languages
        
    Returns:
        TranslationResult: Translation results for each target language
        
    Raises:
        HTTPException: If request validation fails or translation error occurs
    """
    try:
        logger.info(f"Received translation request for {len(request.targetLanguages)} languages")
        
        # Call translation service
        result = await translation_service.translate_text(request)
        
        # Check if there was a validation error
        if result.error and not result.translations:
            logger.error(f"Translation validation failed: {result.error}")
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail=result.error
            )
        
        # Return result (may include partial success with some errors)
        return result
        
    except HTTPException:
        # Re-raise HTTP exceptions
        raise
    except Exception as e:
        error_msg = f"Translation request failed: {str(e)}"
        logger.error(error_msg)
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=error_msg
        )


# Implement GET /api/languages endpoint returning supported languages dictionary
@app.get("/api/languages", response_model=Dict[str, str], tags=["Translation"])
async def get_supported_languages() -> Dict[str, str]:
    """
    Get list of supported languages.
    
    Returns:
        dict: Dictionary mapping language codes to language names
    """
    try:
        logger.info("Retrieving supported languages")
        languages = translation_service.get_supported_languages()
        return languages
    except Exception as e:
        error_msg = f"Failed to retrieve supported languages: {str(e)}"
        logger.error(error_msg)
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=error_msg
        )


# Implement POST /api/analyzeGrammar endpoint accepting AnalysisRequest and returning AnalysisResult
@app.post("/api/analyzeGrammar", response_model=AnalysisResult, tags=["Analysis"])
async def analyze_grammar(request: AnalysisRequest) -> AnalysisResult:
    """
    Analyze grammatical components of text.
    
    Args:
        request: AnalysisRequest containing text and language code
        
    Returns:
        AnalysisResult: Grammar analysis with identified components
        
    Raises:
        HTTPException: If request validation fails or analysis error occurs
    """
    try:
        logger.info(f"Received grammar analysis request for language: {request.languageCode}")
        
        # Call analysis service
        result = await analysis_service.analyze_grammar(request)
        
        # Check if there was a validation error
        if result.error and not result.components:
            logger.error(f"Analysis validation failed: {result.error}")
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail=result.error
            )
        
        # Return result
        return result
        
    except HTTPException:
        # Re-raise HTTP exceptions
        raise
    except Exception as e:
        error_msg = f"Grammar analysis request failed: {str(e)}"
        logger.error(error_msg)
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=error_msg
        )


# Mount static files handler and implement root endpoint
if STATIC_DIR.exists():
    # Mount StaticFiles handler at /static path pointing to static directory
    app.mount("/static", StaticFiles(directory=str(STATIC_DIR)), name="static")
    logger.info(f"Mounted static files from: {STATIC_DIR}")
    
    # Implement GET / endpoint that reads and returns static/index.html file
    @app.get("/", response_class=HTMLResponse, tags=["Web Interface"])
    async def serve_index() -> HTMLResponse:
        """
        Serve the main web interface.
        
        Returns:
            HTMLResponse: HTML content of index.html
            
        Raises:
            HTTPException: If index.html file is not found
        """
        try:
            index_path = STATIC_DIR / "index.html"
            
            if not index_path.exists():
                logger.error(f"index.html not found at: {index_path}")
                raise HTTPException(
                    status_code=status.HTTP_404_NOT_FOUND,
                    detail="index.html file not found"
                )
            
            # Read and return index.html content
            with open(index_path, 'r', encoding='utf-8') as f:
                html_content = f.read()
            
            return HTMLResponse(content=html_content, status_code=200)
            
        except HTTPException:
            # Re-raise HTTP exceptions
            raise
        except Exception as e:
            error_msg = f"Failed to serve index.html: {str(e)}"
            logger.error(error_msg)
            raise HTTPException(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                detail=error_msg
            )
else:
    logger.warning(f"Static directory not found: {STATIC_DIR}")


# Application startup and shutdown events
@app.on_event("startup")
async def startup_event():
    """Log application startup."""
    logger.info("=" * 60)
    logger.info("Agno Colosseo application starting")
    logger.info(f"Server: {SERVER_HOST}:{SERVER_PORT}")
    logger.info(f"Ollama: {OLLAMA_BASE_URL}")
    logger.info(f"Model: {OLLAMA_MODEL_NAME}")
    logger.info("=" * 60)


@app.on_event("shutdown")
async def shutdown_event():
    """Clean up resources on shutdown."""
    logger.info("Agno Colosseo application shutting down")
    await ollama_client.close()


# Main entry point
if __name__ == "__main__":
    logger.info(f"Starting Agno Colosseo on {SERVER_HOST}:{SERVER_PORT}")
    uvicorn.run(
        "main:app",
        host=SERVER_HOST,
        port=SERVER_PORT,
        reload=False,
        log_level="info"
    )
