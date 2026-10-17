package org.espetro.support;

import com.google.gson.Gson;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.*;
import net.minecraftforge.network.simple.SimpleChannel;

import org.espetro.support.FireRequestBoard.*;

import java.util.*;

public final class FireRequestNetwork {
    private static final Gson JSON = new Gson();
    public static final SimpleChannel CHANNEL =
            NetworkRegistry.newSimpleChannel(
                    new ResourceLocation("espetro", "fire_requests"),
                    () -> "1",
                    "1"::equals,
                    "1"::equals);

    public enum Type {
        VIEW,
        CREATE_POINT,
        CREATE_LINE,
        APPROVE,
        REJECT,
        CANCEL
    }

    public record Action(
            Type type,
            long session,
            UUID id,
            double x,
            double z,
            double endX,
            double endZ,
            String note) {
        public static final UUID NONE = new UUID(0, 0);

        public static Action simple(Type type, long session, UUID id) {
            return new Action(type, session, id == null ? NONE : id, 0, 0, 0, 0, "");
        }

        static Action read(FriendlyByteBuf b) {
            return new Action(
                    b.readEnum(Type.class),
                    b.readLong(),
                    b.readUUID(),
                    b.readDouble(),
                    b.readDouble(),
                    b.readDouble(),
                    b.readDouble(),
                    b.readUtf(96));
        }

        void write(FriendlyByteBuf b) {
            b.writeEnum(type);
            b.writeLong(session);
            b.writeUUID(id);
            b.writeDouble(x);
            b.writeDouble(z);
            b.writeDouble(endX);
            b.writeDouble(endZ);
            b.writeUtf(note, 96);
        }
    }

    public record Snapshot(
            long session,
            long revision,
            long tick,
            Actor actor,
            boolean battle,
            int cooldown,
            boolean skillReady,
            Bounds bounds,
            List<Request> requests,
            String message) {}

    public static void register() {
        CHANNEL.messageBuilder(Action.class, 0, NetworkDirection.PLAY_TO_SERVER)
                .encoder(Action::write)
                .decoder(Action::read)
                .consumerMainThread(
                        (p, c) -> {
                            var sender = c.get().getSender();
                            if (sender != null) FireRequestService.action(sender, p);
                            c.get().setPacketHandled(true);
                        })
                .add();
        CHANNEL.messageBuilder(Snapshot.class, 1, NetworkDirection.PLAY_TO_CLIENT)
                .encoder((p, b) -> b.writeUtf(JSON.toJson(p), 262144))
                .decoder(b -> JSON.fromJson(b.readUtf(262144), Snapshot.class))
                .consumerMainThread(
                        (p, c) -> {
                            DistExecutor.unsafeRunWhenOn(
                                    Dist.CLIENT,
                                    () ->
                                            () ->
                                                    org.espetro.client.support.FireSupportClient
                                                            .receive(p));
                            c.get().setPacketHandled(true);
                        })
                .add();
    }

    public static void send(ServerPlayer player, Snapshot snapshot) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), snapshot);
    }

    public static void send(Action action) {
        CHANNEL.sendToServer(action);
    }
}
