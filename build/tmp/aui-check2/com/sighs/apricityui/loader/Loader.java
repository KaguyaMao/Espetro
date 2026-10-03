/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.loader;

import com.sighs.apricityui.ApricityUI;
import com.sighs.apricityui.spi.AuiServices;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Stack;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

public class Loader {
    private static final String DEV_ASSET_ROOT = "src/main/resources/assets/apricityui/apricity";
    private static final String COMMON_DEV_ASSET_ROOT = "common/src/main/resources/assets/apricityui/apricity";
    protected final String extension;
    protected int loadedResourceCount;
    protected BiConsumer<String, String> handler = (key, content) -> {};

    protected Loader(String extension) {
        this.extension = extension;
    }

    public static InputStream getResourceStream(String path) {
        if (path == null || path.isEmpty()) {
            return null;
        }
        try {
            String normalizedPath = path.startsWith("/") ? path.substring(1) : path;
            for (Path devRoot : Loader.getDevResourceRoots()) {
                Path devPath = devRoot.resolve(normalizedPath).normalize();
                if (!Files.exists(devPath, new LinkOption[0]) || !Files.isRegularFile(devPath, new LinkOption[0])) continue;
                return Files.newInputStream(devPath, new OpenOption[0]);
            }
            for (Path projectRoot : Loader.getDevProjectRoots()) {
                for (Path candidate : Loader.buildProjectRootCandidates(projectRoot, normalizedPath)) {
                    if (!Files.exists(candidate, new LinkOption[0]) || !Files.isRegularFile(candidate, new LinkOption[0])) continue;
                    return Files.newInputStream(candidate, new OpenOption[0]);
                }
            }
            Path local = Loader.getGameDir().resolve("apricity/" + normalizedPath);
            if (Files.exists(local, new LinkOption[0]) && Files.isRegularFile(local, new LinkOption[0])) {
                return Files.newInputStream(local, new OpenOption[0]);
            }
        }
        catch (IOException exception) {
            ApricityUI.LOGGER.warn("[AUI Resource] filesystem resource read failed path={}", (Object)path, (Object)exception);
        }
        InputStream bundled = Loader.class.getClassLoader().getResourceAsStream("assets/apricityui/apricity/" + (path.startsWith("/") ? path.substring(1) : path));
        if (bundled != null) {
            return bundled;
        }
        return null;
    }

    public static boolean isRemotePath(String path) {
        if (path == null) {
            return false;
        }
        String trimmed = path.trim();
        return trimmed.regionMatches(true, 0, "https://", 0, "https://".length());
    }

    public static String resolve(String context, String raw) {
        if (raw == null) {
            return "";
        }
        String trimmedRaw = raw.trim();
        if (trimmedRaw.isEmpty()) {
            return "";
        }
        if (Loader.isRemotePath(trimmedRaw)) {
            return trimmedRaw;
        }
        if (trimmedRaw.startsWith("/")) {
            return trimmedRaw.substring(1);
        }
        String safeContext = context == null ? "" : context;
        String base = safeContext.contains("/") ? safeContext.substring(0, safeContext.lastIndexOf(47)) : "";
        String[] parts = (base + "/" + trimmedRaw).split("/");
        Stack<String> stack = new Stack<String>();
        for (String part : parts) {
            if (part.isEmpty() || part.equals(".")) continue;
            if (part.equals("..")) {
                if (stack.isEmpty()) continue;
                stack.pop();
                continue;
            }
            stack.push(part);
        }
        return String.join((CharSequence)"/", stack);
    }

    static List<Path> getDevResourceRoots() {
        Path gameDir = Loader.getGameDir();
        LinkedHashSet<Path> candidates = new LinkedHashSet<Path>();
        Path base = gameDir;
        for (int depth = 0; depth <= 6 && base != null; base = base.getParent(), ++depth) {
            for (String relativeRoot : List.of(DEV_ASSET_ROOT, COMMON_DEV_ASSET_ROOT)) {
                Path candidate = base.resolve(relativeRoot).normalize();
                if (!Files.exists(candidate, new LinkOption[0]) || !Files.isDirectory(candidate, new LinkOption[0])) continue;
                candidates.add(candidate);
            }
        }
        ArrayList<Path> roots = new ArrayList<Path>(candidates);
        roots.sort(Comparator.comparingInt(path -> Loader.distanceFrom(gameDir, path)).reversed());
        return roots;
    }

    public static Path getPrimaryDevResourceRoot() {
        List<Path> roots = Loader.getDevResourceRoots();
        return roots.isEmpty() ? null : roots.get(0);
    }

