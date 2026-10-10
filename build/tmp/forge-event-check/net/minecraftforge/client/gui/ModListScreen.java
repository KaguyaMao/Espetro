/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.NativeImage
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.Tesselator
 *  com.mojang.logging.LogUtils
 *  net.minecraft.Util
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.AbstractSelectionList$Entry
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.components.EditBox
 *  net.minecraft.client.gui.components.ObjectSelectionList$Entry
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.narration.NarratableEntry$NarrationPriority
 *  net.minecraft.client.gui.narration.NarrationElementOutput
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.renderer.texture.DynamicTexture
 *  net.minecraft.client.renderer.texture.TextureManager
 *  net.minecraft.locale.Language
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.network.chat.Style
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.packs.resources.IoSupplier
 *  net.minecraft.util.FormattedCharSequence
 *  net.minecraft.util.StringUtil
 *  net.minecraftforge.fml.ModContainer
 *  net.minecraftforge.fml.ModList
 *  net.minecraftforge.fml.VersionChecker
 *  net.minecraftforge.fml.VersionChecker$CheckResult
 *  net.minecraftforge.fml.loading.FMLPaths
 *  net.minecraftforge.fml.loading.StringUtils
 *  net.minecraftforge.fml.loading.moddiscovery.ModFileInfo
 *  net.minecraftforge.forgespi.language.IModInfo
 *  org.apache.commons.lang3.tuple.Pair
 *  org.slf4j.Logger
 */
package net.minecraftforge.client.gui;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.StringUtil;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.gui.widget.ModListWidget;
import net.minecraftforge.client.gui.widget.ScrollPanel;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.ForgeI18n;
import net.minecraftforge.common.util.MavenVersionStringHelper;
import net.minecraftforge.common.util.Size2i;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.loading.StringUtils;
import net.minecraftforge.fml.loading.moddiscovery.ModFileInfo;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.resource.PathPackResources;
import net.minecraftforge.resource.ResourcePackLoader;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;

