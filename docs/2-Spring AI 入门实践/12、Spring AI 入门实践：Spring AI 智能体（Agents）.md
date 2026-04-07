# Spring AI 入门实践：Spring AI 智能体（Agents）

## 什么是智能体

智能体（Agents）是一种能够自主执行任务的 AI 系统，它可以根据用户的指令，利用工具和知识来完成复杂的任务。

## 准备工作

### 1. 添加 Spring AI 依赖

```xml
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-openai</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-agent</artifactId>
</dependency>
```

### 2. 配置 OpenAI API 密钥

在 `application.properties` 中添加：

```properties
spring.ai.openai.api-key=YOUR_API_KEY
```

## 创建智能体

### 1. 定义工具

```java
@Component
public class CalculatorTool implements Tool {
    @Override
    public String getName() {
        return "calculator";
    }

    @Override
    public String getDescription() {
        return "用于执行数学计算";
    }

    @Override
    public Map<String, ParameterSpec> getParameters() {
        Map<String, ParameterSpec> parameters = new HashMap<>();
        parameters.put("expression", ParameterSpec.builder()
            .description("数学表达式，例如 '2 + 2' 或 '3 * 4'")
            .type(ParameterSpec.Type.STRING)
            .required(true)
            .build());
        return parameters;
    }

    @Override
    public Object execute(Map<String, Object> parameters) {
        String expression = (String) parameters.get("expression");
        // 简单的数学计算实现
        try {
            // 这里使用 ScriptEngine 来执行数学表达式
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("JavaScript");
            Object result = engine.eval(expression);
            return "计算结果: " + result;
        } catch (Exception e) {
            return "计算错误: " + e.getMessage();
        }
    }
}
```

### 2. 创建智能体

```java
@Bean
public Agent agent(ChatClient chatClient, List<Tool> tools) {
    return Agent.builder()
        .chatClient(chatClient)
        .tools(tools)
        .build();
}
```

## 基本操作

### 1. 执行任务

```java
@Autowired
private Agent agent;

public void runAgent() {
    String task = "计算 25 * 4 + 100 的结果";
    AgentResponse response = agent.run(task);
    System.out.println("Agent 响应: " + response.getFinalOutput());
}
```

### 2. 多工具使用

```java
@Component
public class WeatherTool implements Tool {
    @Override
    public String getName() {
        return "weather";
    }

    @Override
    public String getDescription() {
        return "用于获取城市的天气信息";
    }

    @Override
    public Map<String, ParameterSpec> getParameters() {
        Map<String, ParameterSpec> parameters = new HashMap<>();
        parameters.put("city", ParameterSpec.builder()
            .description("城市名称")
            .type(ParameterSpec.Type.STRING)
            .required(true)
            .build());
        return parameters;
    }

    @Override
    public Object execute(Map<String, Object> parameters) {
        String city = (String) parameters.get("city");
        // 模拟天气数据
        Map<String, String> weatherData = new HashMap<>();
        weatherData.put("北京", "晴，25°C");
        weatherData.put("上海", "多云，22°C");
        weatherData.put("广州", "雨，28°C");
        
        return weatherData.getOrDefault(city, "未知城市");
    }
}
```

### 3. 执行复杂任务

```java
public void runComplexTask() {
    String task = "北京的天气如何？另外计算一下 123 * 456 的结果";
    AgentResponse response = agent.run(task);
    System.out.println("Agent 响应: " + response.getFinalOutput());
}
```

## 高级特性

### 1. 自定义智能体配置

```java
@Bean
public Agent agent(ChatClient chatClient, List<Tool> tools) {
    return Agent.builder()
        .chatClient(chatClient)
        .tools(tools)
        .withMaxIterations(10)
        .withSystemPrompt("你是一个 helpful 的智能助手，善于使用工具解决问题。")
        .build();
}
```

### 2. 跟踪执行过程

```java
public void runAgentWithTracking() {
    String task = "计算 99 * 99 的结果";
    AgentResponse response = agent.run(task);
    
    System.out.println("最终输出: " + response.getFinalOutput());
    System.out.println("\n执行过程:");
    for (AgentStep step : response.getSteps()) {
        System.out.println("步骤: " + step.getThought());
        System.out.println("工具: " + step.getToolName());
        System.out.println("参数: " + step.getToolArguments());
        System.out.println("结果: " + step.getToolResult());
        System.out.println();
    }
}
```

