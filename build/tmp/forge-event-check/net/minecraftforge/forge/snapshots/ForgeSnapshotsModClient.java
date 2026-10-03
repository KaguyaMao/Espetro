/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.screens.TitleScreen
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraftforge.fml.VersionChecker$Status
 */
package net.minecraftforge.forge.snapshots;

import java.util.Objects;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraftforge.fml.VersionChecker;

public class ForgeSnapshotsModClient {
    public static void renderMainMenuWarning(VersionChecker.Status status, TitleScreen gui, GuiGraphics graphics, Font font, int width, int height, int alpha) {
        if (status == VersionChecker.Status.BETA || status == VersionChecker.Status.BETA_OUTDATED) {
            MutableComponent line = Component.m_237110_((String)"forge.update.beta.1", (Object[])new Object[]{ChatFormatting.RED, ChatFormatting.RESET}).m_130940_(ChatFormatting.RED);
            int n = width / 2;
            Objects.requireNonNull(font);
            graphics.m_280653_(font, (Component)line, n, 4 + 0 * (9 + 1), 0xFFFFFF | alpha);
            line = Component.m_237115_((String)"forge.update.beta.2");
            int n2 = width / 2;
            Objects.requireNonNull(font);
            graphics.m_280653_(font, (Component)line, n2, 4 + 1 * (9 + 1), 0xFFFFFF | alpha);
        }
    }
}

