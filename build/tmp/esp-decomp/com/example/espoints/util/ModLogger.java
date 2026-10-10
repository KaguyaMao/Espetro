/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.example.espoints.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ModLogger {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String LOG_DIRECTORY = "logs/hcr_mod";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void debug(String message) {
        LOGGER.debug("[HCR] " + message);
    }

    public static void info(String message) {
        LOGGER.info("[HCR] " + message);
    }

    public static void warn(String message) {
        LOGGER.warn("[HCR] " + message);
    }

    public static void error(String message) {
        LOGGER.error("[HCR] " + message);
    }

    public static void error(String message, Throwable throwable) {
        LOGGER.error("[HCR] " + message, throwable);
    }

    public static void logToFile(String level, String message) {
        try {
            Path logDir = Paths.get(LOG_DIRECTORY, new String[0]);
            if (!Files.exists(logDir, new LinkOption[0])) {
                Files.createDirectories(logDir, new FileAttribute[0]);
            }
            Path logFile = logDir.resolve("hcr_mod.log");
            String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
            String logEntry = String.format("[%s] [%s] %s%n", timestamp, level, message);
            Files.write(logFile, logEntry.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        }
        catch (IOException e) {
            LOGGER.error("\u65e0\u6cd5\u5199\u5165\u65e5\u5fd7\u6587\u4ef6", (Throwable)e);
        }
    }

    public static void syncError(String message) {
        ModLogger.error("\u540c\u6b65\u9519\u8bef: " + message);
        ModLogger.logToFile("SYNC_ERROR", message);
    }

    public static void decodeError(String message) {
        ModLogger.error("\u89e3\u7801\u9519\u8bef: " + message);
        ModLogger.logToFile("DECODE_ERROR", message);
    }

    public static int hexToColor(String hexColor, int defaultColor) {
        try {
            String normalizedHex = hexColor.trim().toUpperCase();
            if (normalizedHex.startsWith("#")) {
                normalizedHex = normalizedHex.substring(1);
            }
            int color = Integer.parseInt(normalizedHex, 16);
            return 0xFF000000 | color;
        }
        catch (NumberFormatException e) {
            ModLogger.warn("\u65e0\u6548\u7684\u989c\u8272\u4ee3\u7801: " + hexColor + "\uff0c\u4f7f\u7528\u9ed8\u8ba4\u989c\u8272");
            return defaultColor;
        }
    }
}

