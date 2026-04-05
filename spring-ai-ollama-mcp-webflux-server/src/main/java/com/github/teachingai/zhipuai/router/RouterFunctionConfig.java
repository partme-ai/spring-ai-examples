package com.github.partmeai.zhipuai.router;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class RouterFunctionConfig {

    @Bean
    public RouterFunction<ServerResponse> routes(ChatClient chatClient) {
        return RouterFunctions.route()
                .GET("/route/v1/generate", req -> {
                    String q = req.queryParam("question").orElse("tell me a joke");
                    return ServerResponse.ok().bodyValue(chatClient.prompt(q).call().content());
                })
                .GET("/route/v1/prompt", req -> {
                    String q = req.queryParam("question").orElse("tell me a joke");
                    return ServerResponse.ok().bodyValue(chatClient.prompt(q).call().content());
                })
                .GET("/route/v1/chat/completions", req -> {
                    String q = req.queryParam("question").orElse("tell me a joke");
                    return ServerResponse.ok().bodyValue(chatClient.prompt(q).call().content());
                })
                .build();
    }
}
