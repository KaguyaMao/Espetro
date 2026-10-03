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

public class Gbu12Entity
extends GuidedBombEntity {
    public Gbu12Entity(EntityType<? extends GuidedBombEntity> type, Level level) {
        super(type, level);
        this.setExplosionDamage(325.0f);
        this.setExplosionRadius(8.0f);
    }
}

