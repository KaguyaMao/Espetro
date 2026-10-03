/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.glfw.GLFW
 */
package org.espetro.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import org.espetro.client.gui.EspetroMenuScreen;
import org.espetro.client.gui.TutorialHudOverlay;
import org.espetro.client.gui.TutorialPreviewFactory;
import org.espetro.network.NetworkManager;
import org.espetro.network.TutorialActionPacket;
import org.espetro.tutorial.TutorialStep;
import org.lwjgl.glfw.GLFW;

public final class TutorialClientController {
    private static boolean active;
    private static boolean allowSkip;
    private static String stepId;
    private static int index;
    private static int total;
    private static boolean actionPending;
    private static boolean enterWasDown;

    private TutorialClientController() {
    }

    public static boolean isActive() {
        return active;
    }

    public static String getStepId() {
        return stepId;
    }

    public static int getIndex() {
        return index;
    }

    public static int getTotal() {
        return total;
    }

    public static boolean isAllowSkip() {
        return allowSkip;
    }

    public static void show(String newStepId, int newIndex, int newTotal, boolean newAllowSkip) {
        active = true;
        actionPending = false;
        stepId = newStepId == null ? "" : newStepId;
        index = newIndex;
        total = newTotal;
        allowSkip = newAllowSkip;
        TutorialHudOverlay.onStepChanged();
        TutorialClientController.openPreviewForStep(TutorialStep.byId(stepId));
    }

    public static void clear() {
        boolean wasActive = active;
        active = false;
        actionPending = true;
        enterWasDown = false;
        stepId = "";
        index = 0;
        total = 0;
        TutorialHudOverlay.clear();
        if (wasActive) {
            TutorialClientController.closePreviewScreen();
        }
    }

    public static void tick() {
        boolean enterDown;
        if (!active || actionPending) {
            return;
        }
        Minecraft mc = Minecraft.m_91087_();
        if (mc == null || mc.f_91074_ == null) {
            return;
        }
        if (mc.f_91080_ instanceof ChatScreen) {
            return;
        }
        long window = mc.m_91268_().m_85439_();
        boolean bl = enterDown = GLFW.glfwGetKey((long)window, (int)257) == 1 || GLFW.glfwGetKey((long)window, (int)335) == 1;
        if (enterDown && !enterWasDown && mc.f_91080_ == null) {
            TutorialClientController.requestNext();
        }
        enterWasDown = enterDown;
    }

    public static boolean handleKeyPressed(int keyCode, int scanCode, int modifiers) {
        if (!active || actionPending) {
            return false;
        }
        Minecraft mc = Minecraft.m_91087_();
        if (mc != null && mc.f_91080_ instanceof ChatScreen) {
            return false;
        }
        if (keyCode == 257 || keyCode == 335) {
            TutorialClientController.requestNext();
            return true;
        }
        return false;
    }

    public static void requestNext() {
        if (!active || actionPending) {
            return;
        }
        actionPending = true;
        NetworkManager.NET.sendToServer((Object)TutorialActionPacket.next(stepId));
    }

    public static void requestSkipAll() {
        if (!active) {
            return;
        }
        actionPending = true;
        NetworkManager.NET.sendToServer((Object)TutorialActionPacket.skipAll());
    }

    private static void closePreviewScreen() {
        EspetroMenuScreen screen;
        Screen screen2;
        Minecraft mc = Minecraft.m_91087_();
        if (mc != null && (screen2 = mc.f_91080_) instanceof EspetroMenuScreen && (screen = (EspetroMenuScreen)screen2).isTutorialPreviewMode()) {
            mc.m_91152_(null);
        }
    }

    private static void openPreviewForStep(TutorialStep step) {
        Minecraft mc = Minecraft.m_91087_();
        if (mc == null || mc.f_91074_ == null || step == null) {
            return;
        }
        Screen preview = TutorialPreviewFactory.create(step);
        if (preview == null) {
            EspetroMenuScreen screen;
            Screen screen2 = mc.f_91080_;
            if (screen2 instanceof EspetroMenuScreen && (screen = (EspetroMenuScreen)screen2).isTutorialPreviewMode()) {
                mc.m_91152_(null);
            }
            return;
        }
        if (preview instanceof EspetroMenuScreen) {
            EspetroMenuScreen mutil = (EspetroMenuScreen)preview;
            mutil.setTutorialPreviewMode(true);
        }
        mc.m_91152_(preview);
    }

    static {
        allowSkip = true;
        stepId = "";
    }
}

