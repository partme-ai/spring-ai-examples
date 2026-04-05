package com.github.partmeai.ollama.router;

import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;
import reactor.core.publisher.Flux;

@Configuration
public class RouterFunctionConfig {

    @Bean
    RouterFunction<ServerResponse> routes(OllamaChatModel chatModel) {
        return RouterFunctions.route()

                .GET("/reactive/generate", req -> {
                    String message = req.param("message").orElse("tell me a joke");
                    return ServerResponse.ok().body(chatModel.call(message));
                })
                .GET("/reactive/stream", req -> {
                    Prompt prompt = new Prompt(new UserMessage(req.param("message").orElse("Tell me a joke")));
                    Flux<ChatResponse> streamFlux = Flux.defer(() -> chatModel.stream(prompt));
                    // 构建响应，设置 Content-Type 为 text/event-stream 以支持 SSE
                    return ServerResponse.ok()
                            .contentType(MediaType.TEXT_EVENT_STREAM) // 关键：设置为 text/event-stream
                            .body(streamFlux);
                })
                .build();
    }

}
