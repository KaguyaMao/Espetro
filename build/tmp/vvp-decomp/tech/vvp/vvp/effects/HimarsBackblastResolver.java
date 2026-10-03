/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Matrix4d
 *  org.joml.Vector4d
 */
package tech.vvp.vvp.effects;

import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4d;
import org.joml.Vector4d;
import tech.vvp.vvp.effects.GunAxes;
import tech.vvp.vvp.effects.MuzzleAnchor;

public final class HimarsBackblastResolver {
    public static final double LOCAL_X = 0.0229;
    public static final double LOCAL_Y = 0.2257;
    public static final double LOCAL_Z = -2.0104;

    private HimarsBackblastResolver() {
    }

    public static Pose resolve(VehicleEntity vehicle) {
        return HimarsBackblastResolver.resolve(vehicle, 1.0f);
    }

    public static Pose resolve(VehicleEntity vehicle, float partialTick) {
        Matrix4d barrelTransform = vehicle.getBarrelTransform(partialTick);
        Vector4d world = vehicle.transformPosition(barrelTransform, 0.0229, 0.2257, -2.0104);
        Vec3 position = new Vec3(world.x, world.y, world.z);
        Vec3 barrelDir = vehicle.getVectorFromString("Barrel", partialTick);
        if (barrelDir.m_82556_() < 1.0E-8) {
            barrelDir = vehicle.getBarrelVector(partialTick);
        }
        barrelDir = barrelDir.m_82541_();
        return new Pose(position, barrelDir, barrelDir.m_82490_(-1.0), GunAxes.fromDirection(barrelDir));
    }

    public static Pose resolve(Level level, MuzzleAnchor anchor) {
        if (!anchor.isValid()) {
            return null;
        }
        Entity entity = level.m_6815_(anchor.vehicleId());
        if (!(entity instanceof VehicleEntity)) {
            return null;
        }
        VehicleEntity vehicle = (VehicleEntity)entity;
        return HimarsBackblastResolver.resolve(vehicle);
    }

    public record Pose(Vec3 position, Vec3 barrelDirection, Vec3 backDirection, GunAxes axes) {
    }
}

