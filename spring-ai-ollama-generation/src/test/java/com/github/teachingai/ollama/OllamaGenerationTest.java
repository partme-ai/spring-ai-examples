package com.github.teachingai.ollama;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import reactor.core.publisher.Flux;

public class OllamaGenerationTest {

   private OllamaChatModel chatModel;

    @BeforeEach
    public void setUp() {
        /*
         * deepseek-r1:8b ：https://ollama.com/library/deepseek-r1
         * qwen3:8b ：https://ollama.com/library/qwen8
         * gemma3:4b ：https://ollama.com/library/gemma3
         */
        var ollamaApi = OllamaApi.builder().build();
        var ollamaOptions = OllamaChatOptions.builder()
                .model("gemma3:4b")
                .temperature(0.9d).build();
        chatModel = OllamaChatModel.builder()
                .ollamaApi(ollamaApi)
                .defaultOptions(ollamaOptions).build();
    }

    @Test
    public void testAsync() {
        Prompt prompt = new Prompt("给我讲个关于【龟兔赛跑】的故事！");
        Flux<ChatResponse> flux = chatModel.stream(prompt);
        flux.doFinally(e -> {
            System.out.println();
        }).toStream().forEach(res -> {
            System.out.print(res.getResult().getOutput().getText());
        });
        System.out.println("==============================================================================================================================");
    }

    @Test
    public void testSync() {
        String resp = chatModel.call("给我推荐一本儿童读物！");
        System.out.println(resp);
        System.out.println("==============================================================================================================================");
    }
}
