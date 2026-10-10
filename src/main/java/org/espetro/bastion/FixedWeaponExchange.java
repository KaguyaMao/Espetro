package org.espetro.bastion;

import com.atsuishio.superbwarfare.data.gun.AmmoConsumer;
import com.atsuishio.superbwarfare.data.gun.GunData;
import com.atsuishio.superbwarfare.data.gun.GunProp;
import com.atsuishio.superbwarfare.data.vehicle.subdata.SeatInfo;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.network.PacketDistributor;
import org.espetro.Espetro;
import org.espetro.network.NetworkManager;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * 固定武器的「FOB 弹药兑换」。
 *
 * <p>玩家对己方固定武器按 F（或轮盘选择）时由服务端权威执行：</p>
 * <ol>
 *   <li>校验该实体确实是「已启用 fixed_weapon 的己方工事」，敌方一律拒绝；</li>
 *   <li>找出覆盖它的己方 Radio（= 所在 FOB），校验并扣除 {@code fob_ammo_cost} 点弹药；</li>
 *   <li>按配置产出 {@code exchange_amount} 发弹药：
 *       <ul>
 *         <li>{@code output=player}：作为物品放进玩家背包（配置 {@code ammo_item}，留空则取该武器实际弹药）；</li>
 *         <li>{@code output=vehicle}：优先直接补进武器的<b>弹药池/弹夹</b>
 *             （虚拟弹药 {@code @HeavyAmmo} 等 → {@code GunData.virtualAmmo}；
 *              物品弹药 → {@code GunData.ammo} 直到弹匣上限），装不下的部分再进载具物品箱，
 *              没有物品箱则回退玩家背包。</li>
 *       </ul>
 *   </li>
 * </ol>
 * <p>单档位设计：每调用一次兑换一次，可连续按。</p>
 */
public final class FixedWeaponExchange {

    private FixedWeaponExchange() {
    }

    /**
     * 客户端按 F：校验目标后下发兑换轮盘数据。
     *
     * <p>目标不是"己方已启用的固定武器"时**保持静默**（不提示、不打扰）。</p>
     */
    public static void openWheel(ServerPlayer player, @Nullable UUID weaponEntityId) {
        if (player == null || weaponEntityId == null) {
            return;
        }
        if (!(player.level() instanceof ServerLevel level)) {
            return;
        }
        Entity weapon = level.getEntity(weaponEntityId);
        if (weapon == null || !weapon.isAlive()) {
            return;
        }
        FortificationManager fm = FortificationManager.getInstance();
        FortificationConfig.FortificationDef def = fm.definitionOfEntity(weapon);
        if (def == null || def.fixedWeapon == null || !def.fixedWeapon.enabled) {
            Espetro.LOGGER.info("固定武器轮盘请求被忽略：实体 {} ({}) 不是已启用的固定武器工事",
                weaponEntityId, weapon.getType());
            return;
        }
        String owner = fm.teamOfEntity(weapon);
        if (FortificationManager.isEnemyOf(player, owner)) {
            return;
        }
        FortificationConfig.FixedWeapon spec = def.fixedWeapon;
        int cost = Math.max(0, spec.fobAmmoCost);
        List<BastionData> radios = BastionManager.getInstance()
            .findCoveringRadios(level, weapon.blockPosition(), owner);
        int fobAmmo = radios.isEmpty() ? 0 : radios.get(0).getAmmunitionSupplies();
        boolean affordable;
        String reason = "";
        if (radios.isEmpty()) {
            affordable = false;
            reason = "必须在己方 Radio（FOB）范围内";
        } else if (fobAmmo < cost) {
            affordable = false;
            reason = "FOB 弹药不足";
        } else {
            affordable = true;
        }
        String ammoItemId = "";
        ItemStack ammoTemplate = resolveAmmoStack(weapon, spec);
        if (ammoTemplate != null && !ammoTemplate.isEmpty()) {
            ResourceLocation key = BuiltInRegistries.ITEM.getKey(ammoTemplate.getItem());
            if (key != null) {
                ammoItemId = key.toString();
            }
        }
        NetworkManager.NET.send(PacketDistributor.PLAYER.with(() -> player),
            new org.espetro.network.FixedWeaponWheelPacket(weaponEntityId, def.displayName, ammoItemId,
                Math.max(1, spec.exchangeAmount), cost, fobAmmo, affordable, reason));
    }

