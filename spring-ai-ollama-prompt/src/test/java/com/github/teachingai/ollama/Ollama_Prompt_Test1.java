package com.github.teachingai.ollama;

import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaChatOptions;

import java.util.List;

public class Ollama_Prompt_Test1 {

    public static void main(String[] args) {

        /*
         * deepseek-r1:8b ：https://ollama.com/library/deepseek-r1
         * qwen3:8b ：https://ollama.com/library/qwen8
         * gemma3:4b ：https://ollama.com/library/gemma3
         */
        var ollamaApi = OllamaApi.builder().build();
        var ollamaOptions = OllamaChatOptions.builder()
                .model("qwen3:8b")
                .format("json")
                .temperature(0.9d).build();
        var chatModel = OllamaChatModel.builder()
                .ollamaApi(ollamaApi)
                .defaultOptions(ollamaOptions).build();

        List<Message> messages  = List.of(
                new SystemMessage("你的任务是识别用户对手机流量套餐产品的选择条件。\n" +
                        "每种流量套餐产品包含三个属性：名称，月费价格，月流量。\n" +
                        "根据用户输入，识别用户在上述三种属性上的倾向。"),
                new UserMessage("办个100G的套餐。"));

        Prompt prompt = new Prompt(messages, OllamaChatOptions.builder()
                .model("qwen3:8b")
                .temperature(0d).build());

        ChatResponse resp = chatModel.call(prompt);

        for (Generation generation : resp.getResults()) {
            System.out.println(generation.getOutput().getText());
        }

    }

}
