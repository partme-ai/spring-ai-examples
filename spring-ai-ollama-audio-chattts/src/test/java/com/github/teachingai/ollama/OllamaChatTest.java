package com.github.teachingai.ollama;

import com.github.teachingai.ollama.api.ApiUtils;
import com.github.teachingai.ollama.api.ChatTtsAudioApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaChatOptions;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

@Slf4j
public class OllamaChatTest {

    public static void main(String[] args) {

        /*
         * deepseek-r1:8b ：https://ollama.com/library/deepseek-r1
         * qwen3:8b ：https://ollama.com/library/qwen8
         * gemma3:4b ：https://ollama.com/library/gemma3
         */
        var ollamaApi = OllamaApi.builder().build();
        var ollamaOptions = OllamaChatOptions.builder()
                .model("gemma3:4b")
                .temperature(0.9d).build();
        var chatModel = OllamaChatModel.builder()
                .ollamaApi(ollamaApi)
                .defaultOptions(ollamaOptions).build();

        var chatTtsApi = new ChatTtsAudioApi();
        var chatTtsClient = new ChatTtsAudioSpeechModel(chatTtsApi, ChatTtsAudioSpeechOptions.builder()
                .withPrompt("[oral_2][laugh_0][break_6]")
                .temperature(ApiUtils.DEFAULT_TEMPERATURE)
                .topP(ApiUtils.DEFAULT_TOP_P)
                .topK(ApiUtils.DEFAULT_TOP_K)
                .withMaxInferTokens(ApiUtils.DEFAULT_MAX_INFER_TOKENS)
                .withMaxRefineTokens(ApiUtils.DEFAULT_MAX_REFINE_TOKENS)
                .withSpeed(ApiUtils.DEFAULT_SPEED)
                .withTextSeed(ApiUtils.DEFAULT_TEXT_SEED)
                .withCustomVoice(0)
                .withSkipRefine(0)
                .withStream(0)
                .withVoice(ChatTtsAudioApi.SpeechRequest.Voice.VOICE_SEED_1983_RESTORED_EMB.getValue())
                .build());

        Scanner scanner = new Scanner(System.in);
        List<Message> historyList = new ArrayList<>();
        while (true) {
            System.out.print(">>> ");
            String message = scanner.nextLine();
            if (message.equals("exit")) {
                break;
            }
            historyList.add(new UserMessage(message));
            Prompt prompt = new Prompt(historyList);
            ChatResponse chatResponse = chatModel.call(prompt);
            historyList.add(chatResponse.getResult().getOutput());
            String resp = chatResponse.getResult().getOutput().getText();
            System.out.println("<<< " + resp);
            try {
                System.out.println(">>> 生成音频中...");
                InputStream stream = chatTtsClient.call(MarkdownUtils.removeMarkdownTags(MarkdownUtils.convertChinesePunctuationToEnglish(resp)));
                if(Objects.nonNull(stream)){
                    System.out.println("<<< 音频开始播放...");
                    AudioPlayer.playWav(stream);
                    System.out.println(">>> 音频播放完");
                }
            } catch (Exception e) {
                log.error("音频播放异常:{}", e.getMessage());
            }
        }
    }


}
