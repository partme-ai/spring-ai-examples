package com.github.partmeai.ollama.api;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.function.Consumer;

public class ApiUtils {


    public static final Float DEFAULT_SPEED = 1F;
    public static final String DEFAULT_BASE_URL = "http://localhost:8000";

    public static Consumer<HttpHeaders> getJsonContentHeaders(String apiKey) {
        return (headers) -> {
            headers.setBearerAuth(apiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);
        };
    };

}
