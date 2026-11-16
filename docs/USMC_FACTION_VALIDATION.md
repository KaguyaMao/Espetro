# 美国海军陆战队四份编制文件校验报告

> 对象：`C:\Users\Administrator\Desktop\` 下 `usmc_3th_scout.json` / `usmc_1th_tank.json` / `usmc_31th.json` / `usmc_4th.json`
> 依据：`FactionDataLoader`（拒载/兼容规则）、`VehicleConfig.applyActiveMap`（运行时上限）、服务端注册表（311 个载具实体 ID，扫 125 个 jar）

## 0. 结论速览

| 文件 | 能否直接载入 | 主要阻塞 |
|---|---|---|
| usmc_3th_scout.json | ❌ 拒载 | `vehicles.car`/`supply_truck` 未在 `VehTypes` 声明 |
| usmc_1th_tank.json | ✅ 可载入 | 无载具/职业结构性错误 |
| usmc_31th.json | ❌ 拒载 | `vehicles.car`/`supply_truck` 未在 `VehTypes` 声明 |
| usmc_4th.json | ✅ 可载入 | 无载具/职业结构性错误 |

**四份共有的问题**：
1. **职业 ID 全部用 `US_1th_*`**，与线上 `us_1th_ri.json` 重名 13 个、彼此之间 14 个全重名。加载器把职业存进**全局** `classKits`（`FactionDataLoader.java:126-127`），后加载的会覆盖先加载的，人数上限也会共用 → 必须按编制改前缀。
2. **`mm_armor:mm_armor:blackboots_boots` 双命名空间**（每文件 28 处）→ 该 ID 不存在，靴子会静默丢失；正确是 `mm_armor:blackboots_boots`。
3. `tacz:sound_suppressor`（每文件 2 处）不是有效配件 ID（线上美军编制也有同样问题）→ 消音器静默丢失。
4. 相对线上 `us_1th_ri`：**多 `US_1th_ASSAULT`、少 `US_1th_BIG_SQUADMG`**（班用机枪）。若直接替换线上美军编制，班用机枪职业会消失。
5. `max` 字段在运行时被忽略：`VehicleConfig.java:216` 用 `cfg.max = entities.size() * per_max_count`，表内“实上限”按此计算。

## 第3轻装甲侦察营（`usmc_3th_scout.json`，74673 B）

- 名称 `第3轻装甲侦察营`｜队伍 `DEFEND`｜faction_id `USA`｜show_name `美国海军陆战队`｜icon `⭐`｜selection_image `usmc_3th.png`｜audio `usa`
- VehTypes：`mbt,mgs,ifv,acv,apc,transport_helicopter`

**载具**

| 类型 | 实体 | 台数 | 实上限 | 座位 | 兵力 | 刷新(min) | 部署延迟 attack/defend(s) | 地图点位 |
|---|---|---|---|---|---|---|---|---|
| mbt | dragonrise_reforge:m1a2sepv2 | 1 | 1 | - | 15 | 15 | 1200/1200 | 2 |
| ifv | fcp:lav25<br>fcp:lav25<br>fcp:lav25 | 3 | 3 | 2 | 10 | 10 | 0/600 | 3 |
| car | dragonrise_reforge:hummve<br>dragonrise_reforge:hummve | 2 | 2 | 0 | 5 | 3 | - | 4 |
| supply_truck | fcp:kamaz<br>fcp:kamaz | 2 | 2 | 0 | 5 | 3 | 0/0 | 2 |
| transport_helicopter | fcp:huey_m60<br>fcp:huey_m60 | 2 | 2 | - | 10 | 5 | 600/600 | 2 |

**职业**

| ID | 名称 | maxPlayers | 每班 | team_count | strict_count | 变体 | 解锁(班) |
|---|---|---|---|---|---|---|---|
| `US_1th_COMMANDER` | 队长 | 1 | 1 | True | False | 倍镜/机瞄 | 0 |
| `US_1th_CREW_Leader` | 载具队长 | 1 | 1 | True | False | 倍镜/机瞄 | 0 |
| `US_1th_MEDIC` | 医护兵 | 100 | 2 | False | False | default/倍镜 | 0 |
| `US_1th_CREW` | 载具组员 | 8 | 2 | False | True | default | 2 |
| `US_1th_RIFLEMAN` | 步枪兵 | 100 | -1 | False | False | 倍镜/机瞄 | 0 |
| `US_1th_ASSAULT` | 自动步枪 | 6 | 4 | False | False | 机瞄/倍镜/握把/握把倍镜 | 3 |
| `US_1th_SQUADMG` | 班组机枪 | 2 | 1 | False | True | default/光瞄 | 6 |
| `US_1th_GRENADIER` | 榴弹兵 | 2 | 2 | False | True | default/倍镜 | 3 |
| `US_1th_ANTITANK` | 轻型反坦克兵 | 2 | 1 | False | False | default/倍镜 | 3 |
| `US_1th_MARKSMAN` | 精确射手 | 2 | 1 | False | False | default | 6 |
| `US_1th_MMG` | 通用机枪手 | 2 | 1 | False | True | 机瞄/光瞄 | 6 |
| `US_1th_HEAVYANTITANK` | 重型反坦克兵 | 2 | 1 | False | True | default/光瞄 | 6 |
| `US_1th_ENGINEER` | 战斗工兵 | 2 | 1 | True | False | default/倍镜 | 6 |
| `US_1th_SCOUT` | 侦察兵 | 2 | 1 | False | False | default/倍镜 | 6 |

**致命错误**

- `vehicles.car` 未在 `VehTypes` 声明 → **整份拒载**（`FactionDataLoader.java:164-165`）
- `vehicles.car` 实体 `dragonrise_reforge:hummve` 不在注册表（`:261` 兼容检查失败）
- `vehicles.car` 实体 `dragonrise_reforge:hummve` 不在注册表（`:261` 兼容检查失败）
- `vehicles.supply_truck` 未在 `VehTypes` 声明 → **整份拒载**（`FactionDataLoader.java:164-165`）
- `vehicles.transport_helicopter` 实体 `fcp:huey_m60` 不在注册表（`:261` 兼容检查失败）
- `vehicles.transport_helicopter` 实体 `fcp:huey_m60` 不在注册表（`:261` 兼容检查失败）

**注意**：`VehTypes` 声明了 `mgs`, `acv`, `apc` 但没有配车（该类型不会生成载具）。

## 海军陆战队第一坦克营（`usmc_1th_tank.json`，74078 B）

- 名称 `海军陆战队第一坦克营`｜队伍 `DEFEND`｜faction_id `USA`｜show_name `美国海军陆战队`｜icon `⭐`｜selection_image `us_1th.png`｜audio `usa`
- VehTypes：`mbt,mgs,ifv,acv,apc`

**载具**

| 类型 | 实体 | 台数 | 实上限 | 座位 | 兵力 | 刷新(min) | 部署延迟 attack/defend(s) | 地图点位 |
|---|---|---|---|---|---|---|---|---|
| mbt | dragonrise_reforge:m1a1hc | 1 | 1 | - | 15 | 15 | 0/600 | 2 |
| mgs | dragonrise_reforge:m1a1hc | 1 | 1 | - | 15 | 15 | 600/1200 | 3 |
| acv | dragonrise_reforge:aav7a1 | 1 | 1 | - | 7 | 5 | 0/0 | 3 |
| apc | dragonrise_reforge:aavc7c1<br>dragonrise_reforge:aavc7c1 | 2 | 2 | - | 7 | 5 | 0/0 | 3 |

**职业**

| ID | 名称 | maxPlayers | 每班 | team_count | strict_count | 变体 | 解锁(班) |
|---|---|---|---|---|---|---|---|
| `US_1th_COMMANDER` | 队长 | 1 | 1 | True | False | 倍镜/机瞄 | 0 |
| `US_1th_CREW_Leader` | 载具队长 | 1 | 1 | True | False | 倍镜/机瞄 | 0 |
| `US_1th_MEDIC` | 医护兵 | 100 | 2 | False | False | default/倍镜 | 0 |
| `US_1th_CREW` | 载具组员 | 8 | 2 | False | True | default | 2 |
| `US_1th_RIFLEMAN` | 步枪兵 | 100 | -1 | False | False | 倍镜/机瞄 | 0 |
| `US_1th_ASSAULT` | 自动步枪 | 6 | 4 | False | False | 机瞄/倍镜/握把/握把倍镜 | 3 |
| `US_1th_SQUADMG` | 班组机枪 | 2 | 1 | False | True | default/光瞄 | 6 |
| `US_1th_GRENADIER` | 榴弹兵 | 2 | 2 | False | True | default/倍镜 | 3 |
| `US_1th_ANTITANK` | 轻型反坦克兵 | 2 | 1 | False | False | default/倍镜 | 3 |
| `US_1th_MARKSMAN` | 精确射手 | 2 | 1 | False | False | default | 6 |
| `US_1th_MMG` | 通用机枪手 | 2 | 1 | False | True | 机瞄/光瞄 | 6 |
| `US_1th_HEAVYANTITANK` | 重型反坦克兵 | 2 | 1 | False | True | default/光瞄 | 6 |
| `US_1th_ENGINEER` | 战斗工兵 | 2 | 1 | True | False | default/倍镜 | 6 |
| `US_1th_SCOUT` | 侦察兵 | 2 | 1 | False | False | default/倍镜 | 6 |

**注意**：`VehTypes` 声明了 `ifv` 但没有配车（该类型不会生成载具）。

## 第31海军陆战远征部队（`usmc_31th.json`，74912 B）

- 名称 `第31海军陆战远征部队`｜队伍 `DEFEND`｜faction_id `USA`｜show_name `美国海军陆战队`｜icon `⭐`｜selection_image `usmc_31th.png`｜audio `usa`
- VehTypes：`mbt,mgs,ifv,acv,apc,transport_helicopter`

**载具**

| 类型 | 实体 | 台数 | 实上限 | 座位 | 兵力 | 刷新(min) | 部署延迟 attack/defend(s) | 地图点位 |
|---|---|---|---|---|---|---|---|---|
| mbt | dragonrise_reforge:m1a2sepv2 | 1 | 1 | - | 15 | 15 | 1200/1200 | 2 |
| ifv | fcp:lav25 | 1 | 1 | 2 | 10 | 10 | 0/600 | 3 |
| acv | dragonrise_reforge:aav7a1 | 1 | 1 | - | 7 | 5 | 0/0 | 3 |
| car | dragonrise_reforge:hummve | 1 | 1 | 0 | 5 | 3 | - | 4 |
| supply_truck | fcp:kamaz<br>fcp:kamaz | 2 | 2 | 0 | 5 | 3 | 0/0 | 2 |
| transport_helicopter | dragonrise_reforge:uh60 | 1 | 1 | - | 10 | 5 | 600/600 | 2 |

**职业**

| ID | 名称 | maxPlayers | 每班 | team_count | strict_count | 变体 | 解锁(班) |
|---|---|---|---|---|---|---|---|
| `US_1th_COMMANDER` | 队长 | 1 | 1 | True | False | 倍镜/机瞄 | 0 |
| `US_1th_CREW_Leader` | 载具队长 | 1 | 1 | True | False | 倍镜/机瞄 | 0 |
| `US_1th_MEDIC` | 医护兵 | 100 | 2 | False | False | default/倍镜 | 0 |
| `US_1th_CREW` | 载具组员 | 8 | 2 | False | True | default | 2 |
| `US_1th_RIFLEMAN` | 步枪兵 | 100 | -1 | False | False | 倍镜/机瞄 | 0 |
| `US_1th_ASSAULT` | 自动步枪 | 6 | 4 | False | False | 机瞄/倍镜/握把/握把倍镜 | 3 |
| `US_1th_SQUADMG` | 班组机枪 | 2 | 1 | False | True | default/光瞄 | 6 |
| `US_1th_GRENADIER` | 榴弹兵 | 2 | 2 | False | True | default/倍镜 | 3 |
| `US_1th_ANTITANK` | 轻型反坦克兵 | 2 | 1 | False | False | default/倍镜 | 3 |
| `US_1th_MARKSMAN` | 精确射手 | 2 | 1 | False | False | default | 6 |
| `US_1th_MMG` | 通用机枪手 | 2 | 1 | False | True | 机瞄/光瞄 | 6 |
| `US_1th_HEAVYANTITANK` | 重型反坦克兵 | 2 | 1 | False | True | default/光瞄 | 6 |
| `US_1th_ENGINEER` | 战斗工兵 | 2 | 1 | True | False | default/倍镜 | 6 |
| `US_1th_SCOUT` | 侦察兵 | 2 | 1 | False | False | default/倍镜 | 6 |

**致命错误**

- `vehicles.car` 未在 `VehTypes` 声明 → **整份拒载**（`FactionDataLoader.java:164-165`）
- `vehicles.car` 实体 `dragonrise_reforge:hummve` 不在注册表（`:261` 兼容检查失败）
- `vehicles.supply_truck` 未在 `VehTypes` 声明 → **整份拒载**（`FactionDataLoader.java:164-165`）

**注意**：`VehTypes` 声明了 `mgs`, `apc` 但没有配车（该类型不会生成载具）。

## 第4海军陆战队两栖准备大队（`usmc_4th.json`，74248 B）

- 名称 `第4海军陆战队两栖准备大队`｜队伍 `DEFEND`｜faction_id `USA`｜show_name `美国海军陆战队`｜icon `⭐`｜selection_image `usmc_4th.png`｜audio `usa`
- VehTypes：`mbt,ifv,acv,apc`

**载具**

| 类型 | 实体 | 台数 | 实上限 | 座位 | 兵力 | 刷新(min) | 部署延迟 attack/defend(s) | 地图点位 |
|---|---|---|---|---|---|---|---|---|
| mbt | dragonrise_reforge:m1a1hc | 1 | 1 | - | 15 | 15 | 0/600 | 2 |
| ifv | fcp:lav25<br>fcp:lav25<br>fcp:lav25 | 3 | 3 | 2 | 10 | 10 | 0/600 | 3 |
| acv | dragonrise_reforge:aav7a1<br>dragonrise_reforge:aav7a1 | 2 | 2 | - | 7 | 5 | 0/0 | 3 |
| apc | dragonrise_reforge:aavc7c1<br>dragonrise_reforge:aavc7c1 | 2 | 2 | - | 7 | 5 | 0/0 | 3 |

**职业**

| ID | 名称 | maxPlayers | 每班 | team_count | strict_count | 变体 | 解锁(班) |
|---|---|---|---|---|---|---|---|
| `US_1th_COMMANDER` | 队长 | 1 | 1 | True | False | 倍镜/机瞄 | 0 |
| `US_1th_CREW_Leader` | 载具队长 | 1 | 1 | True | False | 倍镜/机瞄 | 0 |
| `US_1th_MEDIC` | 医护兵 | 100 | 2 | False | False | default/倍镜 | 0 |
| `US_1th_CREW` | 载具组员 | 8 | 2 | False | True | default | 2 |
| `US_1th_RIFLEMAN` | 步枪兵 | 100 | -1 | False | False | 倍镜/机瞄 | 0 |
| `US_1th_ASSAULT` | 自动步枪 | 6 | 4 | False | False | 机瞄/倍镜/握把/握把倍镜 | 3 |
| `US_1th_SQUADMG` | 班组机枪 | 2 | 1 | False | True | default/光瞄 | 6 |
| `US_1th_GRENADIER` | 榴弹兵 | 2 | 2 | False | True | default/倍镜 | 3 |
| `US_1th_ANTITANK` | 轻型反坦克兵 | 2 | 1 | False | False | default/倍镜 | 3 |
| `US_1th_MARKSMAN` | 精确射手 | 2 | 1 | False | False | default | 6 |
| `US_1th_MMG` | 通用机枪手 | 2 | 1 | False | True | 机瞄/光瞄 | 6 |
| `US_1th_HEAVYANTITANK` | 重型反坦克兵 | 2 | 1 | False | True | default/光瞄 | 6 |
| `US_1th_ENGINEER` | 战斗工兵 | 2 | 1 | True | False | default/倍镜 | 6 |
| `US_1th_SCOUT` | 侦察兵 | 2 | 1 | False | False | default/倍镜 | 6 |
