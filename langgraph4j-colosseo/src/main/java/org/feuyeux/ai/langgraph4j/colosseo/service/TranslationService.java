package org.feuyeux.ai.langgraph4j.colosseo.service;

import lombok.extern.slf4j.Slf4j;
import org.feuyeux.ai.langgraph4j.colosseo.graph.TranslationGraph;
import org.feuyeux.ai.langgraph4j.colosseo.model.TranslationRequest;
import org.feuyeux.ai.langgraph4j.colosseo.model.TranslationResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TranslationService {
    
    private final TranslationGraph translationGraph;
    
    @Value("${app.max-text-length:1000}")
    private int maxTextLength;
    
    @Value("${app.supported-languages:en,zh,ja,ko,fr,de,es,it,pt,ru,ar}")
    private String supportedLanguages;
    
    private static final Map<String, String> LANGUAGE_NAMES = Map.ofEntries(
        Map.entry("en", "English"),
        Map.entry("zh", "Chinese"),
        Map.entry("ja", "Japanese"), 
        Map.entry("ko", "Korean"),
        Map.entry("fr", "French"),
        Map.entry("de", "German"),
        Map.entry("es", "Spanish"),
        Map.entry("it", "Italian"),
        Map.entry("pt", "Portuguese"),
        Map.entry("ru", "Russian"),
        Map.entry("ar", "Arabic")
    );
    
    public TranslationService(TranslationGraph translationGraph) {
        this.translationGraph = translationGraph;
    }
    
    public TranslationResult translateText(TranslationRequest request) {
        log.info("Translating text from {} to {}", request.getSourceLanguage(), request.getTargetLanguage());
        
        // Validate input
        if (request.getText() == null || request.getText().trim().isEmpty()) {
            return new TranslationResult("Text cannot be empty");
        }
        
        if (request.getText().length() > maxTextLength) {
            return new TranslationResult("Text exceeds maximum length of " + maxTextLength + " characters");
        }
        
        List<String> supportedLangs = Arrays.asList(supportedLanguages.split(","));
        if (!supportedLangs.contains(request.getSourceLanguage()) || !supportedLangs.contains(request.getTargetLanguage())) {
            return new TranslationResult("Unsupported language. Supported languages: " + supportedLanguages);
        }
        
        if (request.getSourceLanguage().equals(request.getTargetLanguage())) {
            return new TranslationResult("Source and target languages cannot be the same");
        }
        
        try {
            return translationGraph.translate(request);
        } catch (Exception e) {
            log.error("Translation failed", e);
            return new TranslationResult("Translation failed: " + e.getMessage());
        }
    }
    
    public Map<String, TranslationResult> translateToMultipleLanguages(String text, String sourceLanguage, List<String> targetLanguages) {
        log.info("Translating text to multiple languages: {}", targetLanguages);
        
        Map<String, TranslationResult> results = new ConcurrentHashMap<>();
        
        List<CompletableFuture<Void>> futures = targetLanguages.stream()
                .map(targetLanguage -> CompletableFuture.runAsync(() -> {
                    TranslationRequest request = new TranslationRequest();
                    request.setText(text);
                    request.setSourceLanguage(sourceLanguage);
                    request.setTargetLanguage(targetLanguage);
                    
                    TranslationResult result = translateText(request);
                    results.put(targetLanguage, result);
                }))
                .collect(Collectors.toList());
        
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        
        return results;
    }
    
    public Map<String, String> getSupportedLanguages() {
        return LANGUAGE_NAMES;
    }
}