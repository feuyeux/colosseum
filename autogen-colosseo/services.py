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
        self.ollama_client = ollama_client
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
        validation_error = self.validate_request(request)
        if validation_error:
            return TranslationResult(error=validation_error)
        try:
            translated_text = await asyncio.get_event_loop().run_in_executor(
                None,
                self.translation_chain.translate,
                request.text,
                request.source_language,
                request.target_language,
                request.context,
            )
            return TranslationResult(
                translated_text=translated_text,
                source_language=request.source_language,
                target_language=request.target_language,
                confidence=0.95,
            )
        except Exception as e:
            logger.error(f"Translation failed: {e}")
            return TranslationResult(error=f"Translation failed: {str(e)}")
    async def translate_to_multiple_languages(self, text: str, source_language: str, target_languages: List[str]) -> Dict[str, TranslationResult]:
        tasks = []
        for target_language in target_languages:
            request = TranslationRequest(text=text, source_language=source_language, target_language=target_language)
            tasks.append(self.translate_text(request))
        results = await asyncio.gather(*tasks, return_exceptions=True)
        translations = {}
        for i, result in enumerate(results):
            if isinstance(result, Exception):
                translations[target_languages[i]] = TranslationResult(error=str(result))
            else:
                translations[target_languages[i]] = result
        return translations
    def get_supported_languages(self) -> Dict[str, str]:
        return LANGUAGE_NAMES

class AnalysisService:
    def __init__(self, ollama_client: OllamaClient):
        self.ollama_client = ollama_client
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
        validation_error = self.validate_request(request)
        if validation_error:
            return AnalysisResult(error=validation_error)
        try:
            analysis = await asyncio.get_event_loop().run_in_executor(
                None,
                self.analysis_chain.analyze,
                request.text,
                request.language,
            )
            components = []
            lines = analysis.split('\n')
            position = 0
            for line in lines:
                lower = line.lower()
                for comp_type, color in {
                    "noun": "#FF6B6B",
                    "verb": "#4ECDC4",
                    "adjective": "#45B7D1",
                    "adverb": "#96CEB4",
                    "preposition": "#FFEAA7",
                    "conjunction": "#DDA0DD",
                    "pronoun": "#FFB6C1",
                    "article": "#98FB98",
                }.items():
                    if comp_type in lower:
                        components.append(GrammarComponent(component=comp_type, type=comp_type, explanation=comp_type, color=color, position=position, length=len(comp_type)))
                        position += 1
                        break
            return AnalysisResult(text=request.text, language=request.language, components=components)
        except Exception as e:
            logger.error(f"Grammar analysis failed: {e}")
            return AnalysisResult(error=f"Grammar analysis failed: {str(e)}")
