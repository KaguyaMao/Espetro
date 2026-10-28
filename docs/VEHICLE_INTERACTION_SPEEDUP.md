# 载具交互读条 / 物资存取 提速 3 倍

## 需求
User said: “将上下车，更换座位在载具上存取物资的时间减为原来的1/3”。

## 对应实现位置
| 行为 | 配置 | 原值 | 新值 |
| --- | --- | --- | --- |
| 上车读条（按住交互） | `config/espetro-common.toml` → `vehicle.mountDelayTicks` | 60 tick (3.0s) | **20 tick (1.0s)** |
| 下车读条 | `config/espetro-common.toml` → `vehicle.dismountDelayTicks` | 60 tick (3.0s) | **20 tick (1.0s)** |
| 换座（按住 Shift） | `config/espetro-common.toml` → `vehicle.seatSwitchDelayTicks` | 100 tick (5.0s) | **33 tick (1.65s)** |
| 载具上存取弹药/建材（每 100 单位一次） | `config/espetro/fortifications.json` → `vehicle_service.transfer_interval_ticks` | 20 tick (1.0s) | **7 tick (0.35s)** |

代码出处：
- `org/espetro/vehicle/VehicleInteractionConfig.java:30-35`（Forge common config，服务端与客户端各读本地文件）
- `org/espetro/vehicle/VehicleMountServer.java:51`、`org/espetro/vehicle/DismountServer.java:25/32`、`org/espetro/vehicle/SeatSwitchServer.java:30/36`（服务端权威读条）
- `org/espetro/network/VehicleSupplyActionPacket.java:130`（`allowTransferNow` 用 `FortificationConfig.vehicleService().transferIntervalTicks` 做节流）
- 客户端进度条数据来自服务端 `MountProgressPacket` / `VehicleSupplySyncPacket`，因此改服务端即改变实际手感；客户端本地配置同步修改只为让本地门控一致。

## 改动落地
- 服务端 `/config/espetro-common.toml`、`/config/espetro/fortifications.json` 已上传（回读校验值正确）。
- 本机客户端 `…\Squad预发布测试\config\espetro-common.toml`、`…\config\espetro\fortifications.json` 同步修改，原件备份为 `*.bak-speed`。
- 改动前服务端原件备份：`build/tmp/speed/before/`；生成脚本 `build/tmp/speed/gen.mjs`、`build/tmp/speed/client.mjs`。
- 生效：服务端本次操作时实例处于**停止**状态，下次开服即生效（Forge common config 与 fortifications.json 均在启动时读取）。

## 备注
- 主城/大厅阶段上车本来就跳过读条（`GameStateManager#getCurrentPhase().isLobbyLike()`）。
- `nativeVehicles` 白名单里的载具（无人机、tow、container、hj8、9m133、m2、qjz89、dshk、zu23）完全走 SBW 原生交互，不受本组配置影响。
- 想要"存取更快"但保持粒度，还可以调大 `transfer_amount`（默认 100），本次未动。
