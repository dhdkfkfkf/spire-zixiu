# 03: 合字闭环

**What to build:** 在真实战斗的手牌里选中两张部件牌后出现「合」按钮；只有一个有用结果时直接成型，多个结果时弹选字面板让玩家挑；成品牌立刻进入手牌，当回合可以打出。

**Blocked by:** 01、02

**Status:** done

- [x] 选中两张部件牌后「合」按钮出现；选中非部件牌时不出现
- [x] `口+木` 弹出四选一面板，选择后对应成品牌进入手牌
- [x] 拼合不消耗能量、不限次数
- [x] 没有有用结果的组合不可选中，「合」按钮不出现
- [x] 合出的成品牌当回合可以正常打出

## Comments

实现：

- `ui.FusionButton`：实现 BaseMod 的 `PostUpdateSubscriber` / `PostRenderSubscriber`，每帧画一个「合」方块并处理点击，**不需要字节码 patch**。只在 `AbstractDungeon.screen == NONE` 且手牌里有 2 张可拼牌时显示。
- `actions.ChooseWordAction`：多结果时复用 `CardRewardScreen.customCombatOpen`，轮询 `discoveryCard`，选中后 `ShowCardAndAddToHandEffect` 进手（手牌满则进弃牌堆）。
- `cards.CardComponents`：卡牌 ↔ 字学核心的适配层，`expand(card)` 把卡牌展开成部件多重集。
- 四张闭环样品牌：`呆`（1 费 1 虚弱）、`杏`（1 费治疗 2 抽 1）、`困`（1 费 2 虚弱）、`束`（1 费 2 易伤）。它们先按 COMMON 注册，正式牌池在 05/06–08 里重排。

**一条设计决定**：合的两个输入牌进**消耗堆**。如果进弃牌堆，每次合字都会净增一张牌（2 张材料回堆 + 1 张新牌），牌组会无限膨胀。

## 验证记录

```
fusion button clicked
fusion cards selected: 2
fusion parts=[KOU, MU] options=[呆(PLAYABLE), 杏(PLAYABLE), 困(PLAYABLE), 束(PLAYABLE)]
```

人工确认：按钮可点、二选一选牌正常、`口+木` 弹出四选一、选中牌进手、两张材料进消耗堆。

## 两个 StS 自定义 UI 的坑

1. **模块加载期构造的 `Hitbox` 尺寸会被固化成 0**：那时 `Settings.scale` 还没初始化。宽高必须在每帧 update 里重算，否则判定框是一个点，永远 hover 不到，而绘制用的是当前 scale，看起来完全正常。
2. **`Hitbox.update()` 只把 `clickStarted` 转成 `clicked`，从不自己设置 `clickStarted`**。正确写法：`update()` → `hovered && justClickedLeft` 时置 `clickStarted` → 读 `clicked`。
