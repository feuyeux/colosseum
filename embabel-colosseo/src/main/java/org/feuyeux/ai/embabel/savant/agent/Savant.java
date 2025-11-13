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
package org.feuyeux.ai.embabel.savant.agent;

import com.embabel.agent.api.annotation.AchievesGoal;
import com.embabel.agent.api.annotation.Action;
import com.embabel.agent.api.annotation.Agent;
import com.embabel.agent.api.annotation.Export;
import com.embabel.agent.api.common.OperationContext;
import com.embabel.common.ai.model.LlmOptions;
import org.feuyeux.ai.embabel.savant.config.SavantProperties;
import org.feuyeux.ai.embabel.savant.exception.TranslationServiceException;
import org.feuyeux.ai.embabel.savant.exception.TranslationValidationException;
import org.feuyeux.ai.embabel.savant.model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

/**
 * Translation Agent for multi-language translation and grammar analysis.
 * Provides translation services for 11 supported languages and detailed
 * grammar analysis with component highlighting.
 */
@Agent(description = "Multi-language translation and grammar analysis agent")
@Component
public class Savant {

    private static final Logger logger = LoggerFactory.getLogger(Savant.class);
    
    private final SavantProperties properties;
    private final ObjectMapper objectMapper;

    /**
     * Constructor with dependency injection.
     *
     * @param properties configuration properties for translation agent
     */
    public Savant(SavantProperties properties) {
        this.properties = properties;
        this.objectMapper = new ObjectMapper();
        logger.info("Savant initialized with max text length: {}, translation timeout: {}, analysis timeout: {}",
                properties.getMaxTextLength(),
                properties.getTranslationTimeout(),
                properties.getAnalysisTimeout());
    }

