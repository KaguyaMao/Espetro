package org.espetro.client.ui;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.HexFormat;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

/** Pure file installer: safe to call before Minecraft and FancyMenu initialize. */
public final class BundledMenuInstaller {
    private static final String BASE = "assets/espetro/ui-bundle/";
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(BundledMenuInstaller.class);
    private static boolean installed;

    private BundledMenuInstaller() { }

    /** Early mixin-config callback installs before mod constructors read layouts. */
    public static synchronized void installForClient(Path gameDirectory) {
        if (installed) return;
        try {
            Result result = install(gameDirectory, name -> {
                InputStream stream = BundledMenuInstaller.class.getClassLoader().getResourceAsStream(BASE + name);
                if (stream == null) throw new IOException("Missing bundled menu resource: " + name);
                return stream;
            });
            installed = true;
            LOGGER.info("Espetro menu bundle: {} files updated; previous files saved at {}",
                result.updatedFiles(), result.backupDirectory());
        } catch (IOException | RuntimeException failure) {
            // Keep the previously installed menu usable when a disk is full/read-only.
            LOGGER.error("Unable to install Espetro menu bundle; previous menu files retained", failure);
        }
    }

    @FunctionalInterface
    interface Resources { InputStream open(String name) throws IOException; }
    record Entry(String hash, long size, String path) { }
    public record Result(int updatedFiles, Path backupDirectory) { }

    static Result install(Path gameDirectory, Resources resources) throws IOException {
        return install(gameDirectory, resources, path -> path.startsWith("config/fancymenu/"));
    }

    /** Shared staging/verification/backup transaction with a caller-specific path scope. */
    static Result install(Path gameDirectory, Resources resources, Predicate<String> allowedPath) throws IOException {
        Files.createDirectories(gameDirectory);
        Path root = gameDirectory.toRealPath();
        List<Entry> entries;
        try (InputStream stream = resources.open("manifest.tsv")) {
            entries = parse(new String(stream.readAllBytes(), StandardCharsets.UTF_8), allowedPath);
        }
        List<Entry> changed = new ArrayList<>();
        for (Entry entry : entries) {
            Path target = checked(root, entry.path());
            if (!Files.isRegularFile(target, LinkOption.NOFOLLOW_LINKS)
                || Files.size(target) != entry.size() || !digest(target).equals(entry.hash())) changed.add(entry);
        }
        if (changed.isEmpty()) return new Result(0, null);

        Path stage = Files.createTempDirectory(root, ".espetro-menu-stage-");
        Path backup = null;
        try {
            // Verify every changed payload before replacing any live configuration.
            for (Entry entry : changed) {
                Path temporary = stage.resolve(entry.path());
                Files.createDirectories(temporary.getParent());
                try (InputStream stream = resources.open("files/" + entry.path())) {
                    Files.copy(stream, temporary);
                }
                if (Files.size(temporary) != entry.size() || !digest(temporary).equals(entry.hash())) {
                    throw new IOException("Bundled menu integrity failure: " + entry.path());
                }
            }
            List<Entry> replaced = new ArrayList<>();
            for (Entry entry : changed) {
                Path target = checked(root, entry.path());
                if (Files.exists(target, LinkOption.NOFOLLOW_LINKS)) {
                    if (!Files.isRegularFile(target, LinkOption.NOFOLLOW_LINKS)) {
                        throw new IOException("Menu target is not a regular file: " + entry.path());
                    }
                    if (backup == null) {
                        String stamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
                        backup = checked(root, "espetro-backups/" + stamp + "-menu-auto-" + UUID.randomUUID());
                        Files.createDirectories(backup);
                    }
                    Path previous = backup.resolve(entry.path());
                    Files.createDirectories(previous.getParent());
                    Files.copy(target, previous);
                    replaced.add(entry);
                }
            }
            List<Entry> committed = new ArrayList<>();
            try {
                for (Entry entry : changed) {
                    Path target = checked(root, entry.path());
                    Files.createDirectories(target.getParent());
                    replace(stage.resolve(entry.path()), target);
                    committed.add(entry);
                }
            } catch (IOException failure) {
                // Avoid leaving a mixture of new and old layouts after a failed write.
                for (Entry entry : committed) {
                    try {
                        Path target = checked(root, entry.path());
                        if (replaced.contains(entry)) Files.copy(backup.resolve(entry.path()), target,
                            StandardCopyOption.REPLACE_EXISTING);
                        else Files.deleteIfExists(target);
                    } catch (IOException rollback) { failure.addSuppressed(rollback); }
                }
                throw failure;
            }
            if (backup != null) {
                Files.writeString(backup.resolve("README.txt"),
                    "Espetro automatically backed up these UI files before updating.\n"
                    + "Close Minecraft before restoring the saved paths to the game directory.\n", StandardCharsets.UTF_8);
            }
            return new Result(changed.size(), backup);
        } finally {
            try (var paths = Files.walk(stage)) {
                for (Path path : paths.sorted(Comparator.reverseOrder()).toList()) Files.deleteIfExists(path);
            }
        }
    }

    static List<Entry> parse(String manifest) throws IOException {
        return parse(manifest, path -> path.startsWith("config/fancymenu/"));
    }

    private static List<Entry> parse(String manifest, Predicate<String> allowedPath) throws IOException {
        List<Entry> result = new ArrayList<>();
        Set<String> paths = new HashSet<>();
        for (String line : manifest.lines().toList()) {
            if (line.isBlank() || line.startsWith("#")) continue;
            String[] fields = line.split("\t", -1);
            if (fields.length != 3 || !fields[0].matches("[0-9a-f]{64}")
                || !fields[1].matches("[0-9]+") || !allowedPath.test(fields[2])
                || fields[2].contains("\\") || fields[2].contains(":")
                || fields[2].contains("//") || !paths.add(fields[2])) {
                throw new IOException("Invalid bundled menu manifest entry");
            }
            for (String segment : fields[2].split("/", -1)) {
                if (segment.isEmpty() || segment.equals(".") || segment.equals("..")) {
                    throw new IOException("Invalid bundled menu path");
                }
            }
            try { result.add(new Entry(fields[0], Long.parseLong(fields[1]), fields[2])); }
            catch (NumberFormatException failure) { throw new IOException("Invalid menu resource size", failure); }
        }
        if (result.isEmpty()) throw new IOException("Empty menu bundle manifest");
        return List.copyOf(result);
    }

    private static Path checked(Path root, String relative) throws IOException {
        Path resolved = root.resolve(relative).normalize();
        if (!resolved.startsWith(root)) throw new IOException("Menu path outside game directory");
        Path current = root;
        for (Path part : root.relativize(resolved)) {
            current = current.resolve(part);
            if (Files.isSymbolicLink(current)) throw new IOException("Symlink in menu path: " + current);
        }
        return resolved;
    }

    private static String digest(Path path) throws IOException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            try (InputStream stream = new DigestInputStream(Files.newInputStream(path), digest)) {
                stream.transferTo(java.io.OutputStream.nullOutputStream());
            }
            return HexFormat.of().formatHex(digest.digest());
        } catch (NoSuchAlgorithmException impossible) { throw new AssertionError(impossible); }
    }

    private static void replace(Path source, Path target) throws IOException {
        try { Files.move(source, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING); }
        catch (AtomicMoveNotSupportedException ignored) {
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
