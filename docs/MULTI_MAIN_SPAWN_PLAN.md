# 多主基地重生点 + 抢滩前哨载具出生点：实现方案

> 状态：**已实现并上线**（Espetro `1.1.3-l` / EsPoints `1.1.1e`，协议号 `1.36` → **`1.37`**，双端必须同版本）。
> 上线记录：`Espetro 网络协议 1.37: 已注册 78 个网络包`、`战场地图启动准备: status=READY prepared=4 warnings=0`、`Done (17.984s)!`、`EsFactions 已冻结: 20 个编制, 288 个职业`。

## 实现状态（2026-11-10）

| 条目 | 落点 |
|---|---|
| 多主基地数据 | `mapconfig/SpawnPointsSnapshot`（数组或旧对象都可解析，`MAX_MAIN_SPAWNS = 8`） |
| 运行时重生点 | `team/SpawnPointConfig`（`Map<String, List<SpawnPoint>>`，`getSpawnPoint(team)` = 索引 0 兼容壳） |
| 屏障半径/高度 | `game.json` 的 `main_base_barrier_radius`（默认 45，= 半边长）/ `main_base_barrier_height`（默认 12） |
| 每点补给 | `DeploySupplyStationPlacer`（弹药箱）+ `VehicleManager.spawnMainBaseSupplyStations`（载具补给站）逐点放置 |
| 部署界面 | `NetworkManager` 追加 `BastionItem.TYPE_MAIN`（UUID 高位 1L），`UnifiedDeployScreen` 渲染 `◆ 主基地 #n`，命令 `bastion deploy <n>` |
| 前哨载具点 | `VehSpawn.json` 的 `outpost_spawn_points` + `outpost_activate_after_batch`；`vehicle/OutpostSpawnGate` 门控；`VehicleManager.resolveSpawnDeployment` 解锁后优先 |
| 解锁信号 | EsPoints `CapturePointManager` 批次完成 → `EspetroAPI.onCaptureBatchCompleted(batch, team)` → 闸门 + 前哨补给站 |
| 舰船封闭 | `AmphibiousDeployment` 屏障自动高度（包围盒 +2）与 `MAX_BARRIER_BLOCKS = 200_000` 上限 |

## 0. 需求与拆解

用户需求原文（m14283）：

1. 多个主基地重生点，攻/守双方都可以有多个；**屏障保护以第一个主基地重生点为准**（含两栖攻击舰）；
2. 主基地屏障范围（半径）可自由配置；
3. **每个重生点各有一个载具补给站和主基地弹药箱**；
4. 这些重生点要出现在部署界面的「重生点选择框」里；
5. 抢滩登陆前哨载具出生点：占领第一个批次点位后生效；出生在这些点的载具**在生效时开始 CD**。
6. 补充（m14398-4）：部署阶段的**两栖攻击舰必须被屏障完全包裹**（现状是敞口，见 §2.3）。

拆成三条独立机制：

| 机制 | 本质 | 落点 |
|---|---|---|
| A. 多主基地 | 一个队伍从「1 个重生点」变成「N 个重生点（有序）」 | `SpawnPointConfig` + 部署界面 + 命令 |
| B. 前哨载具点 | 一种「稍后解锁」的额外载具槽位 | `VehSpawnSnapshot` + `VehicleConfig` + `VehicleManager` |
| C. 舰船封闭屏障 | 两栖舰船的屏障从「齐甲板开口」变成「整船封闭」 | `AmphibiousConfig` + `AmphibiousDeployment`（仅配置语义，几何算法不动） |

难点不在数据，而在**四处语义**：
- 「第一个」的定义（屏障/无敌区只认索引 0，其余点只提供出生与补给）；
- 「前哨点在生效前不可用」不能被 `findAvailableSlot` 或自动刷新偷偷用掉；
- 「生效时开始 CD」要精确对齐载具刷新计时器（而不是让车立刻刷出来）；
- 「完全包裹」不能把玩家永远关在船里（开战必须整体拆屏障，且顶盖高度要让甲板站人有 2 格净空）。

