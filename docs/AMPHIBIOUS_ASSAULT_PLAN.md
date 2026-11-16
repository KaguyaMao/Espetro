# 两栖登陆作战模式 · 实现方案

> 目标：让一张地图成为「两栖登陆作战地图」，让若干编制成为「两栖登陆专属编制」，
> 开局把两栖攻击舰（LHD）与驱逐舰作为**建筑**放到海里，攻方出生点/载具出生点都在 LHD 上，
> 并用屏障把整艘 LHD 围起来，其余流程与现有「侵攻」完全一致。

---

## 0. 可行性结论（先说结果）

现有代码已经具备 90% 的地基，本模式是「组合既有能力 + 一层新配置」，不需要改动战斗核心：

| 需要的能力 | 现有实现 | 位置 |
|---|---|---|
| 载入地图时向维度里放建筑 | 结构模板加载 + `StructureTemplate` 四向预编译 | `src/main/java/org/espetro/bastion/FortificationTemplateCompiler.java:94-127`（`resolveTemplate` 优先读 `config/espetro/fortification_templates/<name>.nbt`，回退 `server.getStructureManager().get(id)`） |
| 战场维度挂载（粘贴时机） | `importAndLoad` → `copyTemplate` → `createServerLevel` | `src/main/java/org/espetro/dimension/BattlefieldWorldManager.java:274,394,407,856` |
| 出生点覆盖 | 单一入口 `SpawnPointConfig.getSpawnPoint(team)` / `setSpawnPoint(...)` | `src/main/java/org/espetro/team/SpawnPointConfig.java:115-134` |
| 屏障（可围合、分帧放置/拆除） | 攻方等待区屏障队列：BOX 墙 + 顶盖 + `pendingBarrierPlace/Remove` | `src/main/java/org/espetro/team/GameStateManager.java:63-66,963-1060`（`ATTACK_WAITING_BARRIER_SIDE=90`、`HEIGHT=12`、`CAP_LAYERS=1`、`setTemporaryBarrier` 1026 行） |
| 编制限定（攻方编制） | 编制池按 `isCompatibleWithMap` 过滤 + `getWinningFaction` 定胜负 | `src/main/java/org/espetro/team/ClassSelectManager.java:112,234,336` |
| 区块预载（粘贴前确保区块存在） | `addSpawnArea` + `ChunkTickets` | `src/main/java/org/espetro/dimension/BattlefieldWorldManager.java:450-451`、`src/main/java/org/espetro/util/ChunkTickets.java` |

结论：**新增约 900 行 + 3 个资产（LHD/驱逐舰 `.nbt` 与示例配置）**，不需要改协议号（纯服务端）。

---

## 1. 概念与术语

- **两栖地图**：`EsConfig/amphibious.json` 存在且 `enabled=true` 的地图，启动时校验通过后标记 `amphibious`。
- **两栖编制**：编制 JSON 里 `faction.amphibious = true`（可选再给 `faction.naval_bonus`）。
- **冲滩编制**：地图上 `amphibious.assault_faction` 指定的那个编制，是这张图**唯一**可选的攻方编制。
- **旗舰（LHD）**：可站人、可停载具、带屏障的建筑结构；攻方所有出生/复活/载具部署都在其上。
- **护航舰（驱逐舰）**：纯装饰/压制火力的建筑结构，泊在旗舰外侧，不可进入。
- **甲板标记**：结构内用原版 `structure_block`（DATA 模式）标注「出生点 / 载具点 / 舰体范围」，
  粘贴时被程序读走并替换为空气。**这就是“参考原版结构方块”的落地方式**。

---

## 2. 数据设计

### 2.1 地图侧：`EsWorld/<地图>/EsConfig/amphibious.json`（新增，可选文件）

```json
{
  "enabled": true,
  "assault_faction": "plamc_5th_amphibious_assault",
  "beach_direction": 0.0,
  "water_surface_y": 63,
  "ships": [
    {
      "id": "lhd_01",
      "role": "lhd",
      "structure": "espetro:naval/lhd_075",
      "source": "config",
      "draft_adjust": 0,
      "anchor": { "x": 1680, "y": 54, "z": -1520 },
      "rotation": "none",
      "barrier": { "mode": "hull", "height": 16, "cap": true },
      "spawn_from_markers": true
    },
    {
      "id": "ddg_01",
      "role": "destroyer",
      "structure": "espetro:naval/ddg_052d",
      "source": "config",
      "draft_adjust": 0,
      "anchor": { "x": 1600, "y": 54, "z": -1440 },
      "rotation": "clockwise_90",
      "solid": true,
      "barrier": null
    }
  ],
  "protected_radius": 0
}
```

字段说明：

