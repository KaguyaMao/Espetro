# 服务端指挥官技能脚本审计（2026-09-25）

> 审计对象：`/kubejs/startup_scripts/` 三个指挥官技能脚本 + 对应的 `server_scripts/` 实现
> 方法：读服务端实际文件 + 反查 Espetro/EsPoints 源码 + 控制台实测（含 `scheduleInTicks` 长延迟实测）

---

## 0. 结论先行

1. 三份 `startup_scripts` 只负责**登记元数据**（名字/说明/图标/触发方式/冷却/可用角色），**真正的效果**在同名 `server_scripts` 文件里；两者名字一样容易误解。
2. 这三个技能是 **155火炮支援 / 无人机侦测 / 载具补给站**，冷却 180s / 60s / 120s，触发方式 选点/即时/即时。
3. **服务器上跑的三个脚本都是"首次写入时的旧版本"**：模组只补写缺失文件、**从不覆盖**（`EspetroKubeJSDefaultScripts.install()`），所以永远停在你服务器第一次启动时的那一版。
4. **第三個技能（载具补给站）已经是死技能**：模组已把它迁到 Alt 轮盘「建造工事」（工事 `espetro:vehicle_supply_station`），并且**客户端硬编码拦截**它（`AuraTipRadialController`：`if ("vehicle_supply_station".equals(skillId)) return;`）——点它不会有任何反应，服务端那段注册+回调永远不会被走到。
5. 155 火炮的 live 版比模组自带版**旧**（落点上方 20 格 vs 180 格），但**我实测它依赖的 `scheduleInTicks` 在本服务器完全正常**（20/200/600/1400 tick 回调全部按时触发）——所以"旧版坏了"这个说法在这台机器上**不成立**，差异只是设计参数。

---

## 1. 文件与职责

| 目录 | 作用 | 何时执行 | 改了要怎么生效 |
|---|---|---|---|
| `startup_scripts/00_espetro_*.js` | 用 `EspetroCommanderSkills.create(id)...register()` 登记技能元数据 | **仅服务端启动**一次 | **必须重启** |
| `server_scripts/00_espetro_*.js` | 用 `EspetroCommanderSkills.on(id, event => {...})` 实现效果 | 启动 + **每次 `/reload`** | `/reload` 即可 |

- 每次 `/reload`：`EspetroKubeJSPlugin.registerBindings()` 会先 `clearHandlers()` 再让脚本重新注册回调；**技能定义不清空**（定义只在启动时由 startup 脚本写入，`clearDefinitions()`/`registerDefaults()` 在代码里**没有任何调用点**，属于死代码）。
- 日志证据：启动时 3 条 `已注册 KubeJS 技能:` + 3 条 `已注册 KubeJS 指挥官技能回调:`；`/reload` 后只有 3 条回调重注册（定义仍在）。
- 第 4 个文件 `00_zero_armor.js` **不是技能**：用 `ItemAttributeModifierEvent` 把全部物品的 `armor` / `armor_toughness` 清零。

## 2. 运行链路

```
左Alt → AuraTip 轮盘「战术技能」→ 槽位 action espetro:skill_activate
      → NetworkManager.sendCommanderSkillActivate(id)
      → CommanderSkillManager.activateSkill()
          ① 定义存在?  ② usableBy 角色?  ③ 阶段 DEPLOYING/BATTLE?  ④ 冷却?
          ├─ trigger=activate    → 直接构造事件（坐标=玩家当前位置）→ 调 KubeJS 回调
          └─ trigger=target_map  → 反射 EsPoints OpenArtillerySupportMapMessage 打开战术地图
                                    → 玩家点选 → EsPoints SelectArtillerySupportTargetMessage
                                    → EspetroAPI.submitCommanderSkillTarget(x,z)
                                    → 用 heightmap 求落点 Y → 构造带目标的事件（hasTarget=true）
                                    → 调 KubeJS 回调
      → 回调返回非 false ⇒ 记冷却 + 向本阵营广播「⚡ 指挥官 XX 发动了 YY！」
```

- 冷却保存在内存 `Map<UUID, Map<skillId, 结束tick>>`；**回合重置时清空**（`GameStateManager` → `CommanderSkillManager.reset()`）。
- 选点技能需要装 **ESPoints**（反射找不到 `OpenArtillerySupportMapMessage` 就提示版本不一致）。
- 需要**已加入阵营**的技能在回调里自己判断（`event.team()` 为空时 `tell` + `return false`）。

## 3. 可用的 API

