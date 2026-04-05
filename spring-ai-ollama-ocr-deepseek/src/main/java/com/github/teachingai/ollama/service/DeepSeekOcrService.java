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
public class DeepSeekOcrService {

    private final OllamaChatModel chatModel;

    private static final String PROMPT_DOCUMENT = "<image>\n<|grounding|>Convert the document to markdown.";
    private static final String PROMPT_MARKDOWN = "<image>\n<|grounding|>Convert the document to markdown.";
    private static final String PROMPT_FREE_OCR = "<image>\nFree OCR.";
    private static final String PROMPT_TABLE = "<image>\n<|grounding|>Extract the table content.";
    private static final String PROMPT_FORMULA = "<image>\n<|grounding|>Extract the formula.";

    @Autowired
    public DeepSeekOcrService(OllamaChatModel chatModel) {
        this.chatModel = chatModel;
    }

    public OcrResponse parseDocument(String imageBase64) {
        return processImage(imageBase64, PROMPT_DOCUMENT);
    }

    public OcrResponse convertToMarkdown(String imageBase64) {
        return processImage(imageBase64, PROMPT_MARKDOWN);
    }

    public OcrResponse freeOcr(String imageBase64) {
        return processImage(imageBase64, PROMPT_FREE_OCR);
    }

    public OcrResponse extractTable(String imageBase64) {
        return processImage(imageBase64, PROMPT_TABLE);
    }

    public OcrResponse extractFormula(String imageBase64) {
        return processImage(imageBase64, PROMPT_FORMULA);
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
