# 地图编辑模式：点位 / 出生点 / 载具出生点 编辑功能实现方案

> 目标：在 `/espetro edit <map>` 会话里，用「指令 + 世界内可视化」直接创建、修改、删除
> ①据点（CapturePoints）②双方出生点（spawn_points）③载具出生点（VehSpawn），
> 并随 `editsave` 一起写回地图存档；`editabandon` 全部丢弃。
>
> 本文只给方案，不包含实现代码。

---

## 0. 目标与边界

**做**

- 三类数据的创建 / 修改 / 删除 / 列表 / 世界内预览。
- 草稿与存档分离：编辑期间随时改、随时丢，只有保存才落盘。
- 保存后给出「人类可读的变更摘要」，写坏前先备份。

**不做（本期）**

- RAAS 区域池（`CapturePoints.json` 的 `raas` 键）编辑。
- 前哨基地 `outposts.json` 编辑。
- 战术地图底图 `map.png` 自动导出。
- `EsConfig/` 以外的任何文件。

---

## 1. 现状与生效链路（已核实）

### 1.1 三类文件

| 数据 | 文件 | 解析器 | 关键规则 |
|---|---|---|---|
| 据点 | `EsWorld/<map>/EsConfig/CapturePoints.json` | EsPoints `ObjectiveLayout.java:136-147`、`CapturePointManager.java:185-190` | `plannedPoints` 非空、`name` 唯一、`batch` 有效、`pos1 != pos2` |
| 出生点 | `EsWorld/<map>/EsConfig/spawn_points.json` | `SpawnPointsSnapshot.java:29-57` | `spawnPoints.ATTACK/DEFEND` 各含 `x/y/z/yaw`；缺 `yaw` 时 ATTACK=0、DEFEND=180；缺任一队即整份无效 |
| 载具出生点 | `EsWorld/<map>/EsConfig/VehSpawn.json` | `VehSpawnSnapshot.java:54-186` | `VehTypes` 别名只能有一个；`spawn_points` 为对象；每点含 `attack` 与 `defend` 两组坐标；`id` 缺省 `<type>_<序号>`；pose 缺 `x/y/z` 即无效、缺 `yaw` 默认 0 |

据点文件结构（线上实例）：

```json
{"totalBatches":5,"endBehavior":"terminate","attackBatchCompletionReinforcement":50,
 "teamReinforcements":{"ATTACK":75,"DEFEND":350},
 "plannedPoints":[{"name":"A","batch":1,
   "pos1":{"x":-4,"y":76,"z":-953},"pos2":{"x":208,"y":102,"z":-1106}}, ...]}
```

载具点结构（一型多点的数组形式）：

```json
{"VehTypes":["mbt","mgs","ifv","acv","apc","car","truck","supply_truck","transport_helicopter"],
 "spawn_points":{"mbt":[{"id":"mbt_1",
   "attack":{"x":-207,"y":73,"z":-243,"yaw":180},
   "defend":{"x":1980,"y":72,"z":-1596,"yaw":0}}, ...]}}
```

### 1.2 三个必须绕开的坑

1. **配置只在启动期加载**
   `ExternalConfigBootstrap.bootstrapIfNeeded()`（`ExternalConfigBootstrap.java:45`）只在开服跑一次，
   `ActiveMapConfig` 是静态快照（`ActiveMapConfig.java:47` 出生点、`:48` 载具点、`:59-60` 据点）。
   `/espetro reload` 已明确提示「外部地图/编制/EsConfig 不支持热重载」（`EspetroCommand.java:85-91`）。
   ⇒ **写完必须重启**，方案里每一步都要按这个前提给提示。

2. **`Points/` 预设会覆盖 legacy 文件**
   EsPoints `PointsPresetLoader.select(...)`（`PointsPresetLoader.java:74-111`）：
   先扫 `EsWorld/<map>/Points/*.json`，按 `modes` 数组（或 legacy 启发式 `:139-161`）匹配 `game.json` 的
   `game.objectiveMode`（AAS/RAAS），按回合种子随机选一个；**只有 `Points/` 无匹配时才回退
   `EsConfig/CapturePoints.json`**。
   `CREATE_PLUS`、`越南` 现在各有 3 个预设 ⇒ 直接改 legacy 文件**不会生效**。
   ⇒ 编辑器必须让管理员显式选择写入目标：`legacy` 或某个 `Points/<file>.json`。

