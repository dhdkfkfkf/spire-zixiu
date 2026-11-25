#!/usr/bin/env python
"""字修自己的能力图标（血条下方那排小圆标）。

只给「原版没有对应效果」的能力做图标；能用原版图标的（灼烧=中毒、曝光=易伤、
本=多层护甲、行=敏捷、仙=无实体、桂=余像）直接在 Java 里 loadRegion 原版区域。

统一版式：暗墨圆底 + 类型色外圈（增益金 / 减益朱）+ 顶部弧光。
有 AI 底图（dist/art-powers/src/<Key>.png，ComfyUI 生成）就用底图裁成圆形；
没有就退回金色汉字。输出 96×96，代码里按 48 绘制，留 2 倍余量。

用法：
  python tools/make-power-icons.py                # 只用汉字兜底
  python tools/make-power-icons.py --art dist/art-powers/src
"""
import argparse
import os
import sys

from PIL import Image, ImageChops, ImageDraw, ImageFilter, ImageFont

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
OUT = os.path.join(ROOT, "src", "main", "resources", "zixiu", "images", "powers")

SIZE = 96
SS = 4  # 超采样倍数，缩小后线条更干净

FONT_CANDIDATES = [
    r"C:\Windows\Fonts\msyhbd.ttc",
    r"C:\Windows\Fonts\msyh.ttc",
    r"C:\Windows\Fonts\simhei.ttf",
]

# 键名与 Power 类名一致，值 = (兜底用的字, 类型)
ICONS = {
    "Burn": ("灼", "debuff"),
    "Exposure": ("曝", "debuff"),
    "Chain": ("连", "buff"),
    "Cause": ("因", "buff"),
    "Whole": ("全", "buff"),
    "Wang": ("王", "buff"),
    "Flourish": ("昌", "buff"),
    "He": ("合", "buff"),
    "Sen": ("森", "buff"),
    "Jiong": ("炅", "buff"),
    "Zao": ("灶", "buff"),
    "Miao": ("淼", "buff"),
    "Peng": ("朋", "buff"),
    "Jie": ("杰", "buff"),
    "Sky": ("天", "buff"),
    "Ben": ("本", "buff"),
    "Xing": ("行", "buff"),
    "Immortal": ("仙", "buff"),
    "Osmanthus": ("桂", "buff"),
}

RING = {
    "buff": ((214, 176, 96), (120, 94, 48)),
    "debuff": ((204, 88, 62), (108, 42, 32)),
}


def load_font(size):
    for path in FONT_CANDIDATES:
        if os.path.isfile(path):
            return ImageFont.truetype(path, size)
    return ImageFont.load_default()


def dark_disc(d, n, c, r_out):
    for i in range(int(r_out), -1, -1):
        t = i / r_out
        d.ellipse([c - i, c - i, c + i, c + i],
                  fill=(int(36 - 10 * t), int(31 - 9 * t), int(27 - 8 * t), 255))


def rings(d, n, c, r_out, kind):
    ring, ring_dark = RING[kind]
    d.ellipse([c - r_out, c - r_out, c + r_out, c + r_out],
              outline=ring_dark + (255,), width=int(n * 0.035))
    d.ellipse([c - r_out * 0.93, c - r_out * 0.93, c + r_out * 0.93, c + r_out * 0.93],
              outline=ring + (245,), width=int(n * 0.022))
    d.ellipse([c - r_out * 0.80, c - r_out * 0.80, c + r_out * 0.80, c + r_out * 0.80],
              outline=(70, 58, 44, 150), width=max(1, int(n * 0.008)))
    d.arc([c - r_out * 0.86, c - r_out * 0.86, c + r_out * 0.86, c + r_out * 0.86],
          108, 190, fill=(255, 246, 224, 70), width=int(n * 0.026))


def fit_art(art_path, n):
    """AI 底图：按 alpha 紧裁 → 等比缩放进画布（留 6% 边距）→ 居中。"""
    art = Image.open(art_path).convert("RGBA")
    bbox = art.getchannel("A").getbbox()
    if bbox:
        art = art.crop(bbox)
    target = int(n * 0.94)
    scale = min(target / float(art.width), target / float(art.height))
    art = art.resize((max(1, int(art.width * scale)), max(1, int(art.height * scale))),
                     Image.LANCZOS)
    canvas = Image.new("RGBA", (n, n), (0, 0, 0, 0))
    canvas.paste(art, ((n - art.width) // 2, (n - art.height) // 2), art)
    return canvas


def draw_icon(glyph, kind, art_path=None):
    n = SIZE * SS
    c = n / 2.0
    r_out = n * 0.48
    im = Image.new("RGBA", (n, n), (0, 0, 0, 0))
    d = ImageDraw.Draw(im)

    dark_disc(d, n, c, r_out)

    if art_path and os.path.isfile(art_path):
        # AI 底图自带形状（书、印章、日月…），不套圆环，按 alpha 紧裁后居中
        im = fit_art(art_path, n)
        return im.filter(ImageFilter.GaussianBlur(SS * 0.35)).resize((SIZE, SIZE), Image.LANCZOS)
    else:
        font = load_font(int(n * 0.50))
        box = d.textbbox((0, 0), glyph, font=font)
        w, h = box[2] - box[0], box[3] - box[1]
        x = (n - w) / 2.0 - box[0]
        y = (n - h) / 2.0 - box[1] - n * 0.015
        edge = max(1, int(n * 0.022))
        for dx, dy in ((-edge, 0), (edge, 0), (0, -edge), (0, edge),
                       (-edge, -edge), (edge, edge), (-edge, edge), (edge, -edge)):
            d.text((x + dx, y + dy), glyph, font=font, fill=(22, 18, 14, 240))
        d.text((x, y), glyph, font=font, fill=(238, 214, 158, 255))

    rings(d, n, c, r_out, kind)
    return im.filter(ImageFilter.GaussianBlur(SS * 0.35)).resize((SIZE, SIZE), Image.LANCZOS)


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--art", default="", help="ComfyUI 出图目录（按 Key.png 命名）")
    opt = ap.parse_args()
    os.makedirs(OUT, exist_ok=True)

    used_art = 0
    for key, (glyph, kind) in ICONS.items():
        art_path = None
        if opt.art:
            candidate = os.path.join(opt.art, key + ".png")
            if os.path.isfile(candidate):
                art_path = candidate
                used_art += 1
        draw_icon(glyph, kind, art_path).save(os.path.join(OUT, key + ".png"))
    print("power icons: %d（AI 底图 %d）-> %s" % (len(ICONS), used_art, OUT))
    return 0


if __name__ == "__main__":
    sys.exit(main())
