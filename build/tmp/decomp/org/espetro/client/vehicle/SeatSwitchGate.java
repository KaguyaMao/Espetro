/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.event.TickEvent$ClientTickEvent
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 */
package org.espetro.client.vehicle;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.espetro.client.vehicle.VehicleInteractionKind;
import org.espetro.client.vehicle.VehicleInteractionState;
import org.espetro.network.NetworkManager;
import org.espetro.network.SeatSwitchReadyPacket;
import org.espetro.vehicle.SbwVehicleSeatResolver;
import org.espetro.vehicle.VehicleInteractionConfig;

public final class SeatSwitchGate {
    private static int switchTicks;
    private static boolean armed;
    private static boolean registered;
    private static long armedUntilClientTick;

    private SeatSwitchGate() {
    }

    public static void register() {
        if (registered) {
            return;
        }
        registered = true;
        MinecraftForge.EVENT_BUS.register(SeatSwitchGate.class);
    }

    public static boolean isArmed() {
        if (!armed) {
            return VehicleInteractionConfig.seatSwitchDelayTicks() <= 0;
        }
        Minecraft mc = Minecraft.m_91087_();
        if (mc.f_91073_ == null) {
            return false;
        }
        return mc.f_91073_.m_46467_() <= armedUntilClientTick;
    }

    public static void consumeArmed() {
        armed = false;
        switchTicks = 0;
        armedUntilClientTick = 0L;
        if (VehicleInteractionState.kind() == VehicleInteractionKind.SEAT_SWITCH) {
            VehicleInteractionState.clear();
        }
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Minecraft mc = Minecraft.m_91087_();
        if (mc == null || mc.f_91074_ == null || mc.f_91080_ != null) {
            SeatSwitchGate.reset();
            return;
        }
        if (mc.f_91074_.m_20202_() == null || !SbwVehicleSeatResolver.isSupportedVehicle(mc.f_91074_.m_20202_())) {
            SeatSwitchGate.reset();
            return;
        }
        int delay = VehicleInteractionConfig.seatSwitchDelayTicks();
        if (delay <= 0) {
            armed = true;
            armedUntilClientTick = 0x1FFFFFFFFFFFFFFFL;
            return;
        }
        boolean shiftDown = mc.f_91066_.f_92090_.m_90857_();
        if (!shiftDown) {
            if (armed && mc.f_91073_ != null && mc.f_91073_.m_46467_() > armedUntilClientTick) {
                SeatSwitchGate.reset();
            } else if (!armed) {
                SeatSwitchGate.reset();
            }
            return;
        }
        if (armed) {
            VehicleInteractionState.setSeatSwitch(1.0f);
            return;
        }
        float progress = Math.min(1.0f, (float)(++switchTicks) / (float)delay);
        VehicleInteractionState.setSeatSwitch(progress);
        if (switchTicks >= delay) {
            armed = true;
            armedUntilClientTick = mc.f_91073_.m_46467_() + 40L;
            NetworkManager.NET.sendToServer((Object)new SeatSwitchReadyPacket());
            VehicleInteractionState.setSeatSwitch(1.0f);
        }
    }

    private static void reset() {
        if ((switchTicks > 0 || armed) && VehicleInteractionState.kind() == VehicleInteractionKind.SEAT_SWITCH) {
            VehicleInteractionState.clear();
        }
        switchTicks = 0;
        armed = false;
        armedUntilClientTick = 0L;
    }
}

