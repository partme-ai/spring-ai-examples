package com.github.partmeai.aisql.exception;

public class SqlGenerationException extends RuntimeException {
    public SqlGenerationException(String response) {
        super(response);
    }
}
