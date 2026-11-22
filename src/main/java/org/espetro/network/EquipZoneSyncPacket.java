package org.espetro.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.espetro.network.core.PacketHandling;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/** S→C：同阵营换装范围中心点（用于黄框）；不含敌方。 */
public class EquipZoneSyncPacket {

    private final List<Zone> zones;

    public EquipZoneSyncPacket(List<Zone> zones) {
        this.zones = zones == null ? List.of() : List.copyOf(zones);
    }

    public static EquipZoneSyncPacket read(FriendlyByteBuf buf) {
        int n = buf.readVarInt();
        List<Zone> zones = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            zones.add(new Zone(
                buf.readUtf(),
                buf.readDouble(),
                buf.readDouble(),
                buf.readDouble(),
                buf.readDouble()));
        }
        return new EquipZoneSyncPacket(zones);
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(zones.size());
        for (Zone z : zones) {
            buf.writeUtf(z.type);
            buf.writeDouble(z.x);
            buf.writeDouble(z.y);
            buf.writeDouble(z.z);
            buf.writeDouble(z.range);
        }
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        PacketHandling.handleOnClient(ctx, "EquipZoneSync",
            () -> () -> org.espetro.client.ClientPacketHandlers.handleEquipZoneSync(this));
    }

    public List<Zone> getZones() {
        return zones;
    }

    public record Zone(String type, double x, double y, double z, double range) {
    }
}
