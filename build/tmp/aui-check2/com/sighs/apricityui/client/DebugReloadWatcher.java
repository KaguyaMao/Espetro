/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.client;

import com.sighs.apricityui.ApricityUI;
import com.sighs.apricityui.config.ApricityUIConfig;
import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.loader.ClientLoader;
import com.sighs.apricityui.loader.Loader;
import com.sighs.apricityui.parser.HTML;
import com.sighs.apricityui.parser.ResourceUsageIndex;
import com.sighs.apricityui.resource.async.style.StyleAsyncHandler;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public final class DebugReloadWatcher {
    private static final long SCAN_INTERVAL_MS = 500L;
    private static final long RELOAD_THROTTLE_MS = 1000L;
    private static final Map<Path, Long> LAST_MODIFIED = new HashMap<Path, Long>();
    private static long lastScanMs = 0L;
    private static long lastReloadMs = 0L;

    private DebugReloadWatcher() {
    }

    public static void tick() {
        if (!((Boolean)ApricityUIConfig.CLIENT.debugAutoReload.get()).booleanValue()) {
            return;
        }
        long now = System.currentTimeMillis();
        if (now - lastScanMs < 500L) {
            return;
        }
        lastScanMs = now;
        List<Path> roots = Loader.getWatchRoots();
        if (roots.isEmpty()) {
            return;
        }
        for (Path root : roots) {
            DebugReloadWatcher.scanRoot(root, now);
        }
    }

    private static void scanRoot(Path root, long now) {
        try (Stream<Path> stream = Files.walk(root, new FileVisitOption[0]);){
            stream.filter(x$0 -> Files.isRegularFile(x$0, new LinkOption[0])).filter(DebugReloadWatcher::isWatchedExtension).forEach(path -> {
                try {
                    FileTime time = Files.getLastModifiedTime(path, new LinkOption[0]);
                    long lastModified = time.toMillis();
                    Long cached = LAST_MODIFIED.get(path);
                    if (cached == null) {
                        LAST_MODIFIED.put((Path)path, lastModified);
                        return;
                    }
                    if (lastModified != cached) {
                        LAST_MODIFIED.put((Path)path, lastModified);
                        DebugReloadWatcher.triggerReload(path, now);
                    }
                }
                catch (IOException exception) {
                    ApricityUI.LOGGER.warn("[DebugReload] failed to inspect file={}", path, (Object)exception);
                }
            });
        }
        catch (IOException exception) {
            ApricityUI.LOGGER.warn("[DebugReload] failed to scan root={}", (Object)root, (Object)exception);
        }
    }

    private static boolean isWatchedExtension(Path path) {
        String name = path.getFileName().toString().toLowerCase();
        return name.endsWith(".html") || name.endsWith(".css") || name.endsWith(".js");
    }

    private static void triggerReload(Path path, long now) {
        if (now - lastReloadMs < 1000L) {
            return;
        }
        lastReloadMs = now;
        ApricityUI.LOGGER.info("[DebugReload] change detected: {}", (Object)path.toAbsolutePath());
        String logicalPath = DebugReloadWatcher.toLogicalPath(path);
        if (logicalPath != null) {
            boolean handled;
            boolean bl = handled = path.getFileName().toString().toLowerCase().endsWith(".html") ? DebugReloadWatcher.reloadTemplate(logicalPath) : DebugReloadWatcher.refreshAffectedDocuments(logicalPath);
            if (handled) {
                return;
            }
        }
        ClientLoader.reload();
        ApricityUI.LOGGER.info("[DebugReload] reload completed");
    }

    private static boolean reloadTemplate(String logicalPath) {
        boolean isNew;
        boolean bl = isNew = HTML.getTemple(logicalPath) == null;
        if (!HTML.reload(logicalPath)) {
            return false;
        }
        if (isNew) {
            ClientLoader.invalidateStaticResourceCache();
            ApricityUI.LOGGER.info("[DebugReload] template registered: {}", (Object)logicalPath);
            return true;
        }
        int refreshed = DebugReloadWatcher.refreshDocumentsOf(Set.of(logicalPath), false);
        ApricityUI.LOGGER.info("[DebugReload] template reloaded: {} ({} document(s) refreshed)", (Object)logicalPath, (Object)refreshed);
        return true;
    }

    private static boolean refreshAffectedDocuments(String logicalPath) {
        Set<String> templates;
        boolean stylesOnly = logicalPath.endsWith(".css");
        Set<String> set = templates = logicalPath.equals("global.css") || logicalPath.equals("global.js") ? null : ResourceUsageIndex.affectedTemplates(logicalPath);
        if (stylesOnly) {
            StyleAsyncHandler.INSTANCE.invalidatePreparedStylesheets();
        } else if (!logicalPath.equals("global.js")) {
            HTML.invalidatePreparedTemplates(templates);
        }
        int refreshed = DebugReloadWatcher.refreshDocumentsOf(templates, stylesOnly);
        ApricityUI.LOGGER.info("[DebugReload] resource changed: {} ({} document(s) {})", new Object[]{logicalPath, refreshed, stylesOnly ? "restyled" : "refreshed"});
        return true;
    }

    private static int refreshDocumentsOf(Set<String> templates, boolean stylesOnly) {
        int refreshed = 0;
        for (Document document : Document.getAll()) {
            if (document == null || document.isDisposed() || document.isReloadPersistent() || templates != null && !templates.contains(document.getPath())) continue;
            if (stylesOnly) {
                document.refreshStyles();
            } else {
                document.refresh();
            }
            ++refreshed;
        }
        return refreshed;
    }

    private static String toLogicalPath(Path path) {
        Path absolute = path.toAbsolutePath().normalize();
        for (Path root : Loader.getWatchRoots()) {
            Path normalizedRoot = root.toAbsolutePath().normalize();
            if (!absolute.startsWith(normalizedRoot)) continue;
            return normalizedRoot.relativize(absolute).toString().replace("\\", "/");
        }
        return null;
    }
}

