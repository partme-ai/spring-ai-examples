package com.github.teachingai.ollama;

import com.github.teachingai.ollama.api.ApiUtils;
import com.github.teachingai.ollama.api.EmotiVoiceAudioApi;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaChatOptions;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
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

        var chatTtsApi = new EmotiVoiceAudioApi("");
        var chatTtsClient = new EmotiVoiceAudioSpeechClient(chatTtsApi, EmotiVoiceAudioSpeechOptions.builder()
                .model("tts-1")
                .withVoice(EmotiVoiceAudioApi.SpeechRequest.Voice.ALLOY)
                .withResponseFormat(EmotiVoiceAudioApi.SpeechRequest.AudioResponseFormat.MP3)    // MP3, OPUS, AAC, FLAC
                .withSpeed(ApiUtils.DEFAULT_SPEED)
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
                byte[] stream = chatTtsClient.call(MarkdownUtils.removeMarkdownTags(MarkdownUtils.convertChinesePunctuationToEnglish(resp)));
                System.out.println("音频生成完成");
                if(Objects.nonNull(stream)){
                    playWav(stream);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void playWav(byte[] wavStream) {
        try {
            Clip clip = AudioSystem.getClip();
            AudioFormat format = clip.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, format);
            try ( ByteArrayInputStream bais = new ByteArrayInputStream(wavStream);
                  BufferedInputStream bufferedStream = new BufferedInputStream(bais);
                  AudioInputStream ais = AudioSystem.getAudioInputStream(bufferedStream)){
                bufferedStream.mark(bais.available());
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
