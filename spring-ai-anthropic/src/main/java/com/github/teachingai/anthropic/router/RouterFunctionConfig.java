package com.github.partmeai.anthropic.router;

import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

@Configuration
public class RouterFunctionConfig {

    @Bean
    public RouterFunction<ServerResponse> routes(AnthropicChatModel chatModel) {
        return RouterFunctions.route()
                .GET("/route/v1/generate", req ->
                        ServerResponse.ok().body(
                                chatModel.call(req.param("question")
                                        .orElse("tell me a joke"))))
                .GET("/route/v1/prompt", req ->
                        ServerResponse.ok().body(
                                chatModel.call(req.param("question")
                                        .orElse("tell me a joke"))))
                .GET("/route/v1/chat/completions", req ->
                        ServerResponse.ok().body(
                                chatModel.call(req.param("question")
                                        .orElse("tell me a joke"))))
                .build();
    }

}
