package com.github.teachingai.ollama;

import com.github.teachingai.ollama.api.UnifiedTtsAudioApi;
import com.github.teachingai.ollama.util.MarkdownUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.SimpleApiKey;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.ai.utils.AudioPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

@Slf4j
public class OllamaUnifiedTtsTest {

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
                .model("gemma3:4b")
                .temperature(0.9d).build();
        chatModel = OllamaChatModel.builder()
                .ollamaApi(ollamaApi)
                .defaultOptions(ollamaOptions).build();
    }

    @Test
    public void testAudio() {

        var unifiedTtsAudioApi = UnifiedTtsAudioApi.builder()
                .apiKey(new SimpleApiKey(""))
                .build();
        var unifiedTtsModl = new UnifiedTtsAudioSpeechModel(unifiedTtsAudioApi, UnifiedTtsAudioSpeechOptions.builder()
                // `edge-tts -l` 查看可用的声音，
                // 普通话-男：zh-CN-YunyangNeural
                // 普通话-女：zh-CN-XiaoxiaoNeural、zh-CN-XiaoyiNeural
                // 辽宁方言-女：zh-CN-liaoning-XiaobeiNeural
                // 陕西方言-女：zh-CN-shaanxi-XiaoniNeural
                .voice("zh-CN-YunyangNeural")
                .volume(1.0F)
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
                String text = MarkdownUtils.removeMarkdownTags(MarkdownUtils.convertChinesePunctuationToEnglish(resp));
                       text = text.replace("\"", "");
                       text = text.replace("'", "");
                UnifiedTtsAudioApi.SpeechResponse.AudioFile audioFile = unifiedTtsModl.call(text);
                if(Objects.nonNull(audioFile)){
                    System.out.println("<<< 拉取音频文件到本地...");
                    byte[] audioData = unifiedTtsAudioApi.fetchAudio(audioFile.audioUrl());
                    System.out.println("<<< 音频开始播放...");
                    AudioPlayer.play(audioData);
                    System.out.println(">>> 音频播放完");

                    /*// 在当前工程目录下生成测试结果目录并写入文件
                    Path projectDir = Paths.get(System.getProperty("user.dir"));
                    Path resultDir = projectDir.resolve("unified-tts");
                    Files.createDirectories(resultDir);
                    Path out = resultDir.resolve(System.currentTimeMillis() + ".mp3");
                    Path written = unifiedTtsAudioApi.saveToFile(audioFile, out);
                    System.out.println("UnifiedTTS test output: " + written.toAbsolutePath());
                    assertTrue(Files.exists(written), "Output file should exist");
                    assertTrue(Files.size(written) > 0, "Output file size should be > 0");*/
                }
            } catch (Exception e) {
                log.error("UnifiedTTS test error: ", e);
            }
        }
    }
}
