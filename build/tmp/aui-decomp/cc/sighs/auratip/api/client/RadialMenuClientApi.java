/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.api.distmarker.OnlyIn
 */
package cc.sighs.auratip.api.client;

import cc.sighs.auratip.client.RadialMenuClient;
import cc.sighs.auratip.client.render.RadialMenuOverlay;
import cc.sighs.auratip.data.RadialMenuData;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(value=Dist.CLIENT)
public final class RadialMenuClientApi {
    private RadialMenuClientApi() {
    }

    public static void open(ResourceLocation menuId) {
        RadialMenuClient.openMenu(menuId);
    }

    public static boolean replace(ResourceLocation menuId) {
        return RadialMenuClient.replaceMenu(menuId);
    }

    public static boolean replace(RadialMenuData menuData) {
        return RadialMenuClient.replaceMenu(menuData);
    }

    public static Optional<ResourceLocation> activeMenuId() {
        return RadialMenuOverlay.INSTANCE.activeMenuId();
    }

    public static int hoveredSlotIndex() {
        return RadialMenuOverlay.INSTANCE.hoveredSlotIndex();
    }

    public static boolean isActive() {
        return RadialMenuOverlay.INSTANCE.isActive();
    }
}

