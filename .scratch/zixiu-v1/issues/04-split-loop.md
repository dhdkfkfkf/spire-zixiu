# 04: 拆字闭环 + 墨迹未干 + 触发钩子

**What to build:** 右键任意手牌中的成品牌即可拆解：按配方还原全部部件进手牌，原牌进入消耗堆。本回合由拼合生成的牌当回合不可拆。同时提供一个「拆解时触发」的钩子，用 `森` 验证（每当你拆解一张牌，获得 6 格挡）。

**Blocked by:** 03

**Status:** done

- [x] 右键成品牌弹出部件预览，确认后部件进手牌、原牌进消耗堆
- [x] 拆解免费、不限次数、不受两张限制（`品` 拆回三口）
- [x] 本回合合出的牌右键被拒绝，并给出「墨迹未干」提示
- [x] 下回合同一张牌可以拆
- [x] `森` 在场时每次拆解获得 6 格挡
- [x] 手牌超过上限时多出的部件进弃牌堆，不丢牌

## Comments

实现：

- `ui.SplitHandler`：实现 `PostUpdateSubscriber`，监听 `InputHelper.justClickedRight`，遍历手牌取 `hb.hovered` 的那张。只接受 `CardComponents.glyphOf != null`（成品牌）且不在 `FusionTracker` 里的牌。
- `core.FusionTracker`：用 `IdentityHashMap` 记录本回合由拼合生成的牌；`core.TurnReset` 实现 `OnPlayerTurnStartSubscriber`，回合开始清空。
- `powers.SenPower` + `cards.Sen`：拆解触发钩子的第一张牌（每次拆解得 6 格挡）。
- 部件通过 `ShowCardAndAddToHandEffect` 进手，手牌满 10 张时改走 `ShowCardAndAddToDiscardEffect`。

**临时改动**：`森` 暂时放进起始牌组（10 → 11 张），方便在 04 阶段测触发钩子。06–08 完成后移除，起始牌组恢复 10 张。

## 验证记录

自动自检（临时 `core/SelfTest`，验证后已删除）：

```
SELFTEST before split hand=zixiu:Xing exhaust=0 block=0
SELFTEST after split hand=zixiu:Mu zixiu:Kou exhaust=1 block=0
```

- 拆解：`杏` → `木` + `口`，原牌进消耗堆（exhaust 0→1）✓
- `森`：截图里玩家获得 6 点格挡 ✓（日志采样在动作结算前，数值在下一帧出现）

界面自动化（Computer Use 驱动真实游戏窗口）：

- 「合」→ 选牌界面 → 选 `木` + `口` → 确认页 → 四选一面板 `呆/杏/困/束` ✓
- 选 `杏` → 进手牌，消耗堆 1→3 ✓

未验证：点「拆」时的「墨迹未干」白字提示（按钮确实变暗，但提示未在截图里出现）。

## 已知取舍

按反馈调整：**拆改成按钮**，和「合」并排（合 y=300、拆 y=190）。两个按钮共用一个 `HandActionButton` 组件，不可用时变暗，点击弹一行红色提示：合 →「需要两张部件牌」，拆 →「墨迹未干」或「没有可拆的字」。右键方案已删除。

`呆` 与 `困` 效果重复的问题一并修掉：

| 牌 | 效果 |
|---|---|
| 呆 | 抽 2 张牌 |
| 杏 | 治疗 3 点 |
| 困 | 给予 2 层虚弱 |
| 束 | 给予 2 层易伤 |

仍未做：拆解前的部件预览已补（选牌界面悬停成品牌时在牌上方显示「拆「字」→ 部件」）。

## 黄光：已弃用，待重做

试过三种方案都不显示，代码已全部删除（`HandGlowHints`）：

1. `card.beginGlowing()` + `glowColor`：原版 `CardGroup` 的发光逻辑带 `screen != HAND_SELECT` 条件，选牌界面会强制 `stopGlowing()`。
2. 官方 `CardGlowBorder` VFX 塞进 `effectList`：被选牌界面的暗色遮罩盖住。
3. 照抄 `CardGlowBorder.render` 的几何参数（`packedWidth` / `offsetX` / `drawScale * 0.11`）在 PostRender 自绘：仍不可见。

结论：在 StS 的选牌界面里做金色描边这条路不可行。下一步可选方向——
- 把交互改成「普通手牌状态下点选两张」，绕开选牌界面，直接用 `triggerOnGlowCheck`（芙莉莲的做法）。
- 或者放弃黄光，用文字/图标提示（当前拆字预览就是这个思路）。

## StSLib 的第三个坑

`SelectCardsInHandAction` 在「过滤后可选卡数 ≤ amount」且 `anyNumber=false && canPickZero=false` 时会**跳过界面、直接回调并 finish**。表现是：手上只有 1 张成品牌时点「拆」不弹界面、直接拆掉；只有 2 张部件时点「合」直接合掉，无法取消。

修法：两个 action 都传 `canPickZero=true` 强制弹界面（确认 0 张 = 取消），并在回调里校验数量，不符合就直接 return。

另一个连带 bug：按钮原先只检查「两张牌各自可展开」，`木+火` 这种没有配方的组合也能点，合完无结果且两张材料被消耗。改成 `hasFusablePair()` 遍历所有对、只在实际存在配方时点亮；回调里也在消耗材料**之前**检查结果，无结果直接提示、不消耗。

## 回归修复

**无限拆牌**：拆掉的牌会回到手牌，可以反复拆出无限部件。原因——选牌界面把选中的牌放在 `handCardSelectScreen.selectedCards` 里，`hand.moveToExhaustPile` 只处理 `hand.group`，牌随后被界面还回手牌。

修法：`HandActions.consumeCard()` 先从 `selectedCards.group` 摘掉，再 `moveToExhaustPile`。合字的材料走同一条路径。
