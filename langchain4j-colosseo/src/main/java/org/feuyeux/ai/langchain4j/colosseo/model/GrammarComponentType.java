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

import java.util.Map;

/**
 * Enum defining grammar component types and their associated colors for highlighting.
 */
public enum GrammarComponentType {
    // Sentence roles
    SUBJECT("#FF6B6B"),
    PREDICATE("#4ECDC4"),
    OBJECT("#45B7D1"),
    DIRECT_OBJECT("#96CEB4"),
    INDIRECT_OBJECT("#FFEAA7"),
    COMPLEMENT("#DDA0DD"),
    ADVERBIAL("#98D8C8"),
    
    // Parts of speech
    NOUN("#FF7675"),
    VERB("#74B9FF"),
    ADJECTIVE("#55A3FF"),
    ADVERB("#00B894"),
    PRONOUN("#FDCB6E"),
    ARTICLE("#6C5CE7"),
    PREPOSITION("#A29BFE"),
    CONJUNCTION("#FD79A8"),
    PARTICLE("#E17055"),
    
    // Verb types
    MAIN_VERB("#0984E3"),
    AUXILIARY_VERB("#00CEC9"),
    MODAL_VERB("#FDCB6E"),
    COPULA("#6C5CE7"),
    
    // Particles
    TOPIC_PARTICLE("#E17055"),
    SUBJECT_PARTICLE("#00B894"),
    OBJECT_PARTICLE("#0984E3"),
    POSSESSIVE_PARTICLE("#6C5CE7"),
    
    // Chinese-specific
    MEASURE_WORD("#FDCB6E"),
    ASPECT_PARTICLE_LE("#74B9FF"),
    ASPECT_PARTICLE_ZHE("#00CEC9"),
    ASPECT_PARTICLE_GUO("#0984E3"),
    MODAL_PARTICLE_MA("#FDCB6E"),
    MODAL_PARTICLE_NE("#00B894"),
    MODAL_PARTICLE_BA("#E17055"),
    STRUCTURAL_PARTICLE_DE("#6C5CE7"),
    
    // Cases
    NOMINATIVE_CASE("#FF7675"),
    ACCUSATIVE_CASE("#74B9FF"),
    GENITIVE_CASE("#00B894"),
    DATIVE_CASE("#FDCB6E"),
    INSTRUMENTAL_CASE("#6C5CE7"),
    PREPOSITIONAL_CASE("#A29BFE"),
    
    // Tenses
    PRESENT_TENSE("#00CEC9"),
    PAST_TENSE("#E17055"),
    FUTURE_TENSE("#0984E3"),
    PRESENT_PERFECT("#00B894"),
    PAST_PERFECT("#74B9FF"),
    PRESENT_CONTINUOUS("#FDCB6E"),
    PAST_CONTINUOUS("#6C5CE7"),
    IMPERFECT("#A29BFE"),
    PRETERITE("#FD79A8"),
    
    // Aspects
    SIMPLE_ASPECT("#00CEC9"),
    PROGRESSIVE_ASPECT("#74B9FF"),
    PERFECT_ASPECT("#00B894"),
    PERFECTIVE_ASPECT("#0984E3"),
    IMPERFECTIVE_ASPECT("#FDCB6E"),
    
    // Moods
    INDICATIVE_MOOD("#00B894"),
    SUBJUNCTIVE_MOOD("#74B9FF"),
    IMPERATIVE_MOOD("#E17055"),
    CONDITIONAL_MOOD("#FDCB6E"),
    
    // Voices
    ACTIVE_VOICE("#00CEC9"),
    PASSIVE_VOICE("#74B9FF"),
    REFLEXIVE_VOICE("#0984E3"),
    
    // Other
    PUNCTUATION("#636E72"),
    DEFINITE_ARTICLE("#6C5CE7"),
    INDEFINITE_ARTICLE("#A29BFE"),
    MASCULINE("#74B9FF"),
    FEMININE("#FD79A8"),
    NEUTER("#00B894"),
    SINGULAR("#0984E3"),
    PLURAL("#E17055");

    private final String color;

    GrammarComponentType(String color) {
        this.color = color;
    }

    public String getColor() {
        return color;
    }

    /**
     * Get color for a component type string.
     *
     * @param type the component type string
     * @return the color hex code, or a default color if type not found
     */
    public static String getColorForType(String type) {
        try {
            return valueOf(type).getColor();
        } catch (IllegalArgumentException e) {
            // Return a default color if type not found
            return "#95A5A6";
        }
    }
}