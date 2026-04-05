package com.github.partmeai.ollama.service;

import com.github.partmeai.ollama.response.OcrResponse;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.chat.messages.Media;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

import java.util.List;

@Service
public class PaddleOcrService {

    private final OllamaChatModel chatModel;

    private static final String PROMPT_OCR = "OCR:";
    private static final String PROMPT_TABLE = "Table Recognition:";
    private static final String PROMPT_FORMULA = "Formula Recognition:";
    private static final String PROMPT_CHART = "Chart Recognition:";
    private static final String PROMPT_MARKDOWN = "Convert to markdown:";

    @Autowired
    public PaddleOcrService(OllamaChatModel chatModel) {
        this.chatModel = chatModel;
    }

    public OcrResponse recognizeText(String imageBase64) {
        return processImage(imageBase64, PROMPT_OCR);
    }

    public OcrResponse recognizeTable(String imageBase64) {
        return processImage(imageBase64, PROMPT_TABLE);
    }

    public OcrResponse recognizeFormula(String imageBase64) {
        return processImage(imageBase64, PROMPT_FORMULA);
    }

    public OcrResponse recognizeChart(String imageBase64) {
        return processImage(imageBase64, PROMPT_CHART);
    }

    public OcrResponse convertToMarkdown(String imageBase64) {
        return processImage(imageBase64, PROMPT_MARKDOWN);
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

    public OcrResponse processImageWithCustomPrompt(String imageBase64, String customPrompt) {
        return processImage(imageBase64, customPrompt);
    }
}
