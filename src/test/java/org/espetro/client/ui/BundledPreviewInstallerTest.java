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

class BundledPreviewInstallerTest {
    @TempDir Path game;

    private static String row(String path, String value) throws Exception {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes))
            + "\t" + bytes.length + "\t" + path + "\n";
    }

    private static BundledMenuInstaller.Resources resources(String manifest, Map<String, String> payloads) {
        return name -> {
            String value = name.equals("manifest.tsv") ? manifest : payloads.get(name);
            if (value == null) throw new IOException("Missing test payload: " + name);
            return new ByteArrayInputStream(value.getBytes(StandardCharsets.UTF_8));
        };
    }

    @Test void freshClientGetsBothDirectoriesIncludingUnicodeMapNamesAndJpegFactions() throws Exception {
        String faction = "EsFactions/plamc_2th.jpg", map = "EsWorld/越南.png";
        String manifest = row(faction, "jpeg") + row(map, "png");
        var result = BundledPreviewInstaller.install(game, resources(manifest,
            Map.of("files/" + faction, "jpeg", "files/" + map, "png")));
        assertEquals(2, result.updatedFiles());
        assertEquals("jpeg", Files.readString(game.resolve(faction)));
        assertEquals("png", Files.readString(game.resolve(map)));
        assertEquals(0, BundledPreviewInstaller.install(game, resources(manifest, Map.of())).updatedFiles());
        assertEquals("assets/espetro/textures/gui/factions/plamc_2th.jpg",
            BundledPreviewInstaller.resourcePath(faction));
        assertEquals("espetro_previews/files/EsWorld/越南.png", BundledPreviewInstaller.resourcePath(map));
    }

    @Test void updateBacksUpImagesAndLeavesFactionJsonAndWorldDataUntouched() throws Exception {
        Files.createDirectories(game.resolve("EsFactions"));
        Files.createDirectories(game.resolve("EsWorld/region"));
        Files.writeString(game.resolve("EsFactions/pla_112.png"), "old image");
        Files.writeString(game.resolve("EsFactions/user.json"), "operator config");
        Files.writeString(game.resolve("EsWorld/region/r.0.0.mca"), "world data");
        var result = BundledPreviewInstaller.install(game, resources(row("EsFactions/pla_112.png", "new image"),
            Map.of("files/EsFactions/pla_112.png", "new image")));
        assertEquals("old image", Files.readString(result.backupDirectory().resolve("EsFactions/pla_112.png")));
        assertEquals("operator config", Files.readString(game.resolve("EsFactions/user.json")));
        assertEquals("world data", Files.readString(game.resolve("EsWorld/region/r.0.0.mca")));
    }

    @Test void badSecondImageCannotPartiallyUpdateExistingImages() throws Exception {
        Files.createDirectories(game.resolve("EsFactions"));
        Files.writeString(game.resolve("EsFactions/pla_112.png"), "old");
        String manifest = row("EsFactions/pla_112.png", "new") + row("EsWorld/越南.png", "valid");
        assertThrows(IOException.class, () -> BundledPreviewInstaller.install(game, resources(manifest,
            Map.of("files/EsFactions/pla_112.png", "new", "files/EsWorld/越南.png", "corrupt"))));
        assertEquals("old", Files.readString(game.resolve("EsFactions/pla_112.png")));
        assertFalse(Files.exists(game.resolve("EsWorld/越南.png")));
    }

    @Test void rejectsConfigurationWorldFilesAndEscapingPaths() throws Exception {
        for (String path : new String[]{"EsFactions/user.json", "EsWorld/region/r.0.0.mca",
            "EsWorld/../../outside.png", "EsFactions/./a.png", "EsFactions/a\\b.png",
            "/EsFactions/a.png", "config/fancymenu/assets/logo.png"}) {
            assertThrows(IOException.class, () -> BundledPreviewInstaller.install(game,
                resources(row(path, "x"), Map.of("files/" + path, "x"))));
        }
        assertThrows(IOException.class, () -> BundledPreviewInstaller.resourcePath("EsWorld/../outside.png"));
        assertFalse(Files.exists(game.resolve("EsFactions")));
    }
}
