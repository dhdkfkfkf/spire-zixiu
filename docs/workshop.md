# 创意工坊发布

条目已上线：**https://steamcommunity.com/sharedfiles/filedetails/?id=3844170209**
（ID `3844170209`，app 646570，公开）

上传工作区在 `dist\workshop-ws\`，双击 `upload.cmd` 即可发布/更新。上传器**不需要交互式登录**——
它复用已启动并登录的 Steam 会话，不输入密码。

## 一键上传

```
dist\workshop-ws\upload.cmd
```

脚本做的事：`cd` 到游戏目录 → 用游戏自带 JRE 跑工作区里的 `mod-uploader.jar upload -w <工作区>`。

发版前把新的 `mod/zixiu.jar` 复制到 `dist\workshop-ws\content\zixiu.jar`，并改 `config.json` 的 `changeNote`。

## 新建 vs 更新

上传器（`Uploader.createOrUpdateItemFromConfig`）只看 `steamPublishedID` 是不是 `null`：

| config.json | 行为 |
|---|---|
| 键不存在 | `createItem` 新建条目，成功后把数字 ID 写回文件 |
| 键 = `"3844170209"` | 更新该条目 |
| 键 = `""`（空字符串） | **崩**：`NumberFormatException` at `Uploader.updateItemInfo:117` |

所以新建时要把整个键删掉，不能留空值。`new -w` 子命令只生成工作区骨架文件，不创建 Steam 条目。

## 上传器版本

游戏目录的 `mod-uploader.jar`（1,637,535 B）自带 `steam_api64.dll` 242,976 B。这份旧库在本机也能
初始化 Steam（日志会打印 `ACCOUNT ID`），它唯一的问题是空 ID 的处理。工作区里另放了一份
1,674,543 B 的（`steam_api64.dll` 295,336 B，从 spire-pilot 工作区复制），两份都能连上 Steam。

不要用 `start cmd /k` 包一层：输出被重定向时 `System.console()` 为 null，上传器会自己重启一个 cmd
并可能丢掉参数。直接在当前控制台里跑即可。

## 标签

词表取自 app 646570 的工坊筛选器（`workshop/browse/?appid=646570` 页面里的 tag 列表），合法值有
`Character`、`Cards`、`Relics`、`Events`、`Potions`、`Utility`、`Content`、`Simplified Chinese`、
`Traditional Chinese` 等。当前用：

```
Character, Cards, Relics, Events, Simplified Chinese
```

Steam 会把 `Relics`、`Events` 归一成小写 `relics`、`events`，属于正常现象。

## 核对发布结果

```bash
curl -sk -X POST https://api.steampowered.com/ISteamRemoteStorage/GetPublishedFileDetails/v1/ \
  -d "itemcount=1&publishedfileids[0]=3844170209" | head -c 600
```

`visibility` 为 `0` 即公开，`file_size` 应等于 jar 的字节数。工坊网页有缓存延迟，刚发布时
`sharedfiles/filedetails` 可能还返回 Error，过几分钟再看。

## 封面重新生成

```bash
java -cp tools/out CropSquare src/main/resources/zixiu/images/charSelect/portrait.png dist/workshop-preview.png 780 560 1200 512
```

## 发版顺序

1. 改 `CHANGELOG.md`，新增 `docs/RELEASE-<版本>.md`。
2. `tools/build.ps1` 构建。
3. 复制 jar 到工作区、改 `changeNote`，双击 `upload.cmd`。
4. `git tag v<版本> && git push --tags`，在 GitHub Release 附上 `mod/zixiu.jar` 和更新说明。

## 1.0.16 工坊文案（复制到 `config.json` 的 `description` / `changeNote`）

**短描述**

```
字修——汉字拆合玩法｜把汉字拆成部件、再拼成新字来打牌的《杀戮尖塔》角色，
内置与「看牌界面」完全一致的局内卡牌图鉴（字典）。
```

**长描述**

```
【字修——汉字拆合玩法】

把汉字拆成部件、再拼成新字来打牌的角色 Mod。

