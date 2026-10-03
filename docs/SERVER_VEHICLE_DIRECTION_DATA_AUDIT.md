# 服务端载具方向抗性数据审计（2026-09-27）

针对 GScode 新增的 `VehicleDirectionArmorMixin` + `VehicleDamageModifierLoader`
（`$entity.getSourceAngle(source, m) * damage` 真正生效）做的服务端 data 全量核对。

## 1. 生效机制（读代码得出，供对照）
- 加载器：`SimpleJsonResourceReloadListener`，目录 `sbw/vehicles`，扫描**所有**命名空间（mod 内置 + kubejs 覆盖 + 数据包）。
- 只认 `DamageModifiers` 里**以 `$` 开头的字符串**，且必须含 `getSourceAngle(source, m)`；其余 `$` 条目只打一条 warning 后忽略。
- **键 = JSON 资源路径** `<命名空间>:<文件名>`，必须等于载具实体注册 id，否则永远查不到（静默失效）。
- 倍率：`max(1 - m·dot, 0.5)`，`dot` = 载具→攻击者单位向量 · 载具朝向（正面 +1 / 侧面 0 / 背面 -1）。
  即正面 `1-m`、侧面 `1`、背面 `1+m`；可选的 `* (entity.getHealth() > T ? A : B)` 会再乘一次。
- 触发条件：`source.getEntity()` 或 `getDirectEntity()` 非空；**无主爆炸不生效**。
- 位置：`VehicleEntity.hurt` 入口（`@ModifyVariable` HEAD），早于卓越前线自己的装甲/部件血量结算，倍率是与现有 DamageModifiers 相乘关系。

## 2. 运行时日志与离线复算一致（说明审计可信）
服务端今日重启（`dragonrise_reforge-1.5.0-beta-20260927.jar` 已部署，40673294 B）：

```
08:11:45 ERROR SimpleJsonResourceReloadListener: Couldn't parse data file dragonrise_reforge:ztz99a from dragonrise_reforge:sbw/vehicles/ztz99a.json
08:11:47 WARN  VehicleDamageModifierLoader: 不支持的 $ 伤害修正脚本，已忽略：$damage * (entity.getHealth() > 0.1 ? 0.7 : 0.05)（载具 superbwarfare:ah_6）
08:11:47 INFO  VehicleDamageModifierLoader: 载具方向抗性：已加载 96 辆载具共 96 条 $ 脚本修正（另有 1 条形式不支持，已忽略）
```

离线复算（`vehjars/` 抓取 5 个 mod jar 内置 301 个载具 JSON + `vehdata/` 抓取服务端 48 个 kubejs 覆盖文件，
按「kubejs 覆盖 > jar 内置」合成生效集）结果：**96 辆 / 96 条**，与日志逐字吻合；
`superbwarfare:ah_6` 那条不含 `getSourceAngle` 的 `$` 脚本也正是日志里被忽略的 1 条。

## 3. 核心问题：出场的 32 种载具里，只有 1 种真正吃到方向抗性

编制 JSON（15 个文件）里实际出场、且能在载具数据里找到的载具共 **32 种**：

| 分类 | 数量 | 说明 |
|---|---|---|
| ✅ 生效 | **1** | `dragonrise_reforge:ztz96a`（kubejs 覆盖里 m=0.35） |
| ❌ 覆盖把规则遮掉 | **13** | jar 里有 `$`，但同名 kubejs 覆盖文件没有 → 规则失效 |
| ❌ 两边都没有规则 | **18** | jar 与覆盖都没有 `$` → 需要新增 |

### 3.1 覆盖遮掉规则的 13 种（补回一行即可恢复）
`kubejs/data/dragonrise_reforge/sbw/vehicles/*.json` 覆盖了 jar 内置数据，覆盖文件里漏了 `$` 行：

| 载具 | jar 里的原值 |
|---|---|
| `dragonrise_reforge:bmp3` | `$entity.getSourceAngle(source, 0.25) * damage` |
| `dragonrise_reforge:csk181` | `$entity.getSourceAngle(source, 0.05) * damage` |
| `dragonrise_reforge:m1126` | `$entity.getSourceAngle(source, 0.25) * damage` |
| `dragonrise_reforge:m1128` | `$entity.getSourceAngle(source, 0.25) * damage` |
| `dragonrise_reforge:m1296` | `$entity.getSourceAngle(source, 0.25) * damage` |
| `dragonrise_reforge:m1a2sepv1` | `$entity.getSourceAngle(source, 0.3) * damage` |
| `dragonrise_reforge:m1a2sepv2` | `$entity.getSourceAngle(source, 0.3) * damage` |
| `dragonrise_reforge:m3a3` | `$entity.getSourceAngle(source, 0.25) * damage` |
| `dragonrise_reforge:t90mh` | `$entity.getSourceAngle(source, 0.3) * damage` |
| `dragonrise_reforge:zbd04a` | `$entity.getSourceAngle(source, 0.25) * damage` |
| `dragonrise_reforge:zbl08` | `$entity.getSourceAngle(source, 0.25) * damage` |
| `dragonrise_reforge:zlt11` | `$entity.getSourceAngle(source, 0.25) * damage` |
| `dragonrise_reforge:ztz99a` | `$entity.getSourceAngle(source, 0.3) * damage`（且覆盖文件本身损坏，见 3.3） |

（未出场但同样被遮掉的还有 `dragonrise_reforge:sx1`，jar 原值 0.25。）

