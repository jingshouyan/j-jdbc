#!/bin/bash
# j-jdbc 课程构建脚本
# 用法: bash build.sh
# 将 _base.html 中的占位符替换为实际内容，生成 index.html

set -e

DIR="$(cd "$(dirname "$0")" && pwd)"

# 读取 _base.html
BASE=$(cat "$DIR/references/_base.html")
FOOTER=$(cat "$DIR/references/_footer.html")

# 收集所有模块
MODULES=""
for f in $(ls "$DIR/modules/"*.html 2>/dev/null | sort); do
  MODULES="$MODULES$(cat "$f")"
done

# 替换占位符
RESULT="${BASE//MODULE_CONTENT/$MODULES}"
RESULT="${RESULT//FOOTER_CONTENT/$FOOTER}"

echo "$RESULT" > "$DIR/index.html"
echo "✅ 课程已生成: $DIR/index.html"
