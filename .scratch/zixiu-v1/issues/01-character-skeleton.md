# 01: 角色骨架能进游戏

**What to build:** 玩家能在选角界面选到「字修」，进入战斗后拥有 70 点最大生命、3 点能量、10 张起始牌组（4× 击、3× 守、3× 部件：火 / 木 / 口），并携带起始遗物「秃笔」。基础牌与部件牌都能正常打出、结算、进入弃牌堆。

**Blocked by:** None (can start immediately)

**Status:** done

- [x] 自定义角色出现在选角界面，有名字、颜色与描述占位（日志确认 `Successfully recreated ZIXIU`，视觉待人工确认）
- [ ] 进战斗后 70 HP / 3 能量 / 手牌 5 张，起始牌组 10 张正确
- [ ] `击` 造成 6 伤害、`守` 获得 5 格挡，三张部件牌的 0 费微效各自生效
- [ ] 起始遗物「秃笔」在战斗开始时把 1 张随机部件牌加入手牌
- [ ] 能打完一场普通战斗并正常结算奖励

## Comments

构建：`bash scripts/build.sh` → `zixiu.jar`，装进游戏 `mods/` 目录。

启动：`./jre/bin/java.exe -jar <workshop>/1605060445/ModTheSpire.jar --mods zixiu,basemod,stslib`
（注意不要把 `desktop-1.0.jar` 放进 `-cp`，否则游戏类会被两个 ClassLoader 加载，BaseMod 报 VerifyError。）

已修复的两个运行时问题：

1. `LibraryType` 与 `CardColor` 枚举名必须一致，否则 `CardLibrary.LibraryType.valueOf` 抛 `No enum constant ... ZIXIU_COLOR`，主菜单崩溃。
2. 遗物图片经 `ImageMaster` 自动拼 `images/relics/` 前缀且**不补扩展名**，所以图片要放在 jar 根目录的 `images/relics/tubi.png`，构造参数传 `"tubi.png"`。

日志确认：mod 加载、角色注册、起始遗物注入、选角界面渲染均无异常。

第一次开局闪退，补了两个 `CustomPlayer` 的隐藏契约：

3. `getEnergyImage()` 的默认实现只在 `energyOrb instanceof CustomEnergyOrb` 时返回贴图，否则 `throw new RuntimeException`。用官方 `EnergyOrbRed` 必须自己 override，返回 `ImageMaster.ENERGY_RED_LAYER1`。
4. `renderPlayerImage()` 无判空直接调 `animation.type()`。必须给一个非 null 的 `AbstractAnimation`；占位用 `StaticAnimation`（`type()` 返回 `NONE`，走静态贴图分支）。

战斗内行为（牌效、遗物触发、起始牌组）尚未验证，需要人工开局。

第二次开局日志：Neow 事件 → 进入 `MonsterRoom` → `MONSTER: Jaw Worm`，生成 `ZIXIU.autosave`，全程无异常，游戏正常退出。**开局不再闪退，已能进第一场战斗。** 剩余验收项都是数值与手感，需要人眼确认。

人工确认：70 HP / 3 能量 / 起始牌组 / 牌效均正常。秃笔从「随机塞 1 张」改成「三选一」——照 `ChooseOneColorless` 的结构，`customCombatOpen` 弹面板，选中后用 `ShowCardAndAddToHandEffect` 进手（手牌满则进弃牌堆）。
