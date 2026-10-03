# 载具角度伤害抗性 `$entity.getSourceAngle(source, 0.35) * damage` 实施报告

**状态**：已写入 46/47 台载具并 `/reload` 生效（无需重启），服务端与本地逐字节一致。

---

## 1. 改了什么

在每台载具 `DamageModifiers` 数组的**末尾**追加一条脚本型条目：

```json
"$entity.getSourceAngle(source, 0.35) * damage"
```

写法取自 SBW **官方内置载具**（`superbwarfare:ztz_99a` 用 0.3、`bmp_2`/`lav_25` 用 0.15、
卡车 0.25、`csk181` 用 0.05…），语法与位置完全一致。

## 2. 这条脚本的实际含义（反编译确认）

```java
// VehicleVecUtils.getDamageSourceAngle(vehicle, source, m)
Entity atk = source.getEntity() != null ? source.getEntity() : source.getDirectEntity();
if (atk == null) return 1.0f;                       // 无来源实体（如自然爆炸）→ 不修正
Vec3 dir = 车体中心.vectorTo(攻击者位置).normalize();  // 注意是 vectorTo = B - A
return (float) Math.max(1.0 - m * dir.dot(vehicle.getViewVector(1.0f)), 0.5);
```

即 **`伤害 ×= max(1 − m·cosθ, 0.5)`**，θ = 攻击者方位与**车体朝向**的夹角：

| 命中方向 | 系数（m=0.35） | 说明 |
|---|---|---|
| **正面**（攻击者在车头方向） | **×0.65** | 减伤 35% |
| **侧面/斜侧**（cosθ=0） | **×1.00** | 不变 |
| **背面**（攻击者在车尾方向） | **×1.35** | 增伤 35% |

- 判定依据是**攻击者的位置**（炮弹的发射者；无 owner 时用炮弹自身），不是命中点；
- 参考：SBW 内置坦克 0.3 → 正/背 ×0.7/×1.3；步战 0.15 → ×0.85/×1.15。**本次 0.35 比游戏自带主战坦克更强**；
- 放在数组**末尾**是关键：前面的 `±N`（绝对值加减）与 `×` 链先算完，这一条只对**最终值**做方向修正，例如
  `(200 + 13 − 13) × 0.2 × 5 × 1.35 × 0.3703703704 × [0.65|1.00|1.35]`。

## 3. 覆盖范围

| 项目 | 结果 |
|---|---|
| 服务端载具文件 | `kubejs/data/dragonrise_reforge/sbw/vehicles/*.json`（29）+ `kubejs/data/fcp/sbw/vehicles/*.json`（18）= **47** |
| 已写入 | **46 台**（含卡车、悍马、直升机 ah64/z20 等全部载具，按你说的"所有载具"） |
| 跳过 | `dragonrise_reforge:sx1_a` —— 该文件只有 4 个字段（ID/UpStep/TurretPos/BarrelPos）的**局部覆盖**，本身没有 `DamageModifiers`。给它写一份数组会**整体替换**它从模组 jar 继承来的抗性表，风险大于收益，**等你确认后再处理** |
| 模组内置载具 | 未改动（它们自带 `$getSourceAngle`，数值 0.05–0.3 不等） |

## 4. 验证（三重）

1. **落盘一致**：46 个文件全部重新下载比对 SHA256 → **46/46 一致**；
2. **语法可用**：从**服务端实际使用的那支 jar**（`superbwarfare-0.8.9.1-hotfix-…-all-patched2.jar`）反编译确认
   `DamageModify.DamageModifyInstanceBuilder.fromString` 对 `$` 开头走
   `ScriptManager.createSafeScript("damageModifier", body)`，失败会打 `invalid damage modify script`；
   `/reload` 后日志**无该警告、无新增解析错误**（仅 2 条既有的 `example_trailer`/`humveem2` 报错，与本次无关）；
3. **覆盖语义**：控制台召唤实测 —— `fcp:bmp2` 实体血量 **450.0f**（kubejs 值），而 jar 内置是 300
   → **kubejs 整体覆盖内置定义（`map.put(id, object)` 替换，不合并列表）**，因此本条目**只会生效一次**，不会与 jar 自带的角度条目叠加。

## 5. 对互毁的实际影响（三个代表，04A 按炮射导弹）

| 攻 \ 守 | 方向 | ZBL-08 325 | ZBD-04A 350 | ZTZ-99A 550 |
|---|---|---|---|---|
| **ZBL-08** 30mm | 正/侧/背 | 67发/12.0s · 44发/7.8s · 33发/5.8s | 72发/12.9s · 47发/8.4s · 35发/6.2s | 113发/20.4s · 74发/13.3s · 55发/9.8s |
| **ZBD-04A** 炮射导弹 | 正/侧/背 | 2发 · 2发 · **1发** | 3发/11.0s · 2发/5.5s · **1发** | 4发/16.5s · 3发/11.0s · **2发/5.5s** |
| **ZTZ-99A** 125mm | 正/侧/背 | 2发 · 2发 · **1发** | 3发/12.0s · 2发/6.0s · 2发/6.0s | **6发/30.0s** · 4发/18.0s · **3发/12.0s** |

要点：
- **主战坦克互毁从 4 发变为 正面 6 发 / 背面 3 发**（30s vs 12s），绕侧后的收益非常明显；
- 步战之间的机炮对射：正面 72 发 vs 背面 35 发 —— 侧面/背面偷袭的效率差一倍；
- 导弹类因为单发伤害高，发数变化小，但**背面 1 发即可打爆步战、2 发打爆主战坦克**。

## 6. 回滚

```powershell
# 原件（逐字节）在 build/tmp/veh-angle/before/，改后件在 build/tmp/veh-angle/after/
node veh-angle-upload.mjs "veh-angle/before"   # 上传原件回滚
# 或者在服务端控制台/面板执行 reload 前先上传原件
```
相关脚本：`veh-angle-apply.mjs`（写入）、`veh-angle-upload.mjs`（上传）、`veh-angle-verify.mjs`（校验）、
`veh-angle-ttk.mjs`（含角度系数的互毁计算）、`veh-matrix.mjs`（原矩阵）。

## 7. 待确认

1. **`sx1_a` 要不要一起加？** 它没有自己的抗性表（靠 jar 继承）。要加的话建议照抄同底盘 `sx1` 的整份列表再追加本条目；
2. **范围是否要收窄？** 现在卡车/悍马/直升机也吃这套正背修正（按"所有载具"执行）。若只想给装甲车辆，我可以把非装甲类（truck/mv3/ural4320/sx1/ah64/z20 等）的这条删掉。
