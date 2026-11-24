# 与 spire-pilot 的兼容

spire-pilot 靠「镜像世界 + 自己生成行动 + 回放」工作。字修的核心动作**不是卡牌**，所以要让求解器
看见「合」「拆」，需要在它那边补一个 bridge，本模组负责提供稳定的调用面。

## 本模组提供：`zixiu.compat.PilotBridge`

全部是静态方法，求解器用反射调用即可，不需要编译期依赖：

| 方法 | 用途 |
|---|---|
| `canFuse(AbstractCard)` | 这张牌能否作为合字材料 |
| `fusionResults(List<AbstractCard>)` | 这组材料能合出的字（已过滤没有卡牌的字） |
| `canSplit(AbstractCard)` | 能否拆（含「墨迹未干」判定） |
| `splitPartNames(AbstractCard)` | 拆出的部件枚举名，如 `REN` / `KOU` |
| `isFresh(AbstractCard)` | 是否墨迹未干 |
| `splitsThisTurn()` / `fusionsThisTurn()` | 本回合计数（斗 / 行 / 阵 需要） |
| `resolveFusion(List<AbstractCard>, String glyph)` | **同步**合字：烧掉材料、结果进手牌、触发遗物与能力 |
| `resolveSplit(AbstractCard)` | **同步**拆字：原牌进消耗堆、部件进手牌、触发能力 |

「同步」是关键：正常 UI 走 `effectList` 的飞牌特效，沙盒里没有渲染循环，牌会永远落不了地。
这两个方法直接把牌写进 `hand`，因此可以安全地在镜像世界里调用。

## spire-pilot 侧需要的四处改动

1. **`search/Action.java`**：加一个 UI 动作工厂（照 `Action.noble(...)` 的写法），
   例如 `Action.zixiu(String label, String kind)`，`kind` 区分 `"fuse"` / `"split"`。
2. **`search/Legal.java`**：在生成合法行动时
   - 手牌里任取 2～3 张满足 `PilotBridge.canFuse` 且 `fusionResults` 非空的组合 → `fuse` 动作；
   - 每张 `canSplit` 的牌 → 一个 `split` 动作。
   组合数在 3 张时最多 C(10,3)=120，建议按材料多重集去重，只保留不同的「部件组合」，
   不必为同一组合的每种排列生成动作。
3. **`mirror/`（新的两个 bridge 类）**：效果照 `FernWand` / `FgoNoble` 的写法，
   在自己身上调用 `PilotBridge.resolveSplit(card)` / `resolveFusion(cards, glyph)`。
   多个结果时（`口+木` → 呆/杏/困/束）要用 `Action.selection(...)` 把选择当成下一步，
   而不是在 bridge 里随意挑一个。
4. **`mirror/Statics`**：把这三个静态计数纳入镜像，否则沙盒会读到真实回合的数值、再写回去：
   - `zixiu.core.FusionTracker.FRESH`（身份集合）
   - `zixiu.core.FusionTracker.splitsThisTurn`
   - `zixiu.core.FusionTracker.fusionsThisTurn`

数据文件（已在游戏目录建好）：`mods/spire-pilot-content/profiles.txt`

```
zixiu | 字修(箓生) | zixiu.
```

## 搜索空间上的注意点

- 合/拆都是**免费且不限次数**，若不设上限，动作空间会膨胀得非常快。合法动作的生成建议限制为
  「本节点只生成一层合/拆」，跨层靠搜索本身展开。
- 「墨迹未干」让同一个牌不能在同回合被合出来又拆掉，这已经切断了最常见的自环；
  但「拆 → 合 → 拆」仍然可能往返，靠 `GameState` 状态去重即可。
- `斗`（本回合奇数次拆解抽 2）与 `行`（回合结束既合过也拆过）依赖 `splitsThisTurn` /
  `fusionsThisTurn`，镜像时必须一起搬，否则这两件遗物的收益在沙盒里永远是 0 或错位。
- `阵` 的格挡在回合结束才加、`皆` 的拆解治疗会改 HP，这两个都会影响同回合的斩杀线判断。

## 0.3.7 起的机制变化与求解器适配

**机制**

