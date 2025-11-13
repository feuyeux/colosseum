package org.feuyeux.ai.langgraph4j.colosseo.model;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TranslationResult {
    private String translatedText;
    private String sourceLanguage;
    private String targetLanguage;
    private double confidence;
    private Map<String, String> alternatives;
    private String error;
    
    public TranslationResult(String error) {
        this.error = error;
        this.confidence = 0.0;
    }
}