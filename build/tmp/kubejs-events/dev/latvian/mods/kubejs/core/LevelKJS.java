/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.latvian.mods.rhino.util.RemapForJS
 *  dev.latvian.mods.rhino.util.RemapPrefixForJS
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.phys.AABB
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.core.NoMixinException;
import dev.latvian.mods.kubejs.core.WithAttachedData;
import dev.latvian.mods.kubejs.level.BlockContainerJS;
import dev.latvian.mods.kubejs.level.ExplosionJS;
import dev.latvian.mods.kubejs.level.FireworksJS;
import dev.latvian.mods.kubejs.player.EntityArrayList;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.script.ScriptTypeHolder;
import dev.latvian.mods.rhino.util.RemapForJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import java.util.Collection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

@RemapPrefixForJS(value="kjs$")
public interface LevelKJS
extends WithAttachedData<Level>,
ScriptTypeHolder {
    default public Level kjs$self() {
        return (Level)this;
    }

    @Override
    @RemapForJS(value="getSide")
    default public ScriptType kjs$getScriptType() {
        throw new NoMixinException();
    }

    @Override
    default public Component kjs$getName() {
        return Component.m_237113_((String)this.kjs$getDimension().toString());
    }

    @Override
    default public void kjs$tell(Component message) {
        for (Player entity : this.kjs$self().m_6907_()) {
            entity.kjs$tell(message);
        }
    }

    @Override
    default public void kjs$setStatusMessage(Component message) {
        for (Player entity : this.kjs$self().m_6907_()) {
            entity.kjs$setStatusMessage(message);
        }
    }

    @Override
    default public int kjs$runCommand(String command) {
        int m = 0;
        for (Player entity : this.kjs$self().m_6907_()) {
            m = Math.max(m, entity.kjs$runCommand(command));
        }
        return m;
    }

    @Override
    default public int kjs$runCommandSilent(String command) {
        int m = 0;
        for (Player entity : this.kjs$self().m_6907_()) {
            m = Math.max(m, entity.kjs$runCommandSilent(command));
        }
        return m;
    }

    default public ResourceLocation kjs$getDimension() {
        return this.kjs$self().m_46472_().m_135782_();
    }

    default public boolean kjs$isOverworld() {
        return this.kjs$self().m_46472_() == Level.f_46428_;
    }

    default public BlockContainerJS kjs$getBlock(int x, int y, int z) {
        return this.kjs$getBlock(new BlockPos(x, y, z));
    }

    default public BlockContainerJS kjs$getBlock(BlockPos pos) {
        return new BlockContainerJS(this.kjs$self(), pos);
    }

    default public BlockContainerJS kjs$getBlock(BlockEntity blockEntity) {
        return new BlockContainerJS(blockEntity);
    }

    default public EntityArrayList kjs$createEntityList(Collection<? extends Entity> entities) {
        return new EntityArrayList(this.kjs$self(), entities);
    }

    default public EntityArrayList kjs$getPlayers() {
        return this.kjs$createEntityList(this.kjs$self().m_6907_());
    }

    default public EntityArrayList kjs$getEntities() {
        return new EntityArrayList(this.kjs$self(), 0);
    }

    default public ExplosionJS kjs$createExplosion(double x, double y, double z) {
        return new ExplosionJS((LevelAccessor)this.kjs$self(), x, y, z);
    }

    @Nullable
    default public Entity kjs$createEntity(EntityType<?> type) {
        return type.m_20615_(this.kjs$self());
    }

    default public void kjs$spawnFireworks(double x, double y, double z, FireworksJS f) {
        this.kjs$self().m_7967_((Entity)f.createFireworkRocket(this.kjs$self(), x, y, z));
    }

    default public EntityArrayList kjs$getEntitiesWithin(AABB aabb) {
        return new EntityArrayList(this.kjs$self(), this.kjs$self().m_45933_(null, aabb));
    }

    default public void kjs$spawnParticles(ParticleOptions options, boolean overrideLimiter, double x, double y, double z, double vx, double vy, double vz, int count, double speed) {
    }
}