### 3.2 两边都没有规则的 18 种（需新增）
`dragonrise_reforge:m113 / mv3_armed / mv3_supply / t72b3 / uh60 / ural4320_supply / z20 / zbd05 / zsl10 / ztd05`、
`fcp:bmp1am / btr80 / btr82 / gaz_tigr_rws / kamaz / matv_crow`、
`superbwarfare:drone`、`vvp:mi_8`。

> 说明：jar 内置的 301 个载具 JSON 里只有 95 个带 `$getSourceAngle`（其余含 fcp 64 个、vvp 44 个基本都没有），
> 所以「所有载具都有方向抗性」目前在数据上并不成立。若只保留现有覆盖面，则实际只有 96 种（多数不在出场列表里）。

### 3.3 严重：`ztz99a` 覆盖文件是坏 JSON，整份被跳过
`kubejs/data/dragonrise_reforge/sbw/vehicles/ztz99a.json`（13063 B，已二进制下载复核，非读取接口损坏）：

```json
173:       "Weapons": [
174:         "PassengerMachineGun",     ← 多余的尾随逗号
175:       ],
176:       "CanRotateHead": false,
```

严格 JSON 解析失败（`Unexpected token ']'`），服务端日志同步报
`Couldn't parse data file dragonrise_reforge:ztz99a`。**被跳过的后果不只是没有方向抗性**：
kubejs 覆盖遮掉了 jar 里的同名文件，文件被跳过后 `dragonrise_reforge:ztz99a` 这份自定义载具数据
（血量/部件/武器/抗性等 35 条修饰）实际全部没有生效，需要尽快修掉。
（其余 47 个覆盖文件严格可解析，只有这一个有问题。）

## 4. 参数不一致与异常值
生效的 96 条规则里 `m` 有 10 种取值：

| m | 数量 | 前向 ×/背向 × | 代表载具 |
|---|---|---|---|
| **3** | 1 | 0.5 / **4.0** | `superbwarfare:annihilator`（疑似笔误，正面已触底 0.5、背面 4 倍） |
| 0.35 | 1 | 0.65 / 1.35 | `dragonrise_reforge:ztz96a`（当前唯一生效的出场载具） |
| 0.32 | 1 | 0.68 / 1.32 | `dragonrise_reforge:t3485` |
| 0.3 | 40 | 0.70 / 1.30 | `m1a2sepv1/v2`、`t90mh`、`ztz99a`（被遮）等 |
| 0.28 | 1 | 0.72 / 1.28 | `dragonrise_reforge:churchill_vii` |
| 0.25 | 40 | 0.75 / 1.25 | `bmp3`、`zbd04a`、`zbl08`、`zlt11`、`m3a3` 等 |
| 0.2 | 3 | 0.80 / 1.20 | `ac130`、`maus`、`prism_tank` |
| 0.15 | 4 | 0.85 / 1.15 | `superbwarfare:bmp_2`、`lav_150/25/ad` |
| 0.1 | 1 | 0.90 / 1.10 | `superbwarfare:bl_132` |
| 0.05 | 4 | 0.95 / 1.05 | `humvee`、`motuo`、`toyota_seiki`、`wlsc`（`csk181` jar 值同为 0.05） |

22 架飞机使用了血量门写法，只有两种：
- `$entity.getSourceAngle(source, 0.2) * damage * (entity.getHealth() > 0.1 ? 0.5 : 0.05)`
- `$entity.getSourceAngle(source, 0.25) * damage * (entity.getHealth() > 0.1 ? 0.4 : 0.05)`

> 注意血量门比较的是**原始血量 > 0.1**（不是百分比），即「几乎归零」时才切到 0.05 倍。

## 5. 其它已知噪音（非新问题）
- `fcp:example_trailer`：mod 自带示例文件解析失败（日志同样报错），无实际载具引用。
- `superbwarfare:ah_6` 的 `$damage * (entity.getHealth() > 0.1 ? 0.7 : 0.05)` 不含 `getSourceAngle`，按设计被忽略。
- 规则只在有攻击者实体时生效：炮击/无主爆炸（如 155 炮击、TNT）不吃方向倍率。

## 6. 建议动作（按优先级）
1. **修 `ztz99a` 覆盖文件的尾随逗号**（1 处），否则该车所有自定义数据都没加载。
2. **给 3.1 的 13 个覆盖文件补回 jar 里的 `$` 行**（各 1 行，沿用 jar 的 m 值），出场主战载具立刻恢复方向抗性。
3. 决定是否给 3.2 的 18 种也新增（以及统一用哪个 m；可沿用「主战 0.3 / 步战·轮式 0.25 / 轻卡 0.05」的现有档位）。
4. 复核 `superbwarfare:annihilator` 的 `m=3`，以及是否要把出场载具的 m 值统一（现状 0.05~0.35 共 10 档）。

## 7. 审计工具与产物
- `build/tmp/audit-veh-direction-data.mjs` → 抓服务端 48 个 kubejs 载具 JSON（`build/tmp/vehdata/`）
- `build/tmp/extract-veh-json.ps1`（内联执行）→ 从 5 个 mod jar 抽出 301 个载具 JSON（`build/tmp/vehjars/`）
- `build/tmp/audit-veh-rules-effective.mjs` → 合成生效集（`build/tmp/veh-rules-effective.json`），复算出 96/96
- `build/tmp/report-veh-rules.mjs`、`report-inplay-veh-rules.mjs`、`classify-inplay-veh.mjs` → 出场载具定性（`inplay-veh-classify.json`）
