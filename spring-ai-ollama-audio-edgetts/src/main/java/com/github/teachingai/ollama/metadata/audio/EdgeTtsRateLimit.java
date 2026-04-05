package com.github.partmeai.ollama.metadata.audio;

import org.springframework.ai.chat.metadata.RateLimit;

import java.time.Duration;

public class EdgeTtsRateLimit implements RateLimit {

    private static final String RATE_LIMIT_STRING = "{ @type: %1$s, requestsLimit: %2$s, requestsRemaining: %3$s, requestsReset: %4$s, tokensLimit: %5$s; tokensRemaining: %6$s; tokensReset: %7$s }";

    private final Long requestsLimit;

    private final Long requestsRemaining;

    private final Long tokensLimit;

    private final Long tokensRemaining;

    private final Duration requestsReset;

    private final Duration tokensReset;

    public EdgeTtsRateLimit(Long requestsLimit, Long requestsRemaining, Duration requestsReset, Long tokensLimit,
                           Long tokensRemaining, Duration tokensReset) {

        this.requestsLimit = requestsLimit;
        this.requestsRemaining = requestsRemaining;
        this.requestsReset = requestsReset;
        this.tokensLimit = tokensLimit;
        this.tokensRemaining = tokensRemaining;
        this.tokensReset = tokensReset;
    }

    @Override
    public Long getRequestsLimit() {
        return this.requestsLimit;
    }

    @Override
    public Long getTokensLimit() {
        return this.tokensLimit;
    }

    @Override
    public Long getRequestsRemaining() {
        return this.requestsRemaining;
    }

    @Override
    public Long getTokensRemaining() {
        return this.tokensRemaining;
    }

    @Override
    public Duration getRequestsReset() {
        return this.requestsReset;
    }

    @Override
    public Duration getTokensReset() {
        return this.tokensReset;
    }

    @Override
    public String toString() {
        return RATE_LIMIT_STRING.formatted(getClass().getName(), getRequestsLimit(), getRequestsRemaining(),
                getRequestsReset(), getTokensLimit(), getTokensRemaining(), getTokensReset());
    }
}
