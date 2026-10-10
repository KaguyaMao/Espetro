/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.buffer.Unpooled
 *  net.minecraft.network.Connection
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.resources.ResourceLocation
 *  org.apache.commons.lang3.tuple.Pair
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.ApiStatus$Internal
 */
package net.minecraftforge.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.HandshakeHandler;
import net.minecraftforge.network.NetworkConstants;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.event.EventNetworkChannel;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;

public class LoginWrapper {
    private static final Logger LOGGER = LogManager.getLogger();
    @ApiStatus.Internal
    public static final ResourceLocation WRAPPER = new ResourceLocation("fml:loginwrapper");
    private EventNetworkChannel wrapperChannel = NetworkRegistry.ChannelBuilder.named(WRAPPER).clientAcceptedVersions(a -> true).serverAcceptedVersions(a -> true).networkProtocolVersion(() -> "FML3").eventNetworkChannel();

    LoginWrapper() {
        this.wrapperChannel.addListener(this::wrapperReceived);
    }

    private <T extends NetworkEvent> void wrapperReceived(T packet) {
        if (packet instanceof NetworkEvent.ChannelRegistrationChangeEvent) {
            return;
        }
        NetworkEvent.Context wrappedContext = packet.getSource().get();
        FriendlyByteBuf payload = packet.getPayload();
        ResourceLocation targetNetworkReceiver = NetworkConstants.FML_HANDSHAKE_RESOURCE;
        FriendlyByteBuf data = null;
        if (payload != null) {
            targetNetworkReceiver = payload.m_130281_();
            int payloadLength = payload.m_130242_();
            data = new FriendlyByteBuf(payload.readBytes(payloadLength));
        }
        int loginSequence = packet.getLoginIndex();
        LOGGER.debug(HandshakeHandler.FMLHSMARKER, "Recieved login wrapper packet event for channel {} with index {}", (Object)targetNetworkReceiver, (Object)loginSequence);
        NetworkEvent.Context context = new NetworkEvent.Context(wrappedContext.getNetworkManager(), wrappedContext.getDirection(), (rl, buf) -> {
            LOGGER.debug(HandshakeHandler.FMLHSMARKER, "Dispatching wrapped packet reply for channel {} with index {}", rl, (Object)loginSequence);
            wrappedContext.getPacketDispatcher().sendPacket(WRAPPER, this.wrapPacket((ResourceLocation)rl, (FriendlyByteBuf)buf));
        });
        NetworkEvent.LoginPayloadEvent loginPayloadEvent = new NetworkEvent.LoginPayloadEvent(data, () -> context, loginSequence);
        NetworkRegistry.findTarget(targetNetworkReceiver).ifPresent(ni -> {
            ni.dispatchLoginPacket(loginPayloadEvent);
            wrappedContext.setPacketHandled(context.getPacketHandled());
        });
    }

    private FriendlyByteBuf wrapPacket(ResourceLocation rl, FriendlyByteBuf buf) {
        FriendlyByteBuf pb = new FriendlyByteBuf(Unpooled.buffer((int)buf.capacity()));
        pb.m_130085_(rl);
        pb.m_130130_(buf.readableBytes());
        pb.writeBytes((ByteBuf)buf);
        return pb;
    }

    void sendServerToClientLoginPacket(ResourceLocation resourceLocation, FriendlyByteBuf buffer, int index, Connection manager) {
        FriendlyByteBuf pb = this.wrapPacket(resourceLocation, buffer);
        manager.m_129512_(NetworkDirection.LOGIN_TO_CLIENT.buildPacket((Pair<FriendlyByteBuf, Integer>)Pair.of((Object)pb, (Object)index), WRAPPER).getThis());
    }
}

