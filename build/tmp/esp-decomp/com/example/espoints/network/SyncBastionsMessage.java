/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraftforge.network.NetworkEvent$Context
 *  net.minecraftforge.network.PacketDistributor
 */
package com.example.espoints.network;

import com.example.espoints.network.NetworkHandler;
import com.example.espoints.network.PacketValidation;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

public class SyncBastionsMessage {
    private static final int MAX_BASTIONS = 256;
    private static final int MAX_BASES = 64;
    private static final int MAX_SUPPLY_STATIONS = 128;
    private static final String TACTICAL_MAP_HUD_CLASS = "com.example.espoints.hud.TacticalMapHUD";
    private final List<BastionInfo> bastions;
    private final List<BaseInfo> bases;
    private final List<VehicleSupplyStationInfo> vehicleSupplyStations;

    public SyncBastionsMessage(List<BastionInfo> bastions) {
        this(bastions, List.of());
    }

    public SyncBastionsMessage(List<BastionInfo> bastions, List<BaseInfo> bases) {
        this(bastions, bases, List.of());
    }

    public SyncBastionsMessage(List<BastionInfo> bastions, List<BaseInfo> bases, List<VehicleSupplyStationInfo> vehicleSupplyStations) {
        this.bastions = List.copyOf(bastions);
        this.bases = List.copyOf(bases);
        this.vehicleSupplyStations = List.copyOf(vehicleSupplyStations);
    }

    public static void encode(SyncBastionsMessage msg, FriendlyByteBuf buf) {
        PacketValidation.checkedCount(msg.bastions.size(), 256, "bastion");
        PacketValidation.checkedCount(msg.bases.size(), 64, "base");
        PacketValidation.checkedCount(msg.vehicleSupplyStations.size(), 128, "supply station");
        buf.m_130130_(msg.bastions.size());
        for (BastionInfo bastion : msg.bastions) {
            SyncBastionsMessage.validateBastion(bastion);
            buf.m_130072_(bastion.getName(), 64);
            buf.m_130072_(bastion.getTeam(), 32);
            buf.m_130064_(bastion.getPos());
            buf.m_130072_(bastion.getType(), 16);
            buf.m_130130_(bastion.getConstruction());
            buf.m_130130_(bastion.getAmmunition());
            buf.writeBoolean(bastion.isOperational());
            buf.writeDouble(bastion.getBuildRadius());
            buf.writeDouble(bastion.getExclusionRadius());
            buf.m_130103_(bastion.getNextWaveAtMillis());
        }
        buf.m_130130_(msg.bases.size());
        for (BaseInfo base : msg.bases) {
            SyncBastionsMessage.validateCommon(base.getName(), base.getTeam(), base.getPos());
            if (!Float.isFinite(base.getYaw())) {
                throw new IllegalArgumentException("Invalid base yaw");
            }
            buf.m_130072_(base.getName(), 64);
            buf.m_130072_(base.getTeam(), 32);
            buf.m_130064_(base.getPos());
            buf.writeFloat(base.getYaw());
        }
        buf.m_130130_(msg.vehicleSupplyStations.size());
        for (VehicleSupplyStationInfo station : msg.vehicleSupplyStations) {
            SyncBastionsMessage.validateCommon(station.getName(), station.getTeam(), station.getPos());
            buf.m_130072_(station.getName(), 64);
            buf.m_130072_(station.getTeam(), 32);
            buf.m_130064_(station.getPos());
        }
    }