■ 角色：砚秋
家境中落的儒雅贵公子。衣袍洗得发白，礼数一点没少；不修仙、不炼丹，只信手里的字——
拆开了是道理，合起来是命。

■ 玩法
· 拆：把成品牌拆回部件，免费、不限次数
· 合：2～3 张部件拼成一个新字，免费、不限次数
· 拆合规则（选角界面可切换）：方案 1 产出虚无且消耗、用掉的牌进弃牌堆；
  方案 2 产出保留、用掉的牌进消耗堆
· 墨迹未干：本回合合出来的字不能当回合拆，杜绝无限循环
· 连书：与上一张共享部件的成品牌接链，对所有敌人造成 2×连书 伤害
· 曝光 / 灼烧：一加一减两种持续伤害标记
· 九字真言·言灵：集齐临兵斗者皆阵列前行后，出牌按组成单位数推进指针（1→9 自动轮转）并触发言灵

■ 字典（局内卡牌图鉴）
· 顶栏地图图标左边多一本红色字典，点开就是整套字修卡牌图鉴
· 出现时机和右上角「牌组」按钮一致：战斗、地图、商店、事件、营火、各类奖励 / 选牌界面都能开
· 手感照原版：按住任意空白处 1:1 拖动、滚轮按原版步长翻页、右侧原版滚动条（越界回弹），
  5 列 × 0.75 倍卡墙
· 排序照原版：稀有度 / 类型 / 耗能 / A-Z，点一下切换升 / 降序，带原版箭头（中文按原版设置隐藏 A-Z）
· 预览照原版：点任意卡开原版大图、左右箭头可在整套图鉴里连续翻阅；
  排序条右侧有原版「看升级版」勾选框
· 图鉴开着时顶栏的地图 / 牌组 / 设置可以直接切走；
  字典图标悬停歪 15° + 闪光，打开时持续轻晃
· 合字 / 拆字选材料时实时预览能写出的字与效果

■ 内容量
1 名角色 / 84 张卡牌（72 成品牌 + 10 部件 + 2 基础牌）/ 18 件遗物（含九字真言九件）
/ 3 瓶药水 / 3 个事件 / 19 个能力

■ 美术
全套自制：84 张卡图与重绘卡框、稀有度描边、能力与遗物图标、墨池朱砂费用球与能量球、
选角背景、战斗立绘、死亡图、四张通关分镜（一张背景 + 三格画面）。

■ 状态
1.0.16，战斗验证初步完成：可正常从头打到结局，数值仍在调整。欢迎反馈。

适配游戏版本 12-18-2022（最终版），需要 ModTheSpire / BaseMod / StSLib。
```

**更新说明（changeNote）**

```
1.0.16：新增「字典」局内卡牌图鉴——布局与操作完全照原版看牌界面（拖动 / 滚轮 / 滚动条 / 排序 / 看升级版 / 点卡开大图），顶栏红色字典图标悬停歪 15° + 闪光、打开时持续轻晃，并和地图 / 牌组 / 设置互相切换；合字、拆字实时预览；合 / 拆按钮不再压住鼠标。
1.0.15：明 曝光 1(2)、日/火 改成升级才给 1 层曝光/灼烧；九字碑劫持回到「每幕第一个事件房」；通关动画挂纸张音效。
1.0.14：随机部件统一 10 种全量、随机成品牌改用全量注册表；残碑图标再缩小；死亡/胜利与篝火分别用 shoulder / shoulder2 两张正式肩像，通关文案去掉。
1.0.13：九字/言灵调整、残碑图标重画、通关动画加文案且胜利页不再黑屏。
1.0.12：事件房劫持（每幕首个事件房必出九字碑）+ 精英保底字修遗物；修求解器选牌卡死；崩/末/杜 改版。
1.0.11：炅/晶/淼/灭/焚/王/杰/桂/灶/焱 等卡牌重做，九字与事件更容易遇到。
1.0.10：未 的选牌改成「最后一笔选择赢」。
```
