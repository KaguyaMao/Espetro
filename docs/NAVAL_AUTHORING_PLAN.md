# 舰船结构编排（Naval Structure Authoring）：从主城一艘船到 `amphibious.json`

> 目标：管理员在主城把整艘两栖攻击舰/驱逐舰搭好，**一次框选**导出成**多卷结构**
> （`<name>/manifest.json` + `part_*.nbt`），开图时由 `amphibious.json` 引用、自动贴合到锚点，
> 并从船内标记自动得到甲板出生点与车位。
>
> 现状问题：原版结构方块单次上限 48×48×48，真实 LHD 远超；工事那套导出虽然能框大区域，
> 但限额是 64（硬上限 128）格 / 2 MB NBT，且一次性 `fillFromWorld`，装不下也不该照搬。
> 因此按"工事编辑器的交互" + "舰船自己的多卷存储与分级限额"来做。

---

## 0. 设计原则

1. **照抄工事的交互，不照抄工事的限额**：同一个选定棒、同一套 `pos1/pos2/anchor` 习惯，
   但独立目录、独立限额、独立校验。
2. **多卷存储 + 分片捕获**：大船拆成 N 个 `part_*.nbt`（每卷 ≤ 16 格厚），
   每卷单独捕获、单独落盘，任何时刻内存里只有一卷，避免一次 `fillFromWorld` 卡住主线程。
3. **标记代替手填坐标**：船内放标记块，导出时保留、贴图时抹掉，开图时据此生成出生点与车位。
4. **不碰战斗核心**：产物落在已存在的 `config/espetro/naval_structures/` 下，
   `AmphibiousDeployment` 的时序与 `VehSpawnMerge` 的合并规则一行不改；
   标记只是把 `override_spawn` 从"手写坐标"变成"自动生成"。

---

## 1. 现状与差距（源码为准）

| 能力 | 工事（`FortificationAuthoringManager`） | 舰船（当前） |
|---|---|---|
| 选区 | `/espetro fort wand` 发选定棒；左键=pos1、右键=pos2、潜行+右键=锚点；也可 `pos1|pos2|anchor x y z` | **无**，只能靠原版结构方块（48³） |
| 预检 | 方块数/实体策略/结构空位/锚点，`stats()` | 无 |
| 导出 | `save()`：一次 `fillFromWorld` → `NbtIo.writeCompressed` → meta | 无（内置两艘由脚本生成） |
| 限额 | `limits().maxTemplateAxis = 64`（`HARD_MAX_TEMPLATE_AXIS = 128`）、`maxTemplateNbtBytes = 2 MB` | 单卷 256 / 200 万方块 / 32 MB，整船 ≤ 1024 / 4000 万方块 / 64 卷（见 §4.2） |
| 目录 | `config/espetro/fortification_templates/` | `config/espetro/naval_structures/`（已存在、已被 `NavalStructureStore` 扫描） |
| 引用 | `fortifications.json` 的 `structure` | `amphibious.json` 的 `ships[].structure = "espetro:naval/<name>"` |

结论：缺的只有"**框选 → 逐卷导出 → 写 manifest**"这一段，其余（加载、旋转、粘贴、屏障、
载具点合并、守方回退）都已上线；`NavalStructureService.load/place` 需要从"单模板"扩成"多卷"。

---

## 2. 指令表

全部挂在 `/espetro naval` 下（permission 2，与 `fort` 一致）：

```
/espetro naval                        # 状态：当前模式、选区、锚点、限额
/espetro naval wand                   # 发选定棒（复用 BastionItems.FORTIFICATION_WAND）
/espetro naval pos1 [x y z]           # 设角 A（不带坐标 = 准星所指方块）
/espetro naval pos2 [x y z]           # 设角 B
/espetro naval anchor [x y z]         # 设锚点（= 贴图时"最小角"对齐点；默认取 min 角）
/espetro naval marker deck [yaw]      # 在脚下放甲板出生点标记
/espetro naval marker veh <type> <i> [defend]   # 在脚下放车位标记
/espetro naval marker clear           # 清掉选区内的标记块
/espetro naval save <name> [--no-entities] [--carve] [--force]
/espetro naval list | info <name> | delete <name>
/espetro naval preview <name> [none|90|180|270]   # P1：在准星处粘一份预览
```