    public static SyncBastionsMessage decode(FriendlyByteBuf buf) {
        int size = PacketValidation.checkedCount(buf.m_130242_(), 256, "bastion");
        ArrayList<BastionInfo> bastions = new ArrayList<BastionInfo>(size);
        for (int i = 0; i < size; ++i) {
            String name = buf.m_130136_(64);
            String team = buf.m_130136_(32);
            BlockPos pos = buf.m_130135_();
            String type = buf.m_130136_(16);
            int construction = buf.m_130242_();
            int ammunition = buf.m_130242_();
            boolean operational = buf.readBoolean();
            double buildRadius = buf.readDouble();
            double exclusionRadius = buf.readDouble();
            long nextWaveAtMillis = buf.m_130258_();
            if (construction < 0 || ammunition < 0 || buildRadius < 0.0 || exclusionRadius < 0.0 || nextWaveAtMillis < 0L) {
                throw new IllegalArgumentException("Negative bastion field");
            }
            BastionInfo bastion = new BastionInfo(name, team, pos, type, construction, ammunition, operational, buildRadius, exclusionRadius, nextWaveAtMillis);
            SyncBastionsMessage.validateBastion(bastion);
            bastions.add(bastion);
        }
        int baseSize = PacketValidation.checkedCount(buf.m_130242_(), 64, "base");
        ArrayList<BaseInfo> bases = new ArrayList<BaseInfo>(baseSize);
        for (int i = 0; i < baseSize; ++i) {
            String name = buf.m_130136_(64);
            String team = buf.m_130136_(32);
            BlockPos pos = buf.m_130135_();
            float yaw = buf.readFloat();
            SyncBastionsMessage.validateCommon(name, team, pos);
            if (!Float.isFinite(yaw)) {
                throw new IllegalArgumentException("Invalid base yaw");
            }
            bases.add(new BaseInfo(name, team, pos, yaw));
        }
        int stationSize = PacketValidation.checkedCount(buf.m_130242_(), 128, "vehicle supply station");
        ArrayList<VehicleSupplyStationInfo> stations = new ArrayList<VehicleSupplyStationInfo>(stationSize);
        for (int i = 0; i < stationSize; ++i) {
            String name = buf.m_130136_(64);
            String team = buf.m_130136_(32);
            BlockPos pos = buf.m_130135_();
            SyncBastionsMessage.validateCommon(name, team, pos);
            stations.add(new VehicleSupplyStationInfo(name, team, pos));
        }
        return new SyncBastionsMessage(bastions, bases, stations);
    }

    private static void validateBastion(BastionInfo bastion) {
        SyncBastionsMessage.validateCommon(bastion.getName(), bastion.getTeam(), bastion.getPos());
        if (bastion.getType() == null || bastion.getType().isBlank() || bastion.getType().length() > 16 || bastion.getConstruction() > 10000000 || bastion.getAmmunition() > 10000000 || !Double.isFinite(bastion.getBuildRadius()) || !Double.isFinite(bastion.getExclusionRadius()) || bastion.getBuildRadius() > 100000.0 || bastion.getExclusionRadius() > 100000.0 || bastion.getNextWaveAtMillis() < 0L) {
            throw new IllegalArgumentException("Invalid bastion payload");
        }
    }

    private static void validateCommon(String name, String team, BlockPos pos) {
        if (name == null || name.isBlank() || name.length() > 64 || team == null || team.length() > 32 || pos == null) {
            throw new IllegalArgumentException("Invalid tactical structure payload");
        }
    }

