#!/bin/bash

# Spring AI 文档批量优化脚本
# 基于技术博客标准（technical-blog-doc）优化所有文档

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"
DOCS_DIR="${PROJECT_ROOT}/docs/2-Spring AI 入门实践"

echo "======================================"
echo "Spring AI 文档批量优化脚本"
echo "======================================"
echo ""

# 检查是否在正确的目录
if [ ! -d "$DOCS_DIR" ]; then
    echo "❌ 错误：未找到文档目录 $DOCS_DIR"
    exit 1
fi

echo "📂 文档目录: $DOCS_DIR"
echo ""

# 统计信息
TOTAL_DOCS=$(find "$DOCS_DIR" -name "*.md" -type f | wc -l)
echo "📊 找到 $TOTAL_DOCS 篇文档"
echo ""

# 优化函数
optimize_document() {
    local doc_file="$1"
    local doc_name=$(basename "$doc_file")

    echo "🔧 优化: $doc_name"

    # 备份原文件
    cp "$doc_file" "${doc_file}.backup"

    # 读取文件内容
    content=$(cat "$doc_file")

    # 1. 删除"适用场景"章节（如果内容空泛）
    if echo "$content" | grep -q "### 适用场景"; then
        # 检查是否有具体的应用案例
        if ! echo "$content" | grep -A 10 "### 适用场景" | grep -q "^\*\*"; then
            echo "  ⚠️  发现空泛的'适用场景'，建议改为具体应用案例"
        fi
    fi

    # 2. 检查是否有"性能基准"章节
    if ! echo "$content" | grep -q "## 三、性能基准"; then
        echo "  ⚠️  缺少'性能基准'章节（technical-blog-doc 必需）"
    fi

    # 3. 检查使用示例是否完整
    if ! echo "$content" | grep -q "### 9\.[234]"; then
        echo "  ⚠️  使用示例不完整，建议补充 Java/Python/TypeScript 客户端"
    fi

    # 4. 检查致谢是否具体
    if echo "$content" | grep -q "## 致谢"; then
        if echo "$content" | grep -A 5 "## 致谢" | grep -q "感谢.*团队.*让.*变得如此简单"; then
            echo "  ⚠️  致谢过于空泛，建议具体化"
        fi
    fi

    # 5. 检查是否有 AI 味表达
    if echo "$content" | grep -q "让我们\|我们将\|接下来我们将\|本文将带您"; then
        echo "  ⚠️  发现 AI 味表达，建议去除"
    fi

    echo "  ✅ 检查完成"
    echo ""
}

# 主优化流程
main() {
    local count=0

    echo "开始批量检查文档..."
    echo ""

    # 遍历所有文档
    for doc_file in "$DOCS_DIR"/*.md; do
        if [ -f "$doc_file" ]; then
            optimize_document "$doc_file"
            ((count++))
        fi
    done

    echo "======================================"
    echo "✅ 检查完成！共检查 $count 篇文档"
    echo "======================================"
    echo ""
    echo "📋 后续步骤："
    echo "1. 查看上述警告信息"
    echo "2. 参考 OPTIMIZATION_GUIDE.md 进行优化"
    echo "3. 使用 22 号文档作为优化示例"
    echo "4. 备份文件已保存为 *.backup"
    echo ""
}

# 执行主函数
main

exit 0
