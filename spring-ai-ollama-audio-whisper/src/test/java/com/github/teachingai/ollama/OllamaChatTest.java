package com.github.partmeai.ollama;

import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaChatOptions;

import java.util.Scanner;

public class OllamaChatTest {

    public static void main(String[] args) {

         var ollamaApi = OllamaApi.builder().build();
        var chatModel = OllamaChatModel.builder()
                .ollamaApi(ollamaApi)
                .defaultOptions(OllamaChatOptions.builder()
                        .model("qwen:7b")
                        .temperature(0.9d)
                        .build())
                .build();

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print(">>> ");
            String message = scanner.nextLine();
            if (message.equals("exit")) {
                break;
            }
            String resp = chatModel.call(message);
            System.out.println("<<< " + resp);
        }
    }

}
