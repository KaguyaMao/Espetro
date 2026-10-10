/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraftforge.network.NetworkRegistry
 *  net.minecraftforge.network.PacketDistributor
 *  net.minecraftforge.network.simple.SimpleChannel
 */
package LOL_141.vehicle_addition.network;

import LOL_141.vehicle_addition.network.VehicleRadioPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public final class NetworkHandler {
    public static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel((ResourceLocation)new ResourceLocation("vehicle_addition", "vehicle_radio"), () -> "1", "1"::equals, "1"::equals);

    private NetworkHandler() {
    }

    public static void register() {
        CHANNEL.registerMessage(0, VehicleRadioPacket.class, VehicleRadioPacket::encode, VehicleRadioPacket::decode, VehicleRadioPacket::handle);
    }

    public static void sendToServer(VehicleRadioPacket packet) {
        CHANNEL.sendToServer((Object)packet);
    }

    public static void sendToPlayer(VehicleRadioPacket packet, ServerPlayer player) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), (Object)packet);
    }
}

