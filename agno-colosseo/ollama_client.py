import httpx
import logging
from typing import Optional

logger = logging.getLogger(__name__)


class OllamaClient:
    """Client for interacting with Ollama API"""
    
    def __init__(self, base_url: str, model_name: str, timeout: int):
        """
        Initialize Ollama client
        
        Args:
            base_url: Base URL for Ollama API (e.g., http://localhost:11434)
            model_name: Name of the model to use (e.g., qwen2.5:latest)
            timeout: Timeout in seconds for API requests
        """
        self.base_url = base_url
        self.model_name = model_name
        self.timeout = timeout
        self.client = httpx.AsyncClient(timeout=httpx.Timeout(timeout))
    
    def health_check(self) -> bool:
        """
        Check if Ollama service is available
        
        Returns:
            bool: True if Ollama is reachable, False otherwise
        """
        try:
            # Use synchronous client for health check
            with httpx.Client(timeout=httpx.Timeout(5.0)) as sync_client:
                response = sync_client.get(f"{self.base_url}/api/tags")
                return response.status_code == 200
        except Exception as e:
            logger.error(f"Ollama health check failed: {e}")
            return False
    
    async def generate(self, prompt: str, system: Optional[str] = None) -> str:
        """
        Generate text using Ollama API
        
        Args:
            prompt: The prompt to send to the model
            system: Optional system message to set context
            
        Returns:
            str: Generated text response
            
        Raises:
            Exception: If the API request fails or times out
        """
        try:
            payload = {
                "model": self.model_name,
                "prompt": prompt,
                "stream": False
            }
            
            if system:
                payload["system"] = system
            
            response = await self.client.post(
                f"{self.base_url}/api/generate",
                json=payload
            )
            response.raise_for_status()
            
            result = response.json()
            return result.get("response", "")
            
        except httpx.TimeoutException as e:
            logger.error(f"Ollama request timeout: {e}")
            raise Exception(f"Ollama request timed out after {self.timeout} seconds")
        except httpx.ConnectError as e:
            logger.error(f"Ollama connection error: {e}")
            raise Exception(f"Failed to connect to Ollama at {self.base_url}")
        except httpx.HTTPStatusError as e:
            logger.error(f"Ollama HTTP error: {e}")
            raise Exception(f"Ollama API returned error: {e.response.status_code}")
        except Exception as e:
            logger.error(f"Ollama generation error: {e}")
            raise Exception(f"Ollama API error: {str(e)}")
    
    async def close(self):
        """Close the HTTP client"""
        await self.client.aclose()
