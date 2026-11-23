#!/usr/bin/env python
"""从原版卡框图集重绘字修的卡框。

原版把卡框存在 cardui.atlas + cardui*.png 里。这里按 atlas 的 offset 把区域拼回
1024x1024 的完整卡框，再做一层「水墨 + 宣纸」的重绘，保留原版版式（玩家熟悉），
只换材质和配色。输出 BaseMod.addColor 需要的 512/1024 与能量球。

用法： python tools/make-card-frames.py [--game D:\\Steam\\...\\SlayTheSpire]
"""
import argparse
import io
import os
import re
import sys
import zipfile

from PIL import Image, ImageChops, ImageDraw, ImageEnhance, ImageFilter

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
OUT = os.path.join(ROOT, "src", "main", "resources", "zixiu", "images", "cardui")

PAGES = ["cardui.png", "cardui2.png", "cardui3.png", "cardui4.png"]

# 类型 -> 主色（低饱和，压在边框上）
TINTS = {
    "attack": (178, 74, 58),
    "skill": (74, 107, 120),
    "power": (107, 90, 122),
}


def parse_atlas(text):
    """返回 {区域名: (页索引, x, y, w, h, 原宽, 原高, offX, offY)}"""
    regions = {}
    page = -1
    cur = None
    for line in text.splitlines():
        line = line.strip()
        if not line:
            continue
        if line in PAGES:
            page = PAGES.index(line)
            continue
        if line.startswith(("rotate:", "size:", "orig:", "offset:", "index:", "bounds:", "offsets:")):
            key, _, val = line.partition(":")
            val = val.strip()
            if cur is not None:
                if key == "size":
                    cur["w"], cur["h"] = (int(v) for v in val.split(","))
                elif key == "orig":
                    cur["ow"], cur["oh"] = (int(v) for v in val.split(","))
                elif key == "offset":
                    cur["ox"], cur["oy"] = (int(v) for v in val.split(","))
            continue
        if line.startswith("xy:"):
            if cur is not None:
                cur["x"], cur["y"] = (int(v) for v in line[3:].strip().split(","))
            continue
        # 区域名
        if cur is not None:
            regions[cur["name"]] = (cur["page"], cur["x"], cur["y"], cur["w"], cur["h"],
                                    cur["ow"], cur["oh"], cur["ox"], cur["oy"])
        cur = {"name": line, "page": page, "x": 0, "y": 0, "w": 0, "h": 0,
               "ow": 0, "oh": 0, "ox": 0, "oy": 0}
    if cur is not None:
        regions[cur["name"]] = (cur["page"], cur["x"], cur["y"], cur["w"], cur["h"],
                                cur["ow"], cur["oh"], cur["ox"], cur["oy"])
    return regions


def compose(pages, region):
    page_idx, x, y, w, h, ow, oh, ox, oy = region
    crop = pages[page_idx].crop((x, y, x + w, y + h))
    canvas = Image.new("RGBA", (ow, oh), (0, 0, 0, 0))
    canvas.paste(crop, (ox, oy))
    return canvas


def gradient_map(lum, stops):
    """把亮度映射到颜色：stops = [(亮度, (r,g,b)), ...]"""
    r, g, b = Image.new("L", lum.size), Image.new("L", lum.size), Image.new("L", lum.size)
    r_lut, g_lut, b_lut = [], [], []
    for i in range(256):
        # 找到所在区间
        for j in range(len(stops) - 1):
            lo, hi = stops[j], stops[j + 1]
            if lo[0] <= i <= hi[0]:
                t = 0.0 if hi[0] == lo[0] else (i - lo[0]) / float(hi[0] - lo[0])
                r_lut.append(int(lo[1][0] + (hi[1][0] - lo[1][0]) * t))
                g_lut.append(int(lo[1][1] + (hi[1][1] - lo[1][1]) * t))
                b_lut.append(int(lo[1][2] + (hi[1][2] - lo[1][2]) * t))
                break
        else:
            last = stops[-1][1]
            r_lut.append(last[0]); g_lut.append(last[1]); b_lut.append(last[2])
    r.putdata(r_lut); g.putdata(g_lut); b.putdata(b_lut)
    return Image.merge("RGB", (r, g, b))


