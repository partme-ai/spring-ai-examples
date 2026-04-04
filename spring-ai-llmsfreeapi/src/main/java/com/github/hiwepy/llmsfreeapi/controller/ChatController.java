package com.github.hiwepy.llmsfreeapi.controller;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

/**
 * LLMs Free API 聚合示例（Spring AI {@link ChatModel}）。
 */
@RestController
public class ChatController {

    private final String systemPromptTemplate;
    private final ChatModel chatModel;

    @Autowired
    public ChatController(@Value("classpath:/prompt.st") Resource sqlPromptTemplateResource,
                          ChatModel chatModel) {
        try (InputStream inputStream = sqlPromptTemplateResource.getInputStream()) {
            this.systemPromptTemplate = StreamUtils.copyToString(inputStream, Charset.defaultCharset());
        }
        catch (IOException ex) {
            throw new RuntimeException("Failed to read resource", ex);
        }
        this.chatModel = chatModel;
    }

    @GetMapping("/v1/generate")
    public Map<String, Object> generate(
            @RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        return Map.of("generation", chatModel.call(message));
    }

    @GetMapping("/v1/prompt")
    public List<Generation> prompt() {
        Message systemMessage = new SystemMessage(systemPromptTemplate);
        Prompt prompt = new Prompt(List.of(systemMessage));
        return chatModel.call(prompt).getResults();
    }

    @PostMapping("/v1/chat/completions")
    public Flux<ChatResponse> chatCompletions(
            @RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        Prompt prompt = new Prompt(new UserMessage(message));
        return chatModel.stream(prompt);
    }
}
