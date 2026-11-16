package org.espetro.vehicle;

import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.ModList;

import javax.annotation.Nullable;
import java.lang.reflect.Method;

/**
 * 载具轮盘「重置载具」的服务端实现。
 *
 * <p>行为：水平坐标不变，把载具抬到当前坐标正上方 {@link #LIFT_BLOCKS} 格，
 * 并把俯仰/侧倾（以及 SBW 的 fake 姿态、姿态角速度）全部归零，只保留偏航。</p>
 *
 * <p>载具姿态由 sbw_addition 的权威物理接管（客户端跟随服务端完整姿态包），
 * 所以除了改实体字段，还要通知它“这次是瞬移”，否则客户端会按插值慢慢飘上去，
 * 甚至把旧侧倾一起插出来。sbw_addition 是可选运行时依赖，这里全部走反射。</p>
 */
public final class VehiclePoseReset {

    /** 抬升高度（格）：当前坐标正上方三格。 */
    public static final double LIFT_BLOCKS = 3.0D;

    private static final String ADDITION_PHYSICS_SYNC =
        "LOL_141.sbw_addition.compat.VehiclePhysicsSync";

    private static boolean reflectionResolved;
    @Nullable
    private static Method markTeleport;

    private VehiclePoseReset() {
    }

    /**
     * 重置载具姿态并抬高。
     *
     * @return 是否真的执行了重置（实体不是载具/已移除/客户端侧都会返回 false）
     */
    public static boolean reset(@Nullable Entity entity) {
        if (!(entity instanceof VehicleEntity vehicle)) return false;
        if (vehicle.isRemoved() || vehicle.level().isClientSide) return false;

        Vec3 from = vehicle.position();
        Vec3 target = new Vec3(from.x, from.y + LIFT_BLOCKS, from.z);
        float yaw = vehicle.getYRot();

        // 先掐掉惯性，避免“瞬移后继续按旧速度飞出去”。
        vehicle.setDeltaMovement(Vec3.ZERO);
        vehicle.fallDistance = 0.0F;
        vehicle.moveTo(target.x, target.y, target.z, yaw, 0.0F);
        levelPose(vehicle);
        vehicle.setOnGround(false);
        vehicle.updateOBB();
        notifyAdditionTeleport(vehicle);
        return true;
    }

    /** 姿态回正：俯仰/侧倾归零，偏航保留。 */
    private static void levelPose(VehicleEntity vehicle) {
        vehicle.setXRot(0.0F);
        vehicle.xRotO = 0.0F;
        vehicle.setZRot(0.0F);
        vehicle.setPitchAngle(0.0F);
        vehicle.setPitchVelocity(0.0F);
        vehicle.setRoll(0.0F);
        vehicle.setRollAngle(0.0F);
        vehicle.setRollVelocity(0.0F);
        vehicle.setDeltaMovement(Vec3.ZERO);
    }

    /**
     * 让 sbw_addition 的姿态同步把下一次发布当成瞬移（reset 包），客户端才会瞬间贴到新位置。
     */
    private static void notifyAdditionTeleport(VehicleEntity vehicle) {
        Method method = resolveMarkTeleport();
        if (method == null) return;
        try {
            method.invoke(null, vehicle);
        } catch (ReflectiveOperationException | RuntimeException ignored) {
            // 丢一次快照最多让客户端多插值一帧，不影响服务端权威位置。
        }
    }

    @Nullable
    private static Method resolveMarkTeleport() {
        if (reflectionResolved) return markTeleport;
        reflectionResolved = true;
        if (!ModList.get().isLoaded("sbw_addition")) return null;
        try {
            Class<?> sync = Class.forName(ADDITION_PHYSICS_SYNC);
            markTeleport = sync.getMethod("markTeleport", VehicleEntity.class);
        } catch (ReflectiveOperationException | LinkageError | RuntimeException ignored) {
            markTeleport = null;
        }
        return markTeleport;
    }
}
