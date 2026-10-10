# EspetroMini 方案（v2，按你的决策重写）

> 分支：Espetro 仓库新分支 **`mini`**；EsVoice 仓库新分支 **`mini`**（与模组本体在用的分支分开，互不影响）。
> 产物：**单一 modid `espetromini`**，把改过的 EsVoice 与 ApricityUI 一起打包进去；单维度运行。

## 1. 锁定后的决策
| 项 | 决策 |
|---|---|
| 路线 | **B+**：全新 modid `espetromini`，改过的 EsVoice **构进模组**里 |
| EsVoice | 单独开分支（`mini`），与本体用的那份分开；改动只落在这个分支 |
| GUI 库 | **ApricityUI 直接打包进模组**（JarJar 嵌套） |
| 维度 | **单维度跑**，不再有"主世界=大厅"特例 → 语音公共频道改规则 |
| 界面 | 职业栏/地图栏/部署点栏**全部删除**；只剩小队侧栏；**不在任何原版 team 时显示"不在任何队伍中"** |
| git | 先提交当前未提交改动（**不做** `.gitignore` 清理），再开分支 |

## 2. 依赖盘点（已核实，决定打包集）
- **GUI 层只需要 ApricityUI**：`org.espetro.client.aui/*` 的唯一外部 import 是 `com.sighs.apricityui.ApricityUI` / `init.Document` / `init.Element`；
  `AuiScreen extends net.minecraft.client.gui.screens.Screen`（原版），**不依赖 MUtil/oelib/auratip**（代码里出现的 "MUtil" 只是注释）。
- `auratip` / `oelib` / `tetrachordlib` 只被这些地方用到：AuraTip 径向菜单（`AuraTipRadialController`、`RadioRadialController`、`ResupplyRadialController`、`VehicleWheelController`）、
  `EspetroTipNotifier`、`AuraTipAboveScreen`、教程/工事/载具相关 → **全部属于被砍掉的部分**，lite 不需要。
- EsVoice 侧依赖：`voicechat_api`(必需)、`mutil`、`oelib`、`auratip`、`espetro`。并入 espetromini 后，
  只有 `voicechat_api` 是真正需要的；`mutil/oelib/auratip` 通过"删掉 MUtil 设置界面 + AuraTip 通知"即可去掉（见 §6）。

**最终安装集（客户端=服务端）**：`espetromini` + `voicechat` + Forge（ApricityUI 已嵌套在 espetromini 里）。
若要保留 EsVoice 的 MUtil 设置界面，则再嵌套 `mutil`。

## 3. 分支与工程
1. Espetro 仓库：提交当前未提交改动 → `git switch -c mini`。
2. EsVoice 仓库：`git switch -c mini`（改动只在此分支）。
3. Espetro `mini` 分支内新增独立子工程 **`mini/`**（自带 `settings.gradle` / `build.gradle` / `gradle.properties`，与完整版构建互不干扰）：
   ```
   mini/
   ├─ build.gradle            modid=espetromini；jarJar 嵌套 apricityui（+mutli 可选）
   ├─ src/main/java/org/espetromini/
   │  ├─ EspetroMini.java             主类 / 事件 / 网络注册 / 命令注册
   │  ├─ team/TeamProvider.java       原版 team 读取（唯一阵营真源）
   │  ├─ team/SquadManager.java       移植（去人数上限、按原版队隔离）
   │  ├─ team/Fireteam.java           火力组 A/B/C
   │  ├─ team/CommanderRegistry.java  管理员指定 + 记分板 tag 持久化
   │  ├─ config/MiniServerConfig.java world/serverconfig（人数上限 / 小队类型 / 角色映射）
   │  ├─ network/SquadActionPacket / SquadSyncPacket / SquadCreateWithCategoryPacket
   │  ├─ command/SquadCommand / CommanderCommand
   │  └─ client/gui/SquadSidebarScreen.java（+ 复用 client/aui 控件层）
   └─ src/main/resources/  mods.toml / espetromini.mixins.json / 语言文件
   ```
