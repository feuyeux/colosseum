package org.feuyeux.ai.embabel.savant.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.Map;

@Configuration
@PropertySource(value = "classpath:prompts.yaml", factory = YamlPropertySourceFactory.class)
@ConfigurationProperties(prefix = "translation")
public class PromptsConfig {
    
    private String translation;
    private GrammarPrompt grammar;
    private Map<String, String> guidelines;
    private MessageConfig messages;
    
    public static class GrammarPrompt {
        private String prompt;
        
        public String getPrompt() {
            return prompt;
        }
        
        public void setPrompt(String prompt) {
            this.prompt = prompt;
        }
    }
    
    public static class MessageConfig {
        private ValidationMessages validation;
        private ServiceMessages service;
        
        public static class ValidationMessages {
            private String emptySource;
            private String textTooLong;
            private String noLanguages;
            private String invalidLanguages;
            private String emptyAnalysisText;
            private String invalidLanguageCode;
            private String missingTranslation;
            
            // Getters and setters
            public String getEmptySource() { return emptySource; }
            public void setEmptySource(String emptySource) { this.emptySource = emptySource; }
            
            public String getTextTooLong() { return textTooLong; }
            public void setTextTooLong(String textTooLong) { this.textTooLong = textTooLong; }
            
            public String getNoLanguages() { return noLanguages; }
            public void setNoLanguages(String noLanguages) { this.noLanguages = noLanguages; }
            
            public String getInvalidLanguages() { return invalidLanguages; }
            public void setInvalidLanguages(String invalidLanguages) { this.invalidLanguages = invalidLanguages; }
            
            public String getEmptyAnalysisText() { return emptyAnalysisText; }
            public void setEmptyAnalysisText(String emptyAnalysisText) { this.emptyAnalysisText = emptyAnalysisText; }
            
            public String getInvalidLanguageCode() { return invalidLanguageCode; }
            public void setInvalidLanguageCode(String invalidLanguageCode) { this.invalidLanguageCode = invalidLanguageCode; }
            
            public String getMissingTranslation() { return missingTranslation; }
            public void setMissingTranslation(String missingTranslation) { this.missingTranslation = missingTranslation; }
        }
        
        public static class ServiceMessages {
            private String unsupportedLanguage;
            private String emptyTranslationResult;
            private String translationFailed;
            private String grammarUnavailable;
            private String grammarTimeout;
            private String grammarFailed;
            private String grammarError;
            private String emptyGrammarResponse;
            private String invalidGrammarFormat;
            private String noGrammarComponents;
            private String grammarParseError;
            private String grammarProcessingError;
            
            // Getters and setters
            public String getUnsupportedLanguage() { return unsupportedLanguage; }
            public void setUnsupportedLanguage(String unsupportedLanguage) { this.unsupportedLanguage = unsupportedLanguage; }
            
            public String getEmptyTranslationResult() { return emptyTranslationResult; }
            public void setEmptyTranslationResult(String emptyTranslationResult) { this.emptyTranslationResult = emptyTranslationResult; }
            
            public String getTranslationFailed() { return translationFailed; }
            public void setTranslationFailed(String translationFailed) { this.translationFailed = translationFailed; }
            
            public String getGrammarUnavailable() { return grammarUnavailable; }
            public void setGrammarUnavailable(String grammarUnavailable) { this.grammarUnavailable = grammarUnavailable; }
            
            public String getGrammarTimeout() { return grammarTimeout; }
            public void setGrammarTimeout(String grammarTimeout) { this.grammarTimeout = grammarTimeout; }
            
            public String getGrammarFailed() { return grammarFailed; }
            public void setGrammarFailed(String grammarFailed) { this.grammarFailed = grammarFailed; }
            
            public String getGrammarError() { return grammarError; }
            public void setGrammarError(String grammarError) { this.grammarError = grammarError; }
            
            public String getEmptyGrammarResponse() { return emptyGrammarResponse; }
            public void setEmptyGrammarResponse(String emptyGrammarResponse) { this.emptyGrammarResponse = emptyGrammarResponse; }
            
            public String getInvalidGrammarFormat() { return invalidGrammarFormat; }
            public void setInvalidGrammarFormat(String invalidGrammarFormat) { this.invalidGrammarFormat = invalidGrammarFormat; }
            
            public String getNoGrammarComponents() { return noGrammarComponents; }
            public void setNoGrammarComponents(String noGrammarComponents) { this.noGrammarComponents = noGrammarComponents; }
            
            public String getGrammarParseError() { return grammarParseError; }
            public void setGrammarParseError(String grammarParseError) { this.grammarParseError = grammarParseError; }
            
            public String getGrammarProcessingError() { return grammarProcessingError; }
            public void setGrammarProcessingError(String grammarProcessingError) { this.grammarProcessingError = grammarProcessingError; }
        }
        
        public ValidationMessages getValidation() { return validation; }
        public void setValidation(ValidationMessages validation) { this.validation = validation; }
        
        public ServiceMessages getService() { return service; }
        public void setService(ServiceMessages service) { this.service = service; }
    }
    
    // Getters and setters
    public String getTranslation() { return translation; }
    public void setTranslation(String translation) { this.translation = translation; }
    
    public GrammarPrompt getGrammar() { return grammar; }
    public void setGrammar(GrammarPrompt grammar) { this.grammar = grammar; }
    
    public Map<String, String> getGuidelines() { return guidelines; }
    public void setGuidelines(Map<String, String> guidelines) { this.guidelines = guidelines; }
    
    public MessageConfig getMessages() { return messages; }
    public void setMessages(MessageConfig messages) { this.messages = messages; }
}