// LlmUiState.java
package com.example.chatpet;

import java.util.Objects;

/**
 * Sealed class pattern in Java for UI states
 * Base class is abstract to prevent direct instantiation
 */
public abstract class LlmUiState {
    // Private constructor to prevent external subclassing (sealed class behavior)
    private LlmUiState() {}
    
    /**
     * Idle state - ready to accept input
     */
    public static final class Idle extends LlmUiState {
        public static final Idle INSTANCE = new Idle();
        
        private Idle() {}
        
        @Override
        public String toString() {
            return "LlmUiState.Idle";
        }
    }
    
    /**
     * Loading state - processing request
     */
    public static final class Loading extends LlmUiState {
        public static final Loading INSTANCE = new Loading();
        
        private Loading() {}
        
        @Override
        public String toString() {
            return "LlmUiState.Loading";
        }
    }
    
    /**
     * Success state - contains result text
     */
    public static final class Success extends LlmUiState {
        private final String resultText;
        
        public Success(String resultText) {
            this.resultText = resultText;
        }
        
        public String getResultText() {
            return resultText;
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Success success = (Success) o;
            return Objects.equals(resultText, success.resultText);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(resultText);
        }
        
        @Override
        public String toString() {
            return "LlmUiState.Success(resultText=" + resultText + ")";
        }
    }
    
    /**
     * Error state - contains error message
     */
    public static final class Error extends LlmUiState {
        private final String errorMessage;
        
        public Error(String errorMessage) {
            this.errorMessage = errorMessage;
        }
        
        public String getErrorMessage() {
            return errorMessage;
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Error error = (Error) o;
            return Objects.equals(errorMessage, error.errorMessage);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(errorMessage);
        }
        
        @Override
        public String toString() {
            return "LlmUiState.Error(errorMessage=" + errorMessage + ")";
        }
    }
}

