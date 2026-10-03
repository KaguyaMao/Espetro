/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.data.gun.ShootParameters
 *  com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.phys.Vec3
 */
package tech.vvp.vvp.effects;

import com.atsuishio.superbwarfare.data.gun.ShootParameters;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public final class MuzzlePositionResolver {
    private MuzzlePositionResolver() {
    }

    public static MuzzlePose resolve(ShootParameters parameters) {
        Vec3 position = parameters.shootPosition;
        Vec3 direction = parameters.shootDirection;
        Entity supplier = parameters.ammoSupplier;
        Entity shooter = parameters.shooter;
        if (supplier instanceof VehicleEntity) {
            VehicleEntity vehicle = (VehicleEntity)supplier;
            if (shooter != null) {
                direction = vehicle.getShootVec(shooter, 1.0f);
            } else {
                Vec3 seatDir = vehicle.getShootVec(0, 1.0f);
                if (seatDir != null) {
                    direction = seatDir;
                }
            }
            if (position == null || position.equals((Object)Vec3.f_82478_)) {
                position = shooter != null ? vehicle.getShootPos(shooter, 1.0f) : vehicle.getShootPos(0, 1.0f);
            }
        }
        direction = direction == null || direction.m_82556_() < 1.0E-8 ? new Vec3(0.0, 0.0, 1.0) : direction.m_82541_();
        return new MuzzlePose(position, direction);
    }

    public record MuzzlePose(Vec3 position, Vec3 direction) {
    }
}