    public static void handle(SyncBastionsMessage msg, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            if (context.getDirection().getReceptionSide().isClient()) {
                SyncBastionsMessage.handleOnClient(msg.bastions, msg.bases, msg.vehicleSupplyStations);
            }
        });
        context.setPacketHandled(true);
    }

    private static void handleOnClient(List<BastionInfo> bastions, List<BaseInfo> bases, List<VehicleSupplyStationInfo> vehicleSupplyStations) {
        try {
            Class<?> hudClass = Class.forName(TACTICAL_MAP_HUD_CLASS);
            Object hud = hudClass.getMethod("getInstance", new Class[0]).invoke(null, new Object[0]);
            hudClass.getMethod("syncBastionsFromServer", List.class, List.class, List.class).invoke(hud, bastions, bases, vehicleSupplyStations);
        }
        catch (ReflectiveOperationException reflectiveOperationException) {
            // empty catch block
        }
    }

    public static void sendToPlayer(ServerPlayer player, List<BastionInfo> bastions) {
        NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), (Object)new SyncBastionsMessage(bastions));
    }

    public static void sendToPlayer(ServerPlayer player, List<BastionInfo> bastions, List<BaseInfo> bases) {
        SyncBastionsMessage.sendToPlayer(player, bastions, bases, List.of());
    }

    public static void sendToPlayer(ServerPlayer player, List<BastionInfo> bastions, List<BaseInfo> bases, List<VehicleSupplyStationInfo> vehicleSupplyStations) {
        NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), (Object)new SyncBastionsMessage(bastions, bases, vehicleSupplyStations));
    }

    public static class BastionInfo {
        private final String name;
        private final String team;
        private final BlockPos pos;
        private final String type;
        private final int construction;
        private final int ammunition;
        private final boolean operational;
        private final double buildRadius;
        private final double exclusionRadius;
        private final long nextWaveAtMillis;

        public BastionInfo(String name, String team, BlockPos pos) {
            this(name, team, pos, "FOB", 0, 0, true, 150.0, 400.0, 0L);
        }

        public BastionInfo(String name, String team, BlockPos pos, String type, int construction, int ammunition, boolean operational, double buildRadius, double exclusionRadius, long nextWaveAtMillis) {
            this.name = name;
            this.team = team;
            this.pos = pos;
            this.type = type == null ? "FOB" : type;
            this.construction = Math.max(0, construction);
            this.ammunition = Math.max(0, ammunition);
            this.operational = operational;
            this.buildRadius = Math.max(0.0, buildRadius);
            this.exclusionRadius = Math.max(0.0, exclusionRadius);
            this.nextWaveAtMillis = Math.max(0L, nextWaveAtMillis);
        }

        public String getName() {
            return this.name;
        }

        public String getTeam() {
            return this.team;
        }

        public BlockPos getPos() {
            return this.pos;
        }

        public String getType() {
            return this.type;
        }

        public boolean isRally() {
            return "RALLY".equalsIgnoreCase(this.type);
        }

        public boolean isRadio() {
            return "RADIO".equalsIgnoreCase(this.type) || "FOB".equalsIgnoreCase(this.type);
        }

        public boolean isHab() {
            return "HAB".equalsIgnoreCase(this.type);
        }

        public int getConstruction() {
            return this.construction;
        }

        public int getAmmunition() {
            return this.ammunition;
        }

        public boolean isOperational() {
            return this.operational;
        }

        public double getBuildRadius() {
            return this.buildRadius;
        }

        public double getExclusionRadius() {
            return this.exclusionRadius;
        }

        public long getNextWaveSeconds() {
            return Math.max(0L, (this.nextWaveAtMillis - System.currentTimeMillis() + 999L) / 1000L);
        }

        public long getNextWaveAtMillis() {
            return this.nextWaveAtMillis;
        }

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (!(object instanceof BastionInfo)) {
                return false;
            }
            BastionInfo other = (BastionInfo)object;
            return this.construction == other.construction && this.ammunition == other.ammunition && this.operational == other.operational && Double.compare(this.buildRadius, other.buildRadius) == 0 && Double.compare(this.exclusionRadius, other.exclusionRadius) == 0 && this.nextWaveAtMillis == other.nextWaveAtMillis && this.name.equals(other.name) && this.team.equals(other.team) && this.pos.equals((Object)other.pos) && this.type.equals(other.type);
        }

        public int hashCode() {
            return Objects.hash(this.name, this.team, this.pos, this.type, this.construction, this.ammunition, this.operational, this.buildRadius, this.exclusionRadius, this.nextWaveAtMillis);
        }
    }

    public static class BaseInfo {
        private final String name;
        private final String team;
        private final BlockPos pos;
        private final float yaw;

        public BaseInfo(String name, String team, BlockPos pos, float yaw) {
            this.name = name;
            this.team = team;
            this.pos = pos;
            this.yaw = yaw;
        }

        public String getName() {
            return this.name;
        }

        public String getTeam() {
            return this.team;
        }

        public BlockPos getPos() {
            return this.pos;
        }

        public float getYaw() {
            return this.yaw;
        }

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (!(object instanceof BaseInfo)) {
                return false;
            }
            BaseInfo other = (BaseInfo)object;
            return Float.compare(this.yaw, other.yaw) == 0 && this.name.equals(other.name) && this.team.equals(other.team) && this.pos.equals((Object)other.pos);
        }

        public int hashCode() {
            return Objects.hash(this.name, this.team, this.pos, Float.valueOf(this.yaw));
        }
    }

    public static class VehicleSupplyStationInfo {
        private final String name;
        private final String team;
        private final BlockPos pos;

        public VehicleSupplyStationInfo(String name, String team, BlockPos pos) {
            this.name = name;
            this.team = team;
            this.pos = pos;
        }

        public String getName() {
            return this.name;
        }

        public String getTeam() {
            return this.team;
        }

        public BlockPos getPos() {
            return this.pos;
        }

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (!(object instanceof VehicleSupplyStationInfo)) {
                return false;
            }
            VehicleSupplyStationInfo other = (VehicleSupplyStationInfo)object;
            return this.name.equals(other.name) && this.team.equals(other.team) && this.pos.equals((Object)other.pos);
        }

        public int hashCode() {
            return Objects.hash(this.name, this.team, this.pos);
        }
    }
}