| 字段 | 类型 | 说明 |
|---|---|---|
| `enabled` | bool | 关闭后该图按普通侵攻跑（配置留着不删） |
| `assault_faction` | string | 冲滩编制 id。启动校验必须存在、是两栖编制、`team=ATTACK`、与地图兼容 |
| `beach_direction` | double | 冲滩方向（度），仅用于日志/攻略提示与防御方朝向提醒，不影响判定 |
| `ships[].role` | enum | `lhd` / `destroyer`。`lhd` 唯一，多于一个时只取第一个并告警 |
| `ships[].structure` | ResourceLocation | 结构 id（`espetro:naval/<name>`）。查找顺序：`config/espetro/naval_structures/<name>.nbt` → `server.getStructureManager().get(id)` |
| `ships[].source` | enum | `config`（默认，外部 nbt）/ `builtin`（jar 内置）/ `world`（船已烘在地图模板里，不粘贴只扫标记） |
| `water_surface_y` | int | **地图级海面世界 Y，必填**。每张图各自填写（`/espetro naval mapwater`）；缺失则整图按普通侵攻跑并告警（见 2A.5） |
| `ships[].water_surface_y` | int | 可选：同一张图内水域高度不同时，按舰覆盖地图级 |
| `ships[].draft_adjust` | int | 目视微调，正数=再下沉几格；只影响 `anchor.y`，默认 0，**不改 `waterline_y`** |
| `ships[].anchor` | 坐标 | 船位（x/z 生效；y 仅作参考，实际由 `water_surface_y + draft_adjust - meta.waterline_y` 反推） |
| `ships[].rotation` | enum | `none` / `clockwise_90` / `clockwise_180` / `counterclockwise_90` |
| `ships[].barrier.mode` | enum | `hull`（沿舰体外壳）/ `box`（贴最小包围盒）/ `none` |
| `ships[].barrier.height` | int | 屏障高度（从 anchor.y 起算），`hull` 模式用于封顶 |
| `ships[].barrier.cap` | bool | 是否加顶盖（防空投/防飞离） |
| `ships[].spawn_from_markers` | bool | true=从结构标记取点；false=只按 `override_spawn`（见 2.4） |
| `ships[].solid` | bool | 仅驱逐舰用：参与碰撞与保护的实心范围（装饰舰可设为纯视觉，避免玩家爬上去） |

### 2.2 编制侧：在现有编制 JSON 的 `faction` 下加两个字段

```json
"faction": {
  "name": "第5海军陆战战两栖突击旅",
  "team": "ATTACK",
  "faction_id": "PLA",
  "amphibious": true,
  "naval_kit": "lhd_075"
}
```

- `amphibious`：解析到 `FactionDefinition.amphibious`（`FactionDataLoader` 的 `@SerializedName` 风格，默认 false）。
- `naval_kit`：该编制配套的舰船结构 id（用于「这张图没配 amphibious.json，但编制是两栖的」时给出更明确的报错）。
- **不改变现有加载规则**：非两栖地图上两栖编制照常可选，只是没有舰船。

### 2.3 结构资产与标记协议

结构放两处之一（推荐前者，和工事模板同源，改起来不用重打包）：

```
config/espetro/naval_structures/lhd_075.nbt
config/espetro/naval_structures/ddg_052d.nbt
```

结构内允许出现 4 种标记方块，全部用原版 `minecraft:structure_block`（`mode=DATA`），
`metadata` 字符串即标记内容，粘贴后自动清成空气：

| metadata | 含义 |
|---|---|
| `espetro:attack_spawn` | 攻方出生点（甲板上，取方块中心 + 方块朝向作为 yaw） |
| `espetro:veh:<type>:<index>` | 该类型第 index 个载具点的 **attack 侧** |
| `espetro:veh:<type>:<index>:defend` | 同一载具点的 **defend 侧**（舰上通常与 attack 同点，或放在坞舱） |
| `espetro:hull` | 舰体外壳采样点（沿外壳放一圈，程序用它们生成屏障；`mode=hull` 时必需） |

约定：

- 出生点/载具点的 `yaw` 取标记方块的朝向（`structure_block` 的 `rotation` 或放置时的 `FACING`），
  没有朝向信息时用 `beach_direction` 的相反方向。
- 标记方块数量上限 256，超出直接拒载并报错（防呆）。
- 结构最大边长 256、最大体积 400 万方块（约 160×160×160），超出拒载。

### 2.4 兜底：`override_spawn`（不想放标记时用）

```json
"override_spawn": {
  "attack": { "x": 1690, "y": 78, "z": -1512, "yaw": 0 },
  "vehicles": {
    "mbt":   [{ "id": "mbt_1",   "attack": {"x":1700,"y":74,"z":-1518,"yaw":0}, "defend": null }],
    "ifv":   [{ "id": "ifv_1",   "attack": {"x":1710,"y":74,"z":-1518,"yaw":0}, "defend": null }]
  }
}
```

标记与 override 同时存在时：**标记优先**，override 只补缺的类型（并在日志里说明补了哪些）。

---

## 2A. 舰船资产：来源、储存、放置（详细）

这一节把「船从哪来、存哪、怎么摆上去」讲透，是 P0 落地时唯一需要人工介入的部分。

### 2A.1 来源：三条路径，按优先级叠加

| 优先级 | 路径 | 位置 / id 写法 | 谁写 | 随 jar 发布 | 说明 |
|---|---|---|---|---|---|
| 1（最高） | **服主自建** | 文件 `config/espetro/naval_structures/<name>.nbt`；配置里写 `espetro:naval/<name>` | 服主用游戏内导出命令产出 | 否 | 同名时覆盖内置；改船不用重打包 |
| 2 | **内置（数据包）** | jar 内 `data/espetro/structures/naval/<name>.nbt`；配置里同样写 `espetro:naval/<name>` | 开发者 | 是 | 升级 jar 才更新；只读 |
| 3 | **就地（模板自带）** | 配置里 `"source": "world"` 且不写 `structure` | 地图作者在模板里直接摆船 | 随地图 | 不粘贴，只在 `marker_area` 内扫标记；一张图专用 |