    /** 客户端发起的兑换请求（服务端线程执行）。 */
    public static void handle(ServerPlayer player, @Nullable UUID weaponEntityId) {
        if (player == null || weaponEntityId == null) {
            return;
        }
        if (!(player.level() instanceof ServerLevel level)) {
            return;
        }
        Entity weapon = level.getEntity(weaponEntityId);
        if (weapon == null || !weapon.isAlive()) {
            message(player, "§c固定武器不存在或已被摧毁。");
            return;
        }
        FortificationManager fm = FortificationManager.getInstance();
        FortificationConfig.FortificationDef def = fm.definitionOfEntity(weapon);
        if (def == null || def.fixedWeapon == null || !def.fixedWeapon.enabled) {
            Espetro.LOGGER.info("固定武器兑换被拒：实体 {} ({}) 未匹配到已启用的固定武器定义（def={}）",
                weaponEntityId, weapon.getType(),
                def == null ? "无" : def.id + "/enabled=" + (def.fixedWeapon != null && def.fixedWeapon.enabled));
            return;
        }
        String owner = fm.teamOfEntity(weapon);
        if (FortificationManager.isEnemyOf(player, owner)) {
            message(player, "§c这是敌方固定武器，无法兑换。");
            return;
        }
        FortificationConfig.FixedWeapon spec = def.fixedWeapon;
        List<BastionData> radios = BastionManager.getInstance()
            .findCoveringRadios(level, weapon.blockPosition(), owner);
        if (radios.isEmpty()) {
            message(player, "§c必须在己方 Radio（FOB）范围内才能兑换。");
            return;
        }
        BastionData fob = radios.get(0);
        int cost = Math.max(0, spec.fobAmmoCost);
        int amount = Math.max(1, spec.exchangeAmount);
        if (fob.getAmmunitionSupplies() < cost) {
            message(player, "§cFOB 弹药不足（需要 " + cost + "，当前 " + fob.getAmmunitionSupplies() + "）。");
            return;
        }

        // 先装载，成功后才扣弹药（玩家背包路径先校验能否放下）
        String outputMode = spec.output == null ? "vehicle" : spec.output.trim().toLowerCase(java.util.Locale.ROOT);
        boolean toPlayer = "player".equals(outputMode);
        // container：只把弹药放进武器自身的物品箱，交给武器自己的装填逻辑消耗
        // （例如 dragonrise 的反坦克导弹：右键装填时从发射器容器取弹，玩家背包里的不会被消耗）
        boolean toContainer = "container".equals(outputMode);
        ItemStack ammoTemplate = resolveAmmoStack(weapon, spec);
        if (toPlayer && ammoTemplate == null) {
            message(player, "§c该武器使用虚拟弹药，无法放入背包（请把 output 设为 vehicle）。");
            return;
        }
        int delivered;
        if (toPlayer) {
            delivered = giveToPlayer(player, ammoTemplate, amount);
            if (delivered <= 0) {
                message(player, "§c背包已满，无法兑换。");
                return;
            }
        } else {
            if (toContainer) {
                if (ammoTemplate == null) {
                    message(player, "§c该武器使用虚拟弹药，无法放入容器。");
                    return;
                }
                delivered = giveToVehicleInventory(weapon, ammoTemplate, amount);
                if (delivered < amount && delivered > 0) {
                    // 容器没塞完的余量给玩家，避免丢弹
                    giveToPlayer(player, ammoTemplate, amount - delivered);
                    delivered = amount;
                }
                if (delivered <= 0) {
                    message(player, "§c该武器没有可用的弹药容器，无法兑换。");
                    return;
                }
            } else {
                delivered = fillWeaponAmmo(weapon, amount);
                int leftover = amount - delivered;
                if (leftover > 0 && ammoTemplate != null) {
                    int fromVehicle = giveToVehicleInventory(weapon, ammoTemplate, leftover);
                    delivered += fromVehicle;
                    leftover = amount - delivered;
                }
                if (leftover > 0 && ammoTemplate != null) {
                    delivered += giveToPlayer(player, ammoTemplate, leftover);
                }
                if (delivered <= 0) {
                    message(player, "§c弹药无处可装（武器弹药池已满且没有可用的容器）。");
                    return;
                }
            }
        }
        Espetro.LOGGER.info("固定武器兑换: {} 给予 {} x{}（AmmoType 模板={}）",
            def.id, ammoTemplate == null ? "-" : ammoTemplate.getItem(), delivered,
            ammoTemplate == null ? "虚拟弹药" : ammoTemplate.getTag());

        if (cost > 0 && !fob.consumeAmmunitionSupplies(cost)) {
            message(player, "§cFOB 弹药不足。");
            return;
        }
        FobSupplyTracker.notifySupplyChanged(fob);
        String where = toPlayer ? "已放入背包"
            : (toContainer ? "已放入武器弹仓（手持弹药右键装填）" : "已装填进固定武器");
        message(player, "§a已兑换 " + delivered + " 发"
            + (cost > 0 ? "（消耗 FOB 弹药 " + cost + "，剩余 " + fob.getAmmunitionSupplies() + "）" : "")
            + "，" + where);
        // 轮盘保持开启：回发刷新后的状态（FOB 弹药已扣除、是否还够也一并更新），
        // 玩家按住 F 不动就能连续兑换。
        openWheel(player, weaponEntityId);
    }

