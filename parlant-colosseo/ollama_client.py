import httpx
import json
import logging
from typing import Optional, Dict, Any
from config import OLLAMA_BASE_URL, OLLAMA_MODEL_NAME, OLLAMA_TIMEOUT

logger = logging.getLogger(__name__)

class OllamaClient:
    def __init__(self):
        self.base_url = OLLAMA_BASE_URL
        self.model = OLLAMA_MODEL_NAME
        self.timeout = OLLAMA_TIMEOUT
        self.client = httpx.Client(timeout=httpx.Timeout(OLLAMA_TIMEOUT))
    
    def generate(self, prompt: str, system: Optional[str] = None) -> str:
        """Generate text using Ollama API"""
        try:
            payload = {
                "model": self.model,
                "prompt": prompt,
                "stream": False
            }
            
            if system:
                payload["system"] = system
            
            response = self.client.post(
                f"{self.base_url}/api/generate",
                json=payload
            )
            response.raise_for_status()
            
            result = response.json()
            return result.get("response", "")
            
        except Exception as e:
            logger.error(f"Ollama generation error: {e}")
            raise Exception(f"Ollama API error: {str(e)}")
    
    def chat(self, messages: list) -> str:
        """Chat using Ollama API"""
        try:
            payload = {
                "model": self.model,
                "messages": messages,
                "stream": False
            }
            
            response = self.client.post(
                f"{self.base_url}/api/chat",
                json=payload
            )
            response.raise_for_status()
            
            result = response.json()
            return result.get("message", {}).get("content", "")
            
        except Exception as e:
            logger.error(f"Ollama chat error: {e}")
            raise Exception(f"Ollama API error: {str(e)}")
    
    def health_check(self) -> bool:
        """Check if Ollama is available"""
        try:
            response = self.client.get(f"{self.base_url}/api/tags")
            return response.status_code == 200
        except:
            return False