查找顺序与工事模板完全一致（`FortificationTemplateCompiler.resolveTemplate`，`src/main/java/org/espetro/bastion/FortificationTemplateCompiler.java:94-107`）：
先看 `config/espetro/naval_structures/<name>.nbt` 是否存在（`NbtIo.readCompressed` + `server.getStructureManager().readStructure(tag)`），找不到再 `server.getStructureManager().get(id)`。

**作者工作流（推荐路径 1）**

```
# 1) 在超平坦/编辑维度里造好整艘船（甲板上摆好标记方块，见 2.3）
# 2) 圈两个角
/espetro naval pos1          # 站在最小角
/espetro naval pos2          # 站在最大角
/espetro naval info          # 预检：尺寸、方块数、标记清单、是否超限
# 3) 导出（自动备份同名旧文件为 <name>.nbt.bak-<时间戳>）
/espetro naval export lhd_075
# 4) 服务器上让新结构生效（只读 2A.2 的缓存，不需要重启）
/espetro naval reload
```

导出实现直接复用工事编辑器那套（`FortificationAuthoringManager.java:425-442`）：

```java
StructureTemplate template = new StructureTemplate();
template.setAuthor(player.getGameProfile().getName());
template.fillFromWorld(level, min, size, includeEntities, carve ? Blocks.STRUCTURE_VOID : Blocks.AIR);
CompoundTag tag = template.save(new CompoundTag());
NbtIo.writeCompressed(tag, file.toFile());
```

也可以绕过命令：用原版结构方块 `SAVE` 存到 `saves/<world>/generated/minecraft/structures/…`，再把 `.nbt` 拷进 `config/espetro/naval_structures/` 并改名为合法 `<name>.nbt`（必须是 gzip 压缩的标准结构格式）。

**为什么默认推荐「外部 nbt」而不是「烘进地图模板」**

- 一张图一个 `.nbt` 只需几十 MB，地图模板不用为每张图重存一遍船（LHD 级别 40 万方块 ≈ 全量 region 的很大一块）。
- 换船/改船不用重新导出整张地图；同一艘船可被多张两栖图共用。
- 船与地图解耦后，`/espetro edit` 里可以先摆船预览再定 `anchor`。
- 代价：多一次粘贴耗时（预算见 2A.3）与一次结构解析内存；可用 2A.3 的分段粘贴摊平。

### 2A.2 储存：目录、元数据、加载与缓存

**磁盘布局**

```
config/espetro/
├── fortification_templates/          # 工事模板（既有）
│   └── sandbag_wall.nbt
└── naval_structures/                 # 舰船（新增）
    ├── lhd_075.nbt                   # 主体（gzip 结构 NBT）
    ├── lhd_075.meta.json             # 元数据（导出时自动生成）
    ├── lhd_075.nbt.bak-20261106-1530 # 覆盖时的自动备份
    ├── lhd_075.part0.nbt             # 可选：分段（见 2A.3）
    ├── lhd_075.part1.nbt
    └── ddg_052d.nbt
```

命名规则沿用 `FortificationTemplateStore.NAME`：`[a-z0-9_]{1,48}`（禁止路径分隔符，防目录穿越）；
目录常量建议新增 `NavalStructureStore.directory() = FMLPaths.CONFIGDIR.get().resolve("espetro/naval_structures")`。

**`<name>.meta.json`（导出时生成，加载时用来快速校验，避免每次解析大 NBT）**

```json
{
  "format_version": 1,
  "id": "lhd_075",
  "author": "boy",
  "exported_at": "2026-11-06T15:30:12+08:00",
  "mc_version": "1.20.1",
  "size": [257, 56, 32],
  "blocks": 412_336,
  "bytes": 41_208_913,
  "sha1": "9f2c…",
  "waterline_y": 9,
  "waterline_source": "auto",
  "markers": { "count": 14, "attack_spawn": 1, "veh": 12, "hull": 1 }
}
```

| 字段 | 用途 |
|---|---|
| `format_version` | 标记协议版本。将来扩展协议时递增；旧版本仍可读，未知标记忽略并告警 |
| `size` / `blocks` / `bytes` | 限额预检（见 2A.4），不合格直接拒载，不浪费一次解析 |
| `sha1` | 可选：启动时抽查，防手改 NBT 导致客户端/服务端方块不一致 |
| `waterline_y` / `waterline_source` | 相对结构的吃水线高度与来源（`auto`/`marker`/`manual`），用于自动推算 `anchor.y`（见 2A.5） |
| `markers` | 快速判断「这艘船有没有放过出生点/载具点」，缺 `attack_spawn` 时启动期就告警 |

**加载时机与缓存**

- **启动期不加载**（结构解析要几十 MB 内存）。`AmphibiousConfig` 只解析 JSON 与 meta，不读 nbt。
- **首次用于战局时加载**：`BattlefieldWorldManager.importAndLoad` 之后、`AmphibiousDeployment.apply()` 里，用 `CompletableFuture.supplyAsync(..., BattlefieldWorldManager.ioExecutor())` 读盘 + 解析，主线程只做 `get()`（带超时）。
- 缓存键 `<structure-id>`，值 `LoadedShip { StructureTemplate template, ShipMeta meta }`；`HashMap` + `synchronized` 即可（一场最多 2~3 艘）。
- **释放**：`editsave` / `editabandon` / 战局结束（`detachForDiscard`）后清空缓存，避免常驻。解析后的 `StructureTemplate` 常驻内存约为 nbt 的 3~6 倍，故 P0 限制「同场并发舰船 ≤ 2 艘」。
- `/espetro naval reload`：清空缓存 + 重读 meta，仅编辑模式/大厅可用（战局中禁止，避免半途换船）。

