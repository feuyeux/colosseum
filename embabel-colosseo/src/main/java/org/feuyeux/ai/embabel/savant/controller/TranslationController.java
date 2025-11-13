/*
 * Copyright 2024-2025 Embabel Software, Inc.
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
package org.feuyeux.ai.embabel.savant.controller;

import com.embabel.agent.api.common.OperationContext;
import org.feuyeux.ai.embabel.savant.agent.Savant;
import org.feuyeux.ai.embabel.savant.model.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for Savant API endpoints.
 * Provides HTTP endpoints for translation and grammar analysis operations.
 */
@RestController
@RequestMapping("/api")
public class TranslationController {

    private final Savant translationAgent;
    private final OperationContext operationContext;

    public TranslationController(Savant translationAgent, OperationContext operationContext) {
        this.translationAgent = translationAgent;
        this.operationContext = operationContext;
    }

    /**
     * Translate text into multiple target languages.
     *
     * @param request the translation request
     * @return the translation result
     */
    @PostMapping("/translateText")
    public ResponseEntity<TranslationResult> translateText(@RequestBody TranslationRequest request) {
        TranslationResult result = translationAgent.translateText(request, operationContext);
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
        // For the REST API, we need to create a dummy TranslationResult
        // In a real scenario, this should be retrieved from a session or cache
        TranslationResult translationResult = new TranslationResult(
            request.text(),
            java.util.Map.of(request.languageCode(), request.text())
        );
        
        GrammarAnalysis result = translationAgent.analyzeGrammar(request, translationResult, operationContext);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/translateMultiple")
    public ResponseEntity<Map<String, String>> translateMultiple(@RequestParam String text,
                                                                 @RequestParam String sourceLanguage,
                                                                 @RequestBody List<String> targetLanguages) {
        TranslationResult result = translationAgent.translateText(new TranslationRequest(text, targetLanguages), operationContext);
        return ResponseEntity.ok(result.translations());
    }

    @PostMapping("/analyzeAndTranslate")
    public ResponseEntity<Map<String, Object>> analyzeAndTranslate(@RequestBody AnalysisRequest request,
                                                                   @RequestParam(required = false) String targetLanguage) {
        TranslationResult translationResult = new TranslationResult(
            request.text(),
            java.util.Map.of(request.languageCode(), request.text())
        );
        GrammarAnalysis analysis = translationAgent.analyzeGrammar(request, translationResult, operationContext);
        if (targetLanguage != null && !targetLanguage.equals(request.languageCode())) {
            TranslationResult tr = translationAgent.translateText(new TranslationRequest(request.text(), java.util.List.of(targetLanguage)), operationContext);
            return ResponseEntity.ok(Map.of("analysis", analysis, "translation", tr));
        }
        return ResponseEntity.ok(Map.of("analysis", analysis));
    }
}
