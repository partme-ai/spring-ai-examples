package com.github.teachingai.ollama.response;

import io.swagger.v3.oas.annotations.media.Schema;

public class OcrResponse {

    @Schema(description = "是否成功")
    private boolean success;

    @Schema(description = "识别结果")
    private String result;

    @Schema(description = "错误信息")
    private String error;

    @Schema(description = "处理时间（毫秒）")
    private long processingTime;

    public OcrResponse() {
    }

    public OcrResponse(boolean success, String result) {
        this.success = success;
        this.result = result;
    }

    public static OcrResponse success(String result, long processingTime) {
        OcrResponse response = new OcrResponse(true, result);
        response.setProcessingTime(processingTime);
        return response;
    }

    public static OcrResponse error(String error) {
        OcrResponse response = new OcrResponse(false, null);
        response.setError(error);
        return response;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public long getProcessingTime() {
        return processingTime;
    }

    public void setProcessingTime(long processingTime) {
        this.processingTime = processingTime;
    }
}
