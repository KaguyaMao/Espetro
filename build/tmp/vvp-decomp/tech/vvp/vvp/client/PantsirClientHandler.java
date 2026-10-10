/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.api.distmarker.OnlyIn
 */
package tech.vvp.vvp.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import tech.vvp.vvp.network.message.PantsirRadarSyncMessage;

@OnlyIn(value=Dist.CLIENT)
public class PantsirClientHandler {
    private static final Map<Integer, PantsirRadarData> radarDataByVehicle = new HashMap<Integer, PantsirRadarData>();
    private static final float RADAR_ROTATION_SPEED = 0.16941176f;

    public static void handleRadarSync(PantsirRadarSyncMessage message) {
        int i;
        PantsirRadarData data = radarDataByVehicle.computeIfAbsent(message.vehicleId, k -> new PantsirRadarData());
        data.radarState = message.radarState;
        data.targetEntityId = message.targetEntityId;
        data.targetX = message.targetX;
        data.targetY = message.targetY;
        data.targetZ = message.targetZ;
        data.targetVelX = message.targetVelX;
        data.targetVelY = message.targetVelY;
        data.targetVelZ = message.targetVelZ;
        data.lockProgress = message.lockProgress;
        data.targetDistance = message.targetDistance;
        data.serverRadarAngle = message.radarAngle;
        data.clientRadarAngle = message.radarAngle;
        data.lastSyncTime = System.currentTimeMillis();
        data.turretAngle = message.turretAngle;
        data.allTargets.clear();
        for (i = 0; i < message.allTargetIds.length; ++i) {
            data.allTargets.add(new RadarTarget(message.allTargetIds[i], message.allTargetX[i], message.allTargetY[i], message.allTargetZ[i], message.allTargetTypes[i], message.allTargetIsAlly[i]));
        }
        data.missiles.clear();
        for (i = 0; i < message.missileX.length; ++i) {
            data.missiles.add(new MissilePosition(message.missileX[i], message.missileY[i], message.missileZ[i]));
        }
        if (message.signalLost) {
            data.uiLostSignal = true;
            data.uiLastTargetPos = new Vec3(message.lostTargetX, message.lostTargetY, message.lostTargetZ);
        } else {
            data.uiLostSignal = false;
            data.uiLastTargetPos = null;
        }
        data.lastMessageTime = System.currentTimeMillis();
        PantsirClientHandler.cleanupOldData();
    }

    private static void cleanupOldData() {
        long currentTime = System.currentTimeMillis();
        radarDataByVehicle.entrySet().removeIf(entry -> currentTime - ((PantsirRadarData)entry.getValue()).lastMessageTime > 5000L);
    }

    public static PantsirRadarData getRadarData(int vehicleId) {
        return radarDataByVehicle.get(vehicleId);
    }

    public static float getInterpolatedRadarAngle(int vehicleId) {
        PantsirRadarData data = radarDataByVehicle.get(vehicleId);
        if (data == null) {
            return 0.0f;
        }
        long currentTime = System.currentTimeMillis();
        if (data.lastUpdateTime == 0L) {
            data.lastUpdateTime = currentTime;
            return data.clientRadarAngle;
        }
        float deltaTime = currentTime - data.lastUpdateTime;
        data.lastUpdateTime = currentTime;
        deltaTime = Math.min(deltaTime, 50.0f);
        data.clientRadarAngle -= 0.16941176f * deltaTime;
        while (data.clientRadarAngle < -360.0f) {
            data.clientRadarAngle += 360.0f;
        }
        while (data.clientRadarAngle > 0.0f) {
            data.clientRadarAngle -= 360.0f;
        }
        return data.clientRadarAngle;
    }

    public static void reset() {
        radarDataByVehicle.clear();
    }

    public static boolean isTargetLocked(int vehicleId) {
        PantsirRadarData data = radarDataByVehicle.get(vehicleId);
        return data != null && data.radarState == 3;
    }

    public static boolean isLocking(int vehicleId) {
        PantsirRadarData data = radarDataByVehicle.get(vehicleId);
        return data != null && data.radarState == 2;
    }

    public static boolean isTargetDetected(int vehicleId) {
        PantsirRadarData data = radarDataByVehicle.get(vehicleId);
        if (data == null) {
            return false;
        }
        return data.targetEntityId != -1 && (data.radarState == 1 || data.radarState == 2 || data.radarState == 3);
    }

    public static boolean hasTargets(int vehicleId) {
        PantsirRadarData data = radarDataByVehicle.get(vehicleId);
        return data != null && !data.allTargets.isEmpty();
    }

    public static class PantsirRadarData {
        public int radarState = 0;
        public int targetEntityId = -1;
        public double targetX = 0.0;
        public double targetY = 0.0;
        public double targetZ = 0.0;
        public double targetVelX = 0.0;
        public double targetVelY = 0.0;
        public double targetVelZ = 0.0;
        public int lockProgress = 0;
        public double targetDistance = 0.0;
        public float radarAngle = 0.0f;
        public float turretAngle = 0.0f;
        public final List<RadarTarget> allTargets = new ArrayList<RadarTarget>();
        public final List<MissilePosition> missiles = new ArrayList<MissilePosition>();
        public float clientRadarAngle = 0.0f;
        public float serverRadarAngle = 0.0f;
        public long lastSyncTime = 0L;
        public long lastUpdateTime = 0L;
        public Vec3 uiLastTargetPos = null;
        public boolean uiLostSignal = false;
        public long lastMessageTime = System.currentTimeMillis();
    }

    public static class RadarTarget {
        public final int entityId;
        public final double x;
        public final double y;
        public final double z;
        public final int targetType;
        public final boolean isAlly;

        public RadarTarget(int entityId, double x, double y, double z, int targetType, boolean isAlly) {
            this.entityId = entityId;
            this.x = x;
            this.y = y;
            this.z = z;
            this.targetType = targetType;
            this.isAlly = isAlly;
        }
    }

    public static class MissilePosition {
        public final double x;
        public final double y;
        public final double z;

        public MissilePosition(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }
}

