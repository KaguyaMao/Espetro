# 编制修复方案（7 项）——待审核

> 基线：当前的 `out-*.json`（已含上一轮"盔甲韧性归零"），全部 12 个启用编制。
> 生效方式：改完上传 + **完整重启**（EsFactions 启动时冻结）。

---

## ① 美军 M4A1 默认全自动

- 4 个美军编制里所有 `GunId:"tacz:m4a1"` 的给予项，`GunFireMode:"BURST"` → `"AUTO"`。
- 现状：每编制 29 处 M4A1（20 处 BURST、9 处已 AUTO）→ 4 个编制共改 **80 处**。
- 只动快慢机，弹药数/配件/弹匣不变。

## ② 美军步枪兵合并（现在"分裂"成两个职业）

现状（4 个美军编制都一样）：

| 职业 | 名 | row | 限员 | 变体 |
|---|---|---|---|---|
| `*_RIFLEMAN` | **步枪兵** | 2 | max=100、/squad=-1（无限） | default(默认装备)、倍镜(倍镜步枪兵)、机瞄(机瞄步枪兵) |
| `*_ASSAULT` | **自动步枪** | 2 | max=6、/squad=4、unlock_min_squad=3 | 机瞄/红点/倍镜/握把/握把红点/握把倍镜 —— **名字全是"步枪兵(...)"** |

所以界面上看起来是"两个步枪兵"。参考 PLA：只有一个 `自动步枪` 职业，6 个变体全叫"步枪兵(...)"。

**方案**：把 `*_ASSAULT` 的 6 个变体并入 `*_RIFLEMAN`，删除 `*_ASSAULT` 职业。

**待定**：
- 合并后限员用哪套？(a) 步枪兵的 100/-1；(b) 6/4 + unlock_min_squad=3；(c) 其它
- 变体保留几套？(a) 9 个全留；(b) 以 ASSAULT 的 6 个为准；(c) 去重成 6 个（默认/倍镜/机瞄 + 握把/握把红点/握把倍镜）

## ③ 美军班组机枪拆开（像中国/俄国那样两行）

现状：`*_SQUADMG`（名=班组机枪，row=2，max=2//squad=1，M249）变体 = default(机枪手(机瞄)) + 光瞄(机枪手(光瞄))。

参考：
- PLA：`*_SQUADMG` row=2（只有 机瞄）+ `*_BIG_SQUADMG` row=3（机瞄 + 光瞄），row3 的 `unlock_min_squad=6`
- RU：`*_SQUADMG` row=2（机瞄）+ `*_BIG_SQUADMG` row=3（只有 光瞄）

**方案**：新增 `*_BIG_SQUADMG`（名=班组机枪，row=3），把 `光瞄` 变体挪过去；`*_SQUADMG` 留在 row=2 只保留 `机瞄`；两边 max=2、/squad=1 不变，新职业 `unlock_min_squad` 对齐参考值。

**待定**：row3 只放"光瞄"（符合你的描述）还是"机瞄+光瞄"（PLA 的写法）？

## ④ 美军通用机枪换 M60

现状：`*_MMG`（名=通用机枪手，row=4）用 `tacz:m249`：弹 75 发 5.56、弹匣 `556x45_75`、备用弹匣 ×3、副武器 P320（12 发 .45）、补给 `ammo:tacz:556x45 x1/max5` + 手枪弹。

M60 已确认存在于枪包：**`classicr:m60`** —— 数据：弹药 `tacz:308`(7.62×51)、默认弹链 **100 发**、`fire_mode=["auto"]`、可装 scope/muzzle/extended_mag。

**方案**（数值按 PLA 通用机枪 `cib:qjy201` 对齐）：
- 枪：`GunId:"classicr:m60"`、`GunFireMode:"AUTO"`、`GunCurrentAmmoCount:100`
- 弹链：`{AmmoCount:100, AmmoId:"tacz:308", MagazineFamily:"308_100", MaxCapacity:100}`（该家族编制里已在用）
- 备用弹链：由 75 发×3 → **100 发 × N**
- 副武器 P320 保留（PLA 用 QSZ92，美军用 P320 合理）
- 补给：`ammo:tacz:556x45 x1/max5` → `taczmagazines:magazine{...308_100...} x1/max M`；手枪弹补给保留
- 4 个美军编制的 MMG 两个变体都改（机瞄 / 光瞄，光瞄保留 `suffuse:scope_compm4`）