4. `mods.toml`（espetromini）：`modId="espetromini"`，依赖 forge/minecraft + `voicechat_api`；
   ApricityUI 走 JarJar 嵌套（`META-INF/jarjar`），版本范围写宽一点（例如 `[1.2.2,1.3)`），
   已装 ApricityUI 的客户端优先用外置版本（Forge JarJar 标准行为）。
   Espetro 现有构建已有 `local:` 本地 maven 仓库模式（auratip 就是这么做的），把 ApricityUI 发到同一个本地仓库再 `jarJar(...)` 即可。
   ⚠️ 打包前确认 ApricityUI 的授权（jar 内自带 COPYING）允许随模组分发。

## 4. 阵营模型（原版 team）
- `TeamProvider.getTeamId(player)` = `player.getTeam()==null ? null : player.getTeam().getName()`；
  `listTeams(server)` = `server.getScoreboard().getPlayerTeams()`（自动读取世界内**所有**原版队伍，含显示名/颜色/人数）。
- `SquadManager` 原本就按 team 字符串隔离小队 → 直接吃 `getTeamId(...)`，**逻辑零改动**。
- **无队玩家**：侧栏只显示一行「不在任何队伍中」（创建/加入按钮灰显，提示先用原版 `/team join`）。
- 原版队伍不创建、不分配、不改名 —— 全部交给服务器管理（原版 `/team` 或服务器插件）。
- 可选（配置）：`roleMapping = ["red=ATTACK","blue=DEFEND"]`，仅用于名牌颜色等表现，不参与小队逻辑。

## 5. 小队内核与配置
- 搬运 `SquadManager`（875 行）+ `SquadActionPacket` 全部动作（创建/加入/退出/解散/锁定/踢人/队长转移/火力组分配/组长任命）+ `SquadSyncPacket` + `SquadCreateWithCategoryPacket`。
- **取消人数上限**：`MAX_MEMBERS` 的 2 处校验 + 1 处快照改为读配置 `squadMaxMembers`（`0` = 不限，默认 0）；侧栏不再显示 `n/9`。
- **小队类型**写在存档 serverconfig（推荐 SERVER config）：
  ```toml
  # world/serverconfig/espetromini-server.toml
  squadMaxMembers = 0                 # 0 = 不限
  squadTypes = ["infantry|步兵队", "support|支援队", "vehicle|载具队", "recon|侦查队"]
  roleMapping = []                    # 可选，原版队名 → ATTACK/DEFEND
  ```
  备选：`world/serverconfig/espetromini/squad_types.json`，直接沿用现有 `SquadTypesSnapshot` 的 schema（解析器可原样搬）。
- 保留创建时的**命名**与**类型选择**；`/reload` 后重读类型配置（SERVER config 或在 server started/reload 时读取）。

## 6. 语音（EsVoice）并入方式
两个选项，**推荐 V1**：
- **V1（推荐）JarJar 嵌套**：EsVoice 仓库 `mini` 分支里改好、单独构建出 `esvoice-x.y.z-mini.jar`，作为嵌套 jar 打进 espetromini。
  - EsVoice 的 `mods.toml`：删掉 `espetro` 依赖，改为依赖 **`espetromini`**（mandatory）；保留 `voicechat_api`；
    删掉 `mutil/oelib/auratip`（同时删掉 MUtil 设置界面与 AuraTip 通知类）。
  - EsVoice 需要的小队/阵营/指挥官状态，改为调用 espetromini 暴露的 `org.espetromini.api.MiniAPI`
    （`getTeamId` / `getSquadId` / `isSquadLeader` / `isCommander`）—— 反向依赖，方向清晰，不循环。
  - 优点：EsVoice 的 mixin/插件入口/客户端 HUD 结构原样保留，改动集中在路由与依赖。
- **V2 源码 vendoring**：把 EsVoice `mini` 分支的源码拷进 espetromini 的源码树（单 modid，一个 jar，最彻底）；
  代价是后续 EsVoice 更新要手动同步。
- **单维度的频道规则改动**（必须，因你选了不保留大厅约定）：删掉 `VoiceChannelRouter` 里"主世界=大厅 → 全图互听"的特例，
  公共频道统一按**距离 + 阵营过滤**（同队之间不受距离限制与否可配置）；其余（SQUAD 按 squadId、COMMAND 按队长/指挥官）不变。
