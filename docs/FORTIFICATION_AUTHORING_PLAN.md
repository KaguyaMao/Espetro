# 工事可视化编辑器（选定棒）设计方案

> 状态：**方案待确认**，未开始实现。
> 目标：管理员在主城搭好工事后，用一根"选定棒"选两角 + 锚点，用指令导出为工事模板，
> 并自动生成配置定义；选区内有实体时保存实体完整信息。

---

## 1. 范围

**做**
- 新物品：工事选定棒（原版木棍材质），管理员专用。
- 选区：两角 + 锚点，全部服务端记录、客户端只负责画。
- 可视化：选区线框、锚点高亮、原点标记、朝向基准线、HUD 统计。
- 指令：设置选区、预检、导出模板、管理模板、热重载工事定义。
- 导出内容：方块（含方块实体 NBT）+ 实体（完整 NBT）。
- 自动生成 v2 定义条目（写回 `config/espetro/fortifications.json`）。

**不做**
- 不搞客户端自由建模 / 内置 3D 编辑器 / 多人协同编辑。
- 不改现有施工、归属、扣费、减伤逻辑。

---

## 2. 坐标语义（来自现有代码，必须与之一致）

`FortificationTransform.world()`：

```
world = anchor + R(origin_offset + local − pivot)
```

| 符号 | 含义 |
|---|---|
| `local` | 模板内整数格坐标，`0 .. size−1` |
| `pivot` | **旋转中心**（模板坐标），绕它做水平旋转 |
| `origin_offset` | 额外整体偏移，通常 `[0,0,0]` |
| `anchor` | 放置时玩家准星命中的目标格（`raycastPlacePos`，雪层特判） |
| `R` | 水平旋转，`facing=NORTH` → 不旋转 |

选定棒 → 定义映射：

```
min   = (min(A,B).x, min.y, min.z)
size  = max − min + 1
pivot = 锚点格 − min          ← 选定的锚点就是旋转中心
origin_offset = [0,0,0]
```

推论（写进管理员手册）：
- **锚点那块在任意朝向下都停在玩家瞄准的位置**，所以锚点选"贴地那层的中心格/门口地面格"。
- **基准朝向 = 北**：玩家面朝北放置时与管理员搭的完全一致；面朝东/南/西时整体绕锚点旋转 90/180/270°。
- 选区必须是**立体的**（含高度），`y` 也要框到。

---

## 3. 物品：`espetro:fortification_wand`（工事选定棒）

- 注册：`BastionItems.registerAll()` 的 `Registries.ITEM` 段（照现有 `RADIO_BLOCK_ITEM` 写法），
  `new Item.Properties().stacksTo(1)`。
- 材质：`assets/espetro/models/item/fortification_wand.json`
  ```json
  {"parent": "minecraft:item/handheld", "textures": {"layer0": "minecraft:item/stick"}}
  ```
  即**原版木棍贴图**，不新增美术资源。
- 语言：`item.espetro.fortification_wand` = `工事选定棒` / `Fortification Wand`。
- 获取：`/espetro fort wand`；也能 `/give @s espetro:fortification_wand`。
- **权限门**：手持棒子操作时校验 `player.hasPermissions(2)`（`/espetro` 根命令本身就是 permission 2），
  没权限只提示、不改变选区。这样棒子即使流出去也不能用。

---

## 4. 交互（服务端权威）

| 操作 | 效果 |
|---|---|
| 左键点方块 | 设角 A（`pos1`） |
| 右键点方块 | 设角 B（`pos2`） |
| 潜行 + 右键 | 设锚点 |
| 潜行 + 左键 | 清空选区 |
| 手持（无操作） | 每 10 tick 刷新一次统计（方块数/实体数），走 action bar |

实现要点：
- 事件：`PlayerInteractEvent.LeftClickBlock`（`setCanceled(true)` 阻止挖掉方块）、
  `PlayerInteractEvent.RightClickBlock`（`setCanceled(true)` + `setUseBlock(DENY)` 阻止放置/交互）。
- 状态：`Map<UUID, WandSelection>`，`record WandSelection(ResourceKey<Level> dim, BlockPos a, BlockPos b, BlockPos anchor)`。
  换维度、退出登录、`clear` 时失效。