## 示例代码

完整的示例代码如下：

```java
import org.springframework.ai.agent.Agent;
import org.springframework.ai.agent.AgentResponse;
import org.springframework.ai.agent.AgentStep;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tools.Tool;
import org.springframework.ai.tools.ParameterSpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AgentService {

    @Autowired
    private Agent agent;

    public void demo() {
        // 执行简单任务
        System.out.println("=== 执行简单任务 ===");
        String task1 = "计算 25 * 4 + 100 的结果";
        AgentResponse response1 = agent.run(task1);
        System.out.println("任务: " + task1);
        System.out.println("响应: " + response1.getFinalOutput());
        
        // 执行复杂任务
        System.out.println("\n=== 执行复杂任务 ===");
        String task2 = "北京的天气如何？另外计算一下 123 * 456 的结果";
        AgentResponse response2 = agent.run(task2);
        System.out.println("任务: " + task2);
        System.out.println("响应: " + response2.getFinalOutput());
        
        // 跟踪执行过程
        System.out.println("\n=== 跟踪执行过程 ===");
        String task3 = "计算 99 * 99 的结果";
        AgentResponse response3 = agent.run(task3);
        System.out.println("任务: " + task3);
        System.out.println("最终输出: " + response3.getFinalOutput());
        System.out.println("\n执行步骤:");
        for (int i = 0; i < response3.getSteps().size(); i++) {
            AgentStep step = response3.getSteps().get(i);
            System.out.println("步骤 " + (i + 1) + ":");
            System.out.println("  思考: " + step.getThought());
            System.out.println("  工具: " + step.getToolName());
            System.out.println("  参数: " + step.getToolArguments());
            System.out.println("  结果: " + step.getToolResult());
            System.out.println();
        }
    }
}

// CalculatorTool.java
@Component
class CalculatorTool implements Tool {
    @Override
    public String getName() {
        return "calculator";
    }

    @Override
    public String getDescription() {
        return "用于执行数学计算";
    }

    @Override
    public Map<String, ParameterSpec> getParameters() {
        Map<String, ParameterSpec> parameters = new HashMap<>();
        parameters.put("expression", ParameterSpec.builder()
            .description("数学表达式，例如 '2 + 2' 或 '3 * 4'")
            .type(ParameterSpec.Type.STRING)
            .required(true)
            .build());
        return parameters;
    }

    @Override
    public Object execute(Map<String, Object> parameters) {
        String expression = (String) parameters.get("expression");
        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("JavaScript");
            Object result = engine.eval(expression);
            return "计算结果: " + result;
        } catch (Exception e) {
            return "计算错误: " + e.getMessage();
        }
    }
}

// WeatherTool.java
@Component
class WeatherTool implements Tool {
    @Override
    public String getName() {
        return "weather";
    }

    @Override
    public String getDescription() {
        return "用于获取城市的天气信息";
    }

    @Override
    public Map<String, ParameterSpec> getParameters() {
        Map<String, ParameterSpec> parameters = new HashMap<>();
        parameters.put("city", ParameterSpec.builder()
            .description("城市名称")
            .type(ParameterSpec.Type.STRING)
            .required(true)
            .build());
        return parameters;
    }

    @Override
    public Object execute(Map<String, Object> parameters) {
        String city = (String) parameters.get("city");
        Map<String, String> weatherData = new HashMap<>();
        weatherData.put("北京", "晴，25°C");
        weatherData.put("上海", "多云，22°C");
        weatherData.put("广州", "雨，28°C");
        
        return weatherData.getOrDefault(city, "未知城市");
    }
}
```

## 总结

Spring AI 智能体提供了一种强大的方式来构建能够自主执行任务的 AI 系统。通过集成工具和大语言模型，智能体可以解决复杂的问题，为用户提供更加智能和个性化的服务。

## 相关资源

- [Spring AI 智能体文档](https://docs.spring.io/spring-ai/docs/current/reference/html/index.html#agents)
- [OpenAI 工具调用文档](https://platform.openai.com/docs/guides/function-calling)