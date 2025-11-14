package org.feuyeux.ai.springai.colosseo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HealthController {
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "healthy"));
    }

    @PostMapping("/echo")
    public ResponseEntity<Map<String, String>> echo(@RequestBody Map<String, String> payload) {
        String text = payload.getOrDefault("text", "");
        return ResponseEntity.ok(Map.of("text", text));
    }
}
