# 高爆弹药 ApDurability 归零（kubejs 载具）

日期：2026-10-25
需求（m02755）：把 kubejs 中所有载具的高爆弹药 `ApDurability` 全部改为 0，没有该字段的补上。

## 1. 字段语义（反编译实证）

`superbwarfare` jar：`[卓越前线] superbwarfare-0.8.9.1-hotfix-mc1.20.1-993063bed-all.jar`

- `com/atsuishio/superbwarfare/data/gun/DefaultGunData`：`private int apDurability;`（Gson `@SerializedName`，默认 0）。
- 开火时（`GunItem.tick`）只有**弹种为 AP**（`ShellType == "AP"`）才会把 `AP_DURABILITY` 写进 `CannonShellEntity.durability`；HE 弹不吃这个字段。
- 命中载具时该值用于扣除载具「部件耐久 / 装甲耐久」，即**穿透性破坏**。把它设为 0 = 高爆弹只做爆炸伤害，不再穿透掉耐久。

## 2. 识别规则

- A) `Weapons.<名>.AmmoType` 数组里，覆盖项 `{ "Ammo": "..._he", "Override": {...} }` → 在 `Override` 内写 `"ApDurability": 0`。
- B) 武器基础弹药本身就是高爆：`AmmoType[0]` 或 `AmmoType` 字符串为 `*_he`，或该武器 `IsHighExplosiveProjectile: true` → 在武器层级写 `"ApDurability": 0`。

## 3. 改动结果

24 个文件、26 处（23 个 HE 覆盖项 + 3 个武器层级）：

| 文件 | 武器::弹药 |
| --- | --- |
| dragonrise_reforge/bmp3.json | 100MM_Cannon（基础 HE）、Cannon::small_shell_he |
| dragonrise_reforge/zbd04a.json | 100MM_Cannon（基础 HE）、Cannon::small_shell_he |
| dragonrise_reforge/m1128.json | Cannon::large_shell_he |
| dragonrise_reforge/m1296.json | Cannon::small_shell_he |
| dragonrise_reforge/m1a2sepv1.json | Cannon::large_shell_he |
| dragonrise_reforge/m1a2sepv2.json | Cannon::large_shell_he |
| dragonrise_reforge/m3a3.json | Cannon::small_shell_he |
| dragonrise_reforge/t72b3.json | Cannon::large_shell_he |
| dragonrise_reforge/t90mh.json | Cannon::large_shell_he |
| dragonrise_reforge/zbd05.json | Cannon::small_shell_he |
| dragonrise_reforge/zbl08.json | Cannon::small_shell_he |
| dragonrise_reforge/zlt11.json | Cannon::large_shell_he |
| dragonrise_reforge/ztd05.json | Cannon::large_shell_he |
| dragonrise_reforge/ztz96a.json | Cannon::large_shell_he |
| dragonrise_reforge/ztz99a.json | Cannon::large_shell_he |
| fcp/bmp1am.json | Cannon::small_shell_he |
| fcp/bmp2.json | Cannon::small_shell_he |
| fcp/bmp2d.json | Cannon::small_shell_he |
| fcp/bmp2m.json | Cannon::small_shell_he |
| fcp/btr82.json | Cannon::small_shell_he |
| fcp/gaz_tigr_gl.json | Grenade（32mm 榴弹，HE 标记） |
| fcp/stryker_dragoon.json | Cannon::small_shell_he |
| fcp/stryker_mgs.json | Cannon::large_shell_he |
| fcp/t72av.json | Cannon::small_shell_he |

校验：60 个文件全量 JSON 解析通过；除新增 `ApDurability` 外与原文件深度相等（`verify-apdur.mjs` 输出 `deepDiff=0 ApDurability0=26`）。
上传：24/24 OK，远端目录大小与本地逐一对齐（`verify-sizes-dir.mjs` → `verified 24, mismatch 0`）。

## 4. 未处理（需用户决定是否算“高爆弹药”）

爆炸伤害 > 0 但没有置 0 的：反坦克导弹（`medium_anti_ground_missile`、`TOW`、`Konkurs`）、迫击炮（`fcp/stryker_mortar` :: `mortar_shell` exp=100）、无后坐力/火箭（`superbwarfare/annihilator` :: `FE`）、以及各类 AP 弹自带的少量溅射（5~160）。

## 5. 生效条件

`kubejs/data/*/sbw/vehicles/*.json` 只在**服务端启动**时加载（`/reload` 不重读），需重启服务器生效。

## 6. 工具

- `build/tmp/patch-apdur.mjs`：生成 `build/tmp/srvveh-apdur/`
- `build/tmp/verify-apdur.mjs`：深度比对
- `build/tmp/he-audit.mjs`：列出所有爆炸类弹药及是否置 0
- `build/tmp/upload-dir.mjs`、`build/tmp/verify-sizes-dir.mjs`
- 回滚点：`build/tmp/srvveh/`（改动前，已含上一次抗性重调）
