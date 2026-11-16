# 地图编辑模式（/espetro edit 系列）设计方案

> 目标读者：本仓库维护者。本文只描述**方案**，未改动任何代码。
> 结论先行：Espetro 已经具备"把地图模板拷进独立维度 → 挂载 → 卸载 → 删档 → 把人送回主城"的全套机制（战场开局/收尾用的就是它），编辑模式基本是**把这套机制换个入口、加一个反向保存**，不需要新的维度技术。

---

## 0. 实现状态（2026-11-04 已上线）

在 `espetro-1.1.3-x.jar`（16,912,380 B，构建自 `espetro-1.1.3-l.jar`）中实现并实机验证：

| 文件 | 作用 |
|---|---|
| `src/main/resources/data/espetro/dimension/edit.json` | 静态注册 `espetro:edit` 的 LevelStem（`minecraft:overworld` 类型 + noise 生成器），绕过 LEVEL_STEM 冻结问题 |
| `src/main/java/org/espetro/dimension/BattlefieldWorldManager.java` | 抽出 `evacuateToHub` / `unregisterLevel`；新增编辑器锁、`mountEditorDimension`、`detachEditorForSave/WithoutSave`、`copyTemplateForEditor`、`closeEditorLevel`、`deleteEditorDirectory`、`copyEditorTree/deleteEditorTree` |
| `src/main/java/org/espetro/dimension/MapEditManager.java`（新增，~400 行） | 会话状态机 `NONE/WORKING/READY`；`create/enter/save/abandon`；反向写回 + 备份轮转（保留 5 份） |
| `src/main/java/org/espetro/command/EspetroCommand.java` | `/espetro edit <map>`（补全地图名）、`edit in`、`editsave`、`editabandon` |
| `src/main/java/org/espetro/team/GameStateManager.java` | 编辑会话进行中禁止 `prestart`，提示先 save/abandon |

实机验证记录（服务端控制台）：

```
[14:56:49] 地图编辑维度已就绪: CREATE_PLUS（Server）
[14:56:49] 编辑维度已创建：CREATE_PLUS (CREATE_PLUS)。使用 /espetro edit in 进入。
[14:57:21] 已从服务器 Tick 列表摘除可丢弃战场维度: espetro:edit
[14:57:21] 已放弃编辑，地图存档未改动。
[14:59:26] 编辑维度已刷盘: espetro:edit
[14:59:26] 地图 CREATE_PLUS 编辑结果已写回模板: /data/EsWorld/CREATE_PLUS（备份 ./world/./espetro-map-backups/CREATE_PLUS-20261104-145926）
[14:59:26] [Espetro] 地图 CREATE_PLUS 已保存并覆盖存档，编辑维度已关闭。
```

写入前后 `EsWorld/CREATE_PLUS/region` 均为 **241 个文件 / 478,011,392 字节**（无损往返），备份目录 `world/espetro-map-backups/CREATE_PLUS-20261104-145926` 已生成，`world/dimensions/espetro/edit` 已删除。

移植注意：
- 挂载走 `cp --reflink=always`，跨文件系统会打印 `cp: failed to clone ...: Operation not supported` 但会回退成普通复制，不影响结果。
- 每次保存会在 `world/espetro-map-backups/` 落一份全量备份（本图约 478 MB），轮转保留 5 份，需预留磁盘。
- `map.png`（战术地图底图）不会因保存而重新导出，底图改了要另行替换 `EsConfig/map.png`。

---

## 1. 需求原文

1. 新增一个编辑维度；
2. 主城中用 `/espetro edit <英文地图名(可自动补全)>` 打开编辑维度，并把选中地图拷进该维度；
3. `/espetro edit in` 进入编辑维度；不存在时提示"编辑维度未创建"；
4. `/espetro editsave` 把当前编辑结果写入并覆盖地图存档；
5. `/espetro editabandon` 丢弃编辑内容；
6. 4、5 执行后都把所有人传回主城，并删除编辑维度区块。

---

## 2. 现有可复用机制（源码实证）

