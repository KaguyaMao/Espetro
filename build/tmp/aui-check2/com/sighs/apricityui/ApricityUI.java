/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Quaternionf
 *  org.slf4j.Logger
 */
package com.sighs.apricityui;

import com.mojang.logging.LogUtils;
import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.init.Window;
import com.sighs.apricityui.spi.AuiPendingMenu;
import com.sighs.apricityui.spi.AuiServices;
import com.sighs.apricityui.world.WorldWindow;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.slf4j.Logger;

public final class ApricityUI {
    public static final String MODID = "apricityui";
    public static final Logger LOGGER = LogUtils.getLogger();

    private ApricityUI() {
    }

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

    public static List<Document> getAllDocument() {
        return Document.getAll();
    }

    public static void screen(String path) {
        AuiServices.client().openScreen(path);
    }

    public static AuiPendingMenu menu(ServerPlayer player, String templatePath) {
        return AuiServices.network().pendingMenu(player, templatePath);
    }

    @Deprecated
    public static void openScreen(String path) {
        ApricityUI.screen(path);
    }

    public static void closeScreen() {
        AuiServices.client().closeScreen();
    }

    @Deprecated
    public static WorldWindow createWorldWindow(String documentPath, Vec3 position, float width, float height, int maxDistance) {
        WorldWindow window = new WorldWindow(documentPath, position, width, height, maxDistance);
        WorldWindow.addWindow(window);
        return window;
    }

    public static WorldWindow createWorldWindow(String documentPath, Vec3 position, int maxDistance) {
        WorldWindow window = new WorldWindow(documentPath, position, maxDistance);
        WorldWindow.addWindow(window);
        return window;
    }

    public static WorldWindow createWorldWindow(String documentPath, Vec3 position, int maxDistance, int maxDisplayDistance) {
        WorldWindow window = ApricityUI.createWorldWindow(documentPath, position, maxDistance);
        window.setMaxDisplayDistance(maxDisplayDistance);
        return window;
    }

    public static WorldWindow createWorldWindow(String documentPath, double x, double y, double z, int maxDistance) {
        WorldWindow window = new WorldWindow(documentPath, x, y, z, maxDistance);
        WorldWindow.addWindow(window);
        return window;
    }

    public static WorldWindow createWorldWindow(String documentPath, double x, double y, double z, int maxDistance, int maxDisplayDistance) {
        WorldWindow window = ApricityUI.createWorldWindow(documentPath, x, y, z, maxDistance);
        window.setMaxDisplayDistance(maxDisplayDistance);
        return window;
    }

    public static WorldWindow createWorldWindow(String documentPath, Vec3 position, int maxDistance, float yaw, float pitch) {
        WorldWindow window = new WorldWindow(documentPath, position, maxDistance, yaw, pitch);
        WorldWindow.addWindow(window);
        return window;
    }

    public static WorldWindow createWorldWindow(String documentPath, Vec3 position, int maxDistance, float yaw, float pitch, float roll) {
        WorldWindow window = new WorldWindow(documentPath, position, maxDistance, yaw, pitch, roll);
        WorldWindow.addWindow(window);
        return window;
    }

    public static WorldWindow createWorldWindow(String documentPath, Vec3 position, int maxDistance, Vec3 eulerDegrees) {
        WorldWindow window = new WorldWindow(documentPath, position, maxDistance, eulerDegrees);
        WorldWindow.addWindow(window);
        return window;
    }

    public static WorldWindow createWorldWindow(String documentPath, Vec3 position, int maxDistance, Quaternionf orientation) {
        WorldWindow window = new WorldWindow(documentPath, position, maxDistance, orientation);
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

