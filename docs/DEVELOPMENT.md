# Espetro 开发文档

## 架构

```text
数据包 JSON
  -> FactionDataLoader / GameConfig / SpawnPointConfig
  -> GameStateManager 阶段状态机
  -> VoteManager / ClassSelectManager
  -> ClassCountManager / SquadManager
  -> StaminaManager
  -> BastionManager / TeamPackManager / VehicleManager
  -> CommanderSkillManager / EspetroCommanderSkills
  -> NetworkManager
  -> ClientGameState 与各 GUI/HUD
```

服务端是阵营、职业、小队、指挥官和部署状态的唯一权威。客户端只能发送选择请求，最终结果必须由服务端校验后同步。

## 入口与生命周期

`Espetro` 是 Forge 主入口。客户端通过 `DistExecutor` 延迟调用 `EspetroClient.init()`，避免专用服务器解析客户端类。

服务器启动顺序：

1. `FactionDataProvider` 在 `ServerAboutToStartEvent` 加载编制。
2. `Espetro.onServerStarting` 保存服务器实例并调用 `reloadAllConfigs()`。
3. 初始化记分板职业计数、兵站、队包和游戏状态。
4. 每个服务器 tick 更新阶段、运行时维护和指挥官技能；玩家 tick 更新服务端权威体力状态。
5. 停服时清空玩家职业装备并清理世界实体/内存状态。

## 对外 API

### `EspetroAPI`

```java
String team = EspetroAPI.getPlayerTeam(serverPlayer); // ATTACK / DEFEND / null
int squadId = EspetroAPI.getPlayerSquadId(serverPlayer.getUUID());
boolean leader = EspetroAPI.isSquadLeader(serverPlayer.getUUID());
boolean commander = EspetroAPI.isCommander(serverPlayer.getUUID());
boolean accepted = EspetroAPI.submitCommanderSkillTarget(serverPlayer, x, z);
var status = EspetroAPI.getCommanderSkillStatus(serverPlayer, "artillery_155");
var battlefield = EspetroAPI.getActiveBattlefieldSnapshot();
var tactical = EspetroAPI.getTacticalMapStateSnapshot();
```

调用约束：

- 这些方法读取服务端状态，只应在逻辑服务端使用。
- 玩家未在线或尚未选队时可能返回 `null`/`-1`/`false`。
- 不要缓存权限超过一个 tick；指挥官和队长可能在运行时变更。
- `submitCommanderSkillTarget` 是 ESPoints 选点后的服务端入口，Espetro 会重新校验指挥官权限、阶段和冷却，并用 `ServerLevel#getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z)` 补出 Y 坐标；`submitArtillerySupportTarget` 保留为旧名称兼容别名。
- `ActiveBattlefieldSnapshot` 已预计算底图 SHA-256 与宽高；不要再次克隆整图来扫描哈希。
- `TacticalMapStateSnapshot` 是不可变快照。内容与战场 session 未变时返回相同 revision；
  消费方应按 revision 缓存阵营视图。旧 `getFobs()`、`getRallies()` 和
  `getVehicleSupplyStations()` 仍兼容，但内部复用同一快照。

### `Espetro`

```java
Espetro.broadcastToAll("§e行动即将开始");
Espetro.broadcastToTeam("ATTACK", "§c进攻 A 点");
String team = Espetro.getPlayerTeam(player);
Espetro.reloadAllConfigs();
```

`reloadAllConfigs()` 必须在服务器主线程调用。

### KubeJS 指挥官技能

指挥官技能由 KubeJS 完整负责：

- `kubejs/startup_scripts` 调用 `EspetroCommanderSkills.create(id).register()` 注册技能元数据。
- `kubejs/server_scripts` 调用 `EspetroCommanderSkills.on(id, callback)` 实现技能效果。
- 默认脚本按技能拆分，例如 `00_espetro_drone_detection.js`、`00_espetro_vehicle_supply_station.js` 和 `00_espetro_artillery_155.js`。
- `CommanderSkillManager` 只负责权限、阶段、冷却、技能界面同步和 ESPoints 战术地图选点桥接。

