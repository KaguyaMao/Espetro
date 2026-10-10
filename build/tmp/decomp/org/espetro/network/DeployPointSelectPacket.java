/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.network.NetworkEvent$Context
 */
package org.espetro.network;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.espetro.Espetro;

public class DeployPointSelectPacket {
    private final boolean hasDeployPoint;
    private final String deployPointPos;
    private final List<BastionItem> bastions;

    public DeployPointSelectPacket(boolean hasDeployPoint, String deployPointPos, List<BastionItem> bastions) {
        this.hasDeployPoint = hasDeployPoint;
        this.deployPointPos = deployPointPos;
        this.bastions = bastions;
    }

    public DeployPointSelectPacket(FriendlyByteBuf buf) {
        this.hasDeployPoint = buf.readBoolean();
        this.deployPointPos = buf.m_130277_();
        int size = buf.m_130242_();
        this.bastions = new ArrayList<BastionItem>();
        for (int i = 0; i < size; ++i) {
            this.bastions.add(new BastionItem(buf.m_130259_(), buf.m_130277_(), buf.m_130277_()));
        }
    }

    public static DeployPointSelectPacket read(FriendlyByteBuf buf) {
        return new DeployPointSelectPacket(buf);
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeBoolean(this.hasDeployPoint);
        buf.m_130070_(this.deployPointPos);
        buf.m_130130_(this.bastions.size());
        for (BastionItem b : this.bastions) {
            buf.m_130077_(b.id);
            buf.m_130070_(b.name);
            buf.m_130070_(b.pos);
        }
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            try {
                Class.forName("org.espetro.client.ClientPacketHandlers").getMethod("handleDeployPointSelect", DeployPointSelectPacket.class).invoke(null, this);
            }
            catch (Exception e) {
                Espetro.LOGGER.error("Failed to handle DeployPointSelectPacket", (Throwable)e);
            }
        });
        ctx.get().setPacketHandled(true);
    }

    public boolean hasDeployPoint() {
        return this.hasDeployPoint;
    }

    public String getDeployPointPos() {
        return this.deployPointPos;
    }

    public List<BastionItem> getBastions() {
        return this.bastions;
    }

    public static class BastionItem {
        public final UUID id;
        public final String name;
        public final String pos;

        public BastionItem(UUID id, String name, String pos) {
            this.id = id;
            this.name = name;
            this.pos = pos;
        }
    }
}

