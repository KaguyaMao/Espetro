package org.espetro.client.gui;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Objects;
import java.util.Properties;

/** Local UI preferences. This class deliberately has no Minecraft or Forge dependencies. */
public final class ClientUiPreferences {
    public static final String FILE_NAME = "espetro-ui.properties";
    private static final String THEME_KEY = "theme";

    private ClientUiPreferences() {
    }

    public record LoadResult(UiTheme.Mode mode, String warning) {
    }

    /** Missing files use the default; unreadable or malformed files report a safe fallback. */
    public static LoadResult load(Path file) {
        try {
            if (Files.notExists(file)) {
                return new LoadResult(UiTheme.Mode.DARK, "");
            }
            Properties properties = new Properties();
            try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                properties.load(reader);
            }
            return parse(properties.getProperty(THEME_KEY));
        } catch (IOException | IllegalArgumentException | SecurityException failure) {
            return new LoadResult(UiTheme.Mode.DARK,
                "Could not read UI preferences " + file + ": " + failure);
        }
    }

    static LoadResult parse(String value) {
        if (value != null) {
            String normalized = value.strip().toLowerCase(Locale.ROOT);
            if ("light".equals(normalized)) {
                return new LoadResult(UiTheme.Mode.LIGHT, "");
            }
            if ("dark".equals(normalized)) {
                return new LoadResult(UiTheme.Mode.DARK, "");
            }
        }
        return new LoadResult(UiTheme.Mode.DARK,
            "Invalid or missing UI theme; using dark mode (expected light or dark)");
    }

    /** Replace the previous file only after a complete UTF-8 preference file has been written. */
    public static void save(Path file, UiTheme.Mode mode) throws IOException {
        Objects.requireNonNull(mode, "mode");
        Path target = file.toAbsolutePath().normalize();
        Path parent = target.getParent();
        Files.createDirectories(parent);
        Path temporary = Files.createTempFile(parent, "espetro-ui-", ".tmp");
        try {
            Properties properties = new Properties();
            properties.setProperty(THEME_KEY, mode.name().toLowerCase(Locale.ROOT));
            try (Writer writer = Files.newBufferedWriter(temporary, StandardCharsets.UTF_8)) {
                properties.store(writer, "Espetro local UI theme: light or dark");
            }
            try {
                Files.move(temporary, target,
                    StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException unsupported) {
                Files.move(temporary, target, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporary);
        }
    }
}
