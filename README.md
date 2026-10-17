# Espetro

Espetro 是 Minecraft Forge 1.20.1 的战术小队与侵攻流程模组，提供阵营、编制、职业、小队、指挥官、兵力、兵站、队包、前哨、载具和后勤系统。当前开发入口为 **`main` 分支**；本页介绍该分支已实现的功能。

地图据点支持兼容旧配置的 AAS、按合法路线逐阶段抽点的 RAAS，以及每局随机选择 AAS/RAAS；具体格式见[配置文档](docs/CONFIGURATION.md#aas--raas-路线)。

## 已实现的玩法

- **战局与编制**：地图投票、临时战场、尽量保持预组队同边的自动攻守分配、双方并行指挥官投票、编制选择、部署、兵力结算和战场清理。
- **班组与职业**：小队及 A/B/C 火力组、队长转移、职业配额、装备变体、换职冷却和载具组员席位限制。
- **兵站与工事**：Radio、HAB、队包、工事预览、铁铲建造/拆除、建材消耗、敌军靠近时的 HAB 压制，以及兵站失效后的部署点刷新。
- **载具与补给**：按编制及地图点位自动生成/刷新载具、载具状态查询、弹药/建材装卸、步兵补给和按车型限制的换职。SBW 驾驶员离线保留已有专用拦截，其他模组自身的载具回收规则仍需分别核对。
- **战术面板**：`J` 打开班组、职业、部署点、载具信息、地图和指挥官技能；载具信息菜单支持滚动并保留刷新前的展开状态。
- **通用轮盘**：ApricityUI / EsRadial 时钟式图标轮盘、树状工事目录、中心悬停说明、轮盘内返回扇区，以及游戏内和 JSON 两种布局编辑方式。
- **指挥官支援**：内置无人机侦察和 155 火炮示例；155 支持定点炮击和拖线徐进弹幕，共用技能冷却，并可向本方地图广播覆盖区。部署阶段不能使用技能。
- **教程与聊天**：服务端管理的新手说明与只读界面预览；AUI 页面内支持 `T` / `/` 打开聊天或命令输入，关闭聊天后恢复来源菜单。

战术地图、据点与标点由配套 **ESPoints** 提供。具体玩法规则见[使用手册](docs/USER_GUIDE.md)，轮盘布局见[EsRadial 接入说明](docs/esradial-integration.md)。

## 核心流程

```text
主世界主城
 -> 管理员 /espetro prestart
 -> 全服地图投票
 -> 载入获胜战场维度
 -> 自动分配攻/守方并展示分配结果
 -> 双方并行指挥官投票
 -> 双方依次选择编制
 -> 编制揭示
 -> 部署
 -> 战斗
 -> 结算并返回主城
```

管理员可在任意阶段使用 `/espetro stop` 强制结束游戏。所有在线玩家会立即回到主世界主城，
本局投票、队伍、职业、装备、统计和临时战场对象会被清理，随后重新打开 MUtil 主城界面。
战场维度会从服务器 Tick 列表摘除，其区块、实体和 POI 存储句柄会在独立 IO 线程中以“不保存本局破坏”
的方式关闭，随后删除存档中的整个战场维度目录。下一局再次选中该地图时，会重新从只读 `EsWorld`
模板复制并挂载新的战场维度；`EsWorld` 中的原始地图不会被修改。

## 环境与依赖

| 项目 | 版本 |
| --- | --- |
| Java | 17 |
| Minecraft | 1.20.1 |
| Forge | 构建使用 47.4.22；加载范围见 `mods.toml` |
| MUtil | 随所用 AuraTip / 整合包配套安装 |
| OELib | 0.2.4 |
| AuraTip | `1.1.3-beta-espetro.1`（精确版本） |
| ApricityUI | `1.2.6`（精确版本） |
| EsRadial | `0.3.2`（Espetro 声明为客户端精确前置） |
| Tetrachord Lib | `1.20+1.0.3`（精确版本） |
| Rhino (`rhino`) | 2001.2.2-build.17 |
| Architectury API (`architectury`) | 9.1.12 或更高 |
| ESPoints (`espoints`) | 可选集成下限 `1.1.1e`；客户端与服务器使用同一配套构建 |
| KubeJS (`kubejs`) | Forge 1.20.1 使用 `2001.6.5-build.26`，强制依赖 |

版本以 [gradle.properties](gradle.properties) 和 [mods.toml](src/main/resources/META-INF/mods.toml) 为准；“同为 1.1.3”不代表不同后缀的 AuraTip 可以互换。MUtil/AuraTip 仍供保留的界面和联动使用，新增轮盘由 ApricityUI / EsRadial 绘制，不能因为换了轮盘就删掉旧前置。

ESPoints 以 `espoints` 为模组 ID，依赖 Espetro；Espetro 将其声明为软依赖。未安装时部署面板的地图为占位显示，155 火炮也无法打开选点地图。需要地图、据点、标点或火炮选点时，客户端和服务器都应安装同一配套 ESPoints，并安装其声明的 Ping Wheel 1.12.1 等前置。API 与网络协议可能在相同版本号内演进，应成套更新，不能只按版本下限混用新旧构建。

为防止 ESPoints 影响 Espetro 的独立开发启动，本地兄弟项目默认不注入，可用 `-PespetroIncludeLocalEspoints=true` 显式启用。Esvoice 为可选语音扩展，EsICO 为可选体力扩展；SBW、DragonRise 的载具和补给站联动按对应整合包安装。

KubeJS 是强制依赖。加载时会自动暴露 `Espetro`、`EspetroAPI`、`EspetroCommanderSkills`、底层管理器、ESPoints 桥接类和 MUtil 类给 KubeJS。`startup_scripts` 注册默认技能，`server_scripts` 提供侦察和定点炮击效果；徐进弹幕由 Java 端 `ArtilleryBarrageExecutor` 排程执行。载具补给站已改为可配置的“建造工事”，不再是指挥官技能。

默认技能脚本首次启动补写到 `kubejs/`，**已有同名脚本不会被新版 jar 覆盖**。升级前应备份并比较现有脚本与 [jar 内示例](src/main/resources/espetro_kubejs)，保留服主定制；换 jar 不代表旧脚本已同步更新。完整脚本接口见[指挥官技能文档](docs/COMMANDER_SKILL_SCRIPTS.md)。

## 默认按键

| 按键 | 功能 |
| --- | --- |
| `K` | 请求服务端状态并打开队伍/小队相关界面 |
| `J` | 打开战术面板：班组、职业、部署点、载具信息、地图和技能 |
| 长按左 `Alt` | 符合身份权限时打开 EsRadial 战术轮盘；左键选择，松开取消 |
| 部署界面 `C` / `B` | 调整战术地图显示范围 |
| 页面中的 `T` / `/` | 聊天 / 命令输入；聊天关闭后返回来源菜单 |
| 打开轮盘后 `F6` | 编辑当前页布局；Enter 保存、Esc 取消、R 恢复默认后保存 |

载具轮盘沿用 SBW 的交互键，常见默认值为 `F`，以游戏内按键设置为准。ESPoints 的 `V` 地图与标点键由 ESPoints / Ping Wheel 管理。

## 轮盘、后勤与火力支援

### 时钟轮盘与布局编辑

默认按 30° 时钟刻度放置图标，可合并为不同宽度的扇区，不是固定生成 12 个槽。名称和业务说明显示在中心；返回是轮盘内的扇区，右键不用于轮盘导航。

`F6` 中可拖动按钮/空槽换位置、拖动分界线改变大小、滚轮旋转；Tab 切换 30°/45°混合、30°、45°或自由模式。也可编辑客户端 `config/esradial/layouts.json`，下次打开轮盘重读。已有自定义布局不会被新默认排版覆盖；在当前页按 F6、R、Enter 可恢复默认。详细格式见 [EsRadial 布局文档](https://github.com/RositaOVO/EsRadial/blob/1.20.1/docs/LAYOUTS.md)。

### 载具补给与换职

补给载具共用弹药/建材容量池；实际装卸按服务端验证的阵营、距离、Radio 库存、基地范围和车型执行。载具轮盘支持按住左键持续装卸；步兵补给按实际弹药缺口发放，不会在弹药已满时重复扣资源。

显式配置 `fightveh: true` 的步兵战斗载具可显示完整换职列表，但仍需通过职业配额等原有校验。补给载具、未标分类的旧载具及两项分类都为 `false` 的载具，仅允许选择载具组员/驾驶员与载具队长。职业通过 `vehicle_crew` 标签识别，并兼容旧图标名；规则不依赖阵营名称。

### 155 定点炮击与徐进弹幕

在战斗阶段从 J 战术面板选择可用技能，需选点时在同一地图操作：右键单击为定点炮击，按住拖动并松开为徐进弹幕。两种形式共用同一技能和冷却，技能仅选中待用时不会视为已经完成释放；服务端最终校验身份、阶段、目标和冷却。

配套 ESPoints 可向己方显示炮击覆盖区。定点效果可在默认 KubeJS 脚本中调整；徐进弹幕的 Java 排程与默认脚本参数需配套维护。具体权限、冷却和效果以服务器配置为准。

## 新手教程

主城 GUI 的「进入新手教程」或 `/espetro tutorial reopen` 启动服务端权威进度的引导：

- 自绘 **TutorialHudOverlay**（底部说明卡 + 左下「退出教程」），不再依赖关闭 AuraTip 卡片才能下一步。
- 对应阶段打开 **只读预览 GUI**（`tutorialPreviewMode`），点击不会改真实战局。
- **Enter** = 下一步；聊天栏打开时不拦截。
- 左下角或 `/espetro tutorial skip` = 完全退出。

## 配置

所有地图、编制和地图级配置位于游戏/服务端根目录，且只在进程启动时加载：

```text
EsDimensions.json
EsFactions/*.json
EsWorld/<地图文件夹>/
├── level.dat
├── region/
└── EsConfig/
    ├── game.json
    ├── spawn_points.json
    ├── outposts.json
    ├── bastion.json
    ├── logistics.json
    ├── team_pack.json
    ├── SquadTypes.json
    ├── VehSpawn.json
    ├── TacticalMap.json
    ├── CapturePoints.json
    └── map.png              # 可选，由 TacticalMap.json 引用
```

首次启动会从 JAR 安全导出地图、配置和十份示例编制（含 Y64 超平坦 `test_flat`）。若 `EsFactions/` 已有任意 JSON，则整个目录视为服主所有，升级时不会补写、替换或删除任何编制；只有空目录或全新安装才会生成示例编制。`TacticalMap.json` 与 `CapturePoints.json` 是每张地图的必需文件；缺失或非法时该地图会被拒绝。修改外部配置后必须重启客户端或服务端；`/reload` 与 `/espetro reload` 不会重读这些启动冻结配置。

详见[配置文档](docs/CONFIGURATION.md)和[使用手册](docs/USER_GUIDE.md)。

## 体力与交互读条

当前体力系统及体力条已迁至可选的 **EsICO**；未安装 EsICO 时不应期待 Espetro 自己显示体力条。体力参数按所用 EsICO 版本配置。上车、换座、下车等交互读条由 Espetro 自身实现，不依赖把体力模块作为硬前置。

## 构建

构建需要 Java 17。先将 [EsRadial](https://github.com/RositaOVO/EsRadial/tree/1.20.1) 对应版本发布到 Maven Local，并准备当前依赖要求的 Tetrachord 本地产物、定制 AuraTip 与 DragonRise 编译依赖：

- AuraTip 默认查找 `../AuraTip/build/libs/auratip-forge-1.20.1-1.1.3-beta-espetro.1.jar`；也可用 `-PauratipJar=/绝对路径/对应.jar` 指定。
- DragonRise 的补给站配置 API 当前由 `libs/dragonrise_reforge-1.5.0-beta-20260921.jar` 提供；源码直接引用该 API，未准备它时不能仅凭运行时“软依赖”声明判断能完整编译。
- 其他依赖坐标及本地仓库设置见 [build.gradle](build.gradle)。

在仓库根目录执行：

```bash
./gradlew build -PauratipJar=/绝对路径/对应.jar
```

Windows 使用 `gradlew.bat build -PauratipJar=C:/绝对路径/对应.jar`。

产物位于 `build/libs/espetro-<version>.jar`。

### 开发客户端启动（双显卡 / GLFW 报错）

若出现 `Failed to find a valid GLFW profile` / `GLXBadFBConfig`，多为 NVIDIA 驱动与内核模块版本不一致。项目已在 `runClient` 中默认强制走 Intel Mesa：

```bash
./gradlew runClient
# 或
./scripts/run-client.sh
```

根因修复：更新内核/驱动包后**重启系统**，再确认 `nvidia-smi` 与 `glxinfo -B` 正常。

## 文档

- [配置与 JSON 规范](docs/CONFIGURATION.md)
- [玩家与管理员使用手册](docs/USER_GUIDE.md)
- [指挥官技能脚本配置与开发](docs/COMMANDER_SKILL_SCRIPTS.md)
- [开发与 API 文档](docs/DEVELOPMENT.md)
- [KubeJS 联动开发文档](docs/KUBEJS_INTEGRATION.md)
- [完整类参考](docs/CLASS_REFERENCE.md)

## 与 HCR AAD 源码的边界

当前仓库还保留了一份 `src/main/java/com/example/hcrpoints` 旧源码副本，但构建已排除它。当前 [ESPoints 独立仓库](https://github.com/KaguyaMao/EsPoints) 的模组 ID 为 `espoints`，包名为 `com.example.espoints`；Espetro 通过可选依赖和反射桥接调用战术地图。新的战术地图功能应在 ESPoints 开发，Espetro 业务代码应放在 `org.espetro`。
