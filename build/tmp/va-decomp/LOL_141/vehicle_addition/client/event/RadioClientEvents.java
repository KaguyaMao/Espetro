/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.KeyMapping
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.client.event.InputEvent$MouseScrollingEvent
 *  net.minecraftforge.event.TickEvent$ClientTickEvent
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  org.lwjgl.glfw.GLFW
 */
package LOL_141.vehicle_addition.client.event;

import LOL_141.vehicle_addition.client.audio.VehicleRadioBroadcaster;
import LOL_141.vehicle_addition.client.gui.RadioConfigScreen;
import LOL_141.vehicle_addition.init.ModKeyMappings;
import LOL_141.vehicle_addition.radio.RadioManager;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid="vehicle_addition", value={Dist.CLIENT})
public final class RadioClientEvents {
    private static boolean wasMiddleDown = false;

    private RadioClientEvents() {
    }

    @SubscribeEvent
    public static void onMouseScroll(InputEvent.MouseScrollingEvent event) {
        if (!Screen.m_96638_()) {
            return;
        }
        if (Minecraft.m_91087_().f_91074_ == null) {
            return;
        }
        if (RadioManager.isController()) {
            RadioManager.onScroll(event.getScrollDelta());
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        boolean middleDown;
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Minecraft mc = Minecraft.m_91087_();
        if (mc.f_91074_ == null) {
            wasMiddleDown = false;
            return;
        }
        while (((KeyMapping)ModKeyMappings.RADIO_CONFIG.get()).m_90859_()) {
            mc.m_91152_((Screen)new RadioConfigScreen());
        }
        boolean bl = middleDown = GLFW.glfwGetMouseButton((long)mc.m_91268_().m_85439_(), (int)2) == 1;
        if (middleDown && !wasMiddleDown && RadioManager.isController()) {
            RadioManager.nextSong();
        }
        wasMiddleDown = middleDown;
        VehicleRadioBroadcaster.tick();
        RadioManager.clientTick();
    }
}

