# 待补美术清单

卡图按 ID 自动查找：`zixiu/images/cards/<ID>.png`（250×190）+ `<ID>_p.png`（500×380）。文件不存在时自动回退到 `placeholder.png`，所以可以逐张替换，不用改代码。

## 卡图（53 张）

### 基础与部件
| ID | 字 |
|---|---|
| Jin | 击 |
| Shou | 守 |
| Huo | 火 |
| Mu | 木 |
| Kou | 口 |

### 闭环样品
| ID | 字 |
|---|---|
| Dai | 呆 |
| Xing | 杏 |
| Kun | 困 |
| Shu | 束 |
| Sen | 森 |

### 焚脉
| ID | 字 | ID | 字 |
|---|---|---|---|
| Yan | 炎 | Zhong | 众 |
| Can | 灿 | Fen | 焚 |
| Er | 二 | Mie | 灭 |
| Zao | 灶 | Jie | 杰 |
| Da | 大 | Jiong | 炅 |
| Pin | 品 | | |
| YanYan | 焱 | | |
| Beng | 崩 | | |
| Fu | 夫 | | |
| Wang | 王 | | |

### 润脉
| ID | 字 | ID | 字 |
|---|---|---|---|
| Bathe | 沐 | Benevolence | 仁 |
| Blockade | 杜 | Belly | 肚 |
| Grove | 林 | Bland | 淡 |
| Body | 体 | Osmanthus | 桂 |
| Guard | 保 | Hollow | 洼 |
| Drizzle | 淋 | Mound | 垚 |
| Thicket | 丛 | | |

### 照脉
| ID | 字 | ID | 字 |
|---|---|---|---|
| Bright | 明 | Trio | 三 |
| Prosper | 昌 | Forever | 永 |
| Dawn | 旦 | Dim | 杳 |
| Lucky | 吉 | Whole | 全 |
| Fine | 佳 | Sky | 天 |
| But | 但 | | |
| Crystal | 晶 | | |
| Noon | 杲 | | |
| Cause | 因 | | |
| Immortal | 仙 | | |

## 遗物（7 张）

图标 128×128，主体限制在中心 56px；可选大图 256×256（遗物弹窗）。

| 文件 | 名称 | 现状 |
|---|---|---|
| `images/relics/tubi.png` | 秃笔 | 已画（笔杆+笔头） |
| `images/relics/zanyan.png` | 残砚 | 纯色块 |
| `images/relics/huozhezi.png` | 火折子 | 纯色块 |
| `images/relics/jiushuqian.png` | 旧书签 | 纯色块 |
| `images/relics/shuxia.png` | 书匣 | 纯色块 |
| `images/relics/zhubi.png` | 朱笔 | 纯色块 |
| `images/relics/huozipan.png` | 活字盘 | 纯色块 |

每个遗物还需要一份 `images/relics/outline/<id>.png`（同尺寸描边图）。

## 事件（2 张）

600×600。

| 文件 | 事件 |
|---|---|
| `images/events/cuozi.png` | 错字 |
| `images/events/canjuan.png` | 残卷 |

## 药水（3 瓶，暂不需要图）

药水外观目前由原版瓶子按 `PotionColor` 染色，BaseMod 的 `CustomPotion` 没有开放自定义贴图。要做专属瓶子得用反射替换 `containerImg` / `liquidImg` / `hybridImg` / `spotsImg` / `outlineImg` 五个私有贴图，属于额外工程。参考尺寸 64×64。

## 角色

| 文件 | 尺寸 | 现状 |
|---|---|---|
| `zixiu/images/charSelect/portrait.png` | 1920×1200 | 已用真实立绘 |
| `zixiu/images/charSelect/button.png` | 200×200（内容 150×150） | 已用毛笔按钮 |
| `zixiu/images/char/char.png` | 建议高 300px，透明底 | 已用真实立绘（178×300） |
| `zixiu/images/char/shoulder.png` / `shoulder2.png` | 1920×1136，主体仅肩部 | 全透明占位 |
| `zixiu/images/char/corpse.png` | 512×512，透明底 | 简单椭圆占位 |
