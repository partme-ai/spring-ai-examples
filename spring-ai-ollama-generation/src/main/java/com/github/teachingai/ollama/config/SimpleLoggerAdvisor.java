package com.github.partmeai.ollama.config;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientMessageAggregator;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import reactor.core.publisher.Flux;

@Slf4j
public class SimpleLoggerAdvisor implements CallAdvisor, StreamAdvisor {

    @NotNull
    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public int getOrder() {
        return 0;
    }


    @Override
    public @NotNull ChatClientResponse adviseCall(ChatClientRequest request, CallAdvisorChain callAdvisorChain) {
        logRequest(request);

        ChatClientResponse chatClientResponse = callAdvisorChain.nextCall(request);

        logResponse(chatClientResponse);

        return chatClientResponse;
    }

    @NotNull
    @Override
    public Flux<ChatClientResponse> adviseStream(ChatClientRequest chatClientRequest,
                                                 StreamAdvisorChain streamAdvisorChain) {
        logRequest(chatClientRequest);

        Flux<ChatClientResponse> chatClientResponses = streamAdvisorChain.nextStream(chatClientRequest);

        return new ChatClientMessageAggregator().aggregateChatClientResponse(chatClientResponses, this::logResponse);
    }

    private void logRequest(ChatClientRequest request) {
        log.debug("request: {}", request);
    }

    private void logResponse(ChatClientResponse chatClientResponse) {
        log.debug("response: {}", chatClientResponse);
    }

}