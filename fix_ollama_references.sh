#!/bin/bash

# 批量替换 OllamaChatModel 为 OllamaChatModel

echo "开始修复 OllamaChatModel 引用..."

# 查找所有包含 OllamaChatModel 的 Java 文件
find . -name "*.java" -type f -exec grep -l "OllamaChatModel" {} \; | while read file; do
    echo "修复文件: $file"
    
    # 替换 import 语句
    sed -i '' 's/import org\.springframework\.ai\.ollama\.OllamaChatModel;/import org.springframework.ai.ollama.OllamaChatModel;/g' "$file"
    
    # 替换类型声明
    sed -i '' 's/OllamaChatModel/OllamaChatModel/g' "$file"
done

# 查找所有包含 OllamaChatModel 的 README_anthropic_skills.md 文件
find . -name "README.md" -type f -exec grep -l "OllamaChatModel" {} \; | while read file; do
    echo "修复 README 文件: $file"
    
    # 替换 README 中的引用
    sed -i '' 's/OllamaChatModel/OllamaChatModel/g' "$file"
done

echo "修复完成！"