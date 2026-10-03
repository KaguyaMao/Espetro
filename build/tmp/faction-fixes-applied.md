# 编制修复 7 项 —— 已上线

时间：2026-09-24（两次重启，最终 13:31:21 生效）
范围：服务端 `EsFactions/` 12 个启用编制
状态：**已生效**（本次重启同时生效：上一轮"盔甲韧性归零" + 本次 7 项 + 协议 1.35 的新 jar）

## 首次重启事故（已修复）

第一次重启后日志出现：

```
[编制拒载] us_1th_ar.json: 职业 US_1th_arma_SQUADMG (strict_count=true) 的变体上限总和 1 不等于职业上限 2。该编制不会载入
→ EsFactions 已冻结: 8 个编制, 116 个职业   （4 个美军编制全部被拒）
```

**原因**：`FactionDataLoader` 的校验规则 —— `strict_count=true` 的职业，
**Σ(各变体 maxPlayers) 必须等于职业 maxPlayers**。原来 US 班组机枪有 2 个变体、每个上限 1，合计 2 ✓；
我按 ③ 把 row2 裁成只剩 1 个变体后合计变成 1 ≠ 2 → 整个编制被拒载。

**修复**：③ 裁剪 row2 后，若 `strict_count=true`，把剩余变体的上限对齐职业上限（1 → 2）。
同时新增离线体检脚本 `lint-factions.mjs`，复刻加载器的三条拒绝规则
（变体上限≥1、`strict_count` 上限总和一致、`resupply.items` 非空且 ≤64）——这类问题下次能在上传前抓到。

第二次重启：`EsFactions 已冻结: 12 个编制, 172 个职业` ✓（PLA 15×4、RU 14×4、US 14×4），无任何拒载警告。

## 最终验证

- `EsFactions 已冻结: 12 个编制, 172 个职业`；`Espetro 启动配置已冻结：3 个可用地图，12 个 EsFactions 编制`
- 战场启动准备 `status=READY prepared=3 warnings=0 error=null`
- 工事 registry `global=10 maps=3 aliases=12 策略剔除=0 instant=[espetro:radio]`
- 控制台实测 `/espetro freeunlock on|off|status`：日志
  `[FreeUnlock] Server 将装备完全解锁模式设为 true/false（仅本局有效）` ✓ 且测试后已关回关闭
- 12 个文件与服务器回读 SHA256 一致；逐项断言与加载器规则体检全部通过

## 执行结果（脚本自检 + 逐项校验全部通过）

| # | 项目 | 实际改动 |
|---|---|---|
| ① | 美军 M4A1 全自动 | `GunFireMode:"BURST"→"AUTO"` 共 **80 处**（4 编制 × 20） |
| ② | 美军步枪兵合并 | `*_ASSAULT` 的 `握把/握把红点/握把倍镜` 3 个变体并入 `*_RIFLEMAN`，删除 4 个 `*_ASSAULT` 职业；步枪兵变体 = `default,倍镜,机瞄,握把,握把红点,握把倍镜`（6 个） |
| ③ | 美军班组机枪拆两行 | 新增 4 个 `*_BIG_SQUADMG`（row=3，变体 default(机瞄)+光瞄）；`*_SQUADMG` 留 row=2 只含 default(机瞄)。两边 max=2、/squad=1、unlock_min_squad=6 |
| ④ | 美军通用机枪换 M60 | 8 个变体（4 编制 × 2）改为 `classicr:m60`：100 发 `tacz:308` 弹链（家族 `308_100`）、全自动；备用弹链 100 发 ×6；补给换成 308 弹链 `x1/max8`；副武器 P320 与 .45 弹匣/补给保留；光瞄变体保留 `suffuse:scope_compm4` |
| ⑤ | 俄军轻筒/重筒补给 | 轻筒 `[default]` 补给 +`murasamet:rpg7_pg7heat` 筒（x1/max1，4 处）；重筒补给 +`murasamet:og7he` 弹（x1/max1，4 处） |
| ⑥ | 工兵 C4/地雷补给 | 16 个工兵变体各 +`superbwarfare:c4_bomb`（x1/max1）与 `superbwarfare:tm_62`（x1/max3） |
| ⑦ | 全员望远镜 | **344 个变体**末尾追加 `minecraft:spyglass 1`（PLA 120 / RU 96 / US 128）；不进补给表 |

## 校验

- 12 个文件 `fix-*.json` 与服务器回读 **SHA256 完全一致**（二进制下载比对）。
- 逐项断言全部通过：望远镜每变体恰好 1 个、无残留 `BURST`、MMG 无 `m249`、`*_ASSAULT` 已删除、
  步枪兵 6 变体且限员仍为 100/-1、BIG_SQUADMG row=3 且变体 default+光瞄、M60 关键字段齐全、
  备用弹链与补给数量正确、俄军筒/弹到位、工兵补给数量正确。
- 通用 SNBT 健康检查：不存在 `}` 紧跟字母的漏逗号（本轮我曾生成一次该错误，已修正并加了断言）。
- 非目标内容零改动：每个文件的 `faction/vehTypes/vehicles` 与修复前逐字节一致。

## 已知取舍（如需调整告诉我）

1. ④ 的 US `*_SQUADMG`（row2）`unlock_min_squad` 仍是 **6**（PLA 的 row2 是 3）；新 row3 是 6。要对齐 PLA 就把 row2 改 3。
2. ② 合并后，原 `*_ASSAULT` 的 `maxPlayers=6 / max_per_squad=4 / unlock_min_squad=3` 限制随职业一起消失，
   合并后的步枪兵沿用 **100 人、无小队限制**；握把变体因此不再受限。
3. ③ 由"一个班组机枪（机瞄+光瞄）"变成"两个职业"，小队里最多仍是 2 人（每职业 2 人上限、每小队 1 人）——
   合计上限从 2 变 4，请确认这是你要的。
4. ⑦ 望远镜放在每个变体的**最后一项**，不会打乱原有槽位顺序；不带耐久/附魔。

## 回滚

- 服务端：`EsFactions/<名>.json.bak-20260924-fixes`（12 个，= 上一轮盔甲版）
- 本地：`build/tmp/EsFactions-backup-before-20260924-fixes/`（12 份）
- 更早的原始版（未减韧性）：`build/tmp/EsFactions-backup-before-20260924-armornerf/`
- 脚本与报告：`build/tmp/apply-faction-fixes.mjs`、`fix-report.json`、`verify-faction-fixes.mjs`
