from pydantic import BaseModel, Field
from typing import Optional, Dict, List, Any

class TranslationRequest(BaseModel):
    text: str = Field(..., min_length=1, max_length=1000, description="Text to translate")
    source_language: str = Field(..., description="Source language code")
    target_language: str = Field(..., description="Target language code")
    context: Optional[str] = Field(None, description="Optional context for translation")

class TranslationResult(BaseModel):
    translated_text: str
    source_language: str
    target_language: str
    confidence: float = 0.95
    alternatives: Dict[str, str] = Field(default_factory=dict)
    error: Optional[str] = None
    
    def __init__(self, error: str = None, **data):
        if error:
            super().__init__(translated_text="", source_language="", target_language="", confidence=0.0, error=error)
        else:
            super().__init__(**data)

class AnalysisRequest(BaseModel):
    text: str = Field(..., min_length=1, max_length=1000, description="Text to analyze")
    language: str = Field(..., description="Language code")
    analysis_type: str = Field("full", description="Type of analysis")

class GrammarComponent(BaseModel):
    component: str
    type: str
    explanation: str
    color: str
    position: int
    length: int

class AnalysisResult(BaseModel):
    text: str
    language: str
    components: List[GrammarComponent] = Field(default_factory=list)
    metadata: Dict[str, Any] = Field(default_factory=dict)
    error: Optional[str] = None
    
    def __init__(self, error: str = None, **data):
        if error:
            super().__init__(text="", language="", error=error)
        else:
            super().__init__(**data)