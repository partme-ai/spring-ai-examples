package com.github.teachingai.ollama.router;

import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.Duration;

@Configuration
public class RouterFunctionConfig {

    @Bean
    RouterFunction<ServerResponse> routes(OllamaChatModel chatModel) {
        return RouterFunctions.route()
                .GET("/generate", req ->
                        ServerResponse.ok().body(
                                chatModel.call(req.param("message")
                                        .orElse("tell me a joke"))))
                .GET("/prompt", req -> {
                    PromptTemplate promptTemplate = new PromptTemplate("Tell me a {adjective} joke about {topic}");
                    Prompt prompt = new Prompt(new UserMessage(req.param("prompt").orElse("Tell me a joke")));
                    return ServerResponse.ok().body(chatModel.call(prompt));
                })
                .GET("/chat", req -> {
                    Prompt prompt = new Prompt(new UserMessage(req.param("message").orElse("Tell me a joke")));
                    return ServerResponse.ok().body(chatModel.stream(prompt));
                })
                .GET("/reactive/generate", req -> {
                    String message = req.param("message").orElse("tell me a joke");
                    return ServerResponse.ok().body(
                            Mono.fromCallable(() -> chatModel.call(message))
                    );
                })
                .GET("/reactive/stream", req -> {
                    Prompt prompt = new Prompt(new UserMessage(req.param("message").orElse("Tell me a joke")));
                    return ServerResponse.ok().body(
                            chatModel.stream(prompt)
                                    .delayElements(Duration.ofMillis(100))
                    );
                })
                .build();
    }

}
