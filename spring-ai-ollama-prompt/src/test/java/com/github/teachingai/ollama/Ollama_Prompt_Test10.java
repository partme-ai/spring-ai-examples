package com.github.teachingai.ollama;

import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaChatOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Ollama_Prompt_Test10 {

    private static List<Message> messages = new ArrayList<>();

    private static String instruction = "给定一段用户与手机流量套餐客服的对话，。\n" +
            "你的任务是判断客服介绍产品信息的准确性：\n" +
            "\n" +
            "当向用户介绍流量套餐产品时，客服人员必须准确提及产品名称、月费价格和月流量总量。上述信息缺失一项或多项，或信息与实时不符，都算信息不准确\n" +
            "\n" +
            "已知产品包括：\n" +
            "\n" +
            "经济套餐：月费50元，月流量10G\n" +
            "畅游套餐：月费180元，月流量100G\n" +
            "无限套餐：月费300元，月流量1000G\n" +
            "校园套餐：月费150元，月流量200G，限在校学生办理\n";

/*
    static {
        messages.add(new SystemMessage("你是一个手机流量套餐的客服代表，你叫小瓜。可以帮助用户选择最合适的流量套餐产品，你没有办理业务的能力。可以选择的套餐包括：\n" +
                "经济套餐，月费50元，10G流量；\n" +
                "畅游套餐，月费180元，100G流量；\n" +
                "无限套餐，月费300元，1000G流量；\n" +
                "校园套餐，月费150元，200G流量，仅限在校生。"));
    }*/

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

        String output_format = "如果信息准确，输出：Y\n" +
                "如果信息不准确，输出：N\n";

        String context = "用户：你们有什么流量大的套餐\n" +
                "客服：您好，我们现在正在推广无限套餐，每月300元就可以享受1000G流量，您感兴趣吗？\n";

        String cot = "请一步一步分析以下对话后，输出Y或N\n";

        PromptTemplate promptTemplate = new PromptTemplate("{instruction} \n\n {output_format} \n\n {cot} \n\n 对话记录：{context}");
        // Prompt prompt = promptTemplate.create(Map.of("instruction", instruction, "output_format", output_format, "cot", cot, "context", context));
        String promptStr = promptTemplate.render(Map.of("instruction", instruction, "output_format", output_format, "cot", cot, "context", context));

        // 连续调用 5 次
        for (int i = 0; i < 5; i++) {
            System.out.println("------第" + (i + 1) + "次------");
            getCompletion(chatModel, promptStr, "qwen3:8b");
        }

        for (Message message : messages) {
            System.out.println(message.getText());
        }

    }

}
