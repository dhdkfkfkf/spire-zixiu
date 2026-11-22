# 02: 字学核心（纯函数 + 单测）

**What to build:** 一个不引用任何游戏引擎类型的模块，把汉字规则完整表达出来：部件枚举、展开后的部件多重集、配方表、`optionsFor(部件) → 可选字`、`partsOf(字) → 部件`。先用 10 条种子配方打通，JUnit 直接测。

**Blocked by:** None (can start immediately)

**Status:** done

- [x] 部件枚举覆盖 10 种，异体归并（亻→人、氵→水、灬→火、士→土、囗→口）在映射层完成
- [x] `{口,木}` 返回 呆 / 杏 / 困 / 束 四个结果；`{木,一}` 返回 本 / 末 / 未
- [x] `{水,水,水}` 返回空；`{土,土}` 返回 `圭` 且 role 为字胚
- [x] `partsOf("品")` 返回三口；`partsOf("永")` 返回空
- [x] 配方键与选择顺序无关（`{木,口}` 与 `{口,木}` 同键）
- [x] 全部用例可在无游戏引擎的环境下跑绿

## Comments

实现：`src/main/java/zixiu/lexicon/`（Component / WordRole / WordSpec / RecipeKey / ZixiuLexicon），13 条 JUnit 用例在 `src/test/java/zixiu/lexicon/ZixiuLexiconTest.java`。

运行：`bash scripts/test.sh` → `OK (13 tests)`。

种子配方 16 条，已覆盖 ticket 要求的全部判定（撞车、字胚、废字、无配方、独体、异体归并、成品牌中转）。完整 85 字在 05 补齐。
