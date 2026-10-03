/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.data.gun.ShootParameters
 *  com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 */
package frontline.combat.fcp.effects;

import com.atsuishio.superbwarfare.data.gun.ShootParameters;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import frontline.combat.fcp.effects.MuzzleBurstTracker;
import frontline.combat.fcp.effects.MuzzlePositionResolver;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public record MuzzleAnchor(int vehicleId, int seatIndex) {
    public static final MuzzleAnchor NONE = new MuzzleAnchor(-1, 0);

    public boolean isValid() {
        return this.vehicleId >= 0;
    }

    public static MuzzleAnchor from(ShootParameters parameters) {
        Entity entity = parameters.ammoSupplier;
        if (!(entity instanceof VehicleEntity)) {
            return NONE;
        }
        VehicleEntity vehicle = (VehicleEntity)entity;
        int seat = parameters.shooter != null ? vehicle.getSeatIndex(parameters.shooter) : 0;
        return new MuzzleAnchor(vehicle.m_19879_(), seat);
    }

    public MuzzlePositionResolver.MuzzlePose resolve(Level level) {
        if (!this.isValid()) {
            return null;
        }
        Entity entity = level.m_6815_(this.vehicleId);
        if (!(entity instanceof VehicleEntity)) {
            MuzzleBurstTracker.clear(this.vehicleId);
            return null;
        }
        VehicleEntity vehicle = (VehicleEntity)entity;
        Vec3 position = vehicle.getShootPos(this.seatIndex, 1.0f);
        Vec3 direction = vehicle.getShootVec(this.seatIndex, 1.0f);
        direction = direction == null || direction.m_82556_() < 1.0E-8 ? vehicle.m_20252_(1.0f) : direction.m_82541_();
        return new MuzzlePositionResolver.MuzzlePose(position, direction);
    }
}

