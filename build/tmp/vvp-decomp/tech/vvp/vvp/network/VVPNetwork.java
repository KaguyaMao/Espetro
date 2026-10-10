/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraftforge.network.NetworkDirection
 *  net.minecraftforge.network.NetworkEvent$Context
 *  net.minecraftforge.network.NetworkRegistry
 *  net.minecraftforge.network.simple.SimpleChannel
 */
package tech.vvp.vvp.network;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import tech.vvp.vvp.network.message.HimarsFdcCancelMessage;
import tech.vvp.vvp.network.message.HimarsFdcDesignateMessage;
import tech.vvp.vvp.network.message.HimarsFdcFireMessage;
import tech.vvp.vvp.network.message.PantsirLockRequestMessage;
import tech.vvp.vvp.network.message.PantsirRadarSyncMessage;
import tech.vvp.vvp.network.message.SeatSwapMessage;

public class VVPNetwork {
    public static final String MOD_ID = "vvp";
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel VVP_HANDLER = NetworkRegistry.newSimpleChannel((ResourceLocation)new ResourceLocation("vvp", "vvp"), () -> "1", "1"::equals, "1"::equals);
    private static int messageID = 0;

    public static <T> void addNetworkMessage(Class<T> messageType, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder, BiConsumer<T, Supplier<NetworkEvent.Context>> messageConsumer) {
        VVP_HANDLER.registerMessage(messageID, messageType, encoder, decoder, messageConsumer);
        ++messageID;
    }

    public static <T> void addNetworkMessage(Class<T> messageType, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder, BiConsumer<T, Supplier<NetworkEvent.Context>> messageConsumer, Optional<NetworkDirection> direction) {
        VVP_HANDLER.registerMessage(messageID, messageType, encoder, decoder, messageConsumer, direction);
        ++messageID;
    }

    public static void register() {
        VVPNetwork.addNetworkMessage(PantsirLockRequestMessage.class, PantsirLockRequestMessage::encode, PantsirLockRequestMessage::decode, PantsirLockRequestMessage::handler, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        VVPNetwork.addNetworkMessage(PantsirRadarSyncMessage.class, PantsirRadarSyncMessage::encode, PantsirRadarSyncMessage::decode, PantsirRadarSyncMessage::handler, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        VVPNetwork.addNetworkMessage(SeatSwapMessage.class, SeatSwapMessage::encode, SeatSwapMessage::decode, SeatSwapMessage::handler, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        VVPNetwork.addNetworkMessage(HimarsFdcDesignateMessage.class, HimarsFdcDesignateMessage::encode, HimarsFdcDesignateMessage::decode, HimarsFdcDesignateMessage::handler, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        VVPNetwork.addNetworkMessage(HimarsFdcFireMessage.class, HimarsFdcFireMessage::encode, HimarsFdcFireMessage::decode, HimarsFdcFireMessage::handler, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        VVPNetwork.addNetworkMessage(HimarsFdcCancelMessage.class, HimarsFdcCancelMessage::encode, HimarsFdcCancelMessage::decode, HimarsFdcCancelMessage::handler, Optional.of(NetworkDirection.PLAY_TO_SERVER));
    }
}

