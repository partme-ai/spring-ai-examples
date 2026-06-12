package com.github.partmeai.ollama;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = com.github.partmeai.ollama.SpringAiOllamaApplication.class)
@AutoConfigureWebMvc
@ActiveProfiles("test")
public class OllamaGenerationApiTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void testGenerate_Success() throws Exception {
        // 测试成功的文本生成请求
        System.out.println("=== 测试成功的文本生成请求 ===");
        System.out.println("请求消息: 你好，世界！");
        
        MvcResult result = mockMvc.perform(get("/ai/generate")
                .param("message", "你好，世界！")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Generated successfully"))
                .andExpect(jsonPath("$.generation").exists())
                .andReturn();
        
        // 打印响应结果
        String responseContent = result.getResponse().getContentAsString();
        System.out.println("响应状态: " + result.getResponse().getStatus());
        System.out.println("响应内容: " + responseContent);
        System.out.println("=====================================\n");
    }

    @Test
    void testGenerate_WithDefaultMessage() throws Exception {
        // 测试使用默认消息的请求
        System.out.println("=== 测试使用默认消息的请求 ===");
        System.out.println("请求消息: (使用默认值)");
        
        MvcResult result = mockMvc.perform(get("/ai/generate")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Generated successfully"))
                .andExpect(jsonPath("$.generation").exists())
                .andReturn();
        
        // 打印响应结果
        String responseContent = result.getResponse().getContentAsString();
        System.out.println("响应状态: " + result.getResponse().getStatus());
        System.out.println("响应内容: " + responseContent);
        System.out.println("=====================================\n");
    }

    @Test
    void testGenerate_EmptyMessage() throws Exception {
        // 测试空消息的请求
        System.out.println("=== 测试空消息的请求 ===");
        System.out.println("请求消息: (空字符串)");
        
        MvcResult result = mockMvc.perform(get("/ai/generate")
                .param("message", "")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Generated successfully"))
                .andExpect(jsonPath("$.generation").exists())
                .andReturn();
        
        // 打印响应结果
        String responseContent = result.getResponse().getContentAsString();
        System.out.println("响应状态: " + result.getResponse().getStatus());
        System.out.println("响应内容: " + responseContent);
        System.out.println("=====================================\n");
    }

    @Test
    void testGenerateStream_Success() throws Exception {
        // 测试流式文本生成请求
        System.out.println("=== 测试流式文本生成请求 ===");
        System.out.println("请求消息: 讲个笑话");
        
        MvcResult result = mockMvc.perform(get("/ai/generateStream")
                .param("message", "讲个笑话")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        
        // 打印响应信息
        System.out.println("响应状态: " + result.getResponse().getStatus());
        System.out.println("响应内容类型: " + result.getResponse().getContentType());
        
        // 注意：流式响应是异步的，MockMvc 可能无法直接获取完整内容
        // 这里主要验证请求能够正常处理，不验证具体内容
        System.out.println("流式响应请求处理成功");
        System.out.println("注意：流式响应是异步的，完整内容需要在实际应用中验证");
        System.out.println("=====================================\n");
    }

    @Test
    void testGenerateStream_WithDefaultMessage() throws Exception {
        // 测试使用默认消息的流式请求
        System.out.println("=== 测试使用默认消息的流式请求 ===");
        System.out.println("请求消息: (使用默认值)");
        
        MvcResult result = mockMvc.perform(get("/ai/generateStream")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        
        // 打印响应信息
        System.out.println("响应状态: " + result.getResponse().getStatus());
        System.out.println("响应内容类型: " + result.getResponse().getContentType());
        System.out.println("流式响应请求处理成功");
        System.out.println("注意：流式响应是异步的，完整内容需要在实际应用中验证");
        System.out.println("=====================================\n");
    }

    @Test
    void testGenerateStream_EmptyMessage() throws Exception {
        // 测试空消息的流式请求
        System.out.println("=== 测试空消息的流式请求 ===");
        System.out.println("请求消息: (空字符串)");
        
        MvcResult result = mockMvc.perform(get("/ai/generateStream")
                .param("message", "")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        
        // 打印响应信息
        System.out.println("响应状态: " + result.getResponse().getStatus());
        System.out.println("响应内容类型: " + result.getResponse().getContentType());
        System.out.println("流式响应请求处理成功");
        System.out.println("注意：流式响应是异步的，完整内容需要在实际应用中验证");
        System.out.println("=====================================\n");
    }

    @Test
    void testGenerate_ChineseMessage() throws Exception {
        // 测试中文消息
        System.out.println("=== 测试中文消息 ===");
        System.out.println("请求消息: 请用中文回答：什么是人工智能？");
        
        MvcResult result = mockMvc.perform(get("/ai/generate")
                .param("message", "请用中文回答：什么是人工智能？")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Generated successfully"))
                .andExpect(jsonPath("$.generation").exists())
                .andReturn();
        
        // 打印响应结果
        String responseContent = result.getResponse().getContentAsString();
        System.out.println("响应状态: " + result.getResponse().getStatus());
        System.out.println("响应内容: " + responseContent);
        System.out.println("=====================================\n");
    }

    @Test
    void testGenerate_EnglishMessage() throws Exception {
        // 测试英文消息
        System.out.println("=== 测试英文消息 ===");
        System.out.println("请求消息: What is artificial intelligence?");
        
        MvcResult result = mockMvc.perform(get("/ai/generate")
                .param("message", "What is artificial intelligence?")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Generated successfully"))
                .andExpect(jsonPath("$.generation").exists())
                .andReturn();
        
        // 打印响应结果
        String responseContent = result.getResponse().getContentAsString();
        System.out.println("响应状态: " + result.getResponse().getStatus());
        System.out.println("响应内容: " + responseContent);
        System.out.println("=====================================\n");
    }

    @Test
    void testGenerateStream_LongMessage() throws Exception {
        // 测试长消息的流式生成
        System.out.println("=== 测试长消息的流式生成 ===");
        String longMessage = "请详细解释什么是机器学习，包括监督学习、无监督学习和强化学习的区别，并举例说明每种学习方式的应用场景。";
        System.out.println("请求消息: " + longMessage);
        System.out.println("消息长度: " + longMessage.length() + " 字符");
        
        MvcResult result = mockMvc.perform(get("/ai/generateStream")
                .param("message", longMessage)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        
        // 打印响应信息
        System.out.println("响应状态: " + result.getResponse().getStatus());
        System.out.println("响应内容类型: " + result.getResponse().getContentType());
        System.out.println("流式响应请求处理成功");
        System.out.println("注意：流式响应是异步的，完整内容需要在实际应用中验证");
        System.out.println("=====================================\n");
    }

    @Test
    void testGenerateStream_HTTPAsyncProcessing() throws Exception {
        // 通过 HTTP 接口测试流式响应的异步处理
        System.out.println("=== 通过 HTTP 接口测试流式响应的异步处理 ===");
        String testMessage = "HTTP异步流式测试";
        System.out.println("请求消息: " + testMessage);
        
        // 通过 HTTP 接口发起流式请求
        MvcResult result = mockMvc.perform(get("/ai/generateStream")
                .param("message", testMessage)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        
        System.out.println("HTTP流式请求状态: " + result.getResponse().getStatus());
        System.out.println("HTTP流式请求内容类型: " + result.getResponse().getContentType());
        System.out.println("HTTP流式异步处理测试完成");
        System.out.println("注意：完整的流式内容需要在实际的 HTTP 客户端中验证");
        System.out.println("=====================================\n");
    }
}
