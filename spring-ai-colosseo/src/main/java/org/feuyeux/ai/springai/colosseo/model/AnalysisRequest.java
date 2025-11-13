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

/**
 * Request record for grammar analysis operations.
 * Contains the text to analyze and the language code.
 */
public record AnalysisRequest(
    
    @NotNull(message = "Text cannot be null")
    @NotEmpty(message = "Text cannot be empty")
    String text,
    
    @NotNull(message = "Language code cannot be null")
    @NotEmpty(message = "Language code cannot be empty")
    String languageCode
) {
    
    /**
     * Validates that the language code is supported.
     *
     * @return true if language code is valid, false otherwise
     */
    public boolean hasValidLanguageCode() {
        return SupportedLanguage.fromCode(languageCode).isPresent();
    }
    
    /**
     * Compact constructor for additional validation.
     */
    public AnalysisRequest {
        if (text != null) {
            text = text.trim();
        }
        if (languageCode != null) {
            languageCode = languageCode.trim();
        }
    }
}