**失败处理**：meta 缺失或不合法 → 用「文件名 = id」的最小信息继续尝试（尺寸只能靠解析后校验）；nbt 解析失败 → 该舰跳过，日志 `[两栖] 舰船 lhd_075 加载失败`，并在启动摘要里标记这张图 `amphibious=disabled`（按普通侵攻跑），不阻断整场。

### 2A.3 放置：锚点、旋转、吃水、分帧与标记消费

**锚点与旋转**

- `anchor` = 结构**最小角**的世界坐标，与结构方块 `LOAD` 语义一致（`ships[].anchor` 字段说明见 2.1）。
- 旋转先算尺寸再定位，避免转 90° 后越界：

```java
Rotation rot = settings.rotation();                  // none / cw90 / cw180 / ccw90
Vec3i size = template.getSize(rot);                  // 旋转后的尺寸
BlockPos anchor = anchorFromDeck(deckAnchor, size, rot, meta.waterlineY(), ship.draft());
```

- 粘贴调用（`flags` 用 `2|16` = 更新客户端 + 不触发邻居更新；水上粘贴避免水回流）：

```java
StructurePlaceSettings settings = new StructurePlaceSettings()
    .setRotation(rot)
    .setIgnoreEntities(false)
    .setIgnoreStructureBlock(true);                  // 标记方块先留着，扫完再清
template.placeInWorld(level, anchor, anchor, settings, level.random, Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE);
```

**吃水 / 高度**：`anchor.y = map.water_surface_y + ship.draft_adjust - meta.waterline_y`
（地图侧海面高度 − 结构水线 + 人工微调，见 2A.5；不再用「甲板目标高度」反推，避免两处真相）。
摆完立刻 `ChunkTickets.acquire(...)`
预载全部覆盖区块（复用 `org.espetro.util.ChunkTickets`），保证同 tick 的碰撞/生成正常。

**分帧预算**：LHD 约 40 万方块，一次性粘贴会卡 1~3 s。方案：
`pasteBudgetTicksPerShip = 40`，按 y 切片（每 tick 粘 1~2 层），全部完成后才允许传送玩家；
若某张图对开局时间敏感，可用 2A.2 的分段 nbt（`part0..N`，按船长切 8~16 段，段间重叠 2 格防接缝），
每 tick 粘一段，实测总耗时相当但单 tick 峰值低。

**标记消费（结构 → 出生点/载具点）**

1. 解析阶段就读出标记，**不需要等粘贴完**：

```java
CompoundTag saved = template.save(new CompoundTag());
ListTag bes = saved.getList("block_entities", 10);       // 结构里的方块实体
// 只认 minecraft:structure_block 且 nbt.mode == "SAVE"? —— 标记用 DATA 模式 + metadata 字段
```

2. 位置换算：结构内相对坐标 `pos` 经 `StructureTemplate.getZeroPositionWithTransform(pos, Mirror.NONE, rot, sizeX, sizeZ)` 得到放置后的相对偏移，再加 `anchor`。
3. `yaw`：取标记方块 **state 的 `facing`**（`north/east/south/west` → 0/90/180/270），再叠加 `rotation`；无 facing 时用 `beach_direction` 反向。
4. 写回：
   - 出生点 `SpawnPointConfig.setSpawnPoint("ATTACK", deckPos)`（唯一入口，复活/装备区/补给站自动跟随）。
   - 载具点写进内存快照（`ActiveMapConfig.forRound` 后合并），**不落盘**（见 2.1 / §6）。
5. 清理：标记方块本身在粘贴后 `setBlock(AIR)` 覆盖（`ignoreStructureBlock(true)` 让它们先落地再被清掉）；
   标记数量 > 256 时拒载该舰（防呆，见 2.3）。

**幂等与回滚**

- 粘贴区域必须**空水域**：配置里的 `anchor` + 尺寸构成的 bbox 内不能有玩家建筑；启动期用一次「非空气方块计数」抽查（> 5% 就告警）。
- 粘贴中途失败/超时 → 丢弃整个维度并重来（复用既有 `BattlefieldWorldManager.detachForDiscard` → `importAndLoad`），不要尝试增量回滚。
- 战局结束本来就会丢维度（`discardAndDelete`），所以**不需要**为船单独做拆除逻辑；但 `/espetro edit` 预览里粘的船要能单独撤（编辑维度整块丢弃即可）。

**在编辑模式里摆船（作者体验）**

```
/espetro edit in                    # 进入编辑维度（已实现）
/espetro edit ship place lhd_075 1680 62 -1520 none
/espetro edit ship show             # 粒子画出甲板出生点/载具点/外壳采样点
/espetro edit ship clear            # 撤掉预览船（重建编辑维度）
/espetro configsave                 # 把 anchor / 标记结果写进 amphibious.json
```

### 2A.4 限额：**不能**复用工事模板的限制

工事模板的现有限额（`FortificationConfig.java:990/994/996`）：

| 字段 | 默认值 | 对舰船是否够用 |
|---|---|---|
| `maxTemplateBlocks` | 4,096 | ❌ LHD 约 40 万方块 |
| `maxTemplateAxis` | 64 | ❌ LHD 约 257 长 |
| `maxTemplateNbtBytes` | 2,097,152（2 MB） | ❌ 实测量级 20~60 MB |

舰船用**独立限额**（`amphibious.json` 的 `naval_limits`，或 `config/espetro/bastion.json` 同级）：

