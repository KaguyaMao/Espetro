/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.FriendlyByteBuf
 */
package dev.latvian.mods.kubejs.script;

import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.kubejs.util.LogType;
import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.network.FriendlyByteBuf;

public class ConsoleLine {
    public static final ConsoleLine[] EMPTY_ARRAY = new ConsoleLine[0];
    public final ConsoleJS console;
    public final long timestamp;
    public String message;
    public LogType type = LogType.INFO;
    public String group = "";
    public Collection<SourceLine> sourceLines = Set.of();
    public Path externalFile = null;
    public List<String> stackTrace = List.of();
    private String cachedText;

    public ConsoleLine(ConsoleJS console, long timestamp, String message) {
        this.console = console;
        this.timestamp = timestamp;
        this.message = message;
    }

    public ConsoleLine(FriendlyByteBuf buf) {
        this.console = ScriptType.VALUES[buf.readByte()].console;
        this.timestamp = buf.readLong();
        this.message = buf.m_130277_();
        this.type = LogType.VALUES[buf.readByte()];
        this.group = "";
        this.sourceLines = buf.m_236845_(SourceLine::new);
        this.stackTrace = buf.m_236845_(FriendlyByteBuf::m_130277_);
    }

    public static void writeToNet(FriendlyByteBuf buf, ConsoleLine line) {
        buf.writeByte(line.console.scriptType.ordinal());
        buf.writeLong(line.timestamp);
        buf.m_130070_(line.message);
        buf.writeByte(line.type.ordinal());
        buf.m_236828_(line.sourceLines, SourceLine::write);
        buf.m_236828_(line.stackTrace, FriendlyByteBuf::m_130070_);
    }

    public String getText() {
        if (this.cachedText == null) {
            StringBuilder builder = new StringBuilder();
            if (!this.sourceLines.isEmpty()) {
                for (SourceLine line : this.sourceLines) {
                    if (line.line == 0 || line.source.isEmpty()) continue;
                    builder.append(line.source).append('#').append(line.line).append(':').append(' ');
                    break;
                }
            }
            if (!this.group.isEmpty()) {
                builder.append(this.group);
            }
            builder.append(this.message);
            this.cachedText = builder.toString();
        }
        return this.cachedText;
    }

    public ConsoleLine withSourceLine(String source, int line) {
        if (source == null) {
            source = "";
        }
        if (!source.isEmpty() && source.startsWith(this.console.scriptType.nameStrip)) {
            source = source.substring(this.console.scriptType.nameStrip.length());
        }
        if (line < 0) {
            line = 0;
        }
        if (this.sourceLines.isEmpty()) {
            this.sourceLines = Set.of(new SourceLine(source, line));
            return this;
        }
        if (this.sourceLines.size() == 1) {
            SourceLine line0 = this.sourceLines.iterator().next();
            this.sourceLines = new LinkedHashSet<SourceLine>();
            this.sourceLines.add(line0);
        }
        this.sourceLines.add(new SourceLine(source, line));
        return this;
    }

    public ConsoleLine withExternalFile(Path path) {
        this.externalFile = path;
        this.sourceLines = Set.of(new SourceLine(path.getFileName().toString(), 0));
        return this;
    }

    public String toString() {
        return this.getText();
    }

    public record SourceLine(String source, int line) {
        public SourceLine(FriendlyByteBuf buf) {
            this(buf.m_130277_(), buf.m_130242_());
        }

        @Override
        public String toString() {
            if (this.source.isEmpty() && this.line == 0) {
                return "";
            }
            if (this.source.isEmpty()) {
                return "<unknown source>#" + this.line;
            }
            if (this.line == 0) {
                return this.source;
            }
            return this.source + "#" + this.line;
        }

        public static void write(FriendlyByteBuf buf, SourceLine sourceLine) {
            buf.m_130070_(sourceLine.source);
            buf.m_130130_(sourceLine.line);
        }
    }
}

