package com.github.partmeai.ollama.controller;

import com.github.partmeai.ollama.request.OcrRequest;
import com.github.partmeai.ollama.response.OcrResponse;
import com.github.partmeai.ollama.service.QianfanOcrService;
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
@Tag(name = "OCR API", description = "基于 Qianfan-OCR 的端到端文档智能接口")
public class OcrController {

    private final QianfanOcrService ocrService;

    @Autowired
    public OcrController(QianfanOcrService ocrService) {
        this.ocrService = ocrService;
    }

    @PostMapping("/document")
    @Operation(summary = "文档解析", description = "将文档转换为 Markdown 格式")
    public ResponseEntity<OcrResponse> parseDocument(@RequestBody OcrRequest request) {
        OcrResponse response;
        if (request.isEnableThinking()) {
            response = ocrService.parseDocumentWithThinking(request.getImageBase64());
        } else {
            response = ocrService.parseDocument(request.getImageBase64());
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/layout")
    @Operation(summary = "布局分析", description = "分析文档布局（启用 Layout-as-Thought）")
    public ResponseEntity<OcrResponse> analyzeLayout(@RequestBody OcrRequest request) {
        OcrResponse response = ocrService.analyzeLayout(request.getImageBase64());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/table")
    @Operation(summary = "表格提取", description = "提取文档中的所有表格")
    public ResponseEntity<OcrResponse> extractTables(@RequestBody OcrRequest request) {
        OcrResponse response = ocrService.extractTables(request.getImageBase64());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/chart")
    @Operation(summary = "图表理解", description = "分析文档中的图表")
    public ResponseEntity<OcrResponse> analyzeCharts(@RequestBody OcrRequest request) {
        OcrResponse response = ocrService.analyzeCharts(request.getImageBase64());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/kie")
    @Operation(summary = "关键信息提取", description = "提取文档中的关键信息")
    public ResponseEntity<OcrResponse> extractKeyInfo(@RequestBody OcrRequest request) {
        OcrResponse response = ocrService.extractKeyInfo(request.getImageBase64());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/json")
    @Operation(summary = "JSON 转换", description = "将文档转换为 JSON 格式")
    public ResponseEntity<OcrResponse> convertToJson(@RequestBody OcrRequest request) {
        OcrResponse response = ocrService.convertToJson(request.getImageBase64());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/custom")
    @Operation(summary = "自定义 Prompt", description = "使用自定义 Prompt 进行 OCR")
    public ResponseEntity<OcrResponse> customOcr(@RequestBody OcrRequest request) {
        String prompt = request.getPrompt() != null ? request.getPrompt() : "Convert this document to markdown.";
        OcrResponse response = ocrService.processImageWithCustomPrompt(
            request.getImageBase64(), 
            prompt, 
            request.isEnableThinking()
        );
        return ResponseEntity.ok(response);
    }
}
