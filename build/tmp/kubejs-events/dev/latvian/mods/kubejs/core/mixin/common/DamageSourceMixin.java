/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.latvian.mods.rhino.util.RemapForJS
 *  dev.latvian.mods.rhino.util.RemapPrefixForJS
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  org.jetbrains.annotations.Nullable
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 */
package dev.latvian.mods.kubejs.core.mixin.common;

import dev.latvian.mods.rhino.util.RemapForJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@RemapPrefixForJS(value="kjs$")
@Mixin(value={DamageSource.class})
public abstract class DamageSourceMixin {
    @Shadow
    @RemapForJS(value="getType")
    public abstract String m_19385_();

    @Shadow
    @RemapForJS(value="getImmediate")
    public abstract Entity m_7640_();

    @Shadow
    @RemapForJS(value="getActual")
    public abstract Entity m_7639_();

    @Nullable
    public Player kjs$getPlayer() {
        Player p;
        Entity entity = this.m_7639_();
        return entity instanceof Player ? (p = (Player)entity) : null;
    }
}

