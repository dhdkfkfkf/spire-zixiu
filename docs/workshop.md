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
