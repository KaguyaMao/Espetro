/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.entity.projectile.Agm65Entity
 *  com.atsuishio.superbwarfare.tools.EntityFindUtil
 *  com.atsuishio.superbwarfare.tools.RangeTool
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.boss.enderdragon.EnderDragon
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 */
package com.redabysslucia.dragonrise_reforge.entities.projectile;

import com.atsuishio.superbwarfare.entity.projectile.Agm65Entity;
import com.atsuishio.superbwarfare.tools.EntityFindUtil;
import com.atsuishio.superbwarfare.tools.RangeTool;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class Agm65CustomEntity
extends Agm65Entity {
    private static final float MAX_TURN_SPEED_PER_TICK = 0.5f;
    private static final double LOFT_THRESHOLD = 300.0;
    private static final double LOFT_HEIGHT_FACTOR = 0.05;

    public Agm65CustomEntity(EntityType<? extends Agm65Entity> type, Level level) {
        super(type, level);
    }

    public void turn(Vec3 vec3, float turnSpeed) {
        super.turn(this.recalculateToVec(), Math.min(turnSpeed, 0.5f));
    }

    private Vec3 recalculateToVec() {
        Vec3 toVec = this.m_20154_();
        Entity entity = EntityFindUtil.findEntity((Level)this.m_9236_(), (String)this.getTargetUUID());
        if (this.getGuideType() == 0) {
            if (!this.getTargetUUID().equals("none") && entity != null && this.m_9236_() instanceof ServerLevel) {
                double dis = entity.m_20182_().m_82505_(this.m_20182_()).m_165924_();
                double height = dis > 300.0 ? 0.05 * (dis - 300.0) : 0.0;
                Vec3 targetPos = new Vec3(entity.m_20185_(), entity.m_20186_() + (double)(entity instanceof EnderDragon ? -2 : 0) + height, entity.m_20189_());
                toVec = RangeTool.calculateFiringSolution((Vec3)this.m_20182_(), (Vec3)targetPos, (Vec3)entity.m_20184_(), (double)this.m_20184_().m_82553_(), (double)0.0);
            }
        } else if (this.m_9236_() instanceof ServerLevel && this.getTargetPos() != null) {
            double dis = this.getTargetPos().m_82505_(this.m_20182_()).m_165924_();
            double height = dis > 300.0 ? 0.05 * (dis - 300.0) : 0.0;
            Vec3 targetPos = this.getTargetPos().m_82520_(0.0, height, 0.0);
            toVec = RangeTool.calculateFiringSolution((Vec3)this.m_20182_(), (Vec3)targetPos, (Vec3)Vec3.f_82478_, (double)this.m_20184_().m_82553_(), (double)0.0);
        }
        return toVec;
    }
}

