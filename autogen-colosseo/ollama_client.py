import httpx
import logging
from config import OLLAMA_BASE_URL, OLLAMA_MODEL_NAME, OLLAMA_TIMEOUT

logger = logging.getLogger(__name__)

class OllamaClient:
    def __init__(self):
        self.base_url = OLLAMA_BASE_URL
        self.model = OLLAMA_MODEL_NAME
        self.client = httpx.Client(timeout=httpx.Timeout(OLLAMA_TIMEOUT))

    def generate(self, prompt: str):
        try:
            r = self.client.post(f"{self.base_url}/api/generate", json={"model": self.model, "prompt": prompt, "stream": False})
            r.raise_for_status()
            return r.json().get("response", "")
        except Exception as e:
            logger.error(f"Ollama generation error: {e}")
            raise Exception(f"Ollama API error: {str(e)}")

    def health_check(self) -> bool:
        try:
            r = self.client.get(f"{self.base_url}/api/tags")
            return r.status_code == 200
        except:
            return False
