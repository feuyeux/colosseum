package org.feuyeux.ai.langgraph4j.colosseo.graph;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import org.feuyeux.ai.langgraph4j.colosseo.model.TranslationRequest;
import org.feuyeux.ai.langgraph4j.colosseo.model.TranslationResult;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TranslationGraph {
    
    private final ChatLanguageModel chatLanguageModel;
    private final TranslationAgent translationAgent;
    
    public TranslationGraph(ChatLanguageModel chatLanguageModel) {
        this.chatLanguageModel = chatLanguageModel;
        this.translationAgent = AiServices.builder(TranslationAgent.class)
                .chatLanguageModel(chatLanguageModel)
                .build();
    }
    
    public TranslationResult translate(TranslationRequest request) {
        try {
            String translatedText = translationAgent.translate(
                request.getText(),
                request.getSourceLanguage(),
                request.getTargetLanguage(),
                request.getContext()
            );
            
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
    
    interface TranslationAgent {
        String translate(String text, String sourceLang, String targetLang, String context);
    }
}