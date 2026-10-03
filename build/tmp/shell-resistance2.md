# 服务端载具对「机炮炮弹 / 主炮炮弹」的抗性（修正版）

判定依据（反编译 SBW 0.8.9.1 确认）：
- `superbwarfare:cannon_shell`（主炮）与 `superbwarfare:small_cannon_shell`（机炮）**直接命中**时造成的伤害类型是 `superbwarfare:projectile_hit`，直接实体就是炮弹本身。
- 炮弹**爆炸溅射**走 `CustomExplosion` → 伤害类型 `superbwarfare:custom_explosion`。
- `#superbwarfare:projectile`（含 gunfire/arrow/trident/tacz:bullet…）与 `#superbwarfare:projectile_absolute`（gunfire_absolute…）**只对子弹生效，对炮弹无效**；`projectile_explosion` 是反坦克地雷（Ptkm 等）的伤害类型。
- 条目按列表顺序结算：`*` 乘法；`-` = max(0, 伤害−值)；`+` = max(0, 伤害+值)。

下表为「基础伤害 → 实收伤害」：机炮直击基准 65（30mm APDS）、机炮 HE 直击 30、主炮直击 200（100/125mm）、爆炸 30、子弹 10（参考）。

| 载具 | 血量/炮塔 | 机炮直击 65 | 机炮HE 30 | 主炮直击 200 | 炮弹爆炸 30 | 参考·子弹 10 |
|---|---|---|---|---|---|---|
| dragonrise_reforge:ah64 | 300/无 | 37.0 (×0.570) | 17.1 (×0.570) | 180.0 (×0.900) | 28.5 (×0.950) | 0.3 (×0.030) |
| dragonrise_reforge:bmp3 | 400/炮塔300 轮100 引擎150 | 6.5 (×0.100) | 3.0 (×0.100) | 100.0 (×0.500) | 1.2 (×0.040) | 0.0 (×0.000) |
| dragonrise_reforge:csk181 | 200/无 | 6.5 (×0.100) | 3.0 (×0.100) | 100.0 (×0.500) | 1.2 (×0.040) | 0.0 (×0.000) |
| dragonrise_reforge:m1126 | 200/无 | 6.5 (×0.100) | 3.0 (×0.100) | 100.0 (×0.500) | 1.2 (×0.040) | 0.0 (×0.000) |
| dragonrise_reforge:m1128 | 200/炮塔200 轮100 引擎150 | 6.5 (×0.100) | 3.0 (×0.100) | 100.0 (×0.500) | 1.2 (×0.040) | 0.0 (×0.000) |
| dragonrise_reforge:m113 | 200/无 | 6.5 (×0.100) | 3.0 (×0.100) | 100.0 (×0.500) | 1.2 (×0.040) | 0.0 (×0.000) |
| dragonrise_reforge:m1296 | 200/炮塔200 轮100 引擎150 | 6.5 (×0.100) | 3.0 (×0.100) | 100.0 (×0.500) | 1.2 (×0.040) | 0.0 (×0.000) |
| dragonrise_reforge:m1a2sepv1 | 600/炮塔300 轮100 引擎150 | 6.5 (×0.100) | 3.0 (×0.100) | 60.0 (×0.300) | 0.1 (×0.003) | 0.0 (×0.000) |
| dragonrise_reforge:m1a2sepv2 | 400/炮塔300 轮100 引擎150 | 6.5 (×0.100) | 3.0 (×0.100) | 60.0 (×0.300) | 0.1 (×0.003) | 0.0 (×0.000) |
| dragonrise_reforge:m3a3 | 250/炮塔200 轮100 引擎150 | 6.5 (×0.100) | 3.0 (×0.100) | 100.0 (×0.500) | 1.2 (×0.040) | 0.0 (×0.000) |
| dragonrise_reforge:mv3 | 100/无 | 23.4 (×0.360) | 10.8 (×0.360) | 64.0 (×0.320) | 27.0 (×0.900) | 5.0 (×0.500) |
| dragonrise_reforge:mv3_armed | 250/无 | 23.4 (×0.360) | 10.8 (×0.360) | 64.0 (×0.320) | 27.0 (×0.900) | 5.0 (×0.500) |
| dragonrise_reforge:mv3_supply | 250/无 | 23.4 (×0.360) | 10.8 (×0.360) | 64.0 (×0.320) | 27.0 (×0.900) | 5.0 (×0.500) |
| dragonrise_reforge:sx1 | 100/无 | 93.6 (×1.440) | 43.2 (×1.440) | 256.0 (×1.280) | 54.0 (×1.800) | 10.0 (×1.000) |
| dragonrise_reforge:sx1_a | undefined/无 | 65.0 (×1.000) | 30.0 (×1.000) | 200.0 (×1.000) | 30.0 (×1.000) | 10.0 (×1.000) |
| dragonrise_reforge:t72b3 | 380/炮塔300 轮100 引擎150 | 6.5 (×0.100) | 3.0 (×0.100) | 60.0 (×0.300) | 0.1 (×0.003) | 0.0 (×0.000) |
| dragonrise_reforge:t90mh | 420/炮塔300 轮100 引擎150 | 6.5 (×0.100) | 3.0 (×0.100) | 60.0 (×0.300) | 0.1 (×0.003) | 0.0 (×0.000) |
| dragonrise_reforge:ural4320 | 300/无 | 23.4 (×0.360) | 10.8 (×0.360) | 64.0 (×0.320) | 27.0 (×0.900) | 5.0 (×0.500) |
| dragonrise_reforge:ural4320_supply | 300/无 | 23.4 (×0.360) | 10.8 (×0.360) | 64.0 (×0.320) | 27.0 (×0.900) | 5.0 (×0.500) |
| dragonrise_reforge:ural4320_zu23 | 300/无 | 23.4 (×0.360) | 10.8 (×0.360) | 64.0 (×0.320) | 27.0 (×0.900) | 5.0 (×0.500) |
| dragonrise_reforge:z20 | 300/无 | 37.0 (×0.570) | 17.1 (×0.570) | 180.0 (×0.900) | 28.5 (×0.950) | 0.3 (×0.030) |
| dragonrise_reforge:zbd04a | 200/炮塔200 轮100 引擎150 | 6.5 (×0.100) | 3.0 (×0.100) | 100.0 (×0.500) | 1.2 (×0.040) | 0.0 (×0.000) |
| dragonrise_reforge:zbd05 | 200/炮塔200 轮100 引擎150 | 6.5 (×0.100) | 3.0 (×0.100) | 100.0 (×0.500) | 1.2 (×0.040) | 0.0 (×0.000) |
| dragonrise_reforge:zbl08 | 175/炮塔200 轮100 引擎150 | 6.5 (×0.100) | 3.0 (×0.100) | 100.0 (×0.500) | 1.2 (×0.040) | 0.0 (×0.000) |
| dragonrise_reforge:zlt11 | 175/炮塔200 轮100 引擎150 | 6.5 (×0.100) | 3.0 (×0.100) | 100.0 (×0.500) | 1.2 (×0.040) | 0.0 (×0.000) |
| dragonrise_reforge:zsl10 | 175/无 | 6.5 (×0.100) | 3.0 (×0.100) | 100.0 (×0.500) | 1.2 (×0.040) | 0.0 (×0.000) |
| dragonrise_reforge:ztd05 | 200/炮塔200 轮100 引擎150 | 6.5 (×0.100) | 3.0 (×0.100) | 100.0 (×0.500) | 1.2 (×0.040) | 0.0 (×0.000) |
| dragonrise_reforge:ztz96a | 380/炮塔300 轮100 引擎150 | 6.5 (×0.100) | 3.0 (×0.100) | 60.0 (×0.300) | 0.1 (×0.003) | 0.0 (×0.000) |
| dragonrise_reforge:ztz99a | 400/炮塔300 轮100 引擎150 | 6.5 (×0.100) | 3.0 (×0.100) | 60.0 (×0.300) | 0.1 (×0.003) | 0.0 (×0.000) |
| fcp:bmp1am | 270/炮塔200 轮100 引擎150 | 6.5 (×0.100) | 3.0 (×0.100) | 100.0 (×0.500) | 1.2 (×0.040) | 0.0 (×0.000) |
| fcp:bmp2 | 300/无 | 6.5 (×0.100) | 3.0 (×0.100) | 100.0 (×0.500) | 1.2 (×0.040) | 0.0 (×0.000) |
| fcp:bmp2d | 200/无 | 6.5 (×0.100) | 3.0 (×0.100) | 100.0 (×0.500) | 1.2 (×0.040) | 0.0 (×0.000) |
| fcp:bmp2m | 300/无 | 6.5 (×0.100) | 3.0 (×0.100) | 100.0 (×0.500) | 1.2 (×0.040) | 0.0 (×0.000) |
| fcp:btr80 | 175/无 | 6.5 (×0.100) | 3.0 (×0.100) | 100.0 (×0.500) | 1.2 (×0.040) | 0.0 (×0.000) |
| fcp:btr82 | 175/炮塔200 轮100 引擎150 | 6.5 (×0.100) | 3.0 (×0.100) | 100.0 (×0.500) | 1.2 (×0.040) | 0.0 (×0.000) |
| fcp:gaz_tigr_gl | 175/无 | 6.5 (×0.100) | 3.0 (×0.100) | 100.0 (×0.500) | 1.2 (×0.040) | 0.0 (×0.000) |
| fcp:gaz_tigr_mg | 175/无 | 6.5 (×0.100) | 3.0 (×0.100) | 100.0 (×0.500) | 1.2 (×0.040) | 0.0 (×0.000) |
| fcp:gaz_tigr_rws | 175/无 | 6.5 (×0.100) | 3.0 (×0.100) | 100.0 (×0.500) | 1.2 (×0.040) | 0.0 (×0.000) |
| fcp:matv | 175/无 | 6.5 (×0.100) | 3.0 (×0.100) | 100.0 (×0.500) | 1.2 (×0.040) | 0.0 (×0.000) |
| fcp:matv_9in1 | 175/无 | 6.5 (×0.100) | 3.0 (×0.100) | 100.0 (×0.500) | 1.2 (×0.040) | 0.0 (×0.000) |
| fcp:matv_crow | 175/无 | 6.5 (×0.100) | 3.0 (×0.100) | 100.0 (×0.500) | 1.2 (×0.040) | 0.0 (×0.000) |
| fcp:matv_tow | 175/无 | 6.5 (×0.100) | 3.0 (×0.100) | 100.0 (×0.500) | 1.2 (×0.040) | 0.0 (×0.000) |
| fcp:stryker_dragoon | 200/炮塔200 轮100 引擎150 | 6.5 (×0.100) | 3.0 (×0.100) | 100.0 (×0.500) | 1.2 (×0.040) | 0.0 (×0.000) |
| fcp:stryker_m2 | 200/无 | 6.5 (×0.100) | 3.0 (×0.100) | 100.0 (×0.500) | 1.2 (×0.040) | 0.0 (×0.000) |
| fcp:stryker_mgs | 200/无 | 6.5 (×0.100) | 3.0 (×0.100) | 100.0 (×0.500) | 1.2 (×0.040) | 0.0 (×0.000) |
| fcp:stryker_mortar | 200/无 | 6.5 (×0.100) | 3.0 (×0.100) | 100.0 (×0.500) | 1.2 (×0.040) | 0.0 (×0.000) |
| fcp:t72av | 300/无 | 5.3 (×0.081) | 0.0 (×0.000) | 39.0 (×0.195) | 1.0 (×0.033) | 0.0 (×0.000) |

## 生效条目明细（只列对炮弹真正生效的条目）

### dragonrise_reforge:ah64  (HP 300)
  - 机炮直击: superbwarfare:projectile_hit * 0.6  [65.00 -> 39.00]  |  @superbwarfare:small_cannon_shell * 0.95  [39.00 -> 37.05]
  - 主炮直击: superbwarfare:projectile_hit * 0.6  [200.00 -> 120.00]  |  @superbwarfare:cannon_shell * 1.5  [120.00 -> 180.00]
  - 炮弹爆炸: @superbwarfare:small_cannon_shell * 0.95  [30.00 -> 28.50]

### dragonrise_reforge:bmp3  (HP 400)
  - 机炮直击: @superbwarfare:small_cannon_shell + 13  [65.00 -> 78.00]  |  All - 13  [78.00 -> 65.00]  |  All * 0.2  [65.00 -> 13.00]  |  @superbwarfare:small_cannon_shell * 5  [13.00 -> 65.00]  |  superbwarfare:projectile_hit * 1.35  [65.00 -> 87.75]  |  @superbwarfare:small_cannon_shell * 0.0740740741  [87.75 -> 6.50]
  - 主炮直击: @superbwarfare:cannon_shell + 13  [200.00 -> 213.00]  |  All - 13  [213.00 -> 200.00]  |  All * 0.2  [200.00 -> 40.00]  |  @superbwarfare:cannon_shell * 5  [40.00 -> 200.00]  |  superbwarfare:projectile_hit * 1.35  [200.00 -> 270.00]  |  @superbwarfare:cannon_shell * 0.3703703704  [270.00 -> 100.00]
  - 炮弹爆炸: @superbwarfare:small_cannon_shell + 13  [30.00 -> 43.00]  |  All - 13  [43.00 -> 30.00]  |  All * 0.2  [30.00 -> 6.00]  |  @superbwarfare:small_cannon_shell * 5  [6.00 -> 30.00]  |  superbwarfare:custom_explosion * 0.54  [30.00 -> 16.20]  |  @superbwarfare:small_cannon_shell * 0.0740740741  [16.20 -> 1.20]

### dragonrise_reforge:csk181  (HP 200)
  - 机炮直击: @superbwarfare:small_cannon_shell + 13  [65.00 -> 78.00]  |  All - 13  [78.00 -> 65.00]  |  All * 0.2  [65.00 -> 13.00]  |  @superbwarfare:small_cannon_shell * 5  [13.00 -> 65.00]  |  superbwarfare:projectile_hit * 1.35  [65.00 -> 87.75]  |  @superbwarfare:small_cannon_shell * 0.0740740741  [87.75 -> 6.50]
  - 主炮直击: @superbwarfare:cannon_shell + 13  [200.00 -> 213.00]  |  All - 13  [213.00 -> 200.00]  |  All * 0.2  [200.00 -> 40.00]  |  @superbwarfare:cannon_shell * 5  [40.00 -> 200.00]  |  superbwarfare:projectile_hit * 1.35  [200.00 -> 270.00]  |  @superbwarfare:cannon_shell * 0.3703703704  [270.00 -> 100.00]
  - 炮弹爆炸: @superbwarfare:small_cannon_shell + 13  [30.00 -> 43.00]  |  All - 13  [43.00 -> 30.00]  |  All * 0.2  [30.00 -> 6.00]  |  @superbwarfare:small_cannon_shell * 5  [6.00 -> 30.00]  |  superbwarfare:custom_explosion * 0.54  [30.00 -> 16.20]  |  @superbwarfare:small_cannon_shell * 0.0740740741  [16.20 -> 1.20]

### dragonrise_reforge:m1126  (HP 200)
  - 机炮直击: @superbwarfare:small_cannon_shell + 13  [65.00 -> 78.00]  |  All - 13  [78.00 -> 65.00]  |  All * 0.2  [65.00 -> 13.00]  |  @superbwarfare:small_cannon_shell * 5  [13.00 -> 65.00]  |  superbwarfare:projectile_hit * 1.35  [65.00 -> 87.75]  |  @superbwarfare:small_cannon_shell * 0.0740740741  [87.75 -> 6.50]
  - 主炮直击: @superbwarfare:cannon_shell + 13  [200.00 -> 213.00]  |  All - 13  [213.00 -> 200.00]  |  All * 0.2  [200.00 -> 40.00]  |  @superbwarfare:cannon_shell * 5  [40.00 -> 200.00]  |  superbwarfare:projectile_hit * 1.35  [200.00 -> 270.00]  |  @superbwarfare:cannon_shell * 0.3703703704  [270.00 -> 100.00]
  - 炮弹爆炸: @superbwarfare:small_cannon_shell + 13  [30.00 -> 43.00]  |  All - 13  [43.00 -> 30.00]  |  All * 0.2  [30.00 -> 6.00]  |  @superbwarfare:small_cannon_shell * 5  [6.00 -> 30.00]  |  superbwarfare:custom_explosion * 0.54  [30.00 -> 16.20]  |  @superbwarfare:small_cannon_shell * 0.0740740741  [16.20 -> 1.20]

