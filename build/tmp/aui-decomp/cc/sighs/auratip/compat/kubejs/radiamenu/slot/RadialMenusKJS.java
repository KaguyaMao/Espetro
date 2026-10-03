/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.latvian.mods.kubejs.typings.Info
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 */
package cc.sighs.auratip.compat.kubejs.radiamenu.slot;

import cc.sighs.auratip.api.client.RadialMenuClientApi;
import cc.sighs.auratip.api.radiamenu.icon.IRadialIcon;
import cc.sighs.auratip.api.radiamenu.icon.ItemIcon;
import cc.sighs.auratip.api.radiamenu.icon.TextureIcon;
import cc.sighs.auratip.compat.kubejs.radiamenu.slot.RadialMenuExtraSlotRegistry;
import cc.sighs.auratip.data.RadialMenuData;
import cc.sighs.auratip.data.action.Action;
import dev.latvian.mods.kubejs.typings.Info;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class RadialMenusKJS {
    @Info(value="Client-only: open a radial menu by id. The id can omit namespace (defaults to kubejs).")
    public static void open(String menuId) {
        if (menuId == null || menuId.isEmpty()) {
            return;
        }
        RadialMenuClientApi.open(RadialMenusKJS.normalizeId(menuId));
    }

    @Info(value="Client-only: replace the active radial menu by id without close/open animation.")
    public static boolean replace(String menuId) {
        if (menuId == null || menuId.isEmpty()) {
            return false;
        }
        return RadialMenuClientApi.replace(RadialMenusKJS.normalizeId(menuId));
    }

    @Info(value="Client-only: replace the active radial menu with dynamic RadialMenuData.")
    public static boolean replace(RadialMenuData menuData) {
        return menuData != null && RadialMenuClientApi.replace(menuData);
    }

    @Info(value="Client-only: true while a radial menu overlay is active.")
    public static boolean isActive() {
        return RadialMenuClientApi.isActive();
    }

    @Info(value="Client-only: active menu id, or an empty string when no menu is active.")
    public static String activeMenuId() {
        return RadialMenuClientApi.activeMenuId().map(ResourceLocation::toString).orElse("");
    }

    @Info(value="Client-only: hovered slot index, or -1 when no slot is hovered.")
    public static int hoveredSlotIndex() {
        return RadialMenuClientApi.hoveredSlotIndex();
    }

    @Info(value="Append an extra slot to all base radial menus. The slot is merged into the final menu at open-time.")
    public static void addSlot(String name, String iconId, Action action, Component text, String highlightColor) {
        if (name == null || name.isEmpty()) {
            return;
        }
        if (iconId == null || iconId.isEmpty()) {
            return;
        }
        if (action == null) {
            return;
        }
        RadialMenuData.Slot slot = new RadialMenuData.Slot(name, new TextureIcon(new ResourceLocation(iconId), 1.0f), action, Optional.ofNullable(text), Optional.ofNullable(highlightColor));
        RadialMenuExtraSlotRegistry.addSlot(slot);
    }

    @Info(value="Append an extra slot to all base radial menus. The slot is merged into the final menu at open-time. (TextureIcon)")
    public static void addSlotWithIcon(String name, TextureIcon icon, Action action, Component text, String highlightColor) {
        if (name == null || name.isEmpty()) {
            return;
        }
        if (icon == null) {
            return;
        }
        if (action == null) {
            return;
        }
        RadialMenuData.Slot slot = new RadialMenuData.Slot(name, icon, action, Optional.ofNullable(text), Optional.ofNullable(highlightColor));
        RadialMenuExtraSlotRegistry.addSlot(slot);
    }

    @Info(value="Append an extra slot with an item icon to all base radial menus.")
    public static void addSlotItem(String name, ItemIcon icon, Action action, Component text, String highlightColor) {
        if (name == null || name.isEmpty()) {
            return;
        }
        if (icon == null) {
            return;
        }
        if (action == null) {
            return;
        }
        RadialMenuData.Slot slot = new RadialMenuData.Slot(name, icon, action, Optional.ofNullable(text), Optional.ofNullable(highlightColor));
        RadialMenuExtraSlotRegistry.addSlot(slot);
    }

    @Info(value="Append a configurable extra texture slot to all menus. baseColor is visible without hover.")
    public static void addSlot(String name, String iconId, Action action, Component text, String highlightColor, boolean closeAfterAction, String baseColor) {
        if (iconId == null || iconId.isEmpty()) {
            return;
        }
        RadialMenusKJS.addConfiguredGlobal(name, new TextureIcon(new ResourceLocation(iconId), 1.0f), action, text, highlightColor, closeAfterAction, baseColor);
    }

    @Info(value="Append a persistent extra texture slot to all menus.")
    public static void addPersistentSlot(String name, String iconId, Action action, Component text, String highlightColor, String baseColor) {
        RadialMenusKJS.addSlot(name, iconId, action, text, highlightColor, false, baseColor);
    }

    @Info(value="Append a configurable TextureIcon extra slot to all menus.")
    public static void addSlotWithIcon(String name, TextureIcon icon, Action action, Component text, String highlightColor, boolean closeAfterAction, String baseColor) {
        RadialMenusKJS.addConfiguredGlobal(name, icon, action, text, highlightColor, closeAfterAction, baseColor);
    }

    @Info(value="Append a configurable ItemIcon extra slot to all menus.")
    public static void addSlotItem(String name, ItemIcon icon, Action action, Component text, String highlightColor, boolean closeAfterAction, String baseColor) {
        RadialMenusKJS.addConfiguredGlobal(name, icon, action, text, highlightColor, closeAfterAction, baseColor);
    }

    @Info(value="Remove extra slots by name from all base radial menus. Removes all slots with the same name added via KubeJS.")
    public static void removeSlot(String name) {
        if (name == null || name.isEmpty()) {
            return;
        }
        RadialMenuExtraSlotRegistry.removeSlot(name);
    }

    @Info(value="Append an extra slot to a specific base menu id (only affects that menu). menuId is a ResourceLocation string.")
    public static void addSlot(String menuId, String name, String iconId, Action action, Component text, String highlightColor) {
        if (menuId == null || menuId.isEmpty()) {
            return;
        }
        if (name == null || name.isEmpty()) {
            return;
        }
        if (iconId == null || iconId.isEmpty()) {
            return;
        }
        if (action == null) {
            return;
        }
        ResourceLocation targetMenuId = new ResourceLocation(menuId);
        RadialMenuData.Slot slot = new RadialMenuData.Slot(name, new TextureIcon(new ResourceLocation(iconId), 1.0f), action, Optional.ofNullable(text), Optional.ofNullable(highlightColor));
        RadialMenuExtraSlotRegistry.addSlotForMenu(targetMenuId, slot);
    }

    @Info(value="Append an extra slot to a specific base menu id (only affects that menu). menuId is a ResourceLocation string. (TextureIcon)")
    public static void addSlotWithIcon(String menuId, String name, TextureIcon icon, Action action, Component text, String highlightColor) {
        if (menuId == null || menuId.isEmpty()) {
            return;
        }
        if (name == null || name.isEmpty()) {
            return;
        }
        if (icon == null) {
            return;
        }
        if (action == null) {
            return;
        }
        ResourceLocation targetMenuId = new ResourceLocation(menuId);
        RadialMenuData.Slot slot = new RadialMenuData.Slot(name, icon, action, Optional.ofNullable(text), Optional.ofNullable(highlightColor));
        RadialMenuExtraSlotRegistry.addSlotForMenu(targetMenuId, slot);
    }

    @Info(value="Append an extra slot with an item icon to a specific base menu id.")
    public static void addSlotItem(String menuId, String name, ItemIcon icon, Action action, Component text, String highlightColor) {
        if (menuId == null || menuId.isEmpty()) {
            return;
        }
        if (name == null || name.isEmpty()) {
            return;
        }
        if (icon == null) {
            return;
        }
        if (action == null) {
            return;
        }
        ResourceLocation targetMenuId = new ResourceLocation(menuId);
        RadialMenuData.Slot slot = new RadialMenuData.Slot(name, icon, action, Optional.ofNullable(text), Optional.ofNullable(highlightColor));
        RadialMenuExtraSlotRegistry.addSlotForMenu(targetMenuId, slot);
    }

    @Info(value="Append a configurable extra texture slot to a specific menu.")
    public static void addSlot(String menuId, String name, String iconId, Action action, Component text, String highlightColor, boolean closeAfterAction, String baseColor) {
        if (menuId == null || menuId.isEmpty() || iconId == null || iconId.isEmpty()) {
            return;
        }
        RadialMenusKJS.addConfiguredForMenu(RadialMenusKJS.normalizeId(menuId), name, new TextureIcon(new ResourceLocation(iconId), 1.0f), action, text, highlightColor, closeAfterAction, baseColor);
    }

    @Info(value="Append a persistent extra texture slot to a specific menu.")
    public static void addPersistentSlot(String menuId, String name, String iconId, Action action, Component text, String highlightColor, String baseColor) {
        RadialMenusKJS.addSlot(menuId, name, iconId, action, text, highlightColor, false, baseColor);
    }

    @Info(value="Append a configurable TextureIcon extra slot to a specific menu.")
    public static void addSlotWithIcon(String menuId, String name, TextureIcon icon, Action action, Component text, String highlightColor, boolean closeAfterAction, String baseColor) {
        if (menuId == null || menuId.isEmpty()) {
            return;
        }
        RadialMenusKJS.addConfiguredForMenu(RadialMenusKJS.normalizeId(menuId), name, icon, action, text, highlightColor, closeAfterAction, baseColor);
    }

    @Info(value="Append a configurable ItemIcon extra slot to a specific menu.")
    public static void addSlotItem(String menuId, String name, ItemIcon icon, Action action, Component text, String highlightColor, boolean closeAfterAction, String baseColor) {
        if (menuId == null || menuId.isEmpty()) {
            return;
        }
        RadialMenusKJS.addConfiguredForMenu(RadialMenusKJS.normalizeId(menuId), name, icon, action, text, highlightColor, closeAfterAction, baseColor);
    }

    @Info(value="Remove extra slots by name from a specific base menu id. Removes all slots with the same name added via KubeJS for that menu.")
    public static void removeSlotForMenu(String menuId, String name) {
        if (menuId == null || menuId.isEmpty()) {
            return;
        }
        if (name == null || name.isEmpty()) {
            return;
        }
        RadialMenuExtraSlotRegistry.removeSlotForMenu(RadialMenusKJS.normalizeId(menuId), name);
    }

    private static void addConfiguredGlobal(String name, IRadialIcon icon, Action action, Component text, String highlightColor, boolean closeAfterAction, String baseColor) {
        RadialMenuData.Slot slot = RadialMenusKJS.configuredSlot(name, icon, action, text, highlightColor, closeAfterAction, baseColor);
        if (slot != null) {
            RadialMenuExtraSlotRegistry.addSlot(slot);
        }
    }

    private static void addConfiguredForMenu(ResourceLocation menuId, String name, IRadialIcon icon, Action action, Component text, String highlightColor, boolean closeAfterAction, String baseColor) {
        RadialMenuData.Slot slot = RadialMenusKJS.configuredSlot(name, icon, action, text, highlightColor, closeAfterAction, baseColor);
        if (slot != null) {
            RadialMenuExtraSlotRegistry.addSlotForMenu(menuId, slot);
        }
    }

    private static RadialMenuData.Slot configuredSlot(String name, IRadialIcon icon, Action action, Component text, String highlightColor, boolean closeAfterAction, String baseColor) {
        if (name == null || name.isEmpty() || icon == null || action == null) {
            return null;
        }
        return new RadialMenuData.Slot(name, icon, action, Optional.ofNullable(text), Optional.ofNullable(highlightColor), closeAfterAction, Optional.ofNullable(baseColor));
    }

    private static ResourceLocation normalizeId(String id) {
        if (id == null || id.isEmpty()) {
            return new ResourceLocation("kubejs", "radial_menu");
        }
        if (id.indexOf(58) < 0) {
            return new ResourceLocation("kubejs", id);
        }
        return new ResourceLocation(id);
    }
}

