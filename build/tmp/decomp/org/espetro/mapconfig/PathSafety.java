/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.mapconfig;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Optional;

public final class PathSafety {
    private PathSafety() {
    }

    public static Optional<String> validateMapFolderName(String mapName) {
        if (mapName == null || mapName.isBlank()) {
            return Optional.of("map \u540d\u79f0\u4e0d\u80fd\u4e3a\u7a7a");
        }
        String name = mapName.trim();
        if (name.contains("..") || name.contains("/") || name.contains("\\") || name.contains(":") || name.contains("\u0000")) {
            return Optional.of("map \u540d\u79f0\u975e\u6cd5\uff08\u7981\u6b62\u8def\u5f84\u5206\u9694\u7b26\u3001.. \u6216\u7edd\u5bf9\u8def\u5f84\uff09: " + name);
        }
        if (name.startsWith(".") && !name.equals("_template")) {
            return Optional.of("map \u540d\u79f0\u4e0d\u80fd\u4ee5 . \u5f00\u5934: " + name);
        }
        return Optional.empty();
    }

    public static Path resolveChildDir(Path root, String childName) throws IOException {
        Path child;
        Optional<String> error = PathSafety.validateMapFolderName(childName);
        if (error.isPresent()) {
            throw new IOException(error.get());
        }
        Path rootReal = root.toAbsolutePath().normalize();
        if (Files.exists(rootReal, new LinkOption[0])) {
            rootReal = rootReal.toRealPath(new LinkOption[0]);
        }
        if (!(child = rootReal.resolve(childName).normalize()).startsWith(rootReal)) {
            throw new IOException("\u8def\u5f84\u8d8a\u754c: " + childName);
        }
        if (Files.exists(child, new LinkOption[0]) && Files.isSymbolicLink(child)) {
            Path real = child.toRealPath(new LinkOption[0]);
            if (!real.startsWith(rootReal)) {
                throw new IOException("\u7b26\u53f7\u94fe\u63a5\u8d8a\u754c: " + childName + " -> " + real);
            }
            return real;
        }
        return child;
    }

    public static String slugify(String input) {
        int cp;
        if (input == null || input.isBlank()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        String lower = input.trim().toLowerCase(Locale.ROOT);
        boolean lastDash = false;
        for (int i = 0; i < lower.length(); i += Character.charCount(cp)) {
            cp = lower.codePointAt(i);
            if ((cp < 97 || cp > 122) && (cp < 48 || cp > 57)) continue;
            sb.appendCodePoint(cp);
            lastDash = false;
        }
        while (sb.length() > 0 && sb.charAt(sb.length() - 1) == '_') {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }

    public static String stableShortHash(String input) {
        byte[] bytes;
        int h = -2128831035;
        for (byte b : bytes = input.getBytes(StandardCharsets.UTF_8)) {
            h ^= b & 0xFF;
            h *= 16777619;
        }
        return Integer.toHexString(h);
    }
}

