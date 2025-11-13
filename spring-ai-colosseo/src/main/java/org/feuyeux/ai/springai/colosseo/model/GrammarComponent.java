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

import java.util.Map;

/**
 * Grammar component representing a single grammatical element in analyzed text.
 * Contains the text, component type, color for highlighting, position, and features.
 */
public record GrammarComponent(
    String text,
    String componentType,
    String color,
    int startIndex,
    int endIndex,
    Map<String, String> features
) {
    /**
     * Compact constructor for defensive copying.
     */
    public GrammarComponent {
        if (features != null) {
            features = Map.copyOf(features);
        }
    }
}