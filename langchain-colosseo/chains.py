from langchain.llms import Ollama
from langchain.prompts import PromptTemplate
from langchain.chains import LLMChain
from langchain.schema import BaseOutputParser
from typing import Dict, Any, Optional
import logging

logger = logging.getLogger(__name__)

class TranslationOutputParser(BaseOutputParser):
    """Parser for translation output"""
    
    def parse(self, text: str) -> str:
        """Parse the translation output"""
        return text.strip()

class AnalysisOutputParser(BaseOutputParser):
    """Parser for grammar analysis output"""
    
    def parse(self, text: str) -> str:
        """Parse the grammar analysis output"""
        return text.strip()

class TranslationChain:
    def __init__(self, ollama_client):
        self.ollama_client = ollama_client
        self.llm = self._create_llm()
        self.translation_chain = self._create_translation_chain()
    
    def _create_llm(self):
        """Create Ollama LLM instance"""
        return Ollama(
            base_url="http://localhost:11434",
            model="qwen2.5:latest"
        )
    
    def _create_translation_chain(self):
        """Create translation chain with prompt template"""
        translation_template = """You are a professional translator. Translate the given text from {source_language} to {target_language}.
        
Text to translate: {text}

Context: {context}

Provide only the translation without any additional explanation or commentary. Ensure the translation is accurate and natural-sounding."""
        
        translation_prompt = PromptTemplate(
            input_variables=["text", "source_language", "target_language", "context"],
            template=translation_template
        )
        
        return LLMChain(
            llm=self.llm,
            prompt=translation_prompt,
            output_parser=TranslationOutputParser()
        )
    
    def translate(self, text: str, source_language: str, target_language: str, context: Optional[str] = None) -> str:
        """Translate text using the translation chain"""
        try:
            result = self.translation_chain.run(
                text=text,
                source_language=source_language,
                target_language=target_language,
                context=context or ""
            )
            return result.strip()
        except Exception as e:
            logger.error(f"Translation chain error: {e}")
            raise Exception(f"Translation failed: {str(e)}")

class AnalysisChain:
    def __init__(self, ollama_client):
        self.ollama_client = ollama_client
        self.llm = self._create_llm()
        self.analysis_chain = self._create_analysis_chain()
    
    def _create_llm(self):
        """Create Ollama LLM instance"""
        return Ollama(
            base_url="http://localhost:11434",
            model="qwen2.5:latest"
        )
    
    def _create_analysis_chain(self):
        """Create grammar analysis chain with prompt template"""
        analysis_template = """You are a linguistic expert. Analyze the grammatical components of the given {language} text.
        
Text to analyze: {text}

Provide a detailed analysis focusing on the main grammatical components including parts of speech, sentence structure, and grammatical relationships."""
        
        analysis_prompt = PromptTemplate(
            input_variables=["text", "language"],
            template=analysis_template
        )
        
        return LLMChain(
            llm=self.llm,
            prompt=analysis_prompt,
            output_parser=AnalysisOutputParser()
        )
    
    def analyze(self, text: str, language: str) -> str:
        """Analyze text using the analysis chain"""
        try:
            result = self.analysis_chain.run(
                text=text,
                language=language
            )
            return result.strip()
        except Exception as e:
            logger.error(f"Analysis chain error: {e}")
            raise Exception(f"Grammar analysis failed: {str(e)}")