技能触发支持两类：

- `activate`：点击指挥官技能后直接执行 KubeJS 回调。
- `target_map`/`artillery_target`：点击技能后打开 ESPoints 选点地图，`CommanderSkillManager` 记录待回调技能 ID，右键坐标回传后执行该技能的 KubeJS 回调。

Espetro 不提供指挥官技能效果辅助 API。默认指挥官技能使用 KubeJS 封装的原版方法实现：无人机侦测使用 `level.getPlayers()` 和 `target.getPotionEffects().add(...)`；载具补给站使用 `level.getBlock(...).set(...)`、`level.createEntity(...)`、`entity.mergeNbt(...)` 和 `entity.spawn()`；155 火炮支援脚本使用纯 KubeJS tick 调度、`level.createEntity(...)`、`entity.setPositionAndRotation(...)`、`entity.setMotionX/Y/Z(...)` 和 `entity.spawn()` 发射实体。

完整脚本结构和火炮参数见 `docs/COMMANDER_SKILL_SCRIPTS.md`。

### KubeJS

Espetro 将 KubeJS 作为硬依赖，并通过 `kubejs.plugins.txt` 自动注册 `org.espetro.kubejs.EspetroKubeJSPlugin`。

ForgeGradle 的 `runClient`/`runServer` 使用 Mojang mapped dev 环境；KubeJS 和 Architectury 发布 jar 的 mixin refmap 仍面向生产环境名称。`build.gradle` 在所有开发运行配置中设置 `mixin.env.remapRefMap=true` 和 `mixin.env.refMapRemappingFile=build/createSrgToMcp/output.srg`，确保 1.20.1 的 KubeJS/Architectury mixin 能在开发环境正确重映射。

KubeJS 脚本全局绑定：

- `Espetro`：脚本友好的 facade，提供广播、玩家队伍/职业/小队查询、兵力、阶段、兵站、队包、前哨、载具和指挥官技能入口。
- `EspetroCommanderSkills`、`EspetroCommanderSkillBuilder`、`EspetroCommanderSkillDefinition`、`EspetroCommanderSkillEvent`：指挥官技能创建、注册、回调和事件对象。
- `EspetroAPI`、`EspetroMod`、`EspetroGameConfig`、`EspetroGamePhase`、`EspetroCommanderSkillType`、`EspetroCommanderSkillTargetRequest`、`EspetroCommanderSkillStatus`、`EspetroArtillerySupportRequest`：直接暴露的 Java API/常量类。
- `EspetroClassCountManager`、`EspetroClassEquipment`、`EspetroClassSelectManager`、`EspetroFactionDataLoader`、`EspetroFactionDataProvider`、`EspetroGameStateManager`、`EspetroNetworkManager`（信道与注册）、`EspetroDeployScreenSync`、`EspetroPreMatchScreens`、`EspetroSquadSync`、`EspetroMatchBroadcasts`、`EspetroFactionDisplay`、`EspetroSquadManager`、`EspetroSpawnPointConfig`、`EspetroTeamManager`、`EspetroVoteManager`、`EspetroTroopCountManager`、`EspetroCommanderSkillManager`、`EspetroBastionManager`、`EspetroTeamPackManager`、`EspetroOutpostManager`、`EspetroVehicleConfig`、`EspetroVehicleManager`：底层管理器和工具类绑定。
- `ESPointsCommanderScriptAPI`、`ESPointsTacticalMarkerManager`、`ESPointsTacticalMarkerType`、`ESPointsTacticalMapJsonConfig`：安装 ESPoints 时可用的战术地图与标点绑定。
- `MUtil`、`MUtilMod`：MUtil 主类绑定。

