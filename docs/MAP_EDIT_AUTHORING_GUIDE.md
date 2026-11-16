# 地图编辑模式 · 使用教程

面向服主/管理员。用 `/espetro edit` 系列指令，在独立的编辑维度里改地图（方块），
并在同一地点直接编排**据点**、**出生点**、**载具出生点**，最后保存回地图模板。

> 结论先行：**方块用 `/espetro editsave` 落盘；点位/出生点/载具点用 `/espetro edit configsave` 落盘。**
> 两者都会把配置备份到 `world/espetro-map-backups/`，并且 **EsConfig 只在开服时加载** —— 保存完必须重启服务器才生效。

---

## 1. 前提与限制

| 项 | 要求 |
|---|---|
| 权限 | `permission level ≥ 2`（OP） |
| 游戏阶段 | 必须在**主城**（`LOBBY` 类阶段）。对局进行中 `create` 会被拒绝 |
| 地图 | 必须是「可用地图」（启动日志 `战场地图启动准备: status=READY prepared=N`）；不可用地图会列出拒载原因 |
| 维度 | 编辑维度 `espetro:edit` 随 datapack 常驻注册（`data/espetro/dimension/edit.json`），开服即可用 |
| 会话 | 同一时间只允许一个编辑会话；会话不跨重启（编辑维度区块会被服务端重置逻辑清理） |

编辑维度 = 把地图模板的 `region/entities/poi/data` 整目录复制一份。玩家在编辑维度里的坐标与
地图存档的世界坐标**一一对应**，所以不用做任何换算。

---

## 2. 一条完整的作业流程

```
# ① 开一个编辑会话（选地图，Tab 可补全英文地图名）
/espetro edit CREATE_PLUS
#    → 聊天栏：编辑维度已创建 …  使用 /espetro edit in 进入。

# ② 进入编辑维度（会把你传送到该地图的防守方出生点）
/espetro edit in

# ③ 改方块（自由建造/破坏）。改完直接存：
/espetro editsave        # 写回地图模板 region/entities/poi/data，然后所有人回主城

# ④ 想同时编排点位，就不要先 editsave，接着做下面第 3/4/5 节，
#    先 configsave 存配置，最后 editsave 存方块。
/espetro edit configsave  # 只写 EsConfig，不关维度、不写区块
/espetro editsave         # 再写方块并结束会话
```

> 顺序建议：**先把方块改完 → 再编点位 → `configsave` → `editsave`**。
> `editsave` 之后会话结束，点位就编不了了（需要重新 `edit` 一次）。

---

## 3. 编辑据点（CapturePoints）

一个据点 = 一个长方体区域 + 批次号。

```
/espetro edit point corner A        # 站在长方体第 1 个角上执行
/espetro edit point corner A        # 站在对角执行 → 聊天栏会打印范围与下一步提示
/espetro edit point apply A 1       # 提交：名字 A、批次 1
```

- 第 3 次执行 `corner` 会**重置**为新的第 1 角（方便画错重来）；`point clear A` 直接清空。
- 水平方向至少 3×3 格，否则 `apply` 会拒绝。
- 与已有据点重叠会给出黄色提醒（不阻止保存）。
- 查看/预览/删除：

```
/espetro edit point list            # 列出所有据点、批次、范围 + 待提交角点 + 草稿警告
/espetro edit point info A          # 单个据点的范围与 y 区间
/espetro edit point show            # 在当前维度用白色粒子画出所有据点框
/espetro edit point show A          # 只画 A，并在中心立一根光柱
/espetro edit point del A           # 删除
```

写入位置默认是 `EsConfig/CapturePoints.json`。如果这张图用 `Points/` 预设，需要先切目标：

```
/espetro edit capture list          # 列出 legacy 与 Points/*.json
/espetro edit capture target Points/A.json
```

> `Points/` 预设优先于 `CapturePoints.json`（由 `PointsPresetLoader` 决定）。选错目标 = 改了也不生效。

---

## 4. 编辑出生点（spawn_points.json）

站在想作为出生点的位置、面朝想朝向的方向，执行：

```
/espetro edit spawn attack      # 进攻方出生点 = 你当前位置 + 你的朝向（自动吸附到 90°）
/espetro edit spawn defend      # 防守方出生点
/espetro edit spawn show        # 打印两侧当前值，并用粒子标出（蓝=进攻，橙=防守）
```

两侧都必须存在，否则 `configsave` 会报“出生点缺失”。只想改一侧就直接覆盖那一侧，另一侧保持原值。

---

## 5. 编辑载具出生点（VehSpawn.json）

### 5.1 关键概念

- 一个载具点**同时**包含 `attack`（进攻方出车位）与 `defend`（防守方出车位）两个坐标 —— 同一张图两队都用它。
- 因为人一次只能站一个位置，指令一次只写一侧；**只填了一侧的点是无效草稿**，`configsave` 会被拦下。
- `id` 只是“坑位名”，不绑定具体车辆。
- **数组顺序 = 编制里的槽位顺序**：编制某类型第 i 台车用该类型第 i 个点。
  所以新增点只会排在末尾；要控制顺序只能删了重加。
- 地图某类型的**点数上限**决定编制能声明几台车（`entities.size() > points.size()` → 整份编制被跳过）。
  改点数前先想清楚哪些编制在用它。

### 5.2 指令

