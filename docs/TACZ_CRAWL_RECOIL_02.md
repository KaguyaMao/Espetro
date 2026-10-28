# 编制武器「趴下后座倍率」统一改 0.2

时间：2026-10-19
需求：User——「将编制中已经启用的所有武器趴下后座倍率改为 0.2」，武器 data 在服务端 `/tacz/`，字段 `crawl_recoil_multiplier`。

## 1. 改动范围（27 把枪）

来源：服务端 `/EsFactions/` 下**启用的 13 个编制**文件，统计 `tacz:modern_kinetic_gun{...GunId:"..."}` 得到 27 个唯一枪械 id。

| 枪械 id | 服务端/客户端所在包 | 原值 | 新值 |
|---|---|---|---|
| tacz:m4a1 | VanguardArmament | 0.5 | 0.2 |
| tacz:m249 | tacz_default_gun | 0.1 | 0.2 |
| tacz:m320 | VanguardArmament | 0.5 | 0.2 |
| tacz:p320 | tacz_default_gun | 0.5 | 0.2 |
| ccrp:ak74m | [TACZ1.1.8+Arcana]ClassicRCCRP 1.1.6_release_hotfix2 | 0.5 | 0.2 |
| ccrp:m110a3 | 同上 | 0.5 | 0.2 |
| ccrp:rpk74m | 同上 | 0.5 | 0.2 |
| classicr:m60 | 同上 | 无字段（默认） | 0.2 |
| suffuse:aks74u | Suffuse-GunSmoke-Pack1.0.8-hotfix | 无字段（默认） | 0.2 |
| suffuse:tt33 | 同上 | 无字段（默认） | 0.2 |
| suffuse:pf98a | 同上 | 0.5 | 0.2 |
| suffuse:qlz87 | 同上 | 0.5 | 0.2 |
| murasamet:rpg7_og7he | KhanPowder_v0.8.99_hotfix | 0.5 | 0.2 |
| murasamet:rpg7_pg7heat | 同上 | 0.5 | 0.2 |
| murasamet:rpg7_pg7vr_tandem_heat | 同上 | 0.5 | 0.2 |
| cib:qbz191 / qbz192 / qbu191 / qcw05 / qjb201 / qjy201 / qsz92 / pkp / svd / dzj08 | [Tacz1.1.7+]CIBR_GunsPack_v0.3_1.1.7 | qcw05＝0.75、dzj08/svd＝0.5，其余 8 把无字段 | 0.2 |
| ts:at4 / ts:gustavm4 | VanguardArmament | 0.5 | 0.2 |

合计：**17 个文件替换原数值、10 个文件新增字段**，全部统一为 `0.2`。

## 2. 实现方式

- 枪械数据是 JSON5（含 `//`、`/* */` 注释），直接 `JSON.parse` 会失败，因此用注释感知的文本补丁：
  - `build/tmp/lib-patch.mjs::patchText(text, 0.2)`：扫描顶层键，命中则原位替换数值，未命中则插到根对象末尾；
  - 补丁后用 `parseLoose()`（剥注释+去尾逗号）反序列化，断言除目标字段外结构完全一致，否则抛错。
- 双端各自打补丁（**不互拷**）：服务端 `/tacz` 与客户端 `…\Squad预发布测试\tacz` 的包内容早已分叉（例如 m4a1 伤害 8.8 vs 6.5、爆头倍率 2 vs 3），互拷会顺手改掉别的手感/伤害。
- 服务端 27/27 文件已上传并回读 SHA256 校验通过；客户端同目录 27/27 文件已就地修改，原文件备份在 `build/tmp/guns/orig-client/`。

## 3. 生效条件（关键）

反编译 `[永恒枪械工坊：零] tacz-1.20.1-1.1.8-release.jar` 全量 class 扫描：字符串 `crawl_recoil_multiplier` 只出现在 POJO `com/tacz/guns/resource/pojo/data/gun/GunData.class`；读取该字段的**唯一**类是 `com/tacz/guns/client/event/CameraSetupEvent.class`。

结论：
- 该倍率**纯客户端计算**，服务端 `/tacz` 数值对玩家手感没有影响；
- 玩家必须拿到改动后的客户端 `tacz/` 文件才会生效（服务端改动仅为数据一致性 + 服务端击发/弹道逻辑取同值）；
- 服务端包内数据只在启动时加载，如果需要服务端侧也刷新，用 `/tacz reload`（jar 内 `com/tacz/guns/command/sub/ReloadCommand`，方法 `reloadAllPack`）或下次重启；**不必**为这个改动单独重启。

## 4. 分发

- 打包好的客户端 27 个文件（保留 `tacz/` 下相对路径，解压覆盖即可）：
  `D:\minecraft\modp\Espetro\build\crawl-recoil-0.2-client.zip`（38 KB）
- 清单：`build/tmp/guns/client-sync-list.json`

## 5. 回滚

| 对象 | 回滚点 |
|---|---|
| 服务端 27 个文件 | `build/tmp/guns/orig/` 原始内容（如需回滚可重新上传） |
| 客户端 27 个文件 | `build/tmp/guns/orig-client/` |
| 编制侧 | 未改 |

## 6. 遗留发现

- 编制里引用了 **`ccrp:m110`**，但全部 9 个枪包里都不存在该枪（只有 `ccrp:m110a3`）——持有该职业的玩家拿不到枪。属既有问题，本次未动。
- 本次统计口径为「启用编制里出现过的 GunId」；`plamc_5th.json.dis`、`plamc_7th.json.dis` 两个已停用编制未纳入。
