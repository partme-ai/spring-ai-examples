package com.github.partmeai.ollama;

import org.junit.jupiter.api.Test;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * 冒烟：验证 Ollama 工具调用场景下 {@link OllamaChatModel} 可被 Spring 容器装配。
 */
@SpringBootTest
class OllamaFunctionCallingTest1 {

    @Autowired
    private OllamaChatModel chatModel;

    @Test
    void contextLoadsOllamaChatModel() {
        assertNotNull(chatModel);
    }
}