## 1. 配置格式

### 1.1 `EsConfig/spawn_points.json`（多主基地）

现有格式（单点，必须继续兼容）：

```json
{ "spawnPoints": { "ATTACK": {"x":-185,"y":71,"z":-243,"yaw":0},
                   "DEFEND": {"x":2006,"y":72,"z":-1579,"yaw":180} } }
```

新格式（数组；**索引 0 = 第一主基地**，即屏障/无敌区的中心）：

```json
{
  "spawnPoints": {
    "ATTACK": [
      {"name":"登陆滩头","x":1188.5,"y":27,"z":2113.5,"yaw":180},
      {"name":"北侧集结点","x":1201.5,"y":27,"z":2088.5,"yaw":180}
    ],
    "DEFEND": [
      {"name":"主基地","x":2384,"y":42,"z":578,"yaw":180},
      {"name":"西侧备用","x":2306,"y":44,"z":612,"yaw":90}
    ]
  }
}
```

解析规则（`SpawnPointsSnapshot`）：
- `ATTACK`/`DEFEND` 既可以是对象（旧格式，自动包成 1 元素列表），也可以是数组；
- 数组非空、每项坐标必须有限；`name` 可省略（默认「主基地 #n」）；
- 只保留前 `MAX_MAIN_SPAWNS = 8` 个（超出的记 warning 丢弃，防止部署界面被刷爆）。

### 1.2 `EsConfig/game.json`（屏障半径）

新增两个键（缺省值 = 现状）：

```json
{ "main_base_barrier_radius": 45, "main_base_barrier_height": 12 }
```

- `main_base_barrier_radius`：**半边长**（当前代码 `ATTACK_WAITING_BARRIER_SIDE = 90`，即 ±45）；
- `main_base_barrier_height`：当前 `HEIGHT = 12`；
- 老地图不写这两键 → 45 / 12，行为与现在完全一致；
- `main_base_invulnerability_radius`（现有，默认 150）保持独立，仍以索引 0 为中心。

### 1.3 `EsConfig/VehSpawn.json`（前哨载具点）

主点结构不变，新增两个可选字段：

```json
{
  "VehTypes": ["mgs","ifv","acv","apc","transport_helicopter"],
  "spawn_points": {
    "mgs": [ {"id":"mgs_1","attack":{...},"defend":{...}} ]
  },

  "outpost_spawn_points": {
    "mgs": [ {"id":"mgs_out_1","attack":{"x":1090.5,"y":28,"z":2101.5,"yaw":-90},
                                  "defend":{"x":2410,"y":44,"z":600,"yaw":180}} ]
  },
  "outpost_activate_after_batch": 1,
  "outpost_activate_mode": "batch_complete",
  "outpost_activate_team": "both"
}
```

- `outpost_spawn_points.<type>`：与 `spawn_points.<type>` 同构，但**追加在主点之后**（槽位索引不重排）；
- 每个点同时带 `attack`（进攻方脚下）与 `defend`（防守方脚下）两个坐标 —— **槽位天然分队**：进攻方只能用 `attack` 侧、防守方只能用 `defend` 侧，敌人看不到也刷不出（m14447-1）；
- `outpost_activate_after_batch`（默认 1）：第 N 批点位完成后解锁；
- `outpost_activate_mode`：`batch_complete`（该批全部占领，默认）或 `any_point`（该批任意一点被占即解锁）；
- `outpost_activate_team`：`attack` / `defend` / `both`（默认 `both`）—— 决定哪一侧的前哨点跟随该批次事件解锁；
- 未写 `outpost_spawn_points` 的地图 = 无前哨点，零影响。

## 2. 机制 A：多主基地重生点

### 2.1 状态层

`src/main/java/org/espetro/team/SpawnPointConfig.java`