public class ModListScreen
extends Screen {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int PADDING = 6;
    private static final int BUTTON_MARGIN = 1;
    private static final int NUM_BUTTONS = SortType.values().length;
    private final Screen parentScreen;
    private ModListWidget modList;
    private InfoPanel modInfo;
    private ModListWidget.ModEntry selected = null;
    private int listWidth;
    private List<IModInfo> mods;
    private final List<IModInfo> unsortedMods;
    private Button configButton;
    private Button openModsFolderButton;
    private Button doneButton;
    private String lastFilterText = "";
    private EditBox search;
    private boolean sorted = false;
    private SortType sortType = SortType.NORMAL;

    private static String stripControlCodes(String value) {
        return StringUtil.m_14406_((String)value);
    }

    public ModListScreen(Screen parentScreen) {
        super((Component)Component.m_237115_((String)"fml.menu.mods.title"));
        this.parentScreen = parentScreen;
        this.mods = ModList.get().getMods();
        this.unsortedMods = List.copyOf(this.mods);
    }

    public void m_7856_() {
        for (IModInfo mod : this.mods) {
            this.listWidth = Math.max(this.listWidth, this.getFontRenderer().m_92895_(mod.getDisplayName()) + 10);
            this.listWidth = Math.max(this.listWidth, this.getFontRenderer().m_92895_(MavenVersionStringHelper.artifactVersionToString(mod.getVersion())) + 5);
        }
        this.listWidth = Math.max(Math.min(this.listWidth, this.f_96543_ / 3), 100);
        this.listWidth += this.listWidth % NUM_BUTTONS != 0 ? NUM_BUTTONS - this.listWidth % NUM_BUTTONS : 0;
        int modInfoWidth = this.f_96543_ - this.listWidth - 18;
        int doneButtonWidth = Math.min(modInfoWidth, 200);
        int y = this.f_96544_ - 20 - 6;
        int fullButtonHeight = 32;
        this.doneButton = Button.m_253074_((Component)Component.m_237115_((String)"gui.done"), b -> this.m_7379_()).m_252987_((this.listWidth + 6 + this.f_96543_ - doneButtonWidth) / 2, y, doneButtonWidth, 20).m_253136_();
        this.openModsFolderButton = Button.m_253074_((Component)Component.m_237115_((String)"fml.menu.mods.openmodsfolder"), b -> Util.m_137581_().m_137644_(FMLPaths.MODSDIR.get().toFile())).m_252987_(6, y, this.listWidth, 20).m_253136_();
        this.configButton = Button.m_253074_((Component)Component.m_237115_((String)"fml.menu.mods.config"), b -> this.displayModConfig()).m_252987_(6, y -= 26, this.listWidth, 20).m_253136_();
        this.search = new EditBox(this.getFontRenderer(), 7, y -= 20, this.listWidth - 2, 14, (Component)Component.m_237115_((String)"fml.menu.mods.search"));
        int n = this.search.m_252907_();
        Objects.requireNonNull(this.getFontRenderer());
        this.modList = new ModListWidget(this, this.listWidth, fullButtonHeight, n - 9 - 6);
        this.modList.m_93507_(6);
        this.modInfo = new InfoPanel(this.f_96541_, modInfoWidth, this.f_96544_ - 6 - fullButtonHeight, 6);
        this.m_142416_((GuiEventListener)this.modList);
        this.m_142416_((GuiEventListener)this.modInfo);
        this.m_142416_((GuiEventListener)this.search);
        this.m_142416_((GuiEventListener)this.doneButton);
        this.m_142416_((GuiEventListener)this.configButton);
        this.m_142416_((GuiEventListener)this.openModsFolderButton);
        this.search.m_93692_(false);
        this.search.m_94190_(true);
        this.configButton.f_93623_ = false;
        int width = this.listWidth / NUM_BUTTONS;
        int x = 6;
        SortType.NORMAL.button = Button.m_253074_((Component)SortType.NORMAL.getButtonText(), b -> this.resortMods(SortType.NORMAL)).m_252987_(x, 6, width - 1, 20).m_253136_();
        this.m_142416_((GuiEventListener)SortType.NORMAL.button);
        SortType.A_TO_Z.button = Button.m_253074_((Component)SortType.A_TO_Z.getButtonText(), b -> this.resortMods(SortType.A_TO_Z)).m_252987_(x += width + 1, 6, width - 1, 20).m_253136_();
        this.m_142416_((GuiEventListener)SortType.A_TO_Z.button);
        SortType.Z_TO_A.button = Button.m_253074_((Component)SortType.Z_TO_A.getButtonText(), b -> this.resortMods(SortType.Z_TO_A)).m_252987_(x += width + 1, 6, width - 1, 20).m_253136_();
        this.m_142416_((GuiEventListener)SortType.Z_TO_A.button);
        this.resortMods(SortType.NORMAL);
        this.updateCache();
    }

    private void displayModConfig() {
        if (this.selected == null) {
            return;
        }
        try {
            ConfigScreenHandler.getScreenFactoryFor(this.selected.getInfo()).map(f -> (Screen)f.apply(this.f_96541_, this)).ifPresent(newScreen -> this.f_96541_.m_91152_(newScreen));
        }
        catch (Exception e) {
            LOGGER.error("There was a critical issue trying to build the config GUI for {}", (Object)this.selected.getInfo().getModId(), (Object)e);
        }
    }

    public void m_86600_() {
        this.search.m_94120_();
        this.modList.m_6987_((AbstractSelectionList.Entry)this.selected);
        if (!this.search.m_94155_().equals(this.lastFilterText)) {
            this.reloadMods();
            this.sorted = false;
        }
        if (!this.sorted) {
            this.reloadMods();
            this.mods.sort(this.sortType);
            this.modList.refreshList();
            if (this.selected != null) {
                this.selected = this.modList.m_6702_().stream().filter(e -> e.getInfo() == this.selected.getInfo()).findFirst().orElse(null);
                this.updateCache();
            }
            this.sorted = true;
        }
    }

    public <T extends ObjectSelectionList.Entry<T>> void buildModList(Consumer<T> modListViewConsumer, Function<IModInfo, T> newEntry) {
        for (IModInfo mod : this.mods) {
            modListViewConsumer.accept((ObjectSelectionList.Entry)newEntry.apply(mod));
        }
    }

    private void reloadMods() {
        this.mods = this.unsortedMods.stream().filter(mi -> StringUtils.toLowerCase((String)ModListScreen.stripControlCodes(mi.getDisplayName())).contains(StringUtils.toLowerCase((String)this.search.m_94155_()))).collect(Collectors.toList());
        this.lastFilterText = this.search.m_94155_();
    }

    private void resortMods(SortType newSort) {
        this.sortType = newSort;
        for (SortType sort : SortType.values()) {
            if (sort.button == null) continue;
            sort.button.f_93623_ = this.sortType != sort;
        }
        this.sorted = false;
    }

    public void m_88315_(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.modList.m_88315_(guiGraphics, mouseX, mouseY, partialTick);
        if (this.modInfo != null) {
            this.modInfo.m_88315_(guiGraphics, mouseX, mouseY, partialTick);
        }
        MutableComponent text = Component.m_237115_((String)"fml.menu.mods.search");
        int x = this.modList.getLeft() + (this.modList.getRight() - this.modList.getLeft()) / 2 - this.getFontRenderer().m_92852_((FormattedText)text) / 2;
        this.search.m_88315_(guiGraphics, mouseX, mouseY, partialTick);
        super.m_88315_(guiGraphics, mouseX, mouseY, partialTick);
        Font font = this.getFontRenderer();
        FormattedCharSequence formattedCharSequence = text.m_7532_();
        int n = this.search.m_252907_();
        Objects.requireNonNull(this.getFontRenderer());
        guiGraphics.m_280649_(font, formattedCharSequence, x, n - 9, 0xFFFFFF, false);
    }

    public Minecraft getMinecraftInstance() {
        return this.f_96541_;
    }

    public Font getFontRenderer() {
        return this.f_96547_;
    }

    public void setSelected(ModListWidget.ModEntry entry) {
        this.selected = entry == this.selected ? null : entry;
        this.updateCache();
    }

    private void updateCache() {
        if (this.selected == null) {
            this.configButton.f_93623_ = false;
            this.modInfo.clearInfo();
            return;
        }
        final IModInfo selectedMod = this.selected.getInfo();
        this.configButton.f_93623_ = ConfigScreenHandler.getScreenFactoryFor(selectedMod).isPresent();
        ArrayList<String> lines = new ArrayList<String>();
        VersionChecker.CheckResult vercheck = VersionChecker.getResult((IModInfo)selectedMod);
        Pair logoData = selectedMod.getLogoFile().map(logoFile -> {
            TextureManager tm = this.f_96541_.m_91097_();
            PathPackResources resourcePack = ResourcePackLoader.getPackFor(selectedMod.getModId()).orElse(ResourcePackLoader.getPackFor("forge").orElseThrow(() -> new RuntimeException("Can't find forge, WHAT!")));
            try {
                NativeImage logo = null;
                IoSupplier<InputStream> logoResource = resourcePack.m_8017_((String)logoFile);
                if (logoResource != null) {
                    logo = NativeImage.m_85058_((InputStream)((InputStream)logoResource.m_247737_()));
                }
                if (logo != null) {
                    return Pair.of((Object)tm.m_118490_("modlogo", new DynamicTexture(logo){

                        public void m_117985_() {
                            this.m_117966_();
                            NativeImage td = this.m_117991_();
                            this.m_117991_().m_85013_(0, 0, 0, 0, 0, td.m_84982_(), td.m_85084_(), selectedMod.getLogoBlur(), false, false, false);
                        }
                    }), (Object)new Size2i(logo.m_84982_(), logo.m_85084_()));
                }
            }
            catch (IOException iOException) {
                // empty catch block
            }
            return Pair.of(null, (Object)new Size2i(0, 0));
        }).orElse(Pair.of(null, (Object)new Size2i(0, 0)));
        lines.add(selectedMod.getDisplayName());
        lines.add(ForgeI18n.parseMessage("fml.menu.mods.info.version", MavenVersionStringHelper.artifactVersionToString(selectedMod.getVersion())));
        lines.add(ForgeI18n.parseMessage("fml.menu.mods.info.idstate", selectedMod.getModId(), ModList.get().getModContainerById(selectedMod.getModId()).map(ModContainer::getCurrentState).map(Object::toString).orElse("NONE")));
        selectedMod.getConfig().getConfigElement(new String[]{"credits"}).ifPresent(credits -> lines.add(ForgeI18n.parseMessage("fml.menu.mods.info.credits", credits)));
        selectedMod.getConfig().getConfigElement(new String[]{"authors"}).ifPresent(authors -> lines.add(ForgeI18n.parseMessage("fml.menu.mods.info.authors", authors)));
        selectedMod.getConfig().getConfigElement(new String[]{"displayURL"}).ifPresent(displayURL -> lines.add(ForgeI18n.parseMessage("fml.menu.mods.info.displayurl", displayURL)));
        if (selectedMod.getOwningFile() == null || selectedMod.getOwningFile().getMods().size() == 1) {
            lines.add(ForgeI18n.parseMessage("fml.menu.mods.info.nochildmods", new Object[0]));
        } else {
            lines.add(ForgeI18n.parseMessage("fml.menu.mods.info.childmods", selectedMod.getOwningFile().getMods().stream().map(IModInfo::getDisplayName).collect(Collectors.joining(","))));
        }
        if (vercheck.status().isOutdated()) {
            lines.add(ForgeI18n.parseMessage("fml.menu.mods.info.updateavailable", vercheck.url() == null ? "" : vercheck.url()));
        }
        lines.add(ForgeI18n.parseMessage("fml.menu.mods.info.license", ((ModFileInfo)selectedMod.getOwningFile()).getLicense()));
        lines.add(null);
        lines.add(selectedMod.getDescription());
        if (vercheck.status().isOutdated() && !vercheck.changes().isEmpty()) {
            lines.add(null);
            lines.add(ForgeI18n.parseMessage("fml.menu.mods.info.changelogheader", new Object[0]));
            for (Map.Entry entry : vercheck.changes().entrySet()) {
                lines.add("  " + String.valueOf(entry.getKey()) + ":");
                lines.add((String)entry.getValue());
                lines.add(null);
            }
        }
        this.modInfo.setInfo(lines, (ResourceLocation)logoData.getLeft(), (Size2i)logoData.getRight());
    }

    public void m_6574_(Minecraft mc, int width, int height) {
        String s = this.search.m_94155_();
        SortType sort = this.sortType;
        ModListWidget.ModEntry selected = this.selected;
        this.m_6575_(mc, width, height);
        this.search.m_94144_(s);
        this.selected = selected;
        if (!this.search.m_94155_().isEmpty()) {
            this.reloadMods();
        }
        if (sort != SortType.NORMAL) {
            this.resortMods(sort);
        }
        this.updateCache();
    }

    public void m_7379_() {
        this.f_96541_.m_91152_(this.parentScreen);
    }

    /*
     * Uses 'sealed' constructs - enablewith --sealed true
     */
    private static enum SortType implements Comparator<IModInfo>
    {
        NORMAL,
        A_TO_Z{

            @Override
            protected int compare(String name1, String name2) {
                return name1.compareTo(name2);
            }
        }
        ,
        Z_TO_A{

            @Override
            protected int compare(String name1, String name2) {
                return name2.compareTo(name1);
            }
        };

        Button button;

        @Override
        protected int compare(String name1, String name2) {
            return 0;
        }

        @Override
        public int compare(IModInfo o1, IModInfo o2) {
            String name1 = StringUtils.toLowerCase((String)ModListScreen.stripControlCodes(o1.getDisplayName()));
            String name2 = StringUtils.toLowerCase((String)ModListScreen.stripControlCodes(o2.getDisplayName()));
            return this.compare(name1, name2);
        }

        Component getButtonText() {
            return Component.m_237115_((String)("fml.menu.mods." + StringUtils.toLowerCase((String)this.name())));
        }
    }

    class InfoPanel
    extends ScrollPanel {
        private ResourceLocation logoPath;
        private Size2i logoDims;
        private List<FormattedCharSequence> lines;

        InfoPanel(Minecraft mcIn, int widthIn, int heightIn, int topIn) {
            super(mcIn, widthIn, heightIn, topIn, ModListScreen.this.modList.getRight() + 6);
            this.logoDims = new Size2i(0, 0);
            this.lines = Collections.emptyList();
        }

        void setInfo(List<String> lines, ResourceLocation logoPath, Size2i logoDims) {
            this.logoPath = logoPath;
            this.logoDims = logoDims;
            this.lines = this.resizeContent(lines);
        }

        void clearInfo() {
            this.logoPath = null;
            this.logoDims = new Size2i(0, 0);
            this.lines = Collections.emptyList();
        }

        private List<FormattedCharSequence> resizeContent(List<String> lines) {
            ArrayList<FormattedCharSequence> ret = new ArrayList<FormattedCharSequence>();
            for (String line : lines) {
                if (line == null) {
                    ret.add(null);
                    continue;
                }
                Component chat = ForgeHooks.newChatWithLinks(line, false);
                int maxTextLength = this.width - 12;
                if (maxTextLength < 0) continue;
                ret.addAll(Language.m_128107_().m_128112_(ModListScreen.this.f_96547_.m_92865_().m_92414_((FormattedText)chat, maxTextLength, Style.f_131099_)));
            }
            return ret;
        }

        @Override
        public int getContentHeight() {
            int height = 50;
            int n = this.lines.size();
            Objects.requireNonNull(ModListScreen.this.f_96547_);
            if ((height += n * 9) < this.bottom - this.top - 8) {
                height = this.bottom - this.top - 8;
            }
            return height;
        }

        @Override
        protected int getScrollAmount() {
            Objects.requireNonNull(ModListScreen.this.f_96547_);
            return 9 * 3;
        }

        @Override
        protected void drawPanel(GuiGraphics guiGraphics, int entryRight, int relativeY, Tesselator tess, int mouseX, int mouseY) {
            if (this.logoPath != null) {
                RenderSystem.enableBlend();
                RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
                int headerHeight = 50;
                guiGraphics.blitInscribed(this.logoPath, this.left + 6, relativeY, this.width - 12, headerHeight, this.logoDims.width, this.logoDims.height, false, true);
                relativeY += headerHeight + 6;
            }
            for (FormattedCharSequence line : this.lines) {
                if (line != null) {
                    RenderSystem.enableBlend();
                    guiGraphics.m_280648_(ModListScreen.this.f_96547_, line, this.left + 6, relativeY, 0xFFFFFF);
                    RenderSystem.disableBlend();
                }
                Objects.requireNonNull(ModListScreen.this.f_96547_);
                relativeY += 9;
            }
            Style component = this.findTextLine(mouseX, mouseY);
            if (component != null) {
                guiGraphics.m_280304_(ModListScreen.this.f_96547_, component, mouseX, mouseY);
            }
        }

        private Style findTextLine(int mouseX, int mouseY) {
            if (!this.m_5953_(mouseX, mouseY)) {
                return null;
            }
            double offset = (float)(mouseY - this.top - 6 - this.border) + this.scrollDistance;
            if (this.logoPath != null) {
                offset -= 50.0;
            }
            if (offset <= 0.0) {
                return null;
            }
            Objects.requireNonNull(ModListScreen.this.f_96547_);
            int lineIdx = (int)(offset / 9.0);
            if (lineIdx >= this.lines.size() || lineIdx < 0) {
                return null;
            }
            FormattedCharSequence line = this.lines.get(lineIdx);
            if (line != null) {
                return ModListScreen.this.f_96547_.m_92865_().m_92338_(line, mouseX - this.left - this.border);
            }
            return null;
        }

        @Override
        public boolean m_6375_(double mouseX, double mouseY, int button) {
            Style component = this.findTextLine((int)mouseX, (int)mouseY);
            if (component != null) {
                ModListScreen.this.m_5561_(component);
                return true;
            }
            return super.m_6375_(mouseX, mouseY, button);
        }

        public NarratableEntry.NarrationPriority m_142684_() {
            return NarratableEntry.NarrationPriority.NONE;
        }

        public void m_142291_(NarrationElementOutput p_169152_) {
        }
    }
}

