/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.latvian.mods.rhino.util.RemapPrefixForJS
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LightningBolt
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.storage.ServerLevelData
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.core.LevelKJS;
import dev.latvian.mods.kubejs.core.WithPersistentData;
import dev.latvian.mods.kubejs.player.EntityArrayList;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ServerLevelData;
import org.jetbrains.annotations.Nullable;

@RemapPrefixForJS(value="kjs$")
public interface ServerLevelKJS
extends LevelKJS,
WithPersistentData {
    default public ServerLevel kjs$self() {
        return (ServerLevel)this;
    }

    @Override
    default public ScriptType kjs$getScriptType() {
        return ScriptType.SERVER;
    }

    @Override
    default public EntityArrayList kjs$getEntities() {
        return new EntityArrayList((Level)this.kjs$self(), this.kjs$self().m_8583_());
    }

    default public void kjs$spawnLightning(double x, double y, double z, boolean effectOnly, @Nullable ServerPlayer player) {
        LightningBolt e = (LightningBolt)EntityType.f_20465_.m_20615_((Level)this.kjs$self());
        e.m_6027_(x, y, z);
        e.m_20879_(player);
        e.m_20874_(effectOnly);
        this.kjs$self().m_7967_((Entity)e);
    }

    default public void kjs$spawnLightning(double x, double y, double z, boolean effectOnly) {
        this.kjs$spawnLightning(x, y, z, effectOnly, null);
    }

    default public void kjs$setTime(long time) {
        ((ServerLevelData)this.kjs$self().m_6106_()).m_6253_(time);
    }

    @Override
    default public void kjs$spawnParticles(ParticleOptions options, boolean overrideLimiter, double x, double y, double z, double vx, double vy, double vz, int count, double speed) {
        for (ServerPlayer player : this.kjs$self().m_6907_()) {
            this.kjs$self().m_8624_(player, options, overrideLimiter, x, y, z, count, vx, vy, vz, speed);
        }
    }
}