    static List<Path> getDevProjectRoots() {
        Path gameDir = Loader.getGameDir();
        LinkedHashSet<Path> candidates = new LinkedHashSet<Path>();
        Iterator<Path> iterator = Loader.getDevResourceRoots().iterator();
        block0: while (iterator.hasNext()) {
            Path devRoot;
            Path current = devRoot = iterator.next();
            for (int depth = 0; depth <= 8 && current != null; current = current.getParent(), ++depth) {
                if (!Loader.isProjectRoot(current)) continue;
                candidates.add(current);
                continue block0;
            }
        }
        Path base = gameDir;
        for (int depth = 0; depth <= 8 && base != null; base = base.getParent(), ++depth) {
            if (!Loader.isProjectRoot(base)) continue;
            candidates.add(base);
        }
        return new ArrayList<Path>(candidates);
    }

    static List<Path> buildProjectRootCandidates(Path projectRoot, String normalizedPath) {
        ArrayList<Path> candidates = new ArrayList<Path>();
        if (projectRoot == null || normalizedPath == null || normalizedPath.isBlank()) {
            return candidates;
        }
        String[] parts = normalizedPath.replace("\\", "/").split("/");
        for (int i = 0; i < parts.length; ++i) {
            String candidatePath = String.join((CharSequence)"/", Arrays.copyOfRange(parts, i, parts.length));
            if (candidatePath.isBlank()) continue;
            candidates.add(projectRoot.resolve(candidatePath).normalize());
        }
        return candidates;
    }

