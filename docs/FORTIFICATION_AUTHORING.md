# 工事可视化编辑器（选定棒）管理员手册

> 版本：Espetro 1.1.3（协议 1.34，`/espetro fort` 系列指令与选定棒物品）。
> 本功能**仅限管理员**（permission 2 = OP）。

---

## 1. 五分钟上手

```
/espetro fort wand          # 给自己一根"工事选定棒"（原版木棍贴图）
左键点方块                  # 角 A
右键点方块                  # 角 B
潜行 + 右键                 # 锚点（放置时的旋转中心）
/espetro fort info          # 看尺寸/方块数/实体数 + 预检报告
/espetro fort save hesco    # 导出模板 + 自动写入定义
/espetro fort reload        # 免重启生效（玩家或控制台都可执行）
```

然后在 Alt 轮盘 →「建造工事」里就能选到它。

**建议：在主城搭。** 主城阶段可以随时 `reload`，不用重启服务器。

---

## 2. 坐标与朝向（务必理解）

```
世界坐标 = 锚点 + R(origin_offset + 模板坐标 − pivot)
```

选定棒做的事：

| 你选的 | 变成 |
|---|---|
| 两个角 | 模板包围盒（`min` 角是模板原点） |
| 锚点 | `pivot = 锚点 − min`（旋转中心） |

两条推论：

1. **锚点那一格在任意朝向下都停在玩家瞄准的位置**（它就是旋转中心）。
   所以锚点建议选**贴地那层的中心格**或**门口地面格**。
2. **基准朝向是"北"**：玩家面朝北放置时和你搭的一模一样；面朝东/南/西时，
   整座工事绕锚点旋转 90/180/270°。

---

## 3. 空气与"结构空位"（最容易踩的坑）

导出时默认 **只保存实际存在的方块**，空气格不写进模板。

原因：编译后空气格会变成"放置时必须清空这格"，而且**所有**格位都会被要求可替换
（`spaceIsClear`），如果把包围盒里的空气一起存，玩家放置时得清空整个包围盒（含建筑外的空白），
根本没法用。

| 想要的效果 | 怎么做 |
|---|---|
| 这格保持原样，什么都别动 | 在该格放**结构空位 `structure_void`**（导出后是 `IGNORE`） |
| 连空气一起存、放置时清空整片区域 | `save <名字> --carve`（危险，自行确认） |
| 不想保存实体 | `save <名字> --no-entities` |

另外：**导出前把脚手架/临时方块清掉**，它们会被一起存进模板。
若你**故意**用 `minecraft:barrier` 做隐形占位，那没问题——注意它们会被算作可损伤部件，
参与施工进度与摧毁结算。

---

## 4. 指令一览

| 指令 | 说明 |
|---|---|
| `/espetro fort` / `help` | 用法 |
| `/espetro fort wand` | 发一根选定棒 |
| `/espetro fort pos1 [x y z]` | 设角 A（不带坐标 = 准星所指方块） |
| `/espetro fort pos2 [x y z]` | 设角 B |
| `/espetro fort anchor [x y z]` | 设锚点 |
| `/espetro fort info` | 选区详情 + 预检报告 + 实体策略 |
| `/espetro fort save <名字> [参数]` | 导出模板 + 写入定义 |
| `/espetro fort list` | 列出模板（方块/实体数、作者、时间、是否已定义） |
| `/espetro fort delete <名字>` | 删除模板文件（不动 JSON 定义） |
| `/espetro fort reload` | 免重启重载工事定义（玩家/控制台均可） |
| `/espetro fort clear` | 清空选区 |

`save` 参数：

```
--name <显示名>           默认 = 名字
--icon <贴图>             默认 minecraft:item/stick（贴图路径，见 §7 备注）
--cost <建造型点数>       默认 100
--progress <施工进度>     默认 100（structural_value 同值）
--radio-range <true|false>  默认 true
--roles commander,squad_leader  默认 commander,squad_leader,fireteam_leader
--damageable-entities 0,2  把指定实体索引也当作可损伤部件
--instant                 放置即建成（跳过施工阶段）
--no-entities             不保存实体
--carve                   连空气一起存（见 §3）
--no-define               只写模板，不改 JSON
--force                   覆盖同名定义
```

### 选定棒操作

