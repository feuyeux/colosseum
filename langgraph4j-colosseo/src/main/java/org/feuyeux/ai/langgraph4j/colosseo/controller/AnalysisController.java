package org.feuyeux.ai.langgraph4j.colosseo.controller;

import lombok.extern.slf4j.Slf4j;
import org.feuyeux.ai.langgraph4j.colosseo.model.AnalysisRequest;
import org.feuyeux.ai.langgraph4j.colosseo.model.AnalysisResult;
import org.feuyeux.ai.langgraph4j.colosseo.model.TranslationResult;
import org.feuyeux.ai.langgraph4j.colosseo.service.AnalysisService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class AnalysisController {
    
    private final AnalysisService analysisService;
    
    public AnalysisController(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }
    
    @PostMapping("/analyzeGrammar")
    public ResponseEntity<AnalysisResult> analyzeGrammar(@Valid @RequestBody AnalysisRequest request) {
        log.info("Grammar analysis request received for language: {}", request.getLanguage());
        
        try {
            AnalysisResult result = analysisService.analyzeGrammar(request, null);
            
            if (result.getError() != null) {
                log.error("Grammar analysis error: {}", result.getError());
                return ResponseEntity.badRequest().body(result);
            }
            
            log.info("Grammar analysis completed successfully");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Unexpected error during grammar analysis", e);
            AnalysisResult errorResult = new AnalysisResult("Unexpected error: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResult);
        }
    }
    
    @PostMapping("/analyzeAndTranslate")
    public ResponseEntity<AnalysisResult> analyzeAndTranslate(
            @Valid @RequestBody AnalysisRequest request,
            @RequestParam(required = false) String targetLanguage) {
        
        log.info("Combined analysis and translation request received");
        
        try {
            // First analyze the grammar
            AnalysisResult analysisResult = analysisService.analyzeGrammar(request, null);
            
            if (analysisResult.getError() != null) {
                log.error("Grammar analysis error: {}", analysisResult.getError());
                return ResponseEntity.badRequest().body(analysisResult);
            }
            
            log.info("Combined analysis and translation completed successfully");
            return ResponseEntity.ok(analysisResult);
        } catch (Exception e) {
            log.error("Unexpected error during combined analysis and translation", e);
            AnalysisResult errorResult = new AnalysisResult("Unexpected error: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResult);
        }
    }
}