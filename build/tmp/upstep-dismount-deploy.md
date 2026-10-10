# 部署记录：下车修正 + 认领清理 + UpStep 2.25

时间：2026-09-23 16:17（服务器重启完成）
产物：`build/libs/espetro-1.1.3-i.jar`（16518256 B）→ 服务器 `mods/espetro-1.1.3-x.jar`
启动结果：`Done (3.242s)!`，Espetro 战场准备 `status=READY prepared=3 warnings=0 error=null`

## 一、代码改动（需重启，已生效）

### 1. 跳边 / 观战 / 重连 / 对局重置时强制下车
`org/espetro/team/GameStateManager.java` → `clearPlayerRoundAssignment(ServerPlayer)`

```java
if (player.getVehicle() != null) {
    player.stopRiding();
}
```

堵住的漏洞：玩家在**敌方载具内部**时被管理员跳边（观战→队伍分支不击杀），
换队后座位校验不会再触发（人已经在车里），可继续使用敌方载具。
现在所有调用 `clearPlayerRoundAssignment` 的路径（跳边两个分支、设为观察者、
重连归队、对局开始/强制结束/重置）都会先脱离载具，之后每次上车重新走阵营校验。

### 2. 待处理认领申请随换队作废
`org/espetro/vehicle/VehicleEventHandler.java` 新增：

```java
public static void clearClaimsFor(UUID memberUuid)   // 删除该玩家在所有载具队列里的申请，空队列回收
public static void clearAllClaims()                  // 清空全部（对局重置）
```

- `GameStateManager.clearPlayerRoundAssignment` 里调用 `clearClaimsFor(player.getUUID())`；
- `VehicleManager.reset()` 里追加 `clearAllClaims()`（地图切换 / 对局重置时载具已全删）。

理由：队长在换队后仍会收到该队员在**旧队伍**提交的 `[通过]/[否决]` 按钮，
点"通过"会把载具归属写到一个已不存在的编制上。

字节码核对（reobf 后）：`clearPlayerRoundAssignment` 中依次为
`m_20202_()`(getVehicle) → `m_8127_()`(stopRiding) → `clearClaimsFor(UUID)`。

## 二、数据改动（`/reload` 已生效）

全部 **47** 台载具的 `UpStep` 统一改为 **2.25**（先按 1.75 发过一版，随后按要求改为 2.25）：

| 原值 | 台数 | 车型 |
| --- | --- | --- |
| 1.1 | 2 | ah64, z20（直升机） |
| 1.5 | 16 | m1126/m1128/m113/m1296, mv3×3, sx1, ural4320×3, zbl08, zlt11, zsl10, btr80, btr82 |
| 2.2 | 16 | bmp1am/bmp2/bmp2d/bmp2m, gaz_tigr×3, matv×4, stryker×4, t72av |
| 2.25 | 12 | bmp3, csk181, m1a2sepv1/v2, m3a3, t72b3, t90mh, zbd04a, zbd05, ztd05, ztz96a, ztz99a |
| 无 | 1 | sx1_a（补齐） |

`UpStep` 即 SBW `DefaultVehicleData.getUpStep()`，被 `VehicleEntity.getStepHeight()` 直接返回，
决定载具能直接"抬腿"越过的台阶高度（爬坡能力）。回读校验：`{"2.25": 47}`，0 异常。
生效方式为控制台 `/reload`（SBW 的 `ComplexJsonResourceReloadListener` 在 reload 中重新解析了
`dragonrise_reforge:sbw/vehicles` 与 `fcp:sbw/vehicles`），未再重启服务器。

reload 中的解析报错均为**改动前就存在**的：`fcp:example_trailer`（示例文件）、
`fcp:humveem2`（模组自带，不在 kubejs 数据包内），两者在 08:17:45 启动时同样报错。
KubeJS 的 2 条报错是用户自己脚本的 `EsVehEvents is not defined`，与本次无关。

## 三、回滚点

- 上一版 jar（认领/归属版本）：`build/tmp/espetro-1.1.3-x-claim.jar`（16517723 B）
- 本次改动前的数据备份（原始值 1.1/1.5/2.2/2.25）：`build/tmp/srv-vehicles-before-upstep/`
- 1.75 版镜像：`build/tmp/srv-vehicles-upstep/`
- 2.25 版备份/镜像：`build/tmp/srv-vehicles-before-upstep-2.25/`、`build/tmp/srv-vehicles-upstep-2.25/`