### dragonrise_reforge:m1128  (HP 200)
  - 机炮直击: @superbwarfare:small_cannon_shell + 13  [65.00 -> 78.00]  |  All - 13  [78.00 -> 65.00]  |  All * 0.2  [65.00 -> 13.00]  |  @superbwarfare:small_cannon_shell * 5  [13.00 -> 65.00]  |  superbwarfare:projectile_hit * 1.35  [65.00 -> 87.75]  |  @superbwarfare:small_cannon_shell * 0.0740740741  [87.75 -> 6.50]
  - 主炮直击: @superbwarfare:cannon_shell + 13  [200.00 -> 213.00]  |  All - 13  [213.00 -> 200.00]  |  All * 0.2  [200.00 -> 40.00]  |  @superbwarfare:cannon_shell * 5  [40.00 -> 200.00]  |  superbwarfare:projectile_hit * 1.35  [200.00 -> 270.00]  |  @superbwarfare:cannon_shell * 0.3703703704  [270.00 -> 100.00]
  - 炮弹爆炸: @superbwarfare:small_cannon_shell + 13  [30.00 -> 43.00]  |  All - 13  [43.00 -> 30.00]  |  All * 0.2  [30.00 -> 6.00]  |  @superbwarfare:small_cannon_shell * 5  [6.00 -> 30.00]  |  superbwarfare:custom_explosion * 0.54  [30.00 -> 16.20]  |  @superbwarfare:small_cannon_shell * 0.0740740741  [16.20 -> 1.20]

### dragonrise_reforge:m113  (HP 200)
  - 机炮直击: @superbwarfare:small_cannon_shell + 13  [65.00 -> 78.00]  |  All - 13  [78.00 -> 65.00]  |  All * 0.2  [65.00 -> 13.00]  |  @superbwarfare:small_cannon_shell * 5  [13.00 -> 65.00]  |  superbwarfare:projectile_hit * 1.35  [65.00 -> 87.75]  |  @superbwarfare:small_cannon_shell * 0.0740740741  [87.75 -> 6.50]
  - 主炮直击: @superbwarfare:cannon_shell + 13  [200.00 -> 213.00]  |  All - 13  [213.00 -> 200.00]  |  All * 0.2  [200.00 -> 40.00]  |  @superbwarfare:cannon_shell * 5  [40.00 -> 200.00]  |  superbwarfare:projectile_hit * 1.35  [200.00 -> 270.00]  |  @superbwarfare:cannon_shell * 0.3703703704  [270.00 -> 100.00]
  - 炮弹爆炸: @superbwarfare:small_cannon_shell + 13  [30.00 -> 43.00]  |  All - 13  [43.00 -> 30.00]  |  All * 0.2  [30.00 -> 6.00]  |  @superbwarfare:small_cannon_shell * 5  [6.00 -> 30.00]  |  superbwarfare:custom_explosion * 0.54  [30.00 -> 16.20]  |  @superbwarfare:small_cannon_shell * 0.0740740741  [16.20 -> 1.20]

### dragonrise_reforge:m1296  (HP 200)
  - 机炮直击: @superbwarfare:small_cannon_shell + 13  [65.00 -> 78.00]  |  All - 13  [78.00 -> 65.00]  |  All * 0.2  [65.00 -> 13.00]  |  @superbwarfare:small_cannon_shell * 5  [13.00 -> 65.00]  |  superbwarfare:projectile_hit * 1.35  [65.00 -> 87.75]  |  @superbwarfare:small_cannon_shell * 0.0740740741  [87.75 -> 6.50]
  - 主炮直击: @superbwarfare:cannon_shell + 13  [200.00 -> 213.00]  |  All - 13  [213.00 -> 200.00]  |  All * 0.2  [200.00 -> 40.00]  |  @superbwarfare:cannon_shell * 5  [40.00 -> 200.00]  |  superbwarfare:projectile_hit * 1.35  [200.00 -> 270.00]  |  @superbwarfare:cannon_shell * 0.3703703704  [270.00 -> 100.00]
  - 炮弹爆炸: @superbwarfare:small_cannon_shell + 13  [30.00 -> 43.00]  |  All - 13  [43.00 -> 30.00]  |  All * 0.2  [30.00 -> 6.00]  |  @superbwarfare:small_cannon_shell * 5  [6.00 -> 30.00]  |  superbwarfare:custom_explosion * 0.54  [30.00 -> 16.20]  |  @superbwarfare:small_cannon_shell * 0.0740740741  [16.20 -> 1.20]

### dragonrise_reforge:m1a2sepv1  (HP 600)
  - 机炮直击: @superbwarfare:small_cannon_shell + 20  [65.00 -> 85.00]  |  All - 20  [85.00 -> 65.00]  |  All * 0.2  [65.00 -> 13.00]  |  @superbwarfare:small_cannon_shell * 5  [13.00 -> 65.00]  |  superbwarfare:projectile_hit * 1.3  [65.00 -> 84.50]  |  @superbwarfare:small_cannon_shell * 0.0769230769  [84.50 -> 6.50]
  - 主炮直击: @superbwarfare:cannon_shell + 20  [200.00 -> 220.00]  |  All - 20  [220.00 -> 200.00]  |  All * 0.2  [200.00 -> 40.00]  |  @superbwarfare:cannon_shell * 5  [40.00 -> 200.00]  |  superbwarfare:projectile_hit * 1.3  [200.00 -> 260.00]  |  @superbwarfare:cannon_shell * 0.2307692308  [260.00 -> 60.00]
  - 炮弹爆炸: @superbwarfare:small_cannon_shell + 20  [30.00 -> 50.00]  |  All - 20  [50.00 -> 30.00]  |  All * 0.2  [30.00 -> 6.00]  |  @superbwarfare:small_cannon_shell * 5  [6.00 -> 30.00]  |  superbwarfare:custom_explosion * 0.0433333333  [30.00 -> 1.30]  |  @superbwarfare:small_cannon_shell * 0.0769230769  [1.30 -> 0.10]

### dragonrise_reforge:m1a2sepv2  (HP 400)
  - 机炮直击: @superbwarfare:small_cannon_shell + 20  [65.00 -> 85.00]  |  All - 20  [85.00 -> 65.00]  |  All * 0.2  [65.00 -> 13.00]  |  @superbwarfare:small_cannon_shell * 5  [13.00 -> 65.00]  |  superbwarfare:projectile_hit * 1.3  [65.00 -> 84.50]  |  @superbwarfare:small_cannon_shell * 0.0769230769  [84.50 -> 6.50]
  - 主炮直击: @superbwarfare:cannon_shell + 20  [200.00 -> 220.00]  |  All - 20  [220.00 -> 200.00]  |  All * 0.2  [200.00 -> 40.00]  |  @superbwarfare:cannon_shell * 5  [40.00 -> 200.00]  |  superbwarfare:projectile_hit * 1.3  [200.00 -> 260.00]  |  @superbwarfare:cannon_shell * 0.2307692308  [260.00 -> 60.00]
  - 炮弹爆炸: @superbwarfare:small_cannon_shell + 20  [30.00 -> 50.00]  |  All - 20  [50.00 -> 30.00]  |  All * 0.2  [30.00 -> 6.00]  |  @superbwarfare:small_cannon_shell * 5  [6.00 -> 30.00]  |  superbwarfare:custom_explosion * 0.0433333333  [30.00 -> 1.30]  |  @superbwarfare:small_cannon_shell * 0.0769230769  [1.30 -> 0.10]

### dragonrise_reforge:m3a3  (HP 250)
  - 机炮直击: @superbwarfare:small_cannon_shell + 13  [65.00 -> 78.00]  |  All - 13  [78.00 -> 65.00]  |  All * 0.2  [65.00 -> 13.00]  |  @superbwarfare:small_cannon_shell * 5  [13.00 -> 65.00]  |  superbwarfare:projectile_hit * 1.35  [65.00 -> 87.75]  |  @superbwarfare:small_cannon_shell * 0.0740740741  [87.75 -> 6.50]
  - 主炮直击: @superbwarfare:cannon_shell + 13  [200.00 -> 213.00]  |  All - 13  [213.00 -> 200.00]  |  All * 0.2  [200.00 -> 40.00]  |  @superbwarfare:cannon_shell * 5  [40.00 -> 200.00]  |  superbwarfare:projectile_hit * 1.35  [200.00 -> 270.00]  |  @superbwarfare:cannon_shell * 0.3703703704  [270.00 -> 100.00]
  - 炮弹爆炸: @superbwarfare:small_cannon_shell + 13  [30.00 -> 43.00]  |  All - 13  [43.00 -> 30.00]  |  All * 0.2  [30.00 -> 6.00]  |  @superbwarfare:small_cannon_shell * 5  [6.00 -> 30.00]  |  superbwarfare:custom_explosion * 0.54  [30.00 -> 16.20]  |  @superbwarfare:small_cannon_shell * 0.0740740741  [16.20 -> 1.20]

### dragonrise_reforge:mv3  (HP 100)
  - 机炮直击: superbwarfare:projectile_hit * 0.4  [65.00 -> 26.00]  |  @superbwarfare:small_cannon_shell * 0.9  [26.00 -> 23.40]
  - 主炮直击: superbwarfare:projectile_hit * 0.4  [200.00 -> 80.00]  |  @superbwarfare:cannon_shell * 0.8  [80.00 -> 64.00]
  - 炮弹爆炸: @superbwarfare:small_cannon_shell * 0.9  [30.00 -> 27.00]

### dragonrise_reforge:mv3_armed  (HP 250)
  - 机炮直击: superbwarfare:projectile_hit * 0.4  [65.00 -> 26.00]  |  @superbwarfare:small_cannon_shell * 0.9  [26.00 -> 23.40]
  - 主炮直击: superbwarfare:projectile_hit * 0.4  [200.00 -> 80.00]  |  @superbwarfare:cannon_shell * 0.8  [80.00 -> 64.00]
  - 炮弹爆炸: @superbwarfare:small_cannon_shell * 0.9  [30.00 -> 27.00]

### dragonrise_reforge:mv3_supply  (HP 250)
  - 机炮直击: superbwarfare:projectile_hit * 0.4  [65.00 -> 26.00]  |  @superbwarfare:small_cannon_shell * 0.9  [26.00 -> 23.40]
  - 主炮直击: superbwarfare:projectile_hit * 0.4  [200.00 -> 80.00]  |  @superbwarfare:cannon_shell * 0.8  [80.00 -> 64.00]
  - 炮弹爆炸: @superbwarfare:small_cannon_shell * 0.9  [30.00 -> 27.00]

### dragonrise_reforge:sx1  (HP 100)
  - 机炮直击: superbwarfare:projectile_hit * 0.8  [65.00 -> 52.00]  |  @superbwarfare:small_cannon_shell * 1.8  [52.00 -> 93.60]
  - 主炮直击: superbwarfare:projectile_hit * 0.8  [200.00 -> 160.00]  |  @superbwarfare:cannon_shell * 1.6  [160.00 -> 256.00]
  - 炮弹爆炸: @superbwarfare:small_cannon_shell * 1.8  [30.00 -> 54.00]

### dragonrise_reforge:sx1_a  (HP undefined)
  - 机炮直击: （无任何条目生效 → 100%）
  - 主炮直击: （无任何条目生效 → 100%）
  - 炮弹爆炸: （无任何条目生效 → 100%）

### dragonrise_reforge:t72b3  (HP 380)
  - 机炮直击: @superbwarfare:small_cannon_shell + 20  [65.00 -> 85.00]  |  All - 20  [85.00 -> 65.00]  |  All * 0.2  [65.00 -> 13.00]  |  @superbwarfare:small_cannon_shell * 5  [13.00 -> 65.00]  |  superbwarfare:projectile_hit * 1.3  [65.00 -> 84.50]  |  @superbwarfare:small_cannon_shell * 0.0769230769  [84.50 -> 6.50]
  - 主炮直击: @superbwarfare:cannon_shell + 20  [200.00 -> 220.00]  |  All - 20  [220.00 -> 200.00]  |  All * 0.2  [200.00 -> 40.00]  |  @superbwarfare:cannon_shell * 5  [40.00 -> 200.00]  |  superbwarfare:projectile_hit * 1.3  [200.00 -> 260.00]  |  @superbwarfare:cannon_shell * 0.2307692308  [260.00 -> 60.00]
  - 炮弹爆炸: @superbwarfare:small_cannon_shell + 20  [30.00 -> 50.00]  |  All - 20  [50.00 -> 30.00]  |  All * 0.2  [30.00 -> 6.00]  |  @superbwarfare:small_cannon_shell * 5  [6.00 -> 30.00]  |  superbwarfare:custom_explosion * 0.0433333333  [30.00 -> 1.30]  |  @superbwarfare:small_cannon_shell * 0.0769230769  [1.30 -> 0.10]

### dragonrise_reforge:t90mh  (HP 420)
  - 机炮直击: @superbwarfare:small_cannon_shell + 20  [65.00 -> 85.00]  |  All - 20  [85.00 -> 65.00]  |  All * 0.2  [65.00 -> 13.00]  |  @superbwarfare:small_cannon_shell * 5  [13.00 -> 65.00]  |  superbwarfare:projectile_hit * 1.3  [65.00 -> 84.50]  |  @superbwarfare:small_cannon_shell * 0.0769230769  [84.50 -> 6.50]
  - 主炮直击: @superbwarfare:cannon_shell + 20  [200.00 -> 220.00]  |  All - 20  [220.00 -> 200.00]  |  All * 0.2  [200.00 -> 40.00]  |  @superbwarfare:cannon_shell * 5  [40.00 -> 200.00]  |  superbwarfare:projectile_hit * 1.3  [200.00 -> 260.00]  |  @superbwarfare:cannon_shell * 0.2307692308  [260.00 -> 60.00]
  - 炮弹爆炸: @superbwarfare:small_cannon_shell + 20  [30.00 -> 50.00]  |  All - 20  [50.00 -> 30.00]  |  All * 0.2  [30.00 -> 6.00]  |  @superbwarfare:small_cannon_shell * 5  [6.00 -> 30.00]  |  superbwarfare:custom_explosion * 0.0433333333  [30.00 -> 1.30]  |  @superbwarfare:small_cannon_shell * 0.0769230769  [1.30 -> 0.10]

### dragonrise_reforge:ural4320  (HP 300)
  - 机炮直击: superbwarfare:projectile_hit * 0.4  [65.00 -> 26.00]  |  @superbwarfare:small_cannon_shell * 0.9  [26.00 -> 23.40]
  - 主炮直击: superbwarfare:projectile_hit * 0.4  [200.00 -> 80.00]  |  @superbwarfare:cannon_shell * 0.8  [80.00 -> 64.00]
  - 炮弹爆炸: @superbwarfare:small_cannon_shell * 0.9  [30.00 -> 27.00]

### dragonrise_reforge:ural4320_supply  (HP 300)
  - 机炮直击: superbwarfare:projectile_hit * 0.4  [65.00 -> 26.00]  |  @superbwarfare:small_cannon_shell * 0.9  [26.00 -> 23.40]
  - 主炮直击: superbwarfare:projectile_hit * 0.4  [200.00 -> 80.00]  |  @superbwarfare:cannon_shell * 0.8  [80.00 -> 64.00]
  - 炮弹爆炸: @superbwarfare:small_cannon_shell * 0.9  [30.00 -> 27.00]

### dragonrise_reforge:ural4320_zu23  (HP 300)
  - 机炮直击: superbwarfare:projectile_hit * 0.4  [65.00 -> 26.00]  |  @superbwarfare:small_cannon_shell * 0.9  [26.00 -> 23.40]
  - 主炮直击: superbwarfare:projectile_hit * 0.4  [200.00 -> 80.00]  |  @superbwarfare:cannon_shell * 0.8  [80.00 -> 64.00]
  - 炮弹爆炸: @superbwarfare:small_cannon_shell * 0.9  [30.00 -> 27.00]

### dragonrise_reforge:z20  (HP 300)
  - 机炮直击: superbwarfare:projectile_hit * 0.6  [65.00 -> 39.00]  |  @superbwarfare:small_cannon_shell * 0.95  [39.00 -> 37.05]
  - 主炮直击: superbwarfare:projectile_hit * 0.6  [200.00 -> 120.00]  |  @superbwarfare:cannon_shell * 1.5  [120.00 -> 180.00]
  - 炮弹爆炸: @superbwarfare:small_cannon_shell * 0.95  [30.00 -> 28.50]

