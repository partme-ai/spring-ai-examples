## spring-ai-common

> 父工程下的 **共享工具与常量库**（无独立 Spring Boot 应用），供各 `spring-ai-*` 示例模块依赖。

## 官方文档对应

- [Spring AI Reference — Introduction](https://docs.spring.io/spring-ai/reference/index.html)
- 本模块本身不直接绑定某一模型能力；请以引用它的业务模块 README 为准。

## 使用方式

- 在其他模块 `pom.xml` 中依赖：

```xml
<dependency>
  <groupId>com.github.partmeai</groupId>
  <artifactId>spring-ai-common</artifactId>
  <version>${revision}</version>
</dependency>
```

版本 `${revision}` 与父工程 `spring-ai-examples` 一致。
