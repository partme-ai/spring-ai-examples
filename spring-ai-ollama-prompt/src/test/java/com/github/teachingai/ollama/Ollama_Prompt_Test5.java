package com.github.partmeai.ollama;

import com.alibaba.fastjson2.JSONObject;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaChatOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Ollama_Prompt_Test5 {

    /*
     * deepseek-r1:8b ：https://ollama.com/library/deepseek-r1
     * qwen3:8b ：https://ollama.com/library/qwen8
     * gemma3:4b ：https://ollama.com/library/gemma3
     */
    private static OllamaApi ollamaApi = OllamaApi.builder().build();
    private static OllamaChatOptions ollamaOptions = OllamaChatOptions.builder()
            .model("qwen3:8b")
            .format("json")
            .temperature(0.9d).build();
    private static OllamaChatModel chatModel = OllamaChatModel.builder()
            .ollamaApi(ollamaApi)
            .defaultOptions(ollamaOptions).build();

    static class NLU {

        String prompt_template;
        List<Message> messages = new ArrayList<>();

        private NLU(String instruction, String output_format, String examples) {
            this.prompt_template = String.format("{instruction} \n\n {output_format} \n\n {examples} \n\n 用户输入：\n__INPUT__");
        }

        private String getCompletion(NLU self, String promptStr, String model){
            messages.add(new UserMessage(promptStr));
            Prompt prompt = new Prompt(messages, OllamaChatOptions.builder()
                    .model("qwen3:8b")
                    .format("json")
                    .temperature(0d).numGPU(3).build());
            ChatResponse response = chatModel.call(prompt);
           return response.getResults().get(0).getOutput().getText();
        }

        private String parse(NLU self, String user_input, String model){
            String promptStr = self.prompt_template.replace("__INPUT__", user_input);
            return self.getCompletion(self, promptStr, model);
        }

    }

    private class DST {

        private String update(JSONObject state, JSONObject nlu_semantics){

            if (nlu_semantics.containsKey("name")){
                state.clear();
            }
            if (nlu_semantics.containsKey("sort")){
               String slot = nlu_semantics.getJSONObject("sort").getString("value");
                if (state.containsKey(slot) && state.getJSONObject(slot).getString("operator").equals("==")){
                     state.remove(slot);
                }
            }
            for (Map.Entry<String, Object> entry : nlu_semantics.entrySet()) {
                state.put(entry.getKey(), entry.getValue());
            }
            return state.toJSONString();
        }

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

        // 系统提示消息
        SystemMessage systemMessage = new SystemMessage("你的任务是识别用户对手机流量套餐产品的选择条件。\n" +
                "每种流量套餐产品包含三个属性：名称(name)，月费价格(price)，月流量(data)。\n" +
                "根据对话上下文，识别用户在上述三种属性上的需求是什么。识别结果要包含整个对话的信息。\n" +
                "以JSON格式输出。\n" +
                "1. name字段的取值为string类型，取值必须为以下之一：经济套餐、畅游套餐、无限套餐、校园套餐 或 null；\n" +
                "\n" +
                "2. price字段的取值为一个结构体 或 null，包含两个字段：\n" +
                "(1) operator, string类型，取值范围：'<='（小于等于）, '>=' (大于等于), '=='（等于）\n" +
                "(2) value, int类型\n" +
                "\n" +
                "3. data字段的取值为取值为一个结构体 或 null，包含两个字段：\n" +
                "(1) operator, string类型，取值范围：'<='（小于等于）, '>=' (大于等于), '=='（等于）\n" +
                "(2) value, int类型或string类型，string类型只能是'无上限'\n" +
                "\n" +
                "4. 用户的意图可以包含按price或data排序，以sort字段标识，取值为一个结构体：\n" +
                "(1) 结构体中以\"ordering\"=\"descend\"表示按降序排序，以\"value\"字段存储待排序的字段\n" +
                "(2) 结构体中以\"ordering\"=\"ascend\"表示按升序排序，以\"value\"字段存储待排序的字段\n" +
                "\n" +
                "输出中只包含用户提及的字段，不要猜测任何用户未直接提及的字段。不要输出值为null的字段。\n" +
                "\n" +
                "客服：有什么可以帮您\n" +
                "用户：100G套餐有什么\n" +
                "\n" +
                "{\"data\":{\"operator\":\">=\",\"value\":100}}\n" +
                "\n" +
                "客服：有什么可以帮您\n" +
                "用户：100G套餐有什么\n" +
                "客服：我们现在有无限套餐，不限流量，月费300元\n" +
                "用户：太贵了，有200元以内的不\n" +
                "\n" +
                "{\"data\":{\"operator\":\">=\",\"value\":100},\"price\":{\"operator\":\"<=\",\"value\":200}}\n" +
                "\n" +
                "客服：有什么可以帮您\n" +
                "用户：便宜的套餐有什么\n" +
                "客服：我们现在有经济套餐，每月50元，10G流量\n" +
                "用户：100G以上的有什么\n" +
                "\n" +
                "{\"data\":{\"operator\":\">=\",\"value\":100},\"sort\":{\"ordering\"=\"ascend\",\"value\"=\"price\"}}\n" +
                "\n" +
                "客服：有什么可以帮您\n" +
                "用户：100G以上的套餐有什么\n" +
                "客服：我们现在有畅游套餐，流量100G，月费180元\n" +
                "用户：流量最多的呢\n" +
                "\n" +
                "{\"sort\":{\"ordering\"=\"descend\",\"value\"=\"data\"},\"data\":{\"operator\":\">=\",\"value\":100}}" );

        String input_text = "哪个便宜";
                //input_text = "无限量哪个多少钱";
                //input_text = "流量最大的多少钱";

        UserMessage userMessage = new UserMessage(input_text);

        List<Message> messages  = List.of(systemMessage,
                new AssistantMessage("客服：有什么可以帮您"),
                new UserMessage("用户：有什么100G以上的套餐推荐"),
                new AssistantMessage("客服：我们有畅游套餐和无限套餐，您有什么价格倾向吗？"),
                new UserMessage("用户：" + input_text),
                userMessage);

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
