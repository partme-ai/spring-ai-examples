package com.github.teachingai.ollama.controller;

import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;


/**
 * 评价助手控制器
 */
@RequestMapping("evaluation")
@RestController
public class EvaluationAssistantController {

    // 系统提示消息
    private final Resource wordsOfWishByTeacherResource = new ClassPathResource("prompts/evaluation/words_of_wish_by_teacher.st");
    private final Resource wordsOfWishByFamilyResource = new ClassPathResource("prompts/evaluation/words_of_wish_by_family.st");
    private final Resource evaluationSystemBySchoolResource = new ClassPathResource("prompts/evaluation/evaluation_system_by_school.st");
    
    private final OllamaChatModel chatModel;

    @Autowired
    public EvaluationAssistantController(OllamaChatModel chatModel) {
        this.chatModel = chatModel;
    }

    /**
     * 教师寄语生成
     * @param message 用户提示词
     * @return 寄语内容
     */
    @GetMapping("getWordsOfWishByTeacher")
    public List<Generation> getWordsOfWishByTeacher(@RequestParam(value = "message") String message) {
        try {

            // 1. 系统提示词
            String systemPrompt = wordsOfWishByTeacherResource.getContentAsString(StandardCharsets.UTF_8);
            SystemMessage systemMessage = new SystemMessage(systemPrompt);

            // 2. 用户提示词
            UserMessage userMessage = new UserMessage(message);

            List<Message> messages  = List.of(systemMessage, userMessage);

            // 3. 提示词对象
            Prompt prompt = new Prompt(messages, OllamaChatOptions.builder()
                    .model("qwen3:8b")
                    .temperature(0d).build());

            ChatResponse resp = chatModel.call(prompt);
            return resp.getResults();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * 家长寄语生成
     * @param message 用户提示词
     * @return 寄语内容
     */
    @GetMapping("/getWordsOfWishByFamily")
    public List<Generation> getWordsOfWishByFamily(@RequestParam(value = "message") String message) {
        try {

            // 1. 系统提示词
            String systemPrompt = wordsOfWishByFamilyResource.getContentAsString(StandardCharsets.UTF_8);
            SystemMessage systemMessage = new SystemMessage(systemPrompt);

            // 2. 用户提示词
            UserMessage userMessage = new UserMessage(message);

            List<Message> messages  = List.of(systemMessage, userMessage);

            // 3. 提示词对象
            Prompt prompt = new Prompt(messages, OllamaChatOptions.builder()
                    .model("qwen3:8b")
                    .temperature(0d).build());

            ChatResponse resp = chatModel.call(prompt);
            return resp.getResults();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 评价体系生成
     * @param message 用户提示词
     * @return 评价体系
     */
    @GetMapping("/getEvaluationSystemBySchool")
    public List<Generation> getEvaluationSystemBySchool(@RequestParam(value = "message") String message) {
        try {

            // 1. 系统提示词
            String systemPrompt = evaluationSystemBySchoolResource.getContentAsString(StandardCharsets.UTF_8);
            SystemMessage systemMessage = new SystemMessage(systemPrompt);

            // 2. 用户提示词
            UserMessage userMessage = new UserMessage(message);

            List<Message> messages  = List.of(systemMessage, userMessage);

            // 3. 提示词对象
            Prompt prompt = new Prompt(messages, OllamaChatOptions.builder()
                    .model("qwen3:8b")
                    .temperature(0d).build());

            ChatResponse resp = chatModel.call(prompt);
            return resp.getResults();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
