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
 * Exception thrown when translation service operations fail.
 * This exception indicates issues with the AI service (Ollama) or processing errors.
 */
public class TranslationServiceException extends RuntimeException {
    
    private final String errorCode;
    private final boolean retryable;
    
    /**
     * Constructs a new service exception with the specified message.
     *
     * @param message the detail message
     */
    public TranslationServiceException(String message) {
        super(message);
        this.errorCode = "SERVICE_ERROR";
        this.retryable = true;
    }
    
    /**
     * Constructs a new service exception with the specified message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public TranslationServiceException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "SERVICE_ERROR";
        this.retryable = true;
    }
    
    /**
     * Constructs a new service exception with the specified message, error code, and retryable flag.
     *
     * @param message the detail message
     * @param errorCode the error code
     * @param retryable whether the operation can be retried
     */
    public TranslationServiceException(String message, String errorCode, boolean retryable) {
        super(message);
        this.errorCode = errorCode;
        this.retryable = retryable;
    }
    
    /**
     * Constructs a new service exception with the specified message, cause, error code, and retryable flag.
     *
     * @param message the detail message
     * @param cause the cause
     * @param errorCode the error code
     * @param retryable whether the operation can be retried
     */
    public TranslationServiceException(String message, Throwable cause, String errorCode, boolean retryable) {
        super(message, cause);
        this.errorCode = errorCode;
        this.retryable = retryable;
    }
    
    /**
     * Gets the error code associated with this service exception.
     *
     * @return the error code
     */
    public String getErrorCode() {
        return errorCode;
    }
    
    /**
     * Indicates whether the failed operation can be retried.
     *
     * @return true if the operation is retryable, false otherwise
     */
    public boolean isRetryable() {
        return retryable;
    }
}
