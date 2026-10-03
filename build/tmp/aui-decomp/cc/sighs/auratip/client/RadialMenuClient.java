/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.Window
 *  net.minecraft.client.Minecraft
 *  net.minecraft.resources.ResourceLocation
 */
package cc.sighs.auratip.client;

import cc.sighs.auratip.api.radiamenu.RadialMenuExtraSlots;
import cc.sighs.auratip.api.radiamenu.RadialMenuRegistry;
import cc.sighs.auratip.client.render.RadialMenuOverlay;
import cc.sighs.auratip.data.RadialMenuData;
import com.mojang.blaze3d.platform.Window;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

public class RadialMenuClient {
    public static void openMenu(ResourceLocation menuId) {
        Minecraft minecraft = Minecraft.m_91087_();
        if (minecraft.f_91074_ == null || minecraft.f_91073_ == null) {
            return;
        }
        RadialMenuData menuData = RadialMenuClient.buildMenuData(menuId);
        if (menuData != null) {
            RadialMenuClient.openMenuAtCenter(minecraft, menuData);
        }
    }

    public static boolean replaceMenu(ResourceLocation menuId) {
        if (!RadialMenuOverlay.INSTANCE.isActive()) {
            return false;
        }
        RadialMenuData menuData = RadialMenuClient.buildMenuData(menuId);
        return menuData != null && RadialMenuOverlay.INSTANCE.replace(menuData);
    }

    public static boolean replaceMenu(RadialMenuData menuData) {
        return menuData != null && RadialMenuOverlay.INSTANCE.replace(menuData);
    }

    private static RadialMenuData buildMenuData(ResourceLocation menuId) {
        RadialMenuData baseMenu = RadialMenuClient.getBaseMenu(menuId);
        if (baseMenu == null) {
            return null;
        }
        return RadialMenuClient.enhanceWithExtraSlots(baseMenu);
    }

    private static RadialMenuData getBaseMenu(ResourceLocation menuId) {
        return RadialMenuRegistry.resolveMenuToOpen(menuId);
    }

    private static RadialMenuData enhanceWithExtraSlots(RadialMenuData baseMenu) {
        List<RadialMenuData.Slot> extraSlots = RadialMenuExtraSlots.getSlotsForMenu(baseMenu.id());
        if (extraSlots.isEmpty()) {
            return baseMenu;
        }
        List<RadialMenuData.Slot> combinedSlots = Stream.concat(baseMenu.slots().stream(), extraSlots.stream()).collect(Collectors.toMap(RadialMenuData.Slot::name, slot -> slot, (existing, replacement) -> replacement, LinkedHashMap::new)).values().stream().toList();
        return new RadialMenuData(baseMenu.id(), baseMenu.menuSettings(), combinedSlots);
    }

    private static void openMenuAtCenter(Minecraft minecraft, RadialMenuData menuData) {
        Window window = minecraft.m_91268_();
        RadialMenuOverlay.INSTANCE.open(menuData, window.m_85445_(), window.m_85446_(), minecraft);
    }
}

