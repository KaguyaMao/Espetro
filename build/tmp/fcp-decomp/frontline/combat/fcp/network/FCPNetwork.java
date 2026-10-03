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
package frontline.combat.fcp.network;

import frontline.combat.fcp.network.message.SetFireControlMessage;
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

public class FCPNetwork {
    public static final String MODID = "fcp";
    private static final String PROTOCOL_VERSION = "2";
    public static final SimpleChannel FCP_HANDLER = NetworkRegistry.newSimpleChannel((ResourceLocation)new ResourceLocation("fcp", "fcp"), () -> "2", "2"::equals, "2"::equals);
    private static int messageID = 0;

    public static <T> void addNetworkMessage(Class<T> messageType, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder, BiConsumer<T, Supplier<NetworkEvent.Context>> messageConsumer) {
        FCP_HANDLER.registerMessage(messageID, messageType, encoder, decoder, messageConsumer);
        ++messageID;
    }

    public static <T> void addNetworkMessage(Class<T> messageType, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder, BiConsumer<T, Supplier<NetworkEvent.Context>> messageConsumer, Optional<NetworkDirection> direction) {
        FCP_HANDLER.registerMessage(messageID, messageType, encoder, decoder, messageConsumer, direction);
        ++messageID;
    }

    public static void register() {
        FCPNetwork.addNetworkMessage(SetFireControlMessage.class, SetFireControlMessage::encode, SetFireControlMessage::decode, SetFireControlMessage::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
    }
}

