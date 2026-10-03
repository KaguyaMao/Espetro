/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.entity.projectile.Agm65Entity
 *  com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity
 *  com.atsuishio.superbwarfare.tools.EntityFindUtil
 *  com.atsuishio.superbwarfare.tools.RangeTool
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.boss.enderdragon.EnderDragon
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.levelgen.Heightmap$Types
 *  net.minecraft.world.phys.Vec3
 */
package com.redabysslucia.dragonrise_reforge.entities.projectile;

import com.atsuishio.superbwarfare.entity.projectile.Agm65Entity;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.atsuishio.superbwarfare.tools.EntityFindUtil;
import com.atsuishio.superbwarfare.tools.RangeTool;
import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

public class Aim120Entity
extends Agm65Entity {
    private static final float MAX_TURN_SPEED_PER_TICK = 0.9f;
    private static final double PROXIMITY_FUZE_RADIUS = 5.0;
    private static final double MIN_TRACK_HEIGHT = 25.0;
    private static final int LOW_ALT_CHECK_INTERVAL = 20;
    private boolean inertialGuidance;
    private Vec3 lastKnownTargetPos;
    private int lowAltCheckTick;

    public Aim120Entity(EntityType<? extends Agm65Entity> type, Level level) {
        super(type, level);
    }

    public void turn(Vec3 vec3, float turnSpeed) {
        super.turn(this.recalculateToVec(), Math.min(turnSpeed, 0.9f));
    }

    private Vec3 recalculateToVec() {
        Vec3 toVec = this.m_20154_();
        if (this.inertialGuidance) {
            if (this.lastKnownTargetPos != null && this.m_9236_() instanceof ServerLevel) {
                toVec = RangeTool.calculateFiringSolution((Vec3)this.m_20182_(), (Vec3)this.lastKnownTargetPos, (Vec3)Vec3.f_82478_, (double)this.m_20184_().m_82553_(), (double)0.0);
            }
            return toVec;
        }
        Entity entity = EntityFindUtil.findEntity((Level)this.m_9236_(), (String)this.getTargetUUID());
        if (this.getGuideType() == 0) {
            if (!this.getTargetUUID().equals("none") && entity != null && this.m_9236_() instanceof ServerLevel) {
                Vec3 targetPos = new Vec3(entity.m_20185_(), entity.m_20186_() + (double)(entity instanceof EnderDragon ? -2 : 0), entity.m_20189_());
                toVec = RangeTool.calculateFiringSolution((Vec3)this.m_20182_(), (Vec3)targetPos, (Vec3)entity.m_20184_(), (double)this.m_20184_().m_82553_(), (double)0.0);
            }
        } else if (this.m_9236_() instanceof ServerLevel && this.getTargetPos() != null) {
            toVec = RangeTool.calculateFiringSolution((Vec3)this.m_20182_(), (Vec3)this.getTargetPos(), (Vec3)Vec3.f_82478_, (double)this.m_20184_().m_82553_(), (double)0.0);
        }
        return toVec;
    }

    public void m_8119_() {
        super.m_8119_();
        if (this.m_9236_() instanceof ServerLevel && this.m_6084_()) {
            Entity owner = this.m_19749_();
            Entity shooterVehicle = owner != null ? owner.m_20202_() : null;
            List vehicles = this.m_9236_().m_6443_(VehicleEntity.class, this.m_20191_().m_82400_(5.0), v -> v != shooterVehicle);
            if (!vehicles.isEmpty()) {
                this.m_146870_();
                this.causeExplode(this.m_20182_());
                return;
            }
            if (++this.lowAltCheckTick >= 20) {
                this.lowAltCheckTick = 0;
                this.updateGuidanceState();
            }
        }
    }

    private void updateGuidanceState() {
        boolean hasTarget;
        if (!(this.m_9236_() instanceof ServerLevel)) {
            return;
        }
        Entity target = EntityFindUtil.findEntity((Level)this.m_9236_(), (String)this.getTargetUUID());
        boolean bl = hasTarget = this.getGuideType() == 0 && !this.getTargetUUID().equals("none") && target != null;
        if (hasTarget) {
            this.lastKnownTargetPos = target.m_20182_();
            double heightAboveGround = target.m_20186_() - (double)this.m_9236_().m_6924_(Heightmap.Types.WORLD_SURFACE, target.m_20183_().m_123341_(), target.m_20183_().m_123343_());
            if (heightAboveGround < 25.0) {
                this.enterInertialGuidance();
            } else {
                this.inertialGuidance = false;
            }
        } else if (this.lastKnownTargetPos != null) {
            this.enterInertialGuidance();
        }
    }

    private void enterInertialGuidance() {
        if (!this.inertialGuidance) {
            this.inertialGuidance = true;
        }
    }
}

