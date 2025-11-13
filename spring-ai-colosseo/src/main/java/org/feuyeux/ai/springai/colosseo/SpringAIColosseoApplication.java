/*
 * Copyright 2024-2025 Spring AI Colosseo
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
package org.feuyeux.ai.springai.colosseo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for Spring AI Colosseo.
 * Provides multilingual translation and grammar analysis using Spring AI and Ollama.
 */
@SpringBootApplication
public class SpringAIColosseoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringAIColosseoApplication.class, args);
    }
}