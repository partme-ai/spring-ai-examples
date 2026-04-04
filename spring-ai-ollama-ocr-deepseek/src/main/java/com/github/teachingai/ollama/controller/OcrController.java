package com.github.teachingai.ollama.controller;

import com.github.teachingai.ollama.request.OcrRequest;
import com.github.teachingai.ollama.response.OcrResponse;
import com.github.teachingai.ollama.service.DeepSeekOcrService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/v1/ocr")
@Tag(name = "OCR API", description = "基于 DeepSeek-OCR 的 OCR 识别接口")
public class OcrController {

    private final DeepSeekOcrService ocrService;

    @Autowired
    public OcrController(DeepSeekOcrService ocrService) {
        this.ocrService = ocrService;
    }

    @PostMapping("/document")
    @Operation(summary = "文档解析", description = "解析复杂文档并转换为 Markdown")
    public ResponseEntity<OcrResponse> parseDocument(@RequestBody OcrRequest request) {
        OcrResponse response = ocrService.parseDocument(request.getImageBase64());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/markdown")
    @Operation(summary = "Markdown 转换", description = "将文档转换为 Markdown 格式")
    public ResponseEntity<OcrResponse> convertToMarkdown(@RequestBody OcrRequest request) {
        OcrResponse response = ocrService.convertToMarkdown(request.getImageBase64());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/free")
    @Operation(summary = "纯 OCR", description = "无布局的纯文本识别")
    public ResponseEntity<OcrResponse> freeOcr(@RequestBody OcrRequest request) {
        OcrResponse response = ocrService.freeOcr(request.getImageBase64());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/table")
    @Operation(summary = "表格提取", description = "提取表格内容")
    public ResponseEntity<OcrResponse> extractTable(@RequestBody OcrRequest request) {
        OcrResponse response = ocrService.extractTable(request.getImageBase64());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/formula")
    @Operation(summary = "公式提取", description = "提取数学公式")
    public ResponseEntity<OcrResponse> extractFormula(@RequestBody OcrRequest request) {
        OcrResponse response = ocrService.extractFormula(request.getImageBase64());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/custom")
    @Operation(summary = "自定义 Prompt", description = "使用自定义 Prompt 进行 OCR")
    public ResponseEntity<OcrResponse> customOcr(@RequestBody OcrRequest request) {
        String prompt = request.getPrompt() != null ? request.getPrompt() : "<image>\n<|grounding|>Convert the document to markdown.";
        OcrResponse response = ocrService.processImageWithCustomPrompt(request.getImageBase64(), prompt);
        return ResponseEntity.ok(response);
    }
}
