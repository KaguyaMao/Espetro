# 任务：修复 HJ-8 / 9M133 固定发射器「装填后卡死 + 弹药不消耗」

## 背景
- 项目：`dragonrise_reforge`（载具包），Minecraft 1.20.1 / Forge 47.4+ / superbwarfare 0.8.9.1
- 涉及类与方法：
  - `com/redabysslucia/dragonrise_reforge/entities/atmg/HJ8Entity.java` → `interact(Player, InteractionHand)`（约 68–96 行）
  - `com/redabysslucia/dragonrise_reforge/entities/atmg/R9M133Entity.java` → `interact(Player, InteractionHand)`（约 72–100 行）
- 设计意图：这两个实体是**可放置的固定反坦克导弹发射器**，装填方式是"玩家在外部手动装填"：
  手持该武器使用的弹药 → 右键发射器 → 从弹仓取弹压入弹匣；装填完成后才可以上车开火。

## 现象（两个现象、同一根因）
1. 装填动作**触发了**（能听到 `TYPE_63_RELOAD` 音效），但**弹药没有被消耗**；
2. 之后**无法登上发射器**，也**无法再次装填** —— 发射器永久卡死，只能拆掉重建。

## 现行代码（问题所在）
```java
// HJ8Entity / R9M133Entity 的 interact(...)
var stack = player.getMainHandItem();
if (gunData.hasEnoughAmmoToShoot(player)) {
    entityData.set(LOADED, true);
    return super.interact(player, hand);          // 已装填 → 原版交互（上车）
}
if (!entityData.get(LOADED)) {
    if (!gunData.selectedAmmoConsumer().isAmmoItem(stack)) {
        return super.interact(player, hand);      // 手上不是该弹药 → 原版交互（上车）
    }
    if (level() instanceof ServerLevel serverLevel && entityData.get(RELOAD_COOLDOWN) == 0) {
        modifyGunData(0, data -> data.reloadAmmo(player));   // ← 从发射器自身弹仓取弹
        entityData.set(LOADED, true);                        // ← 无条件置 true（BUG）
        serverLevel.playSound(null, getOnPos(), ModSounds.TYPE_63_RELOAD.get(), ...);
    } else {
        player.displayClientMessage(...冷却提示...);
    }
}
return InteractionResult.SUCCESS;                 // ← 无论成功与否都不再走"上车"分支
```

## 根因分析
- `data.reloadAmmo(player)` 的实际取弹来源是**发射器自身的物品容器**（Forge `ITEM_HANDLER`）。
  容器里没有弹药时，这次调用**什么都不做**（不装弹、不扣弹），但代码紧接着仍然执行
  `entityData.set(LOADED, true)` → 发射器进入"**自认为已装填、弹匣其实是空的**"状态。
- 在该状态下：
  - `gunData.hasEnoughAmmoToShoot(player)` 仍为 `false` → 第一个分支不成立；
  - `entityData.get(LOADED)` 已为 `true` → 第二个分支被整体跳过；
  - 直接 `return InteractionResult.SUCCESS` → **既不再尝试装填，也不执行原版上车**
    ⇒ 玩家看到的就是"装不上、也上不去"。
- 附带体验问题：即使装填成功，被消耗的是**发射器弹仓里的**弹药，玩家背包里那一发不会减少，
  容易被误判为"没消耗"。

## 修复要求
1. **只有真的装上才置 `LOADED = true`**：装填失败（弹仓无弹）或处于冷却时保持 `false`，
   并给玩家明确反馈（例如 action bar：`§c没有可用弹药（请先把弹药放进发射器弹仓）`）。
2. **未装填且装填未成功时，必须放行原版交互**（`return super.interact(player, hand)`），
   让玩家仍能上车/正常交互 —— 不要用 `return InteractionResult.SUCCESS` 把交互吞掉。
3. 冷却提示只在"确实尝试装填且处于冷却中"时显示，并且**同样要放行上车**。
4. 保持既有语义不变：
   - `vehicleShoot(...)` 之后 `LOADED = false`，并写入 `RELOAD_COOLDOWN = ceil(20 / (rpm / 60))`；
   - `getRetrieveItems()` 在 `LOADED == true` 时照旧返还
     `selectedAmmoConsumer().stack().copyWithCount(withdrawAmmoCount())`；
   - 尾焰伤害、粒子、音效等逻辑不动。
