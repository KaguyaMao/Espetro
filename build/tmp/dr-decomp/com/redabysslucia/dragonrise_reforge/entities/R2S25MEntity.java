/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 */
package com.redabysslucia.dragonrise_reforge.entities;

import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.redabysslucia.dragonrise_reforge.utils.GeoBasedParticleUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class R2S25MEntity
extends VehicleEntity {
    public R2S25MEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public void m_8119_() {
        super.m_8119_();
        if (this.f_19797_ % 1 == 0 && this.hasPlayerOperator()) {
            GeoBasedParticleUtil.spawnParticlesFromManualPosition((Entity)this, -27.0, 25.0, 47.0);
        }
    }

    private boolean hasPlayerOperator() {
        if (!this.m_20197_().isEmpty()) {
            return this.m_20197_().get(0) instanceof Player;
        }
        return false;
    }
}

