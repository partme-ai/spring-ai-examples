package com.github.teachingai.ollama;

import com.github.teachingai.ollama.api.EdgeTtsAudioApi;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class Ollama_夸夸机_Test {

    /**
     * qwen2:7b ：https://ollama.com/library/qwen2
     * gemma2:9b ：https://ollama.com/library/gemma2
     * glm4:9b ：https://ollama.com/library/glm4
     * llama3:8b ：https://ollama.com/library/llama3
     * mistral ：https://ollama.com/library/mistral
     */
    public static void main(String[] args) throws IOException {

         var ollamaApi = OllamaApi.builder().build();
        var chatModel = OllamaChatModel.builder().ollamaApi(ollamaApi).build();

        SystemMessage systemMessage = new SystemMessage("你是我的私人助理，你最重要的工作就是不断地鼓励我、激励我、夸赞我。你需要以温柔、体贴、亲切的语气和我聊天。你的聊天风格特别可爱有趣，你的每一个回答都要体现这一点。");

        List<Message> messages  = List.of(systemMessage);

        EdgeTtsAudioSpeechClient chatTtsClient = new EdgeTtsAudioSpeechClient(new EdgeTtsAudioApi());

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
                text = StringUtils.replace(text, "\"", "");
                text = StringUtils.replace(text, "'", "");
                InputStream stream = chatTtsClient.call(text);
                if(Objects.nonNull(stream)){
                    System.out.println("<<< 音频开始播放...");
                    AudioPlayer.playMP3(stream);
                    System.out.println(">>> 音频播放完");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}