| 字段 | 建议默认 | 上限（硬编码） |
|---|---|---|
| `max_axis` | 512 | 1024 |
| `max_blocks` | 4,000,000 | 16,000,000 |
| `max_nbt_bytes` | 268,435,456（256 MB） | 1 GB |
| `max_markers` | 256 | 512 |
| `max_ships_per_map` | 4 | 8 |
| `paste_budget_ticks` | 40 | 200 |

超限行为：拒载该舰 + 日志明确写出「尺寸 257 > 512？」这类可读原因，并在 `/espetro naval info` 里先给出预警（导出前就挡住）。

### 2A.5 吃水线：**船与地图两侧都要写进 data**（方案 1：在水里造 + 自动取水面）

Minecraft 没有浮力系统，舰船是**静态建筑**，不会浮起来也不会自己稳下来。所以「吃水线」不是物理量，而是**对齐参数**：

- 船侧：结构局部第几层该与水面重合 → `meta.json` 的 `waterline_y`
- 地图侧：这张图的海面在世界第几层 → `amphibious.json` 的 `water_surface_y`

**两侧都必须显式写入 data，禁止靠默认值兜底**（船侧缺失→该舰拒载；地图侧缺失→整图两栖模式不启用、按普通侵攻跑并告警，见下面的校验表）。放置时只做一次减法：

```
anchor.y        = water_surface_y + draft_adjust - waterline_y   // 不靠肉眼对格子
甲板高出水面      = (size.y - 1) - waterline_y                     // 校验会不会上水
实际吃水（格）    = waterline_y - draft_adjust
```

**字段归属**

| 字段 | 写在哪个 data | 谁写 | 必填 |
|---|---|---|---|
| `waterline_y` | 结构 sidecar `<name>.meta.json` | `naval export` 自动算（方案 1）或手动 `--waterline` | ✅ |
| `waterline_source` | 同上 | `auto` / `marker` / `manual`，留痕便于排查 | ✅ |
| `water_surface_y` | 地图 `EsConfig/amphibious.json`（**地图级**） | `/espetro naval mapwater`（方案 1 采集）或手改 | ✅ |
| `ships[].water_surface_y` | 同上（可选） | 同一张图里不同水域高度不同时覆盖地图级 | 否 |
| `draft_adjust` | `ships[]`，默认 0 | 目视微调（正数=再下沉），不参与测量 | 否 |

**方案 1 的完整流程（唯一标准路径）**

1. **在目标海域按真实浮态造船**（船就浮在你想要的位置），甲板上摆好 `espetro:attack_spawn` / `espetro:veh:*` 标记。
2. 圈选：`/espetro naval pos1` 站在**船体最低块**那一角，`/espetro naval pos2` 站对角。
3. 导出：

   ```
   /espetro naval export lhd_075
   ```

   命令做三件事：
   - 扫描选区内（外扩 8 格）的**水面高度**，取众数作为 `waterSurfaceY`；
     选区跨越多个水域高度（众数占比 < 95%）时拒绝导出并打印分布（`y=63(1840) / y=70(22)`）。
   - 写 `waterline_y = waterSurfaceY - minY`、`waterline_source = "auto"`，控制台打印：
     ```
     [舰船] lhd_075 导出完成：size=257×56×32，方块 412336，
            水面 y=63，minY=54 → waterline_y=9（吃水 9 格，甲板高出水面 46 格）
     ```
   - 写 `<name>.meta.json`（含 size/blocks/bytes/sha1/waterline_y/waterline_source/markers）。

4. 地图侧登记本图海面高度（每张图一次，写 `EsConfig/amphibious.json`）：

   ```
   /espetro naval mapwater            # 站在水面上执行，取脚下水面 y
   /espetro naval mapwater 63         # 或直接给数值
   ```

   回显：`[两栖] CREATE_PLUS 的 water_surface_y = 63（来自玩家 boy @ 1690,63,-1512）`。
   写入走「读-改-写 + 原子替换 + `EsConfig` 整目录备份」，与编辑模式的 `configsave` 同一套（见 §3）。

5. 自检：

   ```
   /espetro naval info lhd_075
   ```

   ```
   结构 lhd_075        size 257×56×32  412336 方块  41.2 MB
   水线（船侧）         waterline_y=9（auto，水面 y=63，minY=54）
   水线（地图侧）       CREATE_PLUS water_surface_y=63
   推得 anchor.y        63 - 9 = 54
   甲板高出水面         56-1-9 = 46 格
   水深检查             锚点 (1680,-1520) 水底 y=38 → 可用 25 格 ≥ 吃水 9 ✅
   ```

**导出与启动时的自动校验**

| 检查 | 触发 | 不通过时 |
|---|---|---|
| 选区水面高度唯一（众数占比 ≥ 95%） | 导出 | 拒绝导出，打印水面高度分布 |
| `1 ≤ waterline_y ≤ size.y - 2` | 导出 | 拒绝导出（水线不能贴底/贴顶） |
| `size.y - 1 - waterline_y ≥ 3` | 导出（`--allow-low-deck` 可跳过） | 警告：甲板离水面太近，浪/爆炸会上水 |
| `meta.waterline_y` 与 `meta.waterline_source` 都存在 | 启动期 | 拒载该舰：`结构缺少水线元数据，请重新 naval export` |
| `water_surface_y` 存在且在 `1..(height-2)` | 启动期 | 整图 `amphibious=disabled`（地图仍可用，按普通侵攻跑）并给出明确原因（**不猜默认值**） |
| `water_surface_y` 处是 `minecraft:water` | 启动期（锚点柱采样） | 拒载：`锚点 (1680,-1520) y=63 不是水面，当前是 stone` |
| 水面到海底 ≥ `waterline_y + 2` | 启动期 | 警告：船底会插海床，建议抬高 `water_surface_y` 或缩小吃水 |

