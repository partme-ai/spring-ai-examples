# Spring AI 应用实战案例

> **对外标题**：Spring AI 应用实战（自然语言转 SQL、酒店推荐等综合案例）

> 系列第十五篇（文稿文件：`spring-ai-application-practice.md`）。结合 **`spring-ai-sql`**（自然语言 → SQL → 查询结果）与 **`spring-ai-project-hotel-recommend`**（综合入口占位），展示 Spring AI 在 **数据访问** 与 **业务编排** 中的落地方式。可选扩展：仓库内另有 **`spring-ai-postgresml`**，可在掌握 JDBC 与 SQL 示例后自行探索向量与 ML 在 PostgreSQL 侧的集成。

## 目录

- [spring-ai-sql：NL2SQL](#spring-ai-sqlnl2sql)
- [spring-ai-project-hotel-recommend](#spring-ai-project-hotel-recommend)
- [官方文档](#官方文档)
- [安全与合规](#安全与合规)
- [运行与验证](#运行与验证)
- [扩展阅读](#扩展阅读)

## spring-ai-sql：NL2SQL

核心类：`com.github.teachingai.aisql.controller.SqlController`

- 注入 **`ChatClient`**，从 **`classpath:/sql-prompt-template.st`** 构造 **`PromptTemplate`**。  
- 将 **用户问题** 与 **`schema.sql` DDL** 一并传入模型，得到 **SQL 字符串**。  
- **仅当** 生成语句以 `select`（忽略大小写）开头时，使用 **`JdbcTemplate.queryForList`** 执行；否则抛出 **`SqlGenerationException`**，防止随意写操作。

```java
String query = aiClient.prompt(prompt).call().content();
if (query.toLowerCase().startsWith("select")) {
    return new Answer(query, jdbcTemplate.queryForList(query));
}
throw new SqlGenerationException(query);
```

**POST `/sql`**，请求体：`{"question":"..."}`。

## spring-ai-project-hotel-recommend

当前入口为 **`com.github.teachingai.Main`**，为 **占位演示**（打印循环），尚未接入 Spring AI；可作为后续 **酒店推荐等业务** 的聚合模块骨架。扩展时建议：引入 Spring Boot 启动类、领域服务与 `ChatClient`/`VectorStore` 等 Bean，并复用父工程 **Spring AI** 版本。

## 官方文档

- [ChatClient](https://docs.spring.io/spring-ai/reference/api/chatclient.html)  
- [Prompt](https://docs.spring.io/spring-ai/reference/api/prompt.html)

## 安全与合规

NL2SQL 在生产中必须：**权限收敛**、**SQL 审计**、**只读数据源** 或 **受限 Schema**，本示例仅作学习演示。

## 运行与验证

```bash
cd spring-ai-sql
mvn spring-boot:run
```

```bash
curl -X POST "http://localhost:8080/sql" \
  -H "Content-Type: application/json" \
  -d '{"question":"列出所有用户"}'
```

（端口与数据库以模块 `application` 配置为准。）

```bash
cd spring-ai-project-hotel-recommend
mvn -q compile exec:java -Dexec.mainClass="com.github.teachingai.Main"
```

（当前仅验证占位 `main` 输出；与 Spring AI 联调需在模块内新增 Boot 应用与业务代码。）

## 扩展阅读

- 上一篇：[Fine-tuning](../2-Spring%20AI%20入门实践/spring-ai-fine-tuning.md)  
- [docs/README.md](./README.md) 总目录
