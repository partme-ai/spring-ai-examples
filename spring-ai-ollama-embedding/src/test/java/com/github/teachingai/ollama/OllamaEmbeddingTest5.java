package com.github.partmeai.ollama;

import com.alibaba.fastjson2.JSONObject;
import org.springframework.ai.document.Document;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaEmbeddingOptions;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.ParagraphPdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;

import java.util.List;
import java.util.Scanner;

/**
 * 该示例用于学习文档解析、嵌入、简单的相似度搜索
 */
public class OllamaEmbeddingTest5 {

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
        var ollamaOptions = OllamaEmbeddingOptions.builder().model("mxbai-embed-large").build();
        var embeddingModel = OllamaEmbeddingModel.builder().ollamaApi(ollamaApi)
                .defaultOptions(ollamaOptions).build();

        /*
         * 1、解析 llama2.pdf
         */
        ParagraphPdfDocumentReader pdfReader = new ParagraphPdfDocumentReader("classpath:/llama2.pdf",
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
        for (Document document : documents) {
            System.out.println( JSONObject.of( "id", document.getId(), "embedding", embeddingModel.embed(document),"content", document.getText(), "metadata", document.getMetadata()));
        }

        /*
         * 3、简单的相似度搜索
         */
        VectorStore vectorStore = SimpleVectorStore.builder(embeddingModel).build();
        vectorStore.add(documents);
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("请输入关键词: ");
            String query = scanner.nextLine();
            if (query.equals("exit")) {
                break;
            }
            System.out.print("Embedding Query: " + embeddingModel.embed(query));
            // Retrieve embeddings
            SearchRequest request = SearchRequest.builder().query(query).topK(2).similarityThreshold(0.5).build();
            List<Document> similarDocuments  = vectorStore.similaritySearch(request);
            System.out.println("查询结果: ");
            for (Document document : similarDocuments ) {
                System.out.println( JSONObject.of( "id", document.getId(), "embedding", document.getScore(),"content", document.getText(), "metadata", document.getMetadata()));
            }
        }
    }

}
