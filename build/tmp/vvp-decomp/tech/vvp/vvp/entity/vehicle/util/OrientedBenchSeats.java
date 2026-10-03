/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.data.vehicle.subdata.SeatInfo
 *  com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity
 *  com.atsuishio.superbwarfare.entity.vehicle.utils.VehicleVecUtils
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.phys.Vec3
 */
package tech.vvp.vvp.entity.vehicle.util;

import com.atsuishio.superbwarfare.data.vehicle.subdata.SeatInfo;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.utils.VehicleVecUtils;
import java.util.List;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public final class OrientedBenchSeats {
    private static final float HEAD_VEHICLE_ALIGN_EPS = 10.0f;

    private OrientedBenchSeats() {
    }

    public static void afterCopyEntityData(VehicleEntity vehicle, Entity entity) {
        int index = vehicle.getSeatIndex(entity);
        List seats = vehicle.computed().seats();
        if (index < 0 || index >= seats.size()) {
            return;
        }
        SeatInfo seat = (SeatInfo)seats.get(index);
        if (seat.getOrientation() == 0.0f) {
            return;
        }
        Vec3 oriented = vehicle.getTransformDirection(1.0f, entity);
        float orientedYaw = (float)(-VehicleVecUtils.getYRotFromVector((Vec3)oriented));
        entity.m_5618_(orientedYaw);
        if (!seat.getCanRotateHead()) {
            entity.m_146922_(orientedYaw);
            entity.f_19859_ = orientedYaw;
            entity.m_5616_(orientedYaw);
            return;
        }
        float headVsVehicle = Mth.m_14177_((float)(entity.m_146908_() - vehicle.m_146908_()));
        if (Math.abs(headVsVehicle) < 10.0f) {
            entity.m_146922_(orientedYaw);
            entity.f_19859_ = orientedYaw;
            entity.m_5616_(orientedYaw);
        }
    }
}