| 操作 | 效果 |
|---|---|
| 左键方块 | 设角 A（不会挖掉方块） |
| 右键方块 | 设角 B（不会放置/交互） |
| 潜行 + 右键 | 设锚点 |
| 潜行 + 左键 | 清空选区 |

手持时左上角有 HUD：尺寸 / 方块数 / 实体数 / 锚点 / pivot / 基准朝向 / 预检结论；
世界里画黄色选区框、青色锚点格、绿色模板原点格（预检不过时选区框变红）。

---

## 5. 导出前预检（为什么必须做）

工事定义**解析/编译失败是全局的**：一旦报错 → `工事 registry 无法冻结` →
战场启动直接失败（地图开不起来）。所以编辑器在 `info`/`save` 时按编译器同一套规则预检：

| 检查 | 上限/规则 |
|---|---|
| 单轴长度 | `limits.max_template_axis`（默认 64） |
| 方块数 | `limits.max_template_blocks`（默认 4096） |
| 实体数 | `limits.max_template_entities`（默认 32） |
| 模板体积 | `limits.max_template_nbt_bytes`（默认 2 MB） |
| 危险方块 | 命令方块 / 链式 / 循环 / 结构方块 / jigsaw（+ `#espetro:forbidden_fortification_blocks`）→ **拒绝**并列出坐标 |
| 方块实体 | 只允许 `banner / sign / hanging_sign / skull / decorated_pot`；其余按策略剔除 NBT（见 §6） |
| 结构实体 | 只允许 `armor_stand / item_frame / glow_item_frame / painting / text_display / item_display / block_display / interaction`；其余按策略剔除 |
| 乘客深度 | `limits.max_passenger_depth`（默认 4） |
| 可损伤部件 | 至少 1 个非空气方块或实体 |
| 锚点 | 必须在选区内 |
| 重名 | `espetro:<名字>` 已存在 → 拒绝（`--force` 覆盖） |

- 玩家（含你自己）不会被存进模板（原版行为）。
- 报错逐条列出，带坐标，方便回到现场改。

---

## 6. 实体与方块实体策略（`entity_policy`，A+B 方案）

- **模板文件永远是无损保存**（实体完整 NBT），方便随时改策略重载。
- **加载/编译时**按策略过滤，默认 `filter`：不允许的类型/字段**剔除并记警告**，
  不会因为多放一只鸡就把整份配置搞崩。改成 `reject` 则恢复旧的"一票否决"。

```json
"entity_policy": {
  "mode": "filter",
  "extra_entity_types": [],
  "extra_block_entity_types": ["create:copycat"],
  "extra_entity_fields": {"create:copycat": ["Item", "Material"]},
  "allow_player_entities": false
}
```

| 字段 | 说明 |
|---|---|
| `mode` | `filter`（默认，剔除+警告）/ `reject`（直接报错） |
| `extra_entity_types` | 追加允许的实体类型，如 `["minecraft:pig","dragonrise_reforge:xxx"]` |
| `extra_block_entity_types` | 追加允许的方块实体，如 `["minecraft:chest"]`（会保留箱子 NBT） |
| `extra_entity_fields` | 按类型追加允许的 NBT 字段（**对方块实体同样生效**）；键 `"*"` 对所有类型生效 |
| `allow_player_entities` | 是否允许 `minecraft:player`（默认 false，不建议开） |

> 实例：瞭望塔模板里有 24 个 `create:copycat_panel`，其方块实体 `create:copycat` 需要
> `Item` 与 `Material` 两个字段才能保住复制外观，上面就是为此配的。

查看当前策略与被剔除项：`/espetro fort info`（也会列在服务器日志
`工事模板实体策略剔除 N 项` 里）。

---

## 7. instant（放置即建成）

给定义加 `"construction": { ..., "instant": true }`（或用选定棒 `save <名字> --instant`）后，
该工事**放下就直接建成**，不再需要按住工兵铲修建：

- 前置流程完全不变：仍然先做角色/阶段/电台范围/空间校验，仍然先扣建材料；
- 若最终空间被占用导致建成失败，会自动**退化为普通施工**并提示玩家清空后铲击；
- `required_progress` 仍然保留，用于结构值上限与摧毁/拆除结算（电台就是 600/600 + instant）；
- 冻结日志会列出 instant 项，便于核对：
  `工事 JSON v2 已事务冻结: global=10 maps=3 aliases=12 策略剔除=0 instant=[espetro:radio]`

