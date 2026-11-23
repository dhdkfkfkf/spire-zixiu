#!/usr/bin/env python
"""九字真言遗物图：AI 出的器物底 + 字体压字。

底图在 dist/art-relics/src（ComfyUI 生成），这里把真正的汉字用金色压在中央空槽上，
再输出 128×128 正图与描边图。

用法：python tools/make-inscription-art.py
"""
import argparse
import os
import sys

from PIL import Image, ImageDraw, ImageFilter, ImageFont

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
SRC = os.path.join(ROOT, "dist", "art-relics", "src")
OUT = os.path.join(ROOT, "src", "main", "resources", "images", "relics")
OUTLINE = os.path.join(OUT, "outline")

GLYPHS = {
    "lin": "临", "bing": "兵", "dou": "斗", "zhe": "者", "jie": "皆",
    "zhen": "阵", "lie": "列", "qian": "前", "xing": "行", "canbei": "碑",
}

FONT_CANDIDATES = [
    r"C:\Windows\Fonts\msyhbd.ttc",
    r"C:\Windows\Fonts\msyh.ttc",
    r"C:\Windows\Fonts\simhei.ttf",
]


def load_font(size):
    for path in FONT_CANDIDATES:
        if os.path.isfile(path):
            return ImageFont.truetype(path, size)
    return ImageFont.load_default()


def stamp(base, glyph):
    """在器物中央压一个金色汉字。"""
    size = base.size[0]
    image = base.copy()
    font = load_font(int(size * 0.46))
    layer = Image.new("RGBA", image.size, (0, 0, 0, 0))
    d = ImageDraw.Draw(layer)
    box = d.textbbox((0, 0), glyph, font=font)
    w, h = box[2] - box[0], box[3] - box[1]
    x = (size - w) // 2 - box[0]
    y = (size - h) // 2 - box[1] - int(size * 0.02)

    # 先画一层深色描边，保证在浅色器物上也读得清
    for dx, dy in ((-3, 0), (3, 0), (0, -3), (0, 3), (-2, -2), (2, 2), (-2, 2), (2, -2)):
        d.text((x + dx, y + dy), glyph, font=font, fill=(38, 28, 20, 235))
    d.text((x, y), glyph, font=font, fill=(226, 186, 106, 255))

    # 轻微的压印阴影
    shadow = layer.filter(ImageFilter.GaussianBlur(3))
    image = Image.alpha_composite(image, shadow)
    image = Image.alpha_composite(image, layer)
    return image


def outline_of(base):
    """描边图：器物轮廓填白。"""
    alpha = base.getchannel("A")
    white = Image.new("RGBA", base.size, (255, 255, 255, 0))
    white.putalpha(alpha)
    return white


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--src", default=SRC)
    opt = ap.parse_args()
    if not os.path.isdir(opt.src):
        sys.exit("缺少器物底图目录 %s" % opt.src)
    os.makedirs(OUT, exist_ok=True)
    os.makedirs(OUTLINE, exist_ok=True)

    count = 0
    for relic_id, glyph in GLYPHS.items():
        path = os.path.join(opt.src, relic_id + ".png")
        if not os.path.isfile(path):
            print("缺底图:", relic_id)
            continue
        base = Image.open(path).convert("RGBA")
        # 统一成正方形画布，主体居中
        size = max(base.size) + 16
        canvas = Image.new("RGBA", (size, size), (0, 0, 0, 0))
        canvas.paste(base, ((size - base.width) // 2, (size - base.height) // 2), base)
        stamped = stamp(canvas, glyph)
        stamped.resize((128, 128), Image.LANCZOS).save(os.path.join(OUT, relic_id + ".png"))
        outline_of(stamped).resize((128, 128), Image.LANCZOS).save(
            os.path.join(OUTLINE, relic_id + ".png"))
        count += 1
    print("inscription relics: %d" % count)
    return 0


if __name__ == "__main__":
    sys.exit(main())
