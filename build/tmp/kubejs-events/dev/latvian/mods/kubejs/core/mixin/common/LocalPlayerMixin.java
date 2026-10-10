/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  dev.latvian.mods.rhino.util.RemapForJS
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.stats.StatsCounter
 *  net.minecraft.world.level.Level
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 */
package dev.latvian.mods.kubejs.core.mixin.common;

import com.mojang.authlib.GameProfile;
import dev.latvian.mods.kubejs.core.mixin.common.AbstractClientPlayerMixin;
import dev.latvian.mods.rhino.util.RemapForJS;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.stats.StatsCounter;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value={LocalPlayer.class})
public abstract class LocalPlayerMixin
extends AbstractClientPlayerMixin {
    public LocalPlayerMixin(Level level, BlockPos blockPos, float f, GameProfile gameProfile) {
        super(level, blockPos, f, gameProfile);
    }

    @Shadow
    @RemapForJS(value="getStatsCounter")
    public abstract StatsCounter m_108630_();
}