3. **载具点会连带生成补给站**
   `VehicleManager.spawnPadSupplyStations`（`VehicleManager.java:2187`）在开局对每个 attack/defend
   pose 各放一个载具坑补给站，侧向偏移 `SUPPLY_SIDE_OFFSET = 6.0`（`:82`）、主基地
   `MAIN_BASE_SUPPLY_SIDE_OFFSET = 3.0`（`:84`），方向由 `getSupplyStationPosition(pose)`（`:2498`）
   按 yaw 右偏决定。⇒ 加/删载具点会自动加/删补给站，方案里要在提示中说明。

### 1.3 已有的可复用件

- 编辑会话：`src/main/java/org/espetro/dimension/MapEditManager.java`
  （`State{NONE,WORKING,READY}`、`create/enter/save/abandon`、`SAVED_DIRS=region/entities/poi/data`、
  备份轮转 5 份）；指令入口 `EspetroCommand.java:35-54`；对局守卫 `GameStateManager.prestart`。
- 世界内线框预览的现成参考：`src/main/java/org/espetro/client/FortificationWandOverlay.java`
  （但那是**客户端**渲染 + 自定义包，走它就必须升 `PROTOCOL_VERSION` 并双端同步，见 §6）。
- 坐标 1:1：编辑维度是模板整目录拷贝（`region/entities/poi/data`），世界坐标 == 地图坐标。

---

## 2. 总体设计

```
/espetro edit <map>        → 复制模板到 espetro:edit（已有）
       │
       ▼
  编辑草稿（内存）  ⇄  world/espetro-edit-draft/<mapFolder>.json（防崩落盘）
       │   point corner/apply/del · spawn set · veh add/del · capture target
       ▼
  /espetro edit configsave   → 只写 EsConfig（不卸载维度，可反复调）
  /espetro edit editsave     → 写 region（已有）+ 写 EsConfig
  /espetro edit editabandon  → 两边都丢（已有）
```

设计要点：

1. **草稿独立于存档**：所有编辑先落到 `MapEditDraft`，磁盘镜像写在
   `<save>/espetro-edit-draft/<mapFolder>.json`，服务器崩溃/重启后 `edit in` 能继续。
2. **采集用「站过去 + 一条指令」**：不引入客户端交互，命令读玩家脚下/准星方块坐标。
3. **预览用服务端粒子**：零客户端改动、不动协议号，任何客户端都能看见。
4. **写入用「读-改-写」而非重建**：Gson 读出 `JsonObject`，只改目标键，保留未知键
   （如 `modes`、`_comment`、`raas`）与数组顺序；写临时文件再 `ATOMIC_MOVE`。
5. **每次写入前整目录备份 `EsConfig/`**（只有几十 KB），独立于 region 的 478 MB 级备份。

---

## 3. 指令表

> 全部要求 `permission >= 2`，且必须在 `edit` 会话（`MapEditManager.State.READY`）内执行。

### 3.1 据点

| 指令 | 作用 | 失败提示 |
|---|---|---|
| `/espetro edit point corner <name> <1\|2>` | 把**脚下方块**记为据点的第 1/2 个角点（暂存） | 会话未就绪 |
| `/espetro edit point apply <name> <batch>` | 用两个角点提交/覆盖据点，`batch` 为批次 | 缺角点 / 两角点相同 / 名称被占用 |
| `/espetro edit point del <name>` | 删除草稿中的据点 | 不存在 |
| `/espetro edit point list` | 列表：`name batch 尺寸 中心` | — |
| `/espetro edit point show [name\|all]` | 粒子画框 8 秒 | — |
| `/espetro edit capture target [legacy\|<file>.json]` | 查看/切换写入目标 | 文件不存在 |

### 3.2 出生点

| 指令 | 作用 | 说明 |
|---|---|---|
| `/espetro edit spawn <attack\|defend>` | 用玩家当前位置+朝向覆盖该队出生点 | x/z 自动 `.5` 对齐，y 取脚下 |
| `/espetro edit spawn show` | 粒子标记两个出生点 + 朝向箭头 | — |

