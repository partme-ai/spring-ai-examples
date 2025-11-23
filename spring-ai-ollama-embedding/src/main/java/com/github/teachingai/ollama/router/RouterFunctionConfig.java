package com.github.teachingai.ollama.router;

import com.github.teachingai.ollama.service.IEmbeddingService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

@Component
@Configuration
public class RouterFunctionConfig {

    @Bean
    RouterFunction<ServerResponse> routes(IEmbeddingService embeddingService) {
        return RouterFunctions.route()
                .GET("/route/v1/embedding", req ->
                        ServerResponse.ok().body(
                                embeddingService.embedding(req.param("text").orElse("tell me a joke"))))
                .POST("/route/v1/embedding", req -> {

                    var file = req.multipartData().getFirst("file");
                    return ServerResponse.ok().body( embeddingService.embedding(file));

                })
                .build();
    }

}
