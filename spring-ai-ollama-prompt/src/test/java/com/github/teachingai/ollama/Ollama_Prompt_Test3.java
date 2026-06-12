package com.github.partmeai.ollama;

import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaChatOptions;

import java.util.List;

public class Ollama_Prompt_Test3 {

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

        // 系统提示消息
        SystemMessage systemMessage = new SystemMessage("你的任务是识别用户对手机流量套餐产品的选择条件。\n" +
                        "每种流量套餐产品包含三个属性：名称(name)，月费价格(price)，月流量(data)。\n" +
                        "根据用户输入，识别用户在上述三种属性上的需求是什么。\n" +
                        "以JSON格式输出。\n" +
                        "1. name字段的取值为string类型，取值必须为以下之一：经济套餐、畅游套餐、无限套餐、校园套餐 或 null；\n" +
                        "2. price字段的取值为一个结构体 或 null，包含两个字段：\n" +
                        "(1) operator, string类型，取值范围：'<='（小于等于）, '>=' (大于等于), '=='（等于）\n" +
                        "(2) value, int类型\n" +
                        "3. data字段的取值为取值为一个结构体 或 null，包含两个字段：\n" +
                        "(1) operator, string类型，取值范围：'<='（小于等于）, '>=' (大于等于), '=='（等于）\n" +
                        "(2) value, int类型或string类型，string类型只能是'无上限'\n" +
                        "4. 用户的意图可以包含按price或data排序，以sort字段标识，取值为一个结构体：\n" +
                        "(1) 结构体中以\"ordering\"=\"descend\"表示按降序排序，以\"value\"字段存储待排序的字段\n" +
                        "(2) 结构体中以\"ordering\"=\"ascend\"表示按升序排序，以\"value\"字段存储待排序的字段\n" +
                        "输出中只包含用户提及的字段，不要猜测任何用户未直接提及的字段，不输出值为null的字段。");

        String input_text = "办个100G以上的套餐";
               //input_text = "有没有便宜的套餐";
               //input_text = "有没有土豪套餐";

        UserMessage userMessage = new UserMessage(input_text);

        List<Message> messages  = List.of(systemMessage, userMessage);

        Prompt prompt = new Prompt(messages, OllamaChatOptions.builder()
                .model("qwen3:8b")
                .format("json")
                .temperature(0d).build());

        ChatResponse resp = chatModel.call(prompt);

        for (Generation generation : resp.getResults()) {
            System.out.println(generation.getOutput().getText());
        }

    }

}
