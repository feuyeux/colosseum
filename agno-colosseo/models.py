"""
Data models for Agno Colosseo translation and grammar analysis service.
"""
from typing import Dict, List, Optional
from pydantic import BaseModel, Field, field_validator


class TranslationRequest(BaseModel):
    """Request model for text translation."""
    sourceText: str = Field(..., min_length=1, max_length=10000)
    targetLanguages: List[str] = Field(..., min_length=1, max_length=12)

    @field_validator('sourceText')
    @classmethod
    def validate_source_text(cls, v: str) -> str:
        if not v or not v.strip():
            raise ValueError("Source text cannot be empty")
        return v

    @field_validator('targetLanguages')
    @classmethod
    def validate_target_languages(cls, v: List[str]) -> List[str]:
        if not v:
            raise ValueError("At least one target language must be specified")
        if len(v) > 12:
            raise ValueError("Maximum 12 target languages allowed")
        return v


class TranslationResult(BaseModel):
    """Result model for text translation."""
    sourceText: str
    translations: Dict[str, str] = Field(default_factory=dict)
    error: Optional[str] = None


class AnalysisRequest(BaseModel):
    """Request model for grammar analysis."""
    text: str = Field(..., min_length=1, max_length=10000)
    languageCode: str

    @field_validator('text')
    @classmethod
    def validate_text(cls, v: str) -> str:
        if not v or not v.strip():
            raise ValueError("Text cannot be empty")
        return v


class GrammarComponent(BaseModel):
    """Model representing a grammatical component in analyzed text."""
    text: str
    componentType: str
    color: str
    startIndex: int
    endIndex: int
    features: Dict[str, str] = Field(default_factory=dict)


class AnalysisResult(BaseModel):
    """Result model for grammar analysis."""
    originalText: str
    languageCode: str
    components: List[GrammarComponent] = Field(default_factory=list)
    error: Optional[str] = None
