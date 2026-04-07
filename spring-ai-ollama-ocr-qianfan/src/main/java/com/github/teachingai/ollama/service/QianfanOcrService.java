package com.github.teachingai.ollama.service;

import com.github.teachingai.ollama.response.OcrResponse;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.chat.messages.Media;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

import java.util.List;

@Service
public class QianfanOcrService {

    private final OllamaChatModel chatModel;

    private static final String PROMPT_MARKDOWN = "Convert this document to markdown.";
    private static final String PROMPT_JSON = "Convert this document to JSON.";
    private static final String PROMPT_TABLE = "Extract all tables from this document.";
    private static final String PROMPT_CHART = "Analyze the charts in this document.";
    private static final String PROMPT_KIE = "Extract the key information from this document.";
    private static final String THINK_PREFIX = "<think⟩";

    @Autowired
    public QianfanOcrService(OllamaChatModel chatModel) {
        this.chatModel = chatModel;
    }

    public OcrResponse parseDocument(String imageBase64) {
        return processImage(imageBase64, PROMPT_MARKDOWN);
    }

    public OcrResponse parseDocumentWithThinking(String imageBase64) {
        return processImage(imageBase64, THINK_PREFIX + PROMPT_MARKDOWN);
    }

    public OcrResponse convertToJson(String imageBase64) {
        return processImage(imageBase64, PROMPT_JSON);
    }

    public OcrResponse extractTables(String imageBase64) {
        return processImage(imageBase64, PROMPT_TABLE);
    }

    public OcrResponse analyzeCharts(String imageBase64) {
        return processImage(imageBase64, PROMPT_CHART);
    }

    public OcrResponse extractKeyInfo(String imageBase64) {
        return processImage(imageBase64, PROMPT_KIE);
    }

    public OcrResponse analyzeLayout(String imageBase64) {
        return processImage(imageBase64, THINK_PREFIX + "Analyze the layout of this document.");
    }

    public OcrResponse processImage(String imageBase64, String prompt) {
        long startTime = System.currentTimeMillis();
        
        try {
            Media imageMedia = new Media(MimeTypeUtils.IMAGE_JPEG, imageBase64);
            UserMessage userMessage = new UserMessage(prompt, List.of(imageMedia));
            Prompt chatPrompt = new Prompt(List.of(userMessage));
            
            String result = chatModel.call(chatPrompt).getResult().getOutput().getText();
            
            long processingTime = System.currentTimeMillis() - startTime;
            return OcrResponse.success(result, processingTime);
        } catch (Exception e) {
            return OcrResponse.error("OCR 处理失败: " + e.getMessage());
        }
    }

    public OcrResponse processImageWithCustomPrompt(String imageBase64, String customPrompt, boolean enableThinking) {
        String prompt = enableThinking ? THINK_PREFIX + customPrompt : customPrompt;
        return processImage(imageBase64, prompt);
    }
}
