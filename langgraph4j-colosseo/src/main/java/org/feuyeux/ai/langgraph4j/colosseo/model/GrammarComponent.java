package org.feuyeux.ai.langgraph4j.colosseo.model;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GrammarComponent {
    private String component;
    private String type;
    private String explanation;
    private String color;
    private int position;
    private int length;
}