    private static void message(ServerPlayer player, String text) {
        player.displayClientMessage(Component.literal(text), true);
    }

    /** 往玩家背包塞 count 发（保留模板栈的 NBT，例如导弹型号），装不下的丢在脚下。 */
    private static int giveToPlayer(ServerPlayer player, @Nullable ItemStack template, int count) {
        if (template == null || template.isEmpty() || count <= 0) {
            return 0;
        }
        ItemStack stack = template.copy();
        stack.setCount(count);
        player.getInventory().add(stack);
        int delivered = count - stack.getCount();
        if (!stack.isEmpty()) {
            player.drop(stack, false);
            delivered = count;
        }
        return delivered;
    }

    /** 往载具/武器自带的物品箱（Forge ITEM_HANDLER）塞 count 发（保留 NBT），返回实际塞入数量。 */
    private static int giveToVehicleInventory(Entity weapon, @Nullable ItemStack template, int count) {
        if (weapon == null || template == null || template.isEmpty() || count <= 0) {
            return 0;
        }
        IItemHandler handler;
        try {
            handler = weapon.getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(null);
        } catch (Throwable t) {
            return 0;
        }
        if (handler == null || handler.getSlots() <= 0) {
            return 0;
        }
        int remaining = count;
        int chunkSize = Math.max(1, template.getMaxStackSize());
        try {
            if (weapon instanceof VehicleEntity vehicle) {
                chunkSize = Math.max(1, Math.min(chunkSize, vehicle.getMaxStackSize()));
            }
        } catch (Throwable ignored) {
            // 取不到载具上限就用物品堆叠上限
        }
        int guard = 0;
        while (remaining > 0 && guard++ < 1024) {
            int chunk = Math.min(remaining, chunkSize);
            ItemStack piece = template.copy();
            piece.setCount(chunk);
            ItemStack left = net.minecraftforge.items.ItemHandlerHelper.insertItemStacked(handler, piece, false);
            int moved = chunk - left.getCount();
            if (moved <= 0) {
                break;
            }
            remaining -= moved;
        }
        return count - remaining;
    }

    /**
     * 把 rounds 发补进武器弹药池/弹夹。
     *
     * @return 实际装入数量（虚拟弹药一次装满；物品弹药受弹匣上限限制）
     */
    private static int fillWeaponAmmo(Entity weapon, int rounds) {
        if (!(weapon instanceof VehicleEntity vehicle) || rounds <= 0) {
            return 0;
        }
        int remaining = rounds;
        try {
            for (int seat = 0; seat < vehicle.getMaxPassengers() && remaining > 0; seat++) {
                SeatInfo seatInfo = vehicle.getSeat(seat);
                if (seatInfo == null) {
                    continue;
                }
                List<String> weapons = seatInfo.weapons();
                if (weapons == null || weapons.isEmpty()) {
                    continue;
                }
                for (int w = 0; w < weapons.size() && remaining > 0; w++) {
                    GunData gun = vehicle.getGunData(seat, w);
                    if (gun == null) {
                        continue;
                    }
                    if (isVirtualAmmo(gun)) {
                        gun.virtualAmmo.add(remaining);
                        remaining = 0;
                        break;
                    }
                    int magazine = magazineOf(gun);
                    if (magazine <= 0) {
                        continue;
                    }
                    int space = Math.max(0, magazine - gun.ammo.get());
                    if (space <= 0) {
                        continue;
                    }
                    int put = Math.min(space, remaining);
                    gun.ammo.add(put);
                    remaining -= put;
                }
            }
        } catch (Throwable t) {
            Espetro.LOGGER.warn("固定武器补弹失败: {}", t.toString());
        }
        return rounds - remaining;
    }

