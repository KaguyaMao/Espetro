# 载具方向抗性数据改动记录（2026-09-27）

配合 GScode 的 `VehicleDirectionArmorMixin`（让 `$entity.getSourceAngle(...)` 真正生效），
完成审计报告 1~4 项数据改动。审计背景见 `SERVER_VEHICLE_DIRECTION_DATA_AUDIT.md`。

## 1. 目标档位与实现方式（关键）
引擎语义（`DirectionArmorRule` + `VehicleDirectionArmor`）：
- 每条 `$entity.getSourceAngle(source, m) * damage` 规则给出 `max(1 - m·dot, 0.5)`，`dot` = 正面 +1 / 侧面 0 / 背面 -1；
- **同一辆车的所有规则连乘**；规则尾巴可带 `* (entity.getHealth() > T ? A : B)`，
  取 `T = -1`（恒真）即等于一个**常数因子 A**。

**最终档位：正面 0.5 / 侧面 1.1 / 背面 1.5**（每辆车写入 3 条规则）：

```
$entity.getSourceAngle(source, -0.3164163132) * damage
$entity.getSourceAngle(source, 0.4122722554) * damage
$entity.getSourceAngle(source, 0.4125) * damage * (entity.getHealth() > -1 ? 1.1 : 1.1)
```

数学：`Π(1-m) = 0.45454545 × 1.1 = 0.5`；`Π(1+m) = 1.36363636 × 1.1 = 1.5`；侧面 `1 × 1.1 = 1.1`。
离线复核（`build/tmp/sim-dirarmor.mjs`，用加载器同款正则，**逐文件把同车规则连乘**）：
27 个目标文件全部 `正面 0.50000 | 侧面 1.10000 | 背面 1.50000`。

> 为什么必须 3 条：侧面恒为 1，要变成 1.1 只能乘常数；而「正面 0.5 + 背面 1.5 + 常数 1.1」在单条规则下无解
> （单条会得到 0.70/1.1/1.5），两条也无解（受 0.5 下限夹紧限制），三条才有解（求解器 `solve-dirarmor.mjs`）。
> 备选：单条 `m = 0.5` → 0.5 / 1.0 / 1.5（侧面回不到 1.1），一条命令即可切换。

## 2. 改动清单（28 个文件）
- **套用新档位（27 个）**
  - 覆盖里补上规则（原本被 kubejs 覆盖遮掉 jar 规则）：`bmp3`、`m1126`、`m1128`、`m1296`、
    `m1a2sepv1`、`m1a2sepv2`、`m3a3`、`t90mh`、`zbd04a`、`zbl08`、`zlt11`、`ztz99a`、`sx1`
  - 原本完全没有规则：`m113`、`t72b3`、`zbd05`、`zsl10`、`ztd05`、`z20`、`uh60`\*、
    `fcp:bmp1am`、`fcp:btr80`、`fcp:btr82`、`vvp:mi_8`\*、`superbwarfare:drone`\*
  - 替换旧值：`ztz96a`（0.35）、`superbwarfare:annihilator`（原 m=3，正面触底 0.5 / 背面 ×4）
  - \* = 服务端原本没有覆盖文件，**新建**（内容取自对应 jar，仅加规则）
- **卡车 / 吉普：按用户要求不套新档位**（判定依据：编制 `vehicles{}` 的槽位 `truck`/`supply_truck`/`car`）
  `mv3_armed`（CTM-131 武装卡车）、`mv3_supply`、`ural4320_supply`（乌拉尔4320）、
  `fcp:gaz_tigr_rws`（虎式）、`fcp:kamaz`（M939）、`fcp:matv_crow`（MATV）—— 保持原样（无方向规则）。
- `csk181`（CSK-181 东风猛士，属被遮蔽的吉普）：只把 **jar 原值**那一行补回（m=0.05 → 0.95/1.00/1.05），不套新档位。

## 3. 附带的坏 JSON 修复
`kubejs/data/dragonrise_reforge/sbw/vehicles/ztz99a.json` 第 174 行尾随逗号已删除，
文件恢复严格合法，服务端不再出现 `Couldn't parse data file dragonrise_reforge:ztz99a`。

## 4. 部署与验证
1. 改前 24 个覆盖文件已二进制备份（真正的原状）：`build/tmp/dirarmor/before-bin/`；
   4 个新建覆盖文件回滚 = 删除。
2. 上传 28 个文件，回下载逐文件 SHA256 比对：**28/28 一致**（`build/tmp/deploy-dirarmor2.log`）。
3. 生成物完整性校验（严格 JSON、方向规则与目标档位一致、其余条目与顶层字段未变）：**28/28 通过**。
4. `/reload` 后服务端日志：`载具方向抗性：已加载 122 辆载具共 176 条 $ 脚本修正`
   （94 辆旧规则 + 27 辆 × 3 条 + `csk181` × 1 条 = 176，逐条吻合），且不再出现 ztz99a 解析失败。
5. 重启后（08:50）再次确认：`Done (2.759s)`、启动日志同为 **122 辆 / 176 条**、
   启动段**无** `Couldn't parse data file dragonrise_reforge:ztz99a`、KubeJS `9/9 server scripts, 0 errors`。
   → `ztz99a` 被遮蔽的自定义载具数据也已恢复。

## 5. 回滚点
- 原状覆盖文件（二进制）：`build/tmp/dirarmor/before-bin/`（24 个）
- 新建覆盖文件（删除即回滚）：`dragonrise_reforge:uh60`、`vvp:mi_8`、`superbwarfare:drone`、`superbwarfare:annihilator`
- 中间版本（上一轮 1.3/0.9/0.5）与生成清单：`build/tmp/dirarmor/`、`build/tmp/dirarmor2/`
- 数据来源快照：`build/tmp/vehdata/`（服务端 kubejs）、`build/tmp/vehjars/`（5 个 jar 内置 301 个载具 JSON）

## 6. 未覆盖范围与遗留问题
- 只改「编制实际出场」的载具；jar 内置其余非出场载具（含 22 架飞机的血量门写法）仍是旧参数。
  要统一需改 GScode 的 `src/main/resources/**/sbw/vehicles/` 并重新构建 jar，或逐个新建覆盖文件（会遮蔽 jar）。
- **坏引用**：编制 `plamc_5th.json` / `plamc_5th_at.json` 引用了 `dragonrise_reforge:zsd05`，
  但 70 个 jar 的条目名、dragonrise 全部 class 常量池、kubejs 里都没有该 id（无数据文件、无实体注册）→
  该槽位生成不了载具。
- 规则只在**有攻击者实体**时生效：炮击 / 无主爆炸不吃方向倍率。
- 侧面 1.1 依赖「恒真血量门」当常数因子；若以后清理血量门逻辑需改用单条 `m=0.5`（0.5/1.0/1.5）或扩展加载器语法。

## 7. 相关脚本
`list-faction-vehicles.mjs`（槽位判定）、`solve-dirarmor.mjs`（档位求解）、`apply-dirarmor-profile.mjs`（生成，支持多规则/多档位）、
`verify-dirarmor.mjs`（完整性校验）、`sim-dirarmor.mjs`（载具层面倍率复核）、`deploy-dirarmor.mjs`（备份→建目录→上传→回下载校验）。