- **选区上限前置校验**：`max_template_axis`（默认 64，硬上限 128）。设第二个角时若超限，
  直接拒绝并提示"选区 65 格超过单轴上限 64"，避免搭完才发现。
- 选区跨区块未加载：`/espetro fort info` 里提示"存在未加载区块，导出会缺方块"，并拒绝导出。

---

## 5. 可视化（客户端新增 overlay）

- 新包 `FortificationWandPacket`（S2C）：`pos1 / pos2 / anchor / size / blockCount / entityCount / 预检摘要`。
  客户端**不做射线检测**，只渲染服务端给的坐标（防作弊、实现简单）。
- 渲染复用现有 `FortificationPlacementController.render()` 的管线
  （`RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS` + `RenderType.lines()` + `LevelRenderer.renderLineBox`）：
  - 选区外框：**黄色**
  - 锚点格：**青色**高亮 + 十字
  - 模板原点（min 角）：**绿色**
  - 朝向基准：从锚点向北画一条短线
- HUD（`RenderGuiEvent.Post` + `GuiGraphics.drawString`，左上角）：
  `尺寸 5×3×2 | 方块 42 | 实体 2 | 锚点(2,0,1) | 朝向基准: 北`
- 预检有红色项时，HUD 用红字显示首条问题。
- 挂载点：`EspetroClient` 现有 listener 注册处 + `ClientPacketHandlers.handleFortificationWand`。

---

## 6. 指令：`/espetro fort <子命令>`（继承根命令的 permission 2）

| 子命令 | 说明 |
|---|---|
| `wand` | 发一根选定棒 |
| `pos1 [x y z]` / `pos2 [x y z]` | 不带坐标 = 取准星所指方块 |
| `anchor [x y z]` | 同上；不带参数 = 准星 |
| `info` | 选区尺寸/方块数/实体数 + **完整预检报告**（§8） |
| `save <name> [flags]` | 导出模板（+ 默认写入定义） |
| `list` | 列出 `config/espetro/fortification_templates/` 下所有模板（尺寸/方块/实体/作者/时间） |
| `delete <name>` | 删除模板文件（仅限该目录，内置模板不可删） |
| `reload` | 热重载工事定义（§10，带门槛） |
| `clear` | 清空当前选区 |

`save` 可选参数：

```
--name <显示名>              默认 = id
--icon <贴图或物品>          默认 minecraft:stick 的贴图
--behavior generic           默认 generic（其余值只给高级用法）
--cost <建造型点数>          默认 100
--progress <施工进度>        默认 100
--radio-range <true|false>   默认 true
--roles commander,squad_leader  默认三者全给
--no-entities                只存方块
--carve                      连空气一起存（危险，见 §7）
--damageable-entities 0,2    指定哪些实体索引算"可损伤部件"
--no-define                  只写模板，不动 JSON
```

---

## 7. 导出实现与"空气/结构空位"策略（关键）

```java
BlockPos min = ..., max = ...;
Vec3i size = new Vec3i(max.getX()-min.getX()+1, ...);
StructureTemplate t = new StructureTemplate();
t.setAuthor(player.getGameProfile().getName());
t.fillFromWorld(level, min, size, includeEntities,
                carve ? Blocks.STRUCTURE_VOID : Blocks.AIR);
CompoundTag tag = t.save(new CompoundTag());
NbtIO.writeCompressed(tag, configDir.resolve("espetro/fortification_templates/" + name + ".nbt"));
```

### 为什么默认 `ignoreBlock = Blocks.AIR`

代码事实：
- `FortificationTemplateCompiler` 只遍历 NBT 的 `blocks` 列表；
  其中**空气 → `EXPLICIT_AIR`**（完工时把那格清成空气），
  **structure_void → `IGNORE`**（完全不碰），未列出的格子 → 不出现、不占位。
- `FortificationManager.createBlueprint()` 把**所有**槽位（含 `EXPLICIT_AIR` 和 `IGNORE`）
  塞进 `slots`，`spaceIsClear()` 要求**每个**槽位当前是空气/可替换方块。

⇒ 如果把包围盒里的空气一起存，玩家放置时会被要求"整个包围盒清空"（包括建筑外的空白格），
基本不可用。手工写的 `sandbag_wall.snbt` 也只列了 6 个沙袋。
所以默认**只存非空气方块**（空气当忽略），与内置模板行为一致。

