/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.pipeline.RenderTarget
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.Screenshot
 */
package com.sighs.apricityui.client;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.sighs.apricityui.ApricityUI;
import com.sighs.apricityui.config.ApricityUIConfig;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Comparator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;

public final class DebugAIScreenshotTicker {
    private static final long CAPTURE_INTERVAL_MS = 1000L;
    private static final int MAX_SCREENSHOTS = 20;
    private static long lastCaptureMs = 0L;
    private static long startMs = 0L;

    private DebugAIScreenshotTicker() {
    }

    public static void tick() {
        if (!((Boolean)ApricityUIConfig.CLIENT.aiAutoScreenshot.get()).booleanValue()) {
            startMs = 0L;
            lastCaptureMs = 0L;
            return;
        }
        long now = System.currentTimeMillis();
        if (startMs == 0L) {
            startMs = now;
        }
        if (now - lastCaptureMs < 1000L) {
            return;
        }
        Minecraft minecraft = Minecraft.m_91087_();
        if (minecraft == null) {
            return;
        }
        RenderTarget target = minecraft.m_91385_();
        if (target == null) {
            return;
        }
        lastCaptureMs = now;
        File baseDir = new File(minecraft.f_91069_, "screenshots");
        File screenshotDir = new File(baseDir, "aui");
        if (!screenshotDir.exists() && !screenshotDir.mkdirs()) {
            return;
        }
        DebugAIScreenshotTicker.cleanupOldScreenshots(screenshotDir);
        Screenshot.m_92289_((File)minecraft.f_91069_, (RenderTarget)target, message -> {
            DebugAIScreenshotTicker.moveLatestScreenshot(baseDir, screenshotDir);
            DebugAIScreenshotTicker.cleanupOldScreenshots(screenshotDir);
        });
    }

    private static void moveLatestScreenshot(File baseDir, File targetDir) {
        File[] files = baseDir.listFiles((d, name) -> name.toLowerCase().endsWith(".png"));
        if (files == null || files.length == 0) {
            return;
        }
        Arrays.sort(files, Comparator.comparingLong(File::lastModified).thenComparing(File::getName).reversed());
        File newest = files[0];
        Path dest = new File(targetDir, newest.getName()).toPath();
        try {
            Files.move(newest.toPath(), dest, StandardCopyOption.REPLACE_EXISTING);
        }
        catch (IOException e) {
            ApricityUI.LOGGER.warn("[AIDebug] Failed to move screenshot: {}", (Object)newest.getAbsolutePath());
        }
    }

    private static void cleanupOldScreenshots(File dir) {
        File[] files = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".png"));
        if (files == null || files.length <= 20) {
            return;
        }
        Arrays.sort(files, Comparator.comparingLong(File::lastModified).thenComparing(File::getName));
        int remaining = files.length;
        for (int i = 0; i < files.length && remaining > 20; ++i) {
            if (files[i].delete()) {
                --remaining;
                continue;
            }
            ApricityUI.LOGGER.warn("[AIDebug] Failed to delete old screenshot: {}", (Object)files[i].getAbsolutePath());
        }
    }
}

