package com.github.teachingai.stabilityai.controller;

import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Stability AI 文生图示例（Spring AI {@link ImageModel}）。
 */
@RestController
public class ChatController {

    private final ImageModel imageModel;

    @Autowired
    public ChatController(ImageModel imageModel) {
        this.imageModel = imageModel;
    }

    @GetMapping("/v1/generate")
    public ImageResponse generate(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        ImagePrompt imagePrompt = new ImagePrompt(message);
        return imageModel.call(imagePrompt);
    }
}
