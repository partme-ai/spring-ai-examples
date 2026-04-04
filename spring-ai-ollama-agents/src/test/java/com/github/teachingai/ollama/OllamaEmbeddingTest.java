package com.github.teachingai.ollama;

import org.springframework.ai.document.Document;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaEmbeddingOptions;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.SearchRequest;

import java.util.List;
import java.util.Scanner;

public class OllamaEmbeddingTest {

    public static void main(String[] args) {

        /*
         * bge-m3 ：https://ollama.com/library/bge-m3
         */
        var ollamaApi = OllamaApi.builder().build();
        var ollamaOptions = OllamaEmbeddingOptions.builder().model("bge-m3:latest").build();
        var embeddingModel = OllamaEmbeddingModel.builder().ollamaApi(ollamaApi)
                .defaultOptions(ollamaOptions).build();
        //测试数据
        VectorStore vectorStore = SimpleVectorStore.builder(embeddingModel).build();
        vectorStore.add(List.of(
                new Document("白日依山尽，黄河入海流。欲穷千里目，更上一层楼。"),
                new Document("青山依旧在，几度夕阳红。白发渔樵江渚上，惯看秋月春风。"),
                new Document("一片孤城万仞山，羌笛何须怨杨柳。春风不度玉门关。"),
                new Document("危楼高百尺，手可摘星辰。不敢高声语，恐惊天上人。")
        ));
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("请输入关键词: ");
            String message = scanner.nextLine();
            if (message.equals("exit")) {
                break;
            }
            List<Document> documents = vectorStore.similaritySearch(SearchRequest.builder().query(message).topK(3).build());
            System.out.println("查询结果: ");
            for (Document doc : documents) {
                System.out.println(doc.getFormattedContent());
            }
        }
    }

}
