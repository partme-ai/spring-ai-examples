package com.github.partmeai.ollama;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
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
import java.util.Map;

public class Ollama_Evaluation_Assistant_Test {

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
                //.model("gemma3:4b")
                .model("qwen3:8b")
                //.format("json")
                .temperature(0.85d).build();
        chatModel = OllamaChatModel.builder()
                .ollamaApi(ollamaApi)
                .defaultOptions(ollamaOptions).build();
    }

    @Test
    public void getWordsOfWishByTeacher() throws IOException {

        // 1、系统提示词
        Resource systemResource = new ClassPathResource("prompts/evaluation/words_of_wish_by_teacher_prompt.st");
        String systemPrompt =  systemResource.getContentAsString(StandardCharsets.UTF_8);

        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate("{prompt}");
        Message systemMessage = systemPromptTemplate.createMessage(Map.of("prompt", systemPrompt));

        // 2. 用户提示词
        String input_text = "小明本学期表现数据：{\n" +
                "  \"基础信息\": {\n" +
                "      \"学校代码\": \"2133000640\",\n" +
                "      \"学校名称\": \"中泰中心小学\",\n" +
                "      \"学校地址\": \"杭州市，余杭区\",\n" +
                "      \"学校性质\": \"小学\",\n" +
                "      \"年级名称\": \"一年级\",\n" +
                "      \"班级名称\": \"01班\",\n" +
                "      \"学生名称\": \"小明\",\n" +
                "  }，\n" +
                "  \"过程评价\": {\n" +
                "      \"学校排名\": \"-3\",\n" +
                "      \"年级排名\": \"-3\",\n" +
                "      \"班级排名\": \"-3\",\n" +
                "      \"获得荣誉\": [\"2023年第一学期三好学生\",\"书香小院士\",\"五竹少年\"],\n" +
                "      \"点评项得分\": {\n" +
                "         \"点评项1\": \"+15\",\n" +
                "         \"点评项2\": \"-3\",\n" +
                "      }\n" +
                "  }\n" +
                "  \"结果评价\": {\n" +
                "      \"品德表现\": {\n" +
                "         \"会合作\": \"A\",\n" +
                "         \"善交流\": \"A\",\n" +
                "         \"守规则\": \"A\",\n" +
                "         \"有爱心\": \"A\",\n" +
                "         \"负责任\": \"A\",\n" +
                "         \"讲文明\": \"A\",\n" +
                "         \"有礼貌\": \"A\",\n" +
                "     }，\n" +
                "     \"学业水平\": {\n" +
                "        \"道德与法治\": {\"认知水平\":\"A\",\"实践能力\":\"A\"},\n" +
                "        \"语文\": {\"识字与写字\":\"A\",\"阅读与鉴赏\":\"A\",\"表达与交流\":\"A\",\"梳理与探究\":\"A\"},\n" +
                "        \"数学\": {\"数与代数\":\"A\",\"空间与图形\":\"A\",\"统计与概率\":\"A\",\"综合与实践\":\"A\"},\n" +
                "        \"英语\": {\"视听理解\":\"A\",\"阅读理解\":\"A\",\"口头表达\":\"A\",\"书面表达\":\"A\",\"综合语言实践\":\"A\"},\n" +
                "        \"科学\": {\"科学观念\":\"A\",\"科学思维\":\"A\",\"探究实践\":\"A\"}\n" +
                "     },\n" +
                "     \"运动健康\": {\"体育与健康\":\"A\",\"体质健康测试\":\"A\",\"运动技能\":\"A\"},\n" +
                "     \"艺术素养\": {\"音乐\":\"A\",\"美术\":\"A\",\"艺术技能\":\"A\"},\n" +
                "     \"其他\": {\n" +
                "         \"信息科技\":\"A\",\n" +
                "         \"劳动\":\"A\",\n" +
                "         \"综合实践活动\":\"A\",\n" +
                "         \"地方课程\":\"A\",\n" +
                "         \"校本课程\":\"A\",\n" +
                "         \"跨学科研究性学习\":\"A\"\n" +
                "     }\n" +
                "  },\n" +
                "  \"非纸笔测评\": {\n" +
                "      \"品德表现\": \"A\",\n" +
                "  }\n" +
                "}";

        List<Message> messages  = List.of(systemMessage, new UserMessage(input_text),
                new UserMessage("请以老师的视角，帮我写一个给小明的教师寄语，150字以内！"));

        // 3. 提示词对象
        Prompt prompt = new Prompt(messages, OllamaChatOptions.builder()
                //.model("qwen3:8b")
                .topK(5)
                .temperature(0.85d).build());

        Flux<ChatResponse> flux = chatModel.stream(prompt);
        flux.doFinally(e -> {
            System.out.println();
        }).toStream().forEach(res -> {
            System.out.print(res.getResult().getOutput().getText());
        });

    }


    @Test
    public void getWordsOfWishByFamily() throws IOException {

        // 1、系统提示词
        Resource systemResource = new ClassPathResource("prompts/evaluation/words_of_wish_by_family_prompt.st");
        String systemPrompt =  systemResource.getContentAsString(StandardCharsets.UTF_8);

        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate("{prompt}");
        Message systemMessage = systemPromptTemplate.createMessage(Map.of("prompt", systemPrompt));

        // 2. 用户提示词
        String input_text = "小明本学期表现数据：{\n" +
                "  \"基础信息\": {\n" +
                "      \"学校代码\": \"2133000640\",\n" +
                "      \"学校名称\": \"中泰中心小学\",\n" +
                "      \"学校地址\": \"杭州市，余杭区\",\n" +
                "      \"学校性质\": \"小学\",\n" +
                "      \"年级名称\": \"一年级\",\n" +
                "      \"班级名称\": \"01班\",\n" +
                "      \"学生名称\": \"小明\",\n" +
                "  }，\n" +
                "  \"过程评价\": {\n" +
                "      \"学校排名\": \"-3\",\n" +
                "      \"年级排名\": \"-3\",\n" +
                "      \"班级排名\": \"-3\",\n" +
                "      \"获得荣誉\": [\"2023年第一学期三好学生\",\"书香小院士\",\"五竹少年\"],\n" +
                "      \"点评项得分\": {\n" +
                "         \"点评项1\": \"+15\",\n" +
                "         \"点评项2\": \"-3\",\n" +
                "      }\n" +
                "  }\n" +
                "  \"结果评价\": {\n" +
                "      \"品德表现\": {\n" +
                "         \"会合作\": \"A\",\n" +
                "         \"善交流\": \"A\",\n" +
                "         \"守规则\": \"A\",\n" +
                "         \"有爱心\": \"A\",\n" +
                "         \"负责任\": \"A\",\n" +
                "         \"讲文明\": \"A\",\n" +
                "         \"有礼貌\": \"A\",\n" +
                "     }，\n" +
                "     \"学业水平\": {\n" +
                "        \"道德与法治\": {\"认知水平\":\"A\",\"实践能力\":\"A\"},\n" +
                "        \"语文\": {\"识字与写字\":\"A\",\"阅读与鉴赏\":\"A\",\"表达与交流\":\"A\",\"梳理与探究\":\"A\"},\n" +
                "        \"数学\": {\"数与代数\":\"A\",\"空间与图形\":\"A\",\"统计与概率\":\"A\",\"综合与实践\":\"A\"},\n" +
                "        \"英语\": {\"视听理解\":\"A\",\"阅读理解\":\"A\",\"口头表达\":\"A\",\"书面表达\":\"A\",\"综合语言实践\":\"A\"},\n" +
                "        \"科学\": {\"科学观念\":\"A\",\"科学思维\":\"A\",\"探究实践\":\"A\"}\n" +
                "     },\n" +
                "     \"运动健康\": {\"体育与健康\":\"A\",\"体质健康测试\":\"A\",\"运动技能\":\"A\"},\n" +
                "     \"艺术素养\": {\"音乐\":\"A\",\"美术\":\"A\",\"艺术技能\":\"A\"},\n" +
                "     \"其他\": {\n" +
                "         \"信息科技\":\"A\",\n" +
                "         \"劳动\":\"A\",\n" +
                "         \"综合实践活动\":\"A\",\n" +
                "         \"地方课程\":\"A\",\n" +
                "         \"校本课程\":\"A\",\n" +
                "         \"跨学科研究性学习\":\"A\"\n" +
                "     }\n" +
                "  },\n" +
                "  \"非纸笔测评\": {\n" +
                "      \"品德表现\": \"A\",\n" +
                "  }\n" +
                "}";

        List<Message> messages  = List.of(systemMessage, new UserMessage(input_text),
                new UserMessage("请以爸爸的视角，请帮我写一个小明的家长寄语，150字以内！"));

        // 3. 提示词对象
        Prompt prompt = new Prompt(messages, OllamaChatOptions.builder()
                //.model("qwen3:8b")
                .topK(5)
                .temperature(0.85d).build());

        Flux<ChatResponse> flux = chatModel.stream(prompt);
        flux.doFinally(e -> {
            System.out.println();
        }).toStream().forEach(res -> {
            System.out.print(res.getResult().getOutput().getText());
        });

    }

}