### 两种特殊表达（写进手册）

| 想要的效果 | 管理员怎么做 |
|---|---|
| 这格保持原样，什么都别动 | 在该格放 **结构空位 `structure_void`** → 导出后是 `IGNORE` |
| 连空气一起存，放置时清空整片区域（挖空碉堡内部/平整地基） | `save --carve`（info/HUD 会红字警告） |

### 其他导出约束
- 只在**已加载区块**内取样；未加载 → 拒绝并提示。
- 脚手架/临时方块会被一起存进去 → 手册强调导出前清干净。
- 方块实体 NBT、实体 NBT 都进文件（无损），加载时的过滤见 §9。

---

## 8. 导出前预检（必须，否则会把整份配置搞崩）

解析/编译失败是**全局**的：`compileAndFreeze` 一旦报错 →
`工事 registry 无法冻结` → `BattlefieldWorldManager.failStartup` → **战场开不起来**。
所以 `info` 与 `save` 都要按 `FortificationTemplateCompiler` 的同一套规则预检，
逐项列出问题坐标：

| 检查 | 规则 |
|---|---|
| 尺寸 | 单轴 ≤ `max_template_axis`（默认 64） |
| 方块数 | ≤ `max_template_blocks`（默认 4096） |
| 实体数 | ≤ `max_template_entities`（默认 32） |
| NBT 体积 | ≤ `max_template_nbt_bytes`（默认 2 MB） |
| 危险方块 | `command_block` / `chain_command_block` / `repeating_command_block` / `structure_block` / `jigsaw` + `#espetro:forbidden_fortification_blocks` → **拒绝并列出坐标** |
| 方块实体白名单 | 只允许 `banner / sign / hanging_sign / skull / decorated_pot`；箱子、熔炉、漏斗、刷怪笼… → 拒绝并列出坐标 |
| 结构实体白名单 | 只允许 `armor_stand / item_frame / glow_item_frame / painting / text_display / item_display / block_display / interaction`；`minecraft:player` 与其它类型 → 拒绝并列出 |
| 乘客深度 | ≤ `max_passenger_depth`（默认 4） |
| 可损伤部件 | 至少 1 个非空气方块或实体，否则编译报"模板没有可损伤部件" |
| 锚点 | 必须落在选区内；否则拒绝 |
| 空模板 | 0 方块且 0 实体 → 拒绝 |
| 重名 | `espetro:<name>` 已存在 → 拒绝（除非 `--force` 并先备份） |

导出成功时同时写一份旁车文件 `fortification_templates/<name>.meta.json`
（作者、时间、维度、完整选区 A/B、锚点、方块数、实体数、尺寸），供 `list` 显示与追溯。

---

## 9. 模板存放与加载（免数据包）

**存放**：`config/espetro/fortification_templates/<name>.nbt`（gzip 标准结构 NBT）。

选它而不是存档数据包的理由：与 `fortifications.json` 同目录、跟着 config 一起备份、
指令可增删查、不需要 `pack.mcmeta`、不用碰 `world/datapacks`。

**加载**：`FortificationTemplateCompiler.compileOne()` 改为两级查找：

```java
ResourceLocation id = ...;                       // espetro:fortifications/<name>
Path file = FMLPaths.CONFIGDIR.get()
    .resolve("espetro/fortification_templates/" + id.getPath().substring("fortifications/".length()) + ".nbt");
StructureTemplate template = Files.isRegularFile(file)
    ? server.getStructureManager().readStructure(NbtIo.readCompressed(file.toFile()))
    : server.getStructureManager().get(id)
        .orElseThrow(() -> new IllegalArgumentException("缺少 Structure NBT " + id));
```

⇒ 内置 6 个模板零改动（仍走数据包/内存包），管理员自建模板走文件目录，
`espetro:fortifications/<name>` 的命名约定不变。
`docs/FORTIFICATIONS_CONFIG.md` 第 118-119 行"必须放数据包"的说法会更新为两条路径（文件目录优先）。