### dragonrise_reforge:zbd04a  (HP 200)
  - 机炮直击: @superbwarfare:small_cannon_shell + 13  [65.00 -> 78.00]  |  All - 13  [78.00 -> 65.00]  |  All * 0.2  [65.00 -> 13.00]  |  @superbwarfare:small_cannon_shell * 5  [13.00 -> 65.00]  |  superbwarfare:projectile_hit * 1.35  [65.00 -> 87.75]  |  @superbwarfare:small_cannon_shell * 0.0740740741  [87.75 -> 6.50]
  - 主炮直击: @superbwarfare:cannon_shell + 13  [200.00 -> 213.00]  |  All - 13  [213.00 -> 200.00]  |  All * 0.2  [200.00 -> 40.00]  |  @superbwarfare:cannon_shell * 5  [40.00 -> 200.00]  |  superbwarfare:projectile_hit * 1.35  [200.00 -> 270.00]  |  @superbwarfare:cannon_shell * 0.3703703704  [270.00 -> 100.00]
  - 炮弹爆炸: @superbwarfare:small_cannon_shell + 13  [30.00 -> 43.00]  |  All - 13  [43.00 -> 30.00]  |  All * 0.2  [30.00 -> 6.00]  |  @superbwarfare:small_cannon_shell * 5  [6.00 -> 30.00]  |  superbwarfare:custom_explosion * 0.54  [30.00 -> 16.20]  |  @superbwarfare:small_cannon_shell * 0.0740740741  [16.20 -> 1.20]

### dragonrise_reforge:zbd05  (HP 200)
  - 机炮直击: @superbwarfare:small_cannon_shell + 13  [65.00 -> 78.00]  |  All - 13  [78.00 -> 65.00]  |  All * 0.2  [65.00 -> 13.00]  |  @superbwarfare:small_cannon_shell * 5  [13.00 -> 65.00]  |  superbwarfare:projectile_hit * 1.35  [65.00 -> 87.75]  |  @superbwarfare:small_cannon_shell * 0.0740740741  [87.75 -> 6.50]
  - 主炮直击: @superbwarfare:cannon_shell + 13  [200.00 -> 213.00]  |  All - 13  [213.00 -> 200.00]  |  All * 0.2  [200.00 -> 40.00]  |  @superbwarfare:cannon_shell * 5  [40.00 -> 200.00]  |  superbwarfare:projectile_hit * 1.35  [200.00 -> 270.00]  |  @superbwarfare:cannon_shell * 0.3703703704  [270.00 -> 100.00]
  - 炮弹爆炸: @superbwarfare:small_cannon_shell + 13  [30.00 -> 43.00]  |  All - 13  [43.00 -> 30.00]  |  All * 0.2  [30.00 -> 6.00]  |  @superbwarfare:small_cannon_shell * 5  [6.00 -> 30.00]  |  superbwarfare:custom_explosion * 0.54  [30.00 -> 16.20]  |  @superbwarfare:small_cannon_shell * 0.0740740741  [16.20 -> 1.20]

### dragonrise_reforge:zbl08  (HP 175)
  - 机炮直击: @superbwarfare:small_cannon_shell + 13  [65.00 -> 78.00]  |  All - 13  [78.00 -> 65.00]  |  All * 0.2  [65.00 -> 13.00]  |  @superbwarfare:small_cannon_shell * 5  [13.00 -> 65.00]  |  superbwarfare:projectile_hit * 1.35  [65.00 -> 87.75]  |  @superbwarfare:small_cannon_shell * 0.0740740741  [87.75 -> 6.50]
  - 主炮直击: @superbwarfare:cannon_shell + 13  [200.00 -> 213.00]  |  All - 13  [213.00 -> 200.00]  |  All * 0.2  [200.00 -> 40.00]  |  @superbwarfare:cannon_shell * 5  [40.00 -> 200.00]  |  superbwarfare:projectile_hit * 1.35  [200.00 -> 270.00]  |  @superbwarfare:cannon_shell * 0.3703703704  [270.00 -> 100.00]
  - 炮弹爆炸: @superbwarfare:small_cannon_shell + 13  [30.00 -> 43.00]  |  All - 13  [43.00 -> 30.00]  |  All * 0.2  [30.00 -> 6.00]  |  @superbwarfare:small_cannon_shell * 5  [6.00 -> 30.00]  |  superbwarfare:custom_explosion * 0.54  [30.00 -> 16.20]  |  @superbwarfare:small_cannon_shell * 0.0740740741  [16.20 -> 1.20]

### dragonrise_reforge:zlt11  (HP 175)
  - 机炮直击: @superbwarfare:small_cannon_shell + 13  [65.00 -> 78.00]  |  All - 13  [78.00 -> 65.00]  |  All * 0.2  [65.00 -> 13.00]  |  @superbwarfare:small_cannon_shell * 5  [13.00 -> 65.00]  |  superbwarfare:projectile_hit * 1.35  [65.00 -> 87.75]  |  @superbwarfare:small_cannon_shell * 0.0740740741  [87.75 -> 6.50]
  - 主炮直击: @superbwarfare:cannon_shell + 13  [200.00 -> 213.00]  |  All - 13  [213.00 -> 200.00]  |  All * 0.2  [200.00 -> 40.00]  |  @superbwarfare:cannon_shell * 5  [40.00 -> 200.00]  |  superbwarfare:projectile_hit * 1.35  [200.00 -> 270.00]  |  @superbwarfare:cannon_shell * 0.3703703704  [270.00 -> 100.00]
  - 炮弹爆炸: @superbwarfare:small_cannon_shell + 13  [30.00 -> 43.00]  |  All - 13  [43.00 -> 30.00]  |  All * 0.2  [30.00 -> 6.00]  |  @superbwarfare:small_cannon_shell * 5  [6.00 -> 30.00]  |  superbwarfare:custom_explosion * 0.54  [30.00 -> 16.20]  |  @superbwarfare:small_cannon_shell * 0.0740740741  [16.20 -> 1.20]

### dragonrise_reforge:zsl10  (HP 175)
  - 机炮直击: @superbwarfare:small_cannon_shell + 13  [65.00 -> 78.00]  |  All - 13  [78.00 -> 65.00]  |  All * 0.2  [65.00 -> 13.00]  |  @superbwarfare:small_cannon_shell * 5  [13.00 -> 65.00]  |  superbwarfare:projectile_hit * 1.35  [65.00 -> 87.75]  |  @superbwarfare:small_cannon_shell * 0.0740740741  [87.75 -> 6.50]
  - 主炮直击: @superbwarfare:cannon_shell + 13  [200.00 -> 213.00]  |  All - 13  [213.00 -> 200.00]  |  All * 0.2  [200.00 -> 40.00]  |  @superbwarfare:cannon_shell * 5  [40.00 -> 200.00]  |  superbwarfare:projectile_hit * 1.35  [200.00 -> 270.00]  |  @superbwarfare:cannon_shell * 0.3703703704  [270.00 -> 100.00]
  - 炮弹爆炸: @superbwarfare:small_cannon_shell + 13  [30.00 -> 43.00]  |  All - 13  [43.00 -> 30.00]  |  All * 0.2  [30.00 -> 6.00]  |  @superbwarfare:small_cannon_shell * 5  [6.00 -> 30.00]  |  superbwarfare:custom_explosion * 0.54  [30.00 -> 16.20]  |  @superbwarfare:small_cannon_shell * 0.0740740741  [16.20 -> 1.20]

### dragonrise_reforge:ztd05  (HP 200)
  - 机炮直击: @superbwarfare:small_cannon_shell + 13  [65.00 -> 78.00]  |  All - 13  [78.00 -> 65.00]  |  All * 0.2  [65.00 -> 13.00]  |  @superbwarfare:small_cannon_shell * 5  [13.00 -> 65.00]  |  superbwarfare:projectile_hit * 1.35  [65.00 -> 87.75]  |  @superbwarfare:small_cannon_shell * 0.0740740741  [87.75 -> 6.50]
  - 主炮直击: @superbwarfare:cannon_shell + 13  [200.00 -> 213.00]  |  All - 13  [213.00 -> 200.00]  |  All * 0.2  [200.00 -> 40.00]  |  @superbwarfare:cannon_shell * 5  [40.00 -> 200.00]  |  superbwarfare:projectile_hit * 1.35  [200.00 -> 270.00]  |  @superbwarfare:cannon_shell * 0.3703703704  [270.00 -> 100.00]
  - 炮弹爆炸: @superbwarfare:small_cannon_shell + 13  [30.00 -> 43.00]  |  All - 13  [43.00 -> 30.00]  |  All * 0.2  [30.00 -> 6.00]  |  @superbwarfare:small_cannon_shell * 5  [6.00 -> 30.00]  |  superbwarfare:custom_explosion * 0.54  [30.00 -> 16.20]  |  @superbwarfare:small_cannon_shell * 0.0740740741  [16.20 -> 1.20]

### dragonrise_reforge:ztz96a  (HP 380)
  - 机炮直击: @superbwarfare:small_cannon_shell + 20  [65.00 -> 85.00]  |  All - 20  [85.00 -> 65.00]  |  All * 0.2  [65.00 -> 13.00]  |  @superbwarfare:small_cannon_shell * 5  [13.00 -> 65.00]  |  superbwarfare:projectile_hit * 1.3  [65.00 -> 84.50]  |  @superbwarfare:small_cannon_shell * 0.0769230769  [84.50 -> 6.50]
  - 主炮直击: @superbwarfare:cannon_shell + 20  [200.00 -> 220.00]  |  All - 20  [220.00 -> 200.00]  |  All * 0.2  [200.00 -> 40.00]  |  @superbwarfare:cannon_shell * 5  [40.00 -> 200.00]  |  superbwarfare:projectile_hit * 1.3  [200.00 -> 260.00]  |  @superbwarfare:cannon_shell * 0.2307692308  [260.00 -> 60.00]
  - 炮弹爆炸: @superbwarfare:small_cannon_shell + 20  [30.00 -> 50.00]  |  All - 20  [50.00 -> 30.00]  |  All * 0.2  [30.00 -> 6.00]  |  @superbwarfare:small_cannon_shell * 5  [6.00 -> 30.00]  |  superbwarfare:custom_explosion * 0.0433333333  [30.00 -> 1.30]  |  @superbwarfare:small_cannon_shell * 0.0769230769  [1.30 -> 0.10]

### dragonrise_reforge:ztz99a  (HP 400)
  - 机炮直击: @superbwarfare:small_cannon_shell + 20  [65.00 -> 85.00]  |  All - 20  [85.00 -> 65.00]  |  All * 0.2  [65.00 -> 13.00]  |  @superbwarfare:small_cannon_shell * 5  [13.00 -> 65.00]  |  superbwarfare:projectile_hit * 1.3  [65.00 -> 84.50]  |  @superbwarfare:small_cannon_shell * 0.0769230769  [84.50 -> 6.50]
  - 主炮直击: @superbwarfare:cannon_shell + 20  [200.00 -> 220.00]  |  All - 20  [220.00 -> 200.00]  |  All * 0.2  [200.00 -> 40.00]  |  @superbwarfare:cannon_shell * 5  [40.00 -> 200.00]  |  superbwarfare:projectile_hit * 1.3  [200.00 -> 260.00]  |  @superbwarfare:cannon_shell * 0.2307692308  [260.00 -> 60.00]
  - 炮弹爆炸: @superbwarfare:small_cannon_shell + 20  [30.00 -> 50.00]  |  All - 20  [50.00 -> 30.00]  |  All * 0.2  [30.00 -> 6.00]  |  @superbwarfare:small_cannon_shell * 5  [6.00 -> 30.00]  |  superbwarfare:custom_explosion * 0.0433333333  [30.00 -> 1.30]  |  @superbwarfare:small_cannon_shell * 0.0769230769  [1.30 -> 0.10]

### fcp:bmp1am  (HP 270)
  - 机炮直击: @superbwarfare:small_cannon_shell + 13  [65.00 -> 78.00]  |  All - 13  [78.00 -> 65.00]  |  All * 0.2  [65.00 -> 13.00]  |  @superbwarfare:small_cannon_shell * 5  [13.00 -> 65.00]  |  superbwarfare:projectile_hit * 1.35  [65.00 -> 87.75]  |  @superbwarfare:small_cannon_shell * 0.0740740741  [87.75 -> 6.50]
  - 主炮直击: @superbwarfare:cannon_shell + 13  [200.00 -> 213.00]  |  All - 13  [213.00 -> 200.00]  |  All * 0.2  [200.00 -> 40.00]  |  @superbwarfare:cannon_shell * 5  [40.00 -> 200.00]  |  superbwarfare:projectile_hit * 1.35  [200.00 -> 270.00]  |  @superbwarfare:cannon_shell * 0.3703703704  [270.00 -> 100.00]
  - 炮弹爆炸: @superbwarfare:small_cannon_shell + 13  [30.00 -> 43.00]  |  All - 13  [43.00 -> 30.00]  |  All * 0.2  [30.00 -> 6.00]  |  @superbwarfare:small_cannon_shell * 5  [6.00 -> 30.00]  |  superbwarfare:custom_explosion * 0.54  [30.00 -> 16.20]  |  @superbwarfare:small_cannon_shell * 0.0740740741  [16.20 -> 1.20]

### fcp:bmp2  (HP 300)
  - 机炮直击: @superbwarfare:small_cannon_shell + 13  [65.00 -> 78.00]  |  All - 13  [78.00 -> 65.00]  |  All * 0.2  [65.00 -> 13.00]  |  @superbwarfare:small_cannon_shell * 5  [13.00 -> 65.00]  |  superbwarfare:projectile_hit * 1.35  [65.00 -> 87.75]  |  @superbwarfare:small_cannon_shell * 0.0740740741  [87.75 -> 6.50]
  - 主炮直击: @superbwarfare:cannon_shell + 13  [200.00 -> 213.00]  |  All - 13  [213.00 -> 200.00]  |  All * 0.2  [200.00 -> 40.00]  |  @superbwarfare:cannon_shell * 5  [40.00 -> 200.00]  |  superbwarfare:projectile_hit * 1.35  [200.00 -> 270.00]  |  @superbwarfare:cannon_shell * 0.3703703704  [270.00 -> 100.00]
  - 炮弹爆炸: @superbwarfare:small_cannon_shell + 13  [30.00 -> 43.00]  |  All - 13  [43.00 -> 30.00]  |  All * 0.2  [30.00 -> 6.00]  |  @superbwarfare:small_cannon_shell * 5  [6.00 -> 30.00]  |  superbwarfare:custom_explosion * 0.54  [30.00 -> 16.20]  |  @superbwarfare:small_cannon_shell * 0.0740740741  [16.20 -> 1.20]

### fcp:bmp2d  (HP 200)
  - 机炮直击: @superbwarfare:small_cannon_shell + 13  [65.00 -> 78.00]  |  All - 13  [78.00 -> 65.00]  |  All * 0.2  [65.00 -> 13.00]  |  @superbwarfare:small_cannon_shell * 5  [13.00 -> 65.00]  |  superbwarfare:projectile_hit * 1.35  [65.00 -> 87.75]  |  @superbwarfare:small_cannon_shell * 0.0740740741  [87.75 -> 6.50]
  - 主炮直击: @superbwarfare:cannon_shell + 13  [200.00 -> 213.00]  |  All - 13  [213.00 -> 200.00]  |  All * 0.2  [200.00 -> 40.00]  |  @superbwarfare:cannon_shell * 5  [40.00 -> 200.00]  |  superbwarfare:projectile_hit * 1.35  [200.00 -> 270.00]  |  @superbwarfare:cannon_shell * 0.3703703704  [270.00 -> 100.00]
  - 炮弹爆炸: @superbwarfare:small_cannon_shell + 13  [30.00 -> 43.00]  |  All - 13  [43.00 -> 30.00]  |  All * 0.2  [30.00 -> 6.00]  |  @superbwarfare:small_cannon_shell * 5  [6.00 -> 30.00]  |  superbwarfare:custom_explosion * 0.54  [30.00 -> 16.20]  |  @superbwarfare:small_cannon_shell * 0.0740740741  [16.20 -> 1.20]

### fcp:bmp2m  (HP 300)
  - 机炮直击: @superbwarfare:small_cannon_shell + 13  [65.00 -> 78.00]  |  All - 13  [78.00 -> 65.00]  |  All * 0.2  [65.00 -> 13.00]  |  @superbwarfare:small_cannon_shell * 5  [13.00 -> 65.00]  |  superbwarfare:projectile_hit * 1.35  [65.00 -> 87.75]  |  @superbwarfare:small_cannon_shell * 0.0740740741  [87.75 -> 6.50]
  - 主炮直击: @superbwarfare:cannon_shell + 13  [200.00 -> 213.00]  |  All - 13  [213.00 -> 200.00]  |  All * 0.2  [200.00 -> 40.00]  |  @superbwarfare:cannon_shell * 5  [40.00 -> 200.00]  |  superbwarfare:projectile_hit * 1.35  [200.00 -> 270.00]  |  @superbwarfare:cannon_shell * 0.3703703704  [270.00 -> 100.00]
  - 炮弹爆炸: @superbwarfare:small_cannon_shell + 13  [30.00 -> 43.00]  |  All - 13  [43.00 -> 30.00]  |  All * 0.2  [30.00 -> 6.00]  |  @superbwarfare:small_cannon_shell * 5  [6.00 -> 30.00]  |  superbwarfare:custom_explosion * 0.54  [30.00 -> 16.20]  |  @superbwarfare:small_cannon_shell * 0.0740740741  [16.20 -> 1.20]