- `Map<String, SpawnPoint>` → `Map<String, List<SpawnPoint>>`；
- 保留全部旧 API 作为「取索引 0」的兼容壳：
  - `getSpawnPoint(String team)` → 列表首元素（**11 个调用点不动**：`BastionEventHandler:103`、`DeploySupplyStationPlacer:60`、`VehicleSupplyActionPacket:371`、`MainBaseProtection:52`、`ClassEquipmentZones:31/51`、`GameStateManager:875/965/1122/1418/1926/2113`、`VehicleManager:986/2258`）；
  - `setSpawnPoint(team,x,y,z,yaw)` → 清空后写入 1 个（旧行为）；
  - 新增 `getSpawnPoints(String team)`、`getSpawnPoint(String team,int index)`、`setSpawnPoints(String team, List<SpawnPoint>)`、`mainSpawnCount(team)`。
- 列表不可变（`List.copyOf`），保证读线程安全。

`src/main/java/org/espetro/mapconfig/SpawnPointsSnapshot.java`：解析数组/对象两种形态（§1.1）。

`src/main/java/org/espetro/mapconfig/GameConfigBridge.java:46/:51`：改为 `setSpawnPoints("ATTACK", snapshot.attack())`。

`src/main/java/org/espetro/naval/AmphibiousDeployment.java:208`：两栖甲板点只覆盖**索引 0**，其余攻方主基地来自地图配置（舰上只允许 1 个出生点，符合实际）。

### 2.2 屏障与保护

- `src/main/java/org/espetro/team/GameStateManager.java:63-66`：`ATTACK_WAITING_BARRIER_SIDE/HEIGHT` 改读 `GameConfig.getMainBaseBarrierRadius()*2` / `getMainBaseBarrierHeight()`；`placeAttackWaitingBarrier()`(:951) 中心仍取 `getSpawnPoint("ATTACK")`（= 索引 0）。
- `src/main/java/org/espetro/protection/MainBaseProtection.java:40-56`：保持「索引 0 + `main_base_invulnerability_radius`」不变 —— 需求只要求屏障以第一点为准。
- 两栖布局时屏障仍走 `AmphibiousDeployment.barrierBlocks()`（船壳），不受半径影响。

### 2.3 两栖攻击舰：部署阶段「完全包裹」

现状（`EsWorld/island/EsConfig/amphibious.json`）：`lhd_01` 的 `barrier={mode:"box", height:31, cap:false}` — 只围到 `y=47`（甲板 46 上方一格），**顶部敞开**，船体上层建筑（该船 `size=274×70×52`、`anchor.y=17` ⇒ 最高到 `y=86`）完全露在外面，不满足「完全包裹」。

几何事实（`src/main/java/org/espetro/naval/BarrierShell.java:28-56`）：
- 四壁：`y ∈ [min.y, min.y+height-1]`，沿 bbox 边界；
- 顶盖：`cap=true` 时在 `y = min.y+height` 铺满整个 bbox 截面（单层）。

改法（两处，都不改几何算法）：

1. **`barrier.height` 支持自动**：`AmphibiousConfig.Ship` 的 `barrierHeight` 允许 `0`（或 `"auto"`），`AmphibiousDeployment` 在算出 `bounds` 后替换为
   `height = (bounds.maxY - bounds.minY) + 2` —— 四壁到船顶上一格、顶盖再上一格，整船封闭（对 075：`17..88`，顶盖 `y=88`）。
2. **`barrier.cap` 默认改为 `true`**（`box` 模式）；显式写 `cap:false` 才保留开口（例如想从甲板下船）。

配套：
- 放置仍走 `GameStateManager.setTemporaryBarrier`（已存在非空气/有碰撞的方块会被跳过 → 船体自身充当墙，不会漏）；移除仍由 `attackWaitingBarrierBlocks` 回滚（`removeAttackWaitingBarrier`），开战即拆，不会把玩家永久关在船里。
- 规模上限：`BarrierShell.box` 产物数 ≤ `MAX_BARRIER_BLOCKS = 200_000`，超出整船拒载并记 `屏障方块过多: N`（075 实测约 6.1 万格 ≈ 153 tick 放完，`BARRIER_BLOCKS_PER_TICK = 400`）。
- 驱逐舰 `ddg_01` **同样封闭**（m14447-2）：改为 `barrier={"mode":"box","height":"auto","cap":true}`；它没有出生点与载具点，封闭只影响观感与「部署阶段不能提前摸到船」。

