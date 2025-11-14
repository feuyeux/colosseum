from pydantic import BaseModel, Field
from typing import Optional, Dict, List, Any

class TranslationRequest(BaseModel):
    sourceText: str = Field(..., min_length=1, max_length=10000, description="Text to translate")
    targetLanguages: List[str] = Field(..., min_items=1, max_items=11, description="Target language codes")

class TranslationResult(BaseModel):
    sourceText: str
    translations: Dict[str, str] = Field(default_factory=dict)
    error: Optional[str] = None

class AnalysisRequest(BaseModel):
    text: str = Field(..., min_length=1, max_length=10000, description="Text to analyze")
    languageCode: str = Field(..., description="Language code")

class GrammarComponent(BaseModel):
    text: str
    componentType: str
    color: str
    startIndex: int
    endIndex: int
    features: Dict[str, str] = Field(default_factory=dict)

class AnalysisResult(BaseModel):
    originalText: str = ""
    languageCode: str = ""
    components: List[GrammarComponent] = Field(default_factory=list)
    error: Optional[str] = None