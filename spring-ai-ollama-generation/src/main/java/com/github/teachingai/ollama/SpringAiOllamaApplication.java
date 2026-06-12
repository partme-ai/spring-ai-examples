package com.github.partmeai.ollama;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableKnife4j
public class SpringAiOllamaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringAiOllamaApplication.class, args);
    }

}
