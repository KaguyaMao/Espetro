# 编制盔甲韧性归零 + 数据清理记录

时间：2026-09-24（文件已上传，**等待用户重启后生效**：EsFactions 在服务器启动时冻结）
范围：服务端 `EsFactions/` 下 12 个启用中的编制文件（`.dis` 的 3 个未动）

## 一、盔甲韧性归零（1152 条）

在每条盔甲给予字符串里追加 NBT：

```snbt
{AttributeModifiers:[{AttributeName:"minecraft:generic.armor_toughness",
  Name:"espetro_toughness_zero",Amount:-1.0,Operation:1,UUID:[I;<按物品 id 派生>]}]}
```

- **Operation:1 = MULTIPLY_BASE，Amount=-1** → `最终值 = 基础值 + 基础值 × (-1) = 0`，
  与盔甲原本韧性多少无关（钻石 2、下界合金 3、mod 甲自定义值都一样归 0）。
  属性自身下限是 0，所以面板显示就是 0。
- **不写 `Slot` 字段** → 原版 `ItemStack.getAttributeModifiers(slot)` 对未标 Slot 的条目
  在所有装备槽都生效，因此不用逐件区分头/胸/腿/脚。
- **UUID 按物品 id 派生且高低位都非 0**：原版会丢弃 UUID 两半任一为 0 的修饰符；
  且不同装备槽用不同 UUID，避免 `AttributeInstance` 的 UUID 冲突（同槽重复装备不会互相顶掉）。

涉及的盔甲 id（均已核实存在）：

| id | 出现次数 |
|---|---|
| `dragonrise_reforge:fast_helmet` / `msv_chest` / `msv_pants` | 各 136→US 四编制 |
| `dragonrise_reforge:med21_chest` / `pants21` / `t21_helmet` | PLA 四编制 |
| `mm_armor:helmet_6b_47emrgoggles_helmet` / `chestplateemrvest_6b_45upgraded_chestplate` / `emruniform_leggings` / `blackboots_boots` | RU 四编制 |
| `mm_armor:tsh_4helmet_helmet` | 每编制 1 条（额外头盔） |

按文件统计（转换后仍带韧性归零标记的条目数）：
PLA 112th / 112th_mesh / 118th / 195th = 各 90；RU 四份 = 各 96；US 四份 = 各 102。

## 二、删除不存在的 id（136 条）

`mm_armor:mm_armor:blackboots_boots`（双冒号，`ResourceLocation` 无法解析）→ 按用户要求**按字面删除**。

⚠️ 它是 **US 四个编制唯一的靴子**（每职业每变体 1 条，共 136 条）。删除后 US 四编制（us_1th_ar /
us_1th_ri / us_2nd_stryker / us_redone）所有职业**没有靴子**。
RU 四编制用的是合法写法 `mm_armor:blackboots_boots`，未受影响。
（若以后要补回 US 靴子，把删除的行改回 `mm_armor:blackboots_boots{...}` 即可。）

## 三、替换不存在的非盔甲物品（16 条，仅 US 四编制工兵）

| 原 id（SBW 里不存在） | 替换为 |
|---|---|
| `superbwarfare:m15_anti_tank_mine 3` | `superbwarfare:tm_62{RepairCost:0,display:{Name:'{"text":"M15 反坦克地雷"}'}} 3` |
| `superbwarfare:m112_c4 1` | `superbwarfare:c4_bomb 1` |

SBW 现有地雷/爆炸物：`tm_62`、`blu_43_mine`、`claymore_mine`、`lunge_mine`、`c4_bomb` 等；
`m15_anti_tank_mine`、`m112_c4` 两个 id 在 SBW 物品模型里完全不存在。

## 四、顺带修复的坏给予字符串（6 条，不改物品内容）

| 位置 | 问题 | 修复 |
|---|---|---|
| pla_112th_brigade / PLA_112_ENGINEER / default[8] | `tm_62` SNBT 缺 1 个 `}` | 补 `}` |
| pla_112th_brigade_mesh / PLA_112th_mesh_ENGINEER / default[8] | 同上 | 补 `}` |
| pla_118th_brigade / PLA_118_ENGINEER / default[8] | 同上 | 补 `}` |
| pla_195th / PLA_195_ENGINEER / default[8] | 同上 | 补 `}` |
| pla_118th_brigade / PLA_118_RAIDER / 机瞄[0] | QCW05 丢失 `AttachmentMUZZLE:{Count:1b,id:"tacz:attachment",tag:{` 前缀（净值 -2） | 还原前缀 |
| pla_118th_brigade / PLA_118_RAIDER / 红点[0] | 多包了一层 `AttachmentSCOPE:{`（净值 +1） | 去掉多余层 |

修复后：全部 12 个文件的给予字符串花括号净值 = 0。

## 五、校验

- 12 个文件服务端**二进制回读 SHA256 与本地转换结果完全一致**（文本读回 API 对个别汉字会乱码，
  故用二进制下载核对）。
- 非 `commands` 字段：转换前后 **JSON 完全一致**（只动了给予列表）。
- 转换结果仍为合法 JSON，且 `JSON.stringify(obj,null,2)+"\n"` 与原文件字节一致（格式未变）。
- 服务器侧抽查：`espetro_toughness_zero` 90 条（pla_118th）、残留非法 id 0、花括号不配平 0、
  中文正常（`铁锹(左键建造，右键拆除)`）。

## 六、回滚

- 服务端：`EsFactions/<名>.json.bak-20260924-armornerf`（12 个，`*.json` 之外的扩展名不会被加载器读取）
- 本地：`build/tmp/EsFactions-backup-before-20260924-armornerf/`（原始 12 份）
- 转换脚本与报告：`build/tmp/transform-factions.mjs`、`transform-report.json`、`verify-factions-out.mjs`

回滚方式：把 `EsFactions/<名>.json.bak-20260924-armornerf` 覆盖回 `<名>.json`，然后重启。

## 七、待办

- 等用户通知后**完整重启**，核对启动日志 `EsFactions 已冻结: 12 个编制, N 个职业`，
  并抽查游戏内一件盔甲的韧性显示为 0。
