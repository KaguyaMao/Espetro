/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.Util
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.screens.Screen
 */
package cc.sighs.auratip.editor.client;

import cc.sighs.auratip.AuraTip;
import cc.sighs.auratip.editor.net.EditorNettyServer;
import cc.sighs.auratip.editor.preview.EditorPreviewApplier;
import cc.sighs.auratip.editor.preview.EditorPreviewScreen;
import cc.sighs.auratip.editor.preview.EditorRadialPreviewApplier;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

public final class EditorClient {
    private static final Path DEV_EDITOR_HTML = Path.of("src/main/resources/assets/auratip/web/editor.html", new String[0]);
    private static EditorNettyServer server;

    private EditorClient() {
    }

    public static boolean isOpen() {
        return server != null && server.isRunning();
    }

    public static void open(String mode) {
        Minecraft mc = Minecraft.m_91087_();
        if (mc.f_91074_ == null) {
            return;
        }
        if (server == null) {
            server = new EditorNettyServer();
        }
        if (!server.isRunning()) {
            server.start();
        }
        mc.m_91152_((Screen)new EditorPreviewScreen(EditorClient::close));
        if ("radial".equalsIgnoreCase(mode)) {
            EditorRadialPreviewApplier.applyDefaultPreview();
        } else {
            EditorPreviewApplier.applyDefaultPreview();
        }
        EditorClient.openBrowser(server.getPort());
        AuraTip.LOGGER.info("AuraTip editor opened (mode={}, port={})", (Object)mode, (Object)server.getPort());
    }

    public static void close() {
        Minecraft mc = Minecraft.m_91087_();
        if (mc.f_91080_ instanceof EditorPreviewScreen) {
            mc.m_91152_(null);
        }
        if (server != null) {
            server.stop();
        }
        AuraTip.LOGGER.info("AuraTip editor closed");
    }

    private static void openBrowser(int port) {
        String url = Files.isRegularFile(DEV_EDITOR_HTML, new LinkOption[0]) ? String.valueOf(DEV_EDITOR_HTML.toAbsolutePath().toUri()) + "?wsPort=" + port : "http://127.0.0.1:" + port + "/editor.html";
        try {
            Util.m_137581_().m_137648_(new URI(url));
        }
        catch (Exception e) {
            AuraTip.LOGGER.warn("Failed to open editor browser URL: {}", (Object)url, (Object)e);
        }
    }
}

