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
        if not request.sourceText or not request.sourceText.strip():
            return "Text cannot be empty"
        
        if len(request.sourceText) > MAX_TEXT_LENGTH:
            return f"Text exceeds maximum length of {MAX_TEXT_LENGTH} characters"
        
        if not request.targetLanguages or len(request.targetLanguages) == 0:
            return "At least one target language must be selected"
        
        invalid_languages = [lang for lang in request.targetLanguages if lang not in SUPPORTED_LANGUAGES]
        if invalid_languages:
            return f"Unsupported languages: {', '.join(invalid_languages)}"
        
        return None
    
    async def translate_text(self, request: TranslationRequest) -> TranslationResult:
        """Translate text to multiple languages"""
        validation_error = self.validate_request(request)
        if validation_error:
            return TranslationResult(sourceText=request.sourceText, error=validation_error)
        
        try:
            logger.info(f"Translating to languages: {request.targetLanguages}")
            
            translations = {}
            for target_lang in request.targetLanguages:
                translated_text = await self.translation_agent.translate(
                    request.sourceText,
                    "auto",  # Auto-detect source language
                    target_lang,
                    None
                )
                translations[target_lang] = translated_text
            
            return TranslationResult(
                sourceText=request.sourceText,
                translations=translations
            )
            
        except Exception as e:
            logger.error(f"Translation failed: {e}")
            return TranslationResult(sourceText=request.sourceText, error=f"Translation failed: {str(e)}")
    
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
        
        if request.languageCode not in SUPPORTED_LANGUAGES:
            return f"Unsupported language. Supported: {', '.join(SUPPORTED_LANGUAGES)}"
        
        return None
    
    async def analyze_grammar(self, request: AnalysisRequest) -> AnalysisResult:
        """Analyze grammar using the analysis agent"""
        validation_error = self.validate_request(request)
        if validation_error:
            return AnalysisResult(error=validation_error)
        
        try:
            logger.info(f"Analyzing grammar for {request.languageCode}")
            
            analysis = await self.analysis_agent.analyze(
                request.text,
                request.languageCode
            )
            
            components = self.parse_grammar_components(analysis, request.text)
            
            return AnalysisResult(
                originalText=request.text,
                languageCode=request.languageCode,
                components=components
            )
            
        except Exception as e:
            logger.error(f"Grammar analysis failed: {e}")
            return AnalysisResult(error=f"Grammar analysis failed: {str(e)}")
    
    def parse_grammar_components(self, analysis: str, original_text: str) -> List[GrammarComponent]:
        """Parse grammar analysis into components"""
        components = []
        
        # Define grammar component types and their colors
        component_colors = {
            "NOUN": "#FF6B6B",
            "VERB": "#4ECDC4",
            "ADJECTIVE": "#45B7D1",
            "ADVERB": "#96CEB4",
            "PREPOSITION": "#FFEAA7",
            "CONJUNCTION": "#DDA0DD",
            "PRONOUN": "#FFB6C1",
            "ARTICLE": "#98FB98",
            "DETERMINER": "#B0E0E6",
            "PARTICLE": "#F0E68C"
        }
        
        # Split text into words for simple analysis
        words = original_text.split()
        current_pos = 0
        
        for word in words:
            # Find the word in the original text to get accurate position
            start_index = original_text.find(word, current_pos)
            if start_index == -1:
                continue
            
            end_index = start_index + len(word)
            current_pos = end_index
            
            # Simple heuristic - in real implementation, use proper NLP
            component_type = "NOUN"  # Default
            if len(word) > 0:
                # Very basic type detection
                if word.lower() in ['the', 'a', 'an']:
                    component_type = "ARTICLE"
                elif word.lower() in ['is', 'are', 'was', 'were', 'be', 'been', 'being']:
                    component_type = "VERB"
                elif word.lower() in ['and', 'or', 'but']:
                    component_type = "CONJUNCTION"
                elif word.lower() in ['in', 'on', 'at', 'to', 'from']:
                    component_type = "PREPOSITION"
            
            color = component_colors.get(component_type, "#CCCCCC")
            
            components.append(GrammarComponent(
                text=word,
                componentType=component_type,
                color=color,
                startIndex=start_index,
                endIndex=end_index,
                features={}
            ))
        
        return components