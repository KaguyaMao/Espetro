/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.phys.Vec3
 */
package com.redabysslucia.dragonrise_reforge.utils;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class GeoBasedParticleUtil {
    private static final Map<String, Map<String, Vec3>> modelBonePivots = new HashMap<String, Map<String, Vec3>>();

    public static void spawnParticlesFromManualPosition(Entity entity, double localX, double localY, double localZ) {
        if (!entity.m_9236_().m_5776_()) {
            return;
        }
        Vec3 pivot = new Vec3(localX, localY, localZ);
        Vec3 worldPos = GeoBasedParticleUtil.calculateBoneWorldPosition(entity, pivot);
        GeoBasedParticleUtil.spawnParticlesAtPosition(entity, worldPos);
    }

    private static Vec3 calculateBoneWorldPosition(Entity entity, Vec3 pivot) {
        Vec3 entityPos = entity.m_20182_();
        double yaw = (double)entity.m_146908_() * Math.PI / 180.0;
        double pitch = (double)entity.m_146909_() * Math.PI / 180.0;
        double rotatedX = pivot.m_7096_() * Math.cos(yaw) - -pivot.m_7094_() * Math.sin(yaw);
        double rotatedZ = pivot.m_7096_() * Math.sin(yaw) + -pivot.m_7094_() * Math.cos(yaw);
        double rotatedY = pivot.m_7098_();
        double finalX = rotatedX * Math.cos(pitch) - rotatedY * Math.sin(pitch);
        double finalY = rotatedX * Math.sin(pitch) + rotatedY * Math.cos(pitch);
        double finalZ = rotatedZ;
        double scale = 0.0625;
        Vec3 result = new Vec3(entityPos.m_7096_() + finalX * scale, entityPos.m_7098_() + finalY * scale, entityPos.m_7094_() + finalZ * scale);
        System.out.println("Calculated bone position: " + result);
        return result;
    }

    private static void spawnParticlesAtPosition(Entity entity, Vec3 position) {
        System.out.println("Spawning particles at position: " + position);
        for (int i = 0; i < 5; ++i) {
            double offsetX = (Math.random() - 0.5) * 0.5;
            double offsetY = (Math.random() - 0.5) * 0.2;
            double offsetZ = (Math.random() - 0.5) * 0.5;
            double velocityX = (Math.random() - 0.2) * 0.2;
            double velocityY = Math.random() * 0.015 + 0.02;
            double velocityZ = (Math.random() - 0.2) * 0.2;
            entity.m_9236_().m_7106_((ParticleOptions)ParticleTypes.f_123762_, position.m_7096_() + offsetX, position.m_7098_() + offsetY, position.m_7094_() + offsetZ, velocityX, velocityY, velocityZ);
        }
    }
}

