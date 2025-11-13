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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Enum representing supported languages for translation and grammar analysis.
 */
public enum SupportedLanguage {
    ENGLISH("en", "English", "English"),
    CHINESE("zh", "Chinese", "中文"),
    JAPANESE("ja", "Japanese", "日本語"),
    KOREAN("ko", "Korean", "한국어"),
    FRENCH("fr", "French", "Français"),
    GERMAN("de", "German", "Deutsch"),
    SPANISH("es", "Spanish", "Español"),
    ITALIAN("it", "Italian", "Italiano"),
    PORTUGUESE("pt", "Portuguese", "Português"),
    RUSSIAN("ru", "Russian", "Русский"),
    ARABIC("ar", "Arabic", "العربية");

    private final String code;
    private final String englishName;
    private final String nativeName;

    SupportedLanguage(String code, String englishName, String nativeName) {
        this.code = code;
        this.englishName = englishName;
        this.nativeName = nativeName;
    }

    public String getCode() {
        return code;
    }

    public String getEnglishName() {
        return englishName;
    }

    public String getNativeName() {
        return nativeName;
    }

    /**
     * Get language by ISO 639-1 code.
     *
     * @param code the language code
     * @return optional containing the language if found
     */
    public static Optional<SupportedLanguage> fromCode(String code) {
        return Arrays.stream(values())
                .filter(lang -> lang.code.equalsIgnoreCase(code))
                .findFirst();
    }

    /**
     * Validate a list of language codes.
     *
     * @param codes the language codes to validate
     * @return true if all codes are valid, false otherwise
     */
    public static boolean validateLanguageCodes(List<String> codes) {
        if (codes == null || codes.isEmpty()) {
            return false;
        }
        return codes.stream()
                .allMatch(code -> fromCode(code).isPresent());
    }
}