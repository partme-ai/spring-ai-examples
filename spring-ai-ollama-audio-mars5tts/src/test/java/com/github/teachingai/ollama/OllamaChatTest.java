package com.github.teachingai.ollama;

import com.github.teachingai.ollama.api.ApiUtils;
import com.github.teachingai.ollama.api.ChatTtsAudioApi;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaChatOptions;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class OllamaChatTest {

    public static void main(String[] args) {

        /**
         * qwen2:7b ：https://ollama.com/library/qwen2
         * mistral ：https://ollama.com/library/mistral
         */
         var ollamaApi = OllamaApi.builder().build();
        var chatModel = OllamaChatModel.builder()
                .ollamaApi(ollamaApi)
                .defaultOptions(OllamaChatOptions.builder()
                        .model("qwen:7b")
                        .temperature(0.9d)
                        .build())
                .build();

        var chatTtsApi = new ChatTtsAudioApi();
        var chatTtsClient = new ChatTtsAudioSpeechClient(chatTtsApi, ChatTtsAudioSpeechOptions.builder()
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
                System.out.print(">>> 开始生成音频...");
                InputStream stream = chatTtsClient.call(MarkdownUtils.removeMarkdownTags(MarkdownUtils.convertChinesePunctuationToEnglish(resp)));
                System.out.println("音频生成完成");
                if(Objects.nonNull(stream)){
                    playWav(stream);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void playWav(InputStream wavStream) {
        try {
            Clip clip = AudioSystem.getClip();
            AudioFormat format = clip.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, format);
            // Wrap the InputStream in a BufferedInputStream
            try (BufferedInputStream bufferedStream = new BufferedInputStream(wavStream);
                 AudioInputStream ais = AudioSystem.getAudioInputStream(bufferedStream)){

                bufferedStream.mark(wavStream.available());

                clip = (Clip) AudioSystem.getLine(info);
                clip.open(ais);

                clip.start();
                Thread.sleep(clip.getMicrosecondLength()/1808);
                clip.drain();
                clip.stop();
            } finally {
                clip.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
