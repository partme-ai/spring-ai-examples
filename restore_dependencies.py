#!/usr/bin/env python3
"""
恢复文档中的Maven依赖更新
将错误的依赖格式替换为正确的格式
"""

import os
import re
from pathlib import Path

DOCS_DIR = Path("/home/wandl/workspaces/workspace-partme-ai/spring-ai-examples/docs/2-Spring AI 入门实践")

# 依赖映射：旧格式 -> 新格式
DEPENDENCY_MAPPINGS = [
    # Ollama 相关
    (r'<artifactId>spring-ai-ollama-spring-boot-starter</artifactId>',
     '<artifactId>spring-ai-starter-model-ollama</artifactId>'),
    
    # OpenAI 相关
    (r'<artifactId>spring-ai-openai-spring-boot-starter</artifactId>',
     '<artifactId>spring-ai-starter-model-openai</artifactId>'),
    
    # Azure OpenAI
    (r'<artifactId>spring-ai-azure-openai-spring-boot-starter</artifactId>',
     '<artifactId>spring-ai-starter-model-azure-openai</artifactId>'),
    
    # Anthropic
    (r'<artifactId>spring-ai-anthropic-spring-boot-starter</artifactId>',
     '<artifactId>spring-ai-starter-model-anthropic</artifactId>'),
    
    # Stability AI
    (r'<artifactId>spring-ai-stability-ai-spring-boot-starter</artifactId>',
     '<artifactId>spring-ai-starter-model-stability-ai</artifactId>'),
    
    # Mistral AI
    (r'<artifactId>spring-ai-mistral-ai-spring-boot-starter</artifactId>',
     '<artifactId>spring-ai-starter-model-mistral-ai</artifactId>'),
    
    # MiniMax
    (r'<artifactId>spring-ai-minimax-spring-boot-starter</artifactId>',
     '<artifactId>spring-ai-starter-model-minimax</artifactId>'),
    
    # 向量数据库依赖
    (r'<artifactId>spring-ai-chroma-store</artifactId>',
     '<artifactId>spring-ai-starter-vector-store-chroma</artifactId>'),
    
    (r'<artifactId>spring-ai-milvus-store</artifactId>',
     '<artifactId>spring-ai-starter-vector-store-milvus</artifactId>'),
    
    (r'<artifactId>spring-ai-neo4j-store</artifactId>',
     '<artifactId>spring-ai-starter-vector-store-neo4j</artifactId>'),
    
    (r'<artifactId>spring-ai-pgvector-store</artifactId>',
     '<artifactId>spring-ai-starter-vector-store-pgvector</artifactId>'),
    
    (r'<artifactId>spring-ai-pinecone-store</artifactId>',
     '<artifactId>spring-ai-starter-vector-store-pinecone</artifactId>'),
    
    (r'<artifactId>spring-ai-qdrant-store</artifactId>',
     '<artifactId>spring-ai-starter-vector-store-qdrant</artifactId>'),
    
    (r'<artifactId>spring-ai-redis-store</artifactId>',
     '<artifactId>spring-ai-starter-vector-store-redis</artifactId>'),
    
    (r'<artifactId>spring-ai-weaviate-store</artifactId>',
     '<artifactId>spring-ai-starter-vector-store-weaviate</artifactId>'),
    
    (r'<artifactId>spring-ai-cassandra-store</artifactId>',
     '<artifactId>spring-ai-starter-vector-store-cassandra</artifactId>'),
    
    (r'<artifactId>spring-ai-couchbase-store</artifactId>',
     '<artifactId>spring-ai-starter-vector-store-couchbase</artifactId>'),
    
    (r'<artifactId>spring-ai-elasticsearch-store</artifactId>',
     '<artifactId>spring-ai-starter-vector-store-elasticsearch</artifactId>'),
    
    (r'<artifactId>spring-ai-mongodb-store</artifactId>',
     '<artifactId>spring-ai-starter-vector-store-mongodb-atlas</artifactId>'),
    
    (r'<artifactId>spring-ai-opensearch-store</artifactId>',
     '<artifactId>spring-ai-starter-vector-store-opensearch</artifactId>'),
    
    (r'<artifactId>spring-ai-gemfire-store</artifactId>',
     '<artifactId>spring-ai-starter-vector-store-gemfire</artifactId>'),
    
    (r'<artifactId>spring-ai-mariadb-store</artifactId>',
     '<artifactId>spring-ai-starter-vector-store-mariadb</artifactId>'),
    
    (r'<artifactId>spring-ai-oracle-store</artifactId>',
     '<artifactId>spring-ai-starter-vector-store-oracle</artifactId>'),
    
    (r'<artifactId>spring-ai-typesense-store</artifactId>',
     '<artifactId>spring-ai-starter-vector-store-typesense</artifactId>'),
    
    # 通用依赖模式
    (r'<artifactId>spring-ai-(.+)-spring-boot-starter</artifactId>',
     r'<artifactId>spring-ai-starter-model-\1</artifactId>'),
]

def update_dependencies_in_file(file_path):
    """更新单个文件中的依赖"""
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    original_content = content
    updated = False
    
    for old_pattern, new_pattern in DEPENDENCY_MAPPINGS:
        new_content, count = re.subn(old_pattern, new_pattern, content, flags=re.IGNORECASE)
        if count > 0:
            content = new_content
            updated = True
    
    if updated and content != original_content:
        with open(file_path, 'w', encoding='utf-8') as f:
            f.write(content)
        return True
    return False

def main():
    print("=" * 80)
    print("恢复文档中的Maven依赖更新")
    print("=" * 80)
    
    # 获取所有文档
    docs = []
    for item in DOCS_DIR.iterdir():
        if item.is_file() and item.name.endswith('.md'):
            docs.append(item)
    
    print(f"找到 {len(docs)} 个文档")
    
    updated_count = 0
    for doc_path in docs:
        if update_dependencies_in_file(doc_path):
            print(f"✅ 更新: {doc_path.name}")
            updated_count += 1
    
    print("\n" + "=" * 80)
    print(f"✅ 完成！")
    print(f"   更新文档数: {updated_count}")
    print("=" * 80)

if __name__ == "__main__":
    main()
