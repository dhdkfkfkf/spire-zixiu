#!/usr/bin/env python
"""字修的战斗能量球贴图（128×128，九张 + 一次得能的闪光）。

结构照原版 images/ui/topPanel/red 的排法：
  layer1..4   有能量时逐层旋转的底图
  layer1d..4d 没能量时的灰暗版
  base        盖在最上面的铁环与玻璃高光（中间透明，露出数字）
BaseMod 的 CustomEnergyOrb 按 [layer1..4, base, layer1d..4d] 的顺序取图。

用法：python tools/make-energy-orb.py
"""
import math
import os
import random
import sys

from PIL import Image, ImageDraw, ImageFilter

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
OUT = os.path.join(ROOT, "src", "main", "resources", "zixiu", "images", "ui", "energy")

S = 128
C = S / 2.0


def lerp(a, b, t):
    return tuple(int(a[i] + (b[i] - a[i]) * t) for i in range(len(a)))


def disc(inner, outer, radius=60.0, edge=4):
    """实心圆盘：从中心到边缘做渐变，最后几像素羽化。"""
    im = Image.new("RGBA", (S, S), (0, 0, 0, 0))
    d = ImageDraw.Draw(im)
    steps = int(radius)
    for i in range(steps, -1, -1):
        t = i / float(radius)
        col = lerp(inner, outer, t)
        a = 255
        if radius - i < edge:
            a = int(255 * (radius - i) / edge)
        d.ellipse([C - i, C - i, C + i, C + i], fill=col + (a,))
    return im


def glow(color, radius=52.0, peak=230):
    """径向光晕，中心最亮，向外衰减。"""
    im = Image.new("RGBA", (S, S), (0, 0, 0, 0))
    d = ImageDraw.Draw(im)
    for i in range(int(radius), -1, -1):
        t = i / float(radius)
        a = int(peak * (1.0 - t) ** 1.7)
        d.ellipse([C - i, C - i, C + i, C + i], fill=color + (a,))
    return im


def brush_ring(radius, color, seed, dots=260, width=(1.4, 4.2), alpha=(90, 200)):
    """用许多小圆点沿圆周堆出一圈毛笔状的能量环。"""
    im = Image.new("RGBA", (S, S), (0, 0, 0, 0))
    d = ImageDraw.Draw(im)
    rnd = random.Random(seed)
    for i in range(dots):
        a = 2.0 * math.pi * i / dots + rnd.uniform(-0.02, 0.02)
        r = radius + rnd.uniform(-1.8, 1.8)
        x = C + r * math.cos(a)
        y = C + r * math.sin(a)
        w = rnd.uniform(*width)
        d.ellipse([x - w, y - w * rnd.uniform(0.5, 0.95),
                   x + w, y + w * rnd.uniform(0.5, 0.95)],
                  fill=color + (rnd.randint(*alpha),))
    return im.filter(ImageFilter.GaussianBlur(0.6))


def wisps(color, seed, arcs=7, radius=(14, 44), alpha=(70, 150)):
    """几道向内卷的墨痕，转起来像气流。"""
    im = Image.new("RGBA", (S, S), (0, 0, 0, 0))
    d = ImageDraw.Draw(im)
    rnd = random.Random(seed)
    for i in range(arcs):
        start = rnd.uniform(0, 360)
        span = rnd.uniform(40, 130)
        r = rnd.uniform(*radius)
        box = [C - r, C - r, C + r, C + r]
        d.arc(box, start, start + span, fill=color + (rnd.randint(*alpha),),
              width=rnd.randint(3, 7))
    return im


def base_ring():
    """顶部铁环：外圈暗铁 + 两道金线 + 左上玻璃高光，中间留空给数字。"""
    im = Image.new("RGBA", (S, S), (0, 0, 0, 0))
    d = ImageDraw.Draw(im)
    outer, inner = 62.0, 47.0
    steps = int(outer - inner) + 1
    for i in range(steps):
        t = i / float(steps)
        col = lerp((26, 23, 20), (58, 50, 40), t)
        r = outer - i
        d.ellipse([C - r, C - r, C + r, C + r], outline=col + (255,), width=1)
    # 外沿暗边、内侧阴影
    d.ellipse([C - outer, C - outer, C + outer, C + outer], outline=(12, 10, 9, 255), width=2)
    d.ellipse([C - inner, C - inner, C + inner, C + inner], outline=(10, 9, 8, 210), width=2)
    # 金线
    d.ellipse([C - 60, C - 60, C + 60, C + 60], outline=(198, 160, 88, 235), width=2)
    d.ellipse([C - 50, C - 50, C + 50, C + 50], outline=(146, 114, 60, 200), width=1)
    # 不再画「玻璃高光」：之前那两道粗弧加一块实心高光斑落在环面上就是两块灰补丁，
    # 看起来像环破了个洞。铁环本身有两道金线和明暗渐变，已经够了。
    return im


def vfx():
    """获得能量时原版会叠这张闪光。"""
    im = glow((255, 226, 150), radius=52.0, peak=210)
    d = ImageDraw.Draw(im)
    rnd = random.Random(7)
    for i in range(10):
        a = rnd.uniform(0, math.tau)
        r0, r1 = 8, rnd.uniform(38, 58)
        d.line([C + r0 * math.cos(a), C + r0 * math.sin(a),
                C + r1 * math.cos(a), C + r1 * math.sin(a)],
               fill=(255, 236, 180, rnd.randint(40, 110)), width=rnd.randint(1, 3))
    return im.filter(ImageFilter.GaussianBlur(0.6))


def main():
    os.makedirs(OUT, exist_ok=True)

    # 有能量：暗墨底 + 余烬核 + 两圈转动金环 + 墨痕
    parts = [
        disc((44, 18, 14), (24, 14, 12)),
        glow((255, 168, 70), radius=46.0, peak=190),
        # alpha 下限别太低：太低的地方会透出底下的暗盘，看上去像环上又有缺口
        brush_ring(42.0, (238, 176, 78), seed=11, alpha=(150, 225)),
        wisps((255, 196, 110), seed=5),
        wisps((255, 214, 150), seed=23, arcs=6, radius=(30, 50), alpha=(50, 110)),
    ]
    for i, part in enumerate(parts, start=1):
        part.save(os.path.join(OUT, "layer%d.png" % i))

    # 没能量：去色压暗，形状一致，转起来仍然有动感
    dim = [
        disc((40, 40, 44), (24, 24, 28)),
        glow((150, 146, 140), radius=44.0, peak=110),
        brush_ring(42.0, (128, 124, 120), seed=11, alpha=(120, 185)),
        wisps((150, 146, 142), seed=5, alpha=(40, 90)),
        wisps((160, 156, 150), seed=23, arcs=6, radius=(30, 50), alpha=(30, 70)),
    ]
    for i, part in enumerate(dim, start=1):
        part.save(os.path.join(OUT, "layer%dd.png" % i))

    base_ring().save(os.path.join(OUT, "base.png"))
    vfx().save(os.path.join(OUT, "vfx.png"))
    print("energy orb: 9 layers + vfx ->", OUT)
    return 0


if __name__ == "__main__":
    sys.exit(main())
