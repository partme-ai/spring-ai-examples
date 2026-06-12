package com.github.partmeai.ollama;

import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

/**
 * [哄哄模拟器](https://hong.greatdk.com/)基于 AI 技术，你需要使用语言技巧和沟通能力，在限定次数内让对方原谅你，这并不容易
 * 它的核心技术就是提示工程。著名提示工程师宝玉[复刻了它的提示词](https://weibo.com/1727858283/ND9pOzB0K)：
 */
public class Ollama_Hong_Prompt_Test {

    public static void main(String[] args) throws IOException {

        /*
         * deepseek-r1:8b ：https://ollama.com/library/deepseek-r1
         * qwen3:8b ：https://ollama.com/library/qwen8
         * gemma3:4b ：https://ollama.com/library/gemma3
         */
        var ollamaApi = OllamaApi.builder().build();
        var model = "gemma3:4b";
        var ollamaOptions = OllamaChatOptions.builder()
                //.model("qwen3:8b")
                .model(model)
                .temperature(0.9d).build();
        var chatModel = OllamaChatModel.builder()
                .ollamaApi(ollamaApi)
                .defaultOptions(ollamaOptions).build();

        List<Message> historyList = new ArrayList<>();
        // 系统提示消息
        Resource systemResource = new ClassPathResource("prompts/hong-prompt.st");
        String systemPrompt =  systemResource.getContentAsString(StandardCharsets.UTF_8);
        historyList.add(new SystemMessage(systemPrompt));
        historyList.add(new UserMessage("因为玩游戏，惹女朋友生气了"));
        // 生成对话
        Scanner scanner = new Scanner(System.in);
        while (true) {
            Prompt prompt = new Prompt(historyList, OllamaChatOptions.builder().build());
            Flux<ChatResponse> flux = chatModel.stream(prompt);
            System.out.print(">>> ");
            flux.doFinally(e -> {
               // System.out.println();
            }).toStream().forEach(res -> {
                historyList.add(res.getResult().getOutput());
                String resp = res.getResult().getOutput().getText();
                System.out.print(resp);
            });
            System.out.print("<<< ");
            String message = scanner.nextLine();
            if (message.equals("exit")) {
                break;
            }

        }
    }

}
