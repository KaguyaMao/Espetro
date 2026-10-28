package org.espetro.client.gui;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClientUiPreferencesTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void absentFileUsesDarkWithoutCreatingFiles() {
        Path file = temporaryDirectory.resolve(ClientUiPreferences.FILE_NAME);

        ClientUiPreferences.LoadResult result = ClientUiPreferences.load(file);

        assertEquals(UiTheme.Mode.DARK, result.mode());
        assertTrue(result.warning().isEmpty());
        assertFalse(Files.exists(file));
    }

    @Test
    void bothModesRoundTripAndReplaceThePreviousSetting() throws IOException {
        Path file = temporaryDirectory.resolve("config").resolve(ClientUiPreferences.FILE_NAME);

        ClientUiPreferences.save(file, UiTheme.Mode.LIGHT);
        assertEquals(UiTheme.Mode.LIGHT, ClientUiPreferences.load(file).mode());

        ClientUiPreferences.save(file, UiTheme.Mode.DARK);
        assertEquals(UiTheme.Mode.DARK, ClientUiPreferences.load(file).mode());
        assertTrue(ClientUiPreferences.load(file).warning().isEmpty());
        try (var children = Files.list(file.getParent())) {
            assertEquals(1, children.count());
        }
    }

    @Test
    void valuesIgnoreCaseAndSurroundingWhitespace() {
        assertEquals(UiTheme.Mode.LIGHT, ClientUiPreferences.parse("  LiGhT  ").mode());
        assertEquals(UiTheme.Mode.DARK, ClientUiPreferences.parse(" DARK ").mode());
        assertTrue(ClientUiPreferences.parse("light").warning().isEmpty());
    }

    @Test
    void invalidAndMissingValuesFallBackWithWarnings() throws IOException {
        Path file = temporaryDirectory.resolve(ClientUiPreferences.FILE_NAME);
        for (String content : new String[] {"theme=unknown", "theme=", "other=light"}) {
            Files.writeString(file, content, StandardCharsets.UTF_8);

            ClientUiPreferences.LoadResult result = ClientUiPreferences.load(file);

            assertEquals(UiTheme.Mode.DARK, result.mode());
            assertFalse(result.warning().isEmpty());
        }
    }

    @Test
    void malformedPropertiesAndUnreadableFilesFallBackWithWarnings() throws IOException {
        Path file = temporaryDirectory.resolve(ClientUiPreferences.FILE_NAME);
        Files.writeString(file, "theme=" + "\\" + "uZZZZ", StandardCharsets.UTF_8);
        assertEquals(UiTheme.Mode.DARK, ClientUiPreferences.load(file).mode());
        assertFalse(ClientUiPreferences.load(file).warning().isEmpty());

        assertEquals(UiTheme.Mode.DARK, ClientUiPreferences.load(temporaryDirectory).mode());
        assertFalse(ClientUiPreferences.load(temporaryDirectory).warning().isEmpty());
    }

    @Test
    void failedSavePreservesTheTargetAndRemovesTemporaryFile() throws IOException {
        Path target = temporaryDirectory.resolve(ClientUiPreferences.FILE_NAME);
        Files.createDirectory(target);
        Path marker = target.resolve("keep.txt");
        Files.writeString(marker, "keep", StandardCharsets.UTF_8);

        assertThrows(IOException.class, () -> ClientUiPreferences.save(target, UiTheme.Mode.LIGHT));

        assertEquals("keep", Files.readString(marker, StandardCharsets.UTF_8));
        try (var children = Files.list(temporaryDirectory)) {
            assertEquals(1, children.count());
        }
    }
}