    /**
     * Translates source text into multiple target languages.
     * Performs parallel translation using Ollama for each target language.
     *
     * @param request the translation request containing source text and target languages
     * @param context the operation context for AI service access
     * @return TranslationResult containing all translations
     * @throws TranslationValidationException if validation fails
     * @throws TranslationServiceException if translation service fails
     */
    @AchievesGoal(
        description = "Translate text into multiple target languages",
        export = @Export(remote = true, name = "translateText")
    )
    @Action
    public TranslationResult translateText(TranslationRequest request, OperationContext context) {
        Instant startTime = Instant.now();
        logger.info("[{}] Starting translation request for {} target languages, text length: {} characters",
                startTime, request.targetLanguages().size(), request.sourceText().length());
        
        try {
            // Validate input request
            validateTranslationRequest(request);
            
            // Translate to each target language in parallel
            List<CompletableFuture<Map.Entry<String, String>>> translationFutures = 
                request.targetLanguages().stream()
                    .map(languageCode -> CompletableFuture.supplyAsync(() -> {
                        Instant langStartTime = Instant.now();
                        try {
                            logger.debug("[{}] Starting translation to language: {}", langStartTime, languageCode);
                            String translation = translateToLanguage(
                                request.sourceText(), 
                                languageCode, 
                                context
                            );
                            long duration = Instant.now().toEpochMilli() - langStartTime.toEpochMilli();
                            logger.debug("[{}] Completed translation to {} in {}ms", 
                                    Instant.now(), languageCode, duration);
                            return Map.entry(languageCode, translation);
                        } catch (TranslationServiceException e) {
                            logger.error("[{}] Translation service error for language {}: {} (error code: {})",
                                    Instant.now(), languageCode, e.getMessage(), e.getErrorCode(), e);
                            throw e;
                        } catch (Exception e) {
                            logger.error("[{}] Unexpected error during translation to {}: {}",
                                    Instant.now(), languageCode, e.getMessage(), e);
                            throw new TranslationServiceException(
                                "Translation failed for language: " + languageCode,
                                e,
                                "TRANSLATION_ERROR",
                                true
                            );
                        }
                    }))
                    .toList();
            
            // Wait for all translations to complete with timeout
            CompletableFuture<Void> allTranslations = CompletableFuture.allOf(
                translationFutures.toArray(new CompletableFuture[0])
            );
            
            try {
                long timeoutSeconds = properties.getTranslationTimeout().getSeconds();
                logger.debug("Waiting for all translations to complete (timeout: {}s)", timeoutSeconds);
                allTranslations.get(timeoutSeconds, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                logger.error("[{}] Translation timeout after {}s for {} languages",
                        Instant.now(), properties.getTranslationTimeout().getSeconds(), 
                        request.targetLanguages().size(), e);
                throw new TranslationServiceException(
                    String.format("Translation operation timed out after %d seconds. Please try with fewer languages or shorter text.",
                            properties.getTranslationTimeout().getSeconds()),
                    e,
                    "TRANSLATION_TIMEOUT",
                    true
                );
            } catch (Exception e) {
                Throwable cause = e.getCause();
                if (cause instanceof TranslationServiceException) {
                    throw (TranslationServiceException) cause;
                }
                logger.error("[{}] Translation process failed: {}", Instant.now(), e.getMessage(), e);
                throw new TranslationServiceException(
                    "Translation service is temporarily unavailable. Please try again later.",
                    e,
                    "SERVICE_UNAVAILABLE",
                    true
                );
            }
            
            // Collect all translation results into a map
            Map<String, String> translations = translationFutures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue
                ));
            
            long totalDuration = Instant.now().toEpochMilli() - startTime.toEpochMilli();
            logger.info("[{}] Successfully completed translations for {} languages in {}ms",
                    Instant.now(), translations.size(), totalDuration);
            
            return new TranslationResult(request.sourceText(), translations, Instant.now());
            
        } catch (TranslationValidationException e) {
            logger.warn("[{}] Translation validation failed: {} (error code: {})",
                    Instant.now(), e.getMessage(), e.getErrorCode());
            throw e;
        } catch (TranslationServiceException e) {
            logger.error("[{}] Translation service error: {} (error code: {}, retryable: {})",
                    Instant.now(), e.getMessage(), e.getErrorCode(), e.isRetryable());
            throw e;
        } catch (Exception e) {
            logger.error("[{}] Unexpected error during translation: {}", Instant.now(), e.getMessage(), e);
            throw new TranslationServiceException(
                "An unexpected error occurred during translation. Please try again.",
                e,
                "UNEXPECTED_ERROR",
                true
            );
        }
    }

    /**
     * Analyzes the grammar of a translated text.
     * Requires a completed TranslationResult as prerequisite.
     *
     * @param request the analysis request containing text and language code
     * @param translationResult the prerequisite translation result
     * @param context the operation context for AI service access
     * @return GrammarAnalysis containing structured grammar components
     * @throws TranslationValidationException if validation fails
     * @throws TranslationServiceException if analysis service fails
     */
    @AchievesGoal(
        description = "Analyze grammar of translated text with component highlighting",
        export = @Export(remote = true, name = "analyzeGrammar")
    )
    @Action
    public GrammarAnalysis analyzeGrammar(
            AnalysisRequest request,
            TranslationResult translationResult,
            OperationContext context) {
        
        Instant startTime = Instant.now();
        logger.info("[{}] Starting grammar analysis for language: {}", startTime, request.languageCode());
        
        try {
            // Validate input request
            validateAnalysisRequest(request, translationResult);
            
            // Get the translated text for the requested language
            String textToAnalyze = translationResult.getTranslation(request.languageCode());
            logger.debug("[{}] Text to analyze length: {} characters", Instant.now(), textToAnalyze.length());
            
            // Build grammar analysis prompt
            String prompt = buildGrammarAnalysisPrompt(request.languageCode(), textToAnalyze);
            
            // Invoke Ollama for grammar analysis with timeout handling
            String analysisResponse;
            try {
                logger.debug("[{}] Invoking AI service for grammar analysis", Instant.now());
                
                CompletableFuture<String> analysisFuture = CompletableFuture.supplyAsync(() -> {
                    try {
                        return context.ai()
                            .withLlm(LlmOptions
                                .withAutoLlm()
                                .withTemperature(0.3)
                                .withMaxTokens(8192))
                            .generateText(prompt);
                    } catch (Exception e) {
                        logger.error("[{}] AI service invocation failed: {}", Instant.now(), e.getMessage(), e);
                        throw new TranslationServiceException(
                            "Grammar analysis service is temporarily unavailable",
                            e,
                            "AI_SERVICE_ERROR",
                            true
                        );
                    }
                });
                
                long timeoutSeconds = properties.getAnalysisTimeout().getSeconds();
                analysisResponse = analysisFuture.get(timeoutSeconds, TimeUnit.SECONDS);
                
                long aiDuration = Instant.now().toEpochMilli() - startTime.toEpochMilli();
                logger.debug("[{}] Received grammar analysis response for {} in {}ms",
                        Instant.now(), request.languageCode(), aiDuration);
                
            } catch (TimeoutException e) {
                logger.error("[{}] Grammar analysis timeout after {}s for language: {}",
                        Instant.now(), properties.getAnalysisTimeout().getSeconds(), 
                        request.languageCode(), e);
                throw new TranslationServiceException(
                    String.format("Grammar analysis timed out after %d seconds. Please try again.",
                            properties.getAnalysisTimeout().getSeconds()),
                    e,
                    "ANALYSIS_TIMEOUT",
                    true
                );
            } catch (Exception e) {
                Throwable cause = e.getCause();
                if (cause instanceof TranslationServiceException) {
                    throw (TranslationServiceException) cause;
                }
                logger.error("[{}] Failed to analyze grammar for {}: {}",
                        Instant.now(), request.languageCode(), e.getMessage(), e);
                throw new TranslationServiceException(
                    "Grammar analysis failed. Please try again.",
                    e,
                    "ANALYSIS_ERROR",
                    true
                );
            }
            
            // Parse JSON response into List<GrammarComponent>
            List<GrammarComponent> components = parseGrammarComponents(analysisResponse);
            
            long totalDuration = Instant.now().toEpochMilli() - startTime.toEpochMilli();
            logger.info("[{}] Successfully analyzed grammar with {} components in {}ms",
                    Instant.now(), components.size(), totalDuration);
            
            return new GrammarAnalysis(textToAnalyze, request.languageCode(), components, Instant.now());
            
        } catch (TranslationValidationException e) {
            logger.warn("[{}] Grammar analysis validation failed: {} (error code: {})",
                    Instant.now(), e.getMessage(), e.getErrorCode());
            throw e;
        } catch (TranslationServiceException e) {
            logger.error("[{}] Grammar analysis service error: {} (error code: {}, retryable: {})",
                    Instant.now(), e.getMessage(), e.getErrorCode(), e.isRetryable());
            throw e;
        } catch (Exception e) {
            logger.error("[{}] Unexpected error during grammar analysis: {}", Instant.now(), e.getMessage(), e);
            throw new TranslationServiceException(
                "An unexpected error occurred during grammar analysis. Please try again.",
                e,
                "UNEXPECTED_ERROR",
                true
            );
        }
    }

    /**
     * Validates the translation request.
     *
     * @param request the translation request to validate
     * @throws TranslationValidationException if validation fails
     */
    private void validateTranslationRequest(TranslationRequest request) {
        if (request.sourceText() == null || request.sourceText().trim().isEmpty()) {
            logger.warn("Validation failed: empty source text");
            throw new TranslationValidationException(
                "Source text cannot be empty. Please enter text to translate.",
                "EMPTY_SOURCE_TEXT"
            );
        }
        
        if (request.sourceText().length() > properties.getMaxTextLength()) {
            logger.warn("Validation failed: source text length {} exceeds maximum {}",
                    request.sourceText().length(), properties.getMaxTextLength());
            throw new TranslationValidationException(
                String.format("Source text exceeds maximum length of %d characters. Current length: %d characters.",
                    properties.getMaxTextLength(), request.sourceText().length()),
                "TEXT_TOO_LONG"
            );
        }
        
        if (request.targetLanguages() == null || request.targetLanguages().isEmpty()) {
            logger.warn("Validation failed: no target languages selected");
            throw new TranslationValidationException(
                "At least one target language must be selected.",
                "NO_TARGET_LANGUAGES"
            );
        }
        
        if (!request.hasValidLanguageCodes()) {
            logger.warn("Validation failed: invalid language codes in request: {}", request.targetLanguages());
            throw new TranslationValidationException(
                "One or more selected language codes are invalid. Please select valid languages.",
                "INVALID_LANGUAGE_CODE"
            );
        }
        
        logger.debug("Translation request validation passed: {} characters, {} target languages",
                request.sourceText().length(), request.targetLanguages().size());
    }

    /**
     * Validates the analysis request.
     *
     * @param request the analysis request to validate
     * @param translationResult the translation result to check
     * @throws TranslationValidationException if validation fails
     */
    private void validateAnalysisRequest(AnalysisRequest request, TranslationResult translationResult) {
        if (request.text() == null || request.text().trim().isEmpty()) {
            logger.warn("Validation failed: empty text for analysis");
            throw new TranslationValidationException(
                "Text to analyze cannot be empty.",
                "EMPTY_ANALYSIS_TEXT"
            );
        }
        
        if (!request.hasValidLanguageCode()) {
            logger.warn("Validation failed: invalid language code for analysis: {}", request.languageCode());
            throw new TranslationValidationException(
                "Invalid language code: " + request.languageCode() + ". Please select a valid language.",
                "INVALID_LANGUAGE_CODE"
            );
        }
        
        if (!translationResult.hasTranslation(request.languageCode())) {
            logger.warn("Validation failed: no translation found for language: {}", request.languageCode());
            throw new TranslationValidationException(
                "Translation result does not contain translation for language: " + request.languageCode() + 
                ". Please translate to this language first.",
                "TRANSLATION_NOT_FOUND"
            );
        }
        
        logger.debug("Analysis request validation passed for language: {}", request.languageCode());
    }

    /**
     * Translates text to a specific target language.
     *
     * @param sourceText the text to translate
     * @param targetLanguageCode the target language code
     * @param context the operation context
     * @return the translated text
     * @throws TranslationServiceException if translation fails
     */
    private String translateToLanguage(String sourceText, String targetLanguageCode, OperationContext context) {
        SupportedLanguage targetLanguage = SupportedLanguage.fromCode(targetLanguageCode)
            .orElseThrow(() -> new TranslationValidationException(
                "Unsupported language code: " + targetLanguageCode,
                "UNSUPPORTED_LANGUAGE"
            ));
        
        try {
            String prompt = buildTranslationPrompt(sourceText, targetLanguage);
            
            String translation = context.ai()
                .withLlm(LlmOptions
                    .withAutoLlm()
                    .withTemperature(0.7)
                    .withMaxTokens(4096))
                .generateText(prompt);
            
            if (translation == null || translation.trim().isEmpty()) {
                logger.error("AI service returned empty translation for language: {}", targetLanguageCode);
                throw new TranslationServiceException(
                    "Translation service returned empty result for language: " + targetLanguageCode,
                    "EMPTY_TRANSLATION_RESULT",
                    true
                );
            }
            
            return translation;
            
        } catch (TranslationServiceException e) {
            throw e;
        } catch (Exception e) {
            logger.error("AI service error during translation to {}: {}", targetLanguageCode, e.getMessage(), e);
            throw new TranslationServiceException(
                "Failed to translate to " + targetLanguage.getEnglishName() + ". The translation service may be unavailable.",
                e,
                "AI_SERVICE_ERROR",
                true
            );
        }
    }

    /**
     * Builds the translation prompt.
     *
     * @param sourceText the source text to translate
     * @param targetLanguage the target language
     * @return the formatted prompt
     */
    private String buildTranslationPrompt(String sourceText, SupportedLanguage targetLanguage) {
        return String.format("""
            You are a professional translator. Translate the following text to %s (%s).
            
            CRITICAL REQUIREMENTS:
            - The translation MUST be in %s language ONLY
            - Use ONLY %s script/characters (e.g., Arabic must use Arabic script, Hindi must use Devanagari, Chinese must use Chinese characters)
            - Do NOT mix languages or scripts
            - Maintain the original meaning and tone
            - Preserve formatting including line breaks
            - Use natural and idiomatic expressions in the target language
            - Do NOT add explanations, notes, or translations in other languages
            
            Source text:
            %s
            
            Provide ONLY the %s translation, nothing else.
            """,
            targetLanguage.getNativeName(),
            targetLanguage.getEnglishName(),
            targetLanguage.getEnglishName(),
            targetLanguage.getNativeName(),
            sourceText,
            targetLanguage.getNativeName()
        ).trim();
    }

    /**
     * Builds the grammar analysis prompt with comprehensive grammatical component types.
     *
     * @param languageCode the language code
     * @param text the text to analyze
     * @return the formatted prompt
     */
    private String buildGrammarAnalysisPrompt(String languageCode, String text) {
        SupportedLanguage language = SupportedLanguage.fromCode(languageCode)
            .orElseThrow(() -> new IllegalArgumentException("Unsupported language code: " + languageCode));
        
        String languageSpecificGuidelines = getLanguageSpecificGuidelines(language);
        
        return String.format("""
            You are a linguistic expert in %s grammar. Analyze this sentence and return detailed grammatical components.
            
            Sentence: %s
            
            Return JSON format:
            {
              "components": [
                {
                  "text": "word",
                  "componentType": "TYPE",
                  "startIndex": 0,
                  "endIndex": 4,
                  "features": {
                    "syntacticRole": "subject|predicate|object|direct_object|indirect_object",
                    "partOfSpeech": "noun|verb|adjective|adverb|pronoun|article|preposition|conjunction|particle",
                    "case": "nominative|accusative|genitive|dative|instrumental|prepositional",
                    "gender": "masculine|feminine|neuter",
                    "number": "singular|plural|dual",
                    "person": "first|second|third",
                    "tense": "present|past|future|present_perfect|past_perfect|present_continuous",
                    "aspect": "simple|progressive|perfect|perfective|imperfective",
                    "mood": "indicative|subjunctive|imperative|conditional",
                    "voice": "active|passive|reflexive",
                    "honorific": "plain|polite|formal|humble|respectful"
                  }
                }
              ]
            }
            
            Valid componentType values:
            - Sentence roles: SUBJECT, PREDICATE, OBJECT, DIRECT_OBJECT, INDIRECT_OBJECT, COMPLEMENT, ADVERBIAL
            - Parts of speech: NOUN, VERB, ADJECTIVE, ADVERB, PRONOUN, ARTICLE, PREPOSITION, CONJUNCTION, PARTICLE
            - Verb types: MAIN_VERB, AUXILIARY_VERB, MODAL_VERB, COPULA
            - Particles: TOPIC_PARTICLE, SUBJECT_PARTICLE, OBJECT_PARTICLE, POSSESSIVE_PARTICLE
            - Chinese: MEASURE_WORD, ASPECT_PARTICLE_LE, ASPECT_PARTICLE_ZHE, ASPECT_PARTICLE_GUO, MODAL_PARTICLE_MA, MODAL_PARTICLE_NE, MODAL_PARTICLE_BA, STRUCTURAL_PARTICLE_DE
            - Cases: NOMINATIVE_CASE, ACCUSATIVE_CASE, GENITIVE_CASE, DATIVE_CASE, INSTRUMENTAL_CASE, PREPOSITIONAL_CASE
            - Tenses: PRESENT_TENSE, PAST_TENSE, FUTURE_TENSE, PRESENT_PERFECT, PAST_PERFECT, PRESENT_CONTINUOUS, PAST_CONTINUOUS, IMPERFECT, PRETERITE
            - Aspects: SIMPLE_ASPECT, PROGRESSIVE_ASPECT, PERFECT_ASPECT, PERFECTIVE_ASPECT, IMPERFECTIVE_ASPECT
            - Moods: INDICATIVE_MOOD, SUBJUNCTIVE_MOOD, IMPERATIVE_MOOD, CONDITIONAL_MOOD
            - Voices: ACTIVE_VOICE, PASSIVE_VOICE, REFLEXIVE_VOICE
            - Other: PUNCTUATION, DEFINITE_ARTICLE, INDEFINITE_ARTICLE, MASCULINE, FEMININE, NEUTER, SINGULAR, PLURAL
            
            %s
            
            CRITICAL INSTRUCTIONS:
            1. Analyze EVERY word and punctuation mark
            2. For EACH word, identify:
               - Primary componentType (e.g., SUBJECT, VERB, NOUN)
               - syntacticRole in features (subject/predicate/object/etc.)
               - partOfSpeech in features (noun/verb/adjective/etc.)
               - case (if applicable): nominative/accusative/genitive/dative/instrumental/prepositional
               - gender (if applicable): masculine/feminine/neuter
               - number (if applicable): singular/plural/dual
               - person (for verbs/pronouns): first/second/third
               - tense (for verbs): present/past/future/present_perfect/past_perfect/present_continuous
               - aspect (for verbs): simple/progressive/perfect/perfective/imperfective
               - mood (for verbs): indicative/subjunctive/imperative/conditional
               - voice (for verbs): active/passive/reflexive
               - honorific (if applicable): plain/polite/formal/humble/respectful
            3. Use accurate startIndex and endIndex (0-indexed character positions)
            4. Return ONLY valid JSON, no extra text or explanations
            5. Be comprehensive - include ALL grammatical information for each word
            """,
            language.getEnglishName(),
            text,
            languageSpecificGuidelines
        ).trim();
    }
    
    /**
     * Gets language-specific analysis guidelines.
     *
     * @param language the target language
     * @return language-specific guidelines
     */
    private String getLanguageSpecificGuidelines(SupportedLanguage language) {
        return switch (language) {
            case ENGLISH -> """
                English: Articles (a/an=INDEFINITE_ARTICLE, the=DEFINITE_ARTICLE). Tenses: present/past/future/present_perfect/past_perfect/present_continuous/past_continuous. Prepositions: in/on/at/by/with. Features: tense, aspect (simple/progressive/perfect), voice (active/passive), person, number.
                """;
            case CHINESE -> """
                Chinese: Aspect particles: 了(ASPECT_PARTICLE_LE), 着(ASPECT_PARTICLE_ZHE), 过(ASPECT_PARTICLE_GUO). Modal particles: 吗(MODAL_PARTICLE_MA), 呢(MODAL_PARTICLE_NE), 吧(MODAL_PARTICLE_BA). Measure words: 个/只/本/张/条/件(MEASURE_WORD). Structural: 的/地/得(STRUCTURAL_PARTICLE_DE). Features: aspect.
                """;
            case JAPANESE -> """
                Japanese: Particles: は(TOPIC_PARTICLE), が(SUBJECT_PARTICLE), を(OBJECT_PARTICLE), に/で(DIRECTION_PARTICLE/LOCATION_PARTICLE), の(POSSESSIVE_PARTICLE), と/から/まで/へ/や/も(PARTICLE). Honorifics: です/ます(polite), だ/である(plain). Tense: た/だ(past), る/う(present). Features: honorific, tense, aspect.
                """;
            case KOREAN -> """
                Korean: Particles: 이/가(SUBJECT_PARTICLE), 을/를(OBJECT_PARTICLE), 의(POSSESSIVE_PARTICLE), 에/에서(DIRECTION_PARTICLE/LOCATION_PARTICLE), 으로/로/와/과/도/만/부터/까지(PARTICLE). Honorifics: 요(polite), 습니다/ㅂ니다(formal), 해라체(plain). Tense: 았/었(past), ㄴ다/는다(present), 겠(future). Features: honorific, tense.
                """;
            case FRENCH -> """
                French: Articles: le/la/les(DEFINITE_ARTICLE), un/une/des(INDEFINITE_ARTICLE), du/de la(partitive). Gender: masculine/feminine. Tenses: présent, imparfait(IMPERFECT), passé composé(PRESENT_PERFECT), futur, conditionnel. Mood: subjunctive. Features: gender, number, tense, mood, person.
                """;
            case GERMAN -> """
                German: Articles: der/die/das(definite), ein/eine(indefinite). Cases: nominative/accusative/dative/genitive. Gender: masculine/feminine/neuter. Tenses: Präsens, Präteritum, Perfekt, Futur. Features: case, gender, number, tense.
                """;
            case SPANISH -> """
                Spanish: Articles: el/la/los/las(definite), un/una/unos/unas(indefinite). Gender: masculine/feminine. Tenses: presente, pretérito(PRETERITE), imperfecto(IMPERFECT), futuro, condicional. Mood: subjunctive (presente/imperfecto de subjuntivo). Features: gender, number, tense, mood, person.
                """;
            case ITALIAN -> """
                Italian: Articles: il/lo/la/i/gli/le(definite), un/uno/una(indefinite). Gender: masculine/feminine. Tenses: presente, imperfetto(IMPERFECT), passato prossimo(PRESENT_PERFECT), futuro. Mood: indicativo, congiuntivo(SUBJUNCTIVE), condizionale, imperativo. Features: gender, number, tense, mood, person.
                """;
            case PORTUGUESE -> """
                Portuguese: Articles: o/a/os/as(definite), um/uma/uns/umas(indefinite). Gender: masculine/feminine. Tenses: presente, pretérito perfeito, pretérito imperfeito(IMPERFECT), futuro. Mood: indicativo, subjuntivo(SUBJUNCTIVE), imperativo. Features: gender, number, tense, mood, person.
                """;
            case RUSSIAN -> """
                Russian: Cases: nominative/genitive/dative/accusative/instrumental/prepositional. Gender: masculine/feminine/neuter. Aspect: perfective/imperfective. Tenses: past/present/future. Features: case, gender, number (singular/plural), aspect, tense, person.
                """;
            case ARABIC -> """
                Arabic: Definiteness: ال(definite article). Gender: masculine/feminine. Number: singular/dual/plural. Cases: nominative/accusative/genitive. Tenses: past/present/future (with سوف/س). Mood: indicative/subjunctive/jussive. Features: case, gender, number, tense, mood.
                """;
            case HINDI -> """
                Hindi: Postpositions: ने(ergative), को(dative/accusative), से(instrumental/ablative), में(locative), पर(locative). Gender: masculine/feminine. Number: singular/plural. Cases: direct/oblique/vocative. Tenses: present/past/future/present_perfect/past_perfect. Aspect: habitual/progressive/perfective. Honorifics: तू(intimate), तुम(informal), आप(formal). Features: case, gender, number, tense, aspect, honorific, person.
                """;
        };
    }

    /**
     * Parses the grammar analysis JSON response into a list of GrammarComponent objects.
     *
     * @param jsonResponse the JSON response from Ollama
     * @return list of grammar components
     * @throws TranslationServiceException if parsing fails
     */
    private List<GrammarComponent> parseGrammarComponents(String jsonResponse) {
        try {
            // Extract JSON from response (in case there's extra text)
            String cleanJson = extractJson(jsonResponse);
            
            if (cleanJson == null || cleanJson.trim().isEmpty()) {
                logger.error("Empty JSON response from grammar analysis");
                throw new TranslationServiceException(
                    "Grammar analysis returned empty response. Please try again.",
                    "EMPTY_ANALYSIS_RESPONSE",
                    true
                );
            }
            
            JsonNode root = objectMapper.readTree(cleanJson);
            JsonNode componentsNode = root.get("components");
            
            if (componentsNode == null || !componentsNode.isArray()) {
                logger.error("Invalid grammar analysis response format: missing or invalid 'components' field");
                throw new TranslationServiceException(
                    "Grammar analysis returned invalid format. Please try again.",
                    "INVALID_ANALYSIS_FORMAT",
                    true
                );
            }
            
            List<GrammarComponent> components = new ArrayList<>();
            
            for (JsonNode componentNode : componentsNode) {
                try {
                    String text = componentNode.get("text").asText();
                    String componentType = componentNode.get("componentType").asText();
                    int startIndex = componentNode.get("startIndex").asInt();
                    int endIndex = componentNode.get("endIndex").asInt();
                    
                    // Get color for component type
                    String color = GrammarComponentType.getColorForType(componentType);
                    
                    // Parse features map
                    Map<String, String> features = new HashMap<>();
                    JsonNode featuresNode = componentNode.get("features");
                    if (featuresNode != null && featuresNode.isObject()) {
                        featuresNode.fields().forEachRemaining(entry -> 
                            features.put(entry.getKey(), entry.getValue().asText())
                        );
                    }
                    
                    components.add(new GrammarComponent(
                        text, componentType, color, startIndex, endIndex, features
                    ));
                } catch (Exception e) {
                    logger.warn("Failed to parse individual grammar component, skipping: {}", e.getMessage());
                    // Continue parsing other components
                }
            }
            
            if (components.isEmpty()) {
                logger.warn("No valid grammar components found in response");
                throw new TranslationServiceException(
                    "Grammar analysis did not return any valid components. Please try again.",
                    "NO_COMPONENTS_FOUND",
                    true
                );
            }
            
            logger.debug("Successfully parsed {} grammar components", components.size());
            return components;
            
        } catch (JsonProcessingException e) {
            logger.error("Failed to parse grammar analysis JSON: {}", e.getMessage(), e);
            throw new TranslationServiceException(
                "Failed to parse grammar analysis response. The service may have returned invalid data.",
                e,
                "JSON_PARSE_ERROR",
                true
            );
        } catch (TranslationServiceException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error parsing grammar components: {}", e.getMessage(), e);
            throw new TranslationServiceException(
                "An unexpected error occurred while processing grammar analysis.",
                e,
                "PARSE_ERROR",
                true
            );
        }
    }

    /**
     * Extracts JSON content from a response that may contain additional text.
     *
     * @param response the response string
     * @return the extracted JSON string, or null if no valid JSON found
     */
    private String extractJson(String response) {
        if (response == null || response.trim().isEmpty()) {
            logger.warn("Attempted to extract JSON from null or empty response");
            return null;
        }
        
        // Find the first '{' and last '}'
        int start = response.indexOf('{');
        int end = response.lastIndexOf('}');
        
        if (start != -1 && end != -1 && start < end) {
            String extracted = response.substring(start, end + 1);
            logger.debug("Extracted JSON from response (length: {} -> {})", response.length(), extracted.length());
            return extracted;
        }
        
        logger.warn("No valid JSON structure found in response");
        return response;
    }
}
