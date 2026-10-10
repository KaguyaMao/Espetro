/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Direction
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.EntityHitResult
 *  net.minecraft.world.phys.HitResult
 *  net.minecraft.world.phys.HitResult$Type
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.entity;

import dev.latvian.mods.kubejs.level.BlockContainerJS;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class RayTraceResultJS {
    public final Entity fromEntity;
    public final HitResult.Type type;
    public final double distance;
    public Vec3 hit = null;
    public BlockContainerJS block = null;
    public Direction facing = null;
    public Entity entity = null;

    /*
     * Enabled aggressive block sorting
     */
    public RayTraceResultJS(Entity from, @Nullable HitResult result, double d) {
        this.fromEntity = from;
        this.distance = d;
        HitResult.Type type = this.type = result == null ? HitResult.Type.MISS : result.m_6662_();
        if (result instanceof BlockHitResult) {
            BlockHitResult b = (BlockHitResult)result;
            if (result.m_6662_() == HitResult.Type.BLOCK) {
                this.hit = result.m_82450_();
                this.block = new BlockContainerJS(from.m_9236_(), b.m_82425_());
                this.facing = b.m_82434_();
                return;
            }
        }
        if (!(result instanceof EntityHitResult)) return;
        EntityHitResult e = (EntityHitResult)result;
        if (result.m_6662_() != HitResult.Type.ENTITY) return;
        this.hit = result.m_82450_();
        this.entity = e.m_82443_();
    }

    public double getHitX() {
        return this.hit == null ? Double.NaN : this.hit.f_82479_;
    }

    public double getHitY() {
        return this.hit == null ? Double.NaN : this.hit.f_82480_;
    }

    public double getHitZ() {
        return this.hit == null ? Double.NaN : this.hit.f_82481_;
    }
}

