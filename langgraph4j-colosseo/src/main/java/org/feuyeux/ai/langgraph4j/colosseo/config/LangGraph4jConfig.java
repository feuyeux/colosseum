package org.feuyeux.ai.langgraph4j.colosseo.config;

import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaStreamingChatModel;
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
    public OllamaChatModel chatLanguageModel() {
        return OllamaChatModel.builder()
                .baseUrl(ollamaBaseUrl)
                .modelName(ollamaModelName)
                .timeout(ollamaTimeout)
                .build();
    }
    
    @Bean
    public OllamaStreamingChatModel streamingChatLanguageModel() {
        return OllamaStreamingChatModel.builder()
                .baseUrl(ollamaBaseUrl)
                .modelName(ollamaModelName)
                .timeout(ollamaTimeout)
                .build();
    }
}