### 2.4 每个重生点的补给站与弹药箱

- `src/main/java/org/espetro/logistics/DeploySupplyStationPlacer.java:52-71`：`placeAtSpawnPoints` 从「每队 1 次」改为「每队 × 每点」；`placeOne` 标题追加 `#index`（「主基地弹药箱 #2」）；
- `src/main/java/org/espetro/vehicle/VehicleManager.java:2230 spawnMainBaseSupplyStations` 同理循环全部点（`spawnOneMainBaseSupply`(:2296) 只按点放，不改偏移逻辑）。

### 2.5 部署界面（重生点选择框）

**协议**（`PROTOCOL_VERSION` 1.36 → **1.37**）：

`src/main/java/org/espetro/network/UnifiedDeployScreenPacket.java:586-670` 的 `BastionItem`：
- 新增 `public static final String TYPE_MAIN = "main";`
- 新增字段 `int index`（主基地序号；非主基地为 -1），随 `write/read` 走 varint；
- 主基地项：`id = new UUID(0, index+1)`（现有两参构造用 `id.getMostSignificantBits()==0` 判 OUTPOST，必须改成显式 `type`，避免与主基地冲突）。

`src/main/java/org/espetro/network/NetworkManager.java`：
- `sendDeployPointSync`(:1691) / `buildDeployPointItems`(:1726)：在列表**最前面**追加本队全部主基地项（`type=main`、`index=i`、`name`、`pos`）；
- 保留 `deployPos`（= 当前选中主基地坐标）与 `hasDeploy`。

**客户端**：
- `src/main/java/org/espetro/client/gui/UnifiedDeployScreen.java:2510-2525`：把单个「主基地」按钮改成按 `index` 循环的按钮组，文案 `§e◆ 主基地 #<i+1> §7(<name> <x,y,z>)`，命令 `"bastion deploy " + index`；
- 兵站/前哨循环（:2527 起）不变；
- `src/main/java/org/espetro/client/gui/DeployPointSelectScreen.java:53-60`：同一改法（若该屏仍在用）。

**命令**：
- `src/main/java/org/espetro/bastion/BastionCommand.java:36-43`：`/bastion deploy [index]`（省略 = 0，保持旧用法）；
- `:120 respawnAtDeployPoint(player, index)`：越界 → `§c该主基地重生点不存在`；
- `src/main/java/org/espetro/bastion/BastionManager.java:1700 respawnAtDeployPoint(level, player, BlockPos pos)`：新增坐标参数（原方法读 `playerDeployPoints`），玩家指定点后同样 `setRespawnPosition`。

**中途加入**：`GameStateManager:2113` 仍是索引 0（默认点），玩家进场后再用界面切到别的主基地。

## 3. 机制 B：抢滩前哨载具出生点

### 3.1 数据层

`src/main/java/org/espetro/mapconfig/VehSpawnSnapshot.java`
- 新增 `Map<String, List<SpawnPoint>> outpostPointsByType`、`int outpostActivateAfterBatch`、`OutpostMode outpostMode`；
- `parse` 读 `outpost_spawn_points`（同构校验，缺 attack/defend 的点丢弃并 warning）；
- 新增 `List<SpawnPoint> allPoints(String type)` = 主点 + 前哨点（顺序固定，用于槽位映射）。

`src/main/java/org/espetro/vehicle/VehicleConfig.java:198-256`
- `points = map.vehSpawn.allPoints(type)`；
- `entities.size() > points.size()` 仍整份跳过（追加前哨点只会放宽，不会让现有编制失效）；
- `VehicleSlotConfig` 增加 `boolean outpost`（`i >= mainPoints.size()`）；
- `cfg.max` 维持 `entities.size()*perMax` —— 前哨点不增加总量，只增加「可用的落点」。

### 3.2 生效门控