### 3.1 注册（startup）

`EspetroCommanderSkills.create(id)` / `.skill(id)` → `CommanderSkillBuilder`：

| 方法 | 说明 |
|---|---|
| `.displayName(x)` / `.name(x)` | 显示名 |
| `.description(x)` / `.stats(x)` | 描述 / 附加小字（支持 `§` 颜色） |
| `.icon('espetro:textures/gui/commander_skills/xxx.png')` | 轮盘图标 |
| `.activate()` | 点击即执行 |
| `.targetMap()` / `.artilleryTarget()` | 先开战术地图选点再执行（两者等价） |
| `.trigger('activate'\|'target_map'\|'artillery_target')` | 上面两个的底层写法 |
| `.cooldownSeconds(n)` / `.cooldown(n)` | 成功后的冷却秒数 |
| `.enabled(false)` / `.disabled()` | 禁用注册 |
| `.usableBy('commander','squad_leader')` / `.roles(...)` / `.allowCommander()` / `.allowSquadLeader()` | 可用角色；**不写 = 仅指挥官** |
| `.build()` / `.register()` | 构造 / 登记 |

角色 token 别名：`commander/cmd/command/指挥官`、`squad_leader/squadleader/leader/sl/队长/小队长`（其它值忽略；全无效会告警并回退仅指挥官）。

### 3.2 实现（server）

`EspetroCommanderSkills.on(id, event => { ... })`，返回 `false` = 失败（不进冷却 + 提示）。

`KubeCommanderSkillEvent`：`commander()/commanderId()/commanderName()`、`team()`、`level()`、`server()`、`dimensionId()`、`hasTarget()`、`x()/y()/z()`、`blockX/Y/Z()`、`blockPos()`、`facingStepX()/facingStepZ()`（KubeJS 取不到 `getDirection()`，故提供）、`tell()`、`broadcastTeam()`、`broadcastAll()`、`request()`、`getOnlineCommander()`。
配套 `Espetro.*`：`phaseId()`、`getActiveBattlefieldDimension()`、`getBattlefieldSessionId()`、`isPlayerDeployed()`、`getPlayerTeam()`、`sendToPlayer()`、`server()` 等。

## 4. 三个技能的实际实现（服务端在跑的版本）

### 155 火炮支援 `artillery_155`（180s，选点，仅指挥官）
- 实体 `superbwarfare:mortar_shell`，向下初速 1.5，**散布半径 90 格**（以选点为圆心、圆内均匀取样）。
- 时序：校射 2 发（0 tick / 50 tick≈2.5s）→ 20s 起 6 轮 × 4 发，每轮间隔 4s，**共 26 发**。
- 生成高度：`选点Y + 20`。
- 跨局保护：每次落弹前校验 阶段/维度/sessionId，战局变了就取消剩余波次。
- 排定机制：`server.scheduleInTicks`（live 版）。

### 无人机侦测 `drone_detection`（60s，即时，仅指挥官）
- 对 100 格内**已部署**（`Espetro.isPlayerDeployed`）的**敌方**玩家上 `minecraft:glowing` 10 秒，排除自己；回执"发现敌人: N"。

### 载具补给站 `vehicle_supply_station`（120s，即时，仅指挥官）——**已废弃**
- live 版：指挥官前方 3 格直接 `createEntity('dragonrise_reforge:ammo_supply_station')` + 放 `minecraft:barrel` + 打队伍 tag，含"前方空间不足/补给箱被阻挡"检查。
- 现状：**客户端拦截**该技能 ID，不会发送激活包；官方改用 Alt 轮盘「建造工事」里的工事 `espetro:vehicle_supply_station`（实体/回退方块/费用/权限都在 `config/espetro/fortifications.json`）。

## 5. 三处版本差异（live ≠ 模组自带 ≠ 文档）

| 项 | 服务端在跑 | 部署 jar 自带（= 当前仓库资源） | `docs/COMMANDER_SKILL_SCRIPTS.md` 描述 |
|---|---|---|---|
| 火炮实现标识 | `scheduled-waves-20260727` | `wave-queue-20260729` | `espetroFirePureKubeArtillery` |
| 排定机制 | `server.scheduleInTicks` | 自建 `ServerEvents.tick` 队列 | 同左（tick 队列） |
| 落点高度 | 选点Y **+20** | 选点Y **+180** | `launchHeight 600` + 斜向发射 |
| 炮弹 | `superbwarfare:mortar_shell` 垂直下落 | 同左 | `minecraft:tnt`（Fuse）+ 从 260 格外斜射 |
| 校射间隔 | 50 tick（2.5s） | 400 tick（20s） | 400 tick |
| 覆盖起点 | 20s | 40s | 40s |
| 无人机角色 | 仅指挥官（未写 `usableBy`） | 指挥官 **+ 小队长** | 文档示例也未写 usableBy |
| 载具补给站 | 指挥官技能（直接部署，**已死**） | startup 文件已清空（"已迁至建造工事"） | 明确写"已移入建造工事，不再注册" |

