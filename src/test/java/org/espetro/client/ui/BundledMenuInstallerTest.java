package org.espetro.client.ui;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class BundledMenuInstallerTest {
    @TempDir Path root;
    private static final String TARGET = "config/fancymenu/customization/title_screen_layout.txt";

    private static String row(String path, String value) throws Exception {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes))
            + "\t" + bytes.length + "\t" + path + "\n";
    }

    private static BundledMenuInstaller.Resources resources(String manifest, Map<String, String> files) {
        return name -> {
            String text = name.equals("manifest.tsv") ? manifest : files.get(name);
            if (text == null) throw new IOException("Missing test resource " + name);
            return new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
        };
    }

    @Test void replacesOldLayoutAndBacksItUpWithoutTouchingUserPreferences() throws Exception {
        Path layout = root.resolve(TARGET); Files.createDirectories(layout.getParent());
        Files.writeString(layout, "old menu");
        Path options = root.resolve("config/fancymenu/options.txt"); Files.writeString(options, "user options");
        var result = BundledMenuInstaller.install(root, resources(row(TARGET, "new menu"),
            Map.of("files/" + TARGET, "new menu")));
        assertEquals(1, result.updatedFiles());
        assertEquals("new menu", Files.readString(layout));
        assertEquals("old menu", Files.readString(result.backupDirectory().resolve(TARGET)));
        assertEquals("user options", Files.readString(options));
    }

    @Test void matchingFilesSkipPayloadReadsButDeletedOrEditedFilesAreRepaired() throws Exception {
        String manifest = row(TARGET, "menu");
        var source = resources(manifest, Map.of("files/" + TARGET, "menu"));
        var first = BundledMenuInstaller.install(root, source);
        assertEquals(1, first.updatedFiles()); assertNull(first.backupDirectory());
        assertEquals(0, BundledMenuInstaller.install(root, resources(manifest, Map.of())).updatedFiles());
        Files.writeString(root.resolve(TARGET), "edit");
        assertEquals(1, BundledMenuInstaller.install(root, source).updatedFiles());
        Files.delete(root.resolve(TARGET));
        assertEquals(1, BundledMenuInstaller.install(root, source).updatedFiles());
    }

    @Test void corruptSecondPayloadDoesNotReplaceFirstLiveFile() throws Exception {
        Files.createDirectories(root.resolve(TARGET).getParent()); Files.writeString(root.resolve(TARGET), "old");
        String other = "config/fancymenu/assets/logo.png";
        String manifest = row(TARGET, "new") + row(other, "valid image");
        assertThrows(IOException.class, () -> BundledMenuInstaller.install(root,
            resources(manifest, Map.of("files/" + TARGET, "new", "files/" + other, "corrupt"))));
        assertEquals("old", Files.readString(root.resolve(TARGET)));
        assertFalse(Files.exists(root.resolve(other)));
        try (var paths = Files.list(root)) {
            assertTrue(paths.noneMatch(p -> p.getFileName().toString().startsWith(".espetro-menu-stage-")));
        }
    }

    @Test void rejectsTraversalAbsolutePathsAndDuplicateEntriesBeforeWriting() throws Exception {
        for (String path : new String[]{"config/fancymenu/../../options.txt", "config/fancymenu/./logo.png",
            "C:/config/fancymenu/logo.png", "/config/fancymenu/logo.png", "config/fancymenu/a\\b"}) {
            assertThrows(IOException.class, () -> BundledMenuInstaller.parse(row(path, "menu")));
        }
        String row = row(TARGET, "menu");
        assertThrows(IOException.class, () -> BundledMenuInstaller.parse(row + row));
        assertFalse(Files.exists(root.resolve("config")));
    }
}
