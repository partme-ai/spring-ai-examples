package com.github.partmeai.ollama;

import com.alibaba.fastjson2.JSONObject;
import org.springframework.ai.document.Document;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaEmbeddingOptions;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;

import java.util.List;
import java.util.Scanner;

/**
 * 该示例用于学习 文本嵌入、简单的相似度搜索
 */
public class OllamaEmbeddingTest4 {

    /**
     * 下面代码依赖 spring-ai-pdf-document-reader 和 pdfbox（3.0.2）
     */
    public static void main(String[] args) {

        /*
         * mxbai-embed-large ：https://ollama.com/library/mxbai-embed-large
         * nomic-embed-text ：https://ollama.com/library/nomic-embed-text
         * snowflake-arctic-embed ：https://ollama.com/library/snowflake-arctic-embed
         * shaw/dmeta-embedding-zh：https://ollama.com/shaw/dmeta-embedding-zh
         */
        var ollamaApi = OllamaApi.builder().build();
        var ollamaOptions = OllamaEmbeddingOptions.builder().model("shaw/dmeta-embedding-zh").build();
        var embeddingModel = OllamaEmbeddingModel.builder().ollamaApi(ollamaApi)
                .defaultOptions(ollamaOptions).build();
        /*
         * 1、简单的文本嵌入
         */
        VectorStore vectorStore = SimpleVectorStore.builder(embeddingModel).build();
        // 将嵌入存储在 VectorStore
        vectorStore.add(List.of(
            new Document("床前明月光，疑是地上霜。举头望明月，低头思故乡。"),
            new Document("李白乘舟将欲行，忽闻岸上踏歌声。桃花潭水深千尺，不及汪伦送我情。"),
            new Document("日照香炉生紫烟，遥看瀑布挂前川。飞流直下三千尺，疑是银河落九天。"),
            new Document("独在异乡为异客，每逢佳节倍思亲。遥知兄弟登高处，遍插茱萸少一人。")
        ));
        /*
         * 2、简单的相似度搜索
         */
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("请输入关键词: ");
            String query = scanner.nextLine();
            if (query.equals("exit")) {
                break;
            }
            System.out.print("Embedding Query: " + embeddingModel.embed(query));
            // Retrieve embeddings
            SearchRequest request = SearchRequest.builder().query(query).topK(4).similarityThreshold(0.4d).build();
            List<Document> similarDocuments  = vectorStore.similaritySearch(request);
            System.out.println("查询结果: ");
            for (Document document : similarDocuments ) {
                document.getMetadata().put("similarity", document.getMetadata().get("similarity"));
                System.out.println( JSONObject.of( "id", document.getId(), "content", document.getText(),"metadata", document.getMetadata(), "embedding", document.getScore()));
            }
        }
    }

}
