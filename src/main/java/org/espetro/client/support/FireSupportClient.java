package org.espetro.client.support;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.*;
import net.minecraftforge.fml.common.Mod;

import org.espetro.support.FireRequestNetwork;
import org.espetro.support.FireRequestNetwork.*;
import org.lwjgl.glfw.GLFW;

public final class FireSupportClient {
    public static Snapshot snapshot;
    private static int ticks;
    public static final KeyMapping OPEN =
            new KeyMapping(
                    "key.espetro.fire_requests",
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_U,
                    "key.categories.espetro");

    public static void open() {
        var mc = Minecraft.getInstance();
        if (mc.player == null || !connected()) return;
        mc.setScreen(new FireSupportScreen(mc.screen));
        refresh();
    }

    private static boolean connected() {
        var c = Minecraft.getInstance().getConnection();
        return c != null && FireRequestNetwork.CHANNEL.isRemotePresent(c.getConnection());
    }

    public static void refresh() {
        if (connected())
            FireRequestNetwork.send(
                    Action.simple(Type.VIEW, snapshot == null ? 0 : snapshot.session(), null));
    }

    public static void receive(Snapshot data) {
        snapshot = data;
        var mc = Minecraft.getInstance();
        if ("OPEN".equals(data.message())) mc.setScreen(new FireSupportScreen(mc.screen));
        else if (mc.screen instanceof FireSupportScreen s) s.message(data.message());
    }

    @Mod.EventBusSubscriber(
            modid = "espetro",
            value = Dist.CLIENT,
            bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEvents {
        @SubscribeEvent
        public static void keys(RegisterKeyMappingsEvent e) {
            e.register(OPEN);
        }
    }

    @Mod.EventBusSubscriber(modid = "espetro", value = Dist.CLIENT)
    public static class Events {
        @SubscribeEvent
        public static void tick(TickEvent.ClientTickEvent e) {
            if (e.phase != TickEvent.Phase.END) return;
            while (OPEN.consumeClick()) open();
            if (++ticks % 40 == 0 && Minecraft.getInstance().screen instanceof FireSupportScreen)
                refresh();
        }

        @SubscribeEvent
        public static void logout(ClientPlayerNetworkEvent.LoggingOut e) {
            snapshot = null;
            ticks = 0;
        }

        private static boolean deploy(net.minecraft.client.gui.screens.Screen s) {
            return s.getClass().getName().equals("org.espetro.client.gui.UnifiedDeployScreen");
        }

        @SubscribeEvent
        public static void render(ScreenEvent.Render.Post e) {
            if (deploy(e.getScreen()) && connected()) {
                int x = e.getScreen().width - 179;
                e.getGuiGraphics().fill(x, 6, x + 83, 25, 0xee263a30);
                FireSupportScreen.text(
                        e.getGuiGraphics(), "火力申请 [U]", x + 7, 10, 0xffe2c777, 9, 70);
            }
        }

        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public static void click(ScreenEvent.MouseButtonPressed.Pre e) {
            if (e.getScreen() instanceof FireSupportScreen s) {
                s.mouseClicked(e.getMouseX(), e.getMouseY(), e.getButton());
                e.setCanceled(true);
                return;
            }
            if (deploy(e.getScreen())
                    && e.getButton() == 0
                    && e.getMouseX() >= e.getScreen().width - 179
                    && e.getMouseX() < e.getScreen().width - 96
                    && e.getMouseY() >= 6
                    && e.getMouseY() < 25) {
                e.setCanceled(true);
                open();
            }
        }

        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public static void scroll(ScreenEvent.MouseScrolled.Pre e) {
            if (e.getScreen() instanceof FireSupportScreen s) {
                s.mouseScrolled(e.getMouseX(), e.getMouseY(), e.getScrollDelta());
                e.setCanceled(true);
            }
        }
    }
}
