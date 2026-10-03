/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.client.model.entity.VehicleModel$TransformContext
 *  com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  org.jetbrains.annotations.Nullable
 *  software.bernie.geckolib.core.animatable.model.CoreGeoBone
 */
package frontline.combat.fcp.client.model.Util;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import frontline.combat.fcp.entity.vehicle.SteerableVehicle;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;

public final class WheelRotationTransforms {
    public static final double PIXELS_PER_BLOCK = 16.0;
    public static final double DEFAULT_RADIUS = 0.5;
    public static final float DEFAULT_MAX_STEER = 30.0f;
    private static final double MAX_TICK_TRAVEL = 8.0;
    private static final double DEG_PER_RAD = 57.29577951308232;
    private static final Map<Entity, State> STATES = new WeakHashMap<Entity, State>();
    private static final Map<Class<?>, SteerAccessor> STEER_ACCESSORS = new ConcurrentHashMap();

    private WheelRotationTransforms() {
    }

    public static double fromPixels(double pixels) {
        return pixels / 16.0;
    }

    private static double sampleDistance(VehicleEntity vehicle, float partialTick) {
        State s = STATES.computeIfAbsent((Entity)vehicle, k -> new State());
        if (vehicle.f_19797_ != s.lastTick) {
            double dz;
            double dx;
            double dist;
            s.lastTick = vehicle.f_19797_;
            s.prevDistance = s.distance;
            if (s.primed && (dist = Math.sqrt((dx = vehicle.m_20185_() - s.lastX) * dx + (dz = vehicle.m_20189_() - s.lastZ) * dz)) > 1.0E-5 && dist < 8.0) {
                double forwardZ;
                double yaw = Math.toRadians(vehicle.m_146908_());
                double forwardX = -Math.sin(yaw);
                double sign = dx * forwardX + dz * (forwardZ = Math.cos(yaw)) >= 0.0 ? 1.0 : -1.0;
                s.distance += sign * dist;
            }
            s.lastX = vehicle.m_20185_();
            s.lastZ = vehicle.m_20189_();
            s.primed = true;
        }
        return Mth.m_14139_((double)partialTick, (double)s.prevDistance, (double)s.distance);
    }

    private static float rollDegrees(VehicleEntity vehicle, float partialTick, double radius) {
        double r = Math.max(0.05, radius);
        double deg = WheelRotationTransforms.sampleDistance(vehicle, partialTick) / r * 57.29577951308232;
        deg -= 360.0 * Math.floor(deg / 360.0 + 0.5);
        return (float)deg;
    }

    private static void applyRoll(CoreGeoBone bone, SpinAxis axis, float deg, boolean invert) {
        float rad = (float)Math.toRadians(invert ? (double)(-deg) : (double)deg);
        switch (axis) {
            case X: {
                bone.setRotX(rad);
                break;
            }
            case Y: {
                bone.setRotY(rad);
                break;
            }
            case Z: {
                bone.setRotZ(rad);
            }
        }
    }

    @Nullable
    public static <T extends VehicleEntity> VehicleModel.TransformContext<T> match(String boneName, String wheelBone) {
        return WheelRotationTransforms.match(boneName, wheelBone, 0.5, SpinAxis.X, true);
    }

    @Nullable
    public static <T extends VehicleEntity> VehicleModel.TransformContext<T> match(String boneName, String wheelBone, double radius) {
        return WheelRotationTransforms.match(boneName, wheelBone, radius, SpinAxis.X, true);
    }

    @Nullable
    public static <T extends VehicleEntity> VehicleModel.TransformContext<T> match(String boneName, String wheelBone, double radius, SpinAxis axis, boolean invert) {
        if (!wheelBone.equals(boneName)) {
            return null;
        }
        return (bone, vehicle, state) -> WheelRotationTransforms.applyRoll(bone, axis, WheelRotationTransforms.rollDegrees(vehicle, state.getPartialTick(), radius), invert);
    }

    @Nullable
    public static <T extends VehicleEntity> VehicleModel.TransformContext<T> matchAny(String boneName, double radius, String ... wheelBones) {
        for (String wb : wheelBones) {
            if (!wb.equals(boneName)) continue;
            return WheelRotationTransforms.match(boneName, wb, radius);
        }
        return null;
    }