### fcp:btr80  (HP 175)
  - 机炮直击: @superbwarfare:small_cannon_shell + 13  [65.00 -> 78.00]  |  All - 13  [78.00 -> 65.00]  |  All * 0.2  [65.00 -> 13.00]  |  @superbwarfare:small_cannon_shell * 5  [13.00 -> 65.00]  |  superbwarfare:projectile_hit * 1.35  [65.00 -> 87.75]  |  @superbwarfare:small_cannon_shell * 0.0740740741  [87.75 -> 6.50]
  - 主炮直击: @superbwarfare:cannon_shell + 13  [200.00 -> 213.00]  |  All - 13  [213.00 -> 200.00]  |  All * 0.2  [200.00 -> 40.00]  |  @superbwarfare:cannon_shell * 5  [40.00 -> 200.00]  |  superbwarfare:projectile_hit * 1.35  [200.00 -> 270.00]  |  @superbwarfare:cannon_shell * 0.3703703704  [270.00 -> 100.00]
  - 炮弹爆炸: @superbwarfare:small_cannon_shell + 13  [30.00 -> 43.00]  |  All - 13  [43.00 -> 30.00]  |  All * 0.2  [30.00 -> 6.00]  |  @superbwarfare:small_cannon_shell * 5  [6.00 -> 30.00]  |  superbwarfare:custom_explosion * 0.54  [30.00 -> 16.20]  |  @superbwarfare:small_cannon_shell * 0.0740740741  [16.20 -> 1.20]

### fcp:btr82  (HP 175)
  - 机炮直击: @superbwarfare:small_cannon_shell + 13  [65.00 -> 78.00]  |  All - 13  [78.00 -> 65.00]  |  All * 0.2  [65.00 -> 13.00]  |  @superbwarfare:small_cannon_shell * 5  [13.00 -> 65.00]  |  superbwarfare:projectile_hit * 1.35  [65.00 -> 87.75]  |  @superbwarfare:small_cannon_shell * 0.0740740741  [87.75 -> 6.50]
  - 主炮直击: @superbwarfare:cannon_shell + 13  [200.00 -> 213.00]  |  All - 13  [213.00 -> 200.00]  |  All * 0.2  [200.00 -> 40.00]  |  @superbwarfare:cannon_shell * 5  [40.00 -> 200.00]  |  superbwarfare:projectile_hit * 1.35  [200.00 -> 270.00]  |  @superbwarfare:cannon_shell * 0.3703703704  [270.00 -> 100.00]
  - 炮弹爆炸: @superbwarfare:small_cannon_shell + 13  [30.00 -> 43.00]  |  All - 13  [43.00 -> 30.00]  |  All * 0.2  [30.00 -> 6.00]  |  @superbwarfare:small_cannon_shell * 5  [6.00 -> 30.00]  |  superbwarfare:custom_explosion * 0.54  [30.00 -> 16.20]  |  @superbwarfare:small_cannon_shell * 0.0740740741  [16.20 -> 1.20]

### fcp:gaz_tigr_gl  (HP 175)
  - 机炮直击: @superbwarfare:small_cannon_shell + 13  [65.00 -> 78.00]  |  All - 13  [78.00 -> 65.00]  |  All * 0.2  [65.00 -> 13.00]  |  @superbwarfare:small_cannon_shell * 5  [13.00 -> 65.00]  |  superbwarfare:projectile_hit * 1.35  [65.00 -> 87.75]  |  @superbwarfare:small_cannon_shell * 0.0740740741  [87.75 -> 6.50]
  - 主炮直击: @superbwarfare:cannon_shell + 13  [200.00 -> 213.00]  |  All - 13  [213.00 -> 200.00]  |  All * 0.2  [200.00 -> 40.00]  |  @superbwarfare:cannon_shell * 5  [40.00 -> 200.00]  |  superbwarfare:projectile_hit * 1.35  [200.00 -> 270.00]  |  @superbwarfare:cannon_shell * 0.3703703704  [270.00 -> 100.00]
  - 炮弹爆炸: @superbwarfare:small_cannon_shell + 13  [30.00 -> 43.00]  |  All - 13  [43.00 -> 30.00]  |  All * 0.2  [30.00 -> 6.00]  |  @superbwarfare:small_cannon_shell * 5  [6.00 -> 30.00]  |  superbwarfare:custom_explosion * 0.54  [30.00 -> 16.20]  |  @superbwarfare:small_cannon_shell * 0.0740740741  [16.20 -> 1.20]

### fcp:gaz_tigr_mg  (HP 175)
  - 机炮直击: @superbwarfare:small_cannon_shell + 13  [65.00 -> 78.00]  |  All - 13  [78.00 -> 65.00]  |  All * 0.2  [65.00 -> 13.00]  |  @superbwarfare:small_cannon_shell * 5  [13.00 -> 65.00]  |  superbwarfare:projectile_hit * 1.35  [65.00 -> 87.75]  |  @superbwarfare:small_cannon_shell * 0.0740740741  [87.75 -> 6.50]
  - 主炮直击: @superbwarfare:cannon_shell + 13  [200.00 -> 213.00]  |  All - 13  [213.00 -> 200.00]  |  All * 0.2  [200.00 -> 40.00]  |  @superbwarfare:cannon_shell * 5  [40.00 -> 200.00]  |  superbwarfare:projectile_hit * 1.35  [200.00 -> 270.00]  |  @superbwarfare:cannon_shell * 0.3703703704  [270.00 -> 100.00]
  - 炮弹爆炸: @superbwarfare:small_cannon_shell + 13  [30.00 -> 43.00]  |  All - 13  [43.00 -> 30.00]  |  All * 0.2  [30.00 -> 6.00]  |  @superbwarfare:small_cannon_shell * 5  [6.00 -> 30.00]  |  superbwarfare:custom_explosion * 0.54  [30.00 -> 16.20]  |  @superbwarfare:small_cannon_shell * 0.0740740741  [16.20 -> 1.20]

### fcp:gaz_tigr_rws  (HP 175)
  - 机炮直击: @superbwarfare:small_cannon_shell + 13  [65.00 -> 78.00]  |  All - 13  [78.00 -> 65.00]  |  All * 0.2  [65.00 -> 13.00]  |  @superbwarfare:small_cannon_shell * 5  [13.00 -> 65.00]  |  superbwarfare:projectile_hit * 1.35  [65.00 -> 87.75]  |  @superbwarfare:small_cannon_shell * 0.0740740741  [87.75 -> 6.50]
  - 主炮直击: @superbwarfare:cannon_shell + 13  [200.00 -> 213.00]  |  All - 13  [213.00 -> 200.00]  |  All * 0.2  [200.00 -> 40.00]  |  @superbwarfare:cannon_shell * 5  [40.00 -> 200.00]  |  superbwarfare:projectile_hit * 1.35  [200.00 -> 270.00]  |  @superbwarfare:cannon_shell * 0.3703703704  [270.00 -> 100.00]
  - 炮弹爆炸: @superbwarfare:small_cannon_shell + 13  [30.00 -> 43.00]  |  All - 13  [43.00 -> 30.00]  |  All * 0.2  [30.00 -> 6.00]  |  @superbwarfare:small_cannon_shell * 5  [6.00 -> 30.00]  |  superbwarfare:custom_explosion * 0.54  [30.00 -> 16.20]  |  @superbwarfare:small_cannon_shell * 0.0740740741  [16.20 -> 1.20]

### fcp:matv  (HP 175)
  - 机炮直击: @superbwarfare:small_cannon_shell + 13  [65.00 -> 78.00]  |  All - 13  [78.00 -> 65.00]  |  All * 0.2  [65.00 -> 13.00]  |  @superbwarfare:small_cannon_shell * 5  [13.00 -> 65.00]  |  superbwarfare:projectile_hit * 1.35  [65.00 -> 87.75]  |  @superbwarfare:small_cannon_shell * 0.0740740741  [87.75 -> 6.50]
  - 主炮直击: @superbwarfare:cannon_shell + 13  [200.00 -> 213.00]  |  All - 13  [213.00 -> 200.00]  |  All * 0.2  [200.00 -> 40.00]  |  @superbwarfare:cannon_shell * 5  [40.00 -> 200.00]  |  superbwarfare:projectile_hit * 1.35  [200.00 -> 270.00]  |  @superbwarfare:cannon_shell * 0.3703703704  [270.00 -> 100.00]
  - 炮弹爆炸: @superbwarfare:small_cannon_shell + 13  [30.00 -> 43.00]  |  All - 13  [43.00 -> 30.00]  |  All * 0.2  [30.00 -> 6.00]  |  @superbwarfare:small_cannon_shell * 5  [6.00 -> 30.00]  |  superbwarfare:custom_explosion * 0.54  [30.00 -> 16.20]  |  @superbwarfare:small_cannon_shell * 0.0740740741  [16.20 -> 1.20]

### fcp:matv_9in1  (HP 175)
  - 机炮直击: @superbwarfare:small_cannon_shell + 13  [65.00 -> 78.00]  |  All - 13  [78.00 -> 65.00]  |  All * 0.2  [65.00 -> 13.00]  |  @superbwarfare:small_cannon_shell * 5  [13.00 -> 65.00]  |  superbwarfare:projectile_hit * 1.35  [65.00 -> 87.75]  |  @superbwarfare:small_cannon_shell * 0.0740740741  [87.75 -> 6.50]
  - 主炮直击: @superbwarfare:cannon_shell + 13  [200.00 -> 213.00]  |  All - 13  [213.00 -> 200.00]  |  All * 0.2  [200.00 -> 40.00]  |  @superbwarfare:cannon_shell * 5  [40.00 -> 200.00]  |  superbwarfare:projectile_hit * 1.35  [200.00 -> 270.00]  |  @superbwarfare:cannon_shell * 0.3703703704  [270.00 -> 100.00]
  - 炮弹爆炸: @superbwarfare:small_cannon_shell + 13  [30.00 -> 43.00]  |  All - 13  [43.00 -> 30.00]  |  All * 0.2  [30.00 -> 6.00]  |  @superbwarfare:small_cannon_shell * 5  [6.00 -> 30.00]  |  superbwarfare:custom_explosion * 0.54  [30.00 -> 16.20]  |  @superbwarfare:small_cannon_shell * 0.0740740741  [16.20 -> 1.20]

### fcp:matv_crow  (HP 175)
  - 机炮直击: @superbwarfare:small_cannon_shell + 13  [65.00 -> 78.00]  |  All - 13  [78.00 -> 65.00]  |  All * 0.2  [65.00 -> 13.00]  |  @superbwarfare:small_cannon_shell * 5  [13.00 -> 65.00]  |  superbwarfare:projectile_hit * 1.35  [65.00 -> 87.75]  |  @superbwarfare:small_cannon_shell * 0.0740740741  [87.75 -> 6.50]
  - 主炮直击: @superbwarfare:cannon_shell + 13  [200.00 -> 213.00]  |  All - 13  [213.00 -> 200.00]  |  All * 0.2  [200.00 -> 40.00]  |  @superbwarfare:cannon_shell * 5  [40.00 -> 200.00]  |  superbwarfare:projectile_hit * 1.35  [200.00 -> 270.00]  |  @superbwarfare:cannon_shell * 0.3703703704  [270.00 -> 100.00]
  - 炮弹爆炸: @superbwarfare:small_cannon_shell + 13  [30.00 -> 43.00]  |  All - 13  [43.00 -> 30.00]  |  All * 0.2  [30.00 -> 6.00]  |  @superbwarfare:small_cannon_shell * 5  [6.00 -> 30.00]  |  superbwarfare:custom_explosion * 0.54  [30.00 -> 16.20]  |  @superbwarfare:small_cannon_shell * 0.0740740741  [16.20 -> 1.20]

### fcp:matv_tow  (HP 175)
  - 机炮直击: @superbwarfare:small_cannon_shell + 13  [65.00 -> 78.00]  |  All - 13  [78.00 -> 65.00]  |  All * 0.2  [65.00 -> 13.00]  |  @superbwarfare:small_cannon_shell * 5  [13.00 -> 65.00]  |  superbwarfare:projectile_hit * 1.35  [65.00 -> 87.75]  |  @superbwarfare:small_cannon_shell * 0.0740740741  [87.75 -> 6.50]
  - 主炮直击: @superbwarfare:cannon_shell + 13  [200.00 -> 213.00]  |  All - 13  [213.00 -> 200.00]  |  All * 0.2  [200.00 -> 40.00]  |  @superbwarfare:cannon_shell * 5  [40.00 -> 200.00]  |  superbwarfare:projectile_hit * 1.35  [200.00 -> 270.00]  |  @superbwarfare:cannon_shell * 0.3703703704  [270.00 -> 100.00]
  - 炮弹爆炸: @superbwarfare:small_cannon_shell + 13  [30.00 -> 43.00]  |  All - 13  [43.00 -> 30.00]  |  All * 0.2  [30.00 -> 6.00]  |  @superbwarfare:small_cannon_shell * 5  [6.00 -> 30.00]  |  superbwarfare:custom_explosion * 0.54  [30.00 -> 16.20]  |  @superbwarfare:small_cannon_shell * 0.0740740741  [16.20 -> 1.20]

### fcp:stryker_dragoon  (HP 200)
  - 机炮直击: @superbwarfare:small_cannon_shell + 13  [65.00 -> 78.00]  |  All - 13  [78.00 -> 65.00]  |  All * 0.2  [65.00 -> 13.00]  |  @superbwarfare:small_cannon_shell * 5  [13.00 -> 65.00]  |  superbwarfare:projectile_hit * 1.35  [65.00 -> 87.75]  |  @superbwarfare:small_cannon_shell * 0.0740740741  [87.75 -> 6.50]
  - 主炮直击: @superbwarfare:cannon_shell + 13  [200.00 -> 213.00]  |  All - 13  [213.00 -> 200.00]  |  All * 0.2  [200.00 -> 40.00]  |  @superbwarfare:cannon_shell * 5  [40.00 -> 200.00]  |  superbwarfare:projectile_hit * 1.35  [200.00 -> 270.00]  |  @superbwarfare:cannon_shell * 0.3703703704  [270.00 -> 100.00]
  - 炮弹爆炸: @superbwarfare:small_cannon_shell + 13  [30.00 -> 43.00]  |  All - 13  [43.00 -> 30.00]  |  All * 0.2  [30.00 -> 6.00]  |  @superbwarfare:small_cannon_shell * 5  [6.00 -> 30.00]  |  superbwarfare:custom_explosion * 0.54  [30.00 -> 16.20]  |  @superbwarfare:small_cannon_shell * 0.0740740741  [16.20 -> 1.20]

### fcp:stryker_m2  (HP 200)
  - 机炮直击: @superbwarfare:small_cannon_shell + 13  [65.00 -> 78.00]  |  All - 13  [78.00 -> 65.00]  |  All * 0.2  [65.00 -> 13.00]  |  @superbwarfare:small_cannon_shell * 5  [13.00 -> 65.00]  |  superbwarfare:projectile_hit * 1.35  [65.00 -> 87.75]  |  @superbwarfare:small_cannon_shell * 0.0740740741  [87.75 -> 6.50]
  - 主炮直击: @superbwarfare:cannon_shell + 13  [200.00 -> 213.00]  |  All - 13  [213.00 -> 200.00]  |  All * 0.2  [200.00 -> 40.00]  |  @superbwarfare:cannon_shell * 5  [40.00 -> 200.00]  |  superbwarfare:projectile_hit * 1.35  [200.00 -> 270.00]  |  @superbwarfare:cannon_shell * 0.3703703704  [270.00 -> 100.00]
  - 炮弹爆炸: @superbwarfare:small_cannon_shell + 13  [30.00 -> 43.00]  |  All - 13  [43.00 -> 30.00]  |  All * 0.2  [30.00 -> 6.00]  |  @superbwarfare:small_cannon_shell * 5  [6.00 -> 30.00]  |  superbwarfare:custom_explosion * 0.54  [30.00 -> 16.20]  |  @superbwarfare:small_cannon_shell * 0.0740740741  [16.20 -> 1.20]

