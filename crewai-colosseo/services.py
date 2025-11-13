import asyncio
import logging
from typing import Dict, List, Optional
from models import TranslationRequest, TranslationResult, AnalysisRequest, AnalysisResult, GrammarComponent
from chains import TranslationChain, AnalysisChain
from ollama_client import OllamaClient
from config import LANGUAGE_NAMES, SUPPORTED_LANGUAGES, MAX_TEXT_LENGTH

logger = logging.getLogger(__name__)

class TranslationService:
    def __init__(self, ollama_client: OllamaClient):
        self.translation_chain = TranslationChain(ollama_client)
    def validate_request(self, request: TranslationRequest) -> Optional[str]:
        if not request.text or not request.text.strip():
            return "Text cannot be empty"
        if len(request.text) > MAX_TEXT_LENGTH:
            return f"Text exceeds maximum length of {MAX_TEXT_LENGTH} characters"
        if request.source_language not in SUPPORTED_LANGUAGES:
            return f"Unsupported source language. Supported: {', '.join(SUPPORTED_LANGUAGES)}"
        if request.target_language not in SUPPORTED_LANGUAGES:
            return f"Unsupported target language. Supported: {', '.join(SUPPORTED_LANGUAGES)}"
        if request.source_language == request.target_language:
            return "Source and target languages cannot be the same"
        return None
    async def translate_text(self, request: TranslationRequest) -> TranslationResult:
        err = self.validate_request(request)
        if err:
            return TranslationResult(error=err)
        try:
            translated_text = await asyncio.get_event_loop().run_in_executor(None, self.translation_chain.translate, request.text, request.source_language, request.target_language, request.context)
            return TranslationResult(translated_text=translated_text, source_language=request.source_language, target_language=request.target_language, confidence=0.95)
        except Exception as e:
            logger.error(f"Translation failed: {e}")
            return TranslationResult(error=f"Translation failed: {str(e)}")
    async def translate_to_multiple_languages(self, text: str, source_language: str, target_languages: List[str]) -> Dict[str, TranslationResult]:
        tasks = [self.translate_text(TranslationRequest(text=text, source_language=source_language, target_language=t)) for t in target_languages]
        results = await asyncio.gather(*tasks, return_exceptions=True)
        out = {}
        for i, r in enumerate(results):
            out[target_languages[i]] = TranslationResult(error=str(r)) if isinstance(r, Exception) else r
        return out
    def get_supported_languages(self) -> Dict[str, str]:
        return LANGUAGE_NAMES

class AnalysisService:
    def __init__(self, ollama_client: OllamaClient):
        self.analysis_chain = AnalysisChain(ollama_client)
    def validate_request(self, request: AnalysisRequest) -> Optional[str]:
        if not request.text or not request.text.strip():
            return "Text cannot be empty"
        if len(request.text) > MAX_TEXT_LENGTH:
            return f"Text exceeds maximum length of {MAX_TEXT_LENGTH} characters"
        if request.language not in SUPPORTED_LANGUAGES:
            return f"Unsupported language. Supported: {', '.join(SUPPORTED_LANGUAGES)}"
        return None
    async def analyze_grammar(self, request: AnalysisRequest) -> AnalysisResult:
        err = self.validate_request(request)
        if err:
            return AnalysisResult(error=err)
        try:
            analysis = await asyncio.get_event_loop().run_in_executor(None, self.analysis_chain.analyze, request.text, request.language)
            components = []
            position = 0
            for token in analysis.split():
                for comp_type, color in {"noun": "#FF6B6B", "verb": "#4ECDC4", "adjective": "#45B7D1", "adverb": "#96CEB4"}.items():
                    if comp_type in token.lower():
                        components.append(GrammarComponent(component=comp_type, type=comp_type, explanation=comp_type, color=color, position=position, length=len(comp_type)))
                        position += 1
                        break
            return AnalysisResult(text=request.text, language=request.language, components=components)
        except Exception as e:
            logger.error(f"Grammar analysis failed: {e}")
            return AnalysisResult(error=f"Grammar analysis failed: {str(e)}")
