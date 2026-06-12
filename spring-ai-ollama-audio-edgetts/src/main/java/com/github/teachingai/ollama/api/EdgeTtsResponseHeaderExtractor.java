package com.github.partmeai.ollama.api;

import org.springframework.ai.chat.metadata.EmptyRateLimit;
import org.springframework.ai.chat.metadata.RateLimit;
import org.springframework.http.ResponseEntity;

public class EdgeTtsResponseHeaderExtractor {

    public static RateLimit extractAiResponseHeaders(ResponseEntity<?> response) {
/*
        Long requestsLimit = getHeaderAsLong(response, REQUESTS_LIMIT_HEADER.getName());
        Long requestsRemaining = getHeaderAsLong(response, REQUESTS_REMAINING_HEADER.getName());
        Long tokensLimit = getHeaderAsLong(response, TOKENS_LIMIT_HEADER.getName());
        Long tokensRemaining = getHeaderAsLong(response, TOKENS_REMAINING_HEADER.getName());

        Duration requestsReset = getHeaderAsDuration(response, REQUESTS_RESET_HEADER.getName());
        Duration tokensReset = getHeaderAsDuration(response, TOKENS_RESET_HEADER.getName());*/

        return new EmptyRateLimit();
    }


}
