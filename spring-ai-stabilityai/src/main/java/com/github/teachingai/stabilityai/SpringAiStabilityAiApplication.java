package com.github.teachingai.stabilityai;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

@SpringBootApplication
public class SpringAiStabilityAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringAiStabilityAiApplication.class, args);
    }

    /**
     * 函数式路由示例：直接使用 {@link ChatModel}，避免单独引入 spring-ai-client 模块。
     */
    @Bean
    RouterFunction<ServerResponse> routes(ChatModel chatModel) {
        return RouterFunctions.route()
            .GET("/ask", req ->
                ServerResponse.ok().body(
                    chatModel.call(req.param("question")
                            .orElse("tell me a joke"))))
            .build();
    }
}