```
# 新建一个点（id 自动生成 <type>_<n>），先写进攻侧
/espetro edit veh add mbt attack
# 切到防守侧同一位置附近，再写另一侧（用上一步返回的 id）
/espetro edit veh add mbt defend mbt_3

# 也可以一次指定 id
/espetro edit veh add mgs attack mgs_2

/espetro edit veh list              # 列出所有类型与各自点数
/espetro edit veh list mbt          # 列出 mbt 的每个点：id 与两侧坐标
/espetro edit veh show              # 粒子画出全部载具点
/espetro edit veh show mbt          # 只画 mbt
/espetro edit veh del mbt mbt_3     # 删除某个点

/espetro edit veh type list         # 草稿已声明的类型 + 地图 VehTypes
/espetro edit veh type add car      # 手动声明一个类型（一般 veh add 会自动补）
```

- `type` 建议用地图 `VehSpawn.json` 里已有的类型（`mbt/mgs/ifv/acv/apc/car/truck/supply_truck/transport_helicopter`），Tab 有补全。
- 载具朝向取你的视角（吸附 90°）。防守方通常 0°，进攻方通常 180°，按地图需要定。
- 每个载具点会自动生成一个「载具坑补给站」，侧向偏移 6 格，不用手动摆。

---

## 6. 保存、回滚与生效

| 指令 | 落盘内容 | 是否结束会话 | 是否需要重启 |
|---|---|---|---|
| `/espetro edit configsave` | `CapturePoints.json`（或 `Points/x.json`）、`spawn_points.json`、`VehSpawn.json` | 否 | **是** |
| `/espetro editsave` | `region/entities/poi/data` 四个目录（方块与实体） | 是（玩家回主城、删编辑维度） | 是（方块重启后进图才看到） |
| `/espetro editabandon` | 什么都不写 | 是 | 否 |

- 备份位置：`world/espetro-map-backups/<地图名>-EsConfig-<时间戳>/`（配置）与
  `world/espetro-map-backups/<地图名>-<时间戳>/`（方块，保留最近 5 份）。
- 写配置时只改目标键，**保留** `modes`、`_comment`、`endBehavior` 等未知字段；
  `totalBatches` 会按据点最大批次自动重算。
- 保存后聊天栏会给出写入的文件列表与备份路径；`configsave` 成功与否都可复现。

### 生效步骤

1. `configsave`（必要时再 `editsave`）
2. 重启服务器：`node <桌面>/mcsm-server.mjs restart 1`（或面板）
3. 核对启动日志：
   ```
   加载外部编制: /data/EsFactions/<…>.json
   战场地图启动准备: status=READY prepared=3 warnings=0 error=null
   Done (3.xxx s)! For help, type "help"
   ```
   若有 `[编制拒载]`，说明点位/载具点改动导致某编制不兼容，按提示回滚。

---

## 7. 保存前自检清单

```
/espetro edit status
```

会打印：据点数量、载具类型数、载具点总数、写入目标，并列出 `problems()`：

- 据点名为空 / 批次越界（1..64）/ 据点太小
- 出生点缺一侧
- 载具点缺一侧（**最常见**）

`problems()` 非空时 `configsave` 与 `editsave` 都会直接拒绝，不会写半份配置。

另外留意黄色警告（不阻止保存）：

- 单侧出生点距离过大（> 256 格）
- 据点框互相重叠
- 同类型同侧两点间距 < 5 格（容易叠车）

---

## 8. 常见问题

| 现象 | 原因 / 处理 |
|---|---|
| `编辑维度未创建，请先 /espetro edit <地图名>。` | 还没开会话，或会话已结束 |
| `地图 X 未通过启动校验：…` | 该地图缺必需 EsConfig 文件，先补文件再重启 |
| `已存在编辑会话：…` | 先 `editsave` 或 `editabandon` |
| `对局进行中，无法进入地图编辑模式` | 回主城阶段再操作 |
| `configsave` 报“载具点缺一侧” | 用 `veh list <type>` 找到该 id，补另一侧 |
| 保存成功但进图没变化 | 没重启；或据点写到了 `CapturePoints.json` 而地图实际读 `Points/` 预设 |
| 某编制突然消失 | 点位减少导致 `entities.size() > points.size()`，看 `[编制拒载]` 日志并回滚 |
| 想要全量重来 | `editabandon` 丢弃，或从 `world/espetro-map-backups/` 覆盖回去 |

---

## 9. 命令速查

```
/espetro edit <地图名>                     创建编辑维度（Tab 补全）
/espetro edit in                           进入编辑维度
/espetro edit status                       会话与草稿概览（含保存前自检）
/espetro edit point corner <名字>          记录角点（连续两次）
/espetro edit point apply <名字> <批次>    提交据点
/espetro edit point info|del|clear <名字>  查看 / 删除 / 清空角点
/espetro edit point list                   列出据点
/espetro edit point show [名字]            粒子预览
/espetro edit spawn attack|defend          设置出生点
/espetro edit spawn show                   查看并预览出生点
/espetro edit veh add <类型> <attack|defend> [id]
/espetro edit veh del <类型> <id>
/espetro edit veh list [类型]
/espetro edit veh show [类型]
/espetro edit veh type list | type add <类型>
/espetro edit capture list | capture target <legacy|Points/x.json>
/espetro edit configsave                   写 EsConfig（不关维度）
/espetro editsave                          写方块并结束会话
/espetro editabandon                       丢弃并结束会话
```