| 能力 | 位置 | 说明 |
|---|---|---|
| 地图模板目录 | `ActiveMapConfig.templateWorldDir`（`mapconfig/ActiveMapConfig.java:41`） | 每张图一个目录，含 `region/`、`entities/`、`poi/`、`data/`、`EsConfig/`、`level.dat`；启动时校验 `region/` 存在 |
| 维度注册 | `dimension/DimensionPackBootstrap.java:44-52` | 启动期为每个可用地图向内存 `SERVER_DATA` 包注入 `data/<ns>/dimension/<path>.json`；**datapack 加载后维度注册表冻结**，运行期不能新增维度 |
| 拷模板 | `dimension/BattlefieldWorldManager.java:786` `copyTemplate` → `:796` `replaceSaveCopyFast` | 优先 reflink（写时复制），失败回退普通拷贝；只拷 `region/entities/poi/data/EsConfig`，不碰 `level.dat`/`playerdata` |
| 挂载维度 | 同上 `:727` `createServerLevel` | 从 `Registries.LEVEL_STEM` 取 stem，`DerivedLevelData` 继承主世界规则，`forgeGetWorldMap().put(key, level)` |
| 卸载+送主城+删档 | `:632` `detachForDiscard`、`:671` `closeDiscardedLevel`、`:700` `discardAndDelete` | `detachForDiscard` 已实现"重生点写回主城 + `teleportTo(overworld, 共享出生点)`"，正是需求 6 要的行为 |
| 存档目录安全 | `:917` `validateDimensionDirectory` | 维度目录必须落在当前存档内，且逐级解析祖先路径，禁止 `..`/符号链接逃逸 |

战场流程：选图 → `importAndLoad`（拷模板进 `<save>/dimensions/<ns>/<slug>` → 挂载）→ 开打 → `cleanupBattlefield`（清兵站/载具 → 卸载 → 删目录）。
**编辑模式 = 同一个拷贝/挂载/删除管线，去掉对局逻辑，再加一个"目录写回模板"的反向步骤。**

---

## 3. 编辑维度的定义

- 固定 ID：`espetro:edit`（`ResourceLocation("espetro", "edit")`）。
- 存档目录：`<save>/dimensions/espetro/edit`。
- **启动时无条件注册**：在 `DimensionPackBootstrap.onAddPackFinders` 里额外注入一份固定的维度 json（新资源 `src/main/resources/data/espetro/dimension/edit.json`，形状照抄现有地图维：overworld 型 level stem、无结构、无生成器差异——地图地形完全来自被拷贝的 `region/`）。
- 该维度**不进入** `getUsableMaps()`、不参与投票、`prepareAllAtStartup` 跳过；启动校验只要求"能注册"，不要求有存档。

> 为什么必须常驻注册：`createServerLevel` 从 `LEVEL_STEM` 注册表取 stem，取不到直接失败（`:738-741`）。注册表在 datapack 加载阶段冻结，运行期追加不可行。

---

## 4. 指令设计

所有子命令挂在现有 `/espetro` 根下（`command/EspetroCommand.java:33`，已是 `hasPermission(2)`）。

```java
dispatcher.register(Commands.literal("espetro")
    .requires(src -> src.hasPermission(2))
    .then(Commands.literal("edit")
        .then(Commands.argument("map", StringArgumentType.word())
              .suggests(MAP_SUGGESTIONS)          // 可用地图的英文目录名
              .executes(ctx -> MapEditManager.create(ctx, ctx.getArgument("map", String.class))))
        .then(Commands.literal("in")
              .executes(ctx -> MapEditManager.enter(ctx))))
    .then(Commands.literal("editsave")   .executes(ctx -> MapEditManager.save(ctx)))
    .then(Commands.literal("editabandon").executes(ctx -> MapEditManager.abandon(ctx))));
```

`MAP_SUGGESTIONS`：遍历 `ExternalConfigBootstrap.getUsableMaps()`，补全 `map.mapFolder`（英文），tooltip 里带 `displayName`（中文名）与 `dimensionId`。

### 4.1 `/espetro edit <英文地图名>`
前置校验（任一不满足 → 说明原因并返回 0）：