`marker veh <type> <i>` 的 `type` 用地图 `VehSpawn.json` 的类型名（`mbt/mgs/ifv/acv/apc/car/...`）。

---

## 3. 标记块协议（复用已实现的 `NavalStructureService.readMarkers`）

- 方块：`minecraft:structure_block`，`mode=DATA`，`metadata` 精确写成：
  - `espetro:attack_spawn`（可选 `espetro:attack_spawn:yaw=90`）
  - `espetro:veh:<type>:<index>` ← 攻方车位
  - `espetro:veh:<type>:<index>:defend` ← 守方车位（一般不用，守方走岛图原表）
  - `espetro:hull`（可选，标水线/甲板基准点）
- **导出时保留**标记块（后续扫描要用）；**贴图时** `place(..., clearMarkers = true)` 已会抹成空气。
- 命中 `spawn_from_markers: true` 时：
  - 攻方出生点 = `anchor + rotate(markerLocal)`；
  - 每个 `<type>` 的车位 = 该类型所有 `espetro:veh` 标记，按 `index` 排序 → 正好对应编制 `entity[]` 槽位；
  - `override_spawn.attack` / `override_spawn.vehicles` 可以整段删掉。

> 注意：车位标记**必须**在甲板上表面再高一格（与现有 `override_spawn` 语义一致：
> 坐标是载具落点，`y` 用甲板顶 + 1）。

---

## 4. 多卷（multi-part）存储与分片导出

限额不够就**拆卷**：一艘船 = 一个目录 + 一份清单 + N 个 `part_*.nbt`。
每一卷自己满足单卷限额，整船只受"卷数 × 单卷"和总方块数约束。

### 4.1 目录与清单

```
config/espetro/naval_structures/
├── lhd_real/
│   ├── manifest.json
│   ├── part_00.nbt
│   ├── part_01.nbt
│   └── ...
└── lhd_placeholder.nbt      ← 旧的单文件格式仍兼容（内置占位舰）
```

`manifest.json`：

```json
{
  "name": "lhd_real",
  "id": "espetro:naval/lhd_real",
  "format": 2,
  "size": [188, 40, 32],
  "anchor": [1158, 12, 2100],
  "pivot": [94, 0, 16],
  "draft": 9,
  "author": "LinYi",
  "exported_at": "2026-11-09 22:40:11",
  "total_blocks": 118420,
  "parts": [
    { "file": "part_00.nbt", "offset": [0,   0, 0], "size": [16, 40, 32], "blocks": 10080, "markers": ["veh:mgs:1"] },
    { "file": "part_01.nbt", "offset": [16,  0, 0], "size": [16, 40, 32], "blocks": 9870,  "markers": [] },
    { "file": "part_11.nbt", "offset": [176, 0, 0], "size": [12, 40, 32], "blocks":  8120, "markers": ["attack_spawn"] }
  ]
}
```

- `offset` 是**相对整船最小角**的局部坐标，贴图时 `anchor + rotate(offset)`。
- `pivot` = 锚点在船内的局部坐标（导出时算好，贴图时不用再手算）。
- `markers` 是每一卷内扫到的标记（导出时就扫，开图时不必再解 nbt）。

### 4.2 限额（分级）

| 级别 | 单卷 | 整船 |
|---|---|---|
| 边长 | ≤ 256 | ≤ 1024 |
| 方块数 | ≤ 2,000,000 | ≤ 40,000,000 |
| NBT（压缩后） | ≤ 32 MB | — |
| 卷数 | — | ≤ 64 |

也就是整船容量 ≈ 16 亿方块理论上限，实际瓶颈是粘贴时间与磁盘，不是内存。

