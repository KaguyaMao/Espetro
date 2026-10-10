# 编制修复记录：PLA「奇袭兵」弹匣错误（2026-09-26）

## 1. 问题
7 个 PLA 编制的「奇袭兵」职业，其 **红点（红点镜）变体** 携带的备用弹匣给错了型号：
- 枪本体：`cib:qcw05`（QCW-05），弹药 `cib:58x21`，枪内预装弹匣族 `58x21_50` / 50 发
- 错误备用弹匣：`tacz:58x42` 族 `58x42_30` / 30 发 × 5 —— 与枪的口径/弹匣族完全不匹配（无法装填，等于少 6 个可用弹匣）
- 同职业的 **机瞄变体** 是正确的（`58x21_50` × 6），只有红点变体写错

## 2. 涉及文件与改动
全部为 `EsFactions/*.json`，每个文件**仅改 1 条指令**（红点变体 `#9`）：

| 文件 | 职业 key |
|---|---|
| `pla_112th_brigade.json` | `PLA_112_RAIDER` |
| `pla_112th_brigade_mesh.json` | `PLA_112th_mesh_RAIDER` |
| `pla_118th_brigade.json` | `PLA_118_RAIDER` |
| `pla_195th.json` | `PLA_195_RAIDER` |
| `plamc_5th.json` | `PLA_5th_mesh_RAIDER` |
| `plamc_5th_at.json` | `PLA_112_mesh_RAIDER` |
| `plamc_7th.json` | `PLA_7th_mesh_RAIDER` |

改动内容（红点变体第 9 条指令，末尾数字为发放个数）：
```
- taczmagazines:magazine{AmmoCount:30,AmmoId:"tacz:58x42",MagazineFamily:"58x42_30",MaxCapacity:30} 5
+ taczmagazines:magazine{AmmoCount:50,AmmoId:"cib:58x21",MagazineFamily:"58x21_50",MaxCapacity:50} 6
```

## 3. 全量核查（改前/改后）
`node audit-mags2.mjs <目录>` 遍历全部 15 个编制、217 个职业、105 个"多弹匣变体"职业，
比对每个变体内 **枪内预装弹匣 vs 备用弹匣** 的 `AmmoId / MagazineFamily / MaxCapacity`（忽略个数）：

| 目录 | 不一致职业数 |
|---|---|
| `fxall/`（服务器原文件备份，改前） | **7**（即上表 7 个奇袭兵红点变体） |
| `fix-factions/`（改后本地） | **0** |
| `fix-verify/`（改后从服务器回下载） | **0** |

除这 7 条指令外，其余字节完全一致（生成时逐文件比对确认）。

## 4. 部署与验证
1. 7 个文件上传至服务器 `EsFactions/`（overwrite）。
2. 回下载 + SHA256 逐文件比对：**7/7 MATCH**（字节一致，尺寸 96082 / 95842 / 96019 / 95233 / 74346 / 74458 / 74007 B）。
3. 重启服务端（已获用户确认；重启前对在线玩家发了 10 秒提示）：
   - `Done (2.765s)`
   - `EsFactions 已冻结: 15 个编制, 217 个职业`
   - KubeJS `4/4 startup`、`9/9 server`，均 0 errors
   - 仅剩 TACZ 自带数据包噪音报错（`suffuse:knife`、`ciblr:cib_smith_table`），与 2026-08-29 日志完全相同 → 历史遗留，与本次改动无关
4. `node check-all-raiders.mjs <目录>`：7 个奇袭兵职业、14 个变体全部 `OK`（枪族 = 备用弹匣族）。

## 5. 回滚点
- 改前原始文件：`build/tmp/fxall/*.json`（7 个对应文件）
- 回滚方式：把 `fxall/` 中对应文件重新上传至 `EsFactions/` 并重启。

## 6. 相关脚本
- `build/tmp/fix-raider-mag.mjs` —— 生成修复文件
- `build/tmp/audit-mags2.mjs` —— 全编制弹匣一致性审计
- `build/tmp/check-all-raiders.mjs` / `check-raider-mag.mjs` —— 奇袭兵专项核对
- `build/tmp/download-file.mjs` / `upload-one.mjs` —— MCSM 文件上下行
