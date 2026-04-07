package com.github.teachingai.ollama.controller;

import com.github.teachingai.ollama.request.ApiRequest;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class ChatController {

    private final OllamaChatModel chatModel;

    @Autowired
    public ChatController(OllamaChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @GetMapping("/v1/generate")
    public Map generate(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        return Map.of("generation", chatModel.call(message));
    }

    @GetMapping("/v1/prompt")
    public List<Generation> prompt(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        PromptTemplate promptTemplate = new PromptTemplate("Tell me a {adjective} joke about {topic}");
        Prompt prompt = promptTemplate.create(Map.of("adjective", "funny", "topic", "cats"));
        return chatModel.call(prompt).getResults();
    }

    @PostMapping("/v1/chat/completions")
    public Flux<ChatResponse> chatCompletions(@RequestBody ApiRequest.ChatCompletionRequest chatRequest) {

        chatRequest.messages().forEach(System.out::println);


        var optBuilder = OllamaChatOptions.builder().model(chatRequest.model());
        if (chatRequest.temperature() != null) {
            optBuilder.temperature(chatRequest.temperature().doubleValue());
        }
        if (chatRequest.topP() != null) {
            optBuilder.topP(chatRequest.topP().doubleValue());
        }
        ChatOptions modelOptions = optBuilder.build();

        List<Message> messages = chatRequest.messages().stream().map(msg -> {
            switch (msg.role()) {
                case ASSISTANT:
                    return new AssistantMessage(msg.content());
                case SYSTEM:
                    return new SystemMessage(msg.content());
                default:
                    return new UserMessage(msg.content());
            }
        }).collect(Collectors.toList());

        Prompt prompt = new Prompt(messages, modelOptions);
        return chatModel.stream(prompt);
    }

}
