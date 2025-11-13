package org.feuyeux.ai.langgraph4j.colosseo.config;

import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaStreamingChatModel;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.streaming.StreamingChatLanguageModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class LangGraph4jConfig {
    
    @Value("${langgraph4j.ollama.base-url}")
    private String ollamaBaseUrl;
    
    @Value("${langgraph4j.ollama.model-name}")
    private String ollamaModelName;
    
    @Value("${langgraph4j.ollama.timeout}")
    private Duration ollamaTimeout;
    
    @Bean
    public ChatLanguageModel chatLanguageModel() {
        return OllamaChatModel.builder()
                .baseUrl(ollamaBaseUrl)
                .modelName(ollamaModelName)
                .timeout(ollamaTimeout)
                .build();
    }
    
    @Bean
    public StreamingChatLanguageModel streamingChatLanguageModel() {
        return OllamaStreamingChatModel.builder()
                .baseUrl(ollamaBaseUrl)
                .modelName(ollamaModelName)
                .timeout(ollamaTimeout)
                .build();
    }
}