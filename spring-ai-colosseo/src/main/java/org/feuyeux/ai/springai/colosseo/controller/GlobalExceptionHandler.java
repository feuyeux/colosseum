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
package org.feuyeux.ai.springai.colosseo.controller;

import org.feuyeux.ai.springai.colosseo.exception.TranslationServiceException;
import org.feuyeux.ai.springai.colosseo.exception.TranslationValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;

/**
 * Global exception handler for the application.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(TranslationValidationException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(TranslationValidationException e) {
        logger.warn("Validation error: {}", e.getMessage());
        
        Map<String, Object> errorResponse = Map.of(
            "error", "Validation Error",
            "message", e.getMessage(),
            "errorCode", e.getErrorCode(),
            "timestamp", Instant.now()
        );
        
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(TranslationServiceException.class)
    public ResponseEntity<Map<String, Object>> handleServiceException(TranslationServiceException e) {
        logger.error("Service error: {}", e.getMessage(), e);
        
        HttpStatus status = e.isRetryable() ? HttpStatus.SERVICE_UNAVAILABLE : HttpStatus.INTERNAL_SERVER_ERROR;
        
        Map<String, Object> errorResponse = Map.of(
            "error", "Service Error",
            "message", e.getMessage(),
            "errorCode", e.getErrorCode(),
            "retryable", e.isRetryable(),
            "timestamp", Instant.now()
        );
        
        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception e) {
        logger.error("Unexpected error: {}", e.getMessage(), e);
        
        Map<String, Object> errorResponse = Map.of(
            "error", "Internal Server Error",
            "message", "An unexpected error occurred. Please try again later.",
            "timestamp", Instant.now()
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}