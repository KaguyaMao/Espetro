# 载具武器 DefaultZoom 改动记录（2026-09-30）

需求：把服务端上「步兵战车 + 坦克」的武器 `DefaultZoom` 全部改成 **8**。
范围经确认：**只改编制实际出场的 14 台**；**该车所有武器**都设 8。

## 1. 关键机制（决定了能不能纯服务端做）
- `DefaultZoom` 的实际消费点是**客户端**：`ClientEventHandler` 里
  `event.setFOV(event.getFOV() / ((VehicleEntity) vehicle).getDefaultZoom(player))`；
  `VehicleEntity.getDefaultZoom()` 取当前座位武器数据的 `GunProp.DEFAULT_ZOOM`。
  服务端自己**从不调用** `getDefaultZoom`（全类扫描只有 `ClientEventHandler` 一处调用）。
- 但卓越前线的载具数据是**服务端同步给客户端**的：
  `CustomData.VEHICLE_DATA = DataLoader.createData("sbw/vehicles", DefaultVehicleData.class, /*synced=*/true, true, ...)`，
  同步通过 `DataLoader.onDataPackSync(@SubscribeEvent OnDatapackSyncEvent)` →
  `new DataSyncMessage(path, data.serializeToString())` 发给玩家；
  客户端收到后 `data.getDataMap().clear(); putAll(...)` **整表覆盖**本地数据。
  `OnDatapackSyncEvent` 在**玩家登录**与 **`/reload`**（对全在线玩家）时触发。
- 结论：**改服务端 kubejs 数据有效**，玩家在下次登录或下一次 `/reload` 时生效，无需改动客户端整合包。

## 2. 改动清单（14 个文件，均为已有 kubejs 覆盖，未新建文件、未遮蔽 jar）
| 载具 | 武器（改前 → 8） |
|---|---|
| `dragonrise_reforge:ztz99a` | Cannon 8→8、MachineGun 3→8、PassengerMachineGun 3→8 |
| `dragonrise_reforge:ztz96a` | Cannon、MachineGun、PassengerMachineGun 3→8 |
| `dragonrise_reforge:m1a2sepv1` | Cannon、MachineGun、hMachineGun、PassengerMachineGun 3→8 |
| `dragonrise_reforge:m1a2sepv2` | Cannon、MachineGun、PassengerMachineGun 3→8 |
| `dragonrise_reforge:t72b3` | Cannon、MachineGun、PassengerMachineGun 3→8 |
| `dragonrise_reforge:t90mh` | Cannon、MachineGun、PassengerMachineGun 3→8 |
| `dragonrise_reforge:bmp3` | 100MM_Cannon、MainMachineGun、Cannon 3→8 |
| `dragonrise_reforge:m3a3` | Cannon、MainMachineGun、Missile 3→8 |
| `dragonrise_reforge:zbd04a` | 100MM_Cannon、MainMachineGun、Cannon 3→8 |
| `dragonrise_reforge:zbd05` | Cannon、MachineGun、Missile 3→8 |
| `dragonrise_reforge:zbl08` | Cannon、MachineGun、Missile 3→8 |
| `dragonrise_reforge:m1296` | Cannon、MachineGun、PassengerMachineGun 3→8（`NewWeapon/1/2` 是空占位 `{}`，未动） |
| `fcp:bmp1am` | Cannon、Coax 3→8 |
| `fcp:btr82` | Cannon、Coax 3→8 |

写入位置：`kubejs/data/<ns>/sbw/vehicles/<name>.json` 的 **对象形式** `"Weapons": { … }` 表内
（座位里的 `"Weapons": [ "Cannon", … ]` 是武器名数组，已跳过）。

## 3. 校验
- 严格 JSON 解析：14/14 通过。
- 每台车的每个非空武器对象 `DefaultZoom == 8`：14/14 通过。
- 「除 DefaultZoom 外无任何差异」（递归删除该字段后比对 JSON 树）：14/14 通过。
- 上传后回下载逐文件 SHA256 比对：**14/14 一致**（`build/tmp/zoom-deploy.log`）。
- `/reload` 后服务端日志：`Reloaded with no KubeJS errors!` + `Server resource reload complete!`。

## 4. 回滚点
- 改动前的二进制原文：`build/tmp/zoom/before/`（14 个），上传副本：`build/tmp/zoom/after/`
- 清单：`build/tmp/zoom/manifest.json`；回滚 = 把 `before/` 重新上传并 `/reload`。

## 5. 相关脚本
- `build/tmp/zoom-build.mjs` —— 拉取（二进制）+ 文本级改写（只碰对象形式的武器表，保留原格式）
- `build/tmp/zoom-verify.mjs` —— 三层校验
- `build/tmp/zoom-deploy.mjs` —— 上传 + 回下载 SHA256 校验

## 6. 遗留
- 其余 jar 内置的坦克/步兵战车（约 70+ 台，如 `superbwarfare:ztz_99a`、`vvp` 的 BMP/Puma 等）
  **未改**：要改必须新建完整 kubejs 覆盖文件（会遮蔽 jar，之后模组更新那些车不生效），
  或在模组源码里改并重新构建 jar。
- 服务端 `logs/latest.log` 已涨到 18 MB，面板**文本读取接口**会直接返回
  `Error: 超出最大文件编辑限制`；取日志请走二进制下载（`download-file.mjs`）。
