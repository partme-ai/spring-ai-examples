package com.github.partmeai.ollama;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

@SpringBootApplication
public class SpringAiOllamaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringAiOllamaApplication.class, args);
    }

    @Bean
    RouterFunction<ServerResponse> routes(ChatClient chatClient) {
        return RouterFunctions.route()
            .GET("/ask", req ->
                ServerResponse.ok().body(
                    chatClient.prompt(req.param("question")
                            .orElse("tell me a joke"))
                            .call()
                            .content()))
            .build();
    }

}
