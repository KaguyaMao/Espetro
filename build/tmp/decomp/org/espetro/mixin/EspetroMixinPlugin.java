/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.fml.loading.FMLLoader
 *  net.minecraftforge.fml.loading.moddiscovery.ModFileInfo
 *  org.objectweb.asm.tree.ClassNode
 *  org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin
 *  org.spongepowered.asm.mixin.extensibility.IMixinInfo
 */
package org.espetro.mixin;

import java.util.List;
import java.util.Set;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.moddiscovery.ModFileInfo;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

public final class EspetroMixinPlugin
implements IMixinConfigPlugin {
    private static final String SBW_MOD_ID = "superbwarfare";
    private static final String OPTIONAL_SEAT_MIXIN = "org.espetro.mixin.sbw.VehicleEntitySeatAccessMixin";
    private static final String OPTIONAL_SEAT_PACKET_MIXIN = "org.espetro.mixin.sbw.ChangeVehicleSeatMessageMixin";

    public void onLoad(String mixinPackage) {
    }

    public String getRefMapperConfig() {
        return null;
    }

    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (OPTIONAL_SEAT_MIXIN.equals(mixinClassName) || OPTIONAL_SEAT_PACKET_MIXIN.equals(mixinClassName)) {
            return true;
        }
        if (mixinClassName != null && mixinClassName.contains(".sbw.")) {
            return EspetroMixinPlugin.isModPresent(SBW_MOD_ID);
        }
        return true;
    }

    private static boolean isModPresent(String modId) {
        try {
            ModFileInfo info = FMLLoader.getLoadingModList().getModFileById(modId);
            return info != null;
        }
        catch (Throwable t) {
            return false;
        }
    }

    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    public List<String> getMixins() {
        return null;
    }

    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}

