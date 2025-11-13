from typing import Optional
import logging

logger = logging.getLogger(__name__)

class TranslationAgent:
    def __init__(self, ollama_client):
        self.ollama_client = ollama_client

    async def translate(self, text: str, source_language: str, target_language: str, context: Optional[str] = None) -> str:
        try:
            system_prompt = (
                f"You are a professional translator. Translate the given text from {source_language} to {target_language}.\n"
                f"Provide only the translation without any additional explanation or commentary.\n"
                f"Ensure the translation is accurate and natural-sounding."
            )
            user_prompt = f"Text to translate: {text}"
            if context:
                user_prompt += f"\nContext: {context}"

            response = self.ollama_client.generate(user_prompt, system=system_prompt)
            return response.strip()
        except Exception as e:
            logger.error(f"Translation error: {e}")
            return f"Translation error: {str(e)}"

class AnalysisAgent:
    def __init__(self, ollama_client):
        self.ollama_client = ollama_client

    async def analyze(self, text: str, language: str) -> str:
        try:
            system_prompt = (
                f"You are a linguistic expert. Analyze the grammatical components of the given {language} text.\n"
                f"Identify parts of speech, sentence structure, and grammatical relationships.\n"
                f"Provide a detailed analysis focusing on the main grammatical components."
            )
            user_prompt = f"Text to analyze: {text}"

            response = self.ollama_client.generate(user_prompt, system=system_prompt)
            return response.strip()
        except Exception as e:
            logger.error(f"Analysis error: {e}")
            return f"Analysis error: {str(e)}"
