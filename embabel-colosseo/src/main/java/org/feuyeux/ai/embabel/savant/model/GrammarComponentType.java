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
 * Comprehensive enum representing 120+ types of grammar components with associated colors
 * for visual highlighting in the UI. Covers all grammatical categories across 11 supported languages.
 */
public enum GrammarComponentType {
    // ========== Sentence Components - 10 types ==========
    SUBJECT("SUBJECT", "#FF6B6B", "主语", "Subject"),
    PREDICATE("PREDICATE", "#4ECDC4", "谓语", "Predicate"),
    OBJECT("OBJECT", "#45B7D1", "宾语", "Object"),
    DIRECT_OBJECT("DIRECT_OBJECT", "#3A86FF", "直接宾语", "Direct Object"),
    INDIRECT_OBJECT("INDIRECT_OBJECT", "#8338EC", "间接宾语", "Indirect Object"),
    SUBJECT_COMPLEMENT("SUBJECT_COMPLEMENT", "#5A67D8", "主语补语", "Subject Complement"),
    OBJECT_COMPLEMENT("OBJECT_COMPLEMENT", "#667EEA", "宾语补语", "Object Complement"),
    ATTRIBUTIVE("ATTRIBUTIVE", "#FFA07A", "定语", "Attributive"),
    ADVERBIAL("ADVERBIAL", "#FFB347", "状语", "Adverbial"),
    COMPLEMENT("COMPLEMENT", "#FF9E80", "补语", "Complement"),
    
    // ========== Parts of Speech - 10 types ==========
    NOUN("NOUN", "#FF6B6B", "名词", "Noun"),
    PRONOUN("PRONOUN", "#FFDAB9", "代词", "Pronoun"),
    VERB("VERB", "#06FFA5", "动词", "Verb"),
    ADJECTIVE("ADJECTIVE", "#FFA07A", "形容词", "Adjective"),
    ADVERB("ADVERB", "#FFB347", "副词", "Adverb"),
    PREPOSITION("PREPOSITION", "#F38181", "介词", "Preposition"),
    CONJUNCTION("CONJUNCTION", "#AA96DA", "连词", "Conjunction"),
    ARTICLE("ARTICLE", "#A8DADC", "冠词", "Article"),
    DETERMINER("DETERMINER", "#95E1D3", "限定词", "Determiner"),
    INTERJECTION("INTERJECTION", "#FFE5B4", "感叹词", "Interjection"),
    
    // ========== Verb Types - 4 types ==========
    MAIN_VERB("MAIN_VERB", "#06FFA5", "主动词", "Main Verb"),
    AUXILIARY_VERB("AUXILIARY_VERB", "#4ECDC4", "助动词", "Auxiliary Verb"),
    MODAL_VERB("MODAL_VERB", "#2EC4B6", "情态动词", "Modal Verb"),
    COPULA("COPULA", "#1BA098", "系动词", "Copula"),
    
    // ========== Particles - 8 types ==========
    PARTICLE("PARTICLE", "#E0BBE4", "助词", "Particle"),
    TOPIC_PARTICLE("TOPIC_PARTICLE", "#D8A7D8", "主题助词", "Topic Particle"),
    SUBJECT_PARTICLE("SUBJECT_PARTICLE", "#C993C9", "主语助词", "Subject Particle"),
    OBJECT_PARTICLE("OBJECT_PARTICLE", "#BA7FBA", "宾语助词", "Object Particle"),
    POSSESSIVE_PARTICLE("POSSESSIVE_PARTICLE", "#AB6BAB", "属格助词", "Possessive Particle"),
    DIRECTION_PARTICLE("DIRECTION_PARTICLE", "#9C579C", "方向助词", "Direction Particle"),
    LOCATION_PARTICLE("LOCATION_PARTICLE", "#8D438D", "位置助词", "Location Particle"),
    MEANS_PARTICLE("MEANS_PARTICLE", "#7E2F7E", "工具助词", "Means Particle"),
    
    // ========== Chinese Specific - 8 types ==========
    MEASURE_WORD("MEASURE_WORD", "#B8E6B8", "量词", "Measure Word"),
    ASPECT_PARTICLE_LE("ASPECT_PARTICLE_LE", "#6BCB77", "体助词-了", "Aspect Particle 了"),
    ASPECT_PARTICLE_ZHE("ASPECT_PARTICLE_ZHE", "#7DD689", "体助词-着", "Aspect Particle 着"),
    ASPECT_PARTICLE_GUO("ASPECT_PARTICLE_GUO", "#8FE19B", "体助词-过", "Aspect Particle 过"),
    MODAL_PARTICLE_MA("MODAL_PARTICLE_MA", "#A8E6CF", "语气助词-吗", "Modal Particle 吗"),
    MODAL_PARTICLE_NE("MODAL_PARTICLE_NE", "#B9EBD9", "语气助词-呢", "Modal Particle 呢"),
    MODAL_PARTICLE_BA("MODAL_PARTICLE_BA", "#CAF0E3", "语气助词-吧", "Modal Particle 吧"),
    STRUCTURAL_PARTICLE_DE("STRUCTURAL_PARTICLE_DE", "#DBF5ED", "结构助词-的/地/得", "Structural Particle 的/地/得"),
    
