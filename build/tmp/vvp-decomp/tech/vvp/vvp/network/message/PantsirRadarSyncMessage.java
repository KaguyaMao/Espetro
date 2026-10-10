/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.fml.DistExecutor
 *  net.minecraftforge.network.NetworkEvent$Context
 */
package tech.vvp.vvp.network.message;

import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import tech.vvp.vvp.client.PantsirClientHandler;

public class PantsirRadarSyncMessage {
    public static final int STATE_IDLE = 0;
    public static final int STATE_DETECTED = 1;
    public static final int STATE_LOCKING = 2;
    public static final int STATE_LOCKED = 3;
    public static final int STATE_LOST = 4;
    public static final int TARGET_TYPE_UNKNOWN = 0;
    public static final int TARGET_TYPE_HELICOPTER = 1;
    public static final int TARGET_TYPE_AIRPLANE = 2;
    public static final int TARGET_TYPE_MISSILE = 3;
    public static final int TARGET_TYPE_OWN_MISSILE = 4;
    public final int vehicleId;
    public final int radarState;
    public final int targetEntityId;
    public final double targetX;
    public final double targetY;
    public final double targetZ;
    public final double targetVelX;
    public final double targetVelY;
    public final double targetVelZ;
    public final int lockProgress;
    public final double targetDistance;
    public final float radarAngle;
    public final float turretAngle;
    public final int[] allTargetIds;
    public final double[] allTargetX;
    public final double[] allTargetY;
    public final double[] allTargetZ;
    public final int[] allTargetTypes;
    public final boolean[] allTargetIsAlly;
    public final double[] missileX;
    public final double[] missileY;
    public final double[] missileZ;
    public final boolean signalLost;
    public final double lostTargetX;
    public final double lostTargetY;
    public final double lostTargetZ;

    public PantsirRadarSyncMessage(int vehicleId, int radarState, int targetEntityId, double targetX, double targetY, double targetZ, double targetVelX, double targetVelY, double targetVelZ, int lockProgress, double targetDistance, float radarAngle, float turretAngle, int[] allTargetIds, double[] allTargetX, double[] allTargetY, double[] allTargetZ, int[] allTargetTypes, boolean[] allTargetIsAlly, double[] missileX, double[] missileY, double[] missileZ, boolean signalLost, double lostTargetX, double lostTargetY, double lostTargetZ) {
        this.vehicleId = vehicleId;
        this.radarState = radarState;
        this.targetEntityId = targetEntityId;
        this.targetX = targetX;
        this.targetY = targetY;
        this.targetZ = targetZ;
        this.targetVelX = targetVelX;
        this.targetVelY = targetVelY;
        this.targetVelZ = targetVelZ;
        this.lockProgress = lockProgress;
        this.targetDistance = targetDistance;
        this.radarAngle = radarAngle;
        this.turretAngle = turretAngle;
        this.allTargetIds = allTargetIds;
        this.allTargetX = allTargetX;
        this.allTargetY = allTargetY;
        this.allTargetZ = allTargetZ;
        this.allTargetTypes = allTargetTypes;
        this.allTargetIsAlly = allTargetIsAlly;
        this.missileX = missileX;
        this.missileY = missileY;
        this.missileZ = missileZ;
        this.signalLost = signalLost;
        this.lostTargetX = lostTargetX;
        this.lostTargetY = lostTargetY;
        this.lostTargetZ = lostTargetZ;
    }

    public static void encode(PantsirRadarSyncMessage message, FriendlyByteBuf buffer) {
        int i;
        buffer.writeInt(message.vehicleId);
        buffer.writeInt(message.radarState);
        buffer.writeInt(message.targetEntityId);
        buffer.writeDouble(message.targetX);
        buffer.writeDouble(message.targetY);
        buffer.writeDouble(message.targetZ);
        buffer.writeDouble(message.targetVelX);
        buffer.writeDouble(message.targetVelY);
        buffer.writeDouble(message.targetVelZ);
        buffer.writeInt(message.lockProgress);
        buffer.writeDouble(message.targetDistance);
        buffer.writeFloat(message.radarAngle);
        buffer.writeFloat(message.turretAngle);
        buffer.writeInt(message.allTargetIds.length);
        for (i = 0; i < message.allTargetIds.length; ++i) {
            buffer.writeInt(message.allTargetIds[i]);
            buffer.writeDouble(message.allTargetX[i]);
            buffer.writeDouble(message.allTargetY[i]);
            buffer.writeDouble(message.allTargetZ[i]);
            buffer.writeInt(message.allTargetTypes[i]);
            buffer.writeBoolean(message.allTargetIsAlly[i]);
        }
        buffer.writeInt(message.missileX.length);
        for (i = 0; i < message.missileX.length; ++i) {
            buffer.writeDouble(message.missileX[i]);
            buffer.writeDouble(message.missileY[i]);
            buffer.writeDouble(message.missileZ[i]);
        }
        buffer.writeBoolean(message.signalLost);
        buffer.writeDouble(message.lostTargetX);
        buffer.writeDouble(message.lostTargetY);
        buffer.writeDouble(message.lostTargetZ);
    }

    public static PantsirRadarSyncMessage decode(FriendlyByteBuf buffer) {
        int vehicleId = buffer.readInt();
        int radarState = buffer.readInt();
        int targetEntityId = buffer.readInt();
        double targetX = buffer.readDouble();
        double targetY = buffer.readDouble();
        double targetZ = buffer.readDouble();
        double targetVelX = buffer.readDouble();
        double targetVelY = buffer.readDouble();
        double targetVelZ = buffer.readDouble();
        int lockProgress = buffer.readInt();
        double targetDistance = buffer.readDouble();
        float radarAngle = buffer.readFloat();
        float turretAngle = buffer.readFloat();
        int count = buffer.readInt();
        int[] allTargetIds = new int[count];
        double[] allTargetX = new double[count];
        double[] allTargetY = new double[count];
        double[] allTargetZ = new double[count];
        int[] allTargetTypes = new int[count];
        boolean[] allTargetIsAlly = new boolean[count];
        for (int i = 0; i < count; ++i) {
            allTargetIds[i] = buffer.readInt();
            allTargetX[i] = buffer.readDouble();
            allTargetY[i] = buffer.readDouble();
            allTargetZ[i] = buffer.readDouble();
            allTargetTypes[i] = buffer.readInt();
            allTargetIsAlly[i] = buffer.readBoolean();
        }
        int missileCount = buffer.readInt();
        double[] missileX = new double[missileCount];
        double[] missileY = new double[missileCount];
        double[] missileZ = new double[missileCount];
        for (int i = 0; i < missileCount; ++i) {
            missileX[i] = buffer.readDouble();
            missileY[i] = buffer.readDouble();
            missileZ[i] = buffer.readDouble();
        }
        boolean signalLost = buffer.readBoolean();
        double lostTargetX = buffer.readDouble();
        double lostTargetY = buffer.readDouble();
        double lostTargetZ = buffer.readDouble();
        return new PantsirRadarSyncMessage(vehicleId, radarState, targetEntityId, targetX, targetY, targetZ, targetVelX, targetVelY, targetVelZ, lockProgress, targetDistance, radarAngle, turretAngle, allTargetIds, allTargetX, allTargetY, allTargetZ, allTargetTypes, allTargetIsAlly, missileX, missileY, missileZ, signalLost, lostTargetX, lostTargetY, lostTargetZ);
    }

    public static void handler(PantsirRadarSyncMessage message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn((Dist)Dist.CLIENT, () -> () -> PantsirClientHandler.handleRadarSync(message)));
        ctx.get().setPacketHandled(true);
    }
}