- 拆、合产出的牌（部件牌与合出的成品牌）现在带**虚无**（`isEthereal = true`），回合结束若仍在手牌会被消耗。
- 这些牌**不占本回合手牌上限**：即使手牌已有 10 张，拆/合结果仍直接进手牌。

**为什么求解器会炸手牌**

求解器在搜索时会反复调用 `PilotBridge.resolveSplit / resolveFusion`。这两个方法直接操作
`AbstractDungeon.player.hand`（真实世界）。原来手牌 10 张上限把副作用挡住了；去掉上限后，
一次搜索的几百次调用会把手牌瞬间灌满，并导致后续计划里的牌对不上。

**适配结论（1.7.7 起）**

- 搜索期 `AbstractDungeon.player` 本来就是镜像世界，所以不需要额外的沙盒开关或 `*Ids` 纯函数；
  `resolveSplit` / `resolveFusion` 在镜像里执行即可。
- 真正需要注意的只有两点：
  1. **虚无不影响计划身份**：虚无牌当回合结束才消失，计划执行期间身份仍然有效；
  2. **手牌可以超过上限**，由拆/合产出，求解器无需为此做任何适配。
- 多结果已经改成「每个字一个分支」，不再走 mod 的选字界面。

## 给求解器的检查清单（0.3.7）

> **历史章节**：以下是 0.3.7 时的口径。已被下面「1.0.0 求解器适配清单」覆盖的部分——
> 灼烧（改为每回合 −1）、因（改为本次战斗次数）、连书（改为初始遗物）、沓、言灵——以 1.0.0 为准。

按优先级排列，逐条勾掉即可。

### A. 必须镜像的静态字段

- [ ] `DeferredCardInsert.PENDING`、`quiet`
- [ ] `FusionTracker.FRESH`、`splitsThisTurn`、`fusionsThisTurn`
- [ ] `FusionTracker.chain`、`lastWordParts` ← **连书新增，旧文档没有，漏了会算错伤害**
- [ ] 可选：`HandActions.splitting`（只影响 UI 文案）

### B. 机制口径（推演必须按这个算）

- [ ] **拆/合产出的牌是虚无**（`isEthereal = true`）：回合结束若在手牌会被消耗。不要把它们当成
      跨回合资源；计划缓存与「计划里的牌还在不在」校验都要排除虚无牌，或者执行前重读手牌。
- [ ] **拆/合产出的牌不占手牌上限**：手牌可以超过 10 张，求解器无需为此做适配；
      但不要用 `hand.size() <= 10` 做断言。
- [ ] **合字支持 2～3 张材料**，不是固定 2 张。
- [ ] **多结果必须展开成分支**：`口+木` → 呆/杏/困/束，`水+日` → 汨/沓，`口+口` → 回/吕，
      `木+一` → 本/末/未。不要开 mod 的选字界面，直接对每个结果各生成一个分支（1.7.7 已对）。
- [ ] **墨迹未干**：本回合合出来的牌不可拆；`FRESH` 用对象身份（IdentityHashMap）判定，
      镜像时要连 identity 语义一起搬，不能按 `cardId` 去重。
- [ ] **连书**：每打出一张与上一张共享至少一个部件的成品牌，`chain+1`，对所有敌人造成
      `2 × chain` 伤害；不共享则归零。判定依赖 `chain` / `lastWordParts`。
- [ ] **曝光**：受到的伤害 `+X`，**回合结束时 -1**（不是每次受击 -1）。多段攻击不再快速消耗层数。
- [ ] **灼烧**：回合结束失去 X 生命，层数不递减。
- [ ] **因**：每打出一张成品牌，对所有敌人造成「本回合合字次数」点伤害（依赖 `fusionsThisTurn`）。
- [ ] **斗**：每回合第奇数次拆解抽 2；**行**：回合结束若既合过也拆过，下回合 +5 敏捷；
      **都**：见上。这几件遗物都依赖本回合计数。

### C. 动作空间

