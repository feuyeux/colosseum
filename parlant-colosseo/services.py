import asyncio
import logging
from typing import Dict, List, Optional
from models import TranslationRequest, TranslationResult, AnalysisRequest, AnalysisResult, GrammarComponent
from agents import TranslationAgent, AnalysisAgent
from ollama_client import OllamaClient
from config import LANGUAGE_NAMES, SUPPORTED_LANGUAGES, MAX_TEXT_LENGTH
import re

logger = logging.getLogger(__name__)

class TranslationService:
    def __init__(self, ollama_client: OllamaClient):
        self.ollama_client = ollama_client
        self.translation_agent = TranslationAgent(ollama_client)
    
    def validate_request(self, request: TranslationRequest) -> Optional[str]:
        """Validate translation request"""
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
        """Translate text using the translation agent"""
        validation_error = self.validate_request(request)
        if validation_error:
            return TranslationResult(error=validation_error)
        
        try:
            logger.info(f"Translating from {request.source_language} to {request.target_language}")
            
            translated_text = await self.translation_agent.translate(
                request.text,
                request.source_language,
                request.target_language,
                request.context
            )
            
            return TranslationResult(
                translated_text=translated_text,
                source_language=request.source_language,
                target_language=request.target_language,
                confidence=0.95
            )
            
        except Exception as e:
            logger.error(f"Translation failed: {e}")
            return TranslationResult(error=f"Translation failed: {str(e)}")
    
    async def translate_to_multiple_languages(self, text: str, source_language: str, target_languages: List[str]) -> Dict[str, TranslationResult]:
        """Translate text to multiple languages in parallel"""
        logger.info(f"Translating to multiple languages: {target_languages}")
        
        tasks = []
        for target_language in target_languages:
            request = TranslationRequest(
                text=text,
                source_language=source_language,
                target_language=target_language
            )
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
        """Get supported languages"""
        return LANGUAGE_NAMES

class AnalysisService:
    def __init__(self, ollama_client: OllamaClient):
        self.ollama_client = ollama_client
        self.analysis_agent = AnalysisAgent(ollama_client)
    
    def validate_request(self, request: AnalysisRequest) -> Optional[str]:
        """Validate analysis request"""
        if not request.text or not request.text.strip():
            return "Text cannot be empty"
        
        if len(request.text) > MAX_TEXT_LENGTH:
            return f"Text exceeds maximum length of {MAX_TEXT_LENGTH} characters"
        
        if request.language not in SUPPORTED_LANGUAGES:
            return f"Unsupported language. Supported: {', '.join(SUPPORTED_LANGUAGES)}"
        
        return None
    
    async def analyze_grammar(self, request: AnalysisRequest) -> AnalysisResult:
        """Analyze grammar using the analysis agent"""
        validation_error = self.validate_request(request)
        if validation_error:
            return AnalysisResult(error=validation_error)
        
        try:
            logger.info(f"Analyzing grammar for {request.language}")
            
            analysis = await self.analysis_agent.analyze(
                request.text,
                request.language
            )
            
            components = self.parse_grammar_components(analysis, request.text)
            
            return AnalysisResult(
                text=request.text,
                language=request.language,
                components=components
            )
            
        except Exception as e:
            logger.error(f"Grammar analysis failed: {e}")
            return AnalysisResult(error=f"Grammar analysis failed: {str(e)}")
    
    def parse_grammar_components(self, analysis: str, original_text: str) -> List[GrammarComponent]:
        """Parse grammar analysis into components"""
        components = []
        
        # Define grammar component types and their colors
        component_types = {
            "noun": {"color": "#FF6B6B", "explanation": "Noun"},
            "verb": {"color": "#4ECDC4", "explanation": "Verb"},
            "adjective": {"color": "#45B7D1", "explanation": "Adjective"},
            "adverb": {"color": "#96CEB4", "explanation": "Adverb"},
            "preposition": {"color": "#FFEAA7", "explanation": "Preposition"},
            "conjunction": {"color": "#DDA0DD", "explanation": "Conjunction"},
            "pronoun": {"color": "#FFB6C1", "explanation": "Pronoun"},
            "article": {"color": "#98FB98", "explanation": "Article"}
        }
        
        # Simple parsing - in a real implementation, you'd want more sophisticated parsing
        lines = analysis.split('\n')
        position = 0
        
        for line in lines:
            line_lower = line.lower()
            for comp_type, comp_info in component_types.items():
                if comp_type in line_lower:
                    components.append(GrammarComponent(
                        component=comp_type,
                        type=comp_type,
                        explanation=comp_info["explanation"],
                        color=comp_info["color"],
                        position=position,
                        length=len(comp_type)
                    ))
                    position += 1
                    break
        
        return components