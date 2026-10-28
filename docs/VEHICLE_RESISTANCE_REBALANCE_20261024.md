# 载具抗性调整（2026-10-24）

## 需求
1. kubejs 覆盖层里的 10 台装甲吉普：血量统一 150；主炮 AP 直击 ×2.0；机炮直击 ×0.25；步枪弹 0。
2. kubejs 覆盖层所有载具的 `DamageModifiers` 中 `minecraft:explosion` 倍率统一改为 `0.5`。

## 涉及文件
- 服务端目录：`/kubejs/data/<ns>/sbw/vehicles/*.json`（60 个文件，kubejs 覆盖层）
- 本地快照：`build/tmp/srvveh/`（改前）
- 改后副本：`build/tmp/srvveh-fixed/`（上传内容）
- 工具：`build/tmp/pull-veh.mjs`（拉取）、`patch-veh.mjs`（改动+校验）、`simjeep.mjs`（承伤模拟）、`upload-many.mjs`（批量上传）、`verify-sizes.mjs`（上传后核对）

## 1. 十台装甲吉普
`dragonrise_reforge:csk181`、`dragonrise_reforge:sx1`、`fcp:matv`、`fcp:matv_9in1`、`fcp:matv_crow`、`fcp:matv_tow`、`fcp:gaz_tigr_gl`、`fcp:gaz_tigr_mg`、`fcp:gaz_tigr_rws`、`fcp:humveem2`

改动方式（外科式，不重写原有抗性链）：

| 项 | 改法 |
|---|---|
| `MaxHealth` | 全部 → `150` |
| 主炮 AP 直击 | 末尾追加 `@superbwarfare:cannon_shell * K`，K 由原值反解，使结果恰为 **200**（2.0） |
| 机炮直击 | 末尾追加 `@superbwarfare:small_cannon_shell * K`，使结果恰为 **25**（0.25） |
| 步枪弹 | 追加 `tacz:bullet 0`（IMMUNITY 条目，命中即 0） |

追加的 3 条插在第一行 `$` 方向装甲之前（`$` 行属 customList，必须最后执行）。

反解出的倍数：

| 载具 | 炮 × | 机炮 × |
|---|---:|---:|
| csk181 / matv ×4 / gaz_tigr ×3 | 4 | 2.5 |
| sx1 | 1.5625 | 0.173611 |
| humveem2 | 10 | 0.892857 |

验证（`simjeep.mjs`，基础 100 伤害）：10 台全部 = 200 / 25 / 0，血量 150。

## 2. `minecraft:explosion` → 0.5
- 52 个文件改到（原位替换数值、保留运算符），8 个文件原本没有该字段：`dragonrise_reforge:9m133`、`dshk`、`hj8`、`qjz89`、`sx1_a`、`zu23`、`superbwarfare:drone`、`superbwarfare:tow`。
- `minecraft:player_explosion` 未改（用户只点名 `minecraft:explosion`）。

## 3. 部署与生效
- 52/52 上传成功，服务端目录大小逐一核对一致（`verify-sizes.mjs`：size-ok 52, bad 0）。
- **kubejs 的 SBW 载具 JSON 只在服务端启动时读取**（此前实测：`/reload` 不重读），需重启生效。

## 4. 承伤对照（基础 100 伤害，越低越抗打）

| 载具 | 血量 | 主炮AP | 机炮 | 步枪弹 | 炮弹爆炸 | TNT |
|---|---:|---:|---:|---:|---:|---:|
| csk181 / matv ×4 / gaz_tigr ×3 | 150 | 200 | 25 | 0 | 9.4 | 26.1 |
| sx1 | 150 | 200 | 25 | 0 | 100 | 150 |
| humveem2 | 150 | 200 | 25 | 0 | 46 | 71 |

> TNT 列按 `minecraft:explosion` 0.5 计算；炮弹爆炸（`superbwarfare:custom_explosion`）未改，仍按各车原链。