### 3.3 载具出生点

| 指令 | 作用 | 说明 |
|---|---|---|
| `/espetro edit veh add <type> <attack\|defend> [id]` | 新增点或给已有点写该侧坐标 | `id` 省略 → `<type>_<n>` 取最大值+1 |
| `/espetro edit veh del <type> <id>` | 删点 | 会提示哪些编制因此「点数不足」 |
| `/espetro edit veh list [type]` | 列表：`id / attack / defend / 间距` | — |
| `/espetro edit veh show [type]` | 粒子标记（attack 蓝、defend 红） | — |

**语义说明**

- 每个载具点同时含 `attack` 与 `defend` 两组坐标（同一张地图上两队的出生位），而人一次只能站一处，
  所以一条指令只记录一侧；点是「草稿」状态，另一侧补齐后才算有效。
  `VehSpawnSnapshot.parsePoint(...)`（`VehSpawnSnapshot.java:151-167`）要求两侧 pose 都有效，
  否则整点被丢弃并记错误 ⇒ `configsave` 前必须拦下「只填一侧」的点。
- `[id]` 省略：新建点，id 自动 `<type>_<n>`（`n` = 该类型当前点数 + 1）；
  `[id]` 已存在：只覆盖该点这一侧的 pose（补另一侧 / 微调位置）；不存在：按该 id 新建。
- **数组顺序 = 编制槽位顺序**：`VehicleConfig.java:237-242` 第 i 台载具取 `points.get(i)`；
  且 `:211` 编制实体数 > 该类型点数时整份编制被跳过。所以删中间点会让后续点前移，`veh del` 要给位移提示。
- `id` 只标识「类型下的一个点」，不绑定具体载具；`per_max_count > 1` 时同一槽位可同时存在多台
  （`VehicleManager.java:1849`），但**都刷在同一个点上**（`:496-499` 取该槽位 pose）。要避免叠车只能加槽位（多写 entity），不能靠调 per_max。
- 若原文件的 `spawn_points.<type>` 是 legacy 对象形式，写回时保持该形式（`:127-142`），不擅自改成数组。

### 3.4 保存

| 指令 | 作用 |
|---|---|
| `/espetro edit configsave` | 只把草稿写进 `EsConfig/`（不卸载编辑维度），打印变更摘要 |
| `/espetro editsave` | 原有语义 + 写配置（先 `configsave` 再写 region） |
| `/espetro editabandon` | 原有语义：丢草稿 + 丢维度 |

**示例会话**

```
/espetro edit CREATE_PLUS
/espetro edit in
/espetro edit spawn attack                 # 站进攻出生点
/espetro edit spawn defend                 # 站防守出生点
/espetro edit point corner A 1             # 站据点一角
/espetro edit point corner A 2             # 站对角
/espetro edit point apply A 1
/espetro edit point show all
/espetro edit veh add mbt attack
/espetro edit veh add mbt defend
/espetro edit veh list
/espetro edit capture target Points/CapturePointsA.json
/espetro edit configsave                   # 不满意可继续改
/espetro editsave                          # 地形 + 配置一起落盘
```

---

## 4. 数据流与写入策略

### 4.1 草稿模型

```java
record PointDraft(String name, int batch, Vec3i pos1, Vec3i pos2) {}
record SpawnDraft(Pose attack, Pose defend) {}             // Pose = x,y,z,yaw
record VehPointDraft(String id, Pose attack, Pose defend) {}

class MapEditDraft {
    String mapFolder;
    String captureTarget;            // "legacy" 或 "Points/xxx.json"
    Map<String, PointDraft> points;  // LinkedHashMap，保序
    SpawnDraft spawn;
    List<String> vehTypes;           // 与 VehSpawn.json 的 VehTypes 同步
    Map<String, List<VehPointDraft>> vehPoints;
}
```

### 4.2 写入算法（`MapEditConfigWriter`）

