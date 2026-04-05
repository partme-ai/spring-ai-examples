package com.github.partmeai.ollama.service;

import jakarta.servlet.http.Part;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Embedding Service
 */
public interface IEmbeddingService {

    /**
     * Embed text
     *
     * @param text text to embed
     * @return embeddings
     */
    Map<String, Object> embedding(String text);

    /**
     * Embed Document File
     *
     * @param file file to embed
     * @return embeddings
     */
    List<Map<String, Object>> embedding(MultipartFile file) throws IOException;

    /**
     * Embed Document File
     *
     * @param file file to embed
     * @return embeddings
     */
    List<Map<String, Object>> embedding(Part file) throws IOException;

}
