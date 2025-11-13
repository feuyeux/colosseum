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
package org.feuyeux.ai.embabel.savant.model;

import java.util.Arrays;
import java.util.Optional;

/**
 * Enum representing all supported languages for translation.
 * Each language includes its ISO 639-1 code, native name, and English name.
 */
public enum SupportedLanguage {
    ENGLISH("en", "English", "English"),
    CHINESE("zh", "中文", "Chinese"),
    JAPANESE("ja", "日本語", "Japanese"),
    KOREAN("ko", "한국어", "Korean"),
    FRENCH("fr", "Français", "French"),
    GERMAN("de", "Deutsch", "German"),
    SPANISH("es", "Español", "Spanish"),
    ITALIAN("it", "Italiano", "Italian"),
    PORTUGUESE("pt", "Português", "Portuguese"),
    RUSSIAN("ru", "Русский", "Russian"),
    ARABIC("ar", "العربية", "Arabic"),
    HINDI("hi", "हिन्दी", "Hindi");

    private final String code;
    private final String nativeName;
    private final String englishName;

    SupportedLanguage(String code, String nativeName, String englishName) {
        this.code = code;
        this.nativeName = nativeName;
        this.englishName = englishName;
    }

    public String getCode() {
        return code;
    }

    public String getNativeName() {
        return nativeName;
    }

    public String getEnglishName() {
        return englishName;
    }

    /**
     * Find a SupportedLanguage by its language code.
     *
     * @param code the ISO 639-1 language code
     * @return Optional containing the matching SupportedLanguage, or empty if not found
     */
    public static Optional<SupportedLanguage> fromCode(String code) {
        if (code == null) {
            return Optional.empty();
        }
        return Arrays.stream(values())
                .filter(lang -> lang.code.equalsIgnoreCase(code))
                .findFirst();
    }

    /**
     * Check if a language code is supported.
     *
     * @param code the ISO 639-1 language code to check
     * @return true if the language code is supported, false otherwise
     */
    public static boolean isSupported(String code) {
        return fromCode(code).isPresent();
    }

    /**
     * Validate a list of language codes.
     *
     * @param codes the list of language codes to validate
     * @return true if all codes are supported, false if any code is invalid
     */
    public static boolean validateLanguageCodes(java.util.List<String> codes) {
        if (codes == null || codes.isEmpty()) {
            return false;
        }
        return codes.stream().allMatch(SupportedLanguage::isSupported);
    }

    @Override
    public String toString() {
        return String.format("%s (%s)", nativeName, englishName);
    }
}