> LHD 188×40×32 按 16 格切片 = 12 卷，每卷 16×40×32 = 20,480 方块 / 约 0.3 MB，
> 离单卷上限差两个数量级。真正需要拆卷的是 500 格以上的巨舰。

### 4.3 导出算法

```
NavalAuthoringManager.save(player, name, options):
  1) 预检：角点、锚点、尺寸、体积、标记数、名字；给出"预计 N 卷 / 总方块数"
  2) 建临时目录 naval_structures/.<name>.tmp/
  3) 选切轴：最长边（默认 X），卷厚 = 16（可 --slice <n>）
  4) 逐卷流式捕获（每 tick 预算 blocksPerTick = 150,000）：
       对每一卷：
         t = new StructureTemplate()
         t.fillFromWorld(level, sliceMin, sliceSize, includeEntities, carve?STRUCTURE_VOID:AIR)
         tag = t.save(new CompoundTag())            // 单卷 NBT
         立即写 part_XX.nbt（gzip）+ 更新 manifest 草稿
         扫该卷的 structure_block 标记 → 记进 manifest
       每卷完成给玩家一行进度
  5) 全部卷完成 → 写 manifest.json（原子）
     目录 rename 成正式名；同名旧目录先改 <name>.bak-<stamp>
  6) 任一步异常 → 删除 .tmp 目录，保留原版本，会话回 IDLE 并报错
```

**流式**的意义：任一时刻内存里只有"一卷"的 NBT（≤ 32 MB），导出 12 卷也不会 OOM；
中途失败不会留下半个船。

### 4.4 加载与粘贴

`NavalStructureService.load()` 改成三级回退：

1. **目录 + manifest**（本方案，多卷）；
2. 单文件 `<name>.nbt`（旧的/内置的）；
3. classpath `data/<ns>/structures/<path>.nbt`（内置占位舰）。

`Loaded` 从"一个模板"变成"模板列表 + 偏移"：

```java
record Loaded(List<Part> parts, Vec3i totalSize, List<Marker> markers, int blocks) {
    record Part(StructureTemplate template, BlockPos offset, Vec3i size) {}
}
```

`place(level, loaded, anchor, rotation, clearMarkers)`：

```
for (Part p : loaded.parts())
    template.placeInWorld(level, rotate(anchor.offset(p.offset())), ... , settings, random)
```

- 先按 `rotatedBounds(totalSize)` 一次性预载区块（已有 `preloadChunks`）；
- 屏障用整船包围盒（`BarrierShell.box(min,max,height,cap)`），不用改；
- 旋转时 `offset` 与 `size` 一起旋转（`StructureTemplate` 自己会转卷内内容）。

### 4.5 标记与 `spawn_from_markers`

导出时逐卷扫 `minecraft:structure_block` 的 `metadata`，写进 manifest 的 `parts[].markers`。
开图时直接读 manifest：

- `espetro:attack_spawn` → 攻方出生点 = `anchor + rotate(该卷offset + 卷内坐标)`；
- `espetro:veh:<type>:<i>` → 对应槽位车位；
- `espetro:hull` → 水线基准。

manifest 缺 `markers`（手写的旧目录）时，回退到逐卷解 NBT 扫描。

### 4.6 与旧单文件共存

- 内置 `lhd_placeholder.nbt` / `ddg_placeholder.nbt` 保持单文件，不动；
- `NavalStructureStore.pathFor("espetro:naval/x")` 先看 `<x>/manifest.json`，再退 `<x>.nbt`；
- `delete` 同时支持删目录与删文件；`list` 两种都列（目录标注卷数）。

## 5. 与现有代码的接线点

