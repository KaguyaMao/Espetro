/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.level.Level
 */
package com.redabysslucia.dragonrise_reforge.entities.projectile;

import com.redabysslucia.dragonrise_reforge.entities.projectile.GuidedBombEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class Gb250Entity
extends GuidedBombEntity {
    public Gb250Entity(EntityType<? extends Gb250Entity> type, Level level) {
        super(type, level);
        this.setExplosionDamage(325.0f);
        this.setExplosionRadius(8.0f);
    }

    @Override
    protected void updateTarget() {
        if (this.getGuideType() == 1) {
            this.currentTarget = this.getTargetPos();
        }
    }
}

