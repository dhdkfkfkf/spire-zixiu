#!/usr/bin/env python
"""从原版卡框图集重绘字修的卡框。

原版把卡框存在 cardui.atlas + cardui*.png 里。这里按 atlas 的 offset 把区域拼回
1024x1024 的完整卡框，再做一层「水墨 + 宣纸」的重绘，保留原版版式（玩家熟悉），
只换材质和配色。输出 BaseMod.addColor 需要的 512/1024 卡框和稀有度描边环；
费用球（card_orb / big_orb / small_orb）是手绘素材，不在这里生成。

用法： python tools/make-card-frames.py [--game D:\\Steam\\...\\SlayTheSpire] [--only all|frames|ring]

注意 --only frames 会重新跑噪声，每次产出的底图都不一样；只改描边环时用 --only ring，
免得把已经调好的卡框底图冲掉。
"""
import argparse
import io
import os
import re
import sys
import zipfile

import numpy as np
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

# 稀有度 -> 线框/角饰颜色（对齐原版匾额：普通银灰、罕见青、稀有金）
ACCENTS = {
    "common": (198, 198, 204),
    "uncommon": (86, 186, 214),
    "rare": (201, 164, 92),
    "special": (110, 158, 132),
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


def body_rect(alpha, margin=0.25):
    """卡身矩形：取中间 50% 行列左右/上下边界的中位数。

    原版底图的 alpha 里混着几处零散像素（`1024/bg_attack_colorless` 的 bbox 右边到 828，
    而卡身实际只到 806）。照着 bbox 画线框，右侧金线就被画到了 alpha 外面、整条被裁掉——
    表现就是「左边有金线，右边没有 / 边框没贴合」。
    """
    a = np.asarray(alpha)
    h, w = a.shape
    rows = a[int(h * margin):int(h * (1 - margin)), :] > 8
    lefts = np.argmax(rows, axis=1)
    rights = w - 1 - np.argmax(rows[:, ::-1], axis=1)
    cols = a[:, int(w * margin):int(w * (1 - margin))] > 8
    tops = np.argmax(cols, axis=0)
    bottoms = h - 1 - np.argmax(cols[::-1, :], axis=0)
    return (int(np.median(lefts)), int(np.median(tops)),
            int(np.median(rights)), int(np.median(bottoms)))


def restyle(mask_source, kind, accent, seal=(150, 46, 38, 235)):
    """原版卡身压暗 + 程序化线框/角饰。素材来自原版图集，版式不会跑偏。

    accent 是线框与角饰的颜色，按稀有度取原版匾额的配色：
    普通=银灰、罕见=青、稀有=金。
    """
    alpha = mask_source.getchannel("A")
    x0, y0, x1, y1 = body_rect(alpha)
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
    gold = accent + (255,)
    ink = (30, 25, 21, 220)
    dark_edge = (60, 45, 32, 255)
    for inset, line_width, color in ((8, 7, gold), (22, 3, gold), (36, 2, ink)):
        d.rounded_rectangle([x0 + inset, y0 + inset, x1 - inset, y1 - inset],
                            radius=50, outline=color, width=line_width)
    cx = (x0 + x1) // 2
    cy = y0 + 40
    d.polygon([(cx, cy - 13), (cx + 13, cy), (cx, cy + 13), (cx - 13, cy)],
              fill=seal, outline=gold)

    out = Image.alpha_composite(body, overlay)
    # 原版卡框四角有一块深色缺口，压暗后会变成黑色菱形；按圆角矩形裁掉。
    # inset 8 而不是 10：最外那道金线画在 inset 12，留 4px 余量，缩放后不会被裁掉半条
    # （右边少一条金线就是这么来的）。
    clean = Image.new("L", (width, height), 0)
    ImageDraw.Draw(clean).rounded_rectangle(
        [x0, y0, x1, y1], radius=58, fill=255)
    out.putalpha(ImageChops.multiply(alpha, clean))

    # 卡身挪到画布正中：卡图、费用、描述都是游戏按画布中心摆放的，
    # 原版卡框图在画布里偏了几个像素，不校正就会左右留白一边宽一边窄。
    box = out.getchannel("A").getbbox()
    if box:
        piece = out.crop(box)
        centered = Image.new("RGBA", (width, height), (0, 0, 0, 0))
        centered.paste(piece, ((width - piece.width) // 2, (height - piece.height) // 2))
        return centered
    return out


def make_ring(mask, band=3):
    """稀有度描边环：白色中空轮廓，Java 端按稀有度着色后叠在卡面上。

    mask 是 1024 的卡身（只用它的 alpha，确定卡身圆角矩形的范围）。

    环画在卡身轮廓**外面**（从轮廓向外 band 像素，默认 3px，也就是“超细”那条），卡面上任何东西都不会被压住——
    匾额和费用球就贴在卡顶边缘，画在里面一定会盖到它们。
    先在 1024 空间画，再缩到 512：PIL 的圆角矩形没有抗锯齿，直接画 512 会有锯齿。
    """
    ax0, ay0, ax1, ay1 = mask.getchannel("A").getbbox()
    b = band * 2  # 1024 空间
    ring = Image.new("RGBA", (1024, 1024), (0, 0, 0, 0))
    d = ImageDraw.Draw(ring)
    # 外沿：轮廓按 b 外扩，圆角半径同比放大
    d.rounded_rectangle([ax0 - b, ay0 - b, ax1 + b, ay1 + b],
                        radius=58 + b, fill=(255, 255, 255, 255))
    # 挖掉卡身：只留外面这一圈
    d.rounded_rectangle([ax0, ay0, ax1, ay1], radius=58, fill=(0, 0, 0, 0))
    return ring.resize((512, 512), Image.LANCZOS)


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--game", default=r"D:\Steam\steamapps\common\SlayTheSpire")
    # 注意：card_orb / big_orb / small_orb 是手绘的「墨池朱砂」素材（见 git 历史），
    # 不由本脚本生成——之前的自动生成把它们盖成了黑菱形，别再重蹈覆辙。
    ap.add_argument("--only", choices=("all", "frames", "ring"), default="all")
    opt = ap.parse_args()
    do_frames = opt.only in ("all", "frames")
    do_ring = opt.only in ("all", "ring")

    jar = os.path.join(opt.game, "desktop-1.0.jar")
    if not os.path.isfile(jar):
        sys.exit("找不到 %s" % jar)
    os.makedirs(OUT, exist_ok=True)

    with zipfile.ZipFile(jar) as z:
        atlas = z.read("cardui/cardui.atlas").decode("utf-8", errors="ignore")
        pages = [Image.open(io.BytesIO(z.read("cardui/" + p))).convert("RGBA") for p in PAGES]
        regions = parse_atlas(atlas)

        if do_frames or do_ring:
            big_name = "1024/bg_attack_colorless"
            if big_name not in regions:
                sys.exit("缺少区域 %s" % big_name)
            attack_big = restyle(compose(pages, regions[big_name]), "attack", ACCENTS["rare"])

        if do_frames:
            for kind in ("attack", "skill", "power"):
                big_name = "1024/bg_%s_colorless" % kind
                small_name = "512/bg_%s_colorless" % kind
                if big_name not in regions:
                    print("缺少区域", big_name)
                    continue
                big = attack_big if kind == "attack" else restyle(
                    compose(pages, regions[big_name]), kind, ACCENTS["rare"])
                big.save(os.path.join(OUT, "bg_%s_1024.png" % kind))
                big.resize((512, 512), Image.LANCZOS).save(
                    os.path.join(OUT, "bg_%s_512.png" % kind))
                if small_name in regions:
                    small = compose(pages, regions[small_name])
                    small.resize((512, 512), Image.LANCZOS).save(
                        os.path.join(OUT, "bg_%s_512_vanilla.png" % kind))
                print("frames:", kind)

        if do_ring:
            make_ring(attack_big).save(os.path.join(OUT, "frame_ring.png"))
            print("ring: ok")

    return 0


if __name__ == "__main__":
    sys.exit(main())
