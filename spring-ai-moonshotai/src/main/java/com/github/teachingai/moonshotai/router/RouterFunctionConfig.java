package com.github.teachingai.moonshotai.router;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

/**
 * 函数式路由示例：直接使用 {@link ChatModel}，避免单独引入 ChatClient 模块。
 */
@Configuration
public class RouterFunctionConfig {

    @Bean
    RouterFunction<ServerResponse> routes(ChatModel chatModel) {
        return RouterFunctions.route()
                .GET("/ask", req -> ServerResponse.ok()
                        .body(chatModel.call(req.param("question").orElse("tell me a joke"))))
                .build();
    }
}