采用的 API（已核对 1.20.1 官方映射）：
`StructureTemplate.fillFromWorld(Level, BlockPos, Vec3i, boolean, Block)`、
`StructureTemplate.save(CompoundTag)`、`StructureTemplate.setAuthor(String)`、
`StructureTemplateManager.readStructure(CompoundTag)`、`NbtIo.writeCompressed/readCompressed`。

---

## 10. 定义自动写入与生效

`save <name>` 默认追加一条 v2 定义到 `config/espetro/fortifications.json`：

```json
{
  "id": "espetro:<name>",
  "display_name": "<--name 或 name>",
  "icon": {"texture": "minecraft:item/stick"},
  "behavior": "generic",
  "placement": {
    "type": "structure",
    "template": "espetro:fortifications/<name>",
    "origin_offset": [0, 0, 0],
    "pivot": [<anchor.x-min.x>, <anchor.y-min.y>, <anchor.z-min.z>],
    "rotation": "player_facing",
    "mirror": "none",
    "air_policy": "reject_non_replaceable",
    "include_entities": true,
    "palette_index": 0
  },
  "cost": {"construction": 100, "ammunition": 0},
  "construction": {"required_progress": 100, "build_per_hit": 5, "remove_per_hit": 5},
  "durability": {"structural_value": 100, "repair_per_hit": 5,
    "damageable_structure_entities": [],
    "damage_reduction": {"explosion": 0.9, "projectile": 0.9, "direct_break": 0.0}},
  "requirements": {"require_radio_range": true,
    "usable_by": ["commander", "squad_leader", "fireteam_leader"]}
}
```

- 写入方式：**文本级插入**（定位 `fortifications` 数组的收尾 `]`，在其前插入新对象），
  不改动其它条目的排版；写前备份 `fortifications.json.bak-<yyyyMMdd-HHmmss>`；
  插入后用 Gson 解析校验，失败即回滚。
- 必须保留原有 5 条必需定义（`radio / hab / ammo_crate / vehicle_supply_station / sandbag_wall`）。

**生效：`/espetro fort reload`（免重启）**

```java
FortificationConfig.resetForNextServer();          // 解冻
FortificationConfig.loadServerConfig();            // 重新读 JSON
FortificationConfig.compileAndFreeze(server, ExternalConfigBootstrap.getUsableMaps());
```

安全门槛（避免打断进行中的对局）：
- 仅当阶段为 `LOBBY`（主城）**且** `FortificationManager` 没有在建工事时允许；
- 否则提示"当前有对局/在建工事，请完整重启生效"。
- `FortificationConfig` 增加受控入口 `adminReload(server, maps)`（现有 `loadServerConfig()` 在 `frozen` 时直接 return）。

这样"主城搭 → save → reload → 立刻开图测试"是闭环的。

---

## 11. 实体"保存所有信息"的边界 —— 需要你拍板

现状：`FortificationNbtSanitizer` 出于防作弊，**只保留视觉字段白名单**
（盔甲架姿势、展示框物品、告示牌文本、旗帜图案等），
且**非白名单类型直接抛异常** → 整个 registry 冻结失败。
所以"保存实体所有信息"必然要动这里，三个选项：

| 选项 | 做法 | 影响 |
|---|---|---|
| **A（推荐）** | 文件里**无损保存**完整实体 NBT；加载/编译时仍过滤，但把"抛错"改成"剔除 + 记 warning + `/espetro fort info` 列出被剔除项" | 满足"保存所有信息"，且不会因为多放了只鸡就把整份配置搞崩；防作弊边界仍在（箱子/漏斗/刷怪笼不会进工事） |
| **B（可配置白名单）** | 配置新增 `entity_policy` 段（允许的实体类型 + 字段白名单），默认=现有集合 | 想保留载具/炮台/箱子这类实体时自己放开 |
| **C（完全放开）** | 不再过滤 | **不推荐**：工事会变成刷物品/刷实体的通道（箱子、漏斗、刷怪笼、TNT 矿车…） |

我的建议：**A + B**（默认安全；需要时用配置放开；文件始终无损，随时可回滚）。
若你只想要最省事的，选 **A**。

---

## 12. 客户端更新与协议版本（部署必读）

- 新增物品/模型/包 ⇒ **玩家客户端必须同步换新 jar**，否则：
  - 旧客户端收到 `FortificationWandPacket` 会解码失败/报错；
  - 新物品在旧客户端上不存在。