`kubejs.classfilter.txt` 允许 KubeJS 通过 `Java.loadClass("org.espetro...")`、`Java.loadClass("com.example.espoints...")` 和 `Java.loadClass("se.mickelus.mutil...")` 访问 Espetro、ESPoints 与 MUtil 公开类。

```js
PlayerEvents.loggedIn(event => {
  const team = Espetro.getPlayerTeam(event.player)
  const squadId = Espetro.getPlayerSquadId(event.player)
  event.player.tell(`Team: ${team}, squad: ${squadId}`)
})

ServerEvents.loaded(event => {
  Espetro.broadcastToAll('§eEspetro KubeJS bridge loaded')
  const phase = Espetro.phaseId()
  const attackTroops = Espetro.getAttackTroops()
})

ServerEvents.tick(event => {
  const manager = Espetro.game()
  if (manager.getCurrentPhase() === EspetroGamePhase.BATTLE) {
    // 调用 GameStateManager 的公开方法
  }
})
```

KubeJS 脚本与 Java API 一样应在逻辑服务端主线程修改游戏状态。玩家未在线或尚未选队时，查询方法可能返回 `null`、`-1` 或 `false`。指挥官技能选点队列 API 保留用于兼容和观测；默认技能效果由 KubeJS `server_scripts` 执行。

## 编制与职业

### `FactionDataLoader`

```java
FactionDataLoader loader = FactionDataProvider.getOrCreateLoader();
loader.ensureLoaded(server.getResourceManager());

FactionDataLoader.FactionData faction = loader.getFaction("pla_medium_brigade");
FactionDataLoader.ClassKitData kit = loader.getClassKit("PLA_MEDIUM_ASSAULT");
Map<String, FactionDataLoader.VehicleData> vehicles =
    loader.getFactionVehicles("pla_medium_brigade");
```

返回的数组缓存用于热路径；调用方不应修改 `getFactionArray()`、`getClassesForFaction()` 和 `getClassIdsForFaction()` 的内容。

### `ClassEquipment`

```java
ClassEquipment.clearEquipment(player);
ClassEquipment.equipPlayer(player, "pla_medium_brigade", "PLA_MEDIUM_ASSAULT", "default");
```

装备字符串会拼接到 `/give` 或 `/item replace`。配置来源不可信时必须先校验物品 ID 与 SNBT，避免执行任意命令参数。

### `ClassCountManager`

```java
ClassCountManager classes = ClassCountManager.getInstance();
if (!classes.isFull("ATTACK", classId)) {
    classes.selectClass(player, classId);
}
```

该管理器同时保存玩家队伍、编制、职业和计数。换队/退出时必须调用对应清理流程并广播新的计数。
职业或装备变体实际发生变化后，管理器按活动地图
`game.class_switch_cooldown_seconds` 写入 UUID 级冷却；冷却与当前职业记录分离，只在整局状态重置时清空。

## 游戏状态

### `GamePhase`

阶段顺序：`WAITING_FOR_PLAYERS`、`ATTACK_COMMANDER_VOTE`、`DEFEND_COMMANDER_VOTE`、`ATTACK_FACTION_SELECT`、`DEFEND_FACTION_SELECT`、`FACTION_REVEAL`、`DEPLOYING`、`BATTLE`。

```java
GameStateManager state = GameStateManager.getInstance();
if (state.getCurrentPhase() == GamePhase.DEPLOYING) {
    int remaining = state.getDeployTimeRemainingSeconds();
}
```

不要在客户端直接修改 `GameStateManager`；客户端使用 `ClientGameState` 的同步快照。

### 投票和编制选择

```java
VoteManager votes = VoteManager.getInstance();
votes.castVote(voter, targetPlayerId);

ClassSelectManager selection = ClassSelectManager.getInstance();
selection.selectClass(voter, factionId);
```

名称虽然是 `ClassSelectManager`，这里管理的是双方最终“编制/阵营”投票；具体职业由 `ClassCountManager` 处理。

## 小队与队包

### `SquadManager`

