package org.feuyeux.ai.langgraph4j.colosseo.model;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AnalysisResult {
    private String text;
    private String language;
    private List<GrammarComponent> components;
    private Map<String, Object> metadata;
    private String error;
    
    public AnalysisResult(String error) {
        this.error = error;
    }
}