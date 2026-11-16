# USMC 四编制修复 + 载具数据上传清单

> 时间：2026-11-03 20:5x　服务器：23384-现代战争（status=3，0 人在线）

## 一、编制文件修复（已上传至 `EsFactions/`，需重启生效）

| 文件 | 服务端大小 | 职业前缀 | VehTypes（修正后） |
|---|---|---|---|
| `usmc_3th_scout.json` | 74,545 B | `USMC_3th_*` | mbt, ifv, car, supply_truck, transport_helicopter |
| `usmc_1th_tank.json` | 74,003 B | `USMC_1th_*` | mbt, mgs, acv, apc |
| `usmc_31th.json` | 74,820 B | `USMC_31th_*` | mbt, ifv, acv, car, supply_truck, transport_helicopter |
| `usmc_4th.json` | 74,149 B | `USMC_4th_*` | mbt, ifv, acv, apc |

### 具体改动
1. **职业 ID 撞名**：14 个职业全部改为编制专属前缀（`US_1th_*` → `USMC_3th_*` 等），不再与线上 `us_1th_ar.json`/`us_1th_ri.json` 或彼此冲突（`FactionDataLoader.java:126-127` 是全局 `classKits`）。
2. **VehTypes**：与 `vehicles` 完全对齐 —— 补上漏声明的 `car`/`supply_truck`（3th_scout、31th），删掉多声明的 `mgs`/`acv`/`apc`（3th_scout）、`ifv`（1th_tank）、`mgs`/`apc`（31th）。未对齐会触发 `FactionDataLoader.java:164-165` 整份拒载。
3. **悍马拼写**：`dragonrise_reforge:hummve` → `dragonrise_reforge:humvee`（3th_scout ×2、31th ×1）。
4. **直升机**：`fcp:huey_m60` → `fcp:huey_door_gunner_m60`（3th_scout ×2）。
5. **kamaz 保留**：按指示视为 M939 的临时替代，不改 ID；只把它的倒车按既有规则抬到 17 km/h（0.2→0.236111）并把倒车加速度 ×3（`Decrement` 0.013→0.033035）。
6. **map_icon**：全部载具补齐（见下表）。
7. 附带修掉 `mm_armor:mm_armor:blackboots_boots` 双命名空间（每文件 28 处）→ `mm_armor:blackboots_boots`；原写法 ID 不存在，靴子会静默丢失。

### map_icon 分配
| 载具 | 图标 |
|---|---|
| `dragonrise_reforge:m1a2sepv2` / `m1a1hc` | `map_tank.png` |
| `fcp:lav25` | `t_map_wheeledrecon.png` |
| `dragonrise_reforge:humvee` | `map_jeep.png` |
| `fcp:kamaz`（M939 替代） | `map_truck_logistics.png` |
| `dragonrise_reforge:aav7a1` | `map_trackedapc.png` |
| `dragonrise_reforge:aavc7c1` | `t_map_trackedapc_logistics.png` |
| `fcp:huey_door_gunner_m60` | `t_map_helicopter_scout.png` |
| `dragonrise_reforge:uh60` | `map_transporthelo.png` |

图标均取自 `espoints-1.1.1e.jar` 的 `assets/espoints/textures/gui/map|mark`（220 个），逐个校验存在。

## 二、新载具 data 上传（`kubejs/data/<ns>/sbw/vehicles/`）

| # | 载具 ID | 名称 | 类型 | 生命 | 质量 | 座位 | 容量 | 极速（前/倒 km/h） | 文件 |
|---|---|---|---|---|---|---|---|---|---|
| 1 | `dragonrise_reforge:humvee` | M1151 悍马 | Car | 160 | 20 | 5 | Medium | 108 / 36 | 17,282 B |
| 2 | `dragonrise_reforge:m1a1hc` | M1A1HC 主战坦克 | Tank | 600 | 64.6 | 4 | - | 61 / 36 | 10,227 B |
| 3 | `dragonrise_reforge:aav7a1` | AAVP7A1 装甲输送车 | APC | 300 | 22.9 | 21 | Medium | 59 / 25 | 11,491 B |
| 4 | `dragonrise_reforge:aavc7c1` | AAVP7A1 补给型 | APC | 300 | 22.9 | 21 | Medium | 59 / 25 | 8,585 B |
| 5 | `fcp:lav25` | LAV-25 步兵战车 | APC | 250 | 13.6 | 9 | Medium | 68 / 23 | 10,757 B |
| 6 | `fcp:huey_door_gunner_m60` | 休伊 通用直升机 | Helicopter | 280 | 1.0 | 9 | - | - | 6,035 B |
| 7 | `fcp:kamaz` | M939 补给卡车（临时替代） | Car | 150 | 20 | 13 | Medium | 79 / 17 | 7,218 B |

- 来源：客户端 jar（与服务端同名同版本：`dragonrise_reforge-1.5.1-20261102.jar` 40,294,799 B、`fcp-1.1.1-更改特化版.jar` 45,867,253 B），文件内容与 jar 内一致（kamaz 例外：仅倒车两项按规则调整）。
- 上传后逐个回读尺寸：7/7 与本地一致。
- 这些载具此前都不在 kubejs 覆盖目录中（现有 dragonrise 37 个 + fcp 19 个覆盖文件均不含它们）。

## 三、生效方式
- 载具 data（kubejs）→ `/reload` 即可（SBW 的 `ComplexJsonResourceReloadListener` 会重读）。
- 编制（EsFactions）→ **必须重启**（`EsFactions 已冻结` 只在启动时加载）。
