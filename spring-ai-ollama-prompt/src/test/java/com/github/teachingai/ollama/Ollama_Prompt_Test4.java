package com.github.teachingai.ollama;

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

public class Ollama_Prompt_Test4 {

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
                        "输出中只包含用户提及的字段，不要猜测任何用户未直接提及的字段，不输出值为null的字段。" +
                        "例如：\n" +
                        "便宜的套餐：{\"sort\":{\"ordering\"=\"ascend\",\"value\"=\"price\"}}\n" +
                        "有没有不限流量的：{\"data\":{\"operator\":\"==\",\"value\":\"无上限\"}}\n" +
                        "流量大的：{\"sort\":{\"ordering\"=\"descend\",\"value\"=\"data\"}}\n" +
                        "100G以上流量的套餐最便宜的是哪个：{\"sort\":{\"ordering\"=\"ascend\",\"value\"=\"price\"},\"data\":{\"operator\":\">=\",\"value\":100}}\n" +
                        "月费不超过200的：{\"price\":{\"operator\":\"<=\",\"value\":200}}\n" +
                        "就要月费180那个套餐：{\"price\":{\"operator\":\"==\",\"value\":180}}\n" +
                        "经济套餐：{\"name\":\"经济套餐\"}\n" +
                        "土豪套餐：{\"name\":\"无限套餐\"}");

        String input_text = "有没有土豪套餐";
                //input_text = "办个200G的套餐";
                //input_text = "有没有流量大的套餐";
                //input_text = "200元以下，流量大的套餐有啥";
                input_text = "你说那个10G的套餐，叫啥名字";

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