### fcp:stryker_mgs  (HP 200)
  - 机炮直击: @superbwarfare:small_cannon_shell + 13  [65.00 -> 78.00]  |  All - 13  [78.00 -> 65.00]  |  All * 0.2  [65.00 -> 13.00]  |  @superbwarfare:small_cannon_shell * 5  [13.00 -> 65.00]  |  superbwarfare:projectile_hit * 1.35  [65.00 -> 87.75]  |  @superbwarfare:small_cannon_shell * 0.0740740741  [87.75 -> 6.50]
  - 主炮直击: @superbwarfare:cannon_shell + 13  [200.00 -> 213.00]  |  All - 13  [213.00 -> 200.00]  |  All * 0.2  [200.00 -> 40.00]  |  @superbwarfare:cannon_shell * 5  [40.00 -> 200.00]  |  superbwarfare:projectile_hit * 1.35  [200.00 -> 270.00]  |  @superbwarfare:cannon_shell * 0.3703703704  [270.00 -> 100.00]
  - 炮弹爆炸: @superbwarfare:small_cannon_shell + 13  [30.00 -> 43.00]  |  All - 13  [43.00 -> 30.00]  |  All * 0.2  [30.00 -> 6.00]  |  @superbwarfare:small_cannon_shell * 5  [6.00 -> 30.00]  |  superbwarfare:custom_explosion * 0.54  [30.00 -> 16.20]  |  @superbwarfare:small_cannon_shell * 0.0740740741  [16.20 -> 1.20]

### fcp:stryker_mortar  (HP 200)
  - 机炮直击: @superbwarfare:small_cannon_shell + 13  [65.00 -> 78.00]  |  All - 13  [78.00 -> 65.00]  |  All * 0.2  [65.00 -> 13.00]  |  @superbwarfare:small_cannon_shell * 5  [13.00 -> 65.00]  |  superbwarfare:projectile_hit * 1.35  [65.00 -> 87.75]  |  @superbwarfare:small_cannon_shell * 0.0740740741  [87.75 -> 6.50]
  - 主炮直击: @superbwarfare:cannon_shell + 13  [200.00 -> 213.00]  |  All - 13  [213.00 -> 200.00]  |  All * 0.2  [200.00 -> 40.00]  |  @superbwarfare:cannon_shell * 5  [40.00 -> 200.00]  |  superbwarfare:projectile_hit * 1.35  [200.00 -> 270.00]  |  @superbwarfare:cannon_shell * 0.3703703704  [270.00 -> 100.00]
  - 炮弹爆炸: @superbwarfare:small_cannon_shell + 13  [30.00 -> 43.00]  |  All - 13  [43.00 -> 30.00]  |  All * 0.2  [30.00 -> 6.00]  |  @superbwarfare:small_cannon_shell * 5  [6.00 -> 30.00]  |  superbwarfare:custom_explosion * 0.54  [30.00 -> 16.20]  |  @superbwarfare:small_cannon_shell * 0.0740740741  [16.20 -> 1.20]

### fcp:t72av  (HP 300)
  - 机炮直击: All * 0.2  [65.00 -> 13.00]  |  superbwarfare:projectile_hit * 1.25  [13.00 -> 16.25]  |  All - 11  [16.25 -> 5.25]
  - 主炮直击: All * 0.2  [200.00 -> 40.00]  |  superbwarfare:projectile_hit * 1.25  [40.00 -> 50.00]  |  All - 11  [50.00 -> 39.00]
  - 炮弹爆炸: All * 0.2  [30.00 -> 6.00]  |  superbwarfare:custom_explosion * 2.0  [6.00 -> 12.00]  |  All - 11  [12.00 -> 1.00]

## 原始 DamageModifiers（全部条目，便于核对）

### dragonrise_reforge:ah64
```
minecraft:arrow * 0.1
minecraft:trident * 0.2
minecraft:mob_attack * 0.3
minecraft:mob_attack_no_aggro * 0.3
minecraft:mob_projectile * 0.2
minecraft:player_attack * 0.2
minecraft:lava - -10
minecraft:lava * 2.6
@minecraft:tnt * 3
@minecraft:tnt_minecart * 3
minecraft:explosion * 3.5
minecraft:player_explosion * 3.5
superbwarfare:projectile_hit * 0.6
superbwarfare:grapeshot_hit * 0.5
#superbwarfare:projectile * 0.03
#superbwarfare:projectile_absolute * 0.3
superbwarfare:vehicle_strike * 6
superbwarfare:laser * 0.35
@superbwarfare:cannon_shell * 1.5
@superbwarfare:small_cannon_shell * 0.95
@superbwarfare:gun_grenade * 2.2
@superbwarfare:rgo_grenade * 6
@superbwarfare:hand_grenade * 5
@superbwarfare:mortar_shell * 3
@#superbwarfare:at_rocket * 1.5
@#superbwarfare:aerial_bomb * 2
```

### dragonrise_reforge:bmp3
```
@superbwarfare:small_cannon_shell + 13
@superbwarfare:cannon_shell + 13
All - 13
minecraft:lava + 13
minecraft:lava * 10
@minecraft:tnt * 3
@minecraft:tnt_minecart * 3
All * 0.2
@superbwarfare:small_cannon_shell * 5
@superbwarfare:cannon_shell * 5
minecraft:arrow * 1.5
minecraft:trident * 1.5
minecraft:mob_attack * 2.5
minecraft:mob_attack_no_aggro * 2
minecraft:mob_projectile * 1.5
minecraft:explosion * 6
minecraft:player_explosion * 6
superbwarfare:custom_explosion * 0.54
superbwarfare:projectile_explosion * 2
superbwarfare:mine * 0.7
superbwarfare:lunge_mine * 0.9
superbwarfare:projectile_hit * 1.35
@superbwarfare:small_cannon_shell * 0.0740740741
@superbwarfare:cannon_shell * 0.3703703704
superbwarfare:grapeshot_hit * 0.25
superbwarfare:laser * 1.25
@#superbwarfare:aerial_bomb * 3
@#superbwarfare:aa_missile * 0.5
#superbwarfare:projectile * 0.1
#superbwarfare:projectile_absolute * 0.7
#superbwarfare:vehicle_strike * 13
@superbwarfare:mortar_shell * 1.1
@superbwarfare:gun_grenade * 1.5
@superbwarfare:javelin_missile * 0.8
```

### dragonrise_reforge:csk181
```
@superbwarfare:small_cannon_shell + 13
@superbwarfare:cannon_shell + 13
All - 13
minecraft:lava + 13
minecraft:lava * 10
@minecraft:tnt * 3
@minecraft:tnt_minecart * 3
All * 0.2
@superbwarfare:small_cannon_shell * 5
@superbwarfare:cannon_shell * 5
minecraft:arrow * 1.5
minecraft:trident * 1.5
minecraft:mob_attack * 2.5
minecraft:mob_attack_no_aggro * 2
minecraft:mob_projectile * 1.5
minecraft:explosion * 6
minecraft:player_explosion * 6
superbwarfare:custom_explosion * 0.54
superbwarfare:projectile_explosion * 2
superbwarfare:mine * 0.7
superbwarfare:lunge_mine * 0.9
superbwarfare:projectile_hit * 1.35
@superbwarfare:small_cannon_shell * 0.0740740741
@superbwarfare:cannon_shell * 0.3703703704
superbwarfare:grapeshot_hit * 0.25
superbwarfare:laser * 1.25
@#superbwarfare:aerial_bomb * 3
@#superbwarfare:aa_missile * 0.5
#superbwarfare:projectile * 0.1
#superbwarfare:projectile_absolute * 0.7
#superbwarfare:vehicle_strike * 13
@superbwarfare:mortar_shell * 1.1
@superbwarfare:gun_grenade * 1.5
@superbwarfare:javelin_missile * 0.8
```

### dragonrise_reforge:m1126
```
@superbwarfare:small_cannon_shell + 13
@superbwarfare:cannon_shell + 13
All - 13
minecraft:lava + 13
minecraft:lava * 10
@minecraft:tnt * 3
@minecraft:tnt_minecart * 3
All * 0.2
@superbwarfare:small_cannon_shell * 5
@superbwarfare:cannon_shell * 5
minecraft:arrow * 1.5
minecraft:trident * 1.5
minecraft:mob_attack * 2.5
minecraft:mob_attack_no_aggro * 2
minecraft:mob_projectile * 1.5
minecraft:explosion * 6
minecraft:player_explosion * 6
superbwarfare:custom_explosion * 0.54
superbwarfare:projectile_explosion * 2
superbwarfare:mine * 0.7
superbwarfare:lunge_mine * 0.9
superbwarfare:projectile_hit * 1.35
@superbwarfare:small_cannon_shell * 0.0740740741
@superbwarfare:cannon_shell * 0.3703703704
superbwarfare:grapeshot_hit * 0.25
superbwarfare:laser * 1.25
@#superbwarfare:aerial_bomb * 3
@#superbwarfare:aa_missile * 0.5
#superbwarfare:projectile * 0.1
#superbwarfare:projectile_absolute * 0.7
#superbwarfare:vehicle_strike * 13
@superbwarfare:mortar_shell * 1.1
@superbwarfare:gun_grenade * 1.5
@superbwarfare:javelin_missile * 0.8
```

### dragonrise_reforge:m1128
```
@superbwarfare:small_cannon_shell + 13
@superbwarfare:cannon_shell + 13
All - 13
minecraft:lava + 13
minecraft:lava * 10
@minecraft:tnt * 3
@minecraft:tnt_minecart * 3
All * 0.2
@superbwarfare:small_cannon_shell * 5
@superbwarfare:cannon_shell * 5
minecraft:arrow * 1.5
minecraft:trident * 1.5
minecraft:mob_attack * 2.5
minecraft:mob_attack_no_aggro * 2
minecraft:mob_projectile * 1.5
minecraft:explosion * 6
minecraft:player_explosion * 6
superbwarfare:custom_explosion * 0.54
superbwarfare:projectile_explosion * 2
superbwarfare:mine * 0.7
superbwarfare:lunge_mine * 0.9
superbwarfare:projectile_hit * 1.35
@superbwarfare:small_cannon_shell * 0.0740740741
@superbwarfare:cannon_shell * 0.3703703704
superbwarfare:grapeshot_hit * 0.25
superbwarfare:laser * 1.25
@#superbwarfare:aerial_bomb * 3
@#superbwarfare:aa_missile * 0.5
#superbwarfare:projectile * 0.1
#superbwarfare:projectile_absolute * 0.7
#superbwarfare:vehicle_strike * 13
@superbwarfare:mortar_shell * 1.1
@superbwarfare:gun_grenade * 1.5
@superbwarfare:javelin_missile * 0.8
```

### dragonrise_reforge:m113
```
@superbwarfare:small_cannon_shell + 13
@superbwarfare:cannon_shell + 13
All - 13
minecraft:lava + 13
minecraft:lava * 10
@minecraft:tnt * 3
@minecraft:tnt_minecart * 3
All * 0.2
@superbwarfare:small_cannon_shell * 5
@superbwarfare:cannon_shell * 5
minecraft:arrow * 1.5
minecraft:trident * 1.5
minecraft:mob_attack * 2.5
minecraft:mob_attack_no_aggro * 2
minecraft:mob_projectile * 1.5
minecraft:explosion * 6
minecraft:player_explosion * 6
superbwarfare:custom_explosion * 0.54
superbwarfare:projectile_explosion * 2
superbwarfare:mine * 0.7
superbwarfare:lunge_mine * 0.9
superbwarfare:projectile_hit * 1.35
@superbwarfare:small_cannon_shell * 0.0740740741
@superbwarfare:cannon_shell * 0.3703703704
superbwarfare:grapeshot_hit * 0.25
superbwarfare:laser * 1.25
@#superbwarfare:aerial_bomb * 3
@#superbwarfare:aa_missile * 0.5
#superbwarfare:projectile * 0.1
#superbwarfare:projectile_absolute * 0.7
#superbwarfare:vehicle_strike * 13
@superbwarfare:mortar_shell * 1.1
@superbwarfare:gun_grenade * 1.5
@superbwarfare:javelin_missile * 0.8
```

### dragonrise_reforge:m1296
```
@superbwarfare:small_cannon_shell + 13
@superbwarfare:cannon_shell + 13
All - 13
minecraft:lava + 13
minecraft:lava * 10
@minecraft:tnt * 3
@minecraft:tnt_minecart * 3
All * 0.2
@superbwarfare:small_cannon_shell * 5
@superbwarfare:cannon_shell * 5
minecraft:arrow * 1.5
minecraft:trident * 1.5
minecraft:mob_attack * 2.5
minecraft:mob_attack_no_aggro * 2
minecraft:mob_projectile * 1.5
minecraft:explosion * 6
minecraft:player_explosion * 6
superbwarfare:custom_explosion * 0.54
superbwarfare:projectile_explosion * 2
superbwarfare:mine * 0.7
superbwarfare:lunge_mine * 0.9
superbwarfare:projectile_hit * 1.35
@superbwarfare:small_cannon_shell * 0.0740740741
@superbwarfare:cannon_shell * 0.3703703704
superbwarfare:grapeshot_hit * 0.25
superbwarfare:laser * 1.25
@#superbwarfare:aerial_bomb * 3
@#superbwarfare:aa_missile * 0.5
#superbwarfare:projectile * 0.1
#superbwarfare:projectile_absolute * 0.7
#superbwarfare:vehicle_strike * 13
@superbwarfare:mortar_shell * 1.1
@superbwarfare:gun_grenade * 1.5
@superbwarfare:javelin_missile * 0.8
```

### dragonrise_reforge:m1a2sepv1
```
minecraft:arrow 0
minecraft:trident 0
minecraft:mob_attack 0
minecraft:mob_attack_no_aggro 0
minecraft:mob_projectile 0
minecraft:player_attack 0
#superbwarfare:projectile 0
@superbwarfare:small_cannon_shell + 20
@superbwarfare:cannon_shell + 20
All - 20
minecraft:lava + 20
minecraft:lava * 10
@minecraft:tnt * 4
@minecraft:tnt_minecart * 4
@#superbwarfare:aerial_bomb * 12
All * 0.2
@superbwarfare:small_cannon_shell * 5
@superbwarfare:cannon_shell * 5
superbwarfare:vehicle_strike * 2.5
minecraft:explosion * 2
superbwarfare:custom_explosion * 0.0433333333
superbwarfare:projectile_explosion * 0.65
superbwarfare:mine * 0.5
superbwarfare:lunge_mine * 0.5
superbwarfare:projectile_hit * 1.3
@superbwarfare:small_cannon_shell * 0.0769230769
@superbwarfare:cannon_shell * 0.2307692308
superbwarfare:grapeshot_hit * 0.1
#superbwarfare:projectile_absolute * 0.15
@#superbwarfare:aa_missile * 0.3
@superbwarfare:c4 * 4
@#superbwarfare:at_rocket * 1.1
@superbwarfare:gun_grenade * 1.25
@superbwarfare:mortar_shell * 1.25
@superbwarfare:tm_62 * 2.5
```

### dragonrise_reforge:m1a2sepv2
```
minecraft:arrow 0
minecraft:trident 0
minecraft:mob_attack 0
minecraft:mob_attack_no_aggro 0
minecraft:mob_projectile 0
minecraft:player_attack 0
#superbwarfare:projectile 0
@superbwarfare:small_cannon_shell + 20
@superbwarfare:cannon_shell + 20
All - 20
minecraft:lava + 20
minecraft:lava * 10
@minecraft:tnt * 4
@minecraft:tnt_minecart * 4
@#superbwarfare:aerial_bomb * 12
All * 0.2
@superbwarfare:small_cannon_shell * 5
@superbwarfare:cannon_shell * 5
superbwarfare:vehicle_strike * 2.5
minecraft:explosion * 2
superbwarfare:custom_explosion * 0.0433333333
superbwarfare:projectile_explosion * 0.65
superbwarfare:mine * 0.5
superbwarfare:lunge_mine * 0.5
superbwarfare:projectile_hit * 1.3
@superbwarfare:small_cannon_shell * 0.0769230769
@superbwarfare:cannon_shell * 0.2307692308
superbwarfare:grapeshot_hit * 0.1
#superbwarfare:projectile_absolute * 0.15
@#superbwarfare:aa_missile * 0.3
@superbwarfare:c4 * 4
@#superbwarfare:at_rocket * 1.1
@superbwarfare:gun_grenade * 1.25
@superbwarfare:mortar_shell * 1.25
@superbwarfare:tm_62 * 2.5
```

