#!/usr/bin/env python3
"""把卡牌描述里的关键词按原版中文的分词规则隔开。

原版中文卡面是空格分词匹配词条的，例如「获得 !B! 点 格挡 。」「施加 !M! 层 易伤 。」。
自定义关键词还要写成带 mod 前缀的唯一名（`zixiu:灼烧`），BaseMod 渲染时会去掉前缀。

用法：python scripts/format-keywords.py [--check]
  默认直接改 CardStrings.json；--check 只报告未隔开的词，退出码 1。
"""
import io
import json
import os
import re
import sys

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
STRINGS = os.path.join(ROOT, 'src', 'main', 'resources', 'zixiu', 'localization', 'CardStrings.json')

# 自定义关键词 -> 带前缀的唯一名
CUSTOM = {
    '灼烧': 'zixiu:灼烧',
    '曝光': 'zixiu:曝光',
}
# 原版中文词典里的关键词（出现在描述里也要隔开）
VANILLA = ['格挡', '消耗', '易伤', '虚弱', '力量', '敏捷', '能量', '保留', '固有']

TOKENS = list(CUSTOM.values()) + VANILLA


def spaced(text, token):
    pattern = r'(?<![A-Za-z0-9_:])' + re.escape(token) + r'(?![A-Za-z0-9_:])'
    return re.sub(pattern, ' ' + token + ' ', text)


def format_text(text):
    for zh, unique in CUSTOM.items():
        text = text.replace(unique, zh)  # 幂等：先去掉已有前缀
    for zh, unique in CUSTOM.items():
        text = text.replace(zh, unique)
    for token in TOKENS:
        text = spaced(text, token)
    text = re.sub(r' {2,}', ' ', text)
    return text.strip()


def unspaced_tokens(text):
    """返回描述里存在、但没有按分词隔开的关键词。带前缀的写法先还原，再统一判断。"""
    normalized = text
    for zh, unique in CUSTOM.items():
        normalized = normalized.replace(unique, zh)

    found = []
    for token in list(CUSTOM.keys()) + VANILLA:
        for match in re.finditer(re.escape(token), normalized):
            before = normalized[match.start() - 1] if match.start() > 0 else ' '
            after = normalized[match.end()] if match.end() < len(normalized) else ' '
            if before != ' ' or after != ' ':
                found.append(token)
                break
    return found


def main():
    check = '--check' in sys.argv
    with io.open(STRINGS, encoding='utf-8') as handle:
        data = json.load(handle)

    changed = 0
    problems = []
    for card_id, value in data.items():
        for key in ('DESCRIPTION', 'UPGRADE_DESCRIPTION'):
            text = value.get(key)
            if not text:
                continue
            missing = unspaced_tokens(text) if check else []
            if missing:
                problems.append('%s: %s' % (card_id, ', '.join(sorted(set(missing)))))
                continue
            fixed = format_text(text)
            if fixed != text:
                if not check:
                    value[key] = fixed
                    changed += 1

    if check:
        if problems:
            for problem in problems:
                print(problem)
            return 1
        print('OK：所有卡牌描述的关键词都已按分词隔开。')
        return 0

    with io.open(STRINGS, 'w', encoding='utf-8') as handle:
        json.dump(data, handle, ensure_ascii=False, indent=2)
        handle.write('\n')
    print('formatted %d descriptions' % changed)
    return 0


if __name__ == '__main__':
    sys.exit(main())
