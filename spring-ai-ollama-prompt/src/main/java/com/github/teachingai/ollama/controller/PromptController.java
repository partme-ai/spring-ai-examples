package com.github.partmeai.ollama.controller;

import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class PromptController {

    private final OllamaChatModel chatModel;

    @Autowired
    public PromptController(OllamaChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @GetMapping("/v1/prompt")
    public List<Generation> prompt(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        PromptTemplate promptTemplate = new PromptTemplate("Tell me a {adjective} joke about {topic}");
        Prompt prompt = promptTemplate.create(Map.of("adjective", "funny", "topic", "cats"));
        return chatModel.call(prompt).getResults();
    }



}
