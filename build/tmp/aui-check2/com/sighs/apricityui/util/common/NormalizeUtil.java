/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.util.common;

import java.util.Locale;

public final class NormalizeUtil {
    public static String normalizeContainerId(String containerId) {
        if (containerId == null) {
            return null;
        }
        String normalized = containerId.trim().toLowerCase(Locale.ROOT);
        return normalized.isEmpty() ? null : normalized;
    }

    public static String normalizeTemplatePath(String rawTemplatePath) {
        if (rawTemplatePath == null) {
            return null;
        }
        String path = rawTemplatePath.trim().replace('\\', '/');
        if (path.isEmpty()) {
            return null;
        }
        if (path.startsWith("./")) {
            path = path.substring(2);
        }
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        if (path.startsWith("apricity/")) {
            path = path.substring("apricity/".length());
        }
        if (path.contains("..")) {
            return null;
        }
        if (!path.endsWith(".html")) {
            return null;
        }
        String[] segments = path.split("/");
        if (segments.length < 2) {
            return null;
        }
        return path;
    }
}