| 条件 | 不满足时的提示 |
|---|---|
| 阶段为 `LOBBY`（`GameStateManager.getCurrentPhase().isLobbyLike()`） | `§c对局进行中，无法进入编辑模式；请先结束对局。` |
| 名字能匹配到 usable 地图（大小写不敏感，先精确 `mapFolder` 再忽略大小写） | `§c未找到地图 X。可用：…` |
| 当前无编辑会话 | `§e已存在未保存的编辑会话（地图 Y），请先 /espetro editsave 或 /espetro editabandon` |
| 无战场导入/清理任务在跑（`BattlefieldWorldManager.isBusy()`） | `§c战场正在切换，请稍后重试。` |

执行：
1. 若 `<save>/dimensions/espetro/edit` 残留（异常退出），先卸载+删除，记 WARN。
2. `copyTemplate(map, editDir, tempDir)` → 异步（沿用 `IO` 单线程执行器模型）。
3. 主线程 `createServerLevel(server, EDIT_KEY)` 挂载。
4. 写会话状态（内存 + 持久化标记 `EsWorld/.edit_session.json`：`{map_folder, display_name, created_by, created_at, edit_dimension, version:1}`）。
5. 反馈：`§a已创建编辑维度：<中文名>（<英文名>），区块文件 N 个。使用 /espetro edit in 进入。`

### 4.2 `/espetro edit in`
- 会话不存在 / 维度未挂载 → `§c编辑维度未创建`（按需求原文）。
- 否则：记录玩家原位置（可选，用于异常回滚）、传送到编辑维度；
  - 目标点优先级：会话里记录的上次位置 → 地图 `spawn_points.json` 的 DEFEND → `(0, 128, 0)`；
  - 建议进入时切换 `CREATIVE` + 允许飞行，并记下原游戏模式，`editsave`/`editabandon` 时恢复（是否创造模式做成配置项，默认开）。

### 4.3 `/espetro editsave`
1. 校验会话存在。
2. 把编辑维度里所有玩家送回主城：复用 `detachForDiscard` 的"写回重生点 + `teleportTo(overworld, sharedSpawnPos)`"（需求 6）。
3. 卸载维度并强制刷盘（`level.save(null, true, false)`）。
4. **反向拷贝**（新方法 `copySaveToTemplate(editDir, templateDir, tempDir)`）：
   - 只拷白名单 `region/`、`entities/`、`poi/`、`data/`；
   - **整目录替换**而不是逐文件合并（区块被删/变小的情况必须靠"删旧目录再落新目录"才不会留残块）；
   - `EsConfig/`、`level.dat`、`map.png` **不回写**（配置由人维护，避免编辑器误覆盖；`map.png` 见 §7）；
   - 原子性：先拷到 `templateDir` 同级临时目录，成功后 `删除旧 → move` 到位；失败则保留旧档并报错。
5. 备份：写回前把 `templateDir` 里将被替换的 4 个目录打包到 `EsWorld/_backup/<mapFolder>-<yyyyMMdd-HHmmss>/`（保留最近 N 份，默认 5）。
6. 删除编辑维度目录（`discardAndDelete` 同一套卸载/删除/校验逻辑）。
7. 清会话状态与标记文件；广播 `§a地图 <中文名> 已保存并覆盖存档（N 个区块文件，备份 <路径>）`。

### 4.4 `/espetro editabandon`
- 同上 2、3、6、7，**不做**反向拷贝。

---

## 5. 崩溃/重启恢复

- 服务器启动时（`BattlefieldWorldManager.prepareAtStartup` 之前）：读 `EsWorld/.edit_session.json`：
  - 维度目录存在 → 打 WARN：`发现未完成的编辑会话（地图 X，创建于 ...），请用 /espetro editsave 或 /espetro editabandon 处理`，**不自动删除**（避免误吞编辑成果），维度保持未挂载状态，`edit in` 时自动把它挂回来。
  - 维度目录不存在 → 标记文件视为陈旧，删除并记 INFO。
