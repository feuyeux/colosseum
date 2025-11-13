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
package org.feuyeux.ai.springai.colosseo.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * Request record for translation operations.
 * Contains the source text and list of target languages for translation.
 */
public record TranslationRequest(
    
    @NotNull(message = "Source text cannot be null")
    @NotEmpty(message = "Source text cannot be empty")
    @Size(min = 1, max = 10000, message = "Source text must be between 1 and 10000 characters")
    String sourceText,
    
    @NotNull(message = "Target languages cannot be null")
    @NotEmpty(message = "At least one target language must be selected")
    @Size(min = 1, max = 11, message = "Number of target languages must be between 1 and 11")
    List<String> targetLanguages
) {
    
    /**
     * Validates that all target language codes are supported.
     *
     * @return true if all language codes are valid, false otherwise
     */
    public boolean hasValidLanguageCodes() {
        return SupportedLanguage.validateLanguageCodes(targetLanguages);
    }
    
    /**
     * Compact constructor for additional validation.
     */
    public TranslationRequest {
        if (sourceText != null) {
            sourceText = sourceText.trim();
        }
        if (targetLanguages != null) {
            targetLanguages = List.copyOf(targetLanguages);
        }
    }
}