### dragonrise_reforge:m3a3
```
@superbwarfare:small_cannon_shell + 13
@superbwarfare:cannon_shell + 13
All - 13
minecraft:lava + 13
minecraft:lava * 10
@minecraft:tnt * 3
@minecraft:tnt_minecart * 3
All * 0.2
@superbwarfare:small_cannon_shell * 5
@superbwarfare:cannon_shell * 5
minecraft:arrow * 1.5
minecraft:trident * 1.5
minecraft:mob_attack * 2.5
minecraft:mob_attack_no_aggro * 2
minecraft:mob_projectile * 1.5
minecraft:explosion * 6
minecraft:player_explosion * 6
superbwarfare:custom_explosion * 0.54
superbwarfare:projectile_explosion * 2
superbwarfare:mine * 0.7
superbwarfare:lunge_mine * 0.9
superbwarfare:projectile_hit * 1.35
@superbwarfare:small_cannon_shell * 0.0740740741
@superbwarfare:cannon_shell * 0.3703703704
superbwarfare:grapeshot_hit * 0.25
superbwarfare:laser * 1.25
@#superbwarfare:aerial_bomb * 3
@#superbwarfare:aa_missile * 0.5
#superbwarfare:projectile * 0.1
#superbwarfare:projectile_absolute * 0.7
#superbwarfare:vehicle_strike * 13
@superbwarfare:mortar_shell * 1.1
@superbwarfare:gun_grenade * 1.5
@superbwarfare:javelin_missile * 0.8
```

### dragonrise_reforge:mv3
```
minecraft:arrow * 0.1
minecraft:trident * 0.2
minecraft:mob_attack * 0.2
minecraft:mob_attack_no_aggro * 0.2
minecraft:mob_projectile * 0.2
minecraft:player_attack * 0.2
minecraft:lava - -4
minecraft:lava * 4
@minecraft:tnt * 1.5
@minecraft:tnt_minecart * 1.5
minecraft:explosion * 2
minecraft:player_explosion * 2
superbwarfare:projectile_hit * 0.4
superbwarfare:grapeshot_hit * 0.1
@#superbwarfare:aerial_bomb * 0.7
#superbwarfare:projectile * 0.5
@superbwarfare:cannon_shell * 0.8
@superbwarfare:small_cannon_shell * 0.9
@superbwarfare:gun_grenade * 0.7
@superbwarfare:rgo_grenade * 1.5
@superbwarfare:hand_grenade * 1.25
@superbwarfare:mortar_shell * 1
```

### dragonrise_reforge:mv3_armed
```
minecraft:arrow * 0.1
minecraft:trident * 0.2
minecraft:mob_attack * 0.2
minecraft:mob_attack_no_aggro * 0.2
minecraft:mob_projectile * 0.2
minecraft:player_attack * 0.2
minecraft:lava - -4
minecraft:lava * 4
@minecraft:tnt * 1.5
@minecraft:tnt_minecart * 1.5
minecraft:explosion * 2
minecraft:player_explosion * 2
superbwarfare:projectile_hit * 0.4
superbwarfare:grapeshot_hit * 0.1
@#superbwarfare:aerial_bomb * 0.7
#superbwarfare:projectile * 0.5
@superbwarfare:cannon_shell * 0.8
@superbwarfare:small_cannon_shell * 0.9
@superbwarfare:gun_grenade * 0.7
@superbwarfare:rgo_grenade * 1.5
@superbwarfare:hand_grenade * 1.25
@superbwarfare:mortar_shell * 1
```

### dragonrise_reforge:mv3_supply
```
minecraft:arrow * 0.1
minecraft:trident * 0.2
minecraft:mob_attack * 0.2
minecraft:mob_attack_no_aggro * 0.2
minecraft:mob_projectile * 0.2
minecraft:player_attack * 0.2
minecraft:lava - -4
minecraft:lava * 4
@minecraft:tnt * 1.5
@minecraft:tnt_minecart * 1.5
minecraft:explosion * 2
minecraft:player_explosion * 2
superbwarfare:projectile_hit * 0.4
superbwarfare:grapeshot_hit * 0.1
@#superbwarfare:aerial_bomb * 0.7
#superbwarfare:projectile * 0.5
@superbwarfare:cannon_shell * 0.8
@superbwarfare:small_cannon_shell * 0.9
@superbwarfare:gun_grenade * 0.7
@superbwarfare:rgo_grenade * 1.5
@superbwarfare:hand_grenade * 1.25
@superbwarfare:mortar_shell * 1
```

### dragonrise_reforge:sx1
```
minecraft:arrow * 0.2
minecraft:trident * 0.4
minecraft:mob_attack * 0.4
minecraft:mob_attack_no_aggro * 0.4
minecraft:mob_projectile * 0.4
minecraft:player_attack * 0.4
minecraft:lava - -4
minecraft:lava * 4
@minecraft:tnt * 3
@minecraft:tnt_minecart * 3
minecraft:explosion * 4
minecraft:player_explosion * 4
superbwarfare:projectile_hit * 0.8
superbwarfare:grapeshot_hit * 0.2
@#superbwarfare:aerial_bomb * 1.4
#superbwarfare:projectile * 1.0
superbwarfare:vehicle_strike * 10
@superbwarfare:cannon_shell * 1.6
@superbwarfare:small_cannon_shell * 1.8
@superbwarfare:gun_grenade * 1.4
@superbwarfare:rgo_grenade * 3
@superbwarfare:hand_grenade * 2.5
@superbwarfare:mortar_shell * 2
```

### dragonrise_reforge:sx1_a
```

```

### dragonrise_reforge:t72b3
```
minecraft:arrow 0
minecraft:trident 0
minecraft:mob_attack 0
minecraft:mob_attack_no_aggro 0
minecraft:mob_projectile 0
minecraft:player_attack 0
#superbwarfare:projectile 0
@superbwarfare:small_cannon_shell + 20
@superbwarfare:cannon_shell + 20
All - 20
minecraft:lava + 20
minecraft:lava * 10
@minecraft:tnt * 4
@minecraft:tnt_minecart * 4
@#superbwarfare:aerial_bomb * 12
All * 0.2
@superbwarfare:small_cannon_shell * 5
@superbwarfare:cannon_shell * 5
superbwarfare:vehicle_strike * 2.5
minecraft:explosion * 2
superbwarfare:custom_explosion * 0.0433333333
superbwarfare:projectile_explosion * 0.65
superbwarfare:mine * 0.5
superbwarfare:lunge_mine * 0.5
superbwarfare:projectile_hit * 1.3
@superbwarfare:small_cannon_shell * 0.0769230769
@superbwarfare:cannon_shell * 0.2307692308
superbwarfare:grapeshot_hit * 0.1
#superbwarfare:projectile_absolute * 0.15
@#superbwarfare:aa_missile * 0.3
@superbwarfare:c4 * 4
@#superbwarfare:at_rocket * 1.1
@superbwarfare:gun_grenade * 1.25
@superbwarfare:mortar_shell * 1.25
@superbwarfare:tm_62 * 2.5
```

### dragonrise_reforge:t90mh
```
minecraft:arrow 0
minecraft:trident 0
minecraft:mob_attack 0
minecraft:mob_attack_no_aggro 0
minecraft:mob_projectile 0
minecraft:player_attack 0
#superbwarfare:projectile 0
@superbwarfare:small_cannon_shell + 20
@superbwarfare:cannon_shell + 20
All - 20
minecraft:lava + 20
minecraft:lava * 10
@minecraft:tnt * 4
@minecraft:tnt_minecart * 4
@#superbwarfare:aerial_bomb * 12
All * 0.2
@superbwarfare:small_cannon_shell * 5
@superbwarfare:cannon_shell * 5
superbwarfare:vehicle_strike * 2.5
minecraft:explosion * 2
superbwarfare:custom_explosion * 0.0433333333
superbwarfare:projectile_explosion * 0.65
superbwarfare:mine * 0.5
superbwarfare:lunge_mine * 0.5
superbwarfare:projectile_hit * 1.3
@superbwarfare:small_cannon_shell * 0.0769230769
@superbwarfare:cannon_shell * 0.2307692308
superbwarfare:grapeshot_hit * 0.1
#superbwarfare:projectile_absolute * 0.15
@#superbwarfare:aa_missile * 0.3
@superbwarfare:c4 * 4
@#superbwarfare:at_rocket * 1.1
@superbwarfare:gun_grenade * 1.25
@superbwarfare:mortar_shell * 1.25
@superbwarfare:tm_62 * 2.5
```

### dragonrise_reforge:ural4320
```
minecraft:arrow * 0.1
minecraft:trident * 0.2
minecraft:mob_attack * 0.2
minecraft:mob_attack_no_aggro * 0.2
minecraft:mob_projectile * 0.2
minecraft:player_attack * 0.2
minecraft:lava - -4
minecraft:lava * 4
@minecraft:tnt * 1.5
@minecraft:tnt_minecart * 1.5
minecraft:explosion * 2
minecraft:player_explosion * 2
superbwarfare:projectile_hit * 0.4
superbwarfare:grapeshot_hit * 0.1
@#superbwarfare:aerial_bomb * 0.7
#superbwarfare:projectile * 0.5
@superbwarfare:cannon_shell * 0.8
@superbwarfare:small_cannon_shell * 0.9
@superbwarfare:gun_grenade * 0.7
@superbwarfare:rgo_grenade * 1.5
@superbwarfare:hand_grenade * 1.25
@superbwarfare:mortar_shell * 1
```

### dragonrise_reforge:ural4320_supply
```
minecraft:arrow * 0.1
minecraft:trident * 0.2
minecraft:mob_attack * 0.2
minecraft:mob_attack_no_aggro * 0.2
minecraft:mob_projectile * 0.2
minecraft:player_attack * 0.2
minecraft:lava - -4
minecraft:lava * 4
@minecraft:tnt * 1.5
@minecraft:tnt_minecart * 1.5
minecraft:explosion * 2
minecraft:player_explosion * 2
superbwarfare:projectile_hit * 0.4
superbwarfare:grapeshot_hit * 0.1
@#superbwarfare:aerial_bomb * 0.7
#superbwarfare:projectile * 0.5
@superbwarfare:cannon_shell * 0.8
@superbwarfare:small_cannon_shell * 0.9
@superbwarfare:gun_grenade * 0.7
@superbwarfare:rgo_grenade * 1.5
@superbwarfare:hand_grenade * 1.25
@superbwarfare:mortar_shell * 1
```

### dragonrise_reforge:ural4320_zu23
```
minecraft:arrow * 0.1
minecraft:trident * 0.2
minecraft:mob_attack * 0.2
minecraft:mob_attack_no_aggro * 0.2
minecraft:mob_projectile * 0.2
minecraft:player_attack * 0.2
minecraft:lava - -4
minecraft:lava * 4
@minecraft:tnt * 1.5
@minecraft:tnt_minecart * 1.5
minecraft:explosion * 2
minecraft:player_explosion * 2
superbwarfare:projectile_hit * 0.4
superbwarfare:grapeshot_hit * 0.1
@#superbwarfare:aerial_bomb * 0.7
#superbwarfare:projectile * 0.5
@superbwarfare:cannon_shell * 0.8
@superbwarfare:small_cannon_shell * 0.9
@superbwarfare:gun_grenade * 0.7
@superbwarfare:rgo_grenade * 1.5
@superbwarfare:hand_grenade * 1.25
@superbwarfare:mortar_shell * 1
```

### dragonrise_reforge:z20
```
minecraft:arrow * 0.1
minecraft:trident * 0.2
minecraft:mob_attack * 0.3
minecraft:mob_attack_no_aggro * 0.3
minecraft:mob_projectile * 0.2
minecraft:player_attack * 0.2
minecraft:lava - -10
minecraft:lava * 2.6
@minecraft:tnt * 3
@minecraft:tnt_minecart * 3
minecraft:explosion * 3.5
minecraft:player_explosion * 3.5
superbwarfare:projectile_hit * 0.6
superbwarfare:grapeshot_hit * 0.5
#superbwarfare:projectile * 0.03
#superbwarfare:projectile_absolute * 0.3
superbwarfare:vehicle_strike * 6
superbwarfare:laser * 0.35
@superbwarfare:cannon_shell * 1.5
@superbwarfare:small_cannon_shell * 0.95
@superbwarfare:gun_grenade * 2.2
@superbwarfare:rgo_grenade * 6
@superbwarfare:hand_grenade * 5
@superbwarfare:mortar_shell * 3
@#superbwarfare:at_rocket * 1.5
@#superbwarfare:aerial_bomb * 2
```

### dragonrise_reforge:zbd04a
```
@superbwarfare:small_cannon_shell + 13
@superbwarfare:cannon_shell + 13
All - 13
minecraft:lava + 13
minecraft:lava * 10
@minecraft:tnt * 3
@minecraft:tnt_minecart * 3
All * 0.2
@superbwarfare:small_cannon_shell * 5
@superbwarfare:cannon_shell * 5
minecraft:arrow * 1.5
minecraft:trident * 1.5
minecraft:mob_attack * 2.5
minecraft:mob_attack_no_aggro * 2
minecraft:mob_projectile * 1.5
minecraft:explosion * 6
minecraft:player_explosion * 6
superbwarfare:custom_explosion * 0.54
superbwarfare:projectile_explosion * 2
superbwarfare:mine * 0.7
superbwarfare:lunge_mine * 0.9
superbwarfare:projectile_hit * 1.35
@superbwarfare:small_cannon_shell * 0.0740740741
@superbwarfare:cannon_shell * 0.3703703704
superbwarfare:grapeshot_hit * 0.25
superbwarfare:laser * 1.25
@#superbwarfare:aerial_bomb * 3
@#superbwarfare:aa_missile * 0.5
#superbwarfare:projectile * 0.1
#superbwarfare:projectile_absolute * 0.7
#superbwarfare:vehicle_strike * 13
@superbwarfare:mortar_shell * 1.1
@superbwarfare:gun_grenade * 1.5
@superbwarfare:javelin_missile * 0.8
```

### dragonrise_reforge:zbd05
```
@superbwarfare:small_cannon_shell + 13
@superbwarfare:cannon_shell + 13
All - 13
minecraft:lava + 13
minecraft:lava * 10
@minecraft:tnt * 3
@minecraft:tnt_minecart * 3
All * 0.2
@superbwarfare:small_cannon_shell * 5
@superbwarfare:cannon_shell * 5
minecraft:arrow * 1.5
minecraft:trident * 1.5
minecraft:mob_attack * 2.5
minecraft:mob_attack_no_aggro * 2
minecraft:mob_projectile * 1.5
minecraft:explosion * 6
minecraft:player_explosion * 6
superbwarfare:custom_explosion * 0.54
superbwarfare:projectile_explosion * 2
superbwarfare:mine * 0.7
superbwarfare:lunge_mine * 0.9
superbwarfare:projectile_hit * 1.35
@superbwarfare:small_cannon_shell * 0.0740740741
@superbwarfare:cannon_shell * 0.3703703704
superbwarfare:grapeshot_hit * 0.25
superbwarfare:laser * 1.25
@#superbwarfare:aerial_bomb * 3
@#superbwarfare:aa_missile * 0.5
#superbwarfare:projectile * 0.1
#superbwarfare:projectile_absolute * 0.7
#superbwarfare:vehicle_strike * 13
@superbwarfare:mortar_shell * 1.1
@superbwarfare:gun_grenade * 1.5
@superbwarfare:javelin_missile * 0.8
```

### dragonrise_reforge:zbl08
```
@superbwarfare:small_cannon_shell + 13
@superbwarfare:cannon_shell + 13
All - 13
minecraft:lava + 13
minecraft:lava * 10
@minecraft:tnt * 3
@minecraft:tnt_minecart * 3
All * 0.2
@superbwarfare:small_cannon_shell * 5
@superbwarfare:cannon_shell * 5
minecraft:arrow * 1.5
minecraft:trident * 1.5
minecraft:mob_attack * 2.5
minecraft:mob_attack_no_aggro * 2
minecraft:mob_projectile * 1.5
minecraft:explosion * 6
minecraft:player_explosion * 6
superbwarfare:custom_explosion * 0.54
superbwarfare:projectile_explosion * 2
superbwarfare:mine * 0.7
superbwarfare:lunge_mine * 0.9
superbwarfare:projectile_hit * 1.35
@superbwarfare:small_cannon_shell * 0.0740740741
@superbwarfare:cannon_shell * 0.3703703704
superbwarfare:grapeshot_hit * 0.25
superbwarfare:laser * 1.25
@#superbwarfare:aerial_bomb * 3
@#superbwarfare:aa_missile * 0.5
#superbwarfare:projectile * 0.1
#superbwarfare:projectile_absolute * 0.7
#superbwarfare:vehicle_strike * 13
@superbwarfare:mortar_shell * 1.1
@superbwarfare:gun_grenade * 1.5
@superbwarfare:javelin_missile * 0.8
```