5. **把"弹药从哪来"写清楚并实现一致**：
   - 若玩法就是"先放进发射器弹仓，再手持一枚弹药右键装填"，则必须确保**弹仓里那一发被扣掉**
     （现在的行为是容器里的弹会被扣，但只有在容器有弹时才会发生）；
   - 若希望"玩家背包里有弹药即可装填"，则需要显式从玩家背包取弹并扣减
     （SBW `GunData` 提供 `countBackupAmmo(Entity)` / `countBackupAmmo(IItemHandler)` /
     `consumeBackupAmmo(...)` / `hasBackupAmmo(...)` 等重载，按选定容器使用）。

## 建议补丁（示意，按实际 API 调整）
```java
var stack = player.getMainHandItem();
if (gunData.hasEnoughAmmoToShoot(player)) {
    entityData.set(LOADED, true);
    return super.interact(player, hand);
}
if (!entityData.get(LOADED)) {
    if (gunData.selectedAmmoConsumer().isAmmoItem(stack)) {
        if (level() instanceof ServerLevel serverLevel && entityData.get(RELOAD_COOLDOWN) == 0) {
            int beforeCount = gunData.countBackupAmmo(player);
            modifyGunData(0, data -> data.reloadAmmo(player));
            boolean actuallyLoaded = gunData.hasEnoughAmmoToShoot(player)
                || gunData.countBackupAmmo(player) < beforeCount;   // 真装上 / 真扣了弹
            if (actuallyLoaded) {
                entityData.set(LOADED, true);
                serverLevel.playSound(null, getOnPos(), ModSounds.TYPE_63_RELOAD.get(),
                    SoundSource.PLAYERS, 1f, random.nextFloat() * 0.1f + 0.9f);
                return InteractionResult.SUCCESS;
            }
            player.displayClientMessage(
                Component.literal("§c没有可用弹药（请先把弹药放进发射器弹仓）"), true);
            return super.interact(player, hand);          // 放行上车
        }
        player.displayClientMessage(Component.literal(
            FormatTool.format1DZ((double) (coolDown - entityData.get(RELOAD_COOLDOWN)) / 20)
                + " / " + FormatTool.format1DZ((double) coolDown / 20)), true);
    }
    return super.interact(player, hand);                  // 未装填 → 一律允许上车
}
return super.interact(player, hand);
```

## 复现步骤（当前版本）
1. 放置/部署一具 HJ-8 或 9M133 发射器；
2. 清空发射器弹仓，手持该武器使用的弹药右键 → 有装填音效，但弹药数量不变；
3. 再次右键（换手、换弹、等待冷却后）→ 无任何反应；尝试上车 → **无法上车**；
4. 结论：发射器停留在 `LOADED = true` 且弹匣为空的死锁状态。

## 验收标准
| 场景 | 期望 |
|---|---|
| 弹仓为空 + 手持该弹药右键 | 不置 `LOADED`、提示"没有可用弹药"、**仍可上车**、可反复尝试 |
| 弹仓有弹药 + 手持该弹药右键 | 装填成功、音效正常、**弹仓里那一发被消耗**、弹匣有弹、可上车开火 |
| 开火后 | `LOADED = false`、进入冷却、冷却结束后可再次装填 |
| 冷却中右键 | 只显示冷却提示，**不吞掉上车交互** |
| 打包回收 | 已装填时返还 1 发弹药物品（+ 部署器物品） |

## 与服务端（Espetro 固定武器工事）的联动说明
- 服务端 Espetro 的"固定武器"工事把兑换得到的导弹**放进发射器自身的物品容器**
  （配置 `fixed_weapon.output = "container"`），刻意**不写弹匣**，就是为了交由本类的
  装填流程去消耗它 —— 所以修复后的预期行为是：**弹仓里那一发被扣掉**、弹匣真正有弹、
  玩家可以上车开火。
- 相关定义：`espetro:fixed_atgm_hj8`、`espetro:fixed_atgm_9m133`、`espetro:fixed_atgm_tow`
  （兑换 1 发 / 消耗 FOB 弹药 400）。
