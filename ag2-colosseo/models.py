from pydantic import BaseModel, Field
from typing import Optional, Dict, List, Any

class TranslationRequest(BaseModel):
    text: str = Field(..., min_length=1, max_length=1000)
    source_language: str
    target_language: str
    context: Optional[str] = None

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
    text: str = Field(..., min_length=1, max_length=1000)
    language: str
    analysis_type: str = "full"

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
