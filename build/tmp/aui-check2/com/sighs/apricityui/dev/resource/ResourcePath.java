/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.dev.resource;

import java.util.Locale;

public final class ResourcePath {
    private ResourcePath() {
    }

    public static String normalize(String path) {
        String normalized = ResourcePath.safe(path).replace('\\', '/').trim();
        while (normalized.startsWith("/")) {
            normalized = normalized.substring(1);
        }
        while (normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        return normalized;
    }

    public static String parent(String path) {
        String normalized = ResourcePath.normalize(path);
        int separator = normalized.lastIndexOf(47);
        return separator < 0 ? "" : normalized.substring(0, separator);
    }

    public static String fileName(String path) {
        String normalized = ResourcePath.normalize(path);
        int separator = normalized.lastIndexOf(47);
        return separator < 0 ? normalized : normalized.substring(separator + 1);
    }

    public static String formatSize(long bytes) {
        if (bytes < 0L) {
            return "--";
        }
        if (bytes < 1024L) {
            return bytes + " B";
        }
        double kilobytes = (double)bytes / 1024.0;
        if (kilobytes < 1024.0) {
            return String.format(Locale.ROOT, "%.1f KB", kilobytes);
        }
        return String.format(Locale.ROOT, "%.1f MB", kilobytes / 1024.0);
    }

    public static String safe(String value) {
        return value == null ? "" : value;
    }
}