    static void loadFilesystemStaticResources(Map<String, StaticResourceEntry> merged) {
        Loader.loadLocalFolderEntries(merged);
        Loader.loadDevFolderEntries(merged);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    static String readGlobalCSS() {
        try (InputStream stream = Loader.getResourceStream("global.css");){
            if (stream == null) return null;
            String string = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            return string;
        }
        catch (IOException exception) {
            ApricityUI.LOGGER.warn("[AUI Resource] failed to read global.css", (Throwable)exception);
        }
        return null;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static String readGlobalJS() {
        try (InputStream stream = Loader.getResourceStream("global.js");){
            if (stream == null) return null;
            String string = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            return string;
        }
        catch (IOException exception) {
            ApricityUI.LOGGER.warn("[AUI Resource] failed to read global.js", (Throwable)exception);
        }
        return null;
    }

    private static boolean isProjectRoot(Path path) {
        if (path == null) {
            return false;
        }
        return Files.exists(path.resolve("build.gradle"), new LinkOption[0]) || Files.exists(path.resolve("settings.gradle"), new LinkOption[0]) || Files.exists(path.resolve(".git"), new LinkOption[0]);
    }

    static String extensionOf(String path) {
        if (path == null) {
            return "";
        }
        int idx = path.lastIndexOf(46);
        if (idx < 0 || idx == path.length() - 1) {
            return "";
        }
        return path.substring(idx + 1).toLowerCase(Locale.ROOT);
    }

    static String safe(String value) {
        return value == null ? "" : value;
    }

    public static List<Path> getWatchRoots() {
        ArrayList<Path> roots = new ArrayList<Path>(Loader.getDevResourceRoots());
        Path localRoot = Loader.getGameDir().resolve("apricity").toAbsolutePath().normalize();
        if (Files.exists(localRoot, new LinkOption[0]) && Files.isDirectory(localRoot, new LinkOption[0])) {
            roots.add(localRoot);
        }
        return roots;
    }

    protected void loadFromLocalFolder() {
        Path root = Loader.getGameDir().resolve("apricity");
        try {
            if (!Files.exists(root, new LinkOption[0])) {
                Files.createDirectories(root, new FileAttribute[0]);
                return;
            }
            try (Stream<Path> paths = Files.walk(root, new FileVisitOption[0]);){
                paths.filter(x$0 -> Files.isRegularFile(x$0, new LinkOption[0])).filter(path -> path.toString().endsWith("." + this.extension)).forEach(path -> {
                    try {
                        String content = Files.readString(path, StandardCharsets.UTF_8);
                        String relPath = root.relativize((Path)path).toString().replace("\\", "/");
                        this.handler.accept(relPath, content);
                        ++this.loadedResourceCount;
                    }
                    catch (IOException exception) {
                        ApricityUI.LOGGER.error("[AUI Resource] failed to read local .{} file={}", new Object[]{this.extension, path, exception});
                    }
                });
            }
        }
        catch (IOException exception) {
            ApricityUI.LOGGER.error("[AUI Resource] failed to scan local resource root={}", (Object)root, (Object)exception);
        }
    }

    protected void loadFromDevFolders() {
        List<Path> devRoots = Loader.getDevResourceRoots();
        if (devRoots.isEmpty()) {
            return;
        }
        ArrayList<Path> loadOrder = new ArrayList<Path>(devRoots);
        Collections.reverse(loadOrder);
        for (Path root : loadOrder) {
            this.loadFromRootFolder(root);
        }
    }

    protected void loadFromRootFolder(Path root) {
        try {
            if (!Files.exists(root, new LinkOption[0])) {
                return;
            }
            try (Stream<Path> paths = Files.walk(root, new FileVisitOption[0]);){
                paths.filter(x$0 -> Files.isRegularFile(x$0, new LinkOption[0])).filter(path -> path.toString().endsWith("." + this.extension)).forEach(path -> {
                    try {
                        String content = Files.readString(path, StandardCharsets.UTF_8);
                        String relPath = root.relativize((Path)path).toString().replace("\\", "/");
                        this.handler.accept(relPath, content);
                        ++this.loadedResourceCount;
                    }
                    catch (IOException exception) {
                        ApricityUI.LOGGER.error("[AUI Resource] failed to read dev .{} file={}", new Object[]{this.extension, path, exception});
                    }
                });
            }
        }
        catch (IOException exception) {
            ApricityUI.LOGGER.error("[AUI Resource] failed to scan dev resource root={}", (Object)root, (Object)exception);
        }
    }

    private static void loadLocalFolderEntries(Map<String, StaticResourceEntry> merged) {
        Path root = Loader.getGameDir().resolve("apricity").toAbsolutePath().normalize();
        Loader.loadFromRootEntries(merged, root, ResourceLayer.LOCAL_FOLDER, root.toString(), root.toString());
    }

    private static void loadDevFolderEntries(Map<String, StaticResourceEntry> merged) {
        List<Path> devRoots = Loader.getDevResourceRoots();
        if (devRoots.isEmpty()) {
            return;
        }
        ArrayList<Path> loadOrder = new ArrayList<Path>(devRoots);
        Collections.reverse(loadOrder);
        for (Path root : loadOrder) {
            String sourceRoot = root.toAbsolutePath().normalize().toString();
            Loader.loadFromRootEntries(merged, root, ResourceLayer.DEV_FOLDER, sourceRoot, sourceRoot);
        }
    }

    private static void loadFromRootEntries(Map<String, StaticResourceEntry> merged, Path root, ResourceLayer layer, String sourceRoot, String sourceDetail) {
        try {
            if (!Files.exists(root, new LinkOption[0]) || !Files.isDirectory(root, new LinkOption[0])) {
                return;
            }
            try (Stream<Path> paths = Files.walk(root, new FileVisitOption[0]);){
                paths.filter(x$0 -> Files.isRegularFile(x$0, new LinkOption[0])).forEach(path -> {
                    try {
                        String relPath = root.relativize((Path)path).toString().replace("\\", "/");
                        if (relPath.isBlank()) {
                            return;
                        }
                        long size = Files.size(path);
                        merged.put(relPath, new StaticResourceEntry(relPath, Loader.extensionOf(relPath), layer, sourceRoot, sourceDetail, size));
                    }
                    catch (IOException exception) {
                        ApricityUI.LOGGER.warn("[AUI Resource] failed to inspect static resource file={}", path, (Object)exception);
                    }
                });
            }
        }
        catch (IOException exception) {
            ApricityUI.LOGGER.warn("[AUI Resource] failed to enumerate static resources root={}", (Object)root, (Object)exception);
        }
    }

    private static int distanceFrom(Path gameDir, Path root) {
        try {
            Path parent = root.getParent();
            if (parent == null) {
                return Integer.MAX_VALUE;
            }
            return parent.getNameCount() - gameDir.getNameCount();
        }
        catch (Exception ignored) {
            return Integer.MAX_VALUE;
        }
    }

    private static Path getGameDir() {
        Path dir = AuiServices.client().getGameDirectory();
        return dir != null ? dir : Path.of("", new String[0]).toAbsolutePath().normalize();
    }

    public static enum ResourceLayer {
        RESOURCE_PACK,
        LOCAL_FOLDER,
        DEV_FOLDER;

    }

    public record StaticResourceEntry(String path, String extension, ResourceLayer layer, String sourceRoot, String sourceDetail, long sizeBytes) {
    }
}

