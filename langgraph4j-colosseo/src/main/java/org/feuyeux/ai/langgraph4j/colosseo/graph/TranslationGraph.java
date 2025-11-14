package org.feuyeux.ai.langgraph4j.colosseo.graph;

import dev.langchain4j.model.ollama.OllamaChatModel;
import org.feuyeux.ai.langgraph4j.colosseo.model.TranslationRequest;
import org.feuyeux.ai.langgraph4j.colosseo.model.TranslationResult;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TranslationGraph {
    
    private final OllamaChatModel chatLanguageModel;
    
    public TranslationGraph(OllamaChatModel chatLanguageModel) {
        this.chatLanguageModel = chatLanguageModel;
    }
    
    public TranslationResult translate(TranslationRequest request) {
        try {
            String prompt = String.format("Translate from %s to %s: %s. Context: %s", 
                request.getSourceLanguage(), request.getTargetLanguage(), 
                request.getText(), request.getContext());
            String translatedText = chatLanguageModel.chat(prompt);
            
            return TranslationResult.builder()
                    .translatedText(translatedText)
                    .sourceLanguage(request.getSourceLanguage())
                    .targetLanguage(request.getTargetLanguage())
                    .confidence(0.95)
                    .alternatives(Map.of())
                    .build();
        } catch (Exception e) {
            return new TranslationResult("Translation failed: " + e.getMessage());
        }
    }
}