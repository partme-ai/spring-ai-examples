# Spring AI 项目实践

> **定位**：端到端业务向示例（NL2SQL、推荐、与真实模块编排），强调可运行与落地边界。**无本仓库代码**的条目仅作规划或 wiki 外链，不标为「已落地」。**名称**按课程列表用语（中文）。

## 文档与名称

| 序号 | 名称 | 文档 / 说明 |
|------|------|-------------|
| 15 | Spring AI 应用实战（自然语言转 SQL、酒店推荐等综合案例） | [spring-ai-application-practice.md](./spring-ai-application-practice.md) |

## 本仓库可运行模块

| 名称 | 代码路径 | 说明 |
|------|----------|------|
| Spring AI 应用实战（自然语言转 SQL、酒店推荐等综合案例） | [`spring-ai-sql`](../../spring-ai-sql/)、[`spring-ai-project-hotel-recommend`](../../spring-ai-project-hotel-recommend/) | 详见第 15 篇 |
| Spring AI 与 PostgresML（可选扩展） | [`spring-ai-postgresml`](../../spring-ai-postgresml/) | 可与第 15 篇对照阅读 |

## 网关与大型工程（monorepo 内、非 `spring-ai-examples` 子路径）

| 名称 | 代码路径 | 说明 |
|------|----------|------|
| Spring AI Gateway（开源项目） | `spring-ai-gateway/`（工作空间根目录） | LLM API 网关；与教程示例分离 |

## 待建设或仅外部参考（无 `spring-ai-examples` 专用模块）

| 名称 | 状态 | 备注 |
|------|------|------|
| 基于 Spring AI + 延迟消息 + Tools 实现定时 Tools 调用 | 待建设 | 需独立示例或引用外部仓库后再挂链接 |
| 基于 Spring AI 的 小学生成长分析实现 | 外链 / 待建设 | 见团队 wiki 或产品文档 |
| 基于 Spring AI 的 综合评价 - 教师/家长寄语 | 外链 / 待建设 | 同上 |

## 与系列关系

- 第 1–14 篇见 **[2-Spring AI 入门实践](../2-Spring%20AI%20入门实践/README.md)**。
- 第 15 篇承接第 14 篇（Fine-tuning）的「上一篇 / 下一篇」导航。