**可选兜底（不推荐，仅在无法下水造时）**

- 干船坞：沿外壳在设计水线层铺 `structure_block`（`mode=DATA`，`metadata=espetro:waterline`）→ 导出时写 `waterline_source="marker"`；多层时取众数并列出异常坐标。
- 手动指定：`/espetro naval export lhd_075 --waterline 9` → `waterline_source="manual"`，必须再用 `naval preview` 目视确认一遍。
- 目视微调：`naval preview` → `naval waterline`（沿外壳画粒子）→ `naval sink 1` → `naval commit draft_adjust 1`。
  只调 `draft_adjust`，**永不改 `waterline_y`**，保证数据可追溯。

**同一艘船用于多张图**：`waterline_y` 不变，各图各自有 `water_surface_y`，`anchor.y` 自动换算——结构文件不用改，换图也不会错位。

### 2A.6 待你拍板

1. ~~船体来源~~ ✅ 已定：**外部 `.nbt` + 导出命令**（不烘进地图模板）。
2. ~~水线测量方式~~ ✅ 已定：**方案 1**（在水里造 + 导出自动取水面）；船侧 `waterline_y` 与地图侧 `water_surface_y` 都写进 data，缺失即拒载。
3. **是否需要内置船**：先只支持服主自建（P0），还是同时内置一艘占位 LHD（便于新图直接开跑）。
4. **分段策略**：单文件（实现简单、单 tick 峰值高）vs 分段 nbt（峰值低、导出工具要多做一步）。

---

## 3. 运行流程（时间线）

```
选图投票结束
  └─ GameStateManager 进入部署阶段
      └─ BattlefieldWorldManager.importAndLoad(server, map, cb)
          ├─ copyTemplate(...)            复制地图到 world/dimensions/espetro/<map>
          ├─ createServerLevel(...)       挂载维度（:856 已挂载）
          └─ ★ AmphibiousDeployment.apply(server, level, map)      ← 新增
              ├─ 1. 预载舰船包围盒内所有区块（ChunkTickets，避免粘贴到未加载区块）
              ├─ 2. 逐艘粘贴结构（template.placeInWorld，flags=2|16）
              ├─ 3. 扫描并消费标记方块（出生点 / 载具点 / 舰体采样）
              ├─ 4. 清标记 → 空气；驱逐舰若是纯装饰则替换为 barrier 不可见壳
              ├─ 5. SpawnPointConfig.setSpawnPoint("ATTACK", 甲板点)
              ├─ 6. 载具点合并进本轮 VehSpawnSnapshot（VehSpawnMerge，不落盘）
              ├─ 7. 屏障：按 hull 采样生成外壳 → 入队 pendingBarrierPlace
              └─ 8. 登记舰体保护区（防拆/防炸）
      └─ 玩家传送进维度（走 SpawnPointConfig，自动落到甲板）
  └─ 部署阶段：屏障生效（含顶盖），玩家只能在舰上活动
开战
  └─ 现有逻辑清空屏障队列（pendingBarrierRemove）→ 玩家下舰冲滩
  └─ ATTACK 复活点保持舰上（与侵攻一致：攻方死了在舰上复活）
对局结束 / 切图
  └─ cleanupBattlefield 删维度 → 屏障表、覆盖表跟着维度一起丢弃
```

关键点：**粘贴必须发生在「维度挂载完成」与「玩家传送」之间**，否则玩家会先落到海里。

---

## 4. 新增模块

| 模块 | 职责 | 预估 |
|---|---|---|
| `org.espetro.naval.AmphibiousConfig` | 解析 `amphibious.json` + 校验（结构存在、锚点在海平面以上、`assault_faction` 合法） | 220 行 |
| `org.espetro.naval.NavalStructureStore` | 目录/命名/meta 校验/缓存与失效（对应 2A.2） | 140 行 |
| `org.espetro.naval.NavalStructureService` | 模板加载/旋转/粘贴/标记扫描/清标记 + 区块预载 | 260 行 |
| `org.espetro.naval.AmphibiousDeployment` | 上面的 apply 流程编排（含 spawn 覆盖、载具点覆盖、屏障注册） | 180 行 |
| `org.espetro.naval.BarrierShell` | 由 hull 采样点生成外壳集合（体素化 + 封顶 + 空洞跳过） | 130 行 |
| `org.espetro.naval.VehSpawnMerge` | 把甲板标记点合并进本轮 `VehSpawnSnapshot`（attack 替换、defend 补齐），供 `forRound` 后调用 | 110 行 |
| `org.espetro.naval.NavalProtection` | 舰体范围禁破坏/禁放置/禁爆炸（复用 `MainBaseProtection` 的判定方式） | 90 行 |
| `org.espetro.command.NavalAuthoringCommand` | `/espetro naval pos1|pos2|info|export|reload|list`（导出复用 `FortificationAuthoringManager` 的 `fillFromWorld` + `NbtIo.writeCompressed` + 备份） | 200 行 |
| 改动：`ActiveMapConfig` | 增加 `amphibious` 字段与启动期解析、拒载原因 | 60 行 |
| 改动：`FactionDataLoader` | 增加 `faction.amphibious` 解析 | 30 行 |
| 改动：`ClassSelectManager` | 攻方候选池裁剪为 `assault_faction`，跳过攻方编制投票 | 60 行 |
| 改动：`VehicleManager` | **不需要改**：覆盖点通过 `ActiveMapConfig.forRound` 合并进 `VehSpawnSnapshot` 即可自动生效 | 0 行 |