    @Nullable
    public static <T extends VehicleEntity> VehicleModel.TransformContext<T> matchAny(String boneName, String ... wheelBones) {
        return WheelRotationTransforms.matchAny(boneName, 0.5, wheelBones);
    }

    private static SteerAccessor accessorFor(Class<?> cls) {
        return STEER_ACCESSORS.computeIfAbsent(cls, c -> {
            try {
                Method cur = c.getMethod("getSteeringAngle", new Class[0]);
                Method prv = c.getMethod("getPrevSteeringAngle", new Class[0]);
                if (cur.getReturnType() == Float.TYPE && prv.getReturnType() == Float.TYPE) {
                    cur.setAccessible(true);
                    prv.setAccessible(true);
                    return new SteerAccessor(cur, prv);
                }
            }
            catch (ReflectiveOperationException reflectiveOperationException) {
                // empty catch block
            }
            return SteerAccessor.NONE;
        });
    }

    private static float steerableAngle(VehicleEntity vehicle, float partialTick) {
        if (vehicle instanceof SteerableVehicle) {
            SteerableVehicle s = (SteerableVehicle)vehicle;
            return Mth.m_14179_((float)partialTick, (float)s.getPrevSteeringAngle(), (float)s.getSteeringAngle());
        }
        SteerAccessor a = WheelRotationTransforms.accessorFor(vehicle.getClass());
        if (a.usable()) {
            try {
                float cur = ((Float)a.current().invoke(vehicle, new Object[0])).floatValue();
                float prev = ((Float)a.prev().invoke(vehicle, new Object[0])).floatValue();
                return Mth.m_14179_((float)partialTick, (float)prev, (float)cur);
            }
            catch (ReflectiveOperationException reflectiveOperationException) {
                // empty catch block
            }
        }
        return 0.0f;
    }

    @Nullable
    public static <T extends VehicleEntity> VehicleModel.TransformContext<T> matchTurn(String boneName, String turnBone, double radius, float maxSteerDeg) {
        return WheelRotationTransforms.matchTurn(boneName, turnBone, radius, maxSteerDeg, (x$0, x$1) -> WheelRotationTransforms.steerableAngle(x$0, x$1));
    }

    @Nullable
    public static <T extends VehicleEntity> VehicleModel.TransformContext<T> matchTurn(String boneName, String turnBone, double radius, float maxSteerDeg, SteeringSupplier<T> steering) {
        if (!turnBone.equals(boneName)) {
            return null;
        }
        return (bone, vehicle, state) -> {
            float partialTick = state.getPartialTick();
            WheelRotationTransforms.applyRoll(bone, SpinAxis.X, WheelRotationTransforms.rollDegrees(vehicle, partialTick, radius), true);
            float steer = Mth.m_14036_((float)steering.steeringDegrees(vehicle, partialTick), (float)(-maxSteerDeg), (float)maxSteerDeg);
            bone.setRotY((float)Math.toRadians(steer));
        };
    }

    @Nullable
    public static <T extends VehicleEntity> VehicleModel.TransformContext<T> matchAnyTurn(String boneName, double radius, float maxSteerDeg, String ... turnBones) {
        for (String tb : turnBones) {
            if (!tb.equals(boneName)) continue;
            return WheelRotationTransforms.matchTurn(boneName, tb, radius, maxSteerDeg);
        }
        return null;
    }

    @Nullable
    public static <T extends VehicleEntity> VehicleModel.TransformContext<T> matchAnyTurn(String boneName, String ... turnBones) {
        return WheelRotationTransforms.matchAnyTurn(boneName, 0.5, 30.0f, turnBones);
    }

    private static final class State {
        int lastTick = Integer.MIN_VALUE;
        double lastX;
        double lastZ;
        double distance;
        double prevDistance;
        boolean primed;

        private State() {
        }
    }

    public static enum SpinAxis {
        X,
        Y,
        Z;

    }

    private record SteerAccessor(@Nullable Method current, @Nullable Method prev) {
        static final SteerAccessor NONE = new SteerAccessor(null, null);

        boolean usable() {
            return this.current != null && this.prev != null;
        }
    }

    @FunctionalInterface
    public static interface SteeringSupplier<T extends VehicleEntity> {
        public float steeringDegrees(T var1, float var2);
    }
}