**待定**：备用弹链 N 取多少（现 3，PLA 通用机枪是 6）？补给上限 M（现 5，PLA 是 8）？

## ⑤ 补全俄军轻筒补给

现状（4 个俄军编制的 `*_ANTITANK`）：

| 变体 | 给予的筒 | 给予的弹 | 补给表现状 |
|---|---|---|---|
| `[default] 轻筒(机瞄)` | `murasamet:rpg7_pg7heat` + `murasamet:rpg7_og7he`（两把） | pg7heat、og7he 各 1 | medical/mag/bread/手雷/烟雾 + **两发弹**（各 1/max1）→ **没有"筒"** |
| `[倍镜] 轻筒(倍镜)` | `ts:at4` | — | 有 `gun:ts:at4` ✓ |

对比：PLA 轻筒补给含 `gun:cib:dzj08`、US 轻筒补给含 `gun:ts:at4` ✓ —— **俄军轻筒的 [default] 是唯一没有"筒"补给的**。

**方案**：给 `[default]` 补给追加 `tacz:modern_kinetic_gun{GunCurrentAmmoCount:1,GunFireMode:"SEMI",GunId:"murasamet:rpg7_pg7heat",HasBulletInBarrel:1b,OverHeated:1b}`（count=1/max=1）。

**待定**：只补 pg7heat（推荐）还是两把筒都补？顺带发现**俄军重筒**补给缺 `murasamet:og7he` 弹（有 pg7heat / pg7vr_tandem_heat），要不要一起补？

## ⑥ 所有编制工兵补 C4 + 反坦克地雷的补给

现状：**12 个编制的工兵都发了** C4（`superbwarfare:c4_bomb` ×1）和反坦克地雷（`superbwarfare:tm_62` ×3，美军显示名"M15 反坦克地雷"），但**补给表里一项都没有**（PLA/RU/US 全部如此）。

**方案**：给每个工兵变体的 `resupply.items` 追加两项：

| 物品 | count | max |
|---|---|---|
| `superbwarfare:c4_bomb` | 1 | 1 |
| `superbwarfare:tm_62` | 1 | 3 |

共 **16 个工兵变体**（PLA 4 + RU 4 + US 4×2 变体）。`ammo_cost` 不变。

**待定**：count/max 用 (a) 1/1 与 1/3（每次补 1、上限=发放量，推荐）；(b) 1/1 与 3/3（一次补满）；(c) 你给数。

## ⑦ 所有兵种配发原版望远镜

- 12 个编制、**每一个职业的每一个变体**的 `commands` **末尾**追加 `minecraft:spyglass 1`。
- 数量：PLA 4×30=120、RU 4×24=96、US 4×34=136 → 共 **356 个变体**。
- 放末尾是为了**不改变现有物品的槽位顺序**（避免打乱快捷栏）。
- 默认**不加入补给表**（望远镜非消耗品）。

**待定**：确认"所有兵种"包括队长/载具队长/载具组员（我按全部都给）；是否也要进补给表。

---

## 执行方式（审核通过后）

1. 写一个带开关的转换脚本，对 12 个 `out-*.json` 逐项应用，输出到 `fix-*.json`；
2. 校验：JSON 合法 + 只动该动的字段（对比"非目标字段零改动"）+ 各项数量核对 + 花括号配平 + 引用物品存在于注册表（M60/spyglass/c4/tm_62 等）；
3. 本地备份 + 服务端 `<名>.json.bak-<时间戳>`；
4. 上传（**不重启**，等你说）；
5. 重启后验证：M4A1 全自动、步枪兵只剩一个、班组机枪两行、MMG 是 M60/100 发、俄军轻筒可补筒、工兵能补 C4/地雷、所有职业都有望远镜。

## 待你确认

1. ②合并后限员用哪套？变体保留几套？
2. ③第三行只放光瞄还是光瞄+机瞄？
3. ④M60 备用弹链数量与补给上限？
4. ⑤俄军轻筒补一把还是两把？重筒缺的 og7he 弹要不要一起补？
5. ⑥C4/地雷补给的 count/max？
6. ⑦望远镜是否真的"全部职业"，是否进补给表？
