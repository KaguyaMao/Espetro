/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraftforge.network.NetworkEvent$Context
 *  net.minecraftforge.network.PacketDistributor
 */
package com.example.espoints.network;

import com.example.espoints.client.ClientPlayerIdentityState;
import com.example.espoints.network.NetworkHandler;
import com.example.espoints.network.PacketValidation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

public final class SyncPlayerIdentityMessage {
    private final long session;
    private final List<Identity> identities;

    public SyncPlayerIdentityMessage(long session, List<Identity> identities) {
        if (session <= 0L || identities == null || identities.size() > 256) {
            throw new IllegalArgumentException("Invalid tactical identity table");
        }
        this.session = session;
        this.identities = List.copyOf(identities);
    }

    public long session() {
        return this.session;
    }

    public List<Identity> identities() {
        return this.identities;
    }

    public static void encode(SyncPlayerIdentityMessage message, FriendlyByteBuf buf) {
        buf.m_130103_(message.session);
        buf.m_130130_(message.identities.size());
        HashSet<Integer> shortIds = new HashSet<Integer>();
        HashSet<UUID> uuids = new HashSet<UUID>();
        for (Identity identity : message.identities) {
            if (identity.shortId <= 0 || identity.shortId > 65535 || identity.uuid == null || identity.name.length() > 64 || identity.team.length() > 32 || identity.squadId < -1 || identity.squadId > 65535 || !shortIds.add(identity.shortId) || !uuids.add(identity.uuid)) {
                throw new IllegalArgumentException("Invalid tactical identity");
            }
            buf.writeShort(identity.shortId);
            buf.m_130077_(identity.uuid);
            buf.m_130072_(identity.name, 64);
            buf.m_130072_(identity.team, 32);
            buf.m_130130_(identity.squadId);
            int flags = (identity.squadLeader ? 1 : 0) | (identity.commander ? 2 : 0);
            buf.writeByte(flags);
        }
    }

    public static SyncPlayerIdentityMessage decode(FriendlyByteBuf buf) {
        long session = buf.m_130258_();
        if (session <= 0L) {
            throw new IllegalArgumentException("Invalid tactical identity session");
        }
        int size = PacketValidation.checkedCount(buf.m_130242_(), 256, "player identity");
        ArrayList<Identity> identities = new ArrayList<Identity>(size);
        HashSet<Integer> shortIds = new HashSet<Integer>();
        HashSet<UUID> uuids = new HashSet<UUID>();
        for (int index = 0; index < size; ++index) {
            int shortId = buf.readUnsignedShort();
            UUID uuid = buf.m_130259_();
            String name = buf.m_130136_(64);
            String team = buf.m_130136_(32);
            int squadId = buf.m_130242_();
            short flags = buf.readUnsignedByte();
            if (shortId == 0 || squadId < -1 || squadId > 65535 || (flags & 0xFFFFFFFC) != 0 || !shortIds.add(shortId) || !uuids.add(uuid)) {
                throw new IllegalArgumentException("Invalid tactical identity");
            }
            identities.add(new Identity(shortId, uuid, name, team, squadId, (flags & 1) != 0, (flags & 2) != 0));
        }
        return new SyncPlayerIdentityMessage(session, identities);
    }

    public static void handle(SyncPlayerIdentityMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            if (context.getDirection().getReceptionSide().isClient()) {
                ClientPlayerIdentityState.get().replace(message.session, message.identities);
            }
        });
        context.setPacketHandled(true);
    }

    public static void sendToPlayers(Collection<? extends ServerPlayer> players, long session, List<Identity> identities) {
        if (players == null || players.isEmpty()) {
            return;
        }
        SyncPlayerIdentityMessage message = new SyncPlayerIdentityMessage(session, identities);
        for (ServerPlayer serverPlayer : players) {
            NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), (Object)message);
        }
    }

    public static void sendToPlayer(ServerPlayer player, long session, List<Identity> identities) {
        NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), (Object)new SyncPlayerIdentityMessage(session, identities));
    }

    public record Identity(int shortId, UUID uuid, String name, String team, int squadId, boolean squadLeader, boolean commander) {
        public Identity {
            name = name == null ? "" : name;
            team = team == null ? "" : team;
        }
    }
}

