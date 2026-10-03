/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.entity.OBBEntity
 *  com.atsuishio.superbwarfare.tools.OBB
 *  com.atsuishio.superbwarfare.tools.OBB$Part
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Vector3d
 */
package com.redabysslucia.dragonrise_reforge.utils;

import com.atsuishio.superbwarfare.entity.OBBEntity;
import com.atsuishio.superbwarfare.tools.OBB;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;

public class EngineParticleUtil {
    private static final Map<Entity, Vec3> lastEnginePositions = new HashMap<Entity, Vec3>();

    public static void spawnMainEngineParticles(Entity entity, Level level) {
        if (level.m_5776_() && entity instanceof OBBEntity) {
            OBBEntity obbEntity = (OBBEntity)entity;
            for (OBB obb : obbEntity.getOBBs()) {
                if (obb.part != OBB.Part.EMPTY) continue;
                Vec3 obbCenter = OBB.vector3dToVec3((Vector3d)obb.center);
                Vec3 lastPosition = lastEnginePositions.get(entity);
                if (lastPosition != null && !(lastPosition.m_82557_(obbCenter) > 0.01)) continue;
                EngineParticleUtil.spawnSmokeParticlesAtOBB(level, obb);
                lastEnginePositions.put(entity, obbCenter);
            }
        }
    }

    private static void spawnSmokeParticlesAtOBB(Level level, OBB obb) {
        Vec3 center = OBB.vector3dToVec3((Vector3d)obb.center);
        for (int i = 0; i < 1; ++i) {
            double offsetX = (Math.random() - 0.5) * (obb.extents().x * 2.0);
            double offsetY = (Math.random() - 0.5) * (obb.extents().y * 2.0);
            double offsetZ = (Math.random() - 0.5) * (obb.extents().z * 2.0);
            double motionX = (Math.random() - 0.5) * 0.1;
            double motionY = Math.random() * 0.02 + 0.01;
            double motionZ = (Math.random() - 0.5) * 0.1;
            level.m_7106_((ParticleOptions)ParticleTypes.f_123759_, center.f_82479_ + offsetX, center.f_82480_ + offsetY, center.f_82481_ + offsetZ, motionX, motionY, motionZ);
        }
    }
}

