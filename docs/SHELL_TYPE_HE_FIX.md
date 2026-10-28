# 载具炮弹打不爆（穿过生物/方块）根因与修复

日期：2026-10-26 ｜ 服务端：`100.92.163.58:20001`（MCSM）｜ 实例 `cd06335bc1534e028d92b4b335be248a`

## 1. 现象

载具的高爆弹（`superbwarfare:large_shell_he` / `small_shell_he`）击中生物与方块时**不爆炸**，直接穿过去；破甲弹同样表现为穿透。

## 2. 根因

`CannonShellEntity` 的弹种由 `ShellType`（`GunProp.SHELL_TYPE`）决定，缺省值是 **AP**。
`GunItem` 是全 jar 中唯一读取 `ShellType` 的地方：

```java
// com.atsuishio.superbwarfare.item.gun.GunItem（javap 反查）
getstatic GunProp.SHELL_TYPE
invokevirtual GunData.get(GunProp)   // 缺省 → "AP"
→ CannonShellEntity$Type.valueOf(...)
```

kubejs 里的载具覆盖文件把官方写法 `"ShellType": "HE"` 换成了 `"IsHighExplosiveProjectile": true` /
`"IsArmorPiercingProjectile": false`——这两个键在 superbwarfare 的 2541 个 class 里**零命中**（没有任何代码读它们），
于是高爆弹以 AP 身份生成：

- 撞生物：AP 分支沿路径伤害并继续飞 → 不爆炸；
- 撞方块：AP 分支仅在 `ExplosionConfig.EXPLOSION_DESTROY` 开启时清方块，否则什么也不做 → 表现为穿方块。

数据对照（`build/tmp/scan-shelltype.mjs`）：

| | 载具数 | 带 Override 的炮弹项 | 写了 ShellType |
|---|---|---|---|
| kubejs 60 个覆盖 | 60 | 25（HE） | 1 |
| 官方 `sbw.jar`（2472 个 json） | 1470 | 16（HE） | 9 |

官方用 `ShellType` 的实例：`m_1a_2`、`t_90a`、`ztz_99a`、`plz_05`、`bl_132`、`fh_77bw`、`mk_42`、`mle_1934`、`yx_100`。
官方另有 7 个 `small_shell_he` 项没写 `ShellType`，因为小口径走 `SmallCannonShellEntity`，自带爆炸。

## 3. 修复（24 个文件 / 49 个炮弹覆盖项）

`build/tmp/patch-shell.mjs`：外科式文本改写，只动 `AmmoType` 里的炮弹覆盖项，其余排版与字段原样保留。

| 弹药 | ShellType | Damage（直击） | ExplosionDamage（溅射） | ExplosionRadius |
|---|---|---|---|---|
| `large_shell_he` | HE | 60 | 60 | 10 |
| `large_shell_ap`（破甲弹，按 HE 算） | HE | 200 | 30 | 7 |
| `small_shell_he` | HE | 30 | 30 | 5 |
| `small_shell_ap`（按 HE 算） | HE | 30 | 30 | 5 |

同时删除 `IsHighExplosiveProjectile` / `IsArmorPiercingProjectile` 死键（只删炮弹项内的；导弹项里的同名键保留）。
裸字符串形式的弹药（如 `ah64` 的 `"AmmoType": "superbwarfare:small_shell_ap"`）展开为等价的单元素数组 + Override。
`Override` 中原有的 `Icon` / `Velocity` / `Gravity` / `Name` / `ApDurability` 等一律保留。

改动文件（24）：
`ah64 bmp3 m1128 m1296 m1a2sepv1 m1a2sepv2 m3a3 t72b3 t90mh zbd04a zbd05 zbl08 zlt11 ztd05 ztz96a ztz99a`
（`kubejs/data/dragonrise_reforge/sbw/vehicles/`）+ `bmp1am bmp2 bmp2d bmp2m btr82 stryker_dragoon stryker_mgs t72av`
（`kubejs/data/fcp/sbw/vehicles/`）。

## 4. 部署与验证

- 本地：`build/tmp/srvveh/`（当前线上版本），改动前原件在 `build/tmp/srvveh-shell-before/`。
- 上传：`build/tmp/upload-shell.mjs`（**MCSM 的上传端点与 `upload_dir` 绑定，每个目录要重新申请；实际传输必须用 curl，
  node fetch 的 FormData 会静默失败**），24/24 逐文件按字节数核验通过。
- 回读校验：`PUT /api/files` 读 `m1a2sepv1.json` → 含 `"ShellType": "HE"`、`"Damage": 200`、`"Damage": 60`，死键已消失。
- 结构校验：`build/tmp/verify-shell.mjs` → 49/49 项符合上表，0 问题。

## 5. 生效条件

载具数据只在**服务器启动**时加载（`/reload` 无效）。当前实例处于停止状态，下次开服即生效。

## 6. 未处理（记录）

- 导弹项里的 `IsArmorPiercingProjectile` / `IsHighExplosiveProjectile` 死键（42 处）仍在，但引擎不读，属无害；
  如要清理可另行处理。
- 官方 jar 内 7 个 `small_shell_he` 无 `ShellType` 的项未动（小口径本就不需要）。
