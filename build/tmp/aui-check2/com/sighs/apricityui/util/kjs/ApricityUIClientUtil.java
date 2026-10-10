/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.world.phys.Vec3
 */
package com.sighs.apricityui.util.kjs;

import com.sighs.apricityui.ApricityUI;
import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.init.Window;
import com.sighs.apricityui.registry.annotation.KJSBindings;
import com.sighs.apricityui.screen.AuiLinkedScreen;
import com.sighs.apricityui.ui.ToastManager;
import com.sighs.apricityui.world.WorldWindow;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.phys.Vec3;

@KJSBindings(value="ApricityUI", isClient=true)
public class ApricityUIClientUtil {
    public static Window getWindow() {
        return Window.window;
    }

    public static Document createDocument(String path) {
        return Document.create(path);
    }

    public static Document createInWorldDocument(String path) {
        return Document.createInWorld(path);
    }

    public static void removeDocument(String path) {
        Document.remove(path);
    }

    public static ArrayList<Document> getDocument(String path) {
        return Document.get(path);
    }

    public static Document getDocumentByUUID(String uuid) {
        return Document.getByUUID(uuid);
    }

    public static Document getCurrentScreenDocument() {
        Screen screen = Minecraft.m_91087_().f_91080_;
        if (screen instanceof AuiLinkedScreen) {
            AuiLinkedScreen screen2 = (AuiLinkedScreen)screen;
            return screen2.getLinkedDocument();
        }
        return null;
    }

    public static List<Document> getAllDocument() {
        return Document.getAll();
    }

    public static String toast(String message) {
        return ToastManager.show(message);
    }

    public static String toast(String message, int durationMs) {
        return ToastManager.show(message, durationMs);
    }

    public static String toast(String message, int durationMs, String backgroundColor, String textColor, String borderColor, boolean dismissOnClick, String customStyle) {
        ToastManager.ToastOptions options = new ToastManager.ToastOptions(durationMs, dismissOnClick, backgroundColor, textColor, borderColor, customStyle);
        return ToastManager.show(message, options);
    }

    public static void dismissToast(String id) {
        ToastManager.dismiss(id);
    }

    public static void clearToasts() {
        ToastManager.clear();
    }

    public static void screen(String path) {
        ApricityUI.screen(path);
    }

    @Deprecated
    public static void openScreen(String path) {
        ApricityUIClientUtil.screen(path);
    }

    public static void closeScreen() {
        ApricityUI.closeScreen();
    }

    @Deprecated
    public static WorldWindow createWorldWindow(String path, double x, double y, double z, float width, float height, int maxDistance) {
        WorldWindow window = new WorldWindow(path, new Vec3(x, y, z), width, height, maxDistance);
        WorldWindow.addWindow(window);
        return window;
    }

    public static WorldWindow createWorldWindow(String path, double x, double y, double z, int maxDistance) {
        WorldWindow window = new WorldWindow(path, new Vec3(x, y, z), maxDistance);
        WorldWindow.addWindow(window);
        return window;
    }

    public static WorldWindow createWorldWindow(String path, double x, double y, double z, int maxDistance, int maxDisplayDistance) {
        WorldWindow window = ApricityUIClientUtil.createWorldWindow(path, x, y, z, maxDistance);
        window.setMaxDisplayDistance(maxDisplayDistance);
        return window;
    }

    public static WorldWindow createWorldWindow(String path, double x, double y, double z, int maxDistance, float yaw, float pitch) {
        WorldWindow window = new WorldWindow(path, new Vec3(x, y, z), maxDistance, yaw, pitch);
        WorldWindow.addWindow(window);
        return window;
    }

    public static WorldWindow createWorldWindow(String path, double x, double y, double z, int maxDistance, float yaw, float pitch, float roll) {
        WorldWindow window = new WorldWindow(path, new Vec3(x, y, z), maxDistance, new Vec3((double)pitch, (double)yaw, (double)roll));
        WorldWindow.addWindow(window);
        return window;
    }

    public static void removeWorldWindow(WorldWindow window) {
        if (window == null) {
            return;
        }
        WorldWindow.removeWindow(window);
    }

    public static void clearWorldWindows() {
        WorldWindow.clear();
    }
}

