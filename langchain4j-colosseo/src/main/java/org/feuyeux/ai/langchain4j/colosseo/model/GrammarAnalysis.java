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
import java.util.List;

/**
 * Result record for grammar analysis operations.
 * Contains the analyzed text, language code, grammar components, and timestamp.
 */
public record GrammarAnalysis(
    String text,
    String languageCode,
    List<GrammarComponent> components,
    Instant timestamp
) {
    
    /**
     * Convenience constructor that automatically sets the timestamp to now.
     *
     * @param text the analyzed text
     * @param languageCode the language code
     * @param components the grammar components
     */
    public GrammarAnalysis(String text, String languageCode, List<GrammarComponent> components) {
        this(text, languageCode, components, Instant.now());
    }
}