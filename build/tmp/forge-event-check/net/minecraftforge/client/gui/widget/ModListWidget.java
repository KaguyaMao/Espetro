/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.AbstractSelectionList$Entry
 *  net.minecraft.client.gui.components.ObjectSelectionList
 *  net.minecraft.client.gui.components.ObjectSelectionList$Entry
 *  net.minecraft.locale.Language
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.FormattedCharSequence
 *  net.minecraft.util.StringUtil
 *  net.minecraftforge.fml.VersionChecker
 *  net.minecraftforge.fml.VersionChecker$CheckResult
 *  net.minecraftforge.forgespi.language.IModInfo
 */
package net.minecraftforge.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.StringUtil;
import net.minecraftforge.client.gui.ModListScreen;
import net.minecraftforge.common.util.MavenVersionStringHelper;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.forgespi.language.IModInfo;

public class ModListWidget
extends ObjectSelectionList<ModEntry> {
    private static final ResourceLocation VERSION_CHECK_ICONS = new ResourceLocation("forge", "textures/gui/version_check_icons.png");
    private final int listWidth;
    private final ModListScreen parent;

    private static String stripControlCodes(String value) {
        return StringUtil.m_14406_((String)value);
    }

    public ModListWidget(ModListScreen parent, int listWidth, int top, int bottom) {
        Minecraft minecraft = parent.getMinecraftInstance();
        int n = parent.f_96544_;
        Objects.requireNonNull(parent.getFontRenderer());
        super(minecraft, listWidth, n, top, bottom, 9 * 2 + 8);
        this.parent = parent;
        this.listWidth = listWidth;
        this.refreshList();
    }

    protected int m_5756_() {
        return this.listWidth;
    }

    public int m_5759_() {
        return this.listWidth;
    }

    public void refreshList() {
        this.m_93516_();
        this.parent.buildModList(x$0 -> this.m_7085_((AbstractSelectionList.Entry)x$0), mod -> new ModEntry((IModInfo)mod, this.parent));
    }

    protected void m_7733_(GuiGraphics guiGraphics) {
        this.parent.m_280273_(guiGraphics);
    }

    public class ModEntry
    extends ObjectSelectionList.Entry<ModEntry> {
        private final IModInfo modInfo;
        private final ModListScreen parent;

        ModEntry(IModInfo info, ModListScreen parent) {
            this.modInfo = info;
            this.parent = parent;
        }

        public Component m_142172_() {
            return Component.m_237110_((String)"narrator.select", (Object[])new Object[]{this.modInfo.getDisplayName()});
        }

        public void m_6311_(GuiGraphics guiGraphics, int entryIdx, int top, int left, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isMouseOver, float partialTick) {
            MutableComponent name = Component.m_237113_((String)ModListWidget.stripControlCodes(this.modInfo.getDisplayName()));
            MutableComponent version = Component.m_237113_((String)ModListWidget.stripControlCodes(MavenVersionStringHelper.artifactVersionToString(this.modInfo.getVersion())));
            VersionChecker.CheckResult vercheck = VersionChecker.getResult((IModInfo)this.modInfo);
            Font font = this.parent.getFontRenderer();
            guiGraphics.m_280649_(font, Language.m_128107_().m_5536_(FormattedText.m_130773_((FormattedText[])new FormattedText[]{font.m_92854_((FormattedText)name, ModListWidget.this.listWidth)})), left + 3, top + 2, 0xFFFFFF, false);
            FormattedCharSequence formattedCharSequence = Language.m_128107_().m_5536_(FormattedText.m_130773_((FormattedText[])new FormattedText[]{font.m_92854_((FormattedText)version, ModListWidget.this.listWidth)}));
            Objects.requireNonNull(font);
            guiGraphics.m_280649_(font, formattedCharSequence, left + 3, top + 2 + 9, 0xCCCCCC, false);
            if (vercheck.status().shouldDraw()) {
                RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
                guiGraphics.m_280168_().m_85836_();
                guiGraphics.m_280163_(VERSION_CHECK_ICONS, ModListWidget.this.getLeft() + ModListWidget.this.f_93388_ - 12, top + entryHeight / 4, (float)(vercheck.status().getSheetOffset() * 8), vercheck.status().isAnimated() && (System.currentTimeMillis() / 800L & 1L) == 1L ? 8.0f : 0.0f, 8, 8, 64, 16);
                guiGraphics.m_280168_().m_85849_();
            }
        }

        public boolean m_6375_(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
            this.parent.setSelected(this);
            ModListWidget.this.m_6987_((AbstractSelectionList.Entry)this);
            return false;
        }

        public IModInfo getInfo() {
            return this.modInfo;
        }
    }
}