- [ ] 合/拆是**免费且不限次数**的非卡牌动作，生成时只展开一层，靠搜索跨层，否则空间爆炸。
- [ ] 素材组合建议按**部件多重集**去重，不要为同一组合的排列各生成一个动作。
- [ ] `PilotBridge.resolveSplit / resolveFusion` 写的是当前 `AbstractDungeon.player.hand`；
      求解器在 install 后的镜像窗口里调用（`Solver.expandCore` 先 `install()` 再 `Legal.apply`），
      落点是沙盒手牌，实时手牌不变（S305/S307 每次断言）。

### D. 复位协议

- [ ] **不要调复位**。沙盒是「按窗口复制 + 卸载还原」，进沙盒前调 `FusionTracker.clear()` /
      `DeferredCardInsert.reset()` 清的是**真实**的墨迹未干、计数和连书；而且沙盒必须从实时状态
      开始（本回合合过几次、chain 是几），不是空状态。隔离由镜像负责。
      这两个 `reset()` 只作为应急手段存在。
- [ ] 回合切换时模组自己也会 `FusionTracker.clear()`（`TurnReset`），沙盒不要重复依赖它。

### E. 不要镜像/不要碰的

- [ ] `ZixiuConfig.isolation`：只读配置，影响遗物进入哪个池，与战斗推演无关。
- [ ] `JiuZiBei.visits`：局级事件计数，搜索期间不要递增。
- [ ] `PoolDiagnostics.dumpedAfterInit`：开局自检，写一次文件。
- [ ] `ScreenToast` / `SplitPreview` / `HandActionButton` 是纯 UI，推演时可以整体跳过。
- [ ] `InscriptionRewardPatch`（残碑）挂在 `CombatRewardScreen.setupItemReward`，在战斗推演之外。

### F. 已知坑

- [ ] 手牌瞬间涨到 100+：`PENDING` 未镜像（1.7.7 已修）。
- [ ] 「计划里的牌不在手上了」：虚无牌回合结束消失，或执行过程中手牌被拆合改动——执行前重读。
- [ ] 沙盒里算出的合字收益偏小/偏大：先查 `fusionsThisCombat`（因）、`fusionsThisTurn`、`chain` 有没有镜像。
- [ ] 方案 2 下行动作数算多了：材料是**进消耗堆**的，别当成还能复用。

## 1.0.0 求解器适配清单（待发）

**结论：`PilotBridge` 的方法签名一个都没变，不用重新对接调用面。** 要跟的是「推演口径」
和几个新的静态状态；其中「要镜像的东西」已经打包成一对调用，不用再反射私有字段。

### A. 先接这一对调用（替代反射静态字段）

```java
Object snap = PilotBridge.captureStatics();     // 进沙盒前
// ...在镜像里推演...
((zixiu.compat.PilotStatics) snap).restore();   // 出沙盒后写回
```

`PilotStatics` 快照覆盖：

| 字段 | 含义 |
|---|---|
| `splitsThisTurn` / `fusionsThisTurn` | 本回合拆/合次数（斗、行等依赖） |
| `fusionsThisCombat` | **本次战斗**累计合字次数（因的伤害基数） |
| `fusionMode` | 拆合规则方案 1 / 2（见 C.1） |
| `chain` / `lastWordParts` | 连书状态（与上一张成品牌共享部件的判定）；**跨回合保留** |
| `fresh` | 墨迹未干的牌（**按对象身份**，不能按 cardId 去重） |
| `taWindowOpen` | 沓的曝光翻倍窗口 |
| `yanlingIndex` | 言灵指针 1..9 |
| `yanlingDiscountPending` | 言灵「在」的减费欠账 |

只读状态也都有 getter（反射调用）：`PilotBridge.chain()`、`chainRelic()`、`taCharges()`、
`taWindowOpen()`、`yanlingIndex()`、`yanlingDiscountPending()`、`fusionsThisCombat()`、
`fusionMode()`。

### B. Profiles 名字要改

角色名从 `字修(箓生)` 改成 **`砚秋`**：

```
zixiu | 砚秋 | zixiu.
```

### C. 规则变化（推演必须按这个算）

**C.1 拆合有两种方案（`fusionMode`）**

| | 方案 1（默认） | 方案 2 |
|---|---|---|
| 拆/合产出的牌 | **虚无**（回合结束消失） | 保留 |
| 拆/合用的牌 | **进弃牌堆**（下回合还能摸回） | **进消耗堆** |

