package com.github.partmeai.ollama.request;

import io.swagger.v3.oas.annotations.media.Schema;

public class OcrRequest {

    @Schema(description = "Base64 编码的图片数据", required = true)
    private String imageBase64;

    @Schema(description = "自定义 Prompt（可选）")
    private String prompt;

    @Schema(description = "是否启用思考模式（Layout-as-Thought）")
    private boolean enableThinking;

    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public boolean isEnableThinking() {
        return enableThinking;
    }

    public void setEnableThinking(boolean enableThinking) {
        this.enableThinking = enableThinking;
    }
}
