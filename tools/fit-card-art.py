#!/usr/bin/env python
"""把 500x380 的卡图成品落进 mod 资源目录。

每张牌需要两份：
  zixiu/images/cards/<ID>.png     250x190（手牌/图鉴用）
  zixiu/images/cards/<ID>_p.png   500x380（卡牌大图）

用法：
  python tools/fit-card-art.py <大图目录> [--only Id1,Id2]
"""
import argparse
import os
import sys

from PIL import Image

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
OUT = os.path.join(ROOT, "src", "main", "resources", "zixiu", "images", "cards")

SMALL = (250, 190)
LARGE = (500, 380)


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("src")
    ap.add_argument("--only", default="")
    opt = ap.parse_args()
    only = {x.strip() for x in opt.only.split(",") if x.strip()}
    os.makedirs(OUT, exist_ok=True)

    count = 0
    for name in sorted(os.listdir(opt.src)):
        if not name.lower().endswith(".png"):
            continue
        card_id = os.path.splitext(name)[0]
        if only and card_id not in only:
            continue
        im = Image.open(os.path.join(opt.src, name)).convert("RGB")
        im.resize(LARGE, Image.LANCZOS).save(os.path.join(OUT, card_id + "_p.png"))
        im.resize(SMALL, Image.LANCZOS).save(os.path.join(OUT, card_id + ".png"))
        count += 1
    print("fitted %d cards -> %s" % (count, OUT))
    return 0


if __name__ == "__main__":
    sys.exit(main())
