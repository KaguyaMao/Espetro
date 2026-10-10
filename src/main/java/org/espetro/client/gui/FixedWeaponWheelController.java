package org.espetro.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.espetro.network.FixedWeaponWheelPacket;
import org.espetro.network.NetworkManager;
import org.esradial.client.Actions;
import org.esradial.client.ItemIcon;
import org.esradial.client.RadialMenuBuilder;
import org.esradial.client.RadialMenuClientApi;
import org.esradial.client.RadialMenuData;
import org.esradial.client.RadialMenuRegistry;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 固定武器兑换轮盘（EsRadial）。
 *
 * <p>流程：对固定武器按 F → {@code FixedWeaponOpenPacket} → 服务端回 {@link FixedWeaponWheelPacket}
 * → 这里打开轮盘；轮盘里点"兑换" → {@code FixedWeaponExchangePacket} → 服务端扣 FOB 弹药并发弹，
 * 随后**关闭轮盘**（想再换就再按一次 F，等于一个档位可连按）。</p>
 */
public final class FixedWeaponWheelController {

    private static final String OWNER = "espetro_fixed_weapon";
    private static final ResourceLocation MENU = id("fixed_weapon/exchange");
    private static final ResourceLocation SELECT = id("fixed_weapon/select");
    private static final String AVAILABLE = "#FFFFD54F";
    private static final String HOVER = "#FFFFFFFF";
    private static final String UNAVAILABLE = "#FF44484D";

    private static boolean initialized;
    private static UUID weaponId;
    private static FixedWeaponWheelPacket latest;
    private static boolean menuWasActive;
    private static boolean holdKeyWasDown;

    private FixedWeaponWheelController() {
    }

    public static void initialize() {
        if (initialized) {
            return;
        }
        initialized = true;
        Actions.register(SELECT, params -> {
            if (weaponId == null || latest == null) {
                return;
            }
            if (!latest.affordable()) {
                if (!latest.reason().isBlank()) {
                    EspetroTipNotifier.showDenial("无法兑换", latest.reason());
                }
                return;
            }
            // 兑换后**不关闭**轮盘：按住 F 期间保持开启，便于连续兑换；
            // 服务端兑换成功后会回发刷新后的轮盘数据（FOB 弹药已扣除）。
            NetworkManager.sendFixedWeaponExchange(weaponId);
        });
    }

    /** 服务端下发轮盘数据 → 打开（或刷新）轮盘。 */
    public static void update(FixedWeaponWheelPacket packet) {
        if (packet == null) {
            return;
        }
        latest = packet;
        weaponId = packet.weaponEntityId();
        show(packet);
    }

    private static void show(FixedWeaponWheelPacket packet) {
        RadialMenuData data = build(packet);
        RadialMenuRegistry.setMenus(OWNER, List.of(data));
        boolean existing = RadialMenuClientApi.activeMenuId().filter(MENU::equals).isPresent();
        if (!existing || !RadialMenuClientApi.replace(data)) {
            RadialMenuClientApi.open(data, RadialMenuClientApi.OpenOptions.click(OWNER));
        }
        menuWasActive = true;
    }

    /**
     * F 键按住的每一 tick 调用：<b>按住期间保持轮盘开启，松手即关闭</b>。
     * 期间轮盘被关掉（例如误按 Esc）但 F 仍按住 → 自动重新打开。
     */
    public static void holdTick(boolean keyDown) {
        if (!keyDown) {
            if (holdKeyWasDown) {
                holdKeyWasDown = false;
                if (isActive()) {
                    RadialMenuClientApi.close();
                }
                weaponId = null;
                latest = null;
                menuWasActive = false;
            }
            return;
        }
        holdKeyWasDown = true;
        if (weaponId != null && latest != null && !isActive()) {
            show(latest);
        }
    }

    /** 只做状态簿记，不再因为菜单关闭而丢弃会话（会话生命周期交给 holdTick）。 */
    public static void tick() {
        if (weaponId == null) {
            return;
        }
        menuWasActive = isActive();
    }

    public static boolean isActive() {
        return weaponId != null
            && RadialMenuClientApi.activeMenuId().filter(MENU::equals).isPresent();
    }

    private static RadialMenuData build(FixedWeaponWheelPacket packet) {
        ItemStack icon = resolveIcon(packet.ammoItemId());
        String label = "兑换 " + packet.amount() + " 发 §b[" + packet.ammoCost() + "]";
        if (!packet.affordable() && !packet.reason().isBlank()) {
            label += " §c" + packet.reason();
        }
        RadialMenuBuilder builder = new RadialMenuBuilder(MENU)
            .title(Component.literal(packet.displayName() + " · FOB 弹药 " + packet.fobAmmo()))
            .radii(44, 108)
            .animationSpeed(1.25F)
            .ringColors(List.of("#B824292B", "#C832383A"))
            .persistentSlot("espetro.fixed_weapon.exchange", new ItemIcon(icon, 1.15F),
                Actions.script(SELECT, Map.of()), Component.literal(label),
                packet.affordable() ? HOVER : UNAVAILABLE,
                packet.affordable() ? AVAILABLE : UNAVAILABLE);
        if (!packet.affordable()) {
            builder = builder.disabledLast(Component.literal(
                packet.reason().isBlank() ? "当前不可兑换" : packet.reason()));
        }
        return builder.build();
    }

    private static ItemStack resolveIcon(String itemId) {
        if (itemId != null && !itemId.isBlank()) {
            ResourceLocation key = ResourceLocation.tryParse(itemId.trim());
            Item item = key == null ? null : BuiltInRegistries.ITEM.get(key);
            if (item != null) {
                return new ItemStack(item);
            }
        }
        // 虚拟弹药（@HeavyAmmo 等）没有物品：给一个通用图标
        Item fallback = BuiltInRegistries.ITEM.get(id("fixed_weapon_icon"));
        return fallback == null || fallback == Items.AIR
            ? new ItemStack(Items.IRON_INGOT) : new ItemStack(fallback);
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath("espetro", path);
    }
}