def restyle(mask_source, kind):
    """原版卡身压暗 + 程序化金线/角饰。素材来自原版图集，版式不会跑偏。"""
    alpha = mask_source.getchannel("A")
    bbox = alpha.getbbox()
    x0, y0, x1, y1 = bbox
    width, height = mask_source.size

    # 压暗、降饱和，并向类型色偏一点
    body = mask_source.convert("RGB")
    body = ImageEnhance.Color(body).enhance(0.55)
    body = ImageEnhance.Brightness(body).enhance(0.62)
    tint = Image.new("RGB", body.size, TINTS[kind])
    body = Image.blend(body, ImageChops.multiply(body, tint), 0.18)

    noise = Image.effect_noise((width, height), 20).convert("L").filter(ImageFilter.GaussianBlur(0.5))
    body = Image.blend(body, ImageChops.overlay(body, Image.merge("RGB", (noise, noise, noise))), 0.25)
    body = body.convert("RGBA")

    overlay = Image.new("RGBA", (width, height), (0, 0, 0, 0))
    d = ImageDraw.Draw(overlay)
    gold = (201, 164, 92, 255)
    ink = (30, 25, 21, 220)
    for inset, line_width, color in ((12, 7, gold), (26, 3, gold), (40, 2, ink)):
        d.rounded_rectangle([x0 + inset, y0 + inset, x1 - inset, y1 - inset],
                            radius=52, outline=color, width=line_width)
    for cx, cy in ((x0 + 40, y0 + 40), (x1 - 40, y0 + 40), (x0 + 40, y1 - 40), (x1 - 40, y1 - 40)):
        d.polygon([(cx, cy - 18), (cx + 18, cy), (cx, cy + 18), (cx - 18, cy)],
                  fill=gold, outline=(60, 45, 32, 255))
    cx = (x0 + x1) // 2
    cy = y0 + 52
    d.polygon([(cx, cy - 13), (cx + 13, cy), (cx, cy + 13), (cx - 13, cy)],
              fill=(150, 46, 38, 235), outline=gold)

    out = Image.alpha_composite(body, overlay)
    out.putalpha(alpha)
    return out


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--game", default=r"D:\Steam\steamapps\common\SlayTheSpire")
    opt = ap.parse_args()

    jar = os.path.join(opt.game, "desktop-1.0.jar")
    if not os.path.isfile(jar):
        sys.exit("找不到 %s" % jar)
    os.makedirs(OUT, exist_ok=True)

    with zipfile.ZipFile(jar) as z:
        atlas = z.read("cardui/cardui.atlas").decode("utf-8", errors="ignore")
        pages = [Image.open(io.BytesIO(z.read("cardui/" + p))).convert("RGBA") for p in PAGES]
        regions = parse_atlas(atlas)

        for kind in ("attack", "skill", "power"):
            big_name = "1024/bg_%s_colorless" % kind
            small_name = "512/bg_%s_colorless" % kind
            if big_name not in regions:
                print("缺少区域", big_name)
                continue
            big = compose(pages, regions[big_name])
            big = restyle(big, kind)
            big.save(os.path.join(OUT, "bg_%s_1024.png" % kind))
            big.resize((512, 512), Image.LANCZOS).save(
                os.path.join(OUT, "bg_%s_512.png" % kind))
            if small_name in regions:
                small = compose(pages, regions[small_name])
                small.resize((512, 512), Image.LANCZOS).save(
                    os.path.join(OUT, "bg_%s_512_vanilla.png" % kind))
            print("frames:", kind)

        # 能量球：原版无色球
        orb = "512/card_colorless_orb"
        if orb in regions:
            ball = compose(pages, regions[orb])
            alpha = ball.getchannel("A")
            rgb = ball.convert("RGB").convert("L")
            tinted = gradient_map(rgb, [(0, (28, 24, 20)), (140, (120, 96, 72)), (255, (245, 232, 200))])
            ball = tinted.convert("RGBA")
            ball.putalpha(alpha)
            ball.save(os.path.join(OUT, "card_orb.png"))
            ball.resize((128, 128), Image.LANCZOS).save(os.path.join(OUT, "big_orb.png"))
            ball.resize((64, 64), Image.LANCZOS).save(os.path.join(OUT, "small_orb.png"))
            print("orbs: ok")
    return 0


if __name__ == "__main__":
    sys.exit(main())
