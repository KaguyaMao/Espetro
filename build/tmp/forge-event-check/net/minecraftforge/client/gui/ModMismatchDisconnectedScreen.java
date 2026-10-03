/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.Tesselator
 *  net.minecraft.ChatFormatting
 *  net.minecraft.Util
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.components.MultiLineLabel
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.narration.NarratableEntry$NarrationPriority
 *  net.minecraft.client.gui.narration.NarrationElementOutput
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.ClickEvent
 *  net.minecraft.network.chat.ClickEvent$Action
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.HoverEvent
 *  net.minecraft.network.chat.HoverEvent$Action
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.network.chat.Style
 *  net.minecraft.network.chat.TextColor
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.FormattedCharSequence
 *  net.minecraftforge.fml.ModList
 *  net.minecraftforge.fml.loading.FMLPaths
 *  org.apache.commons.lang3.tuple.Pair
 */
package net.minecraftforge.client.gui;

import com.mojang.blaze3d.vertex.Tesselator;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.client.gui.widget.ScrollPanel;
import net.minecraftforge.common.ForgeI18n;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.network.ConnectionData;
import net.minecraftforge.network.NetworkRegistry;
import org.apache.commons.lang3.tuple.Pair;

public class ModMismatchDisconnectedScreen
extends Screen {
    private final Component reason;
    private MultiLineLabel message = MultiLineLabel.f_94331_;
    private final Screen parent;
    private int textHeight;
    private final ConnectionData.ModMismatchData modMismatchData;
    private final Path modsDir;
    private final Path logFile;
    private final int listHeight;
    private final Map<ResourceLocation, Pair<String, String>> presentModData;
    private final List<ResourceLocation> missingModData;
    private final Map<ResourceLocation, String> mismatchedModData;
    private final List<String> allModIds;
    private final Map<String, String> presentModUrls;
    private final boolean mismatchedDataFromServer;

    public ModMismatchDisconnectedScreen(Screen parentScreen, Component title, Component reason, ConnectionData.ModMismatchData modMismatchData) {
        super(title);
        this.parent = parentScreen;
        this.reason = reason;
        this.modMismatchData = modMismatchData;
        this.modsDir = FMLPaths.MODSDIR.get();
        this.logFile = FMLPaths.GAMEDIR.get().resolve(Paths.get("logs", "latest.log"));
        this.listHeight = modMismatchData.containsMismatches() ? 140 : 0;
        this.mismatchedDataFromServer = modMismatchData.mismatchedDataFromServer();
        this.presentModData = modMismatchData.presentModData();
        this.missingModData = modMismatchData.mismatchedModData().entrySet().stream().filter(e -> ((String)e.getValue()).equals(NetworkRegistry.ABSENT.version())).map(Map.Entry::getKey).collect(Collectors.toList());
        this.mismatchedModData = modMismatchData.mismatchedModData().entrySet().stream().filter(e -> !((String)e.getValue()).equals(NetworkRegistry.ABSENT.version())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        this.allModIds = this.presentModData.keySet().stream().map(ResourceLocation::m_135827_).distinct().collect(Collectors.toList());
        this.presentModUrls = ModList.get().getMods().stream().filter(info -> this.allModIds.contains(info.getModId())).map(info -> Pair.of((Object)info.getModId(), (Object)info.getConfig().getConfigElement(new String[]{"displayURL"}).orElse(""))).collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
    }

    protected void m_7856_() {
        this.message = MultiLineLabel.m_94341_((Font)this.f_96547_, (FormattedText)this.reason, (int)(this.f_96543_ - 50));
        this.textHeight = this.message.m_5770_() * 9;
        int listLeft = Math.max(8, this.f_96543_ / 2 - 220);
        int listWidth = Math.min(440, this.f_96543_ - 16);
        int upperButtonHeight = Math.min((this.f_96544_ + this.listHeight + this.textHeight) / 2 + 10, this.f_96544_ - 50);
        int lowerButtonHeight = Math.min((this.f_96544_ + this.listHeight + this.textHeight) / 2 + 35, this.f_96544_ - 25);
        if (this.modMismatchData.containsMismatches()) {
            this.m_142416_((GuiEventListener)new MismatchInfoPanel(this.f_96541_, listWidth, this.listHeight, (this.f_96544_ - this.listHeight) / 2, listLeft));
        }
        int buttonWidth = Math.min(210, this.f_96543_ / 2 - 20);
        this.m_142416_((GuiEventListener)Button.m_253074_((Component)Component.m_237113_((String)ForgeI18n.parseMessage("fml.button.open.file", this.logFile.getFileName())), button -> Util.m_137581_().m_137644_(this.logFile.toFile())).m_252987_(Math.max(this.f_96543_ / 4 - buttonWidth / 2, listLeft), upperButtonHeight, buttonWidth, 20).m_253136_());
        this.m_142416_((GuiEventListener)Button.m_253074_((Component)Component.m_237113_((String)ForgeI18n.parseMessage("fml.button.open.mods.folder", new Object[0])), button -> Util.m_137581_().m_137644_(this.modsDir.toFile())).m_252987_(Math.min(this.f_96543_ * 3 / 4 - buttonWidth / 2, listLeft + listWidth - buttonWidth), upperButtonHeight, buttonWidth, 20).m_253136_());
        this.m_142416_((GuiEventListener)Button.m_253074_((Component)Component.m_237115_((String)"gui.toMenu"), button -> this.f_96541_.m_91152_(this.parent)).m_252987_((this.f_96543_ - buttonWidth) / 2, lowerButtonHeight, buttonWidth, 20).m_253136_());
    }

    public void m_88315_(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.m_280273_(guiGraphics);
        int textYOffset = this.modMismatchData.containsMismatches() ? 18 : 0;
        guiGraphics.m_280653_(this.f_96547_, this.f_96539_, this.f_96543_ / 2, (this.f_96544_ - this.listHeight - this.textHeight) / 2 - textYOffset - 18, 0xAAAAAA);
        this.message.m_6276_(guiGraphics, this.f_96543_ / 2, (this.f_96544_ - this.listHeight - this.textHeight) / 2 - textYOffset);
        super.m_88315_(guiGraphics, mouseX, mouseY, partialTicks);
    }

    class MismatchInfoPanel
    extends ScrollPanel {
        private final List<Pair<FormattedCharSequence, Pair<FormattedCharSequence, FormattedCharSequence>>> lineTable;
        private final int contentSize;
        private final int nameIndent = 10;
        private final int tableWidth;
        private final int nameWidth;
        private final int versionWidth;

        public MismatchInfoPanel(Minecraft client, int width, int height, int top, int left) {
            int i;
            super(client, width, height, top, left);
            this.nameIndent = 10;
            this.tableWidth = this.width - this.border * 2 - 6 - 10;
            this.nameWidth = this.tableWidth * 3 / 5;
            this.versionWidth = (this.tableWidth - this.nameWidth) / 2;
            ArrayList<Pair> rawTable = new ArrayList<Pair>();
            if (!ModMismatchDisconnectedScreen.this.missingModData.isEmpty()) {
                rawTable.add(Pair.of((Object)Component.m_237113_((String)ForgeI18n.parseMessage(ModMismatchDisconnectedScreen.this.mismatchedDataFromServer ? "fml.modmismatchscreen.missingmods.server" : "fml.modmismatchscreen.missingmods.client", new Object[0])).m_130940_(ChatFormatting.GRAY), null));
                rawTable.add(Pair.of((Object)Component.m_237113_((String)ForgeI18n.parseMessage("fml.modmismatchscreen.table.modname", new Object[0])).m_130940_(ChatFormatting.UNDERLINE), (Object)Pair.of((Object)"", (Object)ForgeI18n.parseMessage(ModMismatchDisconnectedScreen.this.mismatchedDataFromServer ? "fml.modmismatchscreen.table.youhave" : "fml.modmismatchscreen.table.youneed", new Object[0]))));
                i = 0;
                for (ResourceLocation resourceLocation : ModMismatchDisconnectedScreen.this.missingModData) {
                    rawTable.add(Pair.of((Object)this.toModNameComponent(resourceLocation, (String)ModMismatchDisconnectedScreen.this.presentModData.get(resourceLocation).getLeft(), i), (Object)Pair.of((Object)"", (Object)((String)ModMismatchDisconnectedScreen.this.presentModData.getOrDefault(resourceLocation, (Pair<String, String>)Pair.of((Object)"", (Object)"")).getRight()))));
                    if (++i < 10) continue;
                    rawTable.add(Pair.of((Object)Component.m_237113_((String)ForgeI18n.parseMessage("fml.modmismatchscreen.additional", ModMismatchDisconnectedScreen.this.missingModData.size() - i)).m_130940_(ChatFormatting.ITALIC), (Object)Pair.of((Object)"", (Object)"")));
                    break;
                }
                rawTable.add(Pair.of((Object)Component.m_237113_((String)" "), null));
            }
            if (!ModMismatchDisconnectedScreen.this.mismatchedModData.isEmpty()) {
                rawTable.add(Pair.of((Object)Component.m_237113_((String)ForgeI18n.parseMessage("fml.modmismatchscreen.mismatchedmods", new Object[0])).m_130940_(ChatFormatting.GRAY), null));
                rawTable.add(Pair.of((Object)Component.m_237113_((String)ForgeI18n.parseMessage("fml.modmismatchscreen.table.modname", new Object[0])).m_130940_(ChatFormatting.UNDERLINE), (Object)Pair.of((Object)ForgeI18n.parseMessage(ModMismatchDisconnectedScreen.this.mismatchedDataFromServer ? "fml.modmismatchscreen.table.youhave" : "fml.modmismatchscreen.table.serverhas", new Object[0]), (Object)ForgeI18n.parseMessage(ModMismatchDisconnectedScreen.this.mismatchedDataFromServer ? "fml.modmismatchscreen.table.serverhas" : "fml.modmismatchscreen.table.youhave", new Object[0]))));
                i = 0;
                for (Map.Entry entry : ModMismatchDisconnectedScreen.this.mismatchedModData.entrySet()) {
                    rawTable.add(Pair.of((Object)this.toModNameComponent((ResourceLocation)entry.getKey(), (String)ModMismatchDisconnectedScreen.this.presentModData.get(entry.getKey()).getLeft(), i), (Object)Pair.of((Object)((String)ModMismatchDisconnectedScreen.this.presentModData.getOrDefault(entry.getKey(), (Pair<String, String>)Pair.of((Object)"", (Object)"")).getRight()), (Object)((String)entry.getValue()))));
                    if (++i < 10) continue;
                    rawTable.add(Pair.of((Object)Component.m_237113_((String)ForgeI18n.parseMessage("fml.modmismatchscreen.additional", ModMismatchDisconnectedScreen.this.mismatchedModData.size() - i)).m_130940_(ChatFormatting.ITALIC), (Object)Pair.of((Object)"", (Object)"")));
                    break;
                }
                rawTable.add(Pair.of((Object)Component.m_237113_((String)" "), null));
            }
            this.lineTable = rawTable.stream().flatMap(p -> this.splitLineToWidth((MutableComponent)p.getKey(), (Pair<String, String>)((Pair)p.getValue())).stream()).collect(Collectors.toList());
            this.contentSize = this.lineTable.size();
        }

        private List<Pair<FormattedCharSequence, Pair<FormattedCharSequence, FormattedCharSequence>>> splitLineToWidth(MutableComponent name, Pair<String, String> versions) {
            Style style = name.m_7383_();
            int versionColumns = versions == null ? 0 : (((String)versions.getLeft()).isEmpty() ? (((String)versions.getRight()).isEmpty() ? 0 : 1) : 2);
            int adaptedNameWidth = this.nameWidth + this.versionWidth * (2 - versionColumns) - 4;
            List nameLines = ModMismatchDisconnectedScreen.this.f_96547_.m_92923_((FormattedText)name, adaptedNameWidth);
            List clientVersionLines = ModMismatchDisconnectedScreen.this.f_96547_.m_92923_((FormattedText)Component.m_237113_((String)(versions != null ? (String)versions.getLeft() : "")).m_6270_(style), this.versionWidth - 4);
            List serverVersionLines = ModMismatchDisconnectedScreen.this.f_96547_.m_92923_((FormattedText)Component.m_237113_((String)(versions != null ? (String)versions.getRight() : "")).m_6270_(style), this.versionWidth - 4);
            ArrayList<Pair<FormattedCharSequence, Pair<FormattedCharSequence, FormattedCharSequence>>> splitLines = new ArrayList<Pair<FormattedCharSequence, Pair<FormattedCharSequence, FormattedCharSequence>>>();
            int rowsOccupied = Math.max(nameLines.size(), Math.max(clientVersionLines.size(), serverVersionLines.size()));
            for (int i = 0; i < rowsOccupied; ++i) {
                splitLines.add((Pair<FormattedCharSequence, Pair<FormattedCharSequence, FormattedCharSequence>>)Pair.of((Object)(i < nameLines.size() ? (FormattedCharSequence)nameLines.get(i) : FormattedCharSequence.f_13691_), versions == null ? null : Pair.of((Object)(i < clientVersionLines.size() ? (FormattedCharSequence)clientVersionLines.get(i) : FormattedCharSequence.f_13691_), (Object)(i < serverVersionLines.size() ? (FormattedCharSequence)serverVersionLines.get(i) : FormattedCharSequence.f_13691_))));
            }
            return splitLines;
        }

        private MutableComponent toModNameComponent(ResourceLocation id, String modName, int color) {
            String modId = id.m_135827_();
            String tooltipId = id.m_135815_().isEmpty() ? id.m_135827_() : id.toString();
            return Component.m_237113_((String)modName).m_130940_(color % 2 == 0 ? ChatFormatting.GOLD : ChatFormatting.YELLOW).m_130938_(s -> s.m_131144_(new HoverEvent(HoverEvent.Action.f_130831_, (Object)Component.m_237113_((String)(tooltipId + (String)(!ModMismatchDisconnectedScreen.this.presentModUrls.getOrDefault(modId, "").isEmpty() ? "\n" + ForgeI18n.parseMessage("fml.modmismatchscreen.homepage", new Object[0]) : "")))))).m_130938_(s -> s.m_131142_(!ModMismatchDisconnectedScreen.this.presentModUrls.getOrDefault(modId, "").isEmpty() ? new ClickEvent(ClickEvent.Action.OPEN_URL, ModMismatchDisconnectedScreen.this.presentModUrls.get(modId)) : null));
        }

        @Override
        protected int getContentHeight() {
            Objects.requireNonNull(ModMismatchDisconnectedScreen.this.f_96547_);
            int height = this.contentSize * (9 + 3);
            if (height < this.bottom - this.top - 4) {
                height = this.bottom - this.top - 4;
            }
            return height;
        }

        @Override
        protected void drawPanel(GuiGraphics guiGraphics, int entryRight, int relativeY, Tesselator tess, int mouseX, int mouseY) {
            int i = 0;
            for (Pair<FormattedCharSequence, Pair<FormattedCharSequence, FormattedCharSequence>> line : this.lineTable) {
                FormattedCharSequence name = (FormattedCharSequence)line.getLeft();
                Pair versions = (Pair)line.getRight();
                int color = Optional.ofNullable(ModMismatchDisconnectedScreen.this.f_96547_.m_92865_().m_92338_(name, 0)).map(Style::m_131135_).map(TextColor::m_131265_).orElse(0xFFFFFF);
                int nameLeft = this.left + this.border + (versions == null ? 0 : 10);
                guiGraphics.m_280649_(ModMismatchDisconnectedScreen.this.f_96547_, name, nameLeft, relativeY + i * 12, color, false);
                if (versions != null) {
                    guiGraphics.m_280649_(ModMismatchDisconnectedScreen.this.f_96547_, (FormattedCharSequence)versions.getLeft(), this.left + this.border + 10 + this.nameWidth, relativeY + i * 12, color, false);
                    guiGraphics.m_280649_(ModMismatchDisconnectedScreen.this.f_96547_, (FormattedCharSequence)versions.getRight(), this.left + this.border + 10 + this.nameWidth + this.versionWidth, relativeY + i * 12, color, false);
                }
                ++i;
            }
        }

        @Override
        public void m_88315_(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
            super.m_88315_(guiGraphics, mouseX, mouseY, partialTicks);
            Style style = this.getComponentStyleAt(mouseX, mouseY);
            if (style != null && style.m_131186_() != null) {
                guiGraphics.m_280304_(ModMismatchDisconnectedScreen.this.f_96547_, style, mouseX, mouseY);
            }
        }

        public Style getComponentStyleAt(double x, double y) {
            double relativeX;
            double relativeY;
            int slotIndex;
            if (this.m_5953_(x, y) && (slotIndex = (int)((relativeY = y - (double)this.top + (double)this.scrollDistance - (double)this.border) + (double)(this.border / 2)) / 12) < this.contentSize && (relativeX = x - (double)this.left - (double)this.border - (double)(this.lineTable.get(slotIndex).getRight() == null ? 0 : 10)) >= 0.0) {
                return ModMismatchDisconnectedScreen.this.f_96547_.m_92865_().m_92338_((FormattedCharSequence)this.lineTable.get(slotIndex).getLeft(), (int)relativeX);
            }
            return null;
        }

        @Override
        public boolean m_6375_(double mouseX, double mouseY, int button) {
            Style style = this.getComponentStyleAt(mouseX, mouseY);
            if (style != null) {
                ModMismatchDisconnectedScreen.this.m_5561_(style);
                return true;
            }
            return super.m_6375_(mouseX, mouseY, button);
        }

        public NarratableEntry.NarrationPriority m_142684_() {
            return NarratableEntry.NarrationPriority.NONE;
        }

        public void m_142291_(NarrationElementOutput output) {
        }
    }
}

