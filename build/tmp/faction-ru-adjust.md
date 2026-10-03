# 俄军两项调整（已生效）

时间：2026-09-24（13:50:42 重启后生效）
范围：`ru_205th` / `ru_3th` / `ru_49th` / `ru_6th_tank` 四个俄军编制

## ① 侦察兵移到 row4 第二位

- 改动：`*_SCOUT` 的 `row` 由 **3 → 4**，并在 JSON 中把它移到 `*_HEAVYANTITANK` 之后。
- 依据：客户端 `UnifiedDeployScreen` 按 `cls.row` 分组，**组内顺序 = 数据里的先后顺序**
  （`for (var cls : classes) rows.get(cls.row).add(cls)`），所以调整 JSON 顺序即可控制显示位置。

调整后 row4 的渲染顺序（四个俄军编制一致）：

| 位置 | 职业 |
|---|---|
| 1 | 重型反坦克兵 |
| **2** | **侦察兵**（本次移动） |
| 3 | 通用机枪手 |
| 4 | 战斗工兵 |

## ② 精确射手瞄具换成 huinuo:pso1

- 改动：俄军精确射手（`*_MARKSMAN`）主武器 `cib:svd` 的
  `AttachmentSCOPE ... AttachmentId:"huinuo:pso1e"` → **`huinuo:pso1`**，共 4 处（每编制 1 处）。
- 已核实 `huinuo:pso1` 存在于瞄具包（`data/huinuo/index/attachments/pso1.json`、display/geo/texture 齐全；
  `pso1e` 是另一个变体）。
- 除精确射手外，四个俄军编制里没有其它职业使用 `pso1e`（已全文件扫描确认 0 残留）。

## 校验

- 加载器规则体检（`lint-factions.mjs fix2-`）通过：变体上限≥1、`strict_count` 上限总和一致、
  `resupply.items` 非空且 ≤64。
- 断言校验通过：仅 `classes` 变更（其余字段逐字节一致）、职业数量不变（RU 各 14）、
  侦察兵 `row=4` 且位于 row4 第二位、精确射手瞄具为 `pso1` 且全文件无 `pso1e`。
- 4 个文件服务端二进制回读 **SHA256 与本地一致**。

## 回滚

- 服务端：`EsFactions/<名>.json.bak-20260924-ru-scout`（4 个，= 上一版）
- 本地：`build/tmp/EsFactions-backup-before-20260924-ru-scout/`
- 脚本：`apply-ru-adjust.mjs`、`verify-ru-adjust.mjs`、`ru-adjust-report.json`

## 待办

- ~~需完整重启~~ **已于 13:50:42 重启生效**：
  `Done (3.466s)`、`EsFactions 已冻结: 12 个编制, 172 个职业`（无拒载）、
  `工事 JSON v2 已事务冻结: global=10 maps=3 aliases=12 策略剔除=0 instant=[espetro:radio]`、
  `战场地图启动准备: status=READY prepared=3 warnings=0 error=null`；
  各编制职业数 PLA 15×4 / RU 14×4 / US 14×4。
