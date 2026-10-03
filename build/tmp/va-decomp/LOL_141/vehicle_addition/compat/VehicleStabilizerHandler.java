/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.data.vehicle.subdata.VehicleType
 *  com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.event.TickEvent$RenderTickEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 */
package LOL_141.vehicle_addition.compat;

import LOL_141.vehicle_addition.compat.SeatStabilizerData;
import LOL_141.vehicle_addition.compat.VehicleTerrainCompatHelper;
import com.atsuishio.superbwarfare.data.vehicle.subdata.VehicleType;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import java.util.Random;
import java.util.WeakHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public final class VehicleStabilizerHandler {
    private static final WeakHashMap<Entity, ShakeState> SHAKES = new WeakHashMap();

    private VehicleStabilizerHandler() {
    }

    public static void tickShake(Entity entity) {
        if (!VehicleTerrainCompatHelper.isSbwVehicle(entity)) {
            return;
        }
        VehicleEntity veh = (VehicleEntity)entity;
        if (!VehicleStabilizerHandler.isAffectedType(veh)) {
            return;
        }
        if (!VehicleStabilizerHandler.hasUnstabilizedOccupant(veh)) {
            return;
        }
        ShakeState s = SHAKES.computeIfAbsent(entity, k -> new ShakeState());
        s.prevShakePitch = s.shakePitch;
        s.prevShakeRoll = s.shakeRoll;
        VehicleStabilizerHandler.updateShake(s, entity);
        float deltaPitch = s.shakePitch - s.prevShakePitch;
        float deltaRoll = s.shakeRoll - s.prevShakeRoll;
        if (deltaPitch != 0.0f || deltaRoll != 0.0f) {
            VehicleStabilizerHandler.applyDelta(entity, deltaPitch, deltaRoll);
        }
    }

    private static boolean isAffectedType(VehicleEntity veh) {
        VehicleType type = veh.getVehicleType();
        if (type == null) {
            return false;
        }
        switch (type) {
            case TANK: 
            case APC: 
            case AA: 
            case CAR: 
            case ARTILLERY: 
            case SPECIAL: 
            case BOAT: {
                return true;
            }
        }
        return false;
    }

    private static boolean hasUnstabilizedOccupant(VehicleEntity veh) {
        for (Entity p : veh.m_20197_()) {
            int seatIdx = veh.getSeatIndex(p);
            if (SeatStabilizerData.isStabilized((Entity)veh, seatIdx)) continue;
            return true;
        }
        return false;
    }

    private static void updateShake(ShakeState s, Entity veh) {
        Vec3 motion = veh.m_20184_();
        double speed = motion.m_82553_();
        if (speed <= 0.1) {
            if (s.shakeDuration > 0) {
                --s.shakeDuration;
                VehicleStabilizerHandler.calculateShake(s, veh);
            } else {
                VehicleStabilizerHandler.decay(s);
            }
            return;
        }
        if (s.shakeDuration > 0) {
            --s.shakeDuration;
            s.shakePhase += 0.3f;
            VehicleStabilizerHandler.calculateShake(s, veh);
            if (s.shakeDuration == 0) {
                VehicleStabilizerHandler.resetShake(s);
            }
        } else {
            --s.shakeCooldown;
            VehicleStabilizerHandler.decay(s);
            if (s.shakeCooldown <= 0) {
                VehicleStabilizerHandler.startShake(s, veh);
            }
        }
    }

    private static void decay(ShakeState s) {
        s.shakePitch *= 0.9f;
        s.shakeRoll *= 0.9f;
        if (Math.abs(s.shakePitch) < 0.01f) {
            s.shakePitch = 0.0f;
        }
        if (Math.abs(s.shakeRoll) < 0.01f) {
            s.shakeRoll = 0.0f;
        }
    }

    private static void startShake(ShakeState s, Entity veh) {
        float speedFactor = (float)Math.min(veh.m_20184_().m_82553_(), 2.0);
        s.currentShakeIntensity = (s.random.nextFloat() * 0.6f + 0.6f) * speedFactor;
        s.shakeDuration = s.random.nextInt(5) + 6 + (int)(speedFactor * 6.0f);
        s.shakePhase = s.random.nextFloat() * (float)Math.PI * 2.0f;
    }

    private static void resetShake(ShakeState s) {
        s.shakeCooldown = s.random.nextInt(9) + 4;
        s.shakeDuration = 0;
        s.currentShakeIntensity = 0.0f;
    }

    private static void calculateShake(ShakeState s, Entity veh) {
        float bumpX = (float)Math.sin(s.shakePhase) * s.currentShakeIntensity;
        float direction = VehicleStabilizerHandler.getMovementDirection(veh);
        float bumpY = (float)Math.cos(s.shakePhase * 1.5f) * s.currentShakeIntensity * 2.0f * direction;
        s.shakeRoll = bumpX * 0.5f;
        s.shakePitch = bumpY;
    }

    private static float getMovementDirection(Entity veh) {
        double forwardZ;
        Vec3 motion = veh.m_20184_();
        double yaw = (double)veh.m_146908_() * (Math.PI / 180);
        double forwardX = -Math.sin(yaw);
        double dot = motion.f_82479_ * forwardX + motion.f_82481_ * (forwardZ = Math.cos(yaw));
        return dot > 0.0 ? 1.0f : -1.0f;
    }

    private static void applyDelta(Entity entity, float deltaPitch, float deltaRoll) {
        VehicleEntity veh = (VehicleEntity)entity;
        float xr = veh.m_146909_() + deltaPitch;
        veh.m_146926_(xr);
        veh.f_19860_ = xr;
        veh.setFakePitch(0.0f);
        veh.setFakePitchO(0.0f);
        veh.setPitchAngle(0.0f);
        float rr = veh.getRoll(1.0f) + deltaRoll;
        veh.setZRot(rr);
        veh.setPrevRoll(rr);
        veh.setFakeRoll(0.0f);
        veh.setFakeRollO(0.0f);
        veh.setRollAngle(0.0f);
    }

    private static final class ShakeState {
        final Random random = new Random();
        int shakeCooldown;
        int shakeDuration;
        float currentShakeIntensity;
        float shakePhase;
        float prevShakePitch;
        float prevShakeRoll;
        float shakePitch;
        float shakeRoll;

        ShakeState() {
            VehicleStabilizerHandler.resetShake(this);
        }
    }

    @Mod.EventBusSubscriber(modid="vehicle_addition", value={Dist.CLIENT})
    public static final class ViewSyncEvents {
        private static boolean firstFrame = true;
        private static float lastYRot;
        private static float lastXRot;

        private ViewSyncEvents() {
        }

        @SubscribeEvent
        public static void onRenderTick(TickEvent.RenderTickEvent event) {
            Minecraft mc = Minecraft.m_91087_();
            LocalPlayer player = mc.f_91074_;
            if (player == null || player.m_20202_() == null || mc.m_91104_()) {
                firstFrame = true;
                return;
            }
            Entity vehicle = player.m_20202_();
            if (!VehicleTerrainCompatHelper.isSbwVehicle(vehicle)) {
                firstFrame = true;
                return;
            }
            VehicleEntity veh = (VehicleEntity)vehicle;
            if (!VehicleStabilizerHandler.isAffectedType(veh)) {
                firstFrame = true;
                return;
            }
            int seatIdx = veh.getSeatIndex((Entity)player);
            if (SeatStabilizerData.isStabilized((Entity)veh, seatIdx)) {
                firstFrame = true;
                return;
            }
            float partialTicks = event.renderTickTime;
            float interpYaw = Mth.m_14179_((float)partialTicks, (float)vehicle.f_19859_, (float)vehicle.m_146908_());
            float interpPitch = Mth.m_14179_((float)partialTicks, (float)vehicle.f_19860_, (float)vehicle.m_146909_());
            if (firstFrame) {
                lastYRot = interpYaw;
                lastXRot = interpPitch;
                firstFrame = false;
                return;
            }
            float yawDelta = Mth.m_14177_((float)(interpYaw - lastYRot));
            float pitchDelta = interpPitch - lastXRot;
            player.m_5616_(player.m_6080_() + yawDelta);
            player.m_146922_(player.m_146908_() + yawDelta);
            player.m_146926_(Mth.m_14036_((float)(player.m_146909_() + pitchDelta), (float)-90.0f, (float)90.0f));
            lastYRot = interpYaw;
            lastXRot = interpPitch;
        }
    }
}