1. `Path esConfig = map.templateWorldDir.resolve("EsConfig")`。
2. 备份：`world/espetro-map-backups/EsConfig-<yyyyMMdd-HHmmss>/`（整目录复制，保留 5 份）。
3. 逐文件「读-改-写」：
   - `CapturePoints.json`：替换 `plannedPoints`（按 `batch`、再按 `name` 排序）、重算 `totalBatches = max(batch)`；
     其余键（`endBehavior`、`teamReinforcements`、`raas`…）原样保留。
     若目标是 `Points/<file>.json`：保留其 `modes` / `objectiveMode`，只换 `plannedPoints`。
   - `spawn_points.json`：只改 `spawnPoints.ATTACK` / `.DEFEND`。
   - `VehSpawn.json`：合并 `VehTypes`（只增不删，删类型需显式确认）、按 `type` 覆盖 `spawn_points[type]`。
4. 临时文件 + `Files.move(..., ATOMIC_MOVE)`；UTF-8 无 BOM；`GsonBuilder().setPrettyPrinting()`。
5. 返回 `ChangeSummary{pointsAdded, pointsUpdated, pointsRemoved, spawnChanged,
   vehAdded, vehRemoved, targetFile}`，由指令以多行消息打印。

### 4.3 与 `editsave` 的衔接

`MapEditManager.save` 现在：`detachEditorForSave` → `writeBack`（region 整目录覆盖）→ `closeEditorLevel`。
新流程：**先 `configsave`**（配置写失败就中止，不动 region）→ 再写 region → 提示「需重启生效」。

---

## 5. 校验规则（保存前强制）

| 数据 | 检查 | 处理 |
|---|---|---|
| 据点 | `pos1 != pos2` | 拒绝 |
| 据点 | 三轴跨度 ≥ 3 | 拒绝 |
| 据点 | 任一边 > 256 | 警告（线上最大 A 点 212×26×153） |
| 据点 | 名称非空、去重（忽略大小写） | 拒绝 |
| 据点 | `1 <= batch <= 64` | 拒绝 |
| 据点 | 与已有点体积相交 | 警告 |
| 出生点 | 两队都存在 | 提示「另一队未设置」 |
| 出生点 | `-64 <= y <= 320` | 拒绝 |
| 出生点 | yaw 吸附 90°（可用 `--raw` 关） | 自动归一化 |
| 载具点 | `type ∈ VehTypes` | 提示一并加入 `VehTypes` |
| 载具点 | `id` 唯一 | 拒绝 |
| 载具点 | 同侧同类型两点间距 < 5 格 | 警告（沿用之前的 5 格要求） |
| 载具点 | attack/defend 缺一侧 | 警告「该点不可用」 |
| 载具点 | 删点后某编制 `entities.size() > 点数` | **拒绝并列出受影响编制**（复用 `FactionDataLoader.isCompatibleWithMap` 的规则离线复算） |

---

## 6. 世界内预览

**本期：服务端粒子，零客户端改动**

- `MapEditPreview.drawBox(ServerLevel, AABB, Color)`：12 条棱、每 2 格一个 `sendParticles` 点。
- 配色：据点青、出生点金、载具点 attack 蓝 / defend 红；尺寸越大点越稀。
- 触发：`point show` / `spawn show` / `veh show` 画 8 秒（每 40 tick 重画一次）；`/espetro edit show on`
  可持续重画（会话结束自动关）。
- 文字反馈：`player.displayClientMessage(..., true)` 的 actionbar 显示坐标/尺寸/当前草稿计数。

**后期（可选）：客户端 overlay**

参考 `client/FortificationWandOverlay.java` + `FortificationWandPacket`：可以做拖拽式编辑、名字常显。
代价：新增 `S2C` 包 ⇒ 必须 `NetworkManager.PROTOCOL_VERSION` 1.36 → 1.37，且**客户端 jar 必须同步更新**
（历史上忘记同步客户端 jar 会造成 `invalid discriminator` 静默掉线）。
⇒ 本期不采用。

---

## 7. 代码改动清单

| 文件 | 类型 | 内容 | 约行数 |
|---|---|---|---|
| `dimension/MapEditDraft.java` | 新增 | 草稿模型、磁盘镜像、增删改与变更摘要 | ~260 |
| `dimension/MapEditConfigWriter.java` | 新增 | 三文件「读-改-写」、备份、原子落盘 | ~230 |
| `dimension/MapEditPreview.java` | 新增 | 服务端粒子画框 | ~90 |
| `command/MapEditCommands.java` | 新增 | 上述指令子树 + Tab 补全 + 回显 | ~280 |
| `dimension/MapEditManager.java` | 改 | 会话持有草稿；`save` 先写配置；`abandon` 删草稿 | ~80 |
| `command/EspetroCommand.java` | 改 | 挂载新子树（`edit point/spawn/veh/configsave`） | ~10 |
| `docs/MAP_EDIT_AUTHORING_PLAN.md` | 新增 | 本文件 | — |

