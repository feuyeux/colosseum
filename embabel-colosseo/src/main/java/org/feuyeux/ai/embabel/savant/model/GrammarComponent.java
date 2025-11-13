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

import java.util.Map;

/**
 * Record representing a single grammar component in a sentence.
 * Contains the text, component type, color for highlighting, position indices,
 * and language-specific grammatical features.
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
     * Compact constructor for defensive copying and validation.
     */
    public GrammarComponent {
        if (features != null) {
            features = Map.copyOf(features);
        } else {
            features = Map.of();
        }
    }
    
    /**
     * Convenience constructor without features map.
     *
     * @param text the text of this component
     * @param componentType the type of grammar component
     * @param color the color for highlighting
     * @param startIndex the starting character index
     * @param endIndex the ending character index
     */
    public GrammarComponent(String text, String componentType, String color, 
                           int startIndex, int endIndex) {
        this(text, componentType, color, startIndex, endIndex, Map.of());
    }
    
    /**
     * Get a specific grammatical feature value.
     *
     * @param featureName the name of the feature (e.g., "gender", "case", "tense")
     * @return the feature value, or null if not present
     */
    public String getFeature(String featureName) {
        return features.get(featureName);
    }
    
    /**
     * Check if this component has a specific grammatical feature.
     *
     * @param featureName the name of the feature
     * @return true if the feature exists, false otherwise
     */
    public boolean hasFeature(String featureName) {
        return features.containsKey(featureName);
    }
    
    /**
     * Get the length of this component's text.
     *
     * @return the character length
     */
    public int length() {
        return endIndex - startIndex;
    }
}