- 两种方案都会把用掉的牌移出手牌，区别只在去处（弃牌堆 / 消耗堆），所以不存在「同一张牌反复拆」
  的循环。
- `PilotBridge.resolveFusion / resolveSplit` 已按当前方案执行（材料去处、产出虚无、升级传递
  都和 `HandActions` 同口径），沙盒里只要镜像 `fusionMode`。
- 不要把「材料必烧」写死；方案 1 下材料还在手牌里，可以继续当材料（**没有额外限制**）。

**C.2 墨迹未干**

- 只有**合出来的牌**会进 `FRESH`，两种方案都一样；本回合不能再被拆。
- **拆出来的部件不打墨迹未干**，当回合可以继续拿去合。

**C.3 升级传递**

- 拆：部件的 `upgraded` 与被拆的成品牌一致。
- 合：`升级材料数 ≥ 未升级材料数` → 结果 `upgraded = true`，否则 false
  （2 张 1:1 算升级；3 张 2:1 算升级）。
- 镜像里自己造牌时要照这个设 `upgraded`，否则伤害/格挡会差一截。

**C.4 连书跨回合保留**

- `chain` / `lastWordParts` **不再在回合切换时清零**，只在战斗开始时 `resetCombat()` 归零。
  求解器镜像时不要按回合边界清这两个字段，否则连书收益会少算。
- 连书图标（`ChainPower`）也只在连书断掉（归零）时消失，不再每回合移除。

**C.5 连书变成初始遗物**

- 新遗物 `LianShu`（连书，STARTER，与秃笔一起开局携带），判定
  `player.hasRelic("zixiu:LianShu")`；没有它就不接链。`chain` / `lastWordParts` 语义不变。
- 别在沙盒里把遗物列表清空。

**C.6 沓（Ta）**

- 打出：6(8) 伤害 + 1(2) 层 `TaPower`（玩家 BUFF，`amount` = 还要翻倍的攻击牌数）。
- 打出攻击牌时消耗一层并开窗；窗口内**曝光加成按 2×** 计（只对 `NORMAL` 伤害）；
  换下一张牌关窗，回合结束关窗。
- 漏镜像 `taWindowOpen` 会把这条线的伤害算成 1×。

**C.7 言灵（YanlingPower）**

- 玩家 BUFF，`amount` = 指针 1..9（临/兵/斗/者/皆/阵/列/在/前）。
- 未集齐九字：指针 = 已收集字数，**只在回合开始**触发一次。
- 集齐九字：每张牌先按组成单位数（部件 1 / 单字 1 / 双字 2 / 三字 3）推进指针，再触发；
  数字键 1–9 是 UI，可忽略。
- 「在」= 下一张耗能 ≥1 的牌打出后**返还 1 能量**：镜像 `yanlingDiscountPending` 并在推演里补这 1 点能量。

**C.8 灼烧**

- 回合结束先按层数造成 `HP_LOSS`，**然后层数 −1**（减到 0 移除）。
  旧文档「层数不递减」作废，斩杀线会变。

**C.9 因**

- 伤害 = **本次战斗**累计合字次数（`fusionsThisCombat`），不是本回合。

### D. 卡牌数值变化

| 卡 | 变化 |
|---|---|
| 因 | 本次战斗合字次数（见 C.8） |
| 杏 | 治疗 6（升级 8） |
| 囚 | 升级后**费用 0**，仍给 2 层虚弱/易伤 |
| 沐 | 升级版治疗 4 + 移除自身**全部**负面（未升级只移除 1 个） |
| 夫 | 强化后残血分支 26 → 30 |
| 从 | 单段「4(6) + 每层连书 2(3)」伤害，随 `chain` 变化（`chain` 跨回合保留）；升级同时加基础伤害和每层加成 |
| 秃笔 | 开局三选一改为从 10 个部件里随机抽 3（跨局 RNG，无需镜像状态） |

### E. 不用管

费用球 / 卡框 / 描边 / 立绘 / 死亡图 / 通关动画 / 选角界面按钮（拆合规则按钮只是切
`fusionMode`）/ 残碑与九字碑的发放逻辑（都在战斗推演之外，改的是奖励界面）。

