# 06: 焚脉 15 张

**What to build:** 焚脉的 15 张牌进入奖励池，配套关键词「灼烧」生效：回合结束失去 X 点生命且不递减。玩家能用 `焱` / `焚` 铺烧、用 `灭` 引爆，形成一条完整的铺烧—引爆战斗线。

**Blocked by:** 03、05

**Status:** ready-for-human

- [ ] 灼烧不递减，回合结束按层数扣血
- [ ] `灭` 对有灼烧的目标伤害翻倍并清除灼烧
- [ ] 15 张焚牌按 6/5/4 稀有度进奖励池，费用与效果符合 `docs/cards-v1.md`
- [ ] 一套铺烧 → 引爆的构筑能打赢第一幕精英

## Comments

已实现：

- `powers.BurnPower`：灼烧，`atEndOfRound` 扣血；`BurnPower.apply()` 是统一入口，负责「杰」的额外层数与「王」的格挡触发。
- `powers.ExposurePower`：曝光，`atDamageReceive` 加伤、`onAttacked` 减 1。（照脉会复用）
- `powers.WangPower` / `JiePower` / `JiongPower`：三张能力牌的效果。
- 15 张牌：炎 灿 二 灶 大 品 焱 崩 夫 王 众 焚 灭 杰 炅，全部注册。
- `FusionTracker.splitsThisTurn()`：给 `崩` 按拆解次数加伤。

未验证：进战斗后的实际数值、灼烧结算时机、稀有度权重。需要一局实战。
