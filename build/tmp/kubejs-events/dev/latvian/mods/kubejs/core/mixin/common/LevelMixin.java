/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.latvian.mods.rhino.util.RemapForJS
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.world.level.Level
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 */
package dev.latvian.mods.kubejs.core.mixin.common;

import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.core.LevelKJS;
import dev.latvian.mods.kubejs.util.AttachedData;
import dev.latvian.mods.kubejs.util.KubeJSPlugins;
import dev.latvian.mods.rhino.util.RemapForJS;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value={Level.class})
public abstract class LevelMixin
implements LevelKJS {
    private AttachedData<Level> kjs$attachedData;

    @Override
    public AttachedData<Level> kjs$getData() {
        if (this.kjs$attachedData == null) {
            this.kjs$attachedData = new AttachedData<Level>(this.kjs$self());
            KubeJSPlugins.forEachPlugin(this.kjs$attachedData, KubeJSPlugin::attachLevelData);
        }
        return this.kjs$attachedData;
    }

    @Shadow
    @RemapForJS(value="getTime")
    public abstract long m_46467_();

    @Shadow
    @RemapForJS(value="getDimensionKey")
    public abstract ResourceKey<Level> m_46472_();
}