**合计 ≈ 950 行 Java**；不需要客户端改动、不需要升协议号、不新增依赖
（Gson 已在依赖中：`ActiveMapConfig`/`VehSpawnSnapshot` 都在用）。

---

## 8. 生效与验收

1. `/espetro edit configsave` → 回显变更摘要与目标文件路径。
2. `node build\tmp\pf.mjs read "EsWorld/<map>/EsConfig/spawn_points.json" out.json` 比对内容。
3. 重启服务器（`node build\tmp\pf.mjs action restart`），验收日志：
   - `战场地图启动准备: status=READY prepared=3 warnings=0 error=null`
   - `Points 预设已选择: CapturePointsX.json (mode=AAS, candidates=3)`（若写的是预设）
   - 无 `地图拒绝注册` / `地图缺少 Points/ 中适用于 ... 的预设`
4. 游戏内 `/espetro prestart CREATE_PLUS` → 看载具是否刷在新点、补给站是否跟着生成。

---

## 9. 风险与回滚

| 风险 | 影响 | 对策 |
|---|---|---|
| 配置写坏 / 缺必填 | 启动时 `地图拒绝注册` | 写前整目录备份 `EsConfig/`；失败自动回滚该文件 |
| 载具点减少 | 使用该类型的编制 `entities.size() > 点数` 被拒载 | 保存前校验并拒绝 |
| 写入目标是 `Points/` 预设 | 改 legacy 不生效的错觉 | 保存摘要里显式打印实际写入文件 |
| 编辑期间有人开局 | 配置与地形错位 | 已有 `GameStateManager.prestart` 守卫（编辑会话中拒绝开局） |
| `editsave` 覆盖 region | 地形回退不可逆 | 已有备份轮转 5 份（`world/espetro-map-backups/`） |

---

## 10. 分期

- **P0（本期可实现）**：point corner/apply/del/list/show、spawn set/show、veh add/del/list/show、
  capture target、configsave，并接入 `editsave`/`editabandon`。
- **P1**：客户端 overlay（拖拽编辑、常显名字，需升协议号）、`outposts.json` 编辑、
  `Points` 预设「另存为」、`map.png` 自动导出、RAAS 区域池编辑。

---

## 11. 实现状态（2026-11-05 已上线）

P0 全部落地并通过自检：

| 文件 | 说明 |
|---|---|
| `src/main/java/org/espetro/dimension/MapEditDraft.java` | 草稿模型（据点/角点/出生点/载具点）+ 校验 + 落盘（`world/espetro-edit-draft/<地图>.json`） |
| `src/main/java/org/espetro/dimension/MapEditConfigWriter.java` | 读-改-写三个 EsConfig 文件，保留未知键、原子替换、写前备份 |
| `src/main/java/org/espetro/dimension/MapEditPreview.java` | 服务端粒子预览（不升协议号；白=据点，蓝=进攻，橙=防守） |
| `src/main/java/org/espetro/command/MapEditCommands.java` | `point/spawn/veh/capture/configsave/status` 子树 + Tab 补全 |
| `src/main/java/org/espetro/dimension/MapEditManager.java` | 持有草稿、`create` 时 seed/恢复、`save` 先写配置后写区块、`abandon` 删草稿 |

验证方式：`build/tmp/selftest/MapEditSelfTest.java`（无 Minecraft 依赖，用真实
`SpawnPointsSnapshot.parse` / `VehSpawnSnapshot.parse` 回读）覆盖 35 项断言，全部通过：
未知键保留、`totalBatches` 重算、单侧出生点不互相覆盖、载具点 id 稳定、半侧点被拒、空草稿生成最小结构、备份目录生成。

使用方法见 [MAP_EDIT_AUTHORING_GUIDE.md](MAP_EDIT_AUTHORING_GUIDE.md)。
