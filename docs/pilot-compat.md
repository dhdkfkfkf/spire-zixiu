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
