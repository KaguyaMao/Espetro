/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 */
package org.espetro.mapconfig;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.CopyOption;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;
import org.espetro.Espetro;

public final class ExampleContentInstaller {
    private static final String RESOURCE_ROOT = "/espetro_examples/";
    private static final String MANIFEST = "manifest.json";
    private static final Gson GSON = new Gson();

    private ExampleContentInstaller() {
    }

    public static Result installMissing(Path gameDir) {
        int installed = 0;
        int skipped = 0;
        ArrayList<Object> errors = new ArrayList<Object>();
        Path normalizedGameDir = gameDir.toAbsolutePath().normalize();
        try {
            Manifest manifest = ExampleContentInstaller.readManifest();
            if (manifest.files == null || manifest.files.isEmpty()) {
                return new Result(0, 0, List.of("\u793a\u4f8b\u5185\u5bb9\u6e05\u5355\u4e3a\u7a7a"));
            }
            boolean bl = ExampleContentInstaller.containsFactionJson(normalizedGameDir.resolve("EsFactions"));
            for (String relative : manifest.files) {
                if (!ExampleContentInstaller.isSafeRelativePath(relative)) {
                    errors.add("\u793a\u4f8b\u6e05\u5355\u5305\u542b\u975e\u6cd5\u8def\u5f84: " + relative);
                    continue;
                }
                if (bl && relative.startsWith("EsFactions/")) {
                    ++skipped;
                    continue;
                }
                Path target = normalizedGameDir.resolve(relative).normalize();
                if (!target.startsWith(normalizedGameDir) || target.equals(normalizedGameDir)) {
                    errors.add("\u793a\u4f8b\u8def\u5f84\u8d8a\u754c: " + relative);
                    continue;
                }
                if (Files.exists(target, new LinkOption[0])) {
                    ++skipped;
                    continue;
                }
                Files.createDirectories(target.getParent(), new FileAttribute[0]);
                try {
                    InputStream input = ExampleContentInstaller.openResource(relative);
                    try {
                        try {
                            Files.copy(input, target, new CopyOption[0]);
                            ++installed;
                        }
                        catch (FileAlreadyExistsException ignored) {
                            ++skipped;
                        }
                    }
                    finally {
                        if (input == null) continue;
                        input.close();
                    }
                }
                catch (IOException e) {
                    errors.add(relative + ": " + e.getMessage());
                }
            }
            Path testMap = normalizedGameDir.resolve("EsWorld/test_flat");
            for (String directory : List.of("region", "entities", "poi", "data")) {
                Files.createDirectories(testMap.resolve(directory), new FileAttribute[0]);
            }
        }
        catch (Exception e) {
            errors.add(e.getMessage());
        }
        if (installed > 0) {
            Espetro.LOGGER.info("\u5df2\u5b89\u5168\u5bfc\u51fa Espetro \u793a\u4f8b\u5185\u5bb9: \u65b0\u589e {}, \u8df3\u8fc7 {}", (Object)installed, (Object)skipped);
        }
        for (String string : errors) {
            Espetro.LOGGER.error("[\u793a\u4f8b\u5bfc\u51fa] {}", (Object)string);
        }
        return new Result(installed, skipped, List.copyOf(errors));
    }

    private static boolean containsFactionJson(Path factionsDir) throws IOException {
        if (!Files.isDirectory(factionsDir, new LinkOption[0])) {
            return false;
        }
        try (Stream<Path> files = Files.list(factionsDir);){
            boolean bl = files.anyMatch(path -> Files.isRegularFile(path, new LinkOption[0]) && path.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".json"));
            return bl;
        }
    }

    private static Manifest readManifest() throws IOException {
        try (InputStream input = ExampleContentInstaller.class.getResourceAsStream("/espetro_examples/manifest.json");){
            if (input == null) {
                throw new IOException("JAR \u7f3a\u5c11 /espetro_examples/manifest.json");
            }
            Manifest manifest = (Manifest)GSON.fromJson((Reader)new InputStreamReader(input, StandardCharsets.UTF_8), Manifest.class);
            if (manifest == null) {
                throw new IOException("\u793a\u4f8b\u5185\u5bb9\u6e05\u5355\u65e0\u6cd5\u89e3\u6790");
            }
            Manifest manifest2 = manifest;
            return manifest2;
        }
    }

    private static InputStream openResource(String relative) throws IOException {
        InputStream input = ExampleContentInstaller.class.getResourceAsStream(RESOURCE_ROOT + relative.replace('\\', '/'));
        if (input == null) {
            throw new IOException("JAR \u7f3a\u5c11\u793a\u4f8b\u8d44\u6e90");
        }
        return input;
    }

    static boolean isSafeRelativePath(String relative) {
        Path path;
        if (relative == null || relative.isBlank()) {
            return false;
        }
        try {
            path = Path.of(relative, new String[0]);
        }
        catch (Exception e) {
            return false;
        }
        return !path.isAbsolute() && !relative.contains("\\") && !relative.contains("\u0000") && path.normalize().equals(path) && !relative.startsWith(".");
    }

    private static final class Manifest {
        int version;
        List<String> files;

        private Manifest() {
        }
    }

    public record Result(int installed, int skipped, List<String> errors) {
        public boolean successful() {
            return this.errors.isEmpty();
        }
    }
}