```java
SquadManager squads = SquadManager.getInstance();
SquadManager.ActionResult result = squads.createSquad(player, "Alpha");
if (result.success) {
    NetworkManager.syncSquadsToTeam(result.team);
}
```

```java
int squadId = squads.getPlayerSquadId(player.getUUID());
boolean leader = squads.isSquadLeader(player.getUUID());
List<SquadManager.SquadSnapshot> view = squads.getSquadSnapshots("ATTACK");
```

快照用于网络/UI/外部模组读取，不应暴露内部可变小队对象。

### `TeamPackManager`

```java
TeamPackManager packs = TeamPackManager.getInstance();
String error = packs.placeTeamPack(player, level, targetPos);
if (error != null) {
    player.sendSystemMessage(Component.literal(error));
}
```

队长转移后调用 `handleSquadLeaderTransition`，小队改变后调用 `reconcileTeam`，以同步物品、冷却和现有队包。

## 兵站、前哨和载具

### `BastionManager`

```java
BastionData bastion = BastionManager.getInstance()
    .createBastion(level, pos, "ATTACK", "兵站 A");

BastionManager.getInstance().damageBastionCore(bastion, 1.0F, attacker);
```

`BastionData` 可保存为 NBT，但引用 `ServerLevel`，不得直接跨网络发送。客户端部署列表使用 `UnifiedDeployScreenPacket.BastionItem` DTO。

### `OutpostManager`

```java
String error = OutpostManager.getInstance().tryStartRedeploy(player);
String deployError = OutpostManager.getInstance().tryDeploy(player, index);
```

### `VehicleConfig` 与 `VehicleManager`

```java
VehicleConfig.loadConfig(server);
VehicleConfig.VehicleTypeConfig config =
    VehicleConfig.getVehicleConfig(factionId, vehicleType);

String result = VehicleManager.getInstance().deployVehicle(commander, vehicleType);
```

部署前必须校验指挥官、编制、数量上限、冷却、实体注册 ID 和安全落点。

## 指挥官技能

```java
CommanderSkillManager skills = CommanderSkillManager.getInstance();
boolean activated = skills.activateSkill(commander, CommanderSkillType.DRONE_DETECTION);
boolean scripted = skills.activateSkill(commander, "artillery_155");
int cooldown = skills.getRemainingCooldownSeconds(
    commander.getUUID(),
    CommanderSkillType.DRONE_DETECTION
);
```

现有技能：

| 技能 ID | 枚举 | 服务端行为 |
| --- | --- | --- |
| `drone_detection` | `DRONE_DETECTION` 兼容 ID | 由 KubeJS startup 脚本注册；默认 server 脚本使用 `level.getPlayers()` 和 `getPotionEffects().add(...)` 高亮范围内敌方玩家 |
| `vehicle_supply_station` | `VEHICLE_SUPPLY_STATION` 兼容 ID | 由 KubeJS startup 脚本注册；默认 server 脚本使用 `level.getBlock(...).set(...)`、`level.createEntity(...)`、`entity.mergeNbt(...)` 和 `entity.spawn()` 部署 |
| `artillery_155` | `ARTILLERY_155` 兼容 ID | 由 KubeJS startup 脚本注册为 `targetMap`；打开 ESPoints 战术地图选点，回传后执行 KubeJS 回调 |

指挥官选点技能生命周期（默认示例为 `artillery_155`）：