当前服务器上 `espetro:radio` 已配 `instant: true`：**电台放下即成为可用兵站**。

> 贴图备注：`icon.texture` 是**贴图路径**（客户端直接当纹理加载），
> 例如 `doomsday_decoration:textures/block/hesco.png`；
> 默认值 `minecraft:item/stick` 会被当成不存在的贴图，轮盘里显示缺失贴图。

---

## 8. 文件位置与生效

| 内容 | 路径 |
|---|---|
| 工事定义 | `config/espetro/fortifications.json`（v2） |
| 导出的模板 | `config/espetro/fortification_templates/<名字>.nbt`（gzip 结构 NBT） |
| 模板元信息 | `config/espetro/fortification_templates/<名字>.meta.json`（作者/时间/选区/统计） |
| 内置模板 | mod 自带（`radio / hab_attack / hab_defend / ammo_crate / sandbag_wall / vehicle_supply_station_fallback`） |

- 编译模板时**文件目录优先**，找不到才回退数据包/内置模板 —— 所以内置 5 项工事不受影响，
  也可以手工把 `.nbt` 丢进这个目录来添加模板（不必用选定棒）。
- `save` 会自动在 `fortifications.json` 里追加一条 v2 定义；写前备份
  `fortifications.json.bak-<时间戳>`，写入后自检，失败不影响原文件。
- 生效方式：
  - `/espetro fort reload` —— **免重启**，门槛：当前是主城阶段且没有在建/已建工事；
    失败会**回滚**到重载前的定义（不会把战场搞坏）。玩家与控制台都能执行。
  - 完整重启 —— 任何时候都可用。

---

## 9. 典型流程（推荐）

1. 进主城，创造模式搭好工事。
2. `/espetro fort wand`。
3. 左键一个角、右键对角（把整个建筑框进去，包括高度）。
4. 潜行+右键点**贴地那层的中心格**当锚点。
5. `/espetro fort info` → 看红色报错（有就先修），黄色提醒（例如实体会被剔除）自己确认。
6. `/espetro fort save sandbag_line --name 沙袋防线 --cost 150 --progress 150`。
7. `/espetro fort reload`。
8. 开图 → Alt 轮盘 →「建造工事」→ 选它 → 左键确认施工。
9. 四个朝向都放一次，确认锚点不动、旋转正确、实体（盔甲架/展示框等）出现。

---

## 10. 排错

| 现象 | 原因/处理 |
|---|---|
| `/espetro fort save` 报"预检未通过" | 按 `/espetro fort info` 的红色条目修（超尺寸/危险方块/锚点未设/选区全空气） |
| 提示"已存在工事定义" | 换名字，或 `--force`（会备份旧配置） |
| `reload` 提示"当前不是主城阶段" | 对局中不允许热重载，结束后再试或完整重启 |
| `reload` 提示"存在在建/已建工事" | 同上，完整重启 |
| `reload` 失败并提示已回滚 | 配置里有编译不过的条目，按提示修好后重试（战场仍可用） |
| 日志 `实体 xxx 已被 entity_policy 剔除` | 想让它们进工事就加到 `entity_policy.extra_entity_types` |
| 方块外观丢失（如 copycat 面板） | 把该方块实体类型与所需字段加到 `extra_block_entity_types` / `extra_entity_fields` |
| 箱子里的东西没了 | 方块实体 NBT 默认被剔除；需要就加 `extra_block_entity_types: ["minecraft:chest"]` |
| 放置时"红色范围内存在方块或实体" | 模板里混进了脚手架方块（它们也被算作需要出现的部件），清掉重新导出 |
| 客户端进不来/提示协议不匹配 | 客户端 mods 必须是同一版本（协议 1.34） |
| 轮盘里图标是紫黑格 | `icon` 填的是物品 id 而不是贴图路径；改成 `namespace:textures/block/xxx.png` |

---

## 11. 仅管理员

- `/espetro` 根命令本身要求 permission 2（OP）。
- 选定棒即使被 `/give` 给普通玩家也**完全无用**：交互前会校验 `hasPermissions(2)`，
  否则只提示"选定棒仅管理员可用"且不改变任何状态。
- 选区数据只发给他本人，其他客户端不渲染、不收到包。
- 导出/写入定义/热重载全部记服务器日志（作者、文件大小、备份名、冻结统计）。
