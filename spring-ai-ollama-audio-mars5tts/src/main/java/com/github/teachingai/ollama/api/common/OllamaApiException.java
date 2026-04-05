package com.github.partmeai.ollama.api.common;

/**
 * Non HTTP Error related exceptions
 */
public class OllamaApiException extends RuntimeException {

    public OllamaApiException(String message) {
        super(message);
    }

    public OllamaApiException(String message, Throwable cause) {
        super(message, cause);
    }

}
