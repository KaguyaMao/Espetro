/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.client.gui;

import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;

final class FactionSelectionImageResolver {
    private FactionSelectionImageResolver() {
    }

    static Path resolveClientFile(Path gameDir, String selectionImage) {
        Path candidate;
        if (gameDir == null || selectionImage == null || selectionImage.isBlank() || selectionImage.contains(":")) {
            return null;
        }
        Path root = gameDir.resolve("EsFactions").toAbsolutePath().normalize();
        try {
            candidate = root.resolve(selectionImage.trim()).normalize();
        }
        catch (Exception ignored) {
            return null;
        }
        if (candidate.equals(root) || !candidate.startsWith(root)) {
            return null;
        }
        return Files.isRegularFile(candidate, LinkOption.NOFOLLOW_LINKS) ? candidate : null;
    }
}