### dragonrise_reforge:zlt11
```
@superbwarfare:small_cannon_shell + 13
@superbwarfare:cannon_shell + 13
All - 13
minecraft:lava + 13
minecraft:lava * 10
@minecraft:tnt * 3
@minecraft:tnt_minecart * 3
All * 0.2
@superbwarfare:small_cannon_shell * 5
@superbwarfare:cannon_shell * 5
minecraft:arrow * 1.5
minecraft:trident * 1.5
minecraft:mob_attack * 2.5
minecraft:mob_attack_no_aggro * 2
minecraft:mob_projectile * 1.5
minecraft:explosion * 6
minecraft:player_explosion * 6
superbwarfare:custom_explosion * 0.54
superbwarfare:projectile_explosion * 2
superbwarfare:mine * 0.7
superbwarfare:lunge_mine * 0.9
superbwarfare:projectile_hit * 1.35
@superbwarfare:small_cannon_shell * 0.0740740741
@superbwarfare:cannon_shell * 0.3703703704
superbwarfare:grapeshot_hit * 0.25
superbwarfare:laser * 1.25
@#superbwarfare:aerial_bomb * 3
@#superbwarfare:aa_missile * 0.5
#superbwarfare:projectile * 0.1
#superbwarfare:projectile_absolute * 0.7
#superbwarfare:vehicle_strike * 13
@superbwarfare:mortar_shell * 1.1
@superbwarfare:gun_grenade * 1.5
@superbwarfare:javelin_missile * 0.8
```

### dragonrise_reforge:zsl10
```
@superbwarfare:small_cannon_shell + 13
@superbwarfare:cannon_shell + 13
All - 13
minecraft:lava + 13
minecraft:lava * 10
@minecraft:tnt * 3
@minecraft:tnt_minecart * 3
All * 0.2
@superbwarfare:small_cannon_shell * 5
@superbwarfare:cannon_shell * 5
minecraft:arrow * 1.5
minecraft:trident * 1.5
minecraft:mob_attack * 2.5
minecraft:mob_attack_no_aggro * 2
minecraft:mob_projectile * 1.5
minecraft:explosion * 6
minecraft:player_explosion * 6
superbwarfare:custom_explosion * 0.54
superbwarfare:projectile_explosion * 2
superbwarfare:mine * 0.7
superbwarfare:lunge_mine * 0.9
superbwarfare:projectile_hit * 1.35
@superbwarfare:small_cannon_shell * 0.0740740741
@superbwarfare:cannon_shell * 0.3703703704
superbwarfare:grapeshot_hit * 0.25
superbwarfare:laser * 1.25
@#superbwarfare:aerial_bomb * 3
@#superbwarfare:aa_missile * 0.5
#superbwarfare:projectile * 0.1
#superbwarfare:projectile_absolute * 0.7
#superbwarfare:vehicle_strike * 13
@superbwarfare:mortar_shell * 1.1
@superbwarfare:gun_grenade * 1.5
@superbwarfare:javelin_missile * 0.8
```

### dragonrise_reforge:ztd05
```
@superbwarfare:small_cannon_shell + 13
@superbwarfare:cannon_shell + 13
All - 13
minecraft:lava + 13
minecraft:lava * 10
@minecraft:tnt * 3
@minecraft:tnt_minecart * 3
All * 0.2
@superbwarfare:small_cannon_shell * 5
@superbwarfare:cannon_shell * 5
minecraft:arrow * 1.5
minecraft:trident * 1.5
minecraft:mob_attack * 2.5
minecraft:mob_attack_no_aggro * 2
minecraft:mob_projectile * 1.5
minecraft:explosion * 6
minecraft:player_explosion * 6
superbwarfare:custom_explosion * 0.54
superbwarfare:projectile_explosion * 2
superbwarfare:mine * 0.7
superbwarfare:lunge_mine * 0.9
superbwarfare:projectile_hit * 1.35
@superbwarfare:small_cannon_shell * 0.0740740741
@superbwarfare:cannon_shell * 0.3703703704
superbwarfare:grapeshot_hit * 0.25
superbwarfare:laser * 1.25
@#superbwarfare:aerial_bomb * 3
@#superbwarfare:aa_missile * 0.5
#superbwarfare:projectile * 0.1
#superbwarfare:projectile_absolute * 0.7
#superbwarfare:vehicle_strike * 13
@superbwarfare:mortar_shell * 1.1
@superbwarfare:gun_grenade * 1.5
@superbwarfare:javelin_missile * 0.8
```

### dragonrise_reforge:ztz96a
```
minecraft:arrow 0
minecraft:trident 0
minecraft:mob_attack 0
minecraft:mob_attack_no_aggro 0
minecraft:mob_projectile 0
minecraft:player_attack 0
#superbwarfare:projectile 0
@superbwarfare:small_cannon_shell + 20
@superbwarfare:cannon_shell + 20
All - 20
minecraft:lava + 20
minecraft:lava * 10
@minecraft:tnt * 4
@minecraft:tnt_minecart * 4
@#superbwarfare:aerial_bomb * 12
All * 0.2
@superbwarfare:small_cannon_shell * 5
@superbwarfare:cannon_shell * 5
superbwarfare:vehicle_strike * 2.5
minecraft:explosion * 2
superbwarfare:custom_explosion * 0.0433333333
superbwarfare:projectile_explosion * 0.65
superbwarfare:mine * 0.5
superbwarfare:lunge_mine * 0.5
superbwarfare:projectile_hit * 1.3
@superbwarfare:small_cannon_shell * 0.0769230769
@superbwarfare:cannon_shell * 0.2307692308
superbwarfare:grapeshot_hit * 0.1
#superbwarfare:projectile_absolute * 0.15
@#superbwarfare:aa_missile * 0.3
@superbwarfare:c4 * 4
@#superbwarfare:at_rocket * 1.1
@superbwarfare:gun_grenade * 1.25
@superbwarfare:mortar_shell * 1.25
@superbwarfare:tm_62 * 2.5
```

### dragonrise_reforge:ztz99a
```
minecraft:arrow 0
minecraft:trident 0
minecraft:mob_attack 0
minecraft:mob_attack_no_aggro 0
minecraft:mob_projectile 0
minecraft:player_attack 0
#superbwarfare:projectile 0
@superbwarfare:small_cannon_shell + 20
@superbwarfare:cannon_shell + 20
All - 20
minecraft:lava + 20
minecraft:lava * 10
@minecraft:tnt * 4
@minecraft:tnt_minecart * 4
@#superbwarfare:aerial_bomb * 12
All * 0.2
@superbwarfare:small_cannon_shell * 5
@superbwarfare:cannon_shell * 5
superbwarfare:vehicle_strike * 2.5
minecraft:explosion * 2
superbwarfare:custom_explosion * 0.0433333333
superbwarfare:projectile_explosion * 0.65
superbwarfare:mine * 0.5
superbwarfare:lunge_mine * 0.5
superbwarfare:projectile_hit * 1.3
@superbwarfare:small_cannon_shell * 0.0769230769
@superbwarfare:cannon_shell * 0.2307692308
superbwarfare:grapeshot_hit * 0.1
#superbwarfare:projectile_absolute * 0.15
@#superbwarfare:aa_missile * 0.3
@superbwarfare:c4 * 4
@#superbwarfare:at_rocket * 1.1
@superbwarfare:gun_grenade * 1.25
@superbwarfare:mortar_shell * 1.25
@superbwarfare:tm_62 * 2.5
```

### fcp:bmp1am
```
@superbwarfare:small_cannon_shell + 13
@superbwarfare:cannon_shell + 13
All - 13
minecraft:lava + 13
minecraft:lava * 10
@minecraft:tnt * 3
@minecraft:tnt_minecart * 3
All * 0.2
@superbwarfare:small_cannon_shell * 5
@superbwarfare:cannon_shell * 5
minecraft:arrow * 1.5
minecraft:trident * 1.5
minecraft:mob_attack * 2.5
minecraft:mob_attack_no_aggro * 2
minecraft:mob_projectile * 1.5
minecraft:explosion * 6
minecraft:player_explosion * 6
superbwarfare:custom_explosion * 0.54
superbwarfare:projectile_explosion * 2
superbwarfare:mine * 0.7
superbwarfare:lunge_mine * 0.9
superbwarfare:projectile_hit * 1.35
@superbwarfare:small_cannon_shell * 0.0740740741
@superbwarfare:cannon_shell * 0.3703703704
superbwarfare:grapeshot_hit * 0.25
superbwarfare:laser * 1.25
@#superbwarfare:aerial_bomb * 3
@#superbwarfare:aa_missile * 0.5
#superbwarfare:projectile * 0.1
#superbwarfare:projectile_absolute * 0.7
#superbwarfare:vehicle_strike * 13
@superbwarfare:mortar_shell * 1.1
@superbwarfare:gun_grenade * 1.5
@superbwarfare:javelin_missile * 0.8
```

### fcp:bmp2
```
@superbwarfare:small_cannon_shell + 13
@superbwarfare:cannon_shell + 13
All - 13
minecraft:lava + 13
minecraft:lava * 10
@minecraft:tnt * 3
@minecraft:tnt_minecart * 3
All * 0.2
@superbwarfare:small_cannon_shell * 5
@superbwarfare:cannon_shell * 5
minecraft:arrow * 1.5
minecraft:trident * 1.5
minecraft:mob_attack * 2.5
minecraft:mob_attack_no_aggro * 2
minecraft:mob_projectile * 1.5
minecraft:explosion * 6
minecraft:player_explosion * 6
superbwarfare:custom_explosion * 0.54
superbwarfare:projectile_explosion * 2
superbwarfare:mine * 0.7
superbwarfare:lunge_mine * 0.9
superbwarfare:projectile_hit * 1.35
@superbwarfare:small_cannon_shell * 0.0740740741
@superbwarfare:cannon_shell * 0.3703703704
superbwarfare:grapeshot_hit * 0.25
superbwarfare:laser * 1.25
@#superbwarfare:aerial_bomb * 3
@#superbwarfare:aa_missile * 0.5
#superbwarfare:projectile * 0.1
#superbwarfare:projectile_absolute * 0.7
#superbwarfare:vehicle_strike * 13
@superbwarfare:mortar_shell * 1.1
@superbwarfare:gun_grenade * 1.5
@superbwarfare:javelin_missile * 0.8
```

### fcp:bmp2d
```
@superbwarfare:small_cannon_shell + 13
@superbwarfare:cannon_shell + 13
All - 13
minecraft:lava + 13
minecraft:lava * 10
@minecraft:tnt * 3
@minecraft:tnt_minecart * 3
All * 0.2
@superbwarfare:small_cannon_shell * 5
@superbwarfare:cannon_shell * 5
minecraft:arrow * 1.5
minecraft:trident * 1.5
minecraft:mob_attack * 2.5
minecraft:mob_attack_no_aggro * 2
minecraft:mob_projectile * 1.5
minecraft:explosion * 6
minecraft:player_explosion * 6
superbwarfare:custom_explosion * 0.54
superbwarfare:projectile_explosion * 2
superbwarfare:mine * 0.7
superbwarfare:lunge_mine * 0.9
superbwarfare:projectile_hit * 1.35
@superbwarfare:small_cannon_shell * 0.0740740741
@superbwarfare:cannon_shell * 0.3703703704
superbwarfare:grapeshot_hit * 0.25
superbwarfare:laser * 1.25
@#superbwarfare:aerial_bomb * 3
@#superbwarfare:aa_missile * 0.5
#superbwarfare:projectile * 0.1
#superbwarfare:projectile_absolute * 0.7
#superbwarfare:vehicle_strike * 13
@superbwarfare:mortar_shell * 1.1
@superbwarfare:gun_grenade * 1.5
@superbwarfare:javelin_missile * 0.8
```

### fcp:bmp2m
```
@superbwarfare:small_cannon_shell + 13
@superbwarfare:cannon_shell + 13
All - 13
minecraft:lava + 13
minecraft:lava * 10
@minecraft:tnt * 3
@minecraft:tnt_minecart * 3
All * 0.2
@superbwarfare:small_cannon_shell * 5
@superbwarfare:cannon_shell * 5
minecraft:arrow * 1.5
minecraft:trident * 1.5
minecraft:mob_attack * 2.5
minecraft:mob_attack_no_aggro * 2
minecraft:mob_projectile * 1.5
minecraft:explosion * 6
minecraft:player_explosion * 6
superbwarfare:custom_explosion * 0.54
superbwarfare:projectile_explosion * 2
superbwarfare:mine * 0.7
superbwarfare:lunge_mine * 0.9
superbwarfare:projectile_hit * 1.35
@superbwarfare:small_cannon_shell * 0.0740740741
@superbwarfare:cannon_shell * 0.3703703704
superbwarfare:grapeshot_hit * 0.25
superbwarfare:laser * 1.25
@#superbwarfare:aerial_bomb * 3
@#superbwarfare:aa_missile * 0.5
#superbwarfare:projectile * 0.1
#superbwarfare:projectile_absolute * 0.7
#superbwarfare:vehicle_strike * 13
@superbwarfare:mortar_shell * 1.1
@superbwarfare:gun_grenade * 1.5
@superbwarfare:javelin_missile * 0.8
```

### fcp:btr80
```
@superbwarfare:small_cannon_shell + 13
@superbwarfare:cannon_shell + 13
All - 13
minecraft:lava + 13
minecraft:lava * 10
@minecraft:tnt * 3
@minecraft:tnt_minecart * 3
All * 0.2
@superbwarfare:small_cannon_shell * 5
@superbwarfare:cannon_shell * 5
minecraft:arrow * 1.5
minecraft:trident * 1.5
minecraft:mob_attack * 2.5
minecraft:mob_attack_no_aggro * 2
minecraft:mob_projectile * 1.5
minecraft:explosion * 6
minecraft:player_explosion * 6
superbwarfare:custom_explosion * 0.54
superbwarfare:projectile_explosion * 2
superbwarfare:mine * 0.7
superbwarfare:lunge_mine * 0.9
superbwarfare:projectile_hit * 1.35
@superbwarfare:small_cannon_shell * 0.0740740741
@superbwarfare:cannon_shell * 0.3703703704
superbwarfare:grapeshot_hit * 0.25
superbwarfare:laser * 1.25
@#superbwarfare:aerial_bomb * 3
@#superbwarfare:aa_missile * 0.5
#superbwarfare:projectile * 0.1
#superbwarfare:projectile_absolute * 0.7
#superbwarfare:vehicle_strike * 13
@superbwarfare:mortar_shell * 1.1
@superbwarfare:gun_grenade * 1.5
@superbwarfare:javelin_missile * 0.8
```

### fcp:btr82
```
@superbwarfare:small_cannon_shell + 13
@superbwarfare:cannon_shell + 13
All - 13
minecraft:lava + 13
minecraft:lava * 10
@minecraft:tnt * 3
@minecraft:tnt_minecart * 3
All * 0.2
@superbwarfare:small_cannon_shell * 5
@superbwarfare:cannon_shell * 5
minecraft:arrow * 1.5
minecraft:trident * 1.5
minecraft:mob_attack * 2.5
minecraft:mob_attack_no_aggro * 2
minecraft:mob_projectile * 1.5
minecraft:explosion * 6
minecraft:player_explosion * 6
superbwarfare:custom_explosion * 0.54
superbwarfare:projectile_explosion * 2
superbwarfare:mine * 0.7
superbwarfare:lunge_mine * 0.9
superbwarfare:projectile_hit * 1.35
@superbwarfare:small_cannon_shell * 0.0740740741
@superbwarfare:cannon_shell * 0.3703703704
superbwarfare:grapeshot_hit * 0.25
superbwarfare:laser * 1.25
@#superbwarfare:aerial_bomb * 3
@#superbwarfare:aa_missile * 0.5
#superbwarfare:projectile * 0.1
#superbwarfare:projectile_absolute * 0.7
#superbwarfare:vehicle_strike * 13
@superbwarfare:mortar_shell * 1.1
@superbwarfare:gun_grenade * 1.5
@superbwarfare:javelin_missile * 0.8
```