- SVC 的 8 个 mixin、`RadioTransmitController`、`VoiceSpeakerHud`、`ReceiveGainController`、三个网络包：**原样保留**。

## 7. 指挥官（管理员指定）
- 存储：原版记分板 tag `espetromini_commander`（持久化、重启不丢、可用原版 `/tag` 兜底）；
  `CommanderRegistry` 内存缓存 + 登录校正；配置 `singleCommanderPerTeam = true` 时同队只留一名。
- 命令：`/commander set <player>` / `clear <player>` / `list`（权限等级 2，控制台可用）。
- 指派后广播 + 同步（语音指挥频道立即生效）；保留"指挥小队"自动归队逻辑。
- 删除全部投票/弹劾/空缺志愿链路。

## 8. GUI（小队侧栏）
- 新 `SquadSidebarScreen`（`extends Screen`，直接用 `client/aui` 控件层 + ApricityUI）：
  - 只构建**小队栏**（沿用左栏几何：宽 `min(190, width*0.22)`，贴左）；**其余区域不画任何背景**（世界可见，天然透明）。
  - **左滑进出**：`slideTicks` 8 tick 缓动，`x = -sidebarW * (1 - easeOut(t))`；关闭时反向播完再 `onClose()`；
    实现上逐帧改根元素 `x`（`GuiElement.setX`），不依赖 ApricityUI 的变换 API。
  - 开关：客户端按键（沿用 J，或新增 `key.espetromini.squad`）；数据仍由服务端 `SquadSyncPacket` 主动推送。
  - 无队伍时：只显示「不在任何队伍中」+ 灰显按钮。
- 删除：职业栏、地图栏、部署点栏、票数/阶段标题、指挥官治理小窗、所有轮盘/提示。

## 9. 里程碑与验收
| 里程碑 | 内容 | 验收 |
|---|---|---|
| M0 骨架 | 两个分支 + `mini/` 工程 + mods.toml + JarJar 嵌套 ApricityUI | 客户端能加载；空侧栏界面可渲染，无报错 |
| M1 阵营层 | `TeamProvider` + `/team list` 命令 | 世界内所有原版队伍被正确读取；无队提示正确 |
| M2 小队内核 | 移植 SquadManager/网络包 + 去上限 + serverconfig 类型 | 创建/加入/退出/解散/锁定/踢人/转队长/火力组全部可用 |
| M3 侧栏 GUI | 透明 + 左滑进出 + 按键开关 + 无队态 | 只显示侧栏、世界可见、滑动顺滑、操作与旧版一致 |
| M4 指挥官 | tag + `/commander` 命令 + 广播 | 管理员指派即生效，重启保持 |
| M5 语音并入 | EsVoice `mini` 分支改依赖与频道规则 → 嵌套进 espetromini | 单 jar 生效：小队内互听、指挥频道、公共按距离+阵营 |
| M6 集成 | 构建/部署/2~3 人实测 | 服务端与客户端同 jar；重连、换队、无队、指挥指派均正常 |

## 10. 工作量（粗估）
M0 1 天（含 JarJar/local maven 打通）· M1 0.5 · M2 1.5 · M3 2 · M4 0.5 · M5 2~3（EsVoice 依赖与频道规则 + 嵌套联调）· M6 1 → **合计约 9~10 人日**。
（若语音改走 V2 源码 vendoring，M5 约 2 天，但后续同步成本高。）

## 11. 仍需你确认的点
1. **语音并入方式**：V1 JarJar 嵌套（推荐）还是 V2 源码 vendoring？
2. **分支名**：Espetro `mini` / EsVoice `mini` 可以吗？
3. **公共频道规则**（单维度下）：只按距离？距离 + 同队不限距离？敌方是否完全听不到（现逻辑是"双方都有队且不同则屏蔽"）？
4. **是否保留 EsVoice 的 MUtil 设置界面**（音量/增益）？保留就要一起嵌套 MUtil，否则音量只能在 Simple Voice Chat 自己的设置里调。
5. espetromini 的**版本号**（建议 `1.0.0`）与最低 `voicechat` 版本（现网是 `2.6.20`，EsVoice 要求 `voicechat_api ≥ 2.6.13`）。
6. 小队侧栏的按键：沿用 **J** 还是新增独立键位？