新增 `src/main/java/org/espetro/vehicle/OutpostSpawnGate.java`（服务端单例）：
- `boolean active(String team, String type, int slotIndex)`：**按队伍独立**（`Map<String,Boolean> unlockedByTeam`），对方队伍的前哨点对本队永远不可见/不可用；
- `void unlock(ServerLevel level, String team, String reason)`：只解锁该队，然后对该队**每个含前哨槽位的 (faction, type)** 逐个槽位调用 `VehicleManager.startRespawnCooldown(...)`（=`scheduleAutoRespawn` 的公开封装），实现「出生点生效时开始 CD」；
- `void reset()`：新一局/换图时清空。

改点：
- `VehicleManager.findAvailableSlot`(:1835)：跳过 `slot.outpost && !OutpostSpawnGate.active(...)` 的槽位；
- `VehicleManager.deployVehicle` 的槽位扫描同样跳过；
- `tryAutoRespawn`(:~520)：`slotIndex` 已由 `findAvailableSlot` 过滤，无需额外判断；
- 开局初始化（`startBattle`/`VehicleManager.resetAll`）：前哨槽位**不**下发初始刷新队列。

### 3.3 激活信号

现有链路只有 `src/main/java/org/espetro/api/EspetroAPI.java:99 onCapturePointCaptured(String capturingTeam)`（只播音效，无批次身份）。

方案（二选一，推荐 A）：

**A. 由 EsPoints 主动回调（精确）**
- `D:\minecraft\modp\EsPoints\src\main\java\com\example\espoints\capturepoint\CapturePointManager.java`（`currentBatch`:84、`totalBatches`:85、`attackBatchCompletionReinforcement`:101）在批次推进处调用
  `org.espetro.api.EspetroAPI.onCaptureBatchCompleted(int batchIndex)`（新增，反射/软依赖调用避免硬依赖）；
- Espetro 端 `onCaptureBatchCompleted` → `OutpostSpawnGate.unlock(...)`（`batchIndex >= outpostActivateAfterBatch` 时）；
- `any_point` 模式由 EsPoints 在「首个点位被占」处回调 `onCapturePointCaptured` 扩展一个 `pointId/batch` 重载。

**B. 兜底（不改 EsPoints）**
- Espetro 在 `onCapturePointCaptured` 上累加计数，达到 `capture_points_required` 时解锁（配置默认 = 首批点位数）。精度差一两个点，但无需跨模组改动。

### 3.4 前哨点的补给站

`sink`：`VehicleManager.spawnPadSupplyStations`(:2187) 会遍历所有点；前哨点尚未生效时**不**放站 —— 在 `unlock` 时补放该点补给站（复用一个 `placePadSupplyStation(level, pose, outpost=true)`）。

## 4. 边界与兼容

| 场景 | 处理 |
|---|---|
| 老地图（无新字段） | 全部走旧路径：1 个重生点、无前哨点、屏障 90/12 |
| 两栖地图 | 攻方索引 0 = 甲板点（`override_spawn.attack`）；屏障走船壳；前哨点可配在滩头 |
| 编制刚好卡点数 | 追加前哨点只会放宽 `entities.size() <= points.size()` |
| 前哨车先被摧毁 | 未解锁前不会存在，无需处理 |
| 中途换图 / 重开一局 | `OutpostSpawnGate.reset()`；`SpawnPointConfig` 由 `applyRound` 重写 |
| 玩家在编队前切主基地 | `BastionCommand` 校验 `isWaitingForBastion` + 队伍一致 |
| 敌方想用对方前哨点 | 结构上不可能：槽位按队伍取 `attack`/`defend` 侧，门控也按队伍独立 |
| 客户端旧版本 | 协议号 1.37 变更 → 启动即被拒（不再静默断连） |

## 5. 改动清单与工作量

