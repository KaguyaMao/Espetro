/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.minecraft.world.inventory.MenuType
 */
package dev.latvian.mods.kubejs.bindings.event;

import dev.latvian.mods.kubejs.bindings.event.ItemEvents;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.event.Extra;
import dev.latvian.mods.kubejs.player.ChestEventJS;
import dev.latvian.mods.kubejs.player.InventoryChangedEventJS;
import dev.latvian.mods.kubejs.player.InventoryEventJS;
import dev.latvian.mods.kubejs.player.PlayerAdvancementEventJS;
import dev.latvian.mods.kubejs.player.PlayerChatDecorateEventJS;
import dev.latvian.mods.kubejs.player.PlayerRespawnedEventJS;
import dev.latvian.mods.kubejs.player.SimplePlayerEventJS;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

public interface PlayerEvents {
    public static final Extra SUPPORTS_MENU_TYPE = new Extra().transformer(PlayerEvents::transformMenuType).identity().describeType(context -> context.javaType(MenuType.class));
    public static final EventGroup GROUP = EventGroup.of("PlayerEvents");
    public static final EventHandler LOGGED_IN = GROUP.server("loggedIn", () -> SimplePlayerEventJS.class);
    public static final EventHandler LOGGED_OUT = GROUP.server("loggedOut", () -> SimplePlayerEventJS.class);
    public static final EventHandler RESPAWNED = GROUP.server("respawned", () -> PlayerRespawnedEventJS.class);
    public static final EventHandler TICK = GROUP.common("tick", () -> SimplePlayerEventJS.class);
    public static final EventHandler CHAT = GROUP.server("chat", () -> PlayerChatDecorateEventJS.class).hasResult();
    public static final EventHandler DECORATE_CHAT = GROUP.server("decorateChat", () -> PlayerChatDecorateEventJS.class);
    public static final EventHandler ADVANCEMENT = GROUP.server("advancement", () -> PlayerAdvancementEventJS.class).extra(Extra.ID).hasResult();
    public static final EventHandler INVENTORY_OPENED = GROUP.common("inventoryOpened", () -> InventoryEventJS.class).extra(SUPPORTS_MENU_TYPE);
    public static final EventHandler INVENTORY_CLOSED = GROUP.common("inventoryClosed", () -> InventoryEventJS.class).extra(SUPPORTS_MENU_TYPE);
    public static final EventHandler INVENTORY_CHANGED = GROUP.common("inventoryChanged", () -> InventoryChangedEventJS.class).extra(ItemEvents.SUPPORTS_ITEM);
    public static final EventHandler CHEST_OPENED = GROUP.common("chestOpened", () -> ChestEventJS.class).extra(SUPPORTS_MENU_TYPE);
    public static final EventHandler CHEST_CLOSED = GROUP.common("chestClosed", () -> ChestEventJS.class).extra(SUPPORTS_MENU_TYPE);

    public static Object transformMenuType(Object o) {
        if (o == null) {
            return null;
        }
        if (o instanceof MenuType) {
            MenuType menuType = (MenuType)o;
            return menuType;
        }
        if (o instanceof AbstractContainerMenu) {
            AbstractContainerMenu menu = (AbstractContainerMenu)o;
            try {
                return menu.m_6772_();
            }
            catch (Exception ex) {
                return null;
            }
        }
        ResourceLocation id = ResourceLocation.m_135820_((String)o.toString());
        return id == null ? null : RegistryInfo.MENU.getValue(id);
    }
}

