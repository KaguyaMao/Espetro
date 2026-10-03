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
package com.redabysslucia.dragonrise_reforge.network;

import com.redabysslucia.dragonrise_reforge.network.EngineChangeModeMessage;
import com.redabysslucia.dragonrise_reforge.network.message.OwnBombMessage;
import com.redabysslucia.dragonrise_reforge.network.message.R6DroneControlMessage;
import com.redabysslucia.dragonrise_reforge.network.message.SetFireControlMessage;
import com.redabysslucia.dragonrise_reforge.network.message.ToggleTakeoverMessage;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModNetwork {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel PACKET_HANDLER = NetworkRegistry.newSimpleChannel((ResourceLocation)new ResourceLocation("dragonrise_reforge", "dragonrise_reforge"), () -> "1", "1"::equals, "1"::equals);
    public static int messageID = 0;

    public static void register() {
        ModNetwork.playToServer(EngineChangeModeMessage.class, EngineChangeModeMessage::encode, EngineChangeModeMessage::decode, EngineChangeModeMessage::handler);
        ModNetwork.playToServer(SetFireControlMessage.class, SetFireControlMessage::encode, SetFireControlMessage::decode, SetFireControlMessage::handle);
        ModNetwork.playToServer(ToggleTakeoverMessage.class, ToggleTakeoverMessage::encode, ToggleTakeoverMessage::decode, ToggleTakeoverMessage::handle);
        ModNetwork.playToServer(R6DroneControlMessage.class, R6DroneControlMessage::encode, R6DroneControlMessage::decode, R6DroneControlMessage::handle);
        ModNetwork.playToClient(OwnBombMessage.class, OwnBombMessage::encode, OwnBombMessage::decode, OwnBombMessage::handle);
    }

    public static <T> void playToClient(Class<T> messageType, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder, BiConsumer<T, Supplier<NetworkEvent.Context>> messageConsumer) {
        PACKET_HANDLER.registerMessage(messageID, messageType, encoder, decoder, messageConsumer, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        ++messageID;
    }

    public static <T> void playToClient(T instance, Consumer<Supplier<NetworkEvent.Context>> messageConsumer) {
        Class<?> type = instance.getClass();
        PACKET_HANDLER.registerMessage(messageID, type, (msg, buf) -> {}, buf -> instance, (msg, ctx) -> messageConsumer.accept((Supplier<NetworkEvent.Context>)ctx), Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        ++messageID;
    }

    public static <T> void playToServer(Class<T> messageType, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder, BiConsumer<T, Supplier<NetworkEvent.Context>> messageConsumer) {
        PACKET_HANDLER.registerMessage(messageID, messageType, encoder, decoder, messageConsumer, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        ++messageID;
    }

    public static <T> void playToServer(T instance, Consumer<Supplier<NetworkEvent.Context>> messageConsumer) {
        Class<?> type = instance.getClass();
        PACKET_HANDLER.registerMessage(messageID, type, (msg, buf) -> {}, buf -> instance, (msg, ctx) -> messageConsumer.accept((Supplier<NetworkEvent.Context>)ctx), Optional.of(NetworkDirection.PLAY_TO_SERVER));
        ++messageID;
    }
}

