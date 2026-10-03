/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.entity.vehicle.utils.VehicleVecUtils
 *  net.minecraft.util.Mth
 *  net.minecraft.world.phys.Vec3
 */
package tech.vvp.vvp.firecontrol;

import com.atsuishio.superbwarfare.entity.vehicle.utils.VehicleVecUtils;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import tech.vvp.vvp.entity.vehicle.HimarsEntity;

public final class HimarsBallisticsUtil {
    public static final float PROJECTILE_VELOCITY = 20.0f;
    public static final float GRAVITY = 0.08f;
    public static final double MAX_RANGE_METERS = 5000.0;
    public static final double MIN_RANGE_METERS = 250.0;

    private HimarsBallisticsUtil() {
    }

    public static Vec3 resolveShootOrigin(HimarsEntity himars) {
        Vec3 shootPos = himars.getShootPos("GMLRS", 1.0f);
        return shootPos != null && !shootPos.equals((Object)Vec3.f_82478_) ? shootPos : himars.m_20182_();
    }

    public static double getMaxRange(HimarsEntity himars) {
        return 5000.0;
    }

    public static double getMinRange(HimarsEntity himars) {
        return 250.0;
    }

    public static FireSolution solve(HimarsEntity himars, Vec3 shootOrigin, double targetX, double targetY, double targetZ) {
        double dx = targetX - shootOrigin.f_82479_;
        double dy = targetY - shootOrigin.f_82480_;
        double dz = targetZ - shootOrigin.f_82481_;
        double horizontalDist = Math.sqrt(dx * dx + dz * dz);
        float requiredAzimuth = Mth.m_14177_((float)((float)(Mth.m_14136_((double)dz, (double)dx) * 57.2957763671875) - 90.0f - himars.m_146908_()));
        double v = 20.0;
        double g = 0.08f;
        double discriminant = v * v * v * v - g * (g * horizontalDist * horizontalDist + 2.0 * dy * v * v);
        float requiredElevation = 0.0f;
        boolean inArc = false;
        double timeOfFlight = 0.0;
        if (discriminant >= 0.0 && horizontalDist > 0.001) {
            double angle1 = Math.atan((v * v + Math.sqrt(discriminant)) / (g * horizontalDist));
            double angle2 = Math.atan((v * v - Math.sqrt(discriminant)) / (g * horizontalDist));
            double maxPitchRad = Math.toRadians(himars.getTurretMaxPitch());
            double angle = Math.min(Math.max(angle1, angle2), maxPitchRad);
            double horizontalVel = v * Math.cos(angle);
            double verticalVel = v * Math.sin(angle);
            Vec3 launchVector = new Vec3(dx / horizontalDist * horizontalVel, verticalVel, dz / horizontalDist * horizontalVel);
            requiredElevation = (float)VehicleVecUtils.getXRotFromVector((Vec3)launchVector);
            double tofDisc = verticalVel * verticalVel - 2.0 * g * dy;
            double tofTicks = tofDisc >= 0.0 ? (verticalVel + Math.sqrt(tofDisc)) / g : 2.0 * verticalVel / g;
            timeOfFlight = tofTicks / 20.0;
            inArc = HimarsBallisticsUtil.isWithinArc(himars, requiredElevation, requiredAzimuth, horizontalDist);
        }
        float targetTurretPitch = Mth.m_14036_((float)(-requiredElevation), (float)(-himars.getTurretMaxPitch()), (float)(-himars.getTurretMinPitch()));
        float targetTurretYaw = Mth.m_14177_((float)((float)(-Mth.m_14136_((double)dx, (double)(-dz)) * 57.2957763671875)));
        return new FireSolution(targetX, targetY, targetZ, horizontalDist, requiredElevation, requiredAzimuth, targetTurretYaw, targetTurretPitch, timeOfFlight, inArc);
    }

    public static boolean isWithinArc(HimarsEntity himars, float requiredXRot, float requiredAzimuth, double range) {
        float displayElevation = requiredXRot;
        float minEl = himars.getTurretMinPitch();
        float maxEl = himars.getTurretMaxPitch();
        if (displayElevation < minEl - 1.0f || displayElevation > maxEl + 1.0f) {
            return false;
        }
        if (range < 250.0) {
            return false;
        }
        if (range > 5025.0) {
            return false;
        }
        return HimarsBallisticsUtil.isYawInRange(requiredAzimuth, himars.getTurretMinYaw(), himars.getTurretMaxYaw());
    }

    private static boolean isYawInRange(float yaw, float minYaw, float maxYaw) {
        if (minYaw <= maxYaw) {
            return yaw >= minYaw - 1.0f && yaw <= maxYaw + 1.0f;
        }
        return yaw >= minYaw - 1.0f || yaw <= maxYaw + 1.0f;
    }

    public record FireSolution(double targetX, double targetY, double targetZ, double range, float requiredElevation, float requiredAzimuth, float targetTurretYaw, float targetTurretPitch, double timeOfFlight, boolean inArc) {
        public float displayElevation() {
            return -this.requiredElevation;
        }
    }
}

