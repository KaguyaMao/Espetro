/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.Util
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.AbstractSelectionList$Entry
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.components.ObjectSelectionList
 *  net.minecraft.client.gui.components.ObjectSelectionList$Entry
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.navigation.CommonInputs
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.ClickEvent
 *  net.minecraft.network.chat.ClickEvent$Action
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.Style
 *  net.minecraft.util.FormattedCharSequence
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.api.distmarker.OnlyIn
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.client;

import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.script.ConsoleLine;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.kubejs.util.LogType;
import dev.latvian.mods.kubejs.util.UtilsJS;
import java.awt.Desktop;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.navigation.CommonInputs;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

public class KubeJSErrorScreen
extends Screen {
    public final Screen lastScreen;
    public final ScriptType scriptType;
    public final Path logFile;
    public final List<ConsoleLine> errors;
    public final List<ConsoleLine> warnings;
    public List<ConsoleLine> viewing;
    private ErrorList list;

    public KubeJSErrorScreen(Screen lastScreen, ScriptType scriptType, @Nullable Path logFile, List<ConsoleLine> errors, List<ConsoleLine> warnings) {
        super((Component)Component.m_237119_());
        this.lastScreen = lastScreen;
        this.scriptType = scriptType;
        this.logFile = logFile;
        this.errors = errors;
        this.warnings = warnings;
        this.viewing = errors.isEmpty() && !warnings.isEmpty() ? warnings : errors;
    }

    public KubeJSErrorScreen(Screen lastScreen, ConsoleJS console) {
        this(lastScreen, console.scriptType, console.scriptType.getLogFile(), new ArrayList<ConsoleLine>(console.errors), new ArrayList<ConsoleLine>(console.warnings));
    }

    public Component m_142562_() {
        return Component.m_237113_((String)("There were KubeJS " + this.scriptType.name + " errors!"));
    }

    protected void m_7856_() {
        super.m_7856_();
        this.list = new ErrorList(this, this.f_96541_, this.f_96543_, this.f_96544_, 32, this.f_96544_ - 32, this.viewing);
        this.m_7787_((GuiEventListener)this.list);
        int i = this.f_96544_ - 26;
        if (CommonProperties.get().startupErrorReportUrl.isBlank()) {
            openLog = (Button)this.m_142416_((GuiEventListener)Button.m_253074_((Component)Component.m_237113_((String)"Open Log File"), this::openLog).m_252987_(this.f_96543_ / 2 - 155, i, 150, 20).m_253136_());
            this.m_142416_((GuiEventListener)Button.m_253074_((Component)Component.m_237113_((String)(this.scriptType.isStartup() ? "Quit" : "Close")), this::quit).m_252987_(this.f_96543_ / 2 - 155 + 160, i, 150, 20).m_253136_());
        } else {
            openLog = (Button)this.m_142416_((GuiEventListener)Button.m_253074_((Component)Component.m_237113_((String)"Open Log File"), this::openLog).m_252987_(this.f_96543_ / 4 - 55, i, 100, 20).m_253136_());
            this.m_142416_((GuiEventListener)Button.m_253074_((Component)Component.m_237113_((String)"Report"), this::report).m_252987_(this.f_96543_ / 2 - 50, i, 100, 20).m_253136_());
            this.m_142416_((GuiEventListener)Button.m_253074_((Component)Component.m_237113_((String)(this.scriptType.isStartup() ? "Quit" : "Close")), this::quit).m_252987_(this.f_96543_ * 3 / 4 - 45, i, 100, 20).m_253136_());
        }
        openLog.f_93623_ = this.logFile != null;
        Button viewOther = (Button)this.m_142416_((GuiEventListener)Button.m_253074_((Component)Component.m_237113_((String)(this.viewing == this.errors ? "View Warnings [" + this.warnings.size() + "]" : "View Errors [" + this.errors.size() + "]")), this::viewOther).m_252987_(this.f_96543_ - 107, 7, 100, 20).m_253136_());
        if (this.errors.isEmpty() || this.warnings.isEmpty()) {
            viewOther.f_93623_ = false;
        }
    }

    private void quit(Button button) {
        if (this.scriptType.isStartup()) {
            this.f_96541_.m_91395_();
        } else {
            this.m_7379_();
        }
    }

    private void report(Button button) {
        this.m_5561_(Style.f_131099_.m_131142_(new ClickEvent(ClickEvent.Action.OPEN_URL, CommonProperties.get().startupErrorReportUrl)));
    }

    private void openLog(Button button) {
        if (this.logFile != null) {
            this.m_5561_(Style.f_131099_.m_131142_(new ClickEvent(ClickEvent.Action.OPEN_FILE, this.logFile.toAbsolutePath().toString())));
        }
    }

    private void viewOther(Button button) {
        this.viewing = this.viewing == this.errors ? this.warnings : this.errors;
        this.m_267719_();
    }

    public void m_88315_(GuiGraphics guiGraphics, int mx, int my, float delta) {
        this.m_280273_(guiGraphics);
        this.list.m_88315_(guiGraphics, mx, my, delta);
        guiGraphics.m_280137_(this.f_96547_, "KubeJS " + this.scriptType.name + " script " + (this.viewing == this.errors ? "errors" : "warnings"), this.f_96543_ / 2, 12, 0xFFFFFF);
        super.m_88315_(guiGraphics, mx, my, delta);
    }

    public boolean m_6913_() {
        return !this.scriptType.isStartup();
    }

    public void m_7379_() {
        this.f_96541_.m_91152_(this.lastScreen);
    }

    public static class ErrorList
    extends ObjectSelectionList<Entry> {
        public final KubeJSErrorScreen screen;
        public final List<ConsoleLine> lines;

        public ErrorList(KubeJSErrorScreen screen, Minecraft minecraft, int x1, int height, int y0, int y1, List<ConsoleLine> lines) {
            super(minecraft, x1, height, y0, y1, 48);
            this.screen = screen;
            this.lines = lines;
            this.m_93488_(false);
            Calendar calendar = Calendar.getInstance();
            for (int i = 0; i < lines.size(); ++i) {
                this.m_7085_((AbstractSelectionList.Entry)new Entry(this, minecraft, i, lines.get(i), calendar));
            }
        }

        public boolean m_7933_(int i, int j, int k) {
            Entry sel;
            if (CommonInputs.m_278691_((int)i) && (sel = (Entry)this.m_93511_()) != null) {
                sel.open();
                return true;
            }
            return super.m_7933_(i, j, k);
        }

        public int m_5759_() {
            return (int)((double)this.f_93388_ * 0.93);
        }
    }

    @OnlyIn(value=Dist.CLIENT)
    public static class Entry
    extends ObjectSelectionList.Entry<Entry> {
        private final ErrorList errorList;
        private final Minecraft minecraft;
        private final ConsoleLine line;
        private long lastClickTime;
        private final FormattedCharSequence indexText;
        private final FormattedCharSequence scriptLineText;
        private final FormattedCharSequence timestampText;
        private final List<FormattedCharSequence> errorText;
        private final List<FormattedCharSequence> stackTraceText;

        public Entry(ErrorList errorList, Minecraft minecraft, int index, ConsoleLine line, Calendar calendar) {
            this.errorList = errorList;
            this.minecraft = minecraft;
            this.line = line;
            this.indexText = Component.m_237113_((String)("#" + (index + 1))).m_7532_();
            ArrayList<ConsoleLine.SourceLine> sourceLines = new ArrayList<ConsoleLine.SourceLine>(line.sourceLines);
            Collections.reverse(sourceLines);
            this.scriptLineText = Component.m_237113_((String)sourceLines.stream().map(Object::toString).map(s -> s.isEmpty() ? (this.line.type == LogType.WARN ? "Internal Warning" : "Internal Error") : s).collect(Collectors.joining(" -> "))).m_7532_();
            StringBuilder sb = new StringBuilder();
            calendar.setTimeInMillis(line.timestamp);
            UtilsJS.appendTimestamp(sb, calendar);
            this.timestampText = Component.m_237113_((String)sb.toString()).m_7532_();
            this.errorText = new ArrayList<FormattedCharSequence>(minecraft.f_91062_.m_92923_((FormattedText)Component.m_237113_((String)line.message), errorList.m_5759_()).stream().limit(3L).toList());
            this.stackTraceText = line.stackTrace.isEmpty() ? List.of() : minecraft.f_91062_.m_92923_((FormattedText)Component.m_237113_((String)String.join((CharSequence)"\n", line.stackTrace)).m_6270_(Style.f_131099_.m_131140_(ChatFormatting.GRAY)), Integer.MAX_VALUE);
        }

        public Component m_142172_() {
            return Component.m_237119_();
        }

        public void m_6311_(GuiGraphics g, int idx, int y, int x, int w, int h, int mx, int my, boolean hovered, float delta) {
            int col = this.line.type == LogType.ERROR ? 16735075 : 0xFFBB5B;
            g.m_280648_(this.minecraft.f_91062_, this.indexText, x + 1, y + 1, col);
            g.m_280364_(this.minecraft.f_91062_, this.scriptLineText, x + w / 2, y + 1, 0xFFFFFF);
            g.m_280648_(this.minecraft.f_91062_, this.timestampText, x + w - this.minecraft.f_91062_.m_92724_(this.timestampText) - 4, y + 1, 0x666666);
            for (int i = 0; i < this.errorText.size(); ++i) {
                g.m_280648_(this.minecraft.f_91062_, this.errorText.get(i), x + 1, y + 13 + i * 10, col);
            }
            if (hovered && !this.stackTraceText.isEmpty()) {
                this.errorList.screen.m_257959_(Screen.m_96638_() ? this.stackTraceText : this.stackTraceText.stream().limit(4L).toList());
            }
        }

        public boolean m_6375_(double d, double e, int i) {
            this.errorList.m_6987_((AbstractSelectionList.Entry)this);
            if (Util.m_137550_() - this.lastClickTime < 250L) {
                if (i == 1) {
                    this.minecraft.f_91068_.m_90911_(String.join((CharSequence)"\n", this.line.stackTrace));
                } else {
                    this.open();
                }
                return true;
            }
            this.lastClickTime = Util.m_137550_();
            return true;
        }

        public void open() {
            Path path;
            Object object = this.line.externalFile == null ? (this.line.sourceLines.isEmpty() || this.line.sourceLines.iterator().next().source().isEmpty() ? null : this.line.console.scriptType.path.resolve(this.line.sourceLines.iterator().next().source())) : (path = this.line.externalFile);
            if (path != null && Files.exists(path, new LinkOption[0])) {
                try {
                    if (!Desktop.isDesktopSupported() || !Desktop.getDesktop().isSupported(Desktop.Action.BROWSE_FILE_DIR)) {
                        throw new IllegalStateException("Error");
                    }
                    Desktop.getDesktop().browseFileDirectory(path.toFile());
                }
                catch (Exception ignored) {
                    if (Files.isRegularFile(path, new LinkOption[0]) && !path.getFileName().toString().endsWith(".js")) {
                        path = path.getParent();
                    }
                    this.errorList.screen.m_5561_(Style.f_131099_.m_131142_(new ClickEvent(ClickEvent.Action.OPEN_FILE, path.toAbsolutePath().toString())));
                }
            }
        }
    }
}

