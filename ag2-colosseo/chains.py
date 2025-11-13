from ollama_client import OllamaClient

class TranslationChain:
    def __init__(self, client: OllamaClient):
        self.client = client
    def translate(self, text: str, source_language: str, target_language: str, context: str = None) -> str:
        prompt = f"Translate from {source_language} to {target_language}.\n\n{text}"
        return self.client.generate(prompt)

class AnalysisChain:
    def __init__(self, client: OllamaClient):
        self.client = client
    def analyze(self, text: str, language: str) -> str:
        prompt = f"Analyze grammar for {language} text: {text}"
        return self.client.generate(prompt)