合计 ≈ **1,480 行**（含注释）。其中 `BarrierShell` 与 `NavalProtection` 可以先用简化版（`box` 模式 + 统一半径）在 P0 上线；
`NavalAuthoringCommand` 是 P0 也需要的一块（没有它，服主只能靠原版结构方块手工导出）。

---

## 5. 屏障设计

复用现有攻方等待区屏障的队列与帧预算（`BARRIER_BLOCKS_PER_TICK`），只把「90×90 方框」换成「舰体外壳」：

1. **外壳采样**：结构里 `espetro:hull` 标记沿舰体外缘摆放（每 2–4 格一个），程序按 XZ 平面贪心连成闭环；
2. **体素化**：对每个采样点，向上下各扫描到「实心方块」为止，记录 `(x,z)` 的 y 区间；
3. **生成屏障**：沿闭环每格、在 y 区间内放 `minecraft:barrier`（跳过已有实心方块），再按 `barrier.cap` 加顶盖；
4. **分帧**：全部进 `pendingBarrierPlace`，开战时进 `pendingBarrierRemove` 并恢复原方块（现有逻辑已做原方块记录）；
5. **兜底**：`mode=box` 时直接用包围盒生成六面，不需要 hull 标记（先上线再换 hull）。

注意：屏障只围 LHD。驱逐舰默认 `solid=true` 但**不加屏障**，靠 `NavalProtection` 禁止上下舰。

---

## 6. 出生点与载具点

- **出生点**：`SpawnPointConfig.setSpawnPoint("ATTACK", deckX, deckY, deckZ, yaw)`。
  所有传送（开局、复活、HUD 引导）都走 `getSpawnPoint`，改这一处即可全生效。
  注意 `MainBaseProtection`、`ClassEquipmentZones`、`DeploySupplyStationPlacer` 也是按该点计算的，
  所以补给站、装备区会自动跟到舰上 —— 这是「其余与侵攻一样」的自然结果。
- **载具点**：
  - 地图 `VehSpawn.json` 的 attack 侧点仍在陆上（供非两栖模式使用），两栖模式运行时用「合并后的 `VehSpawnSnapshot`」替换 attack 侧；
  - 合并时机：`ActiveMapConfig.forRound(seed)`（`src/main/java/org/espetro/config/ActiveMapConfig.java:64`）之后、编制加载之前，
    用 `new VehSpawnSnapshot(vehicleTypes, mergedMap, errors)` 造一份新快照塞回本轮配置；
    下游 `VehicleConfig.applyActiveMap`（`:210,241-242`）与 `VehicleManager.spawnPadSupplyStations`（`:2200`）**零改动**自动用新点；
  - 覆盖表按类型给出点列表，**顺序即编制槽位顺序**，数量必须 ≥ 该类型编制声明台数，否则该编制仍会被拒载（日志 `[编制兼容] … → 失败`）；
  - 载具 y 取标记方块 y（甲板面），建议甲板面 y 与 `anchor.y + 结构高度` 对齐，实机若陷地整体 +1。
- **坞舱**：`defend` 侧标记若存在，可作为「守方也能用的同一点位」；两栖图通常让守方继续用岸上点，因此
  合并时只替换 attack，defend 保持地图原值。若地图原点位本来只有一侧，合并时复制 attack 点补齐 defend，
  **只在内存里补、不落盘**，避免踩到「载具点必须两侧齐全」的校验。

---

## 7. 冲滩编制限定

三处收紧，缺一不可：

1. **启动期校验**（`ExternalConfigBootstrap`）：`amphibious.assault_faction` 必须
   - 存在且 `faction.team == "ATTACK"`
   - `faction.amphibious == true`
   - 通过 `FactionDataLoader.isCompatibleWithMap`（VehTypes/点位/实体都合法）
   任一不满足 → 该地图 `usable=false`，拒载原因写进 `rejectionReasons`（服主一眼能看懂）。
2. **攻方候选池**（`ClassSelectManager:112,234`）：`amphibious.assault_faction != null` 时，
   ATTACK 侧候选池只保留它（防守方候选池不变，仍按现有投票）。
3. **跳过攻方投票**（`ClassSelectManager:336`）：`assault_faction` 存在时直接 `finalAttackClass = assault_faction`，
   界面显示「本图攻方编制已锁定：XXX」，避免玩家投了票却无效。

---

## 8. 边界情况与规则

