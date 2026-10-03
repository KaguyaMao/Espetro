/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  net.minecraft.ChatFormatting
 *  net.minecraft.Util
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.AbstractSelectionList$Entry
 *  net.minecraft.client.gui.components.ObjectSelectionList
 *  net.minecraft.client.gui.components.ObjectSelectionList$Entry
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.ErrorScreen
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.util.FormattedCharSequence
 *  net.minecraftforge.fml.LoadingFailedException
 *  net.minecraftforge.fml.ModLoadingException
 *  net.minecraftforge.fml.ModLoadingWarning
 *  net.minecraftforge.fml.loading.FMLPaths
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraftforge.client.gui;

import com.google.common.base.Strings;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.ErrorScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.client.gui.widget.ExtendedButton;
import net.minecraftforge.common.ForgeI18n;
import net.minecraftforge.fml.LoadingFailedException;
import net.minecraftforge.fml.ModLoadingException;
import net.minecraftforge.fml.ModLoadingWarning;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoadingErrorScreen
extends ErrorScreen {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Path modsDir;
    private final Path logFile;
    private final List<ModLoadingException> modLoadErrors;
    private final List<ModLoadingWarning> modLoadWarnings;
    private final Path dumpedLocation;
    private LoadingEntryList entryList;
    private Component errorHeader;
    private Component warningHeader;

    public LoadingErrorScreen(LoadingFailedException loadingException, List<ModLoadingWarning> warnings, File dumpedLocation) {
        super((Component)Component.m_237113_((String)"Loading Error"), null);
        this.modLoadWarnings = warnings;
        this.modLoadErrors = loadingException == null ? Collections.emptyList() : loadingException.getErrors();
        this.modsDir = FMLPaths.MODSDIR.get();
        this.logFile = FMLPaths.GAMEDIR.get().resolve(Paths.get("logs", "latest.log"));
        this.dumpedLocation = dumpedLocation != null ? dumpedLocation.toPath() : null;
    }

    public void m_7856_() {
        super.m_7856_();
        this.m_169413_();
        this.errorHeader = Component.m_237113_((String)(String.valueOf(ChatFormatting.RED) + ForgeI18n.parseMessage("fml.loadingerrorscreen.errorheader", this.modLoadErrors.size()) + String.valueOf(ChatFormatting.RESET)));
        this.warningHeader = Component.m_237113_((String)(String.valueOf(ChatFormatting.YELLOW) + ForgeI18n.parseMessage("fml.loadingerrorscreen.warningheader", this.modLoadErrors.size()) + String.valueOf(ChatFormatting.RESET)));
        int yOffset = 46;
        this.m_142416_((GuiEventListener)new ExtendedButton(50, this.f_96544_ - yOffset, this.f_96543_ / 2 - 55, 20, (Component)Component.m_237113_((String)ForgeI18n.parseMessage("fml.button.open.mods.folder", new Object[0])), b -> Util.m_137581_().m_137644_(this.modsDir.toFile())));
        this.m_142416_((GuiEventListener)new ExtendedButton(this.f_96543_ / 2 + 5, this.f_96544_ - yOffset, this.f_96543_ / 2 - 55, 20, (Component)Component.m_237113_((String)ForgeI18n.parseMessage("fml.button.open.file", this.logFile.getFileName())), b -> Util.m_137581_().m_137644_(this.logFile.toFile())));
        if (this.modLoadErrors.isEmpty()) {
            this.m_142416_((GuiEventListener)new ExtendedButton(this.f_96543_ / 4, this.f_96544_ - 24, this.f_96543_ / 2, 20, (Component)Component.m_237113_((String)ForgeI18n.parseMessage("fml.button.continue.launch", new Object[0])), b -> this.f_96541_.m_91152_(null)));
        } else {
            this.m_142416_((GuiEventListener)new ExtendedButton(this.f_96543_ / 4, this.f_96544_ - 24, this.f_96543_ / 2, 20, (Component)Component.m_237113_((String)ForgeI18n.parseMessage("fml.button.open.file", this.dumpedLocation.getFileName())), b -> Util.m_137581_().m_137644_(this.dumpedLocation.toFile())));
        }
        this.entryList = new LoadingEntryList(this, this.modLoadErrors, this.modLoadWarnings);
        this.m_7787_((GuiEventListener)this.entryList);
        this.m_7522_((GuiEventListener)this.entryList);
    }

    public void m_88315_(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.m_280273_(guiGraphics);
        this.entryList.m_88315_(guiGraphics, mouseX, mouseY, partialTick);
        this.drawMultiLineCenteredString(guiGraphics, this.f_96547_, this.modLoadErrors.isEmpty() ? this.warningHeader : this.errorHeader, this.f_96543_ / 2, 10);
        this.f_169369_.forEach(button -> button.m_88315_(guiGraphics, mouseX, mouseY, partialTick));
    }

    private void drawMultiLineCenteredString(GuiGraphics guiGraphics, Font fr, Component str, int x, int y) {
        for (FormattedCharSequence s : fr.m_92923_((FormattedText)str, this.f_96543_)) {
            guiGraphics.drawString(fr, s, (float)((double)x - (double)fr.m_92724_(s) / 2.0), (float)y, 0xFFFFFF, true);
            Objects.requireNonNull(fr);
            y += 9;
        }
    }

    public static class LoadingEntryList
    extends ObjectSelectionList<LoadingMessageEntry> {
        LoadingEntryList(LoadingErrorScreen parent, List<ModLoadingException> errors, List<ModLoadingWarning> warnings) {
            boolean both;
            int n = parent.f_96543_;
            int n2 = parent.f_96544_;
            int n3 = parent.f_96544_ - 50;
            int n4 = Math.max(errors.stream().mapToInt(error -> parent.f_96547_.m_92923_((FormattedText)Component.m_237113_((String)(error.getMessage() != null ? error.getMessage() : "")), parent.f_96543_ - 20).size()).max().orElse(0), warnings.stream().mapToInt(warning -> parent.f_96547_.m_92923_((FormattedText)Component.m_237113_((String)(warning.formatToString() != null ? warning.formatToString() : "")), parent.f_96543_ - 20).size()).max().orElse(0));
            Objects.requireNonNull(((LoadingErrorScreen)parent).f_96541_.f_91062_);
            super(Objects.requireNonNull(parent.f_96541_), n, n2, 35, n3, n4 * 9 + 8);
            boolean bl = both = !errors.isEmpty() && !warnings.isEmpty();
            if (both) {
                this.m_7085_((AbstractSelectionList.Entry)new LoadingMessageEntry(parent.errorHeader, true));
            }
            errors.forEach(e -> this.m_7085_((AbstractSelectionList.Entry)new LoadingMessageEntry((Component)Component.m_237113_((String)e.formatToString()))));
            if (both) {
                int maxChars = (this.f_93388_ - 10) / ((LoadingErrorScreen)parent).f_96541_.f_91062_.m_92895_("-");
                this.m_7085_((AbstractSelectionList.Entry)new LoadingMessageEntry((Component)Component.m_237113_((String)("\n" + Strings.repeat((String)"-", (int)maxChars) + "\n"))));
                this.m_7085_((AbstractSelectionList.Entry)new LoadingMessageEntry(parent.warningHeader, true));
            }
            warnings.forEach(w -> this.m_7085_((AbstractSelectionList.Entry)new LoadingMessageEntry((Component)Component.m_237113_((String)w.formatToString()))));
        }

        protected int m_5756_() {
            return this.getRight() - 6;
        }

        public int m_5759_() {
            return this.f_93388_;
        }

        public class LoadingMessageEntry
        extends ObjectSelectionList.Entry<LoadingMessageEntry> {
            private final Component message;
            private final boolean center;

            LoadingMessageEntry(Component message) {
                this(message, false);
            }

            LoadingMessageEntry(Component message, boolean center) {
                this.message = Objects.requireNonNull(message);
                this.center = center;
            }

            public Component m_142172_() {
                return Component.m_237110_((String)"narrator.select", (Object[])new Object[]{this.message});
            }

            public void m_6311_(GuiGraphics guiGraphics, int entryIdx, int top, int left, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean p_194999_5_, float partialTick) {
                Font font = Minecraft.m_91087_().f_91062_;
                List strings = font.m_92923_((FormattedText)this.message, LoadingEntryList.this.f_93388_ - 20);
                int y = top + 2;
                for (FormattedCharSequence string : strings) {
                    if (this.center) {
                        guiGraphics.drawString(font, string, (float)left + (float)LoadingEntryList.this.f_93388_ / 2.0f - (float)font.m_92724_(string) / 2.0f, (float)y, 0xFFFFFF, false);
                    } else {
                        guiGraphics.m_280649_(font, string, left + 5, y, 0xFFFFFF, false);
                    }
                    Objects.requireNonNull(font);
                    y += 9;
                }
            }
        }
    }
}