| 文件 | 改动 |
|---|---|
| `naval/NavalAuthoringManager.java`（新） | 会话、标记、预检、**逐卷流式导出**、manifest、list/delete/info；≈520 行 |
| `naval/NavalLimits.java`（新，或并入 `AmphibiousConfig`） | 限额读取（可放 `config/espetro/amphibious_limits.json`，缺省用上表） |
| `command/NavalCommand.java`（新） | `/espetro naval ...` 指令树；≈130 行 |
| `command/EspetroCommand.java` | 注册 `NavalCommand.register(dispatcher)`（1 行） |
| `naval/NavalStructureStore.java` | 目录与单文件并存：`pathFor` 先找 `<name>/manifest.json` 再找 `<name>.nbt`；`listNames` 两种都列；≈60 行 |
| `naval/NavalStructureService.java` | 多卷加载（manifest → parts）、逐卷 `placeInWorld`、`spawn_from_markers=true` 时由 manifest 标记生成 `override`；≈120 行 |
| `naval/AmphibiousDeployment.java` | 接收"标记生成的 points"（复用 `VehSpawnMerge`）；≈30 行 |
| `FortificationWandHandler.java` | 按玩家"当前编排模式"分派 pos1/pos2/anchor 到 fort 或 naval 管理器；≈25 行 |

不改：`FortificationAuthoringManager`、`VehSpawnMerge` 的合并规则、`BattlefieldWorldManager` 的时序。

---

## 6. 风险与对策

| 风险 | 对策 |
|---|---|
| 超大船（256³ = 1670 万方块） | 导出直接拒绝并提示"缩小范围或分两段"；不静默降级 |
| 导出期间玩家继续搭 | 选区快照在开始时固定（min/size），中途改动不影响本次导出；提示"导出中请勿修改船体" |
| 粘贴时卡顿 | 已有 `collectShipChunks` 预载 + `place` 分块；导出侧不影响 |
| 船内方块实体带玩家数据 | `--no-entities` 默认关闭实体捕获；带实体时只带方块实体，不带实体列表 |
| 标记块被误贴进地图 | `place(clearMarkers=true)` 已处理；导出 meta 记录标记清单便于核对 |
| 旧配置回退 | 内置 `lhd_placeholder`/`ddg_placeholder` 保留；删掉导出的 nbt 即回退占位舰 |
| 卷间接缝错位 | 卷边界严格 `[a, b)` 不重叠；`offset` 由导出时算定，贴图逐卷用同一 `anchor`；接缝两侧同属一体方块（如船体）时不会出现重影 |
| 卷数过多导致粘贴慢 | 卷数上限 64，粘贴前一次性预载整船区块；每卷 `placeInWorld` 独立，失败可定位到具体卷 |

---

## 7. 验收清单

1. 主城框选 188×40×32 的船 → `/espetro naval save lhd_real`：
   打印"预计 12 卷"，逐卷进度，全部完成 < 5 s；
   `naval_structures/lhd_real/` 下有 `manifest.json` + 12 个 `part_*.nbt`，无 `.tmp` 残留。
2. `manifest.json` 的 `size` / `total_blocks` / `parts[].offset` 与选区一致；
   每卷 `blocks` 之和 = `total_blocks`；标记数与放置数一致。
3. `NavalStructureService.load("espetro:naval/lhd_real")` 读回 12 卷，
   `place()` 后整船与原建筑逐方块一致（可抽 3 个接缝面比对）。
4. `island/EsConfig/amphibious.json` 换成 `lhd_real` + `spawn_from_markers: true`，
   删掉 `override_spawn`，开图后：
   - 日志三行齐全（`两栖地图已就绪` / `两栖载具点已合并` / `两栖攻方出生点已覆盖`）；
   - 攻方出生在甲板标记处、守方出生在岛内；
   - 所有载具在甲板标记车位，守方载具在岛内网格；
   - 屏障贴合新船体（`barrier.height` 按新船调整）。
5. 导出中途 `/stop` 或断电：重启后 `naval_structures/` 里没有半个船，
   旧版本（`.bak-<stamp>`）仍在，`list` 能看到。
6. `/espetro naval delete lhd_real` 后开图回退内置占位舰（单文件），不报错。
