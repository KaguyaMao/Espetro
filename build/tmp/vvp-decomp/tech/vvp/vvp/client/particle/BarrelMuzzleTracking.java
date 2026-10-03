/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity
 *  net.minecraft.util.Mth
 *  net.minecraft.world.phys.Vec3
 */
package tech.vvp.vvp.client.particle;

import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public final class BarrelMuzzleTracking {
    private static final float REFERENCE_RECOIL_TICKS = 42.0f;
    private static final float SLIDE_MAX = 12.0f;
    private static final float KICK_MAX_DEG = 3.0f;
    private static final float GEO_TO_BLOCK = 0.0625f;

    private BarrelMuzzleTracking() {
    }

    public static Vec3 resolveMuzzle(VehicleEntity vehicle, int seatIndex) {
        Vec3 muzzle = vehicle.getShootPos(seatIndex, 1.0f);
        Vec3 forward = vehicle.getShootVec(seatIndex, 1.0f);
        forward = forward == null || forward.m_82556_() < 1.0E-8 ? vehicle.m_20252_(1.0f) : forward.m_82541_();
        return BarrelMuzzleTracking.applyVisualRecoilOffset(vehicle, muzzle, forward);
    }

    public static boolean applyFollowDelta(VehicleEntity vehicle, int seatIndex, double[] lastMuzzle, double[] position, float smoothing) {
        if (vehicle.getCannonRecoilTime() > 0) {
            Vec3 muzzle = BarrelMuzzleTracking.resolveMuzzle(vehicle, seatIndex);
            lastMuzzle[0] = muzzle.f_82479_;
            lastMuzzle[1] = muzzle.f_82480_;
            lastMuzzle[2] = muzzle.f_82481_;
            return false;
        }
        Vec3 muzzle = BarrelMuzzleTracking.resolveMuzzle(vehicle, seatIndex);
        if (Double.isNaN(lastMuzzle[0])) {
            lastMuzzle[0] = muzzle.f_82479_;
            lastMuzzle[1] = muzzle.f_82480_;
            lastMuzzle[2] = muzzle.f_82481_;
            return false;
        }
        double dx = (muzzle.f_82479_ - lastMuzzle[0]) * (double)smoothing;
        double dy = (muzzle.f_82480_ - lastMuzzle[1]) * (double)smoothing;
        double dz = (muzzle.f_82481_ - lastMuzzle[2]) * (double)smoothing;
        position[0] = position[0] + dx;
        position[1] = position[1] + dy;
        position[2] = position[2] + dz;
        lastMuzzle[0] = muzzle.f_82479_;
        lastMuzzle[1] = muzzle.f_82480_;
        lastMuzzle[2] = muzzle.f_82481_;
        return true;
    }

    private static Vec3 applyVisualRecoilOffset(VehicleEntity vehicle, Vec3 muzzle, Vec3 forward) {
        int recoilTime = vehicle.getCannonRecoilTime();
        if (recoilTime <= 0) {
            return muzzle;
        }
        float force = Mth.m_14036_((float)vehicle.getCannonRecoilForce(), (float)0.0f, (float)2.0f);
        float progress = (float)recoilTime / 42.0f;
        float slide = force * 12.0f * progress * progress * 0.0625f;
        float kick = force * 3.0f * progress * progress * (float)Math.sin(0.6283185307179586 * ((double)recoilTime - 2.5)) * ((float)Math.PI / 180);
        Vec3 right = forward.m_82537_(new Vec3(0.0, 1.0, 0.0));
        if (right.m_82556_() < 1.0E-6) {
            right = forward.m_82537_(new Vec3(1.0, 0.0, 0.0));
        }
        right = right.m_82541_();
        Vec3 up = right.m_82537_(forward).m_82541_();
        return muzzle.m_82546_(forward.m_82490_((double)slide)).m_82549_(up.m_82490_((double)(slide * kick * 4.0f)));
    }
}

