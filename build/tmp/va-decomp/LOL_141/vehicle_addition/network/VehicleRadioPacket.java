/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraftforge.network.NetworkEvent$Context
 */
package LOL_141.vehicle_addition.network;

import LOL_141.vehicle_addition.network.VehicleRadioServer;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class VehicleRadioPacket {
    public int vehicleId;
    public String url;
    public long startTime;

    public VehicleRadioPacket() {
    }

    public VehicleRadioPacket(int vehicleId, String url) {
        this(vehicleId, url, 0L);
    }

    public VehicleRadioPacket(int vehicleId, String url, long startTime) {
        this.vehicleId = vehicleId;
        this.url = url == null ? "" : url;
        this.startTime = startTime;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.vehicleId);
        buf.m_130072_(this.url == null ? "" : this.url, 4096);
        buf.writeLong(this.startTime);
    }

    public static VehicleRadioPacket decode(FriendlyByteBuf buf) {
        return new VehicleRadioPacket(buf.readInt(), buf.m_130136_(4096), buf.readLong());
    }

    public void handle(Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            if (ctx.getDirection().getReceptionSide().isClient()) {
                VehicleRadioServer.onClientPacket(this);
            } else {
                VehicleRadioServer.onServerPacket(this, ctx);
            }
        });
        ctx.setPacketHandled(true);
    }
}