    /** 该枪用的是虚拟弹药（{@code @HeavyAmmo} / {@code @RifleAmmo} 之类，没有对应物品）。 */
    private static boolean isVirtualAmmo(GunData gun) {
        try {
            List<AmmoConsumer> consumers = gun.get(GunProp.AMMO_CONSUMER);
            if (consumers == null || consumers.isEmpty()) {
                return false;
            }
            for (AmmoConsumer consumer : consumers) {
                String ammo = consumer.getAmmo();
                if (ammo != null && ammo.startsWith("@")) {
                    return true;
                }
            }
        } catch (Throwable ignored) {
            // 取不到就按物品弹药处理
        }
        return false;
    }

    private static int magazineOf(GunData gun) {
        try {
            Integer mag = gun.get(GunProp.MAGAZINE);
            return mag == null ? 0 : Math.max(0, mag);
        } catch (Throwable ignored) {
            return 0;
        }
    }

    /**
     * 兑换用的<b>弹药模板栈</b>：配置 {@code ammo_item} 优先；留空则取该武器<b>当前选中</b>的
     * {@code AmmoConsumer.stack()}（保留 NBT，例如导弹型号）——外部手动装填依赖它通过 SBW 的
     * {@code isAmmoItem(stack)} 判定，只复制物品、丢掉 NBT 会导致"右键无法装填"。
     */
    @Nullable
    private static ItemStack resolveAmmoStack(Entity weapon, FortificationConfig.FixedWeapon spec) {
        String configured = spec.ammoItem == null ? "" : spec.ammoItem.trim();
        if (!configured.isEmpty()) {
            return resolveAmmoItem(weapon, spec) == null ? null
                : new ItemStack(resolveAmmoItem(weapon, spec));
        }
        if (!(weapon instanceof VehicleEntity vehicle)) {
            return null;
        }
        try {
            for (int seat = 0; seat < vehicle.getMaxPassengers(); seat++) {
                SeatInfo seatInfo = vehicle.getSeat(seat);
                if (seatInfo == null || seatInfo.weapons() == null) {
                    continue;
                }
                for (int w = 0; w < seatInfo.weapons().size(); w++) {
                    GunData gun = vehicle.getGunData(seat, w);
                    if (gun == null) {
                        continue;
                    }
                    List<AmmoConsumer> consumers = gun.get(GunProp.AMMO_CONSUMER);
                    if (consumers == null || consumers.isEmpty()) {
                        continue;
                    }
                    try {
                        AmmoConsumer selected = gun.selectedAmmoConsumer();
                        if (selected != null) {
                            ItemStack stack = selected.stack();
                            if (stack != null && !stack.isEmpty()) {
                                return stack.copy();
                            }
                        }
                    } catch (Throwable ignored) {
                        // 取不到"当前选中"就退化为第一个
                    }
                    for (AmmoConsumer consumer : consumers) {
                        ItemStack stack = consumer.stack();
                        if (stack != null && !stack.isEmpty()) {
                            return stack.copy();
                        }
                    }
                }
            }
        } catch (Throwable ignored) {
            // 无可用弹药模板
        }
        return null;
    }

    /** 兑换用的弹药物品：配置 {@code ammo_item} 优先，留空则取该武器实际消耗的弹药物品。 */
    @Nullable
    private static Item resolveAmmoItem(Entity weapon, FortificationConfig.FixedWeapon spec) {
        String configured = spec.ammoItem == null ? "" : spec.ammoItem.trim();
        if (!configured.isEmpty()) {
            ResourceLocation id = ResourceLocation.tryParse(configured);
            Item item = id == null ? null : BuiltInRegistries.ITEM.get(id);
            return item == null || item == net.minecraft.world.item.Items.AIR ? null : item;
        }
        if (!(weapon instanceof VehicleEntity vehicle)) {
            return null;
        }
        try {
            for (int seat = 0; seat < vehicle.getMaxPassengers(); seat++) {
                SeatInfo seatInfo = vehicle.getSeat(seat);
                if (seatInfo == null || seatInfo.weapons() == null) {
                    continue;
                }
                for (int w = 0; w < seatInfo.weapons().size(); w++) {
                    GunData gun = vehicle.getGunData(seat, w);
                    if (gun == null) {
                        continue;
                    }
                    List<AmmoConsumer> consumers = gun.get(GunProp.AMMO_CONSUMER);
                    if (consumers == null) {
                        continue;
                    }
                    for (AmmoConsumer consumer : consumers) {
                        ItemStack stack = consumer.stack();
                        if (stack != null && !stack.isEmpty()) {
                            return stack.getItem();
                        }
                    }
                }
            }
        } catch (Throwable ignored) {
            // 无可用物品弹药
        }
        return null;
    }
}