1. 客户端指挥官技能界面发送 `CommanderSkillPacket` 到服务端。
2. `CommanderSkillManager#activateSkill(ServerPlayer, String)` 校验指挥官、游戏阶段和对应技能 ID 的冷却。
3. 对 `target_map`/`artillery_target` 技能定义，`CommanderSkillManager` 记录技能 ID，并使用反射调用 `com.example.espoints.network.OpenArtillerySupportMapMessage#sendTo(ServerPlayer)`。ESPoints 仍是 `mods.toml` 中的 `mandatory = false` 软依赖。
4. ESPoints 客户端打开居中的 `ArtillerySupportMapScreen`，复用 `TacticalMapHUD` 的嵌入式地图渲染、滚轮缩放和左键拖拽。
5. 玩家右键地图后，ESPoints 将屏幕坐标转换为战术地图世界 X/Z，发 C2S 包回服务端，并通过 `EspetroAPI.submitCommanderSkillTarget` 交给 Espetro；旧版 ESPoints 可继续调用 `submitArtillerySupportTarget`。
6. Espetro 再次校验权限、阶段和冷却，用高度图补出 Y 坐标，创建 `CommanderSkillManager.ArtillerySupportRequest`。
7. `CommanderSkillManager` 构造带目标坐标的 `KubeCommanderSkillEvent`，并调用 `EspetroCommanderSkills` 执行该技能在 KubeJS `server_scripts` 中注册的回调；没有待回调技能 ID 时回退到 `artillery_155`。
8. 回调成功后 Espetro 记录兼容队列、同步技能冷却；默认脚本通过纯 KubeJS 队列按两批从目标上空斜向创建实体，并用 `entity.setMotionX/Y/Z(...)` 设置初速度。实体 ID、目标 Y、覆盖半径、发射源范围、速度、批次数量、批次间隔和总体入射角都由 `server_scripts` 中的 JavaScript 配置决定。

新增技能时在 KubeJS `startup_scripts` 注册技能，在 `server_scripts` 注册回调。服务端会把 KubeJS 技能元数据同步给客户端指挥官技能界面。KubeJS 已是硬依赖；若技能依赖其他第三方模组，优先使用 Forge 软依赖和反射或 IMC 等可选集成方式，避免形成循环编译依赖。

## 体力系统

`StaminaManager` 在逻辑服务端维护玩家的会话级体力。重生或重新进入时恢复到 `player_stamina`，退出或停服时清除内存状态。`player_stamina: -1` 会禁用整个系统。

- 持续奔跑开始时扣除一次 `sprint_cost_per_second`，之后每 20 tick 再扣除一次。
- 跳跃每次扣除 `jump_cost`。
- 停止消耗后按 `regen_delay_seconds` 等待，再每 20 tick 恢复一次；`full_recovery_seconds` 默认从最后一次消耗起限制为 12 秒，运行时按体力上限计算恢复量，`regen_per_second` 作为最低值。
- 客户端的 `StaminaJumpPacket` 只报告跳跃动作；是否扣除或阻止跳跃仍由服务端决定。
- `StaminaSyncPacket` 只发给对应玩家，用于更新 `StaminaOverlay` 和客户端耗尽状态。
- `StaminaOverlay` 仅在体力未满时显示，在 Action Bar 下方绘制 `40×2` 像素的无数字白线。

修改结算周期或包结构时，需要同时检查客户端抑制奔跑/跳跃、重生重置、配置热重载和专用服务器类加载。

## 网络开发

`NetworkManager.registerNetwork()` 集中注册所有 Espetro 网络包，注册通过
`network.core.PacketRegistrar` 并声明方向：

```java
PacketRegistrar packets = new PacketRegistrar(NET);
packets.toClient(MyStatePacket.class, MyStatePacket::write, MyStatePacket::read, MyStatePacket::handle);
packets.toServer(MyActionPacket.class, MyActionPacket::write, MyActionPacket::read, MyActionPacket::handle);
```

Forge 会校验方向：`toClient` 的包若从客户端发来会被丢弃，`toServer` 反之。只有请求与响应共用同一类型的包才用
`bidirectional`。注册顺序就是包 ID，增删或调整顺序时提升 `PROTOCOL_VERSION`。

包的 `handle` 统一交给 `network.core.PacketHandling`：

