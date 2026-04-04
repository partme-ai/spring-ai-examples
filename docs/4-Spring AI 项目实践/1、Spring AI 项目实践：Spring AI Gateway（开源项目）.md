# Spring AI 项目实践：Spring AI Gateway（开源项目）

## 概述

Spring AI Gateway 是一个开源的 AI 网关项目，提供了路由、限流、监控等功能，帮助开发者更方便地管理和使用 AI 模型。它作为 AI 服务的统一入口，简化了 AI 模型的调用和管理。

## 核心功能

### 1. 路由转发

Spring AI Gateway 支持将请求路由到不同的 AI 模型服务，实现模型的动态切换和负载均衡。

### 2. 限流控制

通过集成 Guava 等限流库，Spring AI Gateway 可以控制 API 的调用频率，防止系统过载。

### 3. 监控与可观察性

集成 Prometheus 和 LangFuse，提供详细的监控指标和追踪信息。

### 4. 配置管理

支持从配置文件或配置中心读取配置，实现动态配置更新。

### 5. 向量数据库支持

集成多种向量数据库，支持 RAG 应用场景。

## 项目结构

```
spring-ai-gateway/
├── spring-ai-gateway-router-spring-boot-starter/   # 路由转发模块
├── spring-ai-gateway-vector-spring-boot-starter/   # 向量数据库支持
├── spring-ai-gateway-rateLimit-guava-spring-boot-starter/  # 本地限流
├── spring-ai-gateway-config-spring-boot-starter/  # 配置读取
├── spring-ai-gateway-langfuse-spring-boot-starter/  # Langfuse 监控
├── spring-ai-gateway-prometheus-spring-boot-starter/  # Prometheus 监控
├── spring-ai-gateway-server/  # 网关服务
└── pom.xml
```

## 快速开始

### 1. 添加依赖

在 `pom.xml` 文件中添加以下依赖：

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-gateway-server</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-gateway-router-spring-boot-starter</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-gateway-rateLimit-guava-spring-boot-starter</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-gateway-prometheus-spring-boot-starter</artifactId>
    </dependency>
</dependencies>
```

### 2. 配置网关

在 `application.properties` 文件中配置网关相关设置：

```properties
# 网关配置
spring.ai.gateway.server.port=8080

# 路由配置
spring.ai.gateway.router.routes[0].id=openai
spring.ai.gateway.router.routes[0].uri=https://api.openai.com
spring.ai.gateway.router.routes[0].predicates[0]=Path=/openai/**

spring.ai.gateway.router.routes[1].id=ollama
spring.ai.gateway.router.routes[1].uri=http://localhost:11434
spring.ai.gateway.router.routes[1].predicates[0]=Path=/ollama/**

# 限流配置
spring.ai.gateway.ratelimit.guava.enabled=true
spring.ai.gateway.ratelimit.guava.rate=10
spring.ai.gateway.ratelimit.guava.capacity=20

# 监控配置
spring.ai.gateway.prometheus.enabled=true
```

### 3. 启动网关

创建主应用类：

```java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringAiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringAiGatewayApplication.class, args);
    }

}
```

## 核心模块

### 1. 路由转发模块

```java
import org.springframework.ai.gateway.router.RouterFunctionConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class GatewayConfig {

    @Bean
    public RouterFunction<ServerResponse> routerFunction(RouterFunctionConfig routerFunctionConfig) {
        return routerFunctionConfig.routerFunction();
    }

}
```

### 2. 限流模块

```java
import org.springframework.ai.gateway.ratelimit.guava.GuavaRateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RateLimitConfig {

    @Bean
    public GuavaRateLimiter guavaRateLimiter() {
        return new GuavaRateLimiter(10, 20);
    }

}
```

### 3. 监控模块

```java
import org.springframework.ai.gateway.prometheus.PrometheusMetricsConfig;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Configuration;

@Configuration
@Import(PrometheusMetricsConfig.class)
public class MetricsConfig {

}
```

## 测试方法

1. **启动网关**：运行 `SpringAiGatewayApplication` 类
2. **测试路由转发**：
   ```bash
   curl -X POST http://localhost:8080/openai/v1/chat/completions \
     -H "Content-Type: application/json" \
     -H "Authorization: Bearer your-api-key" \
     -d '{"model": "gpt-3.5-turbo", "messages": [{"role": "user", "content": "Hello"}]}'
   ```
3. **测试限流**：快速发送多个请求，观察限流效果
4. **查看监控指标**：
   ```bash
   curl http://localhost:8080/actuator/prometheus
   ```

## 最佳实践

1. **路由配置**：根据业务需求配置合适的路由规则
2. **限流设置**：根据系统承载能力设置合理的限流参数
3. **监控告警**：为关键指标设置告警，及时发现问题
4. **安全配置**：配置适当的认证和授权机制
5. **高可用性**：部署多个网关实例，实现负载均衡

## 相关资源

- [Spring AI Gateway 项目](../../../spring-ai-gateway/)
- [Spring AI 官方文档](https://spring.io/projects/spring-ai)
- [Prometheus 官方文档](https://prometheus.io/docs/)
- [Guava RateLimiter](https://guava.dev/releases/snapshot-jre/api/docs/com/google/common/util/concurrent/RateLimiter.html)

## 部署建议

1. **容器化部署**：使用 Docker 容器化部署网关
2. **Kubernetes 集成**：在 Kubernetes 集群中部署，实现自动扩缩容
3. **配置中心**：使用 Spring Cloud Config 或 Consul 管理配置
4. **服务发现**：集成 Eureka 或 Consul 实现服务发现
5. **日志管理**：集成 ELK 或 Loki 实现日志收集和分析