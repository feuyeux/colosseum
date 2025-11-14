"""
Translation and analysis services for Agno Colosseo.

This module implements service classes that handle translation and grammar
analysis requests, including validation and error handling.
"""
import asyncio
import logging
import re
from typing import Dict, List, Optional
from models import (
    TranslationRequest, 
    TranslationResult,
    AnalysisRequest,
    AnalysisResult,
    GrammarComponent
)
from agents import TranslationAgent, AnalysisAgent
from ollama_client import OllamaClient
from config import (
    MAX_TEXT_LENGTH,
    SUPPORTED_LANGUAGES,
    LANGUAGE_NAMES
)

logger = logging.getLogger(__name__)


class TranslationService:
    """Service for handling text translation requests."""
    
    def __init__(self, ollama_client: OllamaClient):
        """
        Initialize translation service.
        
        Args:
            ollama_client: OllamaClient instance for LLM communication
        """
        self.ollama_client = ollama_client
        self.translation_agent = TranslationAgent(ollama_client)
    
    def validate_request(self, request: TranslationRequest) -> Optional[str]:
        """
        Validate translation request parameters.
        
        Args:
            request: TranslationRequest to validate
            
        Returns:
            Optional[str]: Error message if validation fails, None if valid
        """
        # Check for empty text
        if not request.sourceText or not request.sourceText.strip():
            return "Source text cannot be empty"
        
        # Check text length limit
        if len(request.sourceText) > MAX_TEXT_LENGTH:
            return f"Source text exceeds maximum length of {MAX_TEXT_LENGTH} characters"
        
        # Check target languages presence
        if not request.targetLanguages or len(request.targetLanguages) == 0:
            return "At least one target language must be specified"
        
        # Check supported language codes
        unsupported_languages = [
            lang for lang in request.targetLanguages 
            if lang not in SUPPORTED_LANGUAGES
        ]
        
        if unsupported_languages:
            return f"Unsupported language codes: {', '.join(unsupported_languages)}"
        
        return None

    async def translate_text(self, request: TranslationRequest) -> TranslationResult:
        """
        Translate text to multiple target languages.
        
        Args:
            request: TranslationRequest containing source text and target languages
            
        Returns:
            TranslationResult: Result containing translations for each target language
        """
        # Validate request
        validation_error = self.validate_request(request)
        if validation_error:
            logger.error(f"Translation request validation failed: {validation_error}")
            return TranslationResult(
                sourceText=request.sourceText,
                translations={},
                error=validation_error
            )
        
        translations: Dict[str, str] = {}
        errors: Dict[str, str] = {}
        
        # Create translation tasks for each target language
        async def translate_to_language(target_language: str) -> tuple[str, str, Optional[str]]:
            """Helper function to translate to a single language."""
            try:
                # Detect source language (simplified - assume English for now)
                source_language = "en"
                
                # Invoke TranslationAgent for translation
                translated_text = await self.translation_agent.translate(
                    source_text=request.sourceText,
                    source_language=source_language,
                    target_language=target_language
                )
                
                return target_language, translated_text, None
                
            except Exception as e:
                error_msg = str(e)
                logger.error(f"Translation failed for {target_language}: {error_msg}")
                return target_language, "", error_msg
        
        # Execute all translation tasks concurrently
        logger.info(f"Starting translation to {len(request.targetLanguages)} languages")
        translation_tasks = [
            translate_to_language(lang) 
            for lang in request.targetLanguages
        ]
        
        results = await asyncio.gather(*translation_tasks, return_exceptions=True)
        
        # Process results
        for result in results:
            if isinstance(result, Exception):
                logger.error(f"Translation task failed with exception: {result}")
                continue
            
            target_language, translated_text, error = result
            
            if error:
                errors[target_language] = error
            else:
                translations[target_language] = translated_text
        
        # Determine overall error message
        overall_error = None
        if errors and not translations:
            # All translations failed
            overall_error = "All translations failed"
        elif errors:
            # Some translations failed
            failed_langs = ", ".join(errors.keys())
            overall_error = f"Translation failed for: {failed_langs}"
        
        logger.info(f"Translation completed: {len(translations)} successful, {len(errors)} failed")
        
        return TranslationResult(
            sourceText=request.sourceText,
            translations=translations,
            error=overall_error
        )
    
    def get_supported_languages(self) -> Dict[str, str]:
        """
        Get dictionary of supported languages.
        
        Returns:
            Dict[str, str]: Dictionary mapping language codes to language names
        """
        return LANGUAGE_NAMES


