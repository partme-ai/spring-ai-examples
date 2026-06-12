package com.github.partmeai.ollama.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 部分接口不拦截
        //registry.addInterceptor().excludePathPatterns("/swagger-ui/**", "/v3/api-docs/**");
    }
}