### F. 验收清单

- [ ] `profiles.txt` 名字是「砚秋」
- [ ] 沙盒进出手动调用 `captureStatics()` / `restore()`，且 `fresh` 按对象身份还原
- [ ] 方案 1 / 方案 2 各跑一局推演：材料去留、产出是否虚无、行动作数对得上
- [ ] 「沓 → 攻击」伤害是 2× 曝光；「因」按本次战斗计数
- [ ] 升级传递：拆升级牌得升级部件；2 张 1 升级 1 未升级 → 结果升级

## 直接发给对方的精简版（可整段复制）

> 字修这边有一批改动（版本仍是 1.0.0，还没发），`PilotBridge` 的方法签名没变，不用重对接。要跟的：
>
> 1. **镜像接口换了**：不用再反射私有静态字段，改成
>    `PilotBridge.captureStatics()` → 推演 → `PilotStatics.restore()`，一次覆盖
>    拆/合计数、**本次战斗合字次数**、拆合方案、连书、墨迹未干集合、沓窗口、言灵指针与减费欠账。
>    注意 `fresh` 是**对象身份**语义，不能按 cardId 去重。
> 2. **角色名改成「砚秋」**，`profiles.txt` 里那行同步。
> 3. **拆合有两种方案**（`PilotBridge.fusionMode()`）：方案 1 = 产出虚无 / 用掉的牌进**弃牌堆**；
>    方案 2 = 产出保留 / 用掉的牌进**消耗堆**。两种方案都消耗材料，区别只在去处。
> 4. **墨迹未干只打在合出来的牌上**（两种方案都是），拆出来的部件当回合仍可合。
> 5. **升级传递**：拆 → 部件继承升级；合 → 升级材料数 ≥ 未升级材料数则结果升级。
> 6. **连书成为初始遗物** `LianShu`（没有它不接链）；**连书层数跨回合保留**，
>    只在战斗开始归零，镜像时不要按回合边界清 `chain` / `lastWordParts`。
> 7. **沓**：获得 `TaPower`，窗口内曝光加成 2×（要镜像 `taWindowOpen`）。
> 8. **言灵**：指针 1..9；未集齐只在回合开始触发，集齐后每张牌按组成单位数推进再触发；
>    「在」= 下一张牌返 1 能量（镜像 `yanlingDiscountPending` 并补能量）。
> 9. **灼烧**：回合结束先掉血、**然后层数 −1**（旧文档「不递减」作废）。
> 10. **因**：伤害 = 本次战斗合字次数（新字段 `fusionsThisCombat`）。
> 11. 数值：杏 6(8)、囚 升级后 0 费、沐 升级版清全部负面、夫 强化 30、秃笔开局随机 3 张部件。

### G. 修订记录（求解器反馈后）

- `PilotBridge.resolveFusion / resolveSplit` 之前**没有**实现拆合方案（无条件 `moveToExhaustPile`、
  无条件 `markEthereal`），和 UI 路径不一致。现已补上：
  - 材料/源牌是否进消耗堆看 `ZixiuConfig.consumesMaterials()`
  - 产出是否虚无看 `ZixiuConfig.etherealResults()`
  - 升级传递和 UI 共用 `HandActions.shouldUpgradeResult / upgradeIfPossible`
- 顺手修掉一个重复触发：`resolveFusion` 原来在 `notifyFusion()`（已含 `HePower.onFusion`）之后
  又单独调了一次 `HePower.onFusion()`，会把「合」的收益算两次。
- 方案 1 的材料改为**进弃牌堆**（此前误实现成「留在手上」，会产生反复拆同一张源牌的循环）。
  现在两种方案都消耗材料，求解器按 `fusionMode` 决定是 `moveToDiscardPile` 还是 `moveToExhaustPile`。
  `PilotBridge.resolveFusion / resolveSplit` 已经内置这个分支，求解器只要镜像 `fusionMode`。
- `ZixiuConfig.consumesMaterials()` 已改名 **`exhaustsMaterials()`**（语义：材料是否进消耗堆）。
  如果用反射读过旧名字，换成新名字；或者干脆用 `PilotBridge.fusionMode()` 判断。
