# 创意工坊发布

上传器是游戏目录里的 `mod-uploader.jar`，**需要交互式登录 Steam**，所以这一步只能由你在本地跑。

## 步骤

```powershell
cd D:\Steam\steamapps\common\SlayTheSpire

# 1) 先构建，产出 mod\zixiu.jar
powershell -NoProfile -File C:\Users\10797\ZCodeWorkspace\spire-zixiu\tools\build.ps1

# 2) 新建上传工作区（按提示填标题、描述、封面、内容目录），最后会要求 Steam 登录
.\jre\bin\java.exe -jar mod-uploader.jar new -w zixiu

# 3) 之后每次发版
.\jre\bin\java.exe -jar mod-uploader.jar upload -w zixiu
```

工作区的内容目录指向项目里的 `mod\`（里面只有 `zixiu.jar`）。封面用 `dist\workshop-preview.png`（512×512，已生成；`dist/` 不进仓库，需要重新生成时见下）。

## 标题

```
字修 Zixiu — 汉字拆合角色
```

## 描述

```
落魄书生「字修」：把藏书当弹药的读书人。

【核心机制】
10 个部件牌（人 口 木 月 土 一 水 火 山 日）既是 0 费卡牌，也是拼字材料。
· 合：选两张部件牌拼成一个字，免费、不限次数。口+木 能写出 呆/杏/困/束，由你挑。
· 拆：成品牌拆回部件，原牌进消耗堆。
· 墨迹未干：本回合合出来的字当回合不能拆。

85 个汉字、75 条配方：有的字拆得开却拼不出，有的字是字胚（只能当构件），也有合错才会出现的废字。

【三条流派】
· 焚：灼烧不递减，铺满再引爆。
· 润：格挡、治疗，剩余格挡换抽牌。
· 照：曝光是消耗型增伤，叠层后一轮兑现。

【内容】
53 张牌、7 个遗物、3 瓶药水、2 个第一幕事件。

【依赖】
ModTheSpire、BaseMod、StSLib。游戏版本 12-18-2022。

【已知不足】
卡面暂为占位图，平衡未经实战验证。反馈请到 GitHub issue。
```

## 标签

`Character`、`Cards`、`Relics`、`Simplified Chinese`

## 封面重新生成

```bash
java -cp tools/out CropSquare src/main/resources/zixiu/images/charSelect/portrait.png dist/workshop-preview.png 780 560 1200 512
```

## 发版顺序

1. 改 `CHANGELOG.md`，新增 `docs/RELEASE-<版本>.md`。
2. `tools/build.ps1` 构建。
3. `mod-uploader.jar upload -w zixiu`，更新说明粘贴 `docs/RELEASE-<版本>.md` 的内容。
4. `git tag v<版本> && git push --tags`，在 GitHub Release 附上 `mod/zixiu.jar` 和更新说明。
