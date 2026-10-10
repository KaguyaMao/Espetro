/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.client.animation.entity.BasicProjectileAnimationInstance
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.level.Level
 */
package com.redabysslucia.dragonrise_reforge.entities.projectile;

import com.atsuishio.superbwarfare.client.animation.entity.BasicProjectileAnimationInstance;
import com.redabysslucia.dragonrise_reforge.entities.projectile.GuidedBombEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class Gbu24Entity
extends GuidedBombEntity {
    private final BasicProjectileAnimationInstance<?> anim;

    public Gbu24Entity(EntityType<? extends Gbu24Entity> type, Level level) {
        super(type, level);
        this.anim = this.m_9236_().f_46443_ ? new BasicProjectileAnimationInstance((Entity)this, false) : null;
    }

    @Override
    protected double getCorrectionDegreesPerTick() {
        return 3.75;
    }

    @Override
    public BasicProjectileAnimationInstance<?> getAnimationInstance() {
        return this.anim;
    }
}