### fcp:gaz_tigr_gl
```
@superbwarfare:small_cannon_shell + 13
@superbwarfare:cannon_shell + 13
All - 13
minecraft:lava + 13
minecraft:lava * 10
@minecraft:tnt * 3
@minecraft:tnt_minecart * 3
All * 0.2
@superbwarfare:small_cannon_shell * 5
@superbwarfare:cannon_shell * 5
minecraft:arrow * 1.5
minecraft:trident * 1.5
minecraft:mob_attack * 2.5
minecraft:mob_attack_no_aggro * 2
minecraft:mob_projectile * 1.5
minecraft:explosion * 6
minecraft:player_explosion * 6
superbwarfare:custom_explosion * 0.54
superbwarfare:projectile_explosion * 2
superbwarfare:mine * 0.7
superbwarfare:lunge_mine * 0.9
superbwarfare:projectile_hit * 1.35
@superbwarfare:small_cannon_shell * 0.0740740741
@superbwarfare:cannon_shell * 0.3703703704
superbwarfare:grapeshot_hit * 0.25
superbwarfare:laser * 1.25
@#superbwarfare:aerial_bomb * 3
@#superbwarfare:aa_missile * 0.5
#superbwarfare:projectile * 0.1
#superbwarfare:projectile_absolute * 0.7
#superbwarfare:vehicle_strike * 13
@superbwarfare:mortar_shell * 1.1
@superbwarfare:gun_grenade * 1.5
@superbwarfare:javelin_missile * 0.8
```

### fcp:gaz_tigr_mg
```
@superbwarfare:small_cannon_shell + 13
@superbwarfare:cannon_shell + 13
All - 13
minecraft:lava + 13
minecraft:lava * 10
@minecraft:tnt * 3
@minecraft:tnt_minecart * 3
All * 0.2
@superbwarfare:small_cannon_shell * 5
@superbwarfare:cannon_shell * 5
minecraft:arrow * 1.5
minecraft:trident * 1.5
minecraft:mob_attack * 2.5
minecraft:mob_attack_no_aggro * 2
minecraft:mob_projectile * 1.5
minecraft:explosion * 6
minecraft:player_explosion * 6
superbwarfare:custom_explosion * 0.54
superbwarfare:projectile_explosion * 2
superbwarfare:mine * 0.7
superbwarfare:lunge_mine * 0.9
superbwarfare:projectile_hit * 1.35
@superbwarfare:small_cannon_shell * 0.0740740741
@superbwarfare:cannon_shell * 0.3703703704
superbwarfare:grapeshot_hit * 0.25
superbwarfare:laser * 1.25
@#superbwarfare:aerial_bomb * 3
@#superbwarfare:aa_missile * 0.5
#superbwarfare:projectile * 0.1
#superbwarfare:projectile_absolute * 0.7
#superbwarfare:vehicle_strike * 13
@superbwarfare:mortar_shell * 1.1
@superbwarfare:gun_grenade * 1.5
@superbwarfare:javelin_missile * 0.8
```

### fcp:gaz_tigr_rws
```
@superbwarfare:small_cannon_shell + 13
@superbwarfare:cannon_shell + 13
All - 13
minecraft:lava + 13
minecraft:lava * 10
@minecraft:tnt * 3
@minecraft:tnt_minecart * 3
All * 0.2
@superbwarfare:small_cannon_shell * 5
@superbwarfare:cannon_shell * 5
minecraft:arrow * 1.5
minecraft:trident * 1.5
minecraft:mob_attack * 2.5
minecraft:mob_attack_no_aggro * 2
minecraft:mob_projectile * 1.5
minecraft:explosion * 6
minecraft:player_explosion * 6
superbwarfare:custom_explosion * 0.54
superbwarfare:projectile_explosion * 2
superbwarfare:mine * 0.7
superbwarfare:lunge_mine * 0.9
superbwarfare:projectile_hit * 1.35
@superbwarfare:small_cannon_shell * 0.0740740741
@superbwarfare:cannon_shell * 0.3703703704
superbwarfare:grapeshot_hit * 0.25
superbwarfare:laser * 1.25
@#superbwarfare:aerial_bomb * 3
@#superbwarfare:aa_missile * 0.5
#superbwarfare:projectile * 0.1
#superbwarfare:projectile_absolute * 0.7
#superbwarfare:vehicle_strike * 13
@superbwarfare:mortar_shell * 1.1
@superbwarfare:gun_grenade * 1.5
@superbwarfare:javelin_missile * 0.8
```

### fcp:matv
```
@superbwarfare:small_cannon_shell + 13
@superbwarfare:cannon_shell + 13
All - 13
minecraft:lava + 13
minecraft:lava * 10
@minecraft:tnt * 3
@minecraft:tnt_minecart * 3
All * 0.2
@superbwarfare:small_cannon_shell * 5
@superbwarfare:cannon_shell * 5
minecraft:arrow * 1.5
minecraft:trident * 1.5
minecraft:mob_attack * 2.5
minecraft:mob_attack_no_aggro * 2
minecraft:mob_projectile * 1.5
minecraft:explosion * 6
minecraft:player_explosion * 6
superbwarfare:custom_explosion * 0.54
superbwarfare:projectile_explosion * 2
superbwarfare:mine * 0.7
superbwarfare:lunge_mine * 0.9
superbwarfare:projectile_hit * 1.35
@superbwarfare:small_cannon_shell * 0.0740740741
@superbwarfare:cannon_shell * 0.3703703704
superbwarfare:grapeshot_hit * 0.25
superbwarfare:laser * 1.25
@#superbwarfare:aerial_bomb * 3
@#superbwarfare:aa_missile * 0.5
#superbwarfare:projectile * 0.1
#superbwarfare:projectile_absolute * 0.7
#superbwarfare:vehicle_strike * 13
@superbwarfare:mortar_shell * 1.1
@superbwarfare:gun_grenade * 1.5
@superbwarfare:javelin_missile * 0.8
```

### fcp:matv_9in1
```
@superbwarfare:small_cannon_shell + 13
@superbwarfare:cannon_shell + 13
All - 13
minecraft:lava + 13
minecraft:lava * 10
@minecraft:tnt * 3
@minecraft:tnt_minecart * 3
All * 0.2
@superbwarfare:small_cannon_shell * 5
@superbwarfare:cannon_shell * 5
minecraft:arrow * 1.5
minecraft:trident * 1.5
minecraft:mob_attack * 2.5
minecraft:mob_attack_no_aggro * 2
minecraft:mob_projectile * 1.5
minecraft:explosion * 6
minecraft:player_explosion * 6
superbwarfare:custom_explosion * 0.54
superbwarfare:projectile_explosion * 2
superbwarfare:mine * 0.7
superbwarfare:lunge_mine * 0.9
superbwarfare:projectile_hit * 1.35
@superbwarfare:small_cannon_shell * 0.0740740741
@superbwarfare:cannon_shell * 0.3703703704
superbwarfare:grapeshot_hit * 0.25
superbwarfare:laser * 1.25
@#superbwarfare:aerial_bomb * 3
@#superbwarfare:aa_missile * 0.5
#superbwarfare:projectile * 0.1
#superbwarfare:projectile_absolute * 0.7
#superbwarfare:vehicle_strike * 13
@superbwarfare:mortar_shell * 1.1
@superbwarfare:gun_grenade * 1.5
@superbwarfare:javelin_missile * 0.8
```

### fcp:matv_crow
```
@superbwarfare:small_cannon_shell + 13
@superbwarfare:cannon_shell + 13
All - 13
minecraft:lava + 13
minecraft:lava * 10
@minecraft:tnt * 3
@minecraft:tnt_minecart * 3
All * 0.2
@superbwarfare:small_cannon_shell * 5
@superbwarfare:cannon_shell * 5
minecraft:arrow * 1.5
minecraft:trident * 1.5
minecraft:mob_attack * 2.5
minecraft:mob_attack_no_aggro * 2
minecraft:mob_projectile * 1.5
minecraft:explosion * 6
minecraft:player_explosion * 6
superbwarfare:custom_explosion * 0.54
superbwarfare:projectile_explosion * 2
superbwarfare:mine * 0.7
superbwarfare:lunge_mine * 0.9
superbwarfare:projectile_hit * 1.35
@superbwarfare:small_cannon_shell * 0.0740740741
@superbwarfare:cannon_shell * 0.3703703704
superbwarfare:grapeshot_hit * 0.25
superbwarfare:laser * 1.25
@#superbwarfare:aerial_bomb * 3
@#superbwarfare:aa_missile * 0.5
#superbwarfare:projectile * 0.1
#superbwarfare:projectile_absolute * 0.7
#superbwarfare:vehicle_strike * 13
@superbwarfare:mortar_shell * 1.1
@superbwarfare:gun_grenade * 1.5
@superbwarfare:javelin_missile * 0.8
```

### fcp:matv_tow
```
@superbwarfare:small_cannon_shell + 13
@superbwarfare:cannon_shell + 13
All - 13
minecraft:lava + 13
minecraft:lava * 10
@minecraft:tnt * 3
@minecraft:tnt_minecart * 3
All * 0.2
@superbwarfare:small_cannon_shell * 5
@superbwarfare:cannon_shell * 5
minecraft:arrow * 1.5
minecraft:trident * 1.5
minecraft:mob_attack * 2.5
minecraft:mob_attack_no_aggro * 2
minecraft:mob_projectile * 1.5
minecraft:explosion * 6
minecraft:player_explosion * 6
superbwarfare:custom_explosion * 0.54
superbwarfare:projectile_explosion * 2
superbwarfare:mine * 0.7
superbwarfare:lunge_mine * 0.9
superbwarfare:projectile_hit * 1.35
@superbwarfare:small_cannon_shell * 0.0740740741
@superbwarfare:cannon_shell * 0.3703703704
superbwarfare:grapeshot_hit * 0.25
superbwarfare:laser * 1.25
@#superbwarfare:aerial_bomb * 3
@#superbwarfare:aa_missile * 0.5
#superbwarfare:projectile * 0.1
#superbwarfare:projectile_absolute * 0.7
#superbwarfare:vehicle_strike * 13
@superbwarfare:mortar_shell * 1.1
@superbwarfare:gun_grenade * 1.5
@superbwarfare:javelin_missile * 0.8
```

### fcp:stryker_dragoon
```
@superbwarfare:small_cannon_shell + 13
@superbwarfare:cannon_shell + 13
All - 13
minecraft:lava + 13
minecraft:lava * 10
@minecraft:tnt * 3
@minecraft:tnt_minecart * 3
All * 0.2
@superbwarfare:small_cannon_shell * 5
@superbwarfare:cannon_shell * 5
minecraft:arrow * 1.5
minecraft:trident * 1.5
minecraft:mob_attack * 2.5
minecraft:mob_attack_no_aggro * 2
minecraft:mob_projectile * 1.5
minecraft:explosion * 6
minecraft:player_explosion * 6
superbwarfare:custom_explosion * 0.54
superbwarfare:projectile_explosion * 2
superbwarfare:mine * 0.7
superbwarfare:lunge_mine * 0.9
superbwarfare:projectile_hit * 1.35
@superbwarfare:small_cannon_shell * 0.0740740741
@superbwarfare:cannon_shell * 0.3703703704
superbwarfare:grapeshot_hit * 0.25
superbwarfare:laser * 1.25
@#superbwarfare:aerial_bomb * 3
@#superbwarfare:aa_missile * 0.5
#superbwarfare:projectile * 0.1
#superbwarfare:projectile_absolute * 0.7
#superbwarfare:vehicle_strike * 13
@superbwarfare:mortar_shell * 1.1
@superbwarfare:gun_grenade * 1.5
@superbwarfare:javelin_missile * 0.8
```

### fcp:stryker_m2
```
@superbwarfare:small_cannon_shell + 13
@superbwarfare:cannon_shell + 13
All - 13
minecraft:lava + 13
minecraft:lava * 10
@minecraft:tnt * 3
@minecraft:tnt_minecart * 3
All * 0.2
@superbwarfare:small_cannon_shell * 5
@superbwarfare:cannon_shell * 5
minecraft:arrow * 1.5
minecraft:trident * 1.5
minecraft:mob_attack * 2.5
minecraft:mob_attack_no_aggro * 2
minecraft:mob_projectile * 1.5
minecraft:explosion * 6
minecraft:player_explosion * 6
superbwarfare:custom_explosion * 0.54
superbwarfare:projectile_explosion * 2
superbwarfare:mine * 0.7
superbwarfare:lunge_mine * 0.9
superbwarfare:projectile_hit * 1.35
@superbwarfare:small_cannon_shell * 0.0740740741
@superbwarfare:cannon_shell * 0.3703703704
superbwarfare:grapeshot_hit * 0.25
superbwarfare:laser * 1.25
@#superbwarfare:aerial_bomb * 3
@#superbwarfare:aa_missile * 0.5
#superbwarfare:projectile * 0.1
#superbwarfare:projectile_absolute * 0.7
#superbwarfare:vehicle_strike * 13
@superbwarfare:mortar_shell * 1.1
@superbwarfare:gun_grenade * 1.5
@superbwarfare:javelin_missile * 0.8
```

### fcp:stryker_mgs
```
@superbwarfare:small_cannon_shell + 13
@superbwarfare:cannon_shell + 13
All - 13
minecraft:lava + 13
minecraft:lava * 10
@minecraft:tnt * 3
@minecraft:tnt_minecart * 3
All * 0.2
@superbwarfare:small_cannon_shell * 5
@superbwarfare:cannon_shell * 5
minecraft:arrow * 1.5
minecraft:trident * 1.5
minecraft:mob_attack * 2.5
minecraft:mob_attack_no_aggro * 2
minecraft:mob_projectile * 1.5
minecraft:explosion * 6
minecraft:player_explosion * 6
superbwarfare:custom_explosion * 0.54
superbwarfare:projectile_explosion * 2
superbwarfare:mine * 0.7
superbwarfare:lunge_mine * 0.9
superbwarfare:projectile_hit * 1.35
@superbwarfare:small_cannon_shell * 0.0740740741
@superbwarfare:cannon_shell * 0.3703703704
superbwarfare:grapeshot_hit * 0.25
superbwarfare:laser * 1.25
@#superbwarfare:aerial_bomb * 3
@#superbwarfare:aa_missile * 0.5
#superbwarfare:projectile * 0.1
#superbwarfare:projectile_absolute * 0.7
#superbwarfare:vehicle_strike * 13
@superbwarfare:mortar_shell * 1.1
@superbwarfare:gun_grenade * 1.5
@superbwarfare:javelin_missile * 0.8
```

### fcp:stryker_mortar
```
@superbwarfare:small_cannon_shell + 13
@superbwarfare:cannon_shell + 13
All - 13
minecraft:lava + 13
minecraft:lava * 10
@minecraft:tnt * 3
@minecraft:tnt_minecart * 3
All * 0.2
@superbwarfare:small_cannon_shell * 5
@superbwarfare:cannon_shell * 5
minecraft:arrow * 1.5
minecraft:trident * 1.5
minecraft:mob_attack * 2.5
minecraft:mob_attack_no_aggro * 2
minecraft:mob_projectile * 1.5
minecraft:explosion * 6
minecraft:player_explosion * 6
superbwarfare:custom_explosion * 0.54
superbwarfare:projectile_explosion * 2
superbwarfare:mine * 0.7
superbwarfare:lunge_mine * 0.9
superbwarfare:projectile_hit * 1.35
@superbwarfare:small_cannon_shell * 0.0740740741
@superbwarfare:cannon_shell * 0.3703703704
superbwarfare:grapeshot_hit * 0.25
superbwarfare:laser * 1.25
@#superbwarfare:aerial_bomb * 3
@#superbwarfare:aa_missile * 0.5
#superbwarfare:projectile * 0.1
#superbwarfare:projectile_absolute * 0.7
#superbwarfare:vehicle_strike * 13
@superbwarfare:mortar_shell * 1.1
@superbwarfare:gun_grenade * 1.5
@superbwarfare:javelin_missile * 0.8
```

### fcp:t72av
```
minecraft:lava - -11
minecraft:lava * 10
All * 0.2
minecraft:arrow * 1.5
minecraft:trident * 1.5
minecraft:mob_attack * 2.5
minecraft:mob_attack_no_aggro * 2
minecraft:mob_projectile * 1.5
minecraft:explosion * 6
minecraft:player_explosion * 6
superbwarfare:custom_explosion * 2.0
superbwarfare:projectile_explosion * 2
superbwarfare:mine * 0.75
superbwarfare:projectile_hit * 1.25
superbwarfare:grapeshot_hit * 0.3
superbwarfare:laser * 1.25
@#superbwarfare:aerial_bomb * 3
#superbwarfare:projectile * 0.25
#superbwarfare:projectile_absolute * 0.85
superbwarfare:vehicle_strike * 4
@superbwarfare:mortar_shell * 1.25
@superbwarfare:gun_grenade * 1.5
@superbwarfare:javelin_missile * 0.8
@superbwarfare:igla_9k38_missile * 0.75
@superbwarfare:wire_guide_missile * 1
All - 11
```
