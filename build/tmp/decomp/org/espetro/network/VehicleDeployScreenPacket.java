/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.network.NetworkEvent$Context
 */
package org.espetro.network;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.espetro.Espetro;

public final class VehicleDeployScreenPacket {
    private final boolean openScreen;
    private final List<VehicleInfo> vehicles;

    public VehicleDeployScreenPacket(boolean openScreen, List<VehicleInfo> vehicles) {
        this.openScreen = openScreen;
        this.vehicles = List.copyOf(vehicles);
    }

    public static VehicleDeployScreenPacket read(FriendlyByteBuf buf) {
        boolean open = buf.readBoolean();
        int size = Math.min(128, Math.max(0, buf.m_130242_()));
        ArrayList<VehicleInfo> vehicles = new ArrayList<VehicleInfo>(size);
        for (int i = 0; i < size; ++i) {
            vehicles.add(new VehicleInfo(buf.m_130136_(128), buf.m_130136_(256), buf.m_130242_(), buf.m_130242_(), buf.readLong(), buf.m_130242_()));
        }
        return new VehicleDeployScreenPacket(open, vehicles);
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeBoolean(this.openScreen);
        buf.m_130130_(Math.min(this.vehicles.size(), 128));
        for (int i = 0; i < this.vehicles.size() && i < 128; ++i) {
            VehicleInfo vehicle = this.vehicles.get(i);
            buf.m_130072_(vehicle.type, 128);
            buf.m_130072_(vehicle.displayName, 256);
            buf.m_130130_(vehicle.max);
            buf.m_130130_(vehicle.current);
            buf.writeLong(vehicle.readyAtEpochMs);
            buf.m_130130_(vehicle.respawnMinutes);
        }
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            try {
                Class.forName("org.espetro.client.ClientPacketHandlers").getMethod("handleVehicleDeployScreen", VehicleDeployScreenPacket.class).invoke(null, this);
            }
            catch (ReflectiveOperationException e) {
                Espetro.LOGGER.error("\u5904\u7406\u8f7d\u5177\u90e8\u7f72\u540c\u6b65\u5931\u8d25", (Throwable)e);
            }
        });
        context.setPacketHandled(true);
    }

    public boolean shouldOpenScreen() {
        return this.openScreen;
    }

    public List<VehicleInfo> getVehicles() {
        return this.vehicles;
    }

    public static final class VehicleInfo {
        public final String type;
        public final String displayName;
        public final int max;
        public final int current;
        public final long readyAtEpochMs;
        public final int respawnMinutes;

        public VehicleInfo(String type, String displayName, int max, int current, long readyAtEpochMs, int respawnMinutes) {
            this.type = type;
            this.displayName = displayName;
            this.max = max;
            this.current = current;
            this.readyAtEpochMs = readyAtEpochMs;
            this.respawnMinutes = respawnMinutes;
        }
    }
}

