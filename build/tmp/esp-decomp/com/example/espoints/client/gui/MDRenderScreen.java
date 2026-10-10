/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.util.FormattedCharSequence
 *  org.espetro.client.aui.GuiElement
 */
package com.example.espoints.client.gui;

import com.example.espoints.client.gui.EspetroMenuScreen;
import com.example.espoints.client.gui.HcrAuiWidgets;
import com.example.espoints.client.gui.ScrollableList;
import com.example.espoints.util.ModLogger;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;
import org.espetro.client.aui.GuiElement;

public class MDRenderScreen
extends EspetroMenuScreen {
    private static final String MD_FOLDER_NAME = "HCRmdread";
    private static final int MARGIN = 18;
    private static final int HEADER_H = 48;
    private static final int LINE_HEIGHT = 15;
    private static final int FILE_ROW_H = 22;
    private static final int TOC_ROW_H = 18;
    private static final float LEFT_SIDE_RATIO = 0.32f;
    private ScreenState currentState = ScreenState.FILE_LIST;
    private File currentFile;
    private List<File> mdFiles = new ArrayList<File>();
    private List<FormattedCharSequence> fileContentLines = new ArrayList<FormattedCharSequence>();
    private List<MdTitle> mdTitles = new ArrayList<MdTitle>();
    private int contentScrollOffset = 0;
    private int lastWidth;
    private int lastHeight;

    public MDRenderScreen() {
        super((Component)Component.m_237115_((String)"gui.espoints.md_reader.title"));
    }

    protected void m_7856_() {
        this.loadMDFiles();
        super.m_7856_();
    }

    public void m_6574_(Minecraft minecraft, int width, int height) {
        super.m_6574_(minecraft, width, height);
        if (this.currentState == ScreenState.FILE_CONTENT && this.currentFile != null) {
            this.loadFileContent(this.currentFile, this.getContentWidth(width));
        }
    }

    public void m_86600_() {
        super.m_86600_();
        if (this.f_96543_ != this.lastWidth || this.f_96544_ != this.lastHeight) {
            this.rebuildMenuRoot();
        }
    }

    @Override
    protected void buildMenuRoot(GuiElement root) {
        this.lastWidth = this.f_96543_;
        this.lastHeight = this.f_96544_;
        int pageX = 18;
        int pageY = 18;
        int pageW = Math.max(360, this.f_96543_ - 36);
        int pageH = Math.max(220, this.f_96544_ - 36);
        int leftW = Math.max(150, Math.min(260, (int)((float)pageW * 0.32f)));
        int rightX = pageX + leftW + 12;
        int rightW = Math.max(120, pageW - leftW - 30);
        root.addChild((GuiElement)HcrAuiWidgets.panel(pageX, pageY, pageW, pageH, -535225314, -1604623776));
        this.buildHeader(root, pageX, pageY, pageW);
        int contentY = pageY + 48 + 12;
        int contentH = pageY + pageH - contentY - 18;
        root.addChild((GuiElement)HcrAuiWidgets.panel(pageX + 18, contentY, leftW, contentH, -1340005081, -1604623776));
        root.addChild((GuiElement)HcrAuiWidgets.panel(rightX, contentY, rightW, contentH, -1340005081, -1604623776));
        if (this.currentState == ScreenState.FILE_LIST) {
            this.buildFileList(root, pageX + 18, contentY, leftW, contentH, rightX, rightW);
        } else {
            this.buildFileContent(root, pageX + 18, contentY, leftW, contentH, rightX, rightW, contentH);
        }
    }

    private void buildHeader(GuiElement root, int pageX, int pageY, int pageW) {
        String subtitle = this.currentFile == null ? "\u8bfb\u53d6 HCRmdread \u6587\u4ef6\u5939\u4e2d\u7684 Markdown \u6587\u6863" : this.currentFile.getName();
        root.addChild((GuiElement)HcrAuiWidgets.text(pageX + 18, pageY + 13, "MD \u6587\u4ef6\u9605\u8bfb\u5668", -790040));
        root.addChild((GuiElement)HcrAuiWidgets.text(pageX + 18, pageY + 29, HcrAuiWidgets.trimToWidth(subtitle, Math.max(60, pageW - 150)), -5722440));
        if (this.currentState == ScreenState.FILE_CONTENT) {
            root.addChild((GuiElement)HcrAuiWidgets.button(pageX + pageW - 126, pageY + 14, 50, 18, "\u5217\u8868", () -> {
                this.currentState = ScreenState.FILE_LIST;
                this.currentFile = null;
                this.rebuildMenuRoot();
            }).setTextColor(-14490));
        }
        root.addChild((GuiElement)HcrAuiWidgets.button(pageX + pageW - 68, pageY + 14, 50, 18, "\u5173\u95ed", () -> ((MDRenderScreen)this).m_7379_()).setTextColor(-5722440));
        root.addChild((GuiElement)HcrAuiWidgets.rect(pageX + 18, pageY + 48, pageW - 36, 1, 0x35FFFFFF));
    }

    private void buildFileList(GuiElement root, int leftX, int top, int leftW, int height, int rightX, int rightW) {
        root.addChild((GuiElement)HcrAuiWidgets.text(leftX + 12, top + 10, "\u6587\u4ef6\u5217\u8868", -14490));
        root.addChild((GuiElement)HcrAuiWidgets.text(leftX + 12, top + 26, "\u5171 " + this.mdFiles.size() + " \u4e2a\u6587\u4ef6", -5722440));
        ScrollableList list = new ScrollableList(leftX + 8, top + 46, leftW - 16, Math.max(30, height - 54)).setScrollStep(22).setAlwaysShowScrollbar(true);
        root.addChild((GuiElement)list);
        if (this.mdFiles.isEmpty()) {
            list.addChild(HcrAuiWidgets.textBlock(4, 4, leftW - 32, "\u672a\u627e\u5230 .md \u6587\u4ef6\u3002", -5722440));
        } else {
            int y = 0;
            for (File file : this.mdFiles) {
                list.addChild(HcrAuiWidgets.button(0, y, Math.max(40, leftW - 28), 18, HcrAuiWidgets.trimToWidth(file.getName(), Math.max(32, leftW - 42)), () -> this.openFile(file)).setTextColor(-790040));
                y += 22;
            }
        }
        root.addChild((GuiElement)HcrAuiWidgets.centeredText(rightX, top + Math.max(20, height / 2 - 12), rightW, "\u9009\u62e9\u5de6\u4fa7\u6587\u4ef6\u5f00\u59cb\u9605\u8bfb", -5722440));
    }

    private void buildFileContent(GuiElement root, int leftX, int top, int leftW, int leftH, int rightX, int rightW, int rightH) {
        root.addChild((GuiElement)HcrAuiWidgets.text(leftX + 12, top + 10, "\u76ee\u5f55", -14490));
        ScrollableList toc = new ScrollableList(leftX + 8, top + 30, leftW - 16, Math.max(30, leftH - 38)).setScrollStep(18).setAlwaysShowScrollbar(true);
        root.addChild((GuiElement)toc);
        if (this.mdTitles.isEmpty()) {
            toc.addChild(HcrAuiWidgets.text(4, 4, "\u65e0\u6807\u9898", -5722440));
        } else {
            int y = 0;
            for (MdTitle title : this.mdTitles) {
                int indent = Math.min(30, Math.max(0, (title.level - 1) * 8));
                toc.addChild(HcrAuiWidgets.button(indent, y, Math.max(36, leftW - 28 - indent), 15, HcrAuiWidgets.trimToWidth(title.text, Math.max(24, leftW - 42 - indent)), () -> {
                    this.contentScrollOffset = title.lineIndex;
                }).setTextColor(title.level == 1 ? -14490 : -5722440));
                y += 18;
            }
        }
        root.addChild((GuiElement)new ContentPane(rightX + 10, top + 10, rightW - 20, rightH - 20));
    }

    private void openFile(File file) {
        this.currentFile = file;
        this.loadFileContent(file, this.getContentWidth(this.f_96543_));
        this.currentState = ScreenState.FILE_CONTENT;
        this.rebuildMenuRoot();
    }

    private int getContentWidth(int screenWidth) {
        int pageW = Math.max(360, screenWidth - 36);
        int leftW = Math.max(150, Math.min(260, (int)((float)pageW * 0.32f)));
        return Math.max(120, pageW - leftW - 30) - 28;
    }

    private void loadMDFiles() {
        File minecraftDir = Minecraft.m_91087_().f_91069_;
        Path mdFolderPath = Paths.get(minecraftDir.getAbsolutePath(), MD_FOLDER_NAME);
        File mdFolder = mdFolderPath.toFile();
        if (!mdFolder.exists() && !mdFolder.mkdirs()) {
            ModLogger.error("\u65e0\u6cd5\u521b\u5efaMD\u6587\u4ef6\u6587\u4ef6\u5939: " + String.valueOf(mdFolderPath));
            this.mdFiles = new ArrayList<File>();
            return;
        }
        File[] files = mdFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".md"));
        this.mdFiles = new ArrayList<File>();
        if (files != null) {
            for (File file : files) {
                if (!file.isFile()) continue;
                this.mdFiles.add(file);
            }
            this.mdFiles.sort(Comparator.comparing(File::getName));
        }
    }

    private void loadFileContent(File file, int maxContentWidth) {
        try {
            List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
            this.fileContentLines = new ArrayList<FormattedCharSequence>();
            this.mdTitles = new ArrayList<MdTitle>();
            for (String line : lines) {
                if (line.startsWith("#")) {
                    int level;
                    for (level = 0; level < line.length() && line.charAt(level) == '#'; ++level) {
                    }
                    String titleText = line.substring(level).trim();
                    String rendered = level == 1 ? "=== " + titleText + " ===" : (level == 2 ? "--- " + titleText + " ---" : "   ".repeat(Math.max(0, level - 3)) + "- " + titleText);
                    this.processAndAddText(rendered, maxContentWidth);
                    this.mdTitles.add(new MdTitle(titleText, level, Math.max(0, this.fileContentLines.size() - 1)));
                    continue;
                }
                this.processAndAddText(line, maxContentWidth);
            }
            this.contentScrollOffset = 0;
        }
        catch (IOException e) {
            ModLogger.error("\u8bfb\u53d6MD\u6587\u4ef6\u5931\u8d25: " + e.getMessage());
            this.fileContentLines = new ArrayList<FormattedCharSequence>();
            this.mdTitles = new ArrayList<MdTitle>();
            this.fileContentLines.add(Component.m_237113_((String)("\u65e0\u6cd5\u8bfb\u53d6\u6587\u4ef6\u5185\u5bb9: " + e.getMessage())).m_7532_());
        }
    }

    private void processAndAddText(String text, int maxWidth) {
        if (text.trim().isEmpty()) {
            this.fileContentLines.add(Component.m_237113_((String)"").m_7532_());
            return;
        }
        Font font = Minecraft.m_91087_().f_91062_;
        StringBuilder currentLine = new StringBuilder();
        int i = 0;
        while (i < text.length()) {
            int codePoint = text.codePointAt(i);
            int charCount = Character.charCount(codePoint);
            String currentChar = text.substring(i, i + charCount);
            String testLine = String.valueOf(currentLine) + currentChar;
            if (font.m_92852_((FormattedText)this.addColorToText(testLine)) <= maxWidth) {
                currentLine.append(currentChar);
                i += charCount;
                continue;
            }
            if (currentLine.length() > 0) {
                this.fileContentLines.add(this.addColorToText(currentLine.toString()).m_7532_());
                currentLine = new StringBuilder(currentChar);
            } else {
                this.fileContentLines.add(this.addColorToText(currentChar).m_7532_());
            }
            i += charCount;
        }
        if (currentLine.length() > 0) {
            this.fileContentLines.add(this.addColorToText(currentLine.toString()).m_7532_());
        }
    }

    private Component addColorToText(String text) {
        if (text.contains("\u8b66\u544a") || text.contains("Warning") || text.contains("WARN")) {
            return Component.m_237113_((String)text).m_130938_(style -> style.m_178520_(-22016));
        }
        if (text.contains("\u9519\u8bef") || text.contains("Error") || text.contains("ERR") || text.contains("ERROR")) {
            return Component.m_237113_((String)text).m_130938_(style -> style.m_178520_(-43691));
        }
        if (text.contains("\u63d0\u793a") || text.contains("Tip") || text.contains("\u6ce8\u610f") || text.contains("Note")) {
            return Component.m_237113_((String)text).m_130938_(style -> style.m_178520_(-11141121));
        }
        if (text.startsWith("- ") || text.startsWith("* ")) {
            return Component.m_237113_((String)text).m_130938_(style -> style.m_178520_(-5592406));
        }
        return Component.m_237113_((String)text).m_130938_(style -> style.m_178520_(-1));
    }

    public boolean m_6050_(double mouseX, double mouseY, double delta) {
        int pageW;
        int leftW;
        int rightX;
        if (this.currentState == ScreenState.FILE_CONTENT && this.root != null && mouseX >= (double)(rightX = 18 + (leftW = Math.max(150, Math.min(260, (int)((float)(pageW = Math.max(360, this.f_96543_ - 36)) * 0.32f)))) + 12)) {
            int visibleLines = this.getVisibleContentLines();
            int maxScroll = Math.max(0, this.fileContentLines.size() - visibleLines);
            this.contentScrollOffset = Math.max(0, Math.min(maxScroll, this.contentScrollOffset - (int)Math.signum(delta) * 3));
            return true;
        }
        return super.m_6050_(mouseX, mouseY, delta);
    }

    private int getVisibleContentLines() {
        int pageH = Math.max(220, this.f_96544_ - 36);
        int contentH = pageH - 48 - 30;
        return Math.max(1, contentH / 15);
    }

    public boolean m_7933_(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            if (this.currentState == ScreenState.FILE_CONTENT) {
                this.currentState = ScreenState.FILE_LIST;
                this.currentFile = null;
                this.rebuildMenuRoot();
                return true;
            }
            this.m_7379_();
            return true;
        }
        return super.m_7933_(keyCode, scanCode, modifiers);
    }

    public boolean m_7043_() {
        return false;
    }

    private static enum ScreenState {
        FILE_LIST,
        FILE_CONTENT;

    }

    private static class MdTitle {
        private final String text;
        private final int level;
        private final int lineIndex;

        private MdTitle(String text, int level, int lineIndex) {
            this.text = text;
            this.level = level;
            this.lineIndex = lineIndex;
        }
    }

    private class ContentPane
    extends GuiElement {
        ContentPane(int x, int y, int width, int height) {
            super(x, y, width, height);
        }

        public void draw(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY, float partialTick) {
            if (!this.isVisible()) {
                return;
            }
            int bx = x + this.getX();
            int by = y + this.getY();
            int visibleLines = Math.max(1, this.getHeight() / 15);
            int maxScroll = Math.max(0, MDRenderScreen.this.fileContentLines.size() - visibleLines);
            MDRenderScreen.this.contentScrollOffset = Math.max(0, Math.min(maxScroll, MDRenderScreen.this.contentScrollOffset));
            graphics.m_280588_(bx, by, bx + this.getWidth(), by + this.getHeight());
            int endIndex = Math.min(MDRenderScreen.this.contentScrollOffset + visibleLines, MDRenderScreen.this.fileContentLines.size());
            for (int i = MDRenderScreen.this.contentScrollOffset; i < endIndex; ++i) {
                int lineIndex = i - MDRenderScreen.this.contentScrollOffset;
                graphics.m_280648_(Minecraft.m_91087_().f_91062_, MDRenderScreen.this.fileContentLines.get(i), bx, by + lineIndex * 15, -1);
            }
            graphics.m_280618_();
            if (maxScroll > 0) {
                int trackX = bx + this.getWidth() - 3;
                graphics.m_280509_(trackX, by, trackX + 1, by + this.getHeight(), 0x35FFFFFF);
                int handleH = Math.max(12, (int)((double)visibleLines / (double)MDRenderScreen.this.fileContentLines.size() * (double)this.getHeight()));
                int travel = Math.max(0, this.getHeight() - handleH);
                int handleY = by + (int)((double)MDRenderScreen.this.contentScrollOffset / (double)maxScroll * (double)travel);
                graphics.m_280509_(trackX - 1, handleY, trackX + 3, handleY + handleH, -1325400065);
            }
            super.draw(graphics, x, y, width, height, mouseX, mouseY, partialTick);
        }
    }
}

