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
import org.espetro.client.gui.VehicleWheelController;
import org.espetro.client.vehicle.VehicleInteractionKind;
import org.espetro.client.vehicle.VehicleInteractionState;
import org.espetro.network.DismountRequestPacket;
import org.espetro.network.NetworkManager;
import org.espetro.vehicle.SbwVehicleSeatResolver;
import org.espetro.vehicle.VehicleInteractionConfig;

public final class DismountGate {
    private static boolean registered;
    private static int ticks;
    private static boolean wasDown;

    private DismountGate() {
    }

    public static void register() {
        if (registered) {
            return;
        }
        registered = true;
        MinecraftForge.EVENT_BUS.register(DismountGate.class);
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Minecraft mc = Minecraft.m_91087_();
        if (mc.f_91074_ == null || mc.f_91080_ != null) {
            DismountGate.reset();
            wasDown = false;
            return;
        }
        if (mc.f_91074_.m_20202_() == null || !SbwVehicleSeatResolver.isSupportedVehicle(mc.f_91074_.m_20202_())) {
            DismountGate.reset();
            wasDown = false;
            return;
        }
        int delay = VehicleInteractionConfig.dismountDelayTicks();
        if (delay <= 0) {
            DismountGate.reset();
            wasDown = false;
            return;
        }
        boolean down = VehicleWheelController.isInteractHeld();
        if (!down) {
            if (wasDown) {
                DismountGate.reset();
            }
            wasDown = false;
            return;
        }
        wasDown = true;
        float progress = Math.min(1.0f, (float)(++ticks) / (float)delay);
        VehicleInteractionState.setDismount(progress);
        if (ticks >= delay) {
            NetworkManager.NET.sendToServer((Object)new DismountRequestPacket());
            DismountGate.reset();
        }
    }

    private static void reset() {
        ticks = 0;
        if (VehicleInteractionState.kind() == VehicleInteractionKind.DISMOUNT) {
            VehicleInteractionState.clear();
        }
    }
}

