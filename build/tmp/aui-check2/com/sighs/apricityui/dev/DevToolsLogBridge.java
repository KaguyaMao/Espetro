/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.core.Appender
 *  org.apache.logging.log4j.core.LogEvent
 *  org.apache.logging.log4j.core.LoggerContext
 *  org.apache.logging.log4j.core.appender.AbstractAppender
 *  org.slf4j.Logger
 */
package com.sighs.apricityui.dev;

import com.sighs.apricityui.util.AuiLogging;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.slf4j.Logger;

public final class DevToolsLogBridge {
    private static final String APPENDER_NAME = "ApricityUI-DevToolsConsole";
    private static final int MAX_PENDING_LOGS = 2048;
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
    private static final BlockingQueue<ConsoleLog> PENDING = new ArrayBlockingQueue<ConsoleLog>(2048);
    private static final Object INSTALL_LOCK = new Object();

    private DevToolsLogBridge() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void install(Logger sourceLogger) {
        String loggerName;
        if (sourceLogger == null) {
            return;
        }
        try {
            loggerName = sourceLogger.getName();
        }
        catch (Throwable ignored) {
            return;
        }
        if (loggerName == null || loggerName.isBlank()) {
            return;
        }
        Object object = INSTALL_LOCK;
        synchronized (object) {
            try {
                LoggerContext context = (LoggerContext)LogManager.getContext((boolean)false);
                Appender attached = context.getConfiguration().getAppender(APPENDER_NAME);
                if (attached instanceof BridgeAppender) {
                    AuiLogging.attachPackageAppender(context, attached, null);
                    return;
                }
                if (attached != null) {
                    return;
                }
                BridgeAppender appender = new BridgeAppender();
                appender.start();
                AuiLogging.attachPackageAppender(context, (Appender)appender, null);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
    }

    public static List<ConsoleLog> drain() {
        return DevToolsLogBridge.drain(Integer.MAX_VALUE);
    }

    public static List<ConsoleLog> drain(int maxEntries) {
        if (maxEntries <= 0) {
            return List.of();
        }
        ArrayList<ConsoleLog> drained = new ArrayList<ConsoleLog>();
        PENDING.drainTo(drained, maxEntries);
        return drained;
    }

    private static void enqueue(LogEvent event) {
        if (event == null) {
            return;
        }
        try {
            String text = event.getMessage() == null ? "" : event.getMessage().getFormattedMessage();
            String stack = DevToolsLogBridge.formatStack(event.getThrown());
            ConsoleLog log = new ConsoleLog(DevToolsLogBridge.level(event), text == null ? "" : text, DevToolsLogBridge.source(event), stack, DevToolsLogBridge.formatTime(event.getTimeMillis()));
            if (!PENDING.offer(log)) {
                PENDING.poll();
                PENDING.offer(log);
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private static String level(LogEvent event) {
        String value;
        return switch (value = event.getLevel() == null ? "INFO" : event.getLevel().toString().toUpperCase(Locale.ROOT)) {
            case "ERROR", "FATAL" -> "error";
            case "WARN" -> "warn";
            default -> "info";
        };
    }

    private static String source(LogEvent event) {
        String loggerName = event.getLoggerName();
        return loggerName == null || loggerName.isBlank() ? "ApricityUI" : loggerName;
    }

    private static String formatTime(long timeMillis) {
        try {
            return LocalTime.ofInstant(Instant.ofEpochMilli(timeMillis), ZoneId.systemDefault()).format(TIME_FORMAT);
        }
        catch (Throwable ignored) {
            return LocalTime.now().format(TIME_FORMAT);
        }
    }

    private static String formatStack(Throwable throwable) {
        if (throwable == null) {
            return null;
        }
        StringWriter writer = new StringWriter();
        throwable.printStackTrace(new PrintWriter(writer));
        return writer.toString().trim();
    }

    private static final class BridgeAppender
    extends AbstractAppender {
        private BridgeAppender() {
            super(DevToolsLogBridge.APPENDER_NAME, null, null, true);
        }

        public void append(LogEvent event) {
            DevToolsLogBridge.enqueue(event);
        }
    }

    public record ConsoleLog(String level, String text, String source, String stack, String time) {
    }
}

