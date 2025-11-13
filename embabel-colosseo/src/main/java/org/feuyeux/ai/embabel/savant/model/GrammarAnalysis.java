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

import com.embabel.agent.domain.library.HasContent;
import com.embabel.common.core.types.Timestamped;

import java.time.Instant;
import java.util.List;

/**
 * Result record for grammar analysis operations.
 * Contains the original text, language code, list of grammar components,
 * and a timestamp of when the analysis was completed.
 * 
 * Implements HasContent and Timestamped for Embabel Agent framework integration.
 */
public record GrammarAnalysis(
    String originalText,
    String languageCode,
    List<GrammarComponent> components,
    Instant timestamp
) implements HasContent, Timestamped {
    
    /**
     * Compact constructor for defensive copying and validation.
     */
    public GrammarAnalysis {
        if (components != null) {
            components = List.copyOf(components);
        } else {
            components = List.of();
        }
        if (timestamp == null) {
            timestamp = Instant.now();
        }
    }
    
    /**
     * Convenience constructor that automatically sets the timestamp to now.
     *
     * @param originalText the text that was analyzed
     * @param languageCode the ISO 639-1 language code
     * @param components list of grammar components
     */
    public GrammarAnalysis(String originalText, String languageCode, 
                          List<GrammarComponent> components) {
        this(originalText, languageCode, components, Instant.now());
    }
    
    /**
     * Get the number of components in this analysis.
     *
     * @return the component count
     */
    public int componentCount() {
        return components.size();
    }
    
    /**
     * Get components of a specific type.
     *
     * @param componentType the type to filter by
     * @return list of components matching the type
     */
    public List<GrammarComponent> getComponentsByType(String componentType) {
        return components.stream()
                .filter(c -> c.componentType().equalsIgnoreCase(componentType))
                .toList();
    }
    
    /**
     * Check if the analysis contains any components.
     *
     * @return true if there are components, false otherwise
     */
    public boolean hasComponents() {
        return !components.isEmpty();
    }
    
    @Override
    public String getContent() {
        return originalText;
    }
    
    @Override
    public Instant getTimestamp() {
        return timestamp;
    }
}
