#!/usr/bin/env python3
"""配方 ↔ 卡牌覆盖审计。

检查 ZixiuLexicon 里每个 PLAYABLE 的字是否真的有可合出的卡：
  1. 存在一张卡类的 GLYPH 等于该字；
  2. 该卡类在 CardComponents 里注册（能合出来）；
  3. CardStrings.json 有对应文案。

同时检查 10 个部件都能被 createComponent 造出来。

退出码非 0 表示有缺口，适合放进 CI 或发版前手跑。
"""
import io
import os
import re
import sys

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
CARDS_DIR = os.path.join(ROOT, 'src', 'main', 'java', 'zixiu', 'cards')
LEXICON = os.path.join(ROOT, 'src', 'main', 'java', 'zixiu', 'lexicon', 'ZixiuLexicon.java')
COMPONENTS = os.path.join(CARDS_DIR, 'CardComponents.java')
STRINGS = os.path.join(ROOT, 'src', 'main', 'resources', 'zixiu', 'localization', 'CardStrings.json')

# 击：基础牌「击」（Jin）就是它，但基础牌不可拆也不进配方，所以没有 GLYPH 注册。
BASIC_GLYPHS = {'击'}


def read(path):
    with io.open(path, encoding='utf-8') as handle:
        return handle.read()


def main():
    lexicon = read(LEXICON)
    entries = re.findall(r'add\("(.)",\s*WordRole\.(\w+)', lexicon)
    playable = [glyph for glyph, role in entries if role == 'PLAYABLE']

    glyph_owner = {}
    for name in sorted(os.listdir(CARDS_DIR)):
        if not name.endswith('.java'):
            continue
        source = read(os.path.join(CARDS_DIR, name))
        match = re.search(r'GLYPH\s*=\s*"(.)"', source)
        if match:
            glyph_owner[match.group(1)] = name[:-5]

    registry = read(COMPONENTS)
    registered = set(re.findall(r'register\((\w+)\.GLYPH,', registry))
    component_cases = set(re.findall(r'case\s+(\w+):\s*\n\s*return new', registry))
    strings = read(STRINGS)

    problems = []

    for glyph in playable:
        if glyph in BASIC_GLYPHS:
            continue
        owner = glyph_owner.get(glyph)
        if owner is None:
            problems.append('缺口：%s 是 PLAYABLE，但没有卡类' % glyph)
            continue
        if owner not in registered:
            problems.append('缺口：%s（%s）没有注册进 CardComponents，合不出来' % (glyph, owner))
        if '"zixiu:%s"' % owner not in strings:
            problems.append('缺口：%s（%s）缺 CardStrings 文案' % (glyph, owner))

    components = re.findall(r"([A-Z]+)\('", read(
        os.path.join(ROOT, 'src', 'main', 'java', 'zixiu', 'lexicon', 'Component.java')))
    for component in components:
        if component not in component_cases:
            problems.append('缺口：部件 %s 没有 createComponent 分支' % component)

    print('PLAYABLE 配方：%d，有卡：%d，注册：%d' % (
        len(playable), len(glyph_owner), len(registered)))
    print('部件分支：%d / %d' % (len(component_cases), len(components)))

    if problems:
        print()
        for problem in problems:
            print(problem)
        return 1

    relic_problems = audit_relics()
    if relic_problems:
        print()
        for problem in relic_problems:
            print(problem)
        return 1

    print('OK：所有 PLAYABLE 的字都能合出来，10 个部件都能造卡，遗物文案与图片齐全。')
    return 0


def audit_relics():
    """九字与其它遗物：文案、图片、注册三件套。"""
    relics_dir = os.path.join(ROOT, 'src', 'main', 'java', 'zixiu', 'relics')
    mod = read(os.path.join(ROOT, 'src', 'main', 'java', 'zixiu', 'ZixiuMod.java'))
    strings = read(os.path.join(ROOT, 'src', 'main', 'resources', 'zixiu',
                                'localization', 'RelicStrings.json'))
    art_dir = os.path.join(ROOT, 'src', 'main', 'resources', 'images', 'relics')

    problems = []
    inscription_loop = 'InscriptionRelic.ALL' in mod and 'InscriptionRelic.make' in mod

    for name in sorted(os.listdir(relics_dir)):
        if not name.endswith('.java'):
            continue
        source = read(os.path.join(relics_dir, name))
        if 'abstract class' in source:
            continue
        id_match = re.search(r'public static final String ID = "(\w+)"', source)
        if not id_match:
            continue
        relic_id = id_match.group(1)
        cls = name[:-5]

        if '"zixiu:%s"' % relic_id not in strings:
            problems.append('遗物缺口：%s 缺 RelicStrings' % relic_id)

        registered = ('new %s()' % cls) in mod or (
            inscription_loop and 'class ' + cls in source and 'InscriptionRelic' in source)
        if not registered:
            problems.append('遗物缺口：%s（%s）没有注册' % (relic_id, cls))

        img = re.search(r'super\(ZixiuMod\.makeID\(ID\),\s*"([\w.]+)"', source)
        if img:
            if not os.path.exists(os.path.join(art_dir, img.group(1))):
                problems.append('遗物缺口：%s 缺图 %s' % (relic_id, img.group(1)))
            if not os.path.exists(os.path.join(art_dir, 'outline', img.group(1))):
                problems.append('遗物缺口：%s 缺描边图 %s' % (relic_id, img.group(1)))
    return problems


if __name__ == '__main__':
    sys.exit(main())