**关于"旧版是否坏掉"的实测**：模组新版注释称"部分 KubeJS/Forge 组合会丢失较长延迟的回调"，live 版正依赖 `scheduleInTicks`。我用临时脚本在本服务器实测：**20 / 200 / 600 / 1400 tick（最长 70 秒）四个回调全部按时触发**（已删除该临时脚本，改名为 `.disabled`）。结论：这台机器上**没有**这个问题，live 版火炮不会因此丢波次。真正的差异是**落点高度 20 vs 180**：散布半径 90 格时，20 格高度在丘陵/建筑区可能把炮弹生成进山体，导致哑弹（脚本会打 `wave failed to spawn N/M shells` 警告，可据此判断是否实际发生）。

## 6. 问题清单（按影响排序）

| # | 问题 | 影响 | 建议 |
|---|---|---|---|
| 1 | `vehicle_supply_station` 技能已废弃但服务端仍注册（startup + server + 图标） | 无功能（客户端拦截），但轮盘/日志里仍存在，易误以为可用 | 清空 startup 文件 + 把 server 脚本改名 `.disabled`（与官方默认一致） |
| 2 | 火炮落点高度 20 格 | 复杂地形可能哑弹；命中时间点与 jar 默认差 6 秒下落时间 | 改成 180（只改一个数），或整套换成 jar 默认版 |
| 3 | 技能定义完全依赖 startup 脚本，且 `registerDefaults()` 是死代码 | 删掉 startup 文件后技能彻底消失（只有"文件不存在"时才会重新生成，需重启） | 知悉即可；若想加保险可让 Java 在服务器启动时兜底注册 |
| 4 | 文档与代码不一致（火炮默认实现、`00_espetro_commander_skills.js` 自动改名迁移在代码里查不到） | 照文档改会得到与 jar 不同的行为 | 我把文档更新到当前实现 |
| 5 | 无人机技能仅指挥官可用（jar 默认 commander+squad_leader） | 小队长看不到该技能 | 按你的设计决定 |
| 6 | 冷却为内存态、回合重置清空 | 跨局不保留（设计如此）；重启即全部清零 | 知悉即可 |
| 7 | 同目录 `esvehhp_ammo.js:9`、`esvehhp_tacz_caliber.js:12` 引用未定义的 `EsVehEvents` | 每次 `/reload` 报 2 条 KubeJS 错误（既有噪声，与技能无关） | 需要的话我顺手修 |

## 7. 变更记录（2026-09-25 已实施）

| 项 | 内容 | 生效方式 | 当前状态 |
|---|---|---|---|
| **155 火炮预警** | 新增 `var EspetroArtilleryStartDelayTicks = 30 * 20`；校射 1 从 0 → **600 tick（30s）**，校射 2 = 延迟+2.5s，覆盖 6 轮 = 延迟+20s 起、每 4s 一轮（即 30s / 32.5s / 50~70s） | `/reload` | **已生效**（日志 `artillery_155 script loaded: scheduled-waves-30s-delay-20260925`） |
| **无人机侦测** | ① `server_scripts`：`EspetroDroneDetectionDisabled = true`，点击即提示"§c无人机侦测已暂时停用。"且不进冷却（热禁用）；② `startup_scripts`：加 `.disabled()` | ① `/reload`；② **需重启** | ① **已生效**；② 已写盘，**下次重启后从轮盘彻底消失** |
| 火炮元数据文案 | startup `.stats('§8释放后30秒开始 \| 两轮炮击 \| 冷却: 180秒')` | **需重启** | 已写盘，待重启 |
| 未改动项 | 火炮落点高度仍为 `选点Y + 20`、散布半径 90 格、26 发、180s 冷却、无人机 100 格/10 秒参数 | — | — |

