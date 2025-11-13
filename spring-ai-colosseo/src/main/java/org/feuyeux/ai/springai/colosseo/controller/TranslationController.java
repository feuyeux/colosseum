/*
 * Copyright 2024-2025 Spring AI Colosseo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.feuyeux.ai.springai.colosseo.controller;

import org.feuyeux.ai.springai.colosseo.model.*;
import org.feuyeux.ai.springai.colosseo.service.TranslationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for translation and grammar analysis endpoints.
 */
@RestController
@RequestMapping("/api")
public class TranslationController {

    private static final Logger logger = LoggerFactory.getLogger(TranslationController.class);
    
    private final TranslationService translationService;

    public TranslationController(TranslationService translationService) {
        this.translationService = translationService;
    }

    /**
     * Translate text into multiple target languages.
     *
     * @param request the translation request
     * @return the translation result
     */
    @PostMapping("/translateText")
    public ResponseEntity<TranslationResult> translateText(@RequestBody TranslationRequest request) {
        logger.info("Received translation request for {} target languages", request.targetLanguages().size());
        TranslationResult result = translationService.translateText(request);
        return ResponseEntity.ok(result);
    }

    /**
     * Analyze grammar of translated text.
     *
     * @param request the analysis request
     * @return the grammar analysis result
     */
    @PostMapping("/analyzeGrammar")
    public ResponseEntity<GrammarAnalysis> analyzeGrammar(@RequestBody AnalysisRequest request) {
        logger.info("Received grammar analysis request for language: {}", request.languageCode());
        
        // For the REST API, we need to create a dummy TranslationResult
        // In a real scenario, this should be retrieved from a session or cache
        TranslationResult translationResult = new TranslationResult(
            request.text(),
            Map.of(request.languageCode(), request.text())
        );
        
        GrammarAnalysis result = translationService.analyzeGrammar(request, translationResult);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/translateMultiple")
    public ResponseEntity<Map<String, String>> translateMultiple(@RequestParam String text,
                                                                 @RequestParam String sourceLanguage,
                                                                 @RequestBody List<String> targetLanguages) {
        TranslationResult result = translationService.translateText(new TranslationRequest(text, targetLanguages));
        return ResponseEntity.ok(result.translations());
    }

    @PostMapping("/analyzeAndTranslate")
    public ResponseEntity<Map<String, Object>> analyzeAndTranslate(@RequestBody AnalysisRequest request,
                                                                   @RequestParam(required = false) String targetLanguage) {
        TranslationResult translationResult = new TranslationResult(
            request.text(),
            Map.of(request.languageCode(), request.text())
        );
        GrammarAnalysis analysis = translationService.analyzeGrammar(request, translationResult);
        if (targetLanguage != null && !targetLanguage.equals(request.languageCode())) {
            TranslationResult tr = translationService.translateText(new TranslationRequest(request.text(), List.of(targetLanguage)));
            return ResponseEntity.ok(Map.of("analysis", analysis, "translation", tr));
        }
        return ResponseEntity.ok(Map.of("analysis", analysis));
    }
}