    // ========== Cases - 9 types ==========
    NOMINATIVE_CASE("NOMINATIVE_CASE", "#FFD93D", "主格", "Nominative"),
    ACCUSATIVE_CASE("ACCUSATIVE_CASE", "#FFEC5C", "宾格", "Accusative"),
    GENITIVE_CASE("GENITIVE_CASE", "#FFF176", "属格/所有格", "Genitive"),
    DATIVE_CASE("DATIVE_CASE", "#FFF59D", "与格", "Dative"),
    INSTRUMENTAL_CASE("INSTRUMENTAL_CASE", "#FFEB3B", "工具格", "Instrumental"),
    PREPOSITIONAL_CASE("PREPOSITIONAL_CASE", "#FDD835", "前置格", "Prepositional"),
    LOCATIVE_CASE("LOCATIVE_CASE", "#F9A825", "位格", "Locative"),
    VOCATIVE_CASE("VOCATIVE_CASE", "#F57F17", "呼格", "Vocative"),
    ABLATIVE_CASE("ABLATIVE_CASE", "#E65100", "离格/夺格", "Ablative"),
    
    // ========== Tenses - 12 types ==========
    PRESENT_TENSE("PRESENT_TENSE", "#8FD14F", "现在时", "Present"),
    PAST_TENSE("PAST_TENSE", "#6BCB77", "过去时", "Past"),
    FUTURE_TENSE("FUTURE_TENSE", "#A8E6CF", "将来时", "Future"),
    PRESENT_PERFECT("PRESENT_PERFECT", "#4CAF50", "现在完成时", "Present Perfect"),
    PAST_PERFECT("PAST_PERFECT", "#388E3C", "过去完成时", "Past Perfect"),
    FUTURE_PERFECT("FUTURE_PERFECT", "#2E7D32", "将来完成时", "Future Perfect"),
    PRESENT_CONTINUOUS("PRESENT_CONTINUOUS", "#81C784", "现在进行时", "Present Continuous"),
    PAST_CONTINUOUS("PAST_CONTINUOUS", "#66BB6A", "过去进行时", "Past Continuous"),
    IMPERFECT("IMPERFECT", "#558B2F", "未完成过去时", "Imperfect"),
    PRETERITE("PRETERITE", "#33691E", "简单过去时", "Preterite"),
    PLUPERFECT("PLUPERFECT", "#1B5E20", "过去完成时", "Pluperfect"),
    CONDITIONAL_TENSE("CONDITIONAL_TENSE", "#689F38", "条件时", "Conditional"),
    
    // ========== Aspects - 7 types ==========
    SIMPLE_ASPECT("SIMPLE_ASPECT", "#4D96FF", "简单体", "Simple"),
    PROGRESSIVE_ASPECT("PROGRESSIVE_ASPECT", "#5B9FFF", "进行体", "Progressive"),
    PERFECT_ASPECT("PERFECT_ASPECT", "#6BA8FF", "完成体", "Perfect"),
    PERFECTIVE_ASPECT("PERFECTIVE_ASPECT", "#7BB1FF", "完整体", "Perfective"),
    IMPERFECTIVE_ASPECT("IMPERFECTIVE_ASPECT", "#8BBAFF", "未完整体", "Imperfective"),
    EXPERIENTIAL_ASPECT("EXPERIENTIAL_ASPECT", "#9BC5FF", "经验体", "Experiential"),
    RESULTATIVE_ASPECT("RESULTATIVE_ASPECT", "#ABCFFF", "结果体", "Resultative"),
    
    // ========== Moods - 7 types ==========
    INDICATIVE_MOOD("INDICATIVE_MOOD", "#FF6B9D", "陈述语气", "Indicative"),
    SUBJUNCTIVE_MOOD("SUBJUNCTIVE_MOOD", "#FF8FAB", "虚拟语气", "Subjunctive"),
    IMPERATIVE_MOOD("IMPERATIVE_MOOD", "#FFB3C1", "命令语气", "Imperative"),
    CONDITIONAL_MOOD("CONDITIONAL_MOOD", "#FFC9D0", "条件语气", "Conditional"),
    VOLITIONAL_MOOD("VOLITIONAL_MOOD", "#FFD7DC", "意向语气", "Volitional"),
    POTENTIAL_MOOD("POTENTIAL_MOOD", "#FFE5E9", "可能语气", "Potential"),
    JUSSIVE_MOOD("JUSSIVE_MOOD", "#FFF3F5", "祈使语气", "Jussive"),
    
