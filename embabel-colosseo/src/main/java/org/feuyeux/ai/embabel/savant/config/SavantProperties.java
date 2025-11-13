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
package org.feuyeux.ai.embabel.savant.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

/**
 * Configuration properties for the Savant.
 * Maps to the 'translation' section in application.yaml.
 */
@Component
@ConfigurationProperties(prefix = "translation")
public class SavantProperties {

    /**
     * Maximum length of source text in characters
     */
    private int maxTextLength = 10000;

    /**
     * Timeout for translation operations
     */
    private Duration translationTimeout = Duration.ofSeconds(30);

    /**
     * Timeout for grammar analysis operations
     */
    private Duration analysisTimeout = Duration.ofSeconds(20);

    /**
     * List of supported language codes
     */
    private List<String> supportedLanguages = List.of(
        "en", "zh", "ja", "ko", "fr", "de", "es", "it", "pt", "ru", "ar"
    );

    public int getMaxTextLength() {
        return maxTextLength;
    }

    public void setMaxTextLength(int maxTextLength) {
        this.maxTextLength = maxTextLength;
    }

    public Duration getTranslationTimeout() {
        return translationTimeout;
    }

    public void setTranslationTimeout(Duration translationTimeout) {
        this.translationTimeout = translationTimeout;
    }

    public Duration getAnalysisTimeout() {
        return analysisTimeout;
    }

    public void setAnalysisTimeout(Duration analysisTimeout) {
        this.analysisTimeout = analysisTimeout;
    }

    public List<String> getSupportedLanguages() {
        return supportedLanguages;
    }

    public void setSupportedLanguages(List<String> supportedLanguages) {
        this.supportedLanguages = supportedLanguages;
    }
}
