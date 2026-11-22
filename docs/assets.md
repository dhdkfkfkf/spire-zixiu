# 美术资源尺寸

对齐原版素材，改图时按这张表来。

| 资源 | 路径（jar 内） | 尺寸 |
|---|---|---|
| 卡面（手牌） | `zixiu/images/cards/<name>.png` | 250×190 |
| 卡框（小） | `zixiu/images/cardui/bg_*_512.png` | 512×512 |
| 卡框（大） | `zixiu/images/cardui/bg_*_1024.png` | 1024×1024 |
| 遗物 | `images/relics/<id>.png` | 128×128 画布，主体限制在中心 56px，四周留白 |
| 遗物描边 | `images/relics/outline/<id>.png` | 同上 |
| 选角按钮 | `zixiu/images/charSelect/button.png` | 200×200 |
| 选角立绘 | `zixiu/images/charSelect/portrait.png` | 1920×1200 |
| 战斗立绘 | `zixiu/images/char/char.png` | 468×201（透明背景；或改用 Spine .atlas/.json/.png，30fps） |
| 肩部 | `zixiu/images/char/shoulder.png` | 1920×1136 |
| 尸体 | `zixiu/images/char/corpse.png` | 512×512 |
| 事件 | `images/events/<name>.png` | 600×600 |

## 两个路径规则

- **遗物和事件**走 `ImageMaster`，会被自动拼上 `images/relics/` 或 `images/events/` 前缀，所以文件要放在 jar 根的 `images/` 下，构造参数只传文件名（遗物不补扩展名，要写 `"tubi.png"`）。
- **其他资源**直接传 jar 内完整路径（`zixiu/images/...`）。

## 临时借用

选角立绘与按钮暂时取自芙莉莲 Mod（`FrierenModResources/img/char/Frieren/Character_Portrait.png` 与 `Character_Button.png`），定稿后替换。
