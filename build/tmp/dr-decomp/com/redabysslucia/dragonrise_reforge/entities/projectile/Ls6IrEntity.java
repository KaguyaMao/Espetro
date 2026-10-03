/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.client.animation.entity.BasicProjectileAnimationInstance
 *  com.atsuishio.superbwarfare.tools.EntityFindUtil
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 */
package com.redabysslucia.dragonrise_reforge.entities.projectile;

import com.atsuishio.superbwarfare.client.animation.entity.BasicProjectileAnimationInstance;
import com.atsuishio.superbwarfare.tools.EntityFindUtil;
import com.redabysslucia.dragonrise_reforge.entities.projectile.GuidedBombEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class Ls6IrEntity
extends GuidedBombEntity {
    private final BasicProjectileAnimationInstance<?> anim;

    public Ls6IrEntity(EntityType<? extends Ls6IrEntity> type, Level level) {
        super(type, level);
        this.anim = this.m_9236_().f_46443_ ? new BasicProjectileAnimationInstance((Entity)this, false) : null;
    }

    @Override
    protected double getCorrectionDegreesPerTick() {
        return 15.0;
    }

    @Override
    public BasicProjectileAnimationInstance<?> getAnimationInstance() {
        return this.anim;
    }

    @Override
    protected void updateTarget() {
        String uuid = this.getTargetUUID();
        if (uuid != null && !uuid.equals("none")) {
            Entity target = EntityFindUtil.findEntity((Level)this.m_9236_(), (String)uuid);
            if (target != null && target.m_6084_()) {
                Vec3 targetPos = new Vec3(target.m_20185_(), target.m_20186_() + (double)target.m_20206_() * 0.5, target.m_20189_());
                this.setTargetPos(targetPos);
                this.currentTarget = targetPos;
                return;
            }
            if (this.getTargetPos() != null) {
                this.currentTarget = this.getTargetPos();
                return;
            }
        }
        if (this.getGuideType() == 1) {
            this.currentTarget = this.getTargetPos();
        }
    }
}

