package com.github.partmeai.ollama.controller;

import com.github.partmeai.ollama.request.ApiRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
/**
 * Ollama 聊天控制器
 *
 * <p>提供基于 Ollama 模型的聊天接口，支持流式响应。</p>
 *
 * @author teachingai
 */
public class ChatController {

    /**
     * Ollama 聊天模型客户端
     */
    private final OllamaChatModel chatModel;

    @Autowired
    public ChatController(OllamaChatModel chatModel) {
        this.chatModel = chatModel;
    }

    /**
     * 聊天补全接口
     *
     * <p>接收用户的聊天请求，调用 Ollama 模型生成响应，并以流式方式返回。</p>
     *
     * @param chatRequest 聊天请求对象，包含消息列表和模型参数
     * @return {@link Flux<ChatResponse>} 流式聊天响应
     */
    @PostMapping("/v1/chat/completions")
    public Flux<ChatResponse> chatCompletions(@RequestBody ApiRequest.ChatCompletionRequest chatRequest) {
        // 打印请求消息
        chatRequest.messages().forEach(item -> log.info("{}", item));
        // 构建模型选项
        ChatOptions modelOptions = new OllamaChatOptions.Builder()
                .model(chatRequest.model())
                .temperature(chatRequest.temperature())
                .topP(chatRequest.topP())
                .build();
        // 构建消息列表
        List<Message> messages = chatRequest.messages().stream().map(msg -> switch (msg.role()) {
            case ASSISTANT -> new AssistantMessage(msg.content());
            case SYSTEM -> new SystemMessage(msg.content());
            default -> new UserMessage(msg.content());
        }).collect(Collectors.toList());

        Prompt prompt = new Prompt(messages, modelOptions);
        return chatModel.stream(prompt);
    }

}
