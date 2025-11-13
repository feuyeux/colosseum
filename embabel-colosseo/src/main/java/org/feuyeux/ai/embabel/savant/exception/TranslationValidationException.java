/*
 * Copyright 2024-2025 Embabel Software, Inc.
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
package org.feuyeux.ai.embabel.savant.exception;

/**
 * Exception thrown when translation request validation fails.
 * This exception indicates user input errors that can be corrected.
 */
public class TranslationValidationException extends RuntimeException {
    
    private final String errorCode;
    
    /**
     * Constructs a new validation exception with the specified message.
     *
     * @param message the detail message
     */
    public TranslationValidationException(String message) {
        super(message);
        this.errorCode = "VALIDATION_ERROR";
    }
    
    /**
     * Constructs a new validation exception with the specified message and error code.
     *
     * @param message the detail message
     * @param errorCode the error code
     */
    public TranslationValidationException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
    
    /**
     * Gets the error code associated with this validation exception.
     *
     * @return the error code
     */
    public String getErrorCode() {
        return errorCode;
    }
}
