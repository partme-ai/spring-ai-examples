package com.github.hiwepy.huggingface;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

@SpringBootApplication
public class SpringAiHuggingfaceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringAiHuggingfaceApplication.class, args);
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
