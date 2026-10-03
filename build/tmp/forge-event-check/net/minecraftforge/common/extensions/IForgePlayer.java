/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 */
package net.minecraftforge.common.extensions;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;

public interface IForgePlayer {
    private Player self() {
        return (Player)this;
    }

    default public double getEntityReach() {
        double range = this.self().m_21133_(ForgeMod.ENTITY_REACH.get());
        return range == 0.0 ? 0.0 : range + (double)(this.self().m_7500_() ? 3 : 0);
    }

    default public double getBlockReach() {
        double reach = this.self().m_21133_(ForgeMod.BLOCK_REACH.get());
        return reach == 0.0 ? 0.0 : reach + (this.self().m_7500_() ? 0.5 : 0.0);
    }

    default public boolean canReach(Vec3 entityHitVec, double padding) {
        return this.self().m_146892_().m_82509_((Position)entityHitVec, this.getEntityReach() + padding);
    }

    default public boolean canReach(Entity entity, double padding) {
        return this.isCloseEnough(entity, this.getEntityReach() + padding);
    }

    default public boolean canReachRaw(Entity entity, double padding) {
        double range = this.self().m_21133_(ForgeMod.ENTITY_REACH.get()) + padding;
        return this.isCloseEnough(entity, range);
    }

    default public boolean canReach(BlockPos pos, double padding) {
        double reach = this.getBlockReach() + padding;
        return this.self().m_146892_().m_82557_(Vec3.m_82512_((Vec3i)pos)) <= reach * reach;
    }

    default public boolean canReachRaw(BlockPos pos, double padding) {
        double reach = this.self().m_21133_(ForgeMod.BLOCK_REACH.get()) + padding;
        return this.self().m_146892_().m_82557_(Vec3.m_82512_((Vec3i)pos)) <= reach * reach;
    }

    default public boolean isCloseEnough(Entity entity, double dist) {
        Vec3 eye = this.self().m_146892_();
        AABB aabb = entity.m_20191_().m_82400_((double)entity.m_6143_());
        return aabb.m_272282_(eye) < dist * dist;
    }
}

