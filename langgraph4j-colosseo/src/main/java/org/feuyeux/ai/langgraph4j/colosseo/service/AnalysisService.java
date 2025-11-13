package org.feuyeux.ai.langgraph4j.colosseo.service;

import lombok.extern.slf4j.Slf4j;
import org.feuyeux.ai.langgraph4j.colosseo.graph.AnalysisGraph;
import org.feuyeux.ai.langgraph4j.colosseo.model.AnalysisRequest;
import org.feuyeux.ai.langgraph4j.colosseo.model.AnalysisResult;
import org.feuyeux.ai.langgraph4j.colosseo.model.TranslationResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class AnalysisService {
    
    private final AnalysisGraph analysisGraph;
    
    @Value("${app.max-text-length:1000}")
    private int maxTextLength;
    
    @Value("${app.supported-languages:en,zh,ja,ko,fr,de,es,it,pt,ru,ar}")
    private String supportedLanguages;
    
    public AnalysisService(AnalysisGraph analysisGraph) {
        this.analysisGraph = analysisGraph;
    }
    
    public AnalysisResult analyzeGrammar(AnalysisRequest request, TranslationResult translationResult) {
        log.info("Analyzing grammar for text in language: {}", request.getLanguage());
        
        // Validate input
        if (request.getText() == null || request.getText().trim().isEmpty()) {
            return new AnalysisResult("Text cannot be empty");
        }
        
        if (request.getText().length() > maxTextLength) {
            return new AnalysisResult("Text exceeds maximum length of " + maxTextLength + " characters");
        }
        
        List<String> supportedLangs = Arrays.asList(supportedLanguages.split(","));
        if (!supportedLangs.contains(request.getLanguage())) {
            return new AnalysisResult("Unsupported language. Supported languages: " + supportedLanguages);
        }
        
        try {
            return analysisGraph.analyze(request);
        } catch (Exception e) {
            log.error("Grammar analysis failed", e);
            return new AnalysisResult("Grammar analysis failed: " + e.getMessage());
        }
    }
    
    public AnalysisResult analyzeText(String text, String language) {
        AnalysisRequest request = new AnalysisRequest();
        request.setText(text);
        request.setLanguage(language);
        request.setAnalysisType("full");
        
        return analyzeGrammar(request, null);
    }
}