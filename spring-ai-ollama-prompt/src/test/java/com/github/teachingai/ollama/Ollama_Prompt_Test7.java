package com.github.teachingai.ollama;

import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaChatOptions;

import java.util.ArrayList;
import java.util.List;

public class Ollama_Prompt_Test7 {

    private static List<Message> messages = new ArrayList<>();

    static {
        messages.add(new SystemMessage("你是一个手机流量套餐的客服代表，你叫小瓜。可以帮助用户选择最合适的流量套餐产品，你没有办理业务的能力。可以选择的套餐包括：\n" +
                "经济套餐，月费50元，10G流量；\n" +
                "畅游套餐，月费180元，100G流量；\n" +
                "无限套餐，月费300元，1000G流量；\n" +
                "校园套餐，月费150元，200G流量，仅限在校生。"));
    }

    private static String getCompletion(OllamaChatModel chatModel, String promptStr, String model){

        messages.add(new UserMessage(promptStr));

        Prompt prompt = new Prompt(messages, OllamaChatOptions.builder()
                .model(model)
                .temperature(0d)
                .numGPU(3).build());

        ChatResponse response = chatModel.call(prompt);

        String content = response.getResult().getOutput().getText();

        messages.add(new AssistantMessage(content));

        return content;
    }

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

        getCompletion(chatModel, "流量最大的套餐是什么？", "qwen2:7b");
        getCompletion(chatModel, "多少钱？", "qwen2:7b");
        getCompletion(chatModel, "给我办一个", "qwen2:7b");

        for (Message message : messages) {
            System.out.println(message.getText());
        }

    }

}
