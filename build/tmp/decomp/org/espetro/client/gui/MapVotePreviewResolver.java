/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.client.gui;

import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.List;
import org.espetro.mapconfig.PathSafety;

final class MapVotePreviewResolver {
    private static final List<String> ROOT_NAMES = List.of("Esworld", "EsWorld");

    private MapVotePreviewResolver() {
    }

    static Path resolve(Path gameDir, String mapFolder) {
        if (gameDir == null || PathSafety.validateMapFolderName(mapFolder).isPresent()) {
            return null;
        }
        String fileName = mapFolder.trim() + ".png";
        for (String rootName : ROOT_NAMES) {
            Path root = gameDir.resolve(rootName).toAbsolutePath().normalize();
            Path candidate = root.resolve(fileName).normalize();
            if (!candidate.startsWith(root) || !candidate.getParent().equals(root) || !Files.isRegularFile(candidate, LinkOption.NOFOLLOW_LINKS)) continue;
            return candidate;
        }
        return null;
    }
}

