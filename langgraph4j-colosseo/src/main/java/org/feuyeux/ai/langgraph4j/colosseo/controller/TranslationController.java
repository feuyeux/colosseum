package org.feuyeux.ai.langgraph4j.colosseo.controller;

import lombok.extern.slf4j.Slf4j;
import org.feuyeux.ai.langgraph4j.colosseo.model.TranslationRequest;
import org.feuyeux.ai.langgraph4j.colosseo.model.TranslationResult;
import org.feuyeux.ai.langgraph4j.colosseo.service.TranslationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class TranslationController {
    
    private final TranslationService translationService;
    
    public TranslationController(TranslationService translationService) {
        this.translationService = translationService;
    }
    
    @PostMapping("/translateText")
    public ResponseEntity<TranslationResult> translateText(@Valid @RequestBody TranslationRequest request) {
        log.info("Translation request received: {} -> {}", request.getSourceLanguage(), request.getTargetLanguage());
        
        try {
            TranslationResult result = translationService.translateText(request);
            
            if (result.getError() != null) {
                log.error("Translation error: {}", result.getError());
                return ResponseEntity.badRequest().body(result);
            }
            
            log.info("Translation completed successfully");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Unexpected error during translation", e);
            TranslationResult errorResult = new TranslationResult("Unexpected error: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResult);
        }
    }
    
    @PostMapping("/translateMultiple")
    public ResponseEntity<Map<String, TranslationResult>> translateToMultipleLanguages(
            @RequestParam String text,
            @RequestParam String sourceLanguage,
            @RequestBody List<String> targetLanguages) {
        
        log.info("Multiple translation request received for languages: {}", targetLanguages);
        
        try {
            Map<String, TranslationResult> results = translationService.translateToMultipleLanguages(
                text, sourceLanguage, targetLanguages);
            
            log.info("Multiple translations completed successfully");
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            log.error("Unexpected error during multiple translations", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/languages")
    public ResponseEntity<Map<String, String>> getSupportedLanguages() {
        return ResponseEntity.ok(translationService.getSupportedLanguages());
    }
}