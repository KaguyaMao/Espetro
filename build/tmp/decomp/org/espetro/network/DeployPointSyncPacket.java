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
import org.espetro.network.UnifiedDeployScreenPacket;

public class DeployPointSyncPacket {
    private final List<UnifiedDeployScreenPacket.BastionItem> deployPoints;

    public DeployPointSyncPacket(List<UnifiedDeployScreenPacket.BastionItem> deployPoints) {
        this.deployPoints = deployPoints == null ? new ArrayList() : deployPoints;
    }

    public DeployPointSyncPacket(FriendlyByteBuf buf) {
        int size = buf.m_130242_();
        this.deployPoints = new ArrayList<UnifiedDeployScreenPacket.BastionItem>(size);
        for (int i = 0; i < size; ++i) {
            this.deployPoints.add(new UnifiedDeployScreenPacket.BastionItem(buf));
        }
    }

    public static DeployPointSyncPacket read(FriendlyByteBuf buf) {
        return new DeployPointSyncPacket(buf);
    }

    public void write(FriendlyByteBuf buf) {
        buf.m_130130_(this.deployPoints.size());
        for (UnifiedDeployScreenPacket.BastionItem deployPoint : this.deployPoints) {
            deployPoint.write(buf);
        }
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            try {
                Class.forName("org.espetro.client.ClientPacketHandlers").getMethod("handleDeployPointSync", DeployPointSyncPacket.class).invoke(null, this);
            }
            catch (Exception e) {
                Espetro.LOGGER.error("Failed to handle DeployPointSyncPacket", (Throwable)e);
            }
        });
        ctx.get().setPacketHandled(true);
    }

    public List<UnifiedDeployScreenPacket.BastionItem> getDeployPoints() {
        return this.deployPoints;
    }
}