- `dimension/BattlefieldNamespaceReset.java` 按命名空间清理残留维度目录时，必须把 `espetro/edit` 加白名单，否则重启会把编辑内容清掉（**这是最容易踩的坑**）。

---

## 6. 代码落点

| 文件 | 改动 |
|---|---|
| `editor/MapEditManager.java`（新增，~400 行） | 会话状态机（NONE/COPYING/READY/SAVING）、`create/enter/save/abandon`、异步任务与 busy 保护、会话标记读写、备份 |
| `dimension/DimensionPackBootstrap.java` | 注入固定 `espetro:edit` 维度 json（+1 项资源） |
| `src/main/resources/data/espetro/dimension/edit.json`（新增） | 编辑维度定义（照抄现有地图维度形状） |
| `command/EspetroCommand.java` | 4 个子命令 + 地图名补全 |
| `dimension/BattlefieldWorldManager.java` | 把 `copyTemplate`/`createServerLevel`/`detachForDiscard`/`discardAndDelete`/`validateDimensionDirectory` 抽成包内可复用的静态工具（或新 `dimension/DimensionIO.java`），供编辑器调用，避免复制粘贴 |
| `dimension/BattlefieldNamespaceReset.java` | 白名单 `espetro/edit` |
| `docs/MAP_EDIT_MODE_PLAN.md` | 本文件 |

可选配置化（第一期可硬编码）：创造模式进入、备份保留份数、是否允许控制台建图。

---

## 7. 已知边界与风险

1. **维度必须常驻注册**：多一个空维度，代价可忽略（无区块、不加载）。
2. **同一时间只能有一个编辑会话**（需求是单数；也避免多人同时改同一张图）。
3. **仅主城阶段可用**：对局中开编辑器会与战场维度、计分、兵站系统打架；`LOBBY` 之外一律拒绝。
4. **`map.png`（战术地图底图）不会自动更新**：`EsConfig/TacticalMap.json` 的 `backgroundImage` 指向 `map.png`，它是外部导出的静态图（CREATE_PLUS 的就有 12 MB）。地形改完后需重新导出覆盖，否则战术地图还是旧地形。方案默认**不自动重绘**，在 `editsave` 成功提示里明确写出"请重新导出 map.png"；如果要做自动重绘，那是独立功能（读 region → 渲染 PNG + 重切 EsPoints 瓦片），工作量另算。
5. **拷贝耗时**：reflink 支持的文件系统（btrfs/xfs）几乎瞬时；否则大图普通拷贝可能十几秒到几十秒 → 全程异步 + 状态提示，期间 `Busy` 锁禁止第二次 `edit`/战场切换。
6. **编辑维度隔离**：兵站/载具/计分都以"当前激活地图的 `dimensionKey`"为准，编辑维度 key 不同 → 天然不被这些系统接管；但 `BastionData` 等若有"遍历所有维度"的清理逻辑，需要复核一遍（`cleanupBattlefield` 里对维度内实体/方块的清理是有针对性的，不会误伤编辑维度）。
7. **回写粒度**：`EsConfig/` 不回写意味着"编辑者不能通过摆方块改动配置"（这是有意的），如果将来要支持"在维度里摆出生点标记 → 回写 spawn_points.json"，需要另设标记方块协议。

---

## 8. 验收清单

- [ ] 主城 `/espetro edit create_plus` → 日志出现拷贝 + `已挂载编辑维度: espetro:edit`，`edit` 目录出现 region 文件
- [ ] `/espetro edit in` 进入后能看到该地图地形；重复 `edit` 被拒绝
- [ ] 未创建时 `/espetro edit in` 返回 `编辑维度未创建`
- [ ] 改几块方块 → `/espetro editsave` → 玩家回主城、`edit` 目录被删、模板目录 `region/` 被替换、备份目录生成
- [ ] 再次 `/espetro edit <同一张图>` → 能看到刚才的修改
- [ ] `/espetro editabandon` → 修改不落盘，模板目录 mtime 不变
- [ ] 对局进行中执行 `edit` 被拒绝
- [ ] 编辑中重启服务器 → 启动日志出现"未完成的编辑会话"，`edit in` 能继续（不丢内容）
