/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.event.entity.EntityMountEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.DistExecutor
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.network.NetworkEvent$Context
 */
package LOL_141.vehicle_addition.network;

import LOL_141.vehicle_addition.client.audio.VehicleRadioBroadcaster;
import LOL_141.vehicle_addition.network.NetworkHandler;
import LOL_141.vehicle_addition.network.VehicleRadioPacket;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkEvent;

public final class VehicleRadioServer {
    private static final double BROADCAST_RANGE = 128.0;
    private static final Map<Integer, RadioState> RADIO = new HashMap<Integer, RadioState>();

    private VehicleRadioServer() {
    }

    public static void onClientPacket(VehicleRadioPacket packet) {
        DistExecutor.unsafeRunWhenOn((Dist)Dist.CLIENT, () -> () -> VehicleRadioBroadcaster.onPacket(packet.vehicleId, packet.url, packet.startTime));
    }

    public static void onServerPacket(VehicleRadioPacket packet, NetworkEvent.Context ctx) {
        boolean isStop;
        ServerPlayer sender = ctx.getSender();
        if (sender == null) {
            return;
        }
        Entity vehicle = sender.m_9236_().m_6815_(packet.vehicleId);
        boolean bl = isStop = packet.url == null || packet.url.isEmpty();
        if (vehicle == null) {
            return;
        }
        if (!isStop && sender.m_20202_() != vehicle) {
            return;
        }
        if (isStop) {
            RADIO.remove(packet.vehicleId);
        } else {
            RADIO.put(packet.vehicleId, new RadioState(packet.url, packet.startTime));
        }
        RadioState state = RADIO.get(packet.vehicleId);
        VehicleRadioPacket broadcast = state == null ? new VehicleRadioPacket(packet.vehicleId, "") : new VehicleRadioPacket(packet.vehicleId, state.url, state.startTime);
        for (ServerPlayer p : sender.f_8924_.m_6846_().m_11314_()) {
            if (p == sender || !(p.m_20280_(vehicle) <= 16384.0)) continue;
            NetworkHandler.sendToPlayer(broadcast, p);
        }
    }

    private record RadioState(String url, long startTime) {
    }

    @Mod.EventBusSubscriber(modid="vehicle_addition")
    public static final class MountResend {
        private MountResend() {
        }

        @SubscribeEvent
        public static void onMount(EntityMountEvent event) {
            if (!event.isMounting() || event.getLevel().m_5776_()) {
                return;
            }
            Entity passenger = event.getEntityMounting();
            Entity vehicle = event.getEntityBeingMounted();
            if (!(passenger instanceof Player) || vehicle == null) {
                return;
            }
            RadioState state = RADIO.get(vehicle.m_19879_());
            if (state == null) {
                return;
            }
            if (passenger instanceof ServerPlayer) {
                ServerPlayer serverPlayer = (ServerPlayer)passenger;
                NetworkHandler.sendToPlayer(new VehicleRadioPacket(vehicle.m_19879_(), state.url, state.startTime), serverPlayer);
            }
        }
    }
}

