package org.feuyeux.ai.langgraph4j.colosseo.model;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
public class AnalysisRequest {
    @NotBlank(message = "Text to analyze is required")
    @Size(max = 1000, message = "Text must not exceed 1000 characters")
    private String text;
    
    @NotBlank(message = "Language is required")
    private String language;
    
    private String analysisType = "full";
}