| 文件 | 改动 | 估行 |
|---|---|---|
| `team/SpawnPointConfig.java` | 列表化 + 兼容壳 | 110 |
| `mapconfig/SpawnPointsSnapshot.java` | 数组解析 | 70 |
| `mapconfig/GameSettingsSnapshot.java` / `config/GameConfig.java` | 屏障半径/高度键 | 40 |
| `mapconfig/GameConfigBridge.java` | 写列表 | 30 |
| `team/GameStateManager.java` | 屏障尺寸读配置 | 40 |
| `logistics/DeploySupplyStationPlacer.java` | 遍历多点 | 70 |
| `mapconfig/VehSpawnSnapshot.java` | 前哨点解析 | 90 |
| `vehicle/VehicleConfig.java` | slot.outpost + allPoints | 60 |
| `vehicle/OutpostSpawnGate.java`（新） | 门控与解锁（按队伍） | 165 |
| `vehicle/VehicleManager.java` | 主基地补给多点、槽位过滤、解锁补放 | 150 |
| `api/EspetroAPI.java` + EsPoints 回调 | 批次事件 | 60 |
| `network/UnifiedDeployScreenPacket.java`、`NetworkManager.java` | 主基地项 + 协议 1.37 | 180 |
| `client/gui/UnifiedDeployScreen.java`、`DeployPointSelectScreen.java` | 按钮组 | 150 |
| `bastion/BastionCommand.java`、`BastionManager.java` | index 参数 | 90 |
| `naval/AmphibiousConfig.java`、`AmphibiousDeployment.java` | `barrier.height` 自动 + `cap` 默认 true + 方块数上限 | 45 |
| **合计** | | **≈ 1,350 行** |

## 6. 验收清单

1. 旧图（CREATE_PLUS）开一局：行为与现在完全一致（屏障 ±45、1 个主基地、2 个补给站）。
2. 配 2 个主基地的图：部署界面出现「主基地 #1/#2」；分别选 #1/#2 复活在对应坐标；每点各一套弹药箱 + 载具补给站。
3. 屏障只包住 #1（日志 `已放置攻方等待屏障` 的中心 = #1 坐标；观察 #2 无屏障）。
4. 无敌区仍以 #1 为中心（`MainBaseProtection` 日志/实测）。
5. 前哨载具点：开图时该点在车辆信息界面不可部署、不自动刷新；该批点位**全部**占领后日志出现 `前哨载具点已解锁: <type>×<n>，开始刷新计时`，CD 到期后车辆出现在前哨点。
6. 两栖岛图部署阶段：舰船四壁 + 顶盖完整封闭 —— 从甲板（`y=46`）向上跳/飞行到 `y=88` 撞顶盖；船外水下/空中无法进入；开战瞬间屏障整体消失（`attackWaitingBarrierBlocks` 回滚，玩家可正常下船）。
7. 客户端旧 jar（1.36）连接 → 握手直接拒绝并提示版本不符。
8. 分队隔离：进攻方车辆信息界面看不到自己那侧以外的前哨点；防守方同理；一侧解锁不会让另一侧提前刷车。

## 7. 已定案（m14398 / m14447）

1. 屏障半径 = **半边长**，写进**地图 config**：`game.json.main_base_barrier_radius`（+ `main_base_barrier_height`）。
2. 前哨解锁 = **该批点位完全被占领**（`outpost_activate_mode` 默认 `batch_complete`；`any_point` 仅保留为可选项）。
3. 协议号 1.36 → **1.37**，旧客户端连不上属预期。
4. 两栖攻击舰在部署阶段必须被屏障**完全包裹**（四壁 + 顶盖，见 §2.3）。
5. 前哨载具点**只属于配置它的那一队**（m14447-1）：ATTACK 的 `outpost_spawn_points` 只进攻方编制的槽位表，DEFEND 同理；对方既看不到也刷不出；两侧的解锁互不影响。
6. 驱逐舰部署阶段**同样封闭**（m14447-2，见 §2.3）。

## 8. 遗留说明（无需再确认）

- 攻守两侧的前哨点各有独立的解锁门控：默认都挂在「首批点位完全被占领」这个事件上（`outpost_activate_team` 可配 `attack` / `defend` / `both`，默认 `both`）——占领方与被占领方同时获得自己的前沿载具点，但彼此只能用自己那一侧。
