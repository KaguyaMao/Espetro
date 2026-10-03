/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.Level
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.core.Appender
 *  org.apache.logging.log4j.core.Layout
 *  org.apache.logging.log4j.core.LoggerContext
 *  org.apache.logging.log4j.core.appender.RandomAccessFileAppender
 *  org.apache.logging.log4j.core.appender.RandomAccessFileAppender$Builder
 *  org.apache.logging.log4j.core.appender.RollingRandomAccessFileAppender
 *  org.apache.logging.log4j.core.config.AppenderRef
 *  org.apache.logging.log4j.core.config.Configuration
 *  org.apache.logging.log4j.core.config.LoggerConfig
 *  org.apache.logging.log4j.core.layout.PatternLayout
 */
package com.sighs.apricityui.util;

import java.io.Serializable;
import java.util.Locale;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.RandomAccessFileAppender;
import org.apache.logging.log4j.core.appender.RollingRandomAccessFileAppender;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;

public final class AuiLogging {
    public static final String LOGGER_NAMESPACE = "com.sighs.apricityui";
    private static final String APPENDER_NAME = "ApricityUI-File";
    private static final String FALLBACK_FILE_NAME = "logs/aui.log";
    private static final String FALLBACK_LAYOUT = "[%d{ddMMMyyyy HH:mm:ss.SSS}] [%t/%level] [%logger/%markerSimpleName]: %msg%n%xEx";
    private static final Object INSTALL_LOCK = new Object();

    private AuiLogging() {
    }

    public static void installFileAppender() {
        try {
            LoggerContext context = (LoggerContext)LogManager.getContext((boolean)false);
            Configuration configuration = context.getConfiguration();
            RollingRandomAccessFileAppender latest = AuiLogging.findLatestFileAppender(configuration);
            Layout layout = latest == null ? null : latest.getLayout();
            String fileName = latest == null ? FALLBACK_FILE_NAME : AuiLogging.auiFileName(latest.getFileName());
            Level level = latest == null ? Level.INFO : AuiLogging.appenderLevel(configuration, latest.getName());
            AuiLogging.installFileAppender(context, fileName, (Layout<? extends Serializable>)layout, level);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static void installFileAppender(LoggerContext context, String fileName, Layout<? extends Serializable> layout, Level level) {
        if (context == null) {
            return;
        }
        Object object = INSTALL_LOCK;
        synchronized (object) {
            Configuration configuration = context.getConfiguration();
            Appender existing = configuration.getAppender(APPENDER_NAME);
            if (existing != null) {
                AuiLogging.attachPackageAppender(context, existing, level);
                return;
            }
            PatternLayout effectiveLayout = layout == null ? PatternLayout.newBuilder().withConfiguration(configuration).withPattern(FALLBACK_LAYOUT).build() : layout;
            RandomAccessFileAppender appender = ((RandomAccessFileAppender.Builder)((RandomAccessFileAppender.Builder)((RandomAccessFileAppender.Builder)RandomAccessFileAppender.newBuilder().withName(APPENDER_NAME)).withConfiguration(configuration)).withLayout((Layout)effectiveLayout)).setFileName(fileName == null || fileName.isBlank() ? FALLBACK_FILE_NAME : fileName).setAppend(false).build();
            if (appender == null) {
                return;
            }
            appender.start();
            AuiLogging.attachPackageAppender(context, (Appender)appender, level);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void attachPackageAppender(LoggerContext context, Appender appender, Level level) {
        if (context == null || appender == null) {
            return;
        }
        Object object = INSTALL_LOCK;
        synchronized (object) {
            LoggerConfig logger;
            Configuration configuration = context.getConfiguration();
            Appender registered = configuration.getAppender(appender.getName());
            if (registered == null) {
                if (!appender.isStarted()) {
                    appender.start();
                }
                configuration.addAppender(appender);
                registered = appender;
            }
            if (!LOGGER_NAMESPACE.equals((logger = configuration.getLoggerConfig(LOGGER_NAMESPACE)).getName())) {
                logger = new LoggerConfig(LOGGER_NAMESPACE, logger.getLevel(), true);
                configuration.addLogger(LOGGER_NAMESPACE, logger);
            }
            if (!logger.getAppenders().containsKey(registered.getName())) {
                logger.addAppender(registered, level, null);
            }
            context.updateLoggers();
        }
    }

    static String auiFileName(String latestFileName) {
        return AuiLogging.siblingName(latestFileName, "aui.log", FALLBACK_FILE_NAME);
    }

    private static String siblingName(String source, String targetName, String fallback) {
        if (source == null || source.isBlank()) {
            return fallback;
        }
        int separator = Math.max(source.lastIndexOf(47), source.lastIndexOf(92));
        return (separator < 0 ? "" : source.substring(0, separator + 1)) + targetName;
    }

    private static RollingRandomAccessFileAppender findLatestFileAppender(Configuration configuration) {
        RollingRandomAccessFileAppender rolling;
        Appender named = configuration.getAppender("File");
        if (named instanceof RollingRandomAccessFileAppender && AuiLogging.isLatestLog((rolling = (RollingRandomAccessFileAppender)named).getFileName())) {
            return rolling;
        }
        for (Appender appender : configuration.getAppenders().values()) {
            RollingRandomAccessFileAppender rolling2;
            if (!(appender instanceof RollingRandomAccessFileAppender) || !AuiLogging.isLatestLog((rolling2 = (RollingRandomAccessFileAppender)appender).getFileName())) continue;
            return rolling2;
        }
        return null;
    }

    private static boolean isLatestLog(String fileName) {
        if (fileName == null) {
            return false;
        }
        String normalized = fileName.replace('\\', '/').toLowerCase(Locale.ROOT);
        return normalized.endsWith("/latest.log") || "latest.log".equals(normalized);
    }

    private static Level appenderLevel(Configuration configuration, String appenderName) {
        AppenderRef reference = AuiLogging.appenderReference(configuration.getRootLogger(), appenderName);
        if (reference != null) {
            return reference.getLevel();
        }
        for (LoggerConfig logger : configuration.getLoggers().values()) {
            reference = AuiLogging.appenderReference(logger, appenderName);
            if (reference == null) continue;
            return reference.getLevel();
        }
        return Level.INFO;
    }

    private static AppenderRef appenderReference(LoggerConfig logger, String appenderName) {
        for (AppenderRef reference : logger.getAppenderRefs()) {
            if (!reference.getRef().equals(appenderName)) continue;
            return reference;
        }
        return null;
    }
}

