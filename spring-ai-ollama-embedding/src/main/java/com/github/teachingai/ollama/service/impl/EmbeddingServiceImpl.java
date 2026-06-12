package com.github.partmeai.ollama.service.impl;

import com.github.partmeai.ollama.service.IEmbeddingService;
import jakarta.servlet.http.Part;
import org.springframework.ai.document.Document;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.ParagraphPdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Embedding Service Implementation
 */
@Service
public class EmbeddingServiceImpl implements IEmbeddingService {

    private final OllamaEmbeddingModel embeddingModel;

    @Autowired
    public EmbeddingServiceImpl(OllamaEmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    @Override
    public Map<String, Object> embedding(String text) {
        return Map.of("embeddings", embeddingModel.embed(text));
    }

    @Override
    public List<Map<String, Object>> embedding(MultipartFile file) throws IOException {
        // 1、加载文件为 Resource
        InputStreamResource resource = new InputStreamResource(file.getInputStream());
        // 2、调用 embedding 方法
        return this.embedding(resource);
    }

    @Override
    public List<Map<String, Object>> embedding(Part file) throws IOException {
        // 1、加载文件为 Resource
        InputStreamResource resource = new InputStreamResource(file.getInputStream());
        // 2、调用 embedding 方法
        return this.embedding(resource);
    }

    protected List<Map<String, Object>> embedding(Resource resource) throws IOException {
        /*
         * 1、解析 llama2.pdf
         */
        ParagraphPdfDocumentReader pdfReader = new ParagraphPdfDocumentReader(resource,
                PdfDocumentReaderConfig.builder()
                        .withPageTopMargin(0)
                        .withPageExtractedTextFormatter(ExtractedTextFormatter.builder()
                                .withNumberOfTopTextLinesToDelete(0)
                                .build())
                        .withPagesPerDocument(1)
                        .build());
        /*
         * 2、读取并处理PDF文档以提取段落。
         */
        List<Document> documents = pdfReader.get();
        List<Map<String, Object>> mapList = new ArrayList<>();
        for (Document document : documents) {
            mapList.add(Map.of(
                    "id", document.getId(),
                    "embedding", embeddingModel.embed(document),
                    "content", document.getFormattedContent(),
                    "metadata", document.getMetadata()));
        }
        return mapList;
    }

}
