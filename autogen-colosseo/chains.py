from ollama_client import OllamaClient
from typing import List

class TranslationChain:
    def __init__(self, client: OllamaClient):
        self.client = client
    def translate(self, text: str, source_language: str, target_language: str, context: str = None) -> str:
        prompt = (
            f"Translate the following text from {source_language} to {target_language}.\n\n"
            f"Requirements:\n- Use only {target_language}\n- Preserve meaning and formatting\n\n"
            f"Text:\n{text}"
        )
        return self.client.generate(prompt)

class AnalysisChain:
    def __init__(self, client: OllamaClient):
        self.client = client
    def analyze(self, text: str, language: str) -> str:
        prompt = (
            f"Analyze grammar components for the following {language} text.\n"
            f"Return a concise list of parts of speech with positions.\n\n{text}"
        )
        return self.client.generate(prompt)