**恢复方法**
- 无人机：`server_scripts/00_espetro_drone_detection.js` 里把 `EspetroDroneDetectionDisabled` 改回 `false` → `/reload`（立即恢复功能）；再删掉 startup 里的 `.disabled()` → 重启后重新出现在轮盘。
- 火炮预警时间：只改 `EspetroArtilleryStartDelayTicks`（默认 `30 * 20`）→ `/reload`。
- 整体回滚：改动前原件在 `build/tmp/kjs-server-live/`（server_scripts 三个）与 `build/tmp/kjs-startup/`（startup 三个），用
  `node upload-one.mjs <本地文件> <目标目录> <文件名>` 上传后 `/reload` 即恢复。

## 7.1 变更记录：155 火炮「打密 + 威力大幅提高」（2026-09-25 已生效）

**实现方式**：改 `server_scripts/00_espetro_artillery_155.js`（`/reload` 生效，无需重启）。
调参全部集中在文件顶部常量里。

| 项目 | 改前 | 改后 |
|---|---|---|
| 战斗部 `Damage` | 60（实体默认） | **300** |
| 战斗部 `ExplosionDamage` | 100（实体默认） | **600** |
| 战斗部 `Radius` | 8 | **12** |
| 散布半径 | 90 格 | **45 格** |
| 覆盖轮次 × 每轮发数 | 6 × 4 | **8 × 6** |
| 轮间隔 | 80 tick（4s） | **50 tick（2.5s）** |
| 总发数 | 26 | **50**（校射 2 + 覆盖 48） |
| 时序（30s 预警不变） | 30 / 32.5s，覆盖 50→70s | 30 / 32.5s，覆盖 **50 → 67.5s** |
| **面密度** | 26/(90²π) | 50/(45²π) ≈ **7.7 倍** |

战斗部数值通过 `/summon` NBT 传入（`{Motion:[...],Damage:300.0f,ExplosionDamage:600.0f,Radius:12.0f}`）——
SBW 的 `FastThrowableProjectile.readAdditionalSaveData` 会读取 `Damage` / `ExplosionDamage` / `Radius` 三个键
（实体构造函数默认 60 / 100 / 8，见 `MortarShellEntity` 字节码）。

**效果推算（爆心、不含爆炸距离衰减）**，按载具 `DamageModifiers` 里
`@superbwarfare:mortar_shell` × `All * 0.2` × `superbwarfare:custom_explosion` 结算：

| 目标 | 抗性链倍率 | 改前 100 → 每发 | 改后 600 → 每发 | 打爆所需发数（改前 → 改后） |
|---|---|---|---|---|
| BMP-2 / ZBD-04A / ZBL-08 / BTR-82A / M3A3 | 0.1034× | 10.3 | **62.0** | 32–44 发 → **6–8 发** |
| ZTZ-99A / M1A2 SEPv2 | **0.0087×** | 0.9 | **5.2** | 635 发 → **106 发**（仍几乎无效） |

> ⚠ **主战坦克仍然免疫火炮**：其抗性表里 `superbwarfare:custom_explosion * 0.0433333333`
> 把爆炸伤害压到 0.87%。要让火炮能威胁主战坦克，必须改载具侧这一条（可按目标发数反推）。

**⚠ 未能在无玩家环境下实测**：本服 `simulation-distance=24`，实体 tick 由玩家驱动；
无人在线时连**原版 TNT 都不会下落/引爆**（已用对照实验确认），所以任何"打一发看掉血"的验证
在该环境下都无效（先前得出的"炮弹不飞/爆炸 0 伤害"结论已作废，是测试环境问题）。
验收请在有人的战局里点一次火炮。

**其他**：炮击仍破坏地形（`ExplosionDestroy` 默认 true，半径 12 后更明显），
要关闭在 NBT 里加 `ExplosionDestroy:0b`（注意可能因炮弹落进方块而降低伤害）。
技能说明文案已改为「释放后30秒开始 | 8轮密集覆盖 | 冷却: 180秒」，属 startup 元数据，**需重启**才显示。
回滚：改前原件在 `build/tmp/kjs-server-live/00_espetro_artillery_155.js`。

## 8. 待你决策

1. **155 火炮**：只把落点高度 20 → 180？还是整套替换成 jar 自带的 `wave-queue-20260729`（时序同时后移 20 秒）？
2. **无人机侦测**：是否开放给小队长（`usableBy('commander','squad_leader')`，需重启生效）？
3. **载具补给站技能**：是否清理（startup 清空 + server 脚本 `.disabled`）？
4. 是否需要我**新增技能**（例如烟幕/空袭/侦察机）？文档第 233 行有现成示例，我可以按需写好并部署。