```java
// S→C：双层 lambda，专用服务器不会加载客户端类；异常会带包名记录日志
public void handle(Supplier<NetworkEvent.Context> ctx) {
    PacketHandling.handleOnClient(ctx, "MyState",
        () -> () -> org.espetro.client.ClientPacketHandlers.handleMyState(this));
}

// C→S：在主线程以发送者执行，发送者为空时忽略
public void handle(Supplier<NetworkEvent.Context> ctx) {
    PacketHandling.handleOnServer(ctx, player -> MyManager.getInstance().apply(player, value));
}
```

不要再用 `Class.forName(...).getMethod(...).invoke(...)` 调客户端代码：直接调用能被编译器检查签名，重命名也不会悄悄失效。

要求：

1. 客户端请求必须在服务端重新验证阶段、阵营、权限和索引范围。
2. 世界状态变更在主线程执行（`PacketHandling` 已处理）。
3. GUI 打开包只能在客户端处理；客户端逻辑放在 `ClientPacketHandlers` 或对应控制器。
4. 修改现有编码时同步升级网络协议。
5. 集合、名字和 ID 必须限制数量与长度。

## 客户端开发

`EspetroClient` 注册按键、HUD overlay、轮盘控制器和队友名牌。客户端状态分为：

- `ClientGameState`：阶段、队伍、编制及界面权限。
- `ClientTacticalState`：指挥官、小队成员和名牌颜色。
- `ClientPacketHandlers`：网络包到 GUI/状态类的分派。

GUI 基于 ApricityUI（`client.aui.AuiScreen` / `GuiElement`），可复用的部件：

- `EspetroIcons`：多处共用的 GUI 纹理，新增共用图标在这里声明。
- `hud.HudDraw`：面板、图标、图标+数值、进度条原语和补给 HUD 配色。
- `EspetroRadials`：EsRadial 轮盘的 `isShowing` / `openClick`。
- `EspetroTipNotifier`：右侧短提示（EsRadial `EsToasts`），失败时降级到聊天栏。
- `ScrollableList`：可滚动列表。

## HCR AAD 源码边界

仓库中的 `com.example.hcrpoints` 是旧副本且已从构建排除；独立项目
`/home/shu/IdeaProjects/espetro-HCR` 才是当前 ESPoints 源码。其模组 ID 是 `espoints`，
包名是 `com.example.espoints`，并依赖 Espetro 1.1.0-alpha 的公开地图快照和生命周期事件。
Espetro 将 ESPoints 声明为可选依赖，通过 `HcrTacticalMapBridge` 打开客户端战术地图，避免把
ESPoints 变成 Espetro 的强制运行依赖。

本地兄弟项目默认不加入 Espetro 开发运行环境；需要联调时传入
`-PespetroIncludeLocalEspoints=true`。开发规则：

- Espetro 阵营/职业/小队功能修改 `org.espetro`。
- 据点/战术地图/标点功能修改 `ds`。
- 如暂时无法移除旧副本，发布前至少用类清单比较两个目录，避免同名类实现不同。

## 构建与验证

EsRadial 通过 `mavenLocal` 引入，当前需要带 `EsToasts` 的 0.4.0（EsRadial 仓库 `toast-tips` 分支）。首次构建前先发布到本地：

```bash
# 在 EsRadial 仓库（toast-tips 分支）
./gradlew :forge:publishToMavenLocal
```

`libs/` 下需放置 `dragonrise_reforge-1.5.0-beta-20260921.jar`（仅编译期使用，已被 `.gitignore` 排除）。

```bash
./gradlew compileJava
./gradlew build
./gradlew runClient
./gradlew runServer
```

提交前至少验证：

- 数据包覆盖优先级与 `/reload` 热重载。
- 完整八阶段状态机和中途加入。
- 指挥官、编制、职业、小队人数和权限同步。
- 死亡扣兵力、兵站/队包/前哨复活。
- 载具上限、冷却、实体死亡回收。
- 体力每秒消耗/恢复、恢复延迟、耗尽限制、HUD 显隐和重生重置。
- 玩家退出与停服不会保留装备或运行时缓存。
- 专用服务器不加载 `net.minecraft.client` 类。
