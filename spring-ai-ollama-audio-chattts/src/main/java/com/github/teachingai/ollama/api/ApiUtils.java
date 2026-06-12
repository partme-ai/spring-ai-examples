package com.github.partmeai.ollama.api;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.function.Consumer;

public class ApiUtils {

    public static final String DEFAULT_BASE_URL = "http://127.0.0.1:9966";

    public static final Integer DEFAULT_MAX_REFINE_TOKENS = 2048;

    public static final Integer DEFAULT_MAX_INFER_TOKENS = 384;

    public static final Float DEFAULT_TEXT_SEED = 42F;

    public static final Float DEFAULT_TEMPERATURE = 0.7F;

    public static final Float DEFAULT_TOP_P = 0.7F;

    public static final Integer DEFAULT_TOP_K = 20;

    public static final Integer DEFAULT_SPEED = 1;

    public static final Integer DEFAULT_CUSTOM_VOICE = 0;

    public static Consumer<HttpHeaders> getJsonContentHeaders(String apiKey) {
        return (headers) -> {
            headers.setBearerAuth(apiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);
        };
    };

    public static MultiValueMap toMultiValueMap(ChatTtsAudioApi.SpeechRequest speechRequest) {

        MultiValueMap map = new LinkedMultiValueMap();
        map.add("text", speechRequest.text());
        return map;
    }

}
