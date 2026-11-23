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
    private static final String COMPASS_SIZE_KEY = "compass.size";
    static final float DEFAULT_COMPASS_SIZE = 1f;

    private ClientUiPreferences() {
    }

    public record LoadResult(UiTheme.Mode mode, String warning, float compassSize) {
        public LoadResult(UiTheme.Mode mode, String warning) {
            this(mode, warning, DEFAULT_COMPASS_SIZE);
        }
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
            LoadResult theme = parse(properties.getProperty(THEME_KEY));
            float size = parseCompassSize(properties.getProperty(COMPASS_SIZE_KEY, "1"));
            return new LoadResult(theme.mode(), theme.warning(),
                Float.isNaN(size) ? DEFAULT_COMPASS_SIZE : size);
        } catch (IOException | IllegalArgumentException | SecurityException failure) {
            return new LoadResult(UiTheme.Mode.DARK,
                "Could not read UI preferences " + file + ": " + failure);
        }
    }

    static float parseCompassSize(String value) {
        String normalized = value.strip().toLowerCase(Locale.ROOT);
        if (normalized.endsWith("x") || normalized.endsWith("×")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        try {
            float size = Float.parseFloat(normalized);
            return size >= 0.5f && size <= 2f ? size : Float.NaN;
        } catch (NumberFormatException invalid) {
            return Float.NaN;
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
        save(file, mode, DEFAULT_COMPASS_SIZE);
    }

    static void save(Path file, UiTheme.Mode mode, float compassSize) throws IOException {
        Objects.requireNonNull(mode, "mode");
        Path target = file.toAbsolutePath().normalize();
        Path parent = target.getParent();
        Files.createDirectories(parent);
        Path temporary = Files.createTempFile(parent, "espetro-ui-", ".tmp");
        try {
            Properties properties = new Properties();
            properties.setProperty(THEME_KEY, mode.name().toLowerCase(Locale.ROOT));
            properties.setProperty(COMPASS_SIZE_KEY, Float.toString(compassSize));
            try (Writer writer = Files.newBufferedWriter(temporary, StandardCharsets.UTF_8)) {
                properties.store(writer, "Espetro local UI preferences");
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
