package org.feuyeux.ai.langgraph4j.colosseo.graph;

import dev.langchain4j.model.ollama.OllamaChatModel;
import org.feuyeux.ai.langgraph4j.colosseo.model.AnalysisRequest;
import org.feuyeux.ai.langgraph4j.colosseo.model.AnalysisResult;
import org.feuyeux.ai.langgraph4j.colosseo.model.GrammarComponent;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AnalysisGraph {
    
    private final OllamaChatModel chatLanguageModel;
    
    public AnalysisGraph(OllamaChatModel chatLanguageModel) {
        this.chatLanguageModel = chatLanguageModel;
    }
    
    public AnalysisResult analyze(AnalysisRequest request) {
        try {
            String prompt = String.format("Analyze the grammar of this %s text: %s", 
                request.getLanguage(), request.getText());
            String analysis = chatLanguageModel.chat(prompt);
            
            List<GrammarComponent> components = parseGrammarComponents(analysis, request.getText());
            
            return AnalysisResult.builder()
                    .text(request.getText())
                    .language(request.getLanguage())
                    .components(components)
                    .build();
        } catch (Exception e) {
            return new AnalysisResult("Analysis failed: " + e.getMessage());
        }
    }
    
    private List<GrammarComponent> parseGrammarComponents(String analysis, String originalText) {
        List<GrammarComponent> components = new ArrayList<>();
        
        // Parse the analysis result and create grammar components
        // This is a simplified parser - in a real implementation, you'd want more sophisticated parsing
        String[] lines = analysis.split("\n");
        for (String line : lines) {
            if (line.contains("noun")) {
                components.add(new GrammarComponent("noun", "noun", "Noun", "#FF6B6B", 0, 0));
            } else if (line.contains("verb")) {
                components.add(new GrammarComponent("verb", "verb", "Verb", "#4ECDC4", 0, 0));
            } else if (line.contains("adjective")) {
                components.add(new GrammarComponent("adjective", "adjective", "Adjective", "#45B7D1", 0, 0));
            } else if (line.contains("adverb")) {
                components.add(new GrammarComponent("adverb", "adverb", "Adverb", "#96CEB4", 0, 0));
            } else if (line.contains("preposition")) {
                components.add(new GrammarComponent("preposition", "preposition", "Preposition", "#FFEAA7", 0, 0));
            } else if (line.contains("conjunction")) {
                components.add(new GrammarComponent("conjunction", "conjunction", "Conjunction", "#DDA0DD", 0, 0));
            }
        }
        
        return components;
    }
}