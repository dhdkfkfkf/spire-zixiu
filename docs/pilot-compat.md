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
- [ ] 沙盒里算出的合字收益偏小/偏大：先查 `fusionsThisTurn` 与 `chain` 有没有镜像。
