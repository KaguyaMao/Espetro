/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraftforge.client.gui.overlay.ForgeGui
 *  net.minecraftforge.client.gui.overlay.IGuiOverlay
 */
package com.example.espoints.hud;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class MessagePopup
implements IGuiOverlay {
    private static final MessagePopup INSTANCE = new MessagePopup();
    private static final int DEFAULT_DURATION = 4000;
    private static final int ANIMATION_DURATION = 1000;
    private static final int DEFAULT_WIDTH = 150;
    private static final int DEFAULT_HEIGHT = 40;
    private static final int DEFAULT_BACKGROUND_COLOR = -16777216;
    private static final int DEFAULT_TEXT_COLOR = -1;
    private static final int DEFAULT_BORDER_COLOR = -5592406;
    private static final int DEFAULT_BORDER_WIDTH = 2;
    private final ConcurrentMap<UUID, List<MessageEntry>> playerMessages = new ConcurrentHashMap<UUID, List<MessageEntry>>();

    private MessagePopup() {
    }

    public static MessagePopup getInstance() {
        return INSTANCE;
    }

    public void showWinMessage(UUID playerUUID) {
        this.showMessage(playerUUID, "\u80dc\u5229", 4000L);
    }

    public void showLoseMessage(UUID playerUUID) {
        this.showMessage(playerUUID, "\u5931\u8d25", 4000L);
    }

    public void showMessage(UUID playerUUID, String message, long duration) {
        this.showMessage(playerUUID, message, duration, 150, 40, -16777216, -1, -5592406, 2);
    }

    public void showMessage(UUID playerUUID, String message, long duration, int width, int height, int backgroundColor, int textColor, int borderColor, int borderWidth) {
        long currentTime = System.currentTimeMillis();
        long endTime = currentTime + duration;
        MessageEntry entry = new MessageEntry(message, currentTime, endTime, width, height, backgroundColor, textColor, borderColor, borderWidth);
        List messages = this.playerMessages.computeIfAbsent(playerUUID, k -> new ArrayList());
        messages.add(entry);
    }

    public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        Minecraft minecraft = Minecraft.m_91087_();
        long currentTime = System.currentTimeMillis();
        this.playerMessages.entrySet().removeIf(entry -> {
            ((List)entry.getValue()).removeIf(message -> currentTime > message.endTime);
            return ((List)entry.getValue()).isEmpty();
        });
        if (minecraft.f_91074_ == null) {
            return;
        }
        UUID playerUUID = minecraft.f_91074_.m_20148_();
        List messages = (List)this.playerMessages.get(playerUUID);
        if (messages == null || messages.isEmpty()) {
            return;
        }
        for (MessageEntry entry2 : messages) {
            this.renderMessage(guiGraphics, screenWidth, screenHeight, entry2, currentTime);
        }
    }

    private void renderMessage(GuiGraphics guiGraphics, int screenWidth, int screenHeight, MessageEntry entry, long currentTime) {
        Minecraft minecraft = Minecraft.m_91087_();
        long animationProgress = currentTime - entry.startTime;
        float timeProgress = Math.min(1.0f, (float)animationProgress / 1000.0f);
        float easeProgress = this.easeOutCubic(timeProgress);
        int finalX = screenWidth - entry.width - 10;
        int startX = screenWidth;
        int windowX = (int)((float)startX + (float)(finalX - startX) * easeProgress);
        int windowY = (screenHeight - entry.height) / 2;
        guiGraphics.m_280509_(windowX, windowY, windowX + entry.width, windowY + entry.height, entry.backgroundColor);
        int borderWidth = entry.borderWidth;
        if (borderWidth > 0) {
            guiGraphics.m_280509_(windowX, windowY, windowX + entry.width, windowY + borderWidth, entry.borderColor);
            guiGraphics.m_280509_(windowX, windowY + entry.height - borderWidth, windowX + entry.width, windowY + entry.height, entry.borderColor);
            guiGraphics.m_280509_(windowX, windowY, windowX + borderWidth, windowY + entry.height, entry.borderColor);
            guiGraphics.m_280509_(windowX + entry.width - borderWidth, windowY, windowX + entry.width, windowY + entry.height, entry.borderColor);
        }
        int textWidth = minecraft.f_91062_.m_92895_(entry.message);
        int textX = windowX + (entry.width - textWidth) / 2;
        int n = entry.height;
        Objects.requireNonNull(minecraft.f_91062_);
        int textY = windowY + (n - 9) / 2;
        guiGraphics.m_280056_(minecraft.f_91062_, entry.message, textX, textY, entry.textColor, false);
    }

    private float easeOutCubic(float t) {
        return 1.0f - (float)Math.pow(1.0f - t, 3.0);
    }

    private static class MessageEntry {
        String message;
        long startTime;
        long endTime;
        int width;
        int height;
        int backgroundColor;
        int textColor;
        int borderColor;
        int borderWidth;

        MessageEntry(String message, long startTime, long endTime, int width, int height, int backgroundColor, int textColor, int borderColor, int borderWidth) {
            this.message = message;
            this.startTime = startTime;
            this.endTime = endTime;
            this.width = width;
            this.height = height;
            this.backgroundColor = backgroundColor;
            this.textColor = textColor;
            this.borderColor = borderColor;
            this.borderWidth = borderWidth;
        }
    }
}

