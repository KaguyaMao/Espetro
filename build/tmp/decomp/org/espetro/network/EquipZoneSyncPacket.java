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

public class EquipZoneSyncPacket {
    private final List<Zone> zones;

    public EquipZoneSyncPacket(List<Zone> zones) {
        this.zones = zones == null ? List.of() : List.copyOf(zones);
    }

    public static EquipZoneSyncPacket read(FriendlyByteBuf buf) {
        int n = buf.m_130242_();
        ArrayList<Zone> zones = new ArrayList<Zone>(n);
        for (int i = 0; i < n; ++i) {
            zones.add(new Zone(buf.m_130277_(), buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readDouble()));
        }
        return new EquipZoneSyncPacket(zones);
    }

    public void write(FriendlyByteBuf buf) {
        buf.m_130130_(this.zones.size());
        for (Zone z : this.zones) {
            buf.m_130070_(z.type);
            buf.writeDouble(z.x);
            buf.writeDouble(z.y);
            buf.writeDouble(z.z);
            buf.writeDouble(z.range);
        }
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            try {
                Class.forName("org.espetro.client.ClientPacketHandlers").getMethod("handleEquipZoneSync", EquipZoneSyncPacket.class).invoke(null, this);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        });
        ctx.get().setPacketHandled(true);
    }

    public List<Zone> getZones() {
        return this.zones;
    }

    public record Zone(String type, double x, double y, double z, double range) {
    }
}

