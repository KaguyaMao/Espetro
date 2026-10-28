# 反坦克 / 榴弹武器改动（2026-10-25）

需求来源：用户 2026-10-25 22:0x 的三条指令（`m03008` 清单 + `m03065` 追问的回答 + `m03066`）。

## 1. 数值（`kubejs/server_scripts/granade.js`、`m320.js`、`qlz87.js`）

所有“伤害”都是 `setDamage()`，命中 M1A2SEPv1 **侧甲**时经过抗性链
`(D-20)×0.2×1.3(superbwarfare:projectile_hit)×1.1(@#superbwarfare:at_rocket)×1.1(侧向装甲)`：

| 武器 | 旧 | 新 | 效果（侧甲 500 血） |
|---|---|---|---|
| `ts:gustavm4` + `ts:84mm_ffv751` | 700 | **523** | 3 发剩余 5.05%（25.3 血） |
| `suffuse:pf98a` | 400 | **523** | 同上 |
| `murasamet:rpg7_pg7vr_tandem_heat` | 650 | **523** | 同上 |
| `ts:at4` | 350 | **285** | 6 发击毁（500.5 伤害） |
| `cib:dzj08` | 350 | **285** | 同上 |
| `murasamet:rpg7_pg7heat` | 350 | **285** | 同上 |

反解方式见 `build/tmp/calc-at.mjs`（枚举整数 D 求 3 发≈95% / 6 发≥100% 的最近值）。

初速：

| 武器 | 旧 | 新 |
|---|---|---|
| `tacz:m320` 枪榴弹 | `50/20` = 2.5 格/t | **`150/20` = 7.5 格/t** |
| `ts:gp25` 枪榴弹（`m320.js`） | `50/20` | **`150/20`** |
| `suffuse:qlz87` 榴弹发射器（`qlz87.js`） | 继承子弹速度（约 9.5 格/t） | **固定 10 格/t**（`200/20`，按方向归一化后重设） |

`cib:qlu11`（未进编制）：初速 22.5→10 格/t、伤害 35→25、爆炸半径 7→3。
`qlz87.js` 另外补上 `setDamage(25)`、爆炸半径 4→3。

## 2. 编制（13 个 `EsFactions/*.json`）

改造脚本 `build/tmp/apply-at.mjs`，核对脚本 `build/tmp/verify-at.mjs`。

| 阵营 | 职业 | 改动 |
|---|---|---|
| 美（4 个编制） | `*ANTITANK` 轻筒 | `ts:at4` ×1 → **×2**；补给 count/max 1→2 |
| 中（5 个编制） | `*ANTITANK` 轻筒 | `cib:dzj08` ×1 → **×2**；补给 count/max 1→2 |
| 美（4 个） | `*HEAVYANTITANK` 重筒 | 移除 `ffv551`×2 / `ffv441`×2，`ffv751` → **3 发**；补给同上改为 3/3 |
| 中（5 个） | `*HEAVYANTITANK` 重筒 | `suffuse:120mm` 备弹统一 **3 发**（default 原为 3、光瞄原为 2）；补给 3/3 |
| 俄（4 个） | `*HEAVYANTITANK` 重筒 | 删除 `rpg7_og7he`、`rpg7_pg7heat` 两把筒与 `pg7heat` 弹药；只留 **1 把串联筒 + 3 发串联破甲弹**；补给只剩串联弹 3/3 |
| 俄 | `*ANTITANK` 轻筒 | **不动**（用户：只改重筒） |

**重筒严格三发**（用户 2026-10-25 追加要求）：所有重筒（美/中/俄）的枪械命令统一改为
`GunCurrentAmmoCount:0` + `HasBulletInBarrel:0b`，即膛内清空，背包里正好 **3 发备弹** →
装填 1 发打 1 发，总共 3 发。这样首发也是破甲弹（否则美制 gustav 膛内那发会是默认弹种，伤害只按 500 算）。
脚本：`build/tmp/patch-heavy3.mjs`，核对：`build/tmp/verify-heavy.mjs`。

## 3. 部署状态

- 3 个脚本 + 13 个编制文件已上传服务端，大小逐一核对一致。
- **服务端当前是停止状态**（面板 `/api/instance` → `status: 0`，`started: 21`），所以改动尚未生效；
  编制在启动时冻结，下次启动即生效（脚本同理）。
- 回滚点：`build/tmp/factions-before-at/`（13 个编制原文）、`build/tmp/scripts-before-at/`（3 个脚本原文）。