- `NetworkManager.PROTOCOL_VERSION` 当前是 `"1.33"` → 本次应提到 **`"1.34"`**，
  这样旧客户端的连接会被 Forge 明确拒绝（提示协议不匹配），而不是随机报错。
- `gradle.properties` 的 `mod_version` 建议从 `1.1.3-i` 提到 `1.1.3-j`（便于区分与回滚）。

---

## 13. 改动清单

**新增**
| 文件 | 作用 |
|---|---|
| `bastion/FortificationAuthoringManager.java` | 选区状态、预检、导出、定义追加、`reload` 入口 |
| `bastion/FortificationWandItem.java` | 物品定义与 tooltip |
| `command/FortCommand.java` | `/espetro fort ...` 子命令树 |
| `network/FortificationWandPacket.java` | S2C 同步选区与统计 |
| `client/FortificationWandOverlay.java` | 线框渲染 + HUD |
| `assets/espetro/models/item/fortification_wand.json` | 木棍贴图模型 |
| `docs/FORTIFICATION_AUTHORING.md` | 管理员手册（实施后补） |

**修改**
| 文件 | 改动 |
|---|---|
| `bastion/BastionItems.java` | 注册 `fortification_wand` |
| `bastion/FortificationTemplateCompiler.java` | 模板来源：文件目录优先 |
| `bastion/FortificationConfig.java` | `adminReload(...)` 受控入口 |
| `bastion/FortificationNbtSanitizer.java` | 按 §11 选定方案调整（抛错→剔除 + warning） |
| `network/NetworkManager.java` | 注册新包；`PROTOCOL_VERSION` → `1.34` |
| `client/ClientPacketHandlers.java`、`EspetroClient.java` | 分发新包、挂载渲染 |
| `command/EspetroCommand.java` | 挂 `fort` 子命令（或独立 register） |
| `lang/en_us.json`、`lang/zh_cn.json` | 物品名/提示 |
| `docs/FORTIFICATIONS_CONFIG.md` | 模板存放说明改为"文件目录优先" |

**规模估计**：新增约 900~1200 行 Java，改动约 150 行；2~3 个提交（物品+选区+指令、导出+加载+热重载、文档+客户端更新）。

---

## 14. 实施顺序与验证

1. **物品 + 选区 + `pos1/pos2/anchor/info/clear` + 客户端线框** → 主城自测。
2. **`save`（默认 omit-air）+ 预检 + 文件加载 + `reload`** → 导出一个 3×2 沙袋墙，与内置
   `sandbag_wall.snbt` 对比（应等价）。
3. **定义自动追加**（文本插入 + 备份 + 校验回滚）。
4. **端到端**：主城搭"沙袋墙 + 带盔甲架/展示框的掩体 + 带结构空位的建筑"→ save → reload →
   开图 → Alt 轮盘建造 → 分别面朝北/东/南/西放置，验证：锚点不动、整体绕锚点旋转、
   实体出现、结构空位那格保持原样。
5. **边界回归**（每条都应给出清晰报错且**不影响** registry）：
   单轴 65、含命令方块、含箱子、含玩家、含 5 层乘客、锚点在选区外、全空选区、重名、区块未加载。
6. **整体回归**：启动日志仍为 `工事 JSON v2 已事务冻结: global=5 maps=3 aliases=7`，
   `Done (` 正常，无新增 ERROR；现有 5 条工事在游戏内仍可正常建造。

---

## 15. 待你确认（5 项）

1. **实体策略**：A / A+B / B / C？（建议 A+B，最省事选 A）
2. **模板存放**：`config/espetro/fortification_templates/`（建议）还是仍走 `world/datapacks/`？
3. **热重载**：是否要 `/espetro fort reload`（门槛：主城 + 无在建工事）？还是每次改完完整重启就行？
4. **朝向基准**：以"玩家面朝北 = 原样"为基准（建议）；还是希望在搭的时候额外记录一个基准朝向？
5. **客户端更新**：确认可以要求玩家换新客户端 jar（协议 `1.34`、`mod_version 1.1.3-j`）？
   如果没有条件让玩家更新，这个功能就得砍掉物品/渲染部分，只保留"指令 + 坐标"版本。
