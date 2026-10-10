/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraftforge.event.TickEvent$ClientTickEvent
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.loading.FMLEnvironment
 */
package com.sighs.apricityui.client;

import com.sighs.apricityui.ApricityUI;
import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.parser.HTML;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import net.minecraft.client.Minecraft;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;

public final class QwenHtmlOverlayDebug {
    private static final String SOURCE_URI = "file:/D:/work/AUI/targets/forge-1.20.1/run/apricity/overlays/Qwen_html.html";
    private static final String SCANNED_TEMPLATE_PATH = "overlays/Qwen_html.html";
    private static final int OPEN_DELAY_TICKS = 10;
    private static int elapsedTicks;
    private static boolean attempted;

    private QwenHtmlOverlayDebug() {
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || attempted || FMLEnvironment.production) {
            return;
        }
        Minecraft minecraft = Minecraft.m_91087_();
        if (minecraft == null || minecraft.m_91268_() == null) {
            return;
        }
        if (++elapsedTicks < 10) {
            return;
        }
        if (HTML.getTemple(SCANNED_TEMPLATE_PATH) == null) {
            return;
        }
        attempted = true;
        Path source = Path.of(URI.create(SOURCE_URI));
        if (!Files.isRegularFile(source, new LinkOption[0])) {
            ApricityUI.LOGGER.warn("[QwenOverlayDebug] HTML file is missing: {}", (Object)source);
            return;
        }
        try {
            String markup = Files.readString(source, StandardCharsets.UTF_8);
            HTML.putTemple(SOURCE_URI, markup);
            Document.remove(SOURCE_URI);
            Document overlay = Document.create(SOURCE_URI);
            if (overlay == null) {
                ApricityUI.LOGGER.error("[QwenOverlayDebug] Failed to create overlay: {}", (Object)SOURCE_URI);
                return;
            }
            overlay.setReloadPersistent(true);
            ApricityUI.LOGGER.info("[QwenOverlayDebug] Opened persistent overlay: {}", (Object)SOURCE_URI);
        }
        catch (IOException | LinkageError | RuntimeException exception) {
            ApricityUI.LOGGER.error("[QwenOverlayDebug] Failed to open overlay: {}", (Object)SOURCE_URI, (Object)exception);
        }
    }
}

