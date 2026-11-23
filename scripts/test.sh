#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."

JDK="${JDK_HOME:-/c/Users/10797/.jdks/jdk8u512-b09}"
CP="lib/junit-4.13.2.jar;lib/hamcrest-core-1.3.jar"

rm -rf out/test-run
mkdir -p out/test-run/main out/test-run/test

# 字学核心不依赖游戏引擎，单独编译与测试
"$JDK/bin/javac" -encoding UTF-8 -d out/test-run/main \
  $(find src/main/java/zixiu/lexicon -name '*.java')
"$JDK/bin/javac" -encoding UTF-8 -cp "out/test-run/main;$CP" -d out/test-run/test \
  $(find src/test/java/zixiu/lexicon -name '*.java')
"$JDK/bin/java" -Dfile.encoding=UTF-8 -cp "out/test-run/main;out/test-run/test;$CP" \
  org.junit.runner.JUnitCore zixiu.lexicon.ZixiuLexiconTest

# 配方 ↔ 卡牌覆盖：每个 PLAYABLE 的字都必须有卡、已注册、有文案
if command -v python >/dev/null 2>&1; then
  python scripts/audit-recipes.py
  python scripts/format-keywords.py --check
fi
