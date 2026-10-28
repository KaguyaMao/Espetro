# 补给副手漏扫 / 弹匣填充 / 非创造禁开容器（2026-10-26）

## 1. 补给不再漏扫副手（刷物品漏洞）

`src/main/java/org/espetro/logistics/resupply/ResupplySessionManager.java`

问题：三处"玩家已有多少"的统计只遍历主背包 `player.getInventory().items`，副手 `offhand` 完全没算。
玩家把待补充的物品（弹药/弹匣）放副手 → 统计为 0 → 继续按"未达上限"发放；副手那份永远不被消耗，可无限刷。

改动：

- 新增哨兵槽位

```java
/**
 * Sentinel slot id for the offhand.  The player's offhand must be scanned by
 * every "how much of this item do I already own" check, otherwise a player
 * can park the supplied item in the offhand to keep claiming past the cap.
 */
private static final int OFFHAND_SLOT = -1;
```

- `countNormal(ServerPlayer, EntrySpec)`：主背包统计完后再加 `player.getInventory().offhand.get(0)`，
  判定条件抽成 `matchesCountedNormal(ItemStack, EntrySpec)`（`!isEmpty() && ResupplyItemIdentity.matchesNormal(...)`）。
- `matchingMagazines(...)`：新增 `collectMagazine(compat, spec, slot, stack, out)`，主背包循环之外再收集副手（槽位记为 `OFFHAND_SLOT = -1`）；
  `transactMagazine` 里对 `partial.slot == OFFHAND_SLOT` 用 `player.getInventory().offhand.get(0)` 定位目标。
- `snapshotInventory` / `restoreInventory`：快照改为"主背包 + 副手"（末尾多存一份 offhand），
  回滚时先还原主背包，再 `player.getInventory().offhand.set(0, snapshot.get(main))`，
  避免事务失败回滚时把副手改坏。

结果：副手里的待补充物品会被计入"已有数量"，也会被算进弹匣清单；放进副手不再能刷。

## 2. 弹匣：不再"发新弹匣丢旧弹匣"，改为把背包里的弹匣填满

同文件 + `src/main/java/org/espetro/compat/taczmagazines/*`。

旧逻辑（`transactMagazine`）：`compat.createFull(spec.template)` 造一个满弹匣；
若背包里有同族弹匣（不分余弹多少），就 `shrink(1)` 吃掉一个旧的，再 `add()` 新弹匣 →
打了一半的弹匣被整只丢掉，里面剩余子弹一并消失，玩家每次补充都在丢弹匣。

新逻辑：

```java
// 补给弹匣 = 把背包里已有的半空弹匣填满，而不是「发一个新弹匣再吃掉旧的」：
// 旧做法会连弹匣里剩余的子弹一起丢掉，玩家每次补充都要丢一次。
MagazineSlot partial = matching.stream().filter(slot -> !slot.full)
    .min(Comparator.comparingInt(MagazineSlot::rounds)
        .thenComparingInt(MagazineSlot::slot)).orElse(null);
if (partial == null) return TransactionResult.fail("背包里没有可填充的弹匣");
...
int capacity = spec.magazineIdentity.capacity();
ItemStack target = partial.slot == OFFHAND_SLOT
    ? player.getInventory().offhand.get(0)
    : player.getInventory().getItem(partial.slot);
if (!compat.setAmmoCount(target, capacity)) { ...回滚... }
```

- 选中的是**余弹最少**的那个匹配弹匣（同余弹按槽位序），直接原地补满；不再新建、不再丢弃。
- 成功提示改为：`已填满 1 个弹匣（<原余弹> → <容量> 发），消耗 <N> 弹药`。
- 背包里没有任何半空弹匣时不再凭空给弹匣，而是返回 `背包里没有可填充的弹匣`。

底层新增接口（`MagazineCompat`）：

```java
/**
 * Refills an existing magazine in place.  Used by the resupply menu so a
 * partially spent magazine is topped up instead of being replaced (which
 * used to delete the player's old magazine together with its rounds).
 *
 * @return true when the stack really is a magazine and the count was written
 */
boolean setAmmoCount(ItemStack stack, int count);
```

- `ReflectiveMagazineCompat`：先 `identity(stack)` 确认是同族弹匣，`Math.max(0, Math.min(capacity, count))` 夹取后
  反射调用 TaCZ 弹匣物品的 `setAmmoCount`；异常吞掉返回 `false`（事务回滚，不扣弹药）。
- `NoopMagazineCompat`：返回 `false`（没装 TaCZ Magazines 时保持旧行为不炸）。

配套：目录可用性判定 `hasReplaceableMagazineOrSpace` → `hasFillableMagazine`
（有半空弹匣才可点，理由文案 `背包里没有可填充的弹匣`）。

## 3. 非创造玩家无法打开任何容器（KubeJS）

服务端新脚本：`/kubejs/server_scripts/00_container_lock.js`（1400 B，已上传；本地 `build/tmp/00_container_lock.js`）

```js
const ALLOWED_MENU_NAMES = [
    'net.minecraft.world.inventory.InventoryMenu',
    'VehicleContainerMenu',
    'SuperbItemInterfaceMenu'
]

PlayerEvents.inventoryOpened(event => {
    const player = event.player
    if (player == null) return
    if (player.level.isClientSide()) return
    if (player.isCreative()) return
    const menu = event.inventoryContainer
    if (menu == null) return
    const menuClass = String(menu.getClass().getName())
    for (let i = 0; i < ALLOWED_MENU_NAMES.length; i++) {
        if (menuClass.indexOf(ALLOWED_MENU_NAMES[i]) >= 0) return
    }
    player.closeContainer()
    player.displayClientMessage(Text.red('只有创造模式才能打开容器'), true)
})
```

设计要点（写在脚本注释里）：

- 监听"菜单已打开"再关闭，而不是取消右键 —— Espetro 的补给站/弹药箱/职业轮盘走自定义界面与数据包，
  一旦在 `RightClickBlock` 上 `cancel()`，其它默认优先级监听器就收不到事件，会把核心玩法一起打断。
- 放行三个菜单：玩家背包 `InventoryMenu`、SBW 载具货舱 `*VehicleContainerMenu`（装卸物资用）、`SuperbItemInterfaceMenu`。

## 验证

- `gradlew build` 成功：`espetro-1.1.3-l.jar`，16,871,015 B，
  SHA256 `DAE8211F37F6E3575E283811E82621C23E558C02C201BA4AE74C4385AB035213`。
- 与线上 `espetro-1.1.3-x.jar` 逐类 hash 比对：**仅服务端类变化**
  （`logistics/resupply/ResupplySessionManager*`、`compat/taczmagazines/MagazineCompat`、`NoopMagazineCompat`、`ReflectiveMagazineCompat`），
  **无任何 `network/` 或 `client/` 类变化** → 无协议变化，客户端无需同步更新。
- 已上传 `/mods/espetro-1.1.3-x.jar`（16,871,015 B，大小核对一致）。
- 已上传 `/kubejs/server_scripts/00_container_lock.js` 并执行 `/reload`：日志 `Reloaded with no KubeJS errors!`，容器锁即时生效。

## 待生效

- jar 内的 1、2 两项需要**重启**才生效（模组 jar 只在启动时加载）。第 3 项已生效。
