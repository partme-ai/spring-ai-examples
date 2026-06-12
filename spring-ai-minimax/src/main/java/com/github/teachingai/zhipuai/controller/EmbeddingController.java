package com.github.partmeai.zhipuai.controller;

import org.springframework.ai.minimax.MiniMaxEmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class EmbeddingController {

    private final MiniMaxEmbeddingModel embeddingModel;

    @Autowired
    public EmbeddingController(MiniMaxEmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    @GetMapping(value = "/v1/embedding")
    public Map<String, Object> embedding(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        return Map.of("embeddings", embeddingModel.embed(message));
    }

}
