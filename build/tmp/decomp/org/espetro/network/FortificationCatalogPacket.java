/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.network.NetworkEvent$Context
 *  net.minecraftforge.network.PacketDistributor
 */
package org.espetro.network;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import org.espetro.Espetro;
import org.espetro.bastion.FortificationConfig;
import org.espetro.bastion.FortificationManager;
import org.espetro.network.NetworkManager;

public final class FortificationCatalogPacket {
    private static final int MAX_ENTRIES = 64;
    private final boolean request;
    private final List<Entry> entries;

    private FortificationCatalogPacket(boolean request, List<Entry> entries) {
        this.request = request;
        this.entries = List.copyOf(entries);
    }

    public static FortificationCatalogPacket request() {
        return new FortificationCatalogPacket(true, List.of());
    }

    public static FortificationCatalogPacket forPlayer(ServerPlayer player) {
        ArrayList<Entry> entries = new ArrayList<Entry>();
        for (FortificationConfig.FortificationDef def : FortificationConfig.list()) {
            if (!FortificationManager.canUse(player, def) || entries.size() >= 64) continue;
            entries.add(new Entry(def.id, def.displayName, def.icon, def.constructionCost, def.ammunitionCost));
        }
        return new FortificationCatalogPacket(false, entries);
    }

    public List<Entry> entries() {
        return this.entries;
    }

    public static FortificationCatalogPacket read(FriendlyByteBuf buf) {
        boolean request = buf.readBoolean();
        if (request) {
            return FortificationCatalogPacket.request();
        }
        int size = Math.min(64, Math.max(0, buf.m_130242_()));
        ArrayList<Entry> entries = new ArrayList<Entry>(size);
        for (int i = 0; i < size; ++i) {
            entries.add(new Entry(buf.m_130136_(64), buf.m_130136_(128), buf.m_130136_(256), buf.m_130242_(), buf.m_130242_()));
        }
        return new FortificationCatalogPacket(false, entries);
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeBoolean(this.request);
        if (this.request) {
            return;
        }
        buf.m_130130_(Math.min(this.entries.size(), 64));
        for (int i = 0; i < this.entries.size() && i < 64; ++i) {
            Entry entry = this.entries.get(i);
            buf.m_130072_(entry.id(), 64);
            buf.m_130072_(entry.displayName(), 128);
            buf.m_130072_(entry.icon(), 256);
            buf.m_130130_(Math.max(0, entry.constructionCost()));
            buf.m_130130_(Math.max(0, entry.ammunitionCost()));
        }
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer sender = context.getSender();
            if (sender != null) {
                NetworkManager.NET.send(PacketDistributor.PLAYER.with(() -> sender), (Object)FortificationCatalogPacket.forPlayer(sender));
            } else {
                try {
                    Class.forName("org.espetro.client.ClientPacketHandlers").getMethod("handleFortificationCatalog", FortificationCatalogPacket.class).invoke(null, this);
                }
                catch (ReflectiveOperationException e) {
                    Espetro.LOGGER.error("\u5904\u7406\u5de5\u4e8b\u76ee\u5f55\u540c\u6b65\u5931\u8d25", (Throwable)e);
                }
            }
        });
        context.setPacketHandled(true);
    }

    public record Entry(String id, String displayName, String icon, int constructionCost, int ammunitionCost) {
    }
}

