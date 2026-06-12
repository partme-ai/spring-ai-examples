package com.github.partmeai.deepseek.router;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

@Configuration
public class RouterFunctionConfig {

    @Bean
    public RouterFunction<ServerResponse> routes(ChatClient chatClient) {
        return RouterFunctions.route()
                .GET("/route/v1/generate", req -> ServerResponse.ok()
                        .body(chatClient
                                .prompt(req.param("question").orElse("tell me a joke"))
                                .call()
                                .content()))
                .GET("/route/v1/prompt", req -> ServerResponse.ok()
                        .body(chatClient
                                .prompt(req.param("question").orElse("tell me a joke"))
                                .call()
                                .content()))
                .GET("/route/v1/chat/completions", req -> ServerResponse.ok()
                        .body(chatClient
                                .prompt(req.param("question").orElse("tell me a joke"))
                                .call()
                                .content()))
                .build();
    }
}
