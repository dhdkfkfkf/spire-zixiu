# 字修 · Zixiu

《杀戮尖塔 1》的角色 Mod：把汉字拆开、再合起来打。

角色是「落魄书生」——把藏书当弹药的读书人。10 个部件（人 口 木 月 土 一 水 火 山 日）既是 0 费卡牌，也是拼字材料：

- **合**：免费、不限次数，选两张部件牌拼成一个字。
- **拆**：免费、不限次数，把成品牌拆回部件；原牌进消耗堆。
- **墨迹未干**：本回合合出来的字不能当回合拆，杜绝无限循环。

85 个汉字的拆解表是唯一真相来源；一组部件能写出多个字时（`口+木` → 呆 / 杏 / 困 / 束）由选字面板让玩家挑。

## 当前状态

**0.1.1 早期版本**，机制完整可玩，但：

- 全部卡牌共用一个纯色占位卡图，正式美术未接。
- 数值与平衡未经实战验证。

## 安装

1. 安装 [ModTheSpire](https://steamcommunity.com/sharedfiles/filedetails/?id=1605060445)、[BaseMod](https://steamcommunity.com/sharedfiles/filedetails/?id=1605833019)、[StSLib](https://steamcommunity.com/sharedfiles/filedetails/?id=1609158507)。
2. 把 `zixiu.jar` 放进游戏的 `mods/` 目录。
3. 用 ModTheSpire 启动，选角界面翻到最后一个「字修」。

适配游戏版本 12-18-2022（最终版），Java 8。

## 创意工坊

已发布：<https://steamcommunity.com/sharedfiles/filedetails/?id=3844170209>

订阅后仍需在 ModTheSpire 里勾选「字修」并搭好 BaseMod、StSLib。发版流程见 [docs/workshop.md](docs/workshop.md)。

## 构建

需要 JDK 8（游戏只带 JRE）。依赖的 `desktop-1.0.jar`、`ModTheSpire.jar`、`BaseMod.jar`、`StSLib.jar` 都从安装目录读取，不进仓库。

```powershell
powershell -NoProfile -File tools\build.ps1      # 产出 mod\zixiu.jar
powershell -NoProfile -File tools\install.ps1    # 构建并装进游戏 mods\
```

游戏装在别的盘时传 `-GameDir` / `-WorkshopDir`。字学核心的单元测试不依赖游戏：

```bash
bash scripts/test.sh
```

## 文档

- [GLOSSARY.md](GLOSSARY.md)：部件、配方、字序、废字等术语
- [docs/spec-zixiu.md](docs/spec-zixiu.md)：v1 规格（问题 / 用户故事 / 实现决策 / 测试决策）
- [docs/cards-v1.md](docs/cards-v1.md)：64 张成品牌与 10 张部件牌的数值
- [docs/relics-v1.md](docs/relics-v1.md)：遗物、药水、事件
- [docs/assets.md](docs/assets.md)：美术资源尺寸规范
- [docs/asset-todo.md](docs/asset-todo.md)：待补美术清单
- [docs/adr/](docs/adr/)：架构决策记录
- [.scratch/zixiu-v1/issues/](.scratch/zixiu-v1/issues/)：实现票（tracer bullet 切片）

## 许可

尚未确定，见 issue。
