/*
 * Copyright 2024-2025 LangChain4j Colosseo
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
package org.feuyeux.ai.langchain4j.colosseo.model;

import java.time.Instant;
import java.util.Map;

/**
 * Result record for translation operations.
 * Contains the original source text, translations for each target language,
 * and a timestamp of when the translation was completed.
 */
public record TranslationResult(
    String sourceText,
    Map<String, String> translations,
    Instant timestamp
) {
    
    /**
     * Convenience constructor that automatically sets the timestamp to now.
     *
     * @param sourceText the original source text
     * @param translations map of language code to translated text
     */
    public TranslationResult(String sourceText, Map<String, String> translations) {
        this(sourceText, translations, Instant.now());
    }
    
    /**
     * Get the translation for a specific language code.
     *
     * @param languageCode the ISO 639-1 language code
     * @return the translated text, or null if not found
     */
    public String getTranslation(String languageCode) {
        return translations.get(languageCode);
    }
    
    /**
     * Check if a translation exists for a specific language code.
     *
     * @param languageCode the ISO 639-1 language code
     * @return true if translation exists, false otherwise
     */
    public boolean hasTranslation(String languageCode) {
        return translations.containsKey(languageCode);
    }
}