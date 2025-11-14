"""
Agno-based agents for translation and grammar analysis.

This module implements agent classes using the agno framework
to perform translation and grammar analysis tasks.
"""
import logging
from typing import Optional
from agno.agent import Agent
from ollama_client import OllamaClient
from config import (
    TRANSLATION_PROMPT,
    ANALYSIS_PROMPT,
    OLLAMA_MODEL_NAME,
    LANGUAGE_NAMES
)

logger = logging.getLogger(__name__)


class TranslationAgent:
    """Agent for translating text using agno framework."""
    
    def __init__(self, ollama_client: OllamaClient):
        """
        Initialize translation agent.
        
        Args:
            ollama_client: OllamaClient instance for LLM communication
        """
        self.ollama_client = ollama_client
        # Note: agno doesn't directly support Ollama, so we'll use the ollama_client directly
        # instead of agno's Agent class for now
        self.agent = None
    
    async def translate(
        self,
        source_text: str,
        source_language: str,
        target_language: str
    ) -> str:
        """
        Translate text from source language to target language.
        
        Args:
            source_text: The text to translate
            source_language: Source language code
            target_language: Target language code
            
        Returns:
            str: Translated text
            
        Raises:
            Exception: If translation fails with descriptive error message
        """
        try:
            # Get language name for better prompt context
            language_name = LANGUAGE_NAMES.get(target_language, target_language)
            
            # Format the translation prompt with parameters
            formatted_prompt = TRANSLATION_PROMPT.format(
                target_language=target_language,
                language_name=language_name,
                source_text=source_text
            )
            
            # Execute translation using ollama client directly
            logger.info(f"Translating text to {target_language} ({language_name})")
            translated_text = await self.ollama_client.generate(formatted_prompt)
            
            if not translated_text or not translated_text.strip():
                raise Exception(f"Translation returned empty result for language: {target_language}")
            
            logger.info(f"Successfully translated to {target_language}")
            return translated_text.strip()
                
        except Exception as e:
            error_msg = f"Failed to translate to {target_language}: {str(e)}"
            logger.error(error_msg)
            raise Exception(error_msg)


class AnalysisAgent:
    """Agent for analyzing grammatical components using agno framework."""
    
    def __init__(self, ollama_client: OllamaClient):
        """
        Initialize analysis agent.
        
        Args:
            ollama_client: OllamaClient instance for LLM communication
        """
        self.ollama_client = ollama_client
        # Note: agno doesn't directly support Ollama, so we'll use the ollama_client directly
        # instead of agno's Agent class for now
        self.agent = None
    
    async def analyze(
        self,
        text: str,
        language_code: str
    ) -> str:
        """
        Analyze grammatical components of text.
        
        Args:
            text: The text to analyze
            language_code: Language code of the text
            
        Returns:
            str: Grammar analysis result
            
        Raises:
            Exception: If analysis fails with descriptive error message
        """
        try:
            # Get language name for better prompt context
            language_name = LANGUAGE_NAMES.get(language_code, language_code)
            
            # Format the analysis prompt with parameters
            formatted_prompt = ANALYSIS_PROMPT.format(
                language_name=language_name,
                text=text
            )
            
            # Execute grammar analysis using ollama client directly
            logger.info(f"Analyzing grammar for {language_code} ({language_name}) text")
            analysis_result = await self.ollama_client.generate(formatted_prompt)
            
            if not analysis_result or not analysis_result.strip():
                raise Exception(f"Analysis returned empty result for language: {language_code}")
            
            logger.info(f"Successfully analyzed grammar for {language_code}")
            return analysis_result.strip()
                
        except Exception as e:
            error_msg = f"Failed to analyze grammar for {language_code}: {str(e)}"
            logger.error(error_msg)
            raise Exception(error_msg)
