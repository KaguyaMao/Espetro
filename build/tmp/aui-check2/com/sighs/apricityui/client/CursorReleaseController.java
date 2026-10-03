/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.MouseHandler
 */
package com.sighs.apricityui.client;

import com.sighs.apricityui.spi.AuiServices;
import com.sighs.apricityui.style.Cursor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;

public final class CursorReleaseController {
    private static boolean active;
    private static boolean restoreMouseGrab;

    private CursorReleaseController() {
    }

    public static void tick() {
        Minecraft minecraft = Minecraft.m_91087_();
        MouseHandler mouseHandler = minecraft.f_91067_;
        boolean available = minecraft.f_91073_ != null && minecraft.f_91080_ == null && minecraft.m_91265_() == null && minecraft.m_91302_();
        CursorReleaseController.update(AuiServices.keys().isReleaseMouseDown(), available, mouseHandler.m_91600_(), () -> ((MouseHandler)mouseHandler).m_91602_(), () -> {
            Cursor.resetToDefault();
            mouseHandler.m_91601_();
        });
    }

    public static boolean isActive() {
        return active;
    }

    public static void update(boolean requested, boolean available, boolean mouseGrabbed, Runnable releaseMouse, Runnable grabMouse) {
        if (requested && available) {
            if (!active) {
                active = true;
                restoreMouseGrab = mouseGrabbed;
                if (mouseGrabbed) {
                    releaseMouse.run();
                }
            } else if (mouseGrabbed) {
                restoreMouseGrab = true;
                releaseMouse.run();
            }
            return;
        }
        if (!active) {
            return;
        }
        boolean shouldRestore = restoreMouseGrab && available && !mouseGrabbed;
        active = false;
        restoreMouseGrab = false;
        if (shouldRestore) {
            grabMouse.run();
        }
    }

    public static void resetForTest() {
        active = false;
        restoreMouseGrab = false;
    }
}