    // ========== Voices - 5 types ==========
    ACTIVE_VOICE("ACTIVE_VOICE", "#9C27B0", "主动语态", "Active"),
    PASSIVE_VOICE("PASSIVE_VOICE", "#BA68C8", "被动语态", "Passive"),
    REFLEXIVE_VOICE("REFLEXIVE_VOICE", "#CE93D8", "反身语态", "Reflexive"),
    MIDDLE_VOICE("MIDDLE_VOICE", "#E1BEE7", "中间语态", "Middle"),
    CAUSATIVE_VOICE("CAUSATIVE_VOICE", "#F3E5F5", "使役语态", "Causative"),
    
    // ========== Number - 3 types ==========
    SINGULAR("SINGULAR", "#00BCD4", "单数", "Singular"),
    PLURAL("PLURAL", "#26C6DA", "复数", "Plural"),
    DUAL("DUAL", "#4DD0E1", "双数", "Dual"),
    
    // ========== Gender - 3 types ==========
    MASCULINE("MASCULINE", "#FF5722", "阳性", "Masculine"),
    FEMININE("FEMININE", "#FF7043", "阴性", "Feminine"),
    NEUTER("NEUTER", "#FF8A65", "中性", "Neuter"),
    
    // ========== Person - 3 types ==========
    FIRST_PERSON("FIRST_PERSON", "#009688", "第一人称", "1st Person"),
    SECOND_PERSON("SECOND_PERSON", "#26A69A", "第二人称", "2nd Person"),
    THIRD_PERSON("THIRD_PERSON", "#4DB6AC", "第三人称", "3rd Person"),
    
    // ========== Definiteness - 2 types ==========
    DEFINITE("DEFINITE", "#80CBC4", "定指", "Definite"),
    INDEFINITE("INDEFINITE", "#B2DFDB", "不定指", "Indefinite"),
    
    // ========== Article Types - 3 types ==========
    DEFINITE_ARTICLE("DEFINITE_ARTICLE", "#A8DADC", "定冠词", "Definite Article"),
    INDEFINITE_ARTICLE("INDEFINITE_ARTICLE", "#C5E7E9", "不定冠词", "Indefinite Article"),
    PARTITIVE_ARTICLE("PARTITIVE_ARTICLE", "#E2F4F5", "部分冠词", "Partitive Article"),
    
    // ========== Honorifics - 6 types ==========
    PLAIN_FORM("PLAIN_FORM", "#C3ACD0", "普通体", "Plain"),
    POLITE_FORM("POLITE_FORM", "#D4B5D4", "礼貌体", "Polite"),
    FORMAL_FORM("FORMAL_FORM", "#E5BEE5", "正式体", "Formal"),
    HUMBLE_FORM("HUMBLE_FORM", "#B19CD9", "谦让体", "Humble"),
    RESPECTFUL_FORM("RESPECTFUL_FORM", "#9F7AEA", "尊敬体", "Respectful"),
    INTIMATE_FORM("INTIMATE_FORM", "#8B5CF6", "亲密体", "Intimate"),
    
    // ========== Punctuation - 5 types ==========
    PUNCTUATION("PUNCTUATION", "#CCCCCC", "标点", "Punctuation"),
    PERIOD("PERIOD", "#BDBDBD", "句号", "Period"),
    COMMA("COMMA", "#9E9E9E", "逗号", "Comma"),
    QUESTION_MARK("QUESTION_MARK", "#757575", "问号", "Question Mark"),
    EXCLAMATION_MARK("EXCLAMATION_MARK", "#616161", "感叹号", "Exclamation Mark"),
    
    // ========== Other ==========
    UNKNOWN("UNKNOWN", "#E0E0E0", "未知", "Unknown");

    private final String type;
    private final String color;
    private final String chineseName;
    private final String englishName;

    GrammarComponentType(String type, String color, String chineseName, String englishName) {
        this.type = type;
        this.color = color;
        this.chineseName = chineseName;
        this.englishName = englishName;
    }

    public String getType() {
        return type;
    }

    public String getColor() {
        return color;
    }

    public String getChineseName() {
        return chineseName;
    }

    public String getEnglishName() {
        return englishName;
    }

    /**
     * Get the bilingual name in format "中文 | English".
     *
     * @return bilingual name string
     */
    public String getBilingualName() {
        return chineseName + " | " + englishName;
    }

    /**
     * Find a GrammarComponentType by its type string.
     *
     * @param type the component type string
     * @return Optional containing the matching GrammarComponentType, or empty if not found
     */
    public static Optional<GrammarComponentType> fromType(String type) {
        if (type == null) {
            return Optional.empty();
        }
        return Arrays.stream(values())
                .filter(component -> component.type.equalsIgnoreCase(type))
                .findFirst();
    }

    /**
     * Get the color associated with a component type string.
     *
     * @param type the component type string
     * @return the color hex code, or a default gray color if type is not found
     */
    public static String getColorForType(String type) {
        return fromType(type)
                .map(GrammarComponentType::getColor)
                .orElse("#CCCCCC"); // Default gray color
    }

    @Override
    public String toString() {
        return String.format("%s (%s)", type, chineseName);
    }
}