| 情况 | 处理 |
|---|---|
| 舰船锚点与地形/建筑重叠 | 粘贴用 flags `2\|16`（强制覆盖 + 更新邻居），启动期校验锚点周围 8 格内不得是陆地（`getBlockState` 全为水/空气），否则拒载 |
| 粘贴时区块未加载 | 先按包围盒 `ChunkTickets` 预载并 `getChunk` 同步，粘贴后再释放（维度销毁时会自动清） |
| 玩家掉进海里 | 不额外处理（与侵攻一致，会淹死/被鲨鱼咬）；复活点仍在舰上 |
| 载具落水 | 现有载具涉水逻辑照旧；LHD y 宁可高 1 格，不要陷进甲板 |
| 飞行载具/直升机飞出屏障 | `cap=true` 封顶；开战后屏障拆除，属正常玩法 |
| 舰上能造工事吗 | 允许（甲板是实心方块），但 `NavalProtection` 只保护**舰体结构方块**，不阻止在甲板放工事；若不想允许，把 `protected_radius` 之外再加 `no_fortify=true`（P1） |
| 与地图编辑模式冲突 | 编辑维度里不粘贴舰船（`amphibious.json` 只在战场激活时生效）；管理员想预摆放就手动摆，保存进模板后把 `ships` 清空即可 |
| 中途重开/切图 | 覆盖表、屏障表都是「本轮内存态」，随维度销毁；不会污染下一轮 |
| 两栖图选到非两栖编制 | 由第 7 节启动期校验挡住，服主能看到明确拒载原因 |
| 守方（DEFEND）差异 | 无：出生点、票数、刷新、据点、工事规则全部沿用侵攻 |

---

## 9. 分期

**P0（可上线的最小闭环，约 3–4 天）**
- `amphibious.json` 解析 + 拒载校验（含 `assault_faction`）
- `NavalStructureStore` + `NavalAuthoringCommand`（`pos1/pos2/info/export/reload/list`，导出带备份与 meta）
- 单艘 LHD 粘贴（`rotation` 支持 4 向，按 y 分帧）+ 标记解析（出生点 / 载具点）
- `SpawnPointConfig` 覆盖 + `VehSpawnMerge`
- 屏障 `mode=box`（包围盒六面 + 顶盖），复用现有队列
- 冲滩编制锁定（候选池 + 免投票）
- 一张测试图验收

**P1（体验完善，约 2–3 天）**
- `mode=hull` 外壳屏障（按 hull 标记体素化）
- 驱逐舰（装饰 + 不可进入）+ 岸轰支援（P2 才给火力）
- `NavalProtection` 舰体禁拆禁炸
- 舰上补给站/装备区位置微调（避免卡进舱壁）

**P2（玩法扩展，可选）**
- 舰炮/近防炮压制、登陆艇（LCU）从坞舱放出的载具投放动画
- 舰体受损与沉没、舰载直升机起降点
- 客户端 HUD 显示「冲滩倒计时 / 舰体完整度」（需升协议号）

---

## 10. 验收清单（P0）

1. 启动日志出现 `两栖地图已就绪: <map>（冲滩编制 <faction>，舰船 2 艘）`，无 `拒载`。
2. 进入该图，玩家出生在甲板标记位置（不是海里、不是水下）。
3. `/espetro edit status` 之类无关；用 `/tp` 到甲板外，确认屏障挡住（含向上飞）。
4. 视角看向海面，能看到驱逐舰；看 LHD 甲板，载具按标记位置出现（每种至少 1 台）。
5. 开战瞬间屏障消失，玩家可跳海/下舰；舰上仍可复活。
6. 换到非两栖地图，一切与改动前一致（回归）。
7. 把 `assault_faction` 改成不存在的 id，地图应在启动期被拒载并给出原因。

---

## 11. 风险与备选

| 风险 | 说明 | 缓解 |
|---|---|---|
| 结构粘贴耗时 | 大舰（160×60×60）首次粘贴可能 200–600 ms | 粘贴放在 `ioExecutor` 之前的主线程一次性完成，配 `ChunkTickets`；实测超 1 s 就把舰体拆成 2–3 段结构 |
| 舰船与地图不匹配 | 玩家自己建图时海域不够大 | 启动期用锚点 + 结构体积 + 包围盒水域采样做校验，不合格直接拒载并打印需要的海域尺寸 |
| 「载具点两侧必须齐全」规则 | 现有 `VehSpawnSnapshot.parse` 要求点位两侧都有效 | 合并阶段在内存里补齐 `defend`（复制 attack 点），**不落盘**，避免破坏地图配置 |
| 屏障方块泄漏 | 队列中断可能留下 `barrier` | 复用现有 `pendingBarrierRemove` + 维度销毁兜底；另在开战日志打印「屏障 0 残留」自检 |
| 备选方案：舰船直接烘进地图模板 | 零粘贴成本、点位可手编 | 若 P0 粘贴性能不达标，退回此方案：舰船在编辑维度里摆好，`amphibious.json` 只留 `spawn_from_markers=false` + `override_spawn` |

---

## 12. 附：给地图作者的检查表

- [ ] 海域够大（至少 `舰长+20` × `舰宽+20`），锚点周围 8 格无陆地
- [ ] `amphibious.json` 只填 `anchor.x/z`（`y` 由 `water_surface_y - waterline_y` 自动反推，不用手填）
- [ ] 结构里放好 `espetro:attack_spawn`、`espetro:veh:*` 标记，朝向即出生朝向
- [ ] **船是在水里按真实浮态造的**，导出后 `meta.waterline_y` 与 `waterline_source=auto` 都在
- [ ] **地图已登记 `water_surface_y`**（`/espetro naval mapwater`），且 `naval info` 的 anchor.y 与预期一致
- [ ] `naval info` 的水深检查通过（水面到海底 ≥ 吃水 + 2）
- [ ] 该图攻方载具数量 ≥ 冲滩编制声明的台数（否则编制拒载）
- [ ] 冲滩编制 JSON 里 `team=ATTACK`、`amphibious=true`
- [ ] 保存后重启服务器再验收（EsConfig 不热重载）
