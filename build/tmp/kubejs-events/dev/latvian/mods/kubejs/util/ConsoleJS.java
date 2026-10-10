/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.latvian.mods.rhino.Context
 *  dev.latvian.mods.rhino.EcmaError
 *  dev.latvian.mods.rhino.RhinoException
 *  dev.latvian.mods.rhino.ScriptStackElement
 *  dev.latvian.mods.rhino.WrappedException
 *  net.minecraft.ChatFormatting
 *  net.minecraft.network.chat.Component
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package dev.latvian.mods.kubejs.util;

import dev.latvian.mods.kubejs.DevProperties;
import dev.latvian.mods.kubejs.platform.MiscPlatformHelper;
import dev.latvian.mods.kubejs.script.ConsoleLine;
import dev.latvian.mods.kubejs.script.ScriptManager;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.util.JSObjectType;
import dev.latvian.mods.kubejs.util.LogType;
import dev.latvian.mods.kubejs.util.MutedError;
import dev.latvian.mods.kubejs.util.StackTraceCollector;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.kubejs.util.WrappedJS;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.EcmaError;
import dev.latvian.mods.rhino.RhinoException;
import dev.latvian.mods.rhino.ScriptStackElement;
import dev.latvian.mods.rhino.WrappedException;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class ConsoleJS {
    public static ConsoleJS STARTUP;
    public static ConsoleJS SERVER;
    public static ConsoleJS CLIENT;
    private static final Pattern GARBAGE_PATTERN;
    private static final Function<String, String> ERROR_REDUCE;
    public final ScriptType scriptType;
    public final transient Collection<ConsoleLine> errors;
    public final transient Collection<ConsoleLine> warnings;
    public final transient Logger logger;
    private final Path logFile;
    private boolean capturingErrors;
    private String group;
    private boolean muted;
    private boolean debugEnabled;
    private boolean writeToFile;
    private final List<String> writeQueue;
    private final Calendar calendar;

    public static ConsoleJS getCurrent(ConsoleJS def) {
        Context cx = ScriptManager.getCurrentContext();
        return cx == null ? def : ConsoleJS.getCurrent(cx);
    }

    public static ConsoleJS getCurrent(@Nullable Context cx) {
        if (cx == null && (cx = ScriptManager.getCurrentContext()) == null) {
            return STARTUP;
        }
        return (ConsoleJS)cx.getProperty("Console", null);
    }

    public ConsoleJS(ScriptType m, Logger log) {
        this.scriptType = m;
        this.errors = new ConcurrentLinkedDeque<ConsoleLine>();
        this.warnings = new ConcurrentLinkedDeque<ConsoleLine>();
        this.logger = log;
        this.logFile = m.getLogFile();
        this.capturingErrors = DevProperties.get().alwaysCaptureErrors;
        this.group = "";
        this.muted = false;
        this.debugEnabled = false;
        this.writeToFile = true;
        this.writeQueue = new LinkedList<String>();
        this.calendar = Calendar.getInstance();
    }

    public Logger getLogger() {
        return this.logger;
    }

    protected boolean shouldPrint() {
        return !this.muted;
    }

    public void setMuted(boolean m) {
        this.muted = m;
    }

    public boolean getMuted() {
        return this.muted;
    }

    public void setDebugEnabled(boolean m) {
        this.debugEnabled = m;
    }

    public boolean getDebugEnabled() {
        return this.debugEnabled;
    }

    public synchronized void setWriteToFile(boolean m) {
        this.writeToFile = m;
    }

    public synchronized boolean getWriteToFile() {
        return this.writeToFile;
    }

    public synchronized void setCapturingErrors(boolean enabled) {
        if (DevProperties.get().alwaysCaptureErrors) {
            this.capturingErrors = true;
        } else if (this.capturingErrors != enabled) {
            this.capturingErrors = enabled;
            if (DevProperties.get().debugInfo) {
                if (this.capturingErrors) {
                    this.logger.info("Capturing errors for " + this.scriptType.name + " scripts enabled");
                } else {
                    this.logger.info("Capturing errors for " + this.scriptType.name + " scripts disabled");
                }
            }
        }
    }

    public synchronized void resetFile() {
        this.errors.clear();
        this.warnings.clear();
        this.scriptType.executor.execute(() -> {
            try {
                Files.write(this.logFile, List.of(), new OpenOption[0]);
            }
            catch (Exception ex) {
                this.logger.error("Failed to clear the log file: " + String.valueOf(ex));
            }
        });
    }

    private ConsoleLine line(LogType type, Object object, @Nullable Throwable error) {
        String pe;
        int ci;
        int lpi;
        Object o = UtilsJS.wrap(object, JSObjectType.ANY);
        if (o instanceof Component) {
            Component c = (Component)o;
            o = c.getString();
        }
        long timestamp = System.currentTimeMillis();
        ConsoleLine line = new ConsoleLine(this, timestamp, (String)(o == null || o.getClass().isPrimitive() || o instanceof Boolean || o instanceof String || o instanceof Number || o instanceof WrappedJS ? String.valueOf(o) : String.valueOf(o) + " [" + o.getClass().getName() + "]"));
        line.type = type;
        line.group = this.group;
        if (error instanceof RhinoException) {
            RhinoException ex = (RhinoException)error;
            if (ex.lineSource() != null) {
                line.withSourceLine(ex.lineSource(), ex.lineNumber());
            }
            if (this.capturingErrors) {
                for (ScriptStackElement el : ex.getScriptStack()) {
                    if (el.fileName == null || el.lineNumber < 0) continue;
                    line.withSourceLine(el.fileName, el.lineNumber);
                }
            }
        }
        if (line.message != null && (lpi = line.message.lastIndexOf(40)) > 0 && line.message.charAt(line.message.length() - 1) == ')' && (ci = (pe = line.message.substring(lpi + 1, line.message.length() - 1)).lastIndexOf(35)) > 0) {
            try {
                line.withSourceLine(pe.substring(0, ci), Integer.parseInt(pe.substring(ci + 1)));
                line.message = line.message.substring(0, lpi).trim();
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        if (line.sourceLines.isEmpty()) {
            int[] lineP = new int[]{0};
            String source = Context.getSourcePositionFromStack((Context)this.scriptType.manager.get().context, (int[])lineP);
            line.withSourceLine(source, lineP[0]);
        }
        return line;
    }

    private ConsoleLine log(LogType type, @Nullable Throwable error, Object message) {
        if (this.shouldPrint()) {
            ConsoleLine line = this.line(type, message, error);
            type.callback.accept(this.logger, line.getText());
            if (this.capturingErrors) {
                if (type == LogType.ERROR) {
                    this.errors.add(line);
                } else if (type == LogType.WARN) {
                    this.warnings.add(line);
                }
            }
            if (this.writeToFile) {
                this.writeToFile(type, line.timestamp, line.getText());
            }
            return line;
        }
        return null;
    }

    public synchronized void writeToFile(LogType type, String line) {
        this.writeToFile(type, System.currentTimeMillis(), line);
    }

    public synchronized void writeToFile(LogType type, long timestamp, String line) {
        if (!this.writeToFile || MiscPlatformHelper.get().isDataGen()) {
            return;
        }
        this.calendar.setTimeInMillis(timestamp);
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        UtilsJS.appendTimestamp(sb, this.calendar);
        sb.append(']');
        sb.append(' ');
        sb.append('[');
        sb.append((Object)type);
        sb.append(']');
        sb.append(' ');
        if (type == LogType.ERROR) {
            sb.append('!');
            sb.append(' ');
        }
        sb.append(line);
        this.writeQueue.add(sb.toString());
    }

    public synchronized void flush(boolean sync) {
        if (this.writeQueue.isEmpty()) {
            return;
        }
        List<String> lines = Arrays.asList(this.writeQueue.toArray(UtilsJS.EMPTY_STRING_ARRAY));
        this.writeQueue.clear();
        if (sync) {
            try {
                Files.write(this.logFile, lines, StandardOpenOption.APPEND);
            }
            catch (Exception ex) {
                this.logger.error("Failed to write to the log file: " + String.valueOf(ex));
            }
        } else {
            this.scriptType.executor.execute(() -> {
                try {
                    Files.write(this.logFile, (Iterable<? extends CharSequence>)lines, StandardOpenOption.APPEND);
                }
                catch (Exception ex) {
                    this.logger.error("Failed to write to the log file: " + String.valueOf(ex));
                }
            });
        }
    }

    public void log(Object ... message) {
        for (Object s : message) {
            this.info(s);
        }
    }

    public ConsoleLine info(Object message) {
        return this.log(LogType.INFO, null, message);
    }

    public ConsoleLine infof(String message, Object ... args) {
        return this.info(message.formatted(args));
    }

    public ConsoleLine warn(Object message) {
        return this.log(LogType.WARN, null, message);
    }

    public ConsoleLine warn(String message, Throwable error, @Nullable Pattern exitPattern) {
        if (this.shouldPrint()) {
            ConsoleLine l = this.log(LogType.WARN, error, message.isEmpty() ? error.getMessage() : message + ": " + error.getMessage());
            this.handleError(l, error, exitPattern, !this.capturingErrors);
            return l;
        }
        return null;
    }

    public ConsoleLine warn(String message, Throwable error) {
        return this.warn(message, error, null);
    }

    public ConsoleLine warnf(String message, Object ... args) {
        return this.warn(message.formatted(args));
    }

    public ConsoleLine error(Object message) {
        return this.log(LogType.ERROR, null, message);
    }

    public ConsoleLine error(String message, Throwable error, @Nullable Pattern exitPattern) {
        if (this.shouldPrint()) {
            ConsoleLine l = this.log(LogType.ERROR, error, message.isEmpty() ? error.getMessage() : message + ": " + error.getMessage());
            this.handleError(l, error, exitPattern, true);
            return l;
        }
        return null;
    }

    public ConsoleLine error(String message, Throwable throwable) {
        return this.error(message, throwable, null);
    }

    public ConsoleLine errorf(String message, Object ... args) {
        return this.error(message.formatted(args));
    }

    public boolean shouldPrintDebug() {
        return this.debugEnabled && this.shouldPrint();
    }

    public ConsoleLine debug(Object message) {
        if (this.shouldPrintDebug()) {
            return this.log(LogType.DEBUG, null, message);
        }
        return null;
    }

    public ConsoleLine debugf(String message, Object ... args) {
        if (this.shouldPrintDebug()) {
            return this.debug(message.formatted(args));
        }
        return null;
    }

    public void group() {
        this.group = this.group + "  ";
    }

    public void groupEnd() {
        if (this.group.length() >= 2) {
            this.group = this.group.substring(0, this.group.length() - 2);
        }
    }

    public void trace() {
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        this.info("=== Stack Trace ===");
        for (StackTraceElement element : elements) {
            this.info("=\t" + String.valueOf(element));
        }
    }

    public int getScriptLine() {
        int[] linep = new int[]{0};
        Context.getSourcePositionFromStack((Context)this.scriptType.manager.get().context, (int[])linep);
        return linep[0];
    }

    public void printClass(String className, boolean tree) {
        try {
            Class<?> c = Class.forName(className);
            Class<?> sc = c.getSuperclass();
            this.info("=== " + c.getName() + " ===");
            this.info("= Parent class =");
            this.info("> " + (sc == null ? "-" : sc.getName()));
            HashMap<Object, VarFunc> vars = new HashMap<Object, VarFunc>();
            HashMap<String, VarFunc> funcs = new HashMap<String, VarFunc>();
            for (Field field : c.getDeclaredFields()) {
                if ((field.getModifiers() & 1) == 0 || (field.getModifiers() & 0x80) == 0) continue;
                VarFunc f2 = new VarFunc(field.getName(), field.getType());
                f2.flags |= 1;
                if ((field.getModifiers() & 0x10) == 0) {
                    f2.flags |= 2;
                }
                vars.put(f2.name, f2);
            }
            for (AccessibleObject accessibleObject : c.getDeclaredMethods()) {
                if ((((Method)accessibleObject).getModifiers() & 1) == 0) continue;
                VarFunc f2 = new VarFunc(((Method)accessibleObject).getName(), ((Method)accessibleObject).getReturnType());
                for (int i = 0; i < ((Method)accessibleObject).getParameterCount(); ++i) {
                    f2.params.add(((Executable)accessibleObject).getParameters()[i].getType());
                }
                if (f2.name.length() >= 4 && f2.name.startsWith("get") && Character.isUpperCase(f2.name.charAt(3)) && f2.params.size() == 0) {
                    String n = Character.toLowerCase(f2.name.charAt(3)) + f2.name.substring(4);
                    VarFunc f0 = (VarFunc)vars.get(n);
                    if (f0 == null) {
                        vars.put(n, new VarFunc(n, f2.type));
                        continue;
                    }
                    if (f0.type.equals(f2.type)) {
                        f0.flags |= 1;
                        continue;
                    }
                }
                funcs.put(f2.name, f2);
            }
            this.info("= Variables and Functions =");
            if (vars.isEmpty() && funcs.isEmpty()) {
                this.info("-");
            } else {
                vars.values().stream().sorted().forEach(f -> this.info("> " + ((f.flags & 2) == 0 ? "val" : "var") + " " + f.name + ": " + this.getSimpleName(f.type)));
                funcs.values().stream().sorted().forEach(f -> this.info("> function " + f.name + "(" + f.params.stream().map(this::getSimpleName).collect(Collectors.joining(", ")) + "): " + this.getSimpleName(f.type)));
            }
            if (tree && sc != null) {
                this.info("");
                this.printClass(sc.getName(), true);
            }
        }
        catch (Throwable ex) {
            this.error("= Error loading class '" + className + "' =", ex);
        }
    }

    public void printClass(String className) {
        this.printClass(className, false);
    }

    private String getSimpleName(Class<?> c) {
        if (c.isPrimitive()) {
            return c.getName();
        }
        String s = c.getName();
        int i = s.lastIndexOf(46);
        s = s.substring(i + 1);
        i = s.lastIndexOf(36);
        s = s.substring(i + 1);
        return s;
    }

    public void printObject(@Nullable Object o, boolean tree) {
        if (o == null) {
            this.info("=== null ===");
        } else {
            this.info("=== " + o.getClass().getName() + " ===");
            this.info("= toString() =");
            this.info("> " + String.valueOf(o));
            this.info("= hashCode() =");
            this.info("> " + Integer.toHexString(o.hashCode()));
            this.info("");
            this.printClass(o.getClass().getName(), tree);
        }
    }

    public void printObject(@Nullable Object o) {
        this.printObject(o, false);
    }

    public void handleError(ConsoleLine line, Throwable error, @Nullable Pattern exitPattern, boolean print) {
        MutedError err;
        while (error instanceof WrappedException) {
            WrappedException ex = (WrappedException)error;
            error = ex.getWrappedException();
        }
        if (error instanceof EcmaError) {
            return;
        }
        line.stackTrace = new ArrayList<String>();
        error.printStackTrace(new StackTraceCollector(line.stackTrace, exitPattern, ERROR_REDUCE));
        if (!(!print || error instanceof MutedError && (err = (MutedError)((Object)error)).isMuted())) {
            for (String s : line.stackTrace) {
                line.type.callback.accept(this.logger, s);
                if (!this.writeToFile) continue;
                this.writeToFile(line.type, line.timestamp, s);
            }
        }
    }

    public Component errorsComponent(String command) {
        return Component.m_237113_((String)("KubeJS errors found [" + this.errors.size() + "]! Run '" + command + "' for more info")).kjs$clickRunCommand(command).kjs$hover((Component)Component.m_237113_((String)"Click to show")).m_130940_(ChatFormatting.DARK_RED);
    }

    static {
        GARBAGE_PATTERN = Pattern.compile("(?:TRANSFORMER|LAYER PLUGIN)/\\w+@[^/]+/");
        ERROR_REDUCE = s -> {
            if (s.startsWith("java.util.concurrent.ForkJoin") || s.startsWith("jdk.internal.")) {
                return "";
            }
            return GARBAGE_PATTERN.matcher((CharSequence)s).replaceAll("").replace("dev.latvian.mods.", "\u2026");
        };
    }

    private static final class VarFunc
    implements Comparable<VarFunc> {
        public final String name;
        public final Class<?> type;
        public final ArrayList<Class<?>> params;
        public int flags;

        public VarFunc(String n, Class<?> t) {
            this.name = n;
            this.type = t;
            this.flags = 0;
            this.params = new ArrayList();
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            VarFunc varFunc = (VarFunc)o;
            return Objects.equals(this.name, varFunc.name) && Objects.equals(this.type, varFunc.type) && Objects.equals(this.flags, varFunc.flags) && Objects.equals(this.params, varFunc.params);
        }

        public int hashCode() {
            return Objects.hash(this.name, this.type, this.flags, this.params);
        }

        @Override
        public int compareTo(VarFunc o) {
            return this.name.compareToIgnoreCase(o.name);
        }
    }
}

