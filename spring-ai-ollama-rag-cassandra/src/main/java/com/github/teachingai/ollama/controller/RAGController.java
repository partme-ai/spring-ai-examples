package com.github.partmeai.ollama.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
class RAGController {
    private final ChatClient chatModel;
    private final VectorStore vectorStore;

    RAGController(ChatClient chatModel, VectorStore vectorStore) {
        this.chatModel = chatModel;
        this.vectorStore = vectorStore;
    }

    // 假设我们已经从包含人员信息的文件中读取了文档
    // 并按照上一节的描述将其存储在 VectorStore 中。

    @GetMapping("/ai/rag/people")
    Person chatWithRag(@RequestParam String name) {
        // 使用自然语言查询 VectorStore，查找有关个人的信息。
        List<Document> similarDocuments = vectorStore.similaritySearch(
                SearchRequest.builder().query(name).topK(2).build());
        String information = similarDocuments.stream()
                .map(Document::getText)
                .collect(Collectors.joining(System.lineSeparator()));

        //构建 systemMessage 以指示人工智能模型使用传递的信息
        // 回答问题。
        var systemPromptTemplate = new SystemPromptTemplate("""
              You are a helpful assistant.
              
              Use the following information to answer the question:
              {information}
              """);
        var systemMessage = systemPromptTemplate.createMessage(
                Map.of("information", information));

        // 使用 BeanOutputConverter 将响应解析为 Person 的实例。
        var outputParser = new BeanOutputConverter<>(Person.class);

        // 构建用户信息（userMessage），要求人工智能模型介绍这个人。
        PromptTemplate userMessagePromptTemplate = new PromptTemplate("""
        Tell me about {name} as if current date is {current_date}.

        {format}
        """);
        Map<String,Object> model = Map.of("name", name,
                "current_date", LocalDate.now(),
                "format", outputParser.getFormat());
        var userMessage = new UserMessage(userMessagePromptTemplate.create(model).getContents());

        var prompt = new Prompt(List.of(systemMessage, userMessage));

        String response = chatModel.prompt(prompt).call().content();

        return outputParser.convert(response);
    }
}

record Person(String name,
              String dateOfBirth,
              int experienceInYears,
              List<String> books) {
}