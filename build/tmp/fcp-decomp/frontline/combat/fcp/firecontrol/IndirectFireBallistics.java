/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity
 *  com.atsuishio.superbwarfare.entity.vehicle.utils.VehicleVecUtils
 *  com.atsuishio.superbwarfare.tools.RangeTool
 *  com.atsuishio.superbwarfare.tools.TrajectoryCalculator
 *  net.minecraft.core.BlockPos
 *  net.minecraft.util.Mth
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.phys.Vec3
 */
package frontline.combat.fcp.firecontrol;

import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.utils.VehicleVecUtils;
import com.atsuishio.superbwarfare.tools.RangeTool;
import com.atsuishio.superbwarfare.tools.TrajectoryCalculator;
import frontline.combat.fcp.firecontrol.FireControlComputation;
import frontline.combat.fcp.firecontrol.FireControlSolution;
import frontline.combat.fcp.firecontrol.FireControlStatus;
import frontline.combat.fcp.firecontrol.TrajectoryMode;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

public final class IndirectFireBallistics {
    public static final int MAX_RADIUS = 99;
    public static final int RANGE_TABLE_ROWS = 8;
    private static final double ANGLE_EPSILON = 0.05;

    private IndirectFireBallistics() {
    }

    public static FireControlComputation solve(VehicleEntity vehicle, int seatIndex, BlockPos target, TrajectoryMode mode) {
        return IndirectFireBallistics.solve(vehicle, seatIndex, target.m_252807_(), mode);
    }

    public static FireControlComputation solve(VehicleEntity vehicle, int seatIndex, Vec3 target, TrajectoryMode mode) {
        double distance;
        Vec3 adjustedTarget;
        double velocity = vehicle.getProjectileVelocity(seatIndex);
        double gravity = vehicle.getProjectileGravity(seatIndex);
        if (velocity <= 0.0 || gravity <= 0.0 || vehicle.getGunData(seatIndex) == null) {
            return FireControlComputation.failure(FireControlStatus.INVALID_WEAPON);
        }
        Vec3 muzzle = vehicle.getShootPos(seatIndex, 1.0f);
        Vec3 direction = TrajectoryCalculator.calculateLaunchVector((Vec3)muzzle, (Vec3)(adjustedTarget = target.m_82520_(0.0, -1.0 - 0.0015 * (distance = muzzle.m_82554_(target)), 0.0)), (double)velocity, (double)gravity, (mode == TrajectoryMode.LOW ? 1 : 0) != 0);
        if (direction == null || direction.m_82556_() < 1.0E-8) {
            return FireControlComputation.failure(FireControlStatus.OUT_OF_RANGE);
        }
        double pitch = VehicleVecUtils.getXRotFromVector((Vec3)(direction = direction.m_82541_()));
        if (pitch + 0.05 < (double)vehicle.getTurretMinPitch() || pitch - 0.05 > (double)vehicle.getTurretMaxPitch()) {
            return FireControlComputation.failure(FireControlStatus.PITCH_LIMIT);
        }
        double forwardYaw = VehicleVecUtils.getYRotFromVector((Vec3)vehicle.m_20156_());
        double desiredYaw = VehicleVecUtils.getYRotFromVector((Vec3)direction);
        double relativeYaw = Mth.m_14175_((double)(desiredYaw - forwardYaw));
        if (relativeYaw + 0.05 < (double)vehicle.getTurretMinYaw() || relativeYaw - 0.05 > (double)vehicle.getTurretMaxYaw()) {
            return FireControlComputation.failure(FireControlStatus.YAW_LIMIT);
        }
        double dx = target.f_82479_ - muzzle.f_82479_;
        double dz = target.f_82481_ - muzzle.f_82481_;
        double range = Math.sqrt(dx * dx + dz * dz);
        double horizontalSpeed = velocity * Math.sqrt(direction.f_82479_ * direction.f_82479_ + direction.f_82481_ * direction.f_82481_);
        double flightTime = horizontalSpeed > 1.0E-8 ? range / horizontalSpeed : 0.0;
        double yaw = -VehicleVecUtils.getYRotFromVector((Vec3)direction);
        return FireControlComputation.success(new FireControlSolution(muzzle, target, adjustedTarget, direction, range, pitch, yaw, flightTime));
    }

    public static Vec3 sampleTarget(BlockPos center, int radius, RandomSource random) {
        Vec3 target = center.m_252807_();
        if (radius <= 0) {
            return target;
        }
        double sampledRadius = (double)radius * Math.sqrt(random.m_188500_());
        double angle = random.m_188500_() * Math.PI * 2.0;
        return target.m_82520_(sampledRadius * Math.cos(angle), 0.0, sampledRadius * Math.sin(angle));
    }

    public static double rangeAtPitch(double velocity, double gravity, double muzzleY, double targetY, double pitchDegrees) {
        if (velocity <= 0.0 || gravity <= 0.0 || pitchDegrees <= 0.0) {
            return 0.0;
        }
        if (Math.abs(targetY - muzzleY) < 1.0E-6) {
            return RangeTool.getRange((double)pitchDegrees, (double)velocity, (double)gravity);
        }
        double pitch = Math.toRadians(pitchDegrees);
        double verticalSpeed = velocity * Math.sin(pitch);
        double discriminant = verticalSpeed * verticalSpeed - 2.0 * gravity * (targetY - muzzleY);
        if (discriminant < 0.0) {
            return 0.0;
        }
        double flightTime = (verticalSpeed + Math.sqrt(discriminant)) / gravity;
        return velocity * Math.cos(pitch) * flightTime;
    }
}

