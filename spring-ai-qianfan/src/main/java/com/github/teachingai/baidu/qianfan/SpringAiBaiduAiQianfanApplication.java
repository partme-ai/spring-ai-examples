package com.github.teachingai.baidu.qianfan;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SpringAiBaiduAiQianfanApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringAiBaiduAiQianfanApplication.class, args);
    }

    /**
     * 供路由与演示使用的 {@link ChatClient}（基于千帆 {@link ChatModel} 实现）。
     */
    @Bean
    ChatClient chatClient(ChatModel chatModel) {
        return ChatClient.builder(chatModel).build();
    }
}
