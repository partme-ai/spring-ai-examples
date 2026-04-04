#!/usr/bin/env python3
"""
批量迁移 Spring AI 1.1.4 常见 API 命名（ChatClient、Ollama 选项、ImageModel）。
在 spring-ai-examples 根目录执行: python3 scripts/migrate-spring-ai-114.py
"""
from __future__ import annotations

import pathlib
import re

ROOT = pathlib.Path(__file__).resolve().parents[1]


def migrate_file(path: pathlib.Path) -> bool:
    text = path.read_text(encoding="utf-8")
    orig = text

    text = text.replace(
        "import org.springframework.ai.chat.ChatClient;",
        "import org.springframework.ai.chat.client.ChatClient;",
    )
    text = text.replace(
        "import org.springframework.ai.image.ImageClient;",
        "import org.springframework.ai.image.ImageModel;",
    )
    # 全限定名场景
    text = re.sub(
        r"\borg\.springframework\.ai\.chat\.ChatClient\b",
        "org.springframework.ai.chat.client.ChatClient",
        text,
    )

    # Ollama：嵌入类测试使用 OllamaEmbeddingOptions
    is_embedding_file = "Embedding" in path.name or "OllamaEmbedding" in text[:2000]

    if "import org.springframework.ai.ollama.api.OllamaOptions;" in text:
        text = text.replace(
            "import org.springframework.ai.ollama.api.OllamaOptions;",
            "import org.springframework.ai.ollama.api.OllamaEmbeddingOptions;"
            if is_embedding_file
            else "import org.springframework.ai.ollama.api.OllamaChatOptions;",
        )

    # 将标识符 OllamaOptions 替换为 Chat 或 Embedding 变体
    if "OllamaOptions" in text:
        if is_embedding_file:
            text = text.replace("OllamaOptions", "OllamaEmbeddingOptions")
        else:
            text = text.replace("OllamaOptions", "OllamaChatOptions")

    # Builder：.withModel( -> .model(
    text = re.sub(r"\.withModel\(", ".model(", text)
    text = re.sub(r"\.withTemperature\(", ".temperature(", text)
    text = re.sub(r"\.withTopP\(", ".topP(", text)
    text = re.sub(r"\.withTopK\(", ".topK(", text)
    text = re.sub(r"\.withNumPredict\(", ".numPredict(", text)

    # OllamaChatOptions.create() / OllamaEmbeddingOptions.create()
    text = text.replace("OllamaChatOptions.create()", "OllamaChatOptions.builder().build()")
    text = text.replace("OllamaEmbeddingOptions.create()", "OllamaEmbeddingOptions.builder().build()")

    if text != orig:
        path.write_text(text, encoding="utf-8")
        return True
    return False


def main() -> None:
    changed = 0
    for p in ROOT.rglob("*.java"):
        if migrate_file(p):
            changed += 1
    print(f"migrate-spring-ai-114: updated {changed} files under {ROOT}")


if __name__ == "__main__":
    main()
