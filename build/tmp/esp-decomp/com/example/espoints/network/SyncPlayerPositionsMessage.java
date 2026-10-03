/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.util.Mth
 *  net.minecraftforge.network.NetworkEvent$Context
 *  net.minecraftforge.network.PacketDistributor
 */
package com.example.espoints.network;

import com.example.espoints.client.ClientPlayerIdentityState;
import com.example.espoints.hud.TacticalMapHUD;
import com.example.espoints.network.NetworkHandler;
import com.example.espoints.network.PacketValidation;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

public final class SyncPlayerPositionsMessage {
    public static final int MAX_PLAYERS = 256;
    private static final double FIXED_POINT_SCALE = 8.0;
    private final long session;
    private final Map<Integer, PlayerPosition> positions;

    public SyncPlayerPositionsMessage(long session, Map<Integer, PlayerPosition> positions) {
        if (session <= 0L || positions == null || positions.size() > 256) {
            throw new IllegalArgumentException("Invalid tactical position frame");
        }
        this.session = session;
        this.positions = Map.copyOf(positions);
    }

    public long session() {
        return this.session;
    }

    public Map<Integer, PlayerPosition> positions() {
        return this.positions;
    }

    public static void encode(SyncPlayerPositionsMessage message, FriendlyByteBuf buf) {
        buf.m_130103_(message.session);
        buf.m_130130_(message.positions.size());
        for (Map.Entry<Integer, PlayerPosition> entry : message.positions.entrySet()) {
            int shortId = entry.getKey();
            if (shortId <= 0 || shortId > 65535) {
                throw new IllegalArgumentException("Invalid tactical player id: " + shortId);
            }
            PlayerPosition position = entry.getValue();
            buf.writeShort(shortId);
            buf.writeInt(SyncPlayerPositionsMessage.toFixed(position.x));
            buf.writeInt(SyncPlayerPositionsMessage.toFixed(position.z));
            buf.writeByte(SyncPlayerPositionsMessage.toPackedYaw(position.yaw));
        }
    }

    public static SyncPlayerPositionsMessage decode(FriendlyByteBuf buf) {
        long session = buf.m_130258_();
        if (session <= 0L) {
            throw new IllegalArgumentException("Invalid tactical position session");
        }
        int size = PacketValidation.checkedCount(buf.m_130242_(), 256, "player position");
        HashMap<Integer, PlayerPosition> positions = new HashMap<Integer, PlayerPosition>(size);
        for (int index = 0; index < size; ++index) {
            int shortId = buf.readUnsignedShort();
            if (shortId == 0 || positions.containsKey(shortId)) {
                throw new IllegalArgumentException("Invalid/duplicate tactical player id: " + shortId);
            }
            double x = SyncPlayerPositionsMessage.fromFixed(buf.readInt());
            double z = SyncPlayerPositionsMessage.fromFixed(buf.readInt());
            float yaw = SyncPlayerPositionsMessage.fromPackedYaw(buf.readUnsignedByte());
            positions.put(shortId, PlayerPosition.positionOnly(x, 0.0, z, yaw));
        }
        return new SyncPlayerPositionsMessage(session, positions);
    }

    public static void handle(SyncPlayerPositionsMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            if (!context.getDirection().getReceptionSide().isClient()) {
                return;
            }
            Map<UUID, PlayerPosition> resolved = ClientPlayerIdentityState.get().resolve(message.session, message.positions);
            if (resolved != null) {
                TacticalMapHUD.getInstance().syncPlayerPositionsFromServer(resolved);
            }
        });
        context.setPacketHandled(true);
    }

    public static void sendToPlayers(Collection<? extends ServerPlayer> players, long session, Map<Integer, PlayerPosition> positions) {
        if (players == null || players.isEmpty()) {
            return;
        }
        SyncPlayerPositionsMessage message = new SyncPlayerPositionsMessage(session, positions);
        for (ServerPlayer serverPlayer : players) {
            NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), (Object)message);
        }
    }

    public static int toFixed(double coordinate) {
        if (!Double.isFinite(coordinate) || coordinate < -2.68435456E8 || coordinate > 2.68435455875E8) {
            throw new IllegalArgumentException("Invalid tactical coordinate: " + coordinate);
        }
        return (int)Math.round(coordinate * 8.0);
    }

    public static double fromFixed(int fixed) {
        return (double)fixed / 8.0;
    }

    public static int toPackedYaw(float yaw) {
        return Mth.m_14143_((float)(Mth.m_14177_((float)yaw) * 256.0f / 360.0f)) & 0xFF;
    }

    public static float fromPackedYaw(int packed) {
        return Mth.m_14177_((float)((float)(packed & 0xFF) * 360.0f / 256.0f));
    }

    public static final class PlayerPosition {
        public static final int NO_SQUAD = -1;
        private final double x;
        private final double y;
        private final double z;
        private final String name;
        private final String teamName;
        private final float yaw;
        private final int squadId;
        private final boolean squadLeader;
        private final boolean commander;

        public PlayerPosition(double x, double y, double z, String name, String teamName, float yaw, int squadId, boolean squadLeader, boolean commander) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.name = name == null ? "" : name;
            this.teamName = teamName == null ? "" : teamName;
            this.yaw = yaw;
            this.squadId = squadId;
            this.squadLeader = squadLeader;
            this.commander = commander;
        }

        public static PlayerPosition positionOnly(double x, double y, double z, float yaw) {
            return new PlayerPosition(x, y, z, "", "", yaw, -1, false, false);
        }

        public double getX() {
            return this.x;
        }

        public double getY() {
            return this.y;
        }

        public double getZ() {
            return this.z;
        }

        public String getName() {
            return this.name;
        }

        public String getTeamName() {
            return this.teamName;
        }

        public float getYaw() {
            return this.yaw;
        }

        public int getSquadId() {
            return this.squadId;
        }

        public boolean isSquadLeader() {
            return this.squadLeader;
        }

        public boolean isCommander() {
            return this.commander;
        }
    }
}

