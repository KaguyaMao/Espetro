/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.buffer.Unpooled
 *  net.minecraft.network.Connection
 *  net.minecraft.network.ConnectionProtocol
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.network.PacketListener
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.network.protocol.PacketFlow
 *  net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket
 *  net.minecraft.resources.ResourceLocation
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraftforge.network.filters;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.ConnectionData;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.event.EventNetworkChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VanillaPacketSplitter {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ResourceLocation CHANNEL = new ResourceLocation("forge", "split");
    private static final String VERSION = "1.1";
    private static final int PROTOCOL_MAX = 0x800000;
    private static final int PAYLOAD_TO_CLIENT_MAX = 0x100000;
    private static final int PART_SIZE = 1048570;
    private static final byte STATE_FIRST = 1;
    private static final byte STATE_LAST = 2;
    private static final List<FriendlyByteBuf> receivedBuffers = new ArrayList<FriendlyByteBuf>();

    public static void register() {
        Predicate<String> versionCheck = NetworkRegistry.acceptMissingOr(VERSION);
        EventNetworkChannel channel = NetworkRegistry.newEventChannel(CHANNEL, () -> VERSION, versionCheck, versionCheck);
        channel.addListener(VanillaPacketSplitter::onClientPacket);
    }

    public static void appendPackets(ConnectionProtocol protocol, PacketFlow direction, Packet<?> packet, List<? super Packet<?>> out) {
        if (VanillaPacketSplitter.heuristicIsDefinitelySmallEnough(packet)) {
            out.add(packet);
        } else {
            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
            packet.m_5779_(buf);
            if (buf.readableBytes() <= 0x800000) {
                buf.release();
                out.add(packet);
            } else {
                int parts = (int)Math.ceil((double)buf.readableBytes() / 1048570.0);
                if (parts == 1) {
                    buf.release();
                    out.add(packet);
                } else {
                    for (int part = 0; part < parts; ++part) {
                        ByteBuf partPrefix;
                        if (part == 0) {
                            partPrefix = Unpooled.buffer((int)5);
                            partPrefix.writeByte(1);
                            new FriendlyByteBuf(partPrefix).m_130130_(protocol.m_264521_(direction, packet));
                        } else {
                            partPrefix = Unpooled.buffer((int)1);
                            partPrefix.writeByte(part == parts - 1 ? 2 : 0);
                        }
                        int partSize = Math.min(1048570, buf.readableBytes());
                        ByteBuf partBuf = Unpooled.wrappedBuffer((ByteBuf[])new ByteBuf[]{partPrefix, buf.retainedSlice(buf.readerIndex(), partSize)});
                        buf.skipBytes(partSize);
                        out.add((Packet<?>)new ClientboundCustomPayloadPacket(CHANNEL, new FriendlyByteBuf(partBuf)));
                    }
                    buf.release();
                }
            }
        }
    }

    private static boolean heuristicIsDefinitelySmallEnough(Packet<?> packet) {
        return false;
    }

    private static void onClientPacket(NetworkEvent.ServerCustomPayloadEvent event) {
        NetworkEvent.Context ctx = event.getSource().get();
        PacketFlow direction = ctx.getDirection() == NetworkDirection.PLAY_TO_CLIENT ? PacketFlow.CLIENTBOUND : PacketFlow.SERVERBOUND;
        ConnectionProtocol protocol = ConnectionProtocol.PLAY;
        ctx.setPacketHandled(true);
        FriendlyByteBuf buf = event.getPayload();
        byte state = buf.readByte();
        if (state == 1 && !receivedBuffers.isEmpty()) {
            LOGGER.warn("forge:split received out of order - inbound buffer not empty when receiving first");
            receivedBuffers.clear();
        }
        buf.retain();
        receivedBuffers.add(buf);
        if (state == 2) {
            FriendlyByteBuf full = new FriendlyByteBuf(Unpooled.wrappedBuffer((ByteBuf[])((ByteBuf[])receivedBuffers.toArray(new FriendlyByteBuf[0]))));
            int packetId = full.m_130242_();
            Packet packet = protocol.m_178321_(direction, packetId, full);
            if (packet == null) {
                LOGGER.error("Received invalid packet ID {} in forge:split", (Object)packetId);
            } else {
                receivedBuffers.clear();
                full.release();
                ctx.enqueueWork(() -> VanillaPacketSplitter.genericsFtw(packet, event.getSource().get().getNetworkManager().m_129538_()));
            }
        }
    }

    private static <T extends PacketListener> void genericsFtw(Packet<T> pkt, Object listener) {
        pkt.m_5797_((PacketListener)listener);
    }

    public static RemoteCompatibility getRemoteCompatibility(Connection manager) {
        ConnectionData connectionData = NetworkHooks.getConnectionData(manager);
        if (connectionData != null && connectionData.getChannels().containsKey((Object)CHANNEL)) {
            return RemoteCompatibility.PRESENT;
        }
        return RemoteCompatibility.ABSENT;
    }

    public static boolean isRemoteCompatible(Connection manager) {
        return VanillaPacketSplitter.getRemoteCompatibility(manager) != RemoteCompatibility.ABSENT;
    }

    public static enum RemoteCompatibility {
        ABSENT,
        PRESENT;

    }
}

