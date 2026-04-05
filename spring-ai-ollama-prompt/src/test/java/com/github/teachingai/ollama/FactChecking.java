package com.github.partmeai.ollama;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.evaluation.FactCheckingEvaluator;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.evaluation.EvaluationRequest;
import org.springframework.ai.evaluation.EvaluationResponse;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaChatOptions;

import java.util.Collections;

public class FactChecking {

    private static final String BESPOKE_MINICHECK = "bespoke-minicheck";

@Test
void testFactChecking() {
    // Set up the Ollama API
    OllamaApi ollamaApi = OllamaApi.builder().baseUrl("http://localhost:11434").build();

    ChatModel chatModel = OllamaChatModel.builder().ollamaApi(ollamaApi).defaultOptions(
            OllamaChatOptions.builder().model(BESPOKE_MINICHECK).numPredict(2).temperature(0.0d).build()).build();

    // Create the FactCheckingEvaluator（1.1.x 使用工厂方法，内部绑定 bespoke-minicheck 模型名）
    var factCheckingEvaluator = FactCheckingEvaluator.forBespokeMinicheck(ChatClient.builder(chatModel));

    // Example context and claim
    String context = "The Earth is the third planet from the Sun and the only astronomical object known to harbor life.";
    String claim = "The Earth is the fourth planet from the Sun.";

    // Create an EvaluationRequest
    EvaluationRequest evaluationRequest = new EvaluationRequest(context, Collections.emptyList(), claim);

    // Perform the evaluation
    EvaluationResponse evaluationResponse = factCheckingEvaluator.evaluate(evaluationRequest);

    Assertions.assertFalse(evaluationResponse.isPass(), "The claim should not be supported by the context");

}

}
