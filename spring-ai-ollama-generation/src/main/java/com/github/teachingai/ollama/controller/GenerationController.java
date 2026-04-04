package com.github.teachingai.ollama.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.Map;

/**
 * 文本生成：同步（GET/POST）与流式（GET）。基于 OllamaChatModel（Spring AI 1.1.x）。
 */
@RestController
public class GenerationController {

    private final OllamaChatModel chatModel;

    public GenerationController(OllamaChatModel chatModel) {
        this.chatModel = chatModel;
    }

    /**
     * JSON 请求体，便于与文档及前端 POST 调用对齐。
     *
     * @param body 用户输入文本
     */
    @PostMapping(value = "/ai/generate", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "文本生成（JSON）")
    public Map<String, Object> generatePost(@Valid @RequestBody GenerationBody body) {
        return doGenerate(body.message());
    }

    @GetMapping("/ai/generate")
    @Operation(summary = "文本生成（Query）")
    public Map<String, Object> generate(@RequestParam(value = "message", defaultValue = "你好！") String message) {
        return doGenerate(message);
    }

    private Map<String, Object> doGenerate(String message) {
        try {
            String response = chatModel.call(message);
            return Map.of(
                "success", true,
                "generation", response,
                "message", "Generated successfully"
            );
        } catch (Exception e) {
            return Map.of(
                "success", false,
                "error", e.getMessage(),
                "message", "Generation failed"
            );
        }
    }

    /**
     * POST /ai/generate 的请求体。
     *
     * @param message 提示内容
     */
    public record GenerationBody(@NotBlank String message) {
    }

    @GetMapping(value = "/ai/generateStream", produces = MediaType.TEXT_PLAIN_VALUE)
    public Flux<String> generateStream(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        return this.chatModel.stream(message);
    }

}
