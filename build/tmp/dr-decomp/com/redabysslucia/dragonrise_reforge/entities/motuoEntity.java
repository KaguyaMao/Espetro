/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.Mod
 *  com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity
 *  com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier
 *  net.minecraft.core.Direction
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.NotNull
 */
package com.redabysslucia.dragonrise_reforge.entities;

import com.atsuishio.superbwarfare.Mod;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class motuoEntity
extends VehicleEntity {
    public Vec3 deltaMovement0;

    public motuoEntity(EntityType<motuoEntity> type, Level world) {
        super(type, world);
    }

    public void m_6075_() {
        this.deltaMovement0 = this.m_20184_();
        super.m_6075_();
    }

    public void bounceHorizontal(@NotNull Direction direction) {
        boolean isDownwardImpact;
        double currentSpeed = this.deltaMovement0.m_82553_();
        boolean bl = isDownwardImpact = direction == Direction.DOWN;
        if (isDownwardImpact) {
            if (currentSpeed > 2.0) {
                this.m_20256_(this.m_20184_().m_82490_(0.3));
            }
            super.bounceHorizontal(direction);
            return;
        }
        if (currentSpeed < 0.5) {
            super.bounceHorizontal(direction);
            return;
        }
        if (this.m_9236_() instanceof ServerLevel) {
            for (Entity entity : this.m_20197_()) {
                double speed = this.m_20184_().m_82553_();
                if (!(speed > 0.4)) continue;
                entity.m_8127_();
                Vec3 dir = this.deltaMovement0.m_82541_().m_82549_(this.getUpVec(1.0f).m_82490_(0.6));
                Mod.queueServerWork((int)1, () -> {
                    if (entity instanceof Player) {
                        Player player = (Player)entity;
                        player.m_20256_(dir.m_82541_().m_82490_(speed));
                    } else {
                        entity.m_20256_(dir.m_82541_().m_82490_(speed));
                    }
                });
            }
        }
        super.bounceHorizontal(direction);
    }

    public DamageModifier getDamageModifier() {
        return super.getDamageModifier().custom((entity, source, damage) -> this.getSourceAngle(source, 0.05f) * damage);
    }
}