class AnalysisService:
    """Service for handling grammar analysis requests."""
    
    # Grammar component type to color mapping
    COMPONENT_COLORS = {
        'NOUN': '#FF6B6B',
        'VERB': '#4ECDC4',
        'ADJECTIVE': '#45B7D1',
        'ADVERB': '#96CEB4',
        'PREPOSITION': '#FFEAA7',
        'CONJUNCTION': '#DDA0DD',
        'PRONOUN': '#FFB6C1',
        'ARTICLE': '#98FB98',
        'DETERMINER': '#B0E0E6',
        'PARTICLE': '#F0E68C',
        'OTHER': '#D3D3D3'
    }
    
    def __init__(self, ollama_client: OllamaClient):
        """
        Initialize analysis service.
        
        Args:
            ollama_client: OllamaClient instance for LLM communication
        """
        self.ollama_client = ollama_client
        self.analysis_agent = AnalysisAgent(ollama_client)
    
    def validate_request(self, request: AnalysisRequest) -> Optional[str]:
        """
        Validate grammar analysis request parameters.
        
        Args:
            request: AnalysisRequest to validate
            
        Returns:
            Optional[str]: Error message if validation fails, None if valid
        """
        # Check for empty text
        if not request.text or not request.text.strip():
            return "Text cannot be empty"
        
        # Check text length limit
        if len(request.text) > MAX_TEXT_LENGTH:
            return f"Text exceeds maximum length of {MAX_TEXT_LENGTH} characters"
        
        # Check supported language code
        if request.languageCode not in SUPPORTED_LANGUAGES:
            return f"Unsupported language code: {request.languageCode}"
        
        return None
    
    async def analyze_grammar(self, request: AnalysisRequest) -> AnalysisResult:
        """
        Analyze grammatical components of text.
        
        Args:
            request: AnalysisRequest containing text and language code
            
        Returns:
            AnalysisResult: Result containing grammar components
        """
        # Validate request
        validation_error = self.validate_request(request)
        if validation_error:
            logger.error(f"Analysis request validation failed: {validation_error}")
            return AnalysisResult(
                originalText=request.text,
                languageCode=request.languageCode,
                components=[],
                error=validation_error
            )
        
        try:
            # Invoke AnalysisAgent for grammar analysis
            logger.info(f"Starting grammar analysis for {request.languageCode} text")
            analysis_result = await self.analysis_agent.analyze(
                text=request.text,
                language_code=request.languageCode
            )
            
            # Parse results into grammar components
            components = self.parse_grammar_components(
                analysis=analysis_result,
                original_text=request.text
            )
            
            logger.info(f"Grammar analysis completed: {len(components)} components identified")
            
            return AnalysisResult(
                originalText=request.text,
                languageCode=request.languageCode,
                components=components,
                error=None
            )
            
        except Exception as e:
            error_msg = f"Grammar analysis failed: {str(e)}"
            logger.error(error_msg)
            return AnalysisResult(
                originalText=request.text,
                languageCode=request.languageCode,
                components=[],
                error=error_msg
            )
    
    def parse_grammar_components(
        self, 
        analysis: str, 
        original_text: str
    ) -> List[GrammarComponent]:
        """
        Parse grammar analysis into structured components.
        
        This method extracts words from the original text and assigns
        component types based on the analysis result. It attempts to
        parse structured analysis output, but falls back to simple
        word extraction if parsing fails.
        
        Args:
            analysis: Analysis result string from AnalysisAgent
            original_text: Original text that was analyzed
            
        Returns:
            List[GrammarComponent]: List of grammar components with types and positions
        """
        components: List[GrammarComponent] = []
        
        try:
            # Try to parse structured analysis output
            # Expected format: word|TYPE or word (TYPE) or similar patterns
            component_map = self._parse_structured_analysis(analysis)
            
            if component_map:
                # Use parsed component types
                components = self._create_components_from_map(
                    original_text, 
                    component_map
                )
            else:
                # Fallback: simple word extraction with basic type detection
                components = self._create_components_from_text(original_text)
                
        except Exception as e:
            logger.warning(f"Error parsing grammar components: {e}. Using fallback method.")
            # Fallback to simple word extraction
            components = self._create_components_from_text(original_text)
        
        return components
    
    def _parse_structured_analysis(self, analysis: str) -> Dict[str, str]:
        """
        Parse structured analysis output into word-to-type mapping.
        
        Attempts to extract word and type pairs from various formats:
        - word|TYPE
        - word (TYPE)
        - word: TYPE
        - TYPE: word
        
        Args:
            analysis: Analysis result string
            
        Returns:
            Dict[str, str]: Mapping of words to component types
        """
        component_map: Dict[str, str] = {}
        
        # Pattern 1: word|TYPE or word (TYPE) or word: TYPE
        pattern1 = r'(\w+)\s*[\|\(\:]\s*([A-Z]+)'
        matches = re.findall(pattern1, analysis)
        for word, comp_type in matches:
            if comp_type in self.COMPONENT_COLORS:
                component_map[word.lower()] = comp_type
        
        # Pattern 2: TYPE: word or TYPE - word
        pattern2 = r'([A-Z]+)\s*[\:\-]\s*(\w+)'
        matches = re.findall(pattern2, analysis)
        for comp_type, word in matches:
            if comp_type in self.COMPONENT_COLORS:
                component_map[word.lower()] = comp_type
        
        return component_map
    
    def _create_components_from_map(
        self, 
        text: str, 
        component_map: Dict[str, str]
    ) -> List[GrammarComponent]:
        """
        Create grammar components using word-to-type mapping.
        
        Args:
            text: Original text
            component_map: Mapping of words to component types
            
        Returns:
            List[GrammarComponent]: List of grammar components
        """
        components: List[GrammarComponent] = []
        
        # Extract words with their positions
        word_pattern = r'\b\w+\b'
        for match in re.finditer(word_pattern, text):
            word = match.group()
            start_index = match.start()
            end_index = match.end()
            
            # Get component type from map or default to OTHER
            component_type = component_map.get(word.lower(), 'OTHER')
            color = self.COMPONENT_COLORS.get(component_type, self.COMPONENT_COLORS['OTHER'])
            
            component = GrammarComponent(
                text=word,
                componentType=component_type,
                color=color,
                startIndex=start_index,
                endIndex=end_index,
                features={}
            )
            components.append(component)
        
        return components
    
    def _create_components_from_text(self, text: str) -> List[GrammarComponent]:
        """
        Create grammar components using simple heuristic-based type detection.
        
        This is a fallback method that uses basic patterns to guess
        component types when structured analysis is not available.
        
        Args:
            text: Original text
            
        Returns:
            List[GrammarComponent]: List of grammar components
        """
        components: List[GrammarComponent] = []
        
        # Common articles and determiners
        articles = {'a', 'an', 'the'}
        determiners = {'this', 'that', 'these', 'those', 'my', 'your', 'his', 'her', 'its', 'our', 'their'}
        pronouns = {'i', 'you', 'he', 'she', 'it', 'we', 'they', 'me', 'him', 'her', 'us', 'them'}
        prepositions = {'in', 'on', 'at', 'by', 'for', 'with', 'from', 'to', 'of', 'about', 'into', 'through'}
        conjunctions = {'and', 'or', 'but', 'nor', 'yet', 'so', 'for', 'because', 'although', 'while'}
        
        # Extract words with their positions
        word_pattern = r'\b\w+\b'
        for match in re.finditer(word_pattern, text):
            word = match.group()
            word_lower = word.lower()
            start_index = match.start()
            end_index = match.end()
            
            # Determine component type using simple heuristics
            if word_lower in articles:
                component_type = 'ARTICLE'
            elif word_lower in determiners:
                component_type = 'DETERMINER'
            elif word_lower in pronouns:
                component_type = 'PRONOUN'
            elif word_lower in prepositions:
                component_type = 'PREPOSITION'
            elif word_lower in conjunctions:
                component_type = 'CONJUNCTION'
            elif word_lower.endswith('ly'):
                component_type = 'ADVERB'
            elif word_lower.endswith(('ing', 'ed', 'en')):
                component_type = 'VERB'
            else:
                # Default to NOUN for other words
                component_type = 'NOUN'
            
            color = self.COMPONENT_COLORS.get(component_type, self.COMPONENT_COLORS['OTHER'])
            
            component = GrammarComponent(
                text=word,
                componentType=component_type,
                color=color,
                startIndex=start_index,
                endIndex=end_index,
                features={}
            )
            components.append(component)
        
        return components
