from typing import Optional
import loggingx

logger = logging.getLogger(__name__)

class TranslationAgent:
    def __init__(self, ollama_client):
        self.ollama_client = ollama_client

    async def translate(self, text: str, source_language: str, target_language: str, context: Optional[str] = None) -> str:
        try:
            # Language-specific instructions for native script
            native_script_instructions = {
                "zh": "IMPORTANT: Use Chinese characters (汉字), NOT pinyin or romanization.",
                "ja": "IMPORTANT: Use Japanese script (hiragana, katakana, kanji), NOT romaji.",
                "ko": "IMPORTANT: Use Korean Hangul (한글), NOT romanization.",
                "ru": "IMPORTANT: Use Cyrillic script (кириллица), NOT Latin transliteration.",
                "ar": "IMPORTANT: Use Arabic script (العربية), NOT Latin transliteration.",
                "hi": "IMPORTANT: Use Devanagari script (देवनागरी), NOT Latin transliteration.",
            }
            
            script_instruction = native_script_instructions.get(target_language, "")
            
            system_prompt = (
                f"You are a professional translator. Translate the given text to {target_language}.\n"
                f"{script_instruction}\n"
                f"Provide ONLY the translation in the native script of the target language.\n"
                f"Do NOT provide romanization, transliteration, or pronunciation guides.\n"
                f"Do NOT add any explanations, notes, or commentary.\n"
                f"Ensure the translation is accurate, natural-sounding, and uses proper native characters."
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
