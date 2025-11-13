package org.feuyeux.ai.langgraph4j.colosseo.model;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Map;

@Data
public class TranslationRequest {
    @NotBlank(message = "Text to translate is required")
    @Size(max = 1000, message = "Text must not exceed 1000 characters")
    private String text;
    
    @NotBlank(message = "Source language is required")
    private String sourceLanguage;
    
    @NotBlank(message = "Target language is required")
    private String targetLanguage;
    
    private String context;
}