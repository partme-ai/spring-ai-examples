package com.github.partmeai.ollama.controller;

import com.github.partmeai.ollama.request.OcrRequest;
import com.github.partmeai.ollama.response.OcrResponse;
import com.github.partmeai.ollama.service.PaddleOcrService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/ocr")
@Tag(name = "OCR API", description = "基于 PaddleOCR-VL 的 OCR 识别接口")
public class OcrController {

    private final PaddleOcrService ocrService;

    @Autowired
    public OcrController(PaddleOcrService ocrService) {
        this.ocrService = ocrService;
    }

    @PostMapping("/text")
    @Operation(summary = "文本识别", description = "识别图片中的文本内容")
    public ResponseEntity<OcrResponse> recognizeText(@RequestBody OcrRequest request) {
        OcrResponse response = ocrService.recognizeText(request.getImageBase64());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/table")
    @Operation(summary = "表格识别", description = "识别表格并转换为 Markdown 格式")
    public ResponseEntity<OcrResponse> recognizeTable(@RequestBody OcrRequest request) {
        OcrResponse response = ocrService.recognizeTable(request.getImageBase64());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/formula")
    @Operation(summary = "公式识别", description = "识别数学公式")
    public ResponseEntity<OcrResponse> recognizeFormula(@RequestBody OcrRequest request) {
        OcrResponse response = ocrService.recognizeFormula(request.getImageBase64());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/chart")
    @Operation(summary = "图表识别", description = "识别图表数据")
    public ResponseEntity<OcrResponse> recognizeChart(@RequestBody OcrRequest request) {
        OcrResponse response = ocrService.recognizeChart(request.getImageBase64());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/markdown")
    @Operation(summary = "Markdown 转换", description = "将文档转换为 Markdown 格式")
    public ResponseEntity<OcrResponse> convertToMarkdown(@RequestBody OcrRequest request) {
        OcrResponse response = ocrService.convertToMarkdown(request.getImageBase64());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/custom")
    @Operation(summary = "自定义 Prompt", description = "使用自定义 Prompt 进行 OCR")
    public ResponseEntity<OcrResponse> customOcr(@RequestBody OcrRequest request) {
        String prompt = request.getPrompt() != null ? request.getPrompt() : "OCR:";
        OcrResponse response = ocrService.processImageWithCustomPrompt(request.getImageBase64(), prompt);
        return ResponseEntity.ok(response);
    }
}
