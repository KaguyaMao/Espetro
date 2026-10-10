/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.resource.async.style;

import com.sighs.apricityui.ApricityUI;
import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.layout.Size;
import com.sighs.apricityui.loader.ClientLoader;
import com.sighs.apricityui.loader.Loader;
import com.sighs.apricityui.parser.CSS;
import com.sighs.apricityui.parser.ResourceUsageIndex;
import com.sighs.apricityui.parser.Selector;
import com.sighs.apricityui.render.FontDrawer;
import com.sighs.apricityui.resource.Font;
import com.sighs.apricityui.resource.async.network.NetworkAsyncHandler;
import com.sighs.apricityui.resource.async.style.StyleHandle;
import com.sighs.apricityui.task.AbstractAsyncHandler;
import com.sighs.apricityui.util.AuiLog;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class StyleAsyncHandler
extends AbstractAsyncHandler<ApplyTask> {
    public static final StyleAsyncHandler INSTANCE = new StyleAsyncHandler();
    private static final int MAX_IMPORT_DEPTH = 3;
    private static final Pattern COMMENT_PATTERN = Pattern.compile("/\\*.*?\\*/", 32);
    private static final Pattern IMPORT_PATTERN = Pattern.compile("(?i)@import\\s+(?:url\\s*\\(\\s*)?['\"]?([^'\"\\)\\s;]+)['\"]?\\s*\\)?\\s*;");
    private static final Pattern FONT_FACE_PATTERN = Pattern.compile("(?is)@font-face\\s*\\{(.*?)}");
    private static final Map<UUID, StyleHandle> HANDLES = new ConcurrentHashMap<UUID, StyleHandle>();
    private final Object globalCssCacheLock = new Object();
    private volatile GlobalCssCache globalCssCache;
    private final Map<ParsedCssCacheKey, ParsedCss> parsedCssCache = new ConcurrentHashMap<ParsedCssCacheKey, ParsedCss>();
    private final Map<ExternalCssCacheKey, ParsedCss> preparedExternalCss = new ConcurrentHashMap<ExternalCssCacheKey, ParsedCss>();

    private StyleAsyncHandler() {
        super("style", 256, 3, 1500000L, "ApricityUI-StyleWorker");
    }

    public void attach(Document document, String contextPath, List<String> externalStyleSrcs, List<String> inlineStyles) {
        if (document == null) {
            ApricityUI.LOGGER.error("[AUI CSS] cannot attach styles without document path={}", (Object)AuiLog.source(contextPath));
            return;
        }
        long generation = this.currentGeneration();
        StyleHandle handle = new StyleHandle(document.getUuid(), generation);
        StyleHandle old = HANDLES.put(document.getUuid(), handle);
        if (old != null) {
            old.markStale();
        }
        int order = 0;
        ParsedCss parsedGlobalCss = this.getGlobalCss(generation);
        if (parsedGlobalCss != null) {
            ParsedCss parsed = parsedGlobalCss;
            handle.putCssEntry(order++, new StyleHandle.CssEntry("global.css", parsed.cssText));
            this.enqueueFontLoads(handle, parsed.fontTasks);
        }
        if (inlineStyles != null) {
            for (String inlineCss : inlineStyles) {
                if (inlineCss == null || inlineCss.isBlank()) continue;
                ParsedCss parsed = this.parseCssCached(inlineCss, contextPath, generation);
                handle.putCssEntry(order++, new StyleHandle.CssEntry(contextPath, parsed.cssText));
                this.enqueueFontLoads(handle, parsed.fontTasks);
            }
        }
        if (externalStyleSrcs != null) {
            for (String src : externalStyleSrcs) {
                if (src == null || src.isBlank()) continue;
                String resolved = Loader.resolve(contextPath, src);
                if (resolved == null || resolved.isBlank()) {
                    ApricityUI.LOGGER.error("[AUI CSS] external stylesheet resolved to an empty path document={} src={}", (Object)AuiLog.source(contextPath), (Object)src);
                    continue;
                }
                int currentOrder = order++;
                ParsedCss prepared = this.preparedExternalCss.get(new ExternalCssCacheKey(generation, resolved));
                if (prepared != null) {
                    handle.putCssEntry(currentOrder, new StyleHandle.CssEntry(resolved, prepared.cssText));
                    this.enqueueFontLoads(handle, prepared.fontTasks);
                    continue;
                }
                handle.queueTask();
                this.submitWorker(() -> {
                    try {
                        String merged = this.loadCssWithImports(resolved, 0, new HashSet<String>());
                        ParsedCss parsed = this.parseCssCached(merged, resolved, generation);
                        this.enqueueApplyTask(new CssTask(handle, currentOrder, resolved, parsed.cssText, parsed.fontTasks));
                    }
                    catch (Exception exception) {
                        ApricityUI.LOGGER.error("[AUI CSS] external stylesheet load/parse failed document={} path={}", new Object[]{AuiLog.source(contextPath), resolved, exception});
                        this.enqueueApplyTask(new FailedTask(handle, resolved, "stylesheet", exception));
                    }
                }, rejected -> this.enqueueApplyTask(new FailedTask(handle, resolved, "stylesheet-worker", (Throwable)rejected)));
            }
        }
        this.rebuildCssCache(document, handle);
        handle.markReadyIfIdle();
    }

    @Override
    protected void applyOnMainThread(ApplyTask task, long currentGeneration) {
        if (task.handle().generation() != currentGeneration) {
            return;
        }
        StyleHandle current = HANDLES.get(task.handle().documentId());
        if (current != task.handle()) {
            return;
        }
        Document document = Document.getByUUID(task.handle().documentId().toString());
        if (document == null) {
            task.handle().completeTask(true);
            return;
        }
        task.handle().markApplying();
        if (task instanceof CssTask) {
            CssTask cssTask = (CssTask)task;
            try {
                task.handle().putCssEntry(cssTask.order, new StyleHandle.CssEntry(cssTask.contextPath, cssTask.cssText));
                this.enqueueFontLoads(task.handle(), cssTask.fontTasks);
                this.rebuildCssCache(document, task.handle());
                document.reapplyStylesFromCache();
                task.handle().completeTask(false);
            }
            catch (RuntimeException exception) {
                ApricityUI.LOGGER.error("[AUI CSS] applying stylesheet failed document={} path={}", new Object[]{document.getPath(), cssTask.contextPath, exception});
                task.handle().completeTask(true);
                throw exception;
            }
            return;
        }
        if (task instanceof FontTask) {
            FontTask fontTask = (FontTask)task;
            boolean loaded = this.registerFont(fontTask);
            if (loaded) {
                FontDrawer.clearCache();
                document.invalidateFontMetrics();
            } else {
                ApricityUI.LOGGER.error("[AUI CSS] web font registration failed document={} family={} path={}", new Object[]{document.getPath(), fontTask.family, fontTask.path});
            }
            task.handle().completeTask(!loaded);
            return;
        }
        if (task instanceof FailedTask) {
            FailedTask failedTask = (FailedTask)task;
            ApricityUI.LOGGER.error("[AUI CSS] async style task failed document={} kind={} path={}", new Object[]{document.getPath(), failedTask.kind, failedTask.path, failedTask.error});
            task.handle().completeTask(true);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void onBeforeClear(long nextGeneration) {
        Iterator<StyleHandle> iterator = this.globalCssCacheLock;
        synchronized (iterator) {
            this.globalCssCache = null;
        }
        this.parsedCssCache.clear();
        this.preparedExternalCss.clear();
        for (StyleHandle handle : HANDLES.values()) {
            handle.markStale();
        }
        HANDLES.clear();
    }

    public void warmUpGlobalCss() {
        this.getGlobalCss(this.currentGeneration());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void invalidatePreparedStylesheets() {
        Object object = this.globalCssCacheLock;
        synchronized (object) {
            this.globalCssCache = null;
        }
        this.parsedCssCache.clear();
        this.preparedExternalCss.clear();
        CSS.clearCompiledStylesheets();
        Selector.clearCompiledCache();
    }

    public int warmUpTemplateStyles(String contextPath, List<String> externalStyleSrcs, List<String> inlineStyles, Size viewport) {
        long generation = this.currentGeneration();
        int warmed = 0;
        ParsedCss global = this.getGlobalCss(generation);
        if (global != null) {
            CSS.warmUp(global.cssText, "global.css", viewport);
            ++warmed;
        }
        if (inlineStyles != null) {
            for (String inlineCss : inlineStyles) {
                if (inlineCss == null || inlineCss.isBlank()) continue;
                ParsedCss parsed = this.parseCssCached(inlineCss, contextPath, generation);
                CSS.warmUp(parsed.cssText, contextPath, viewport);
                ++warmed;
            }
        }
        if (externalStyleSrcs != null) {
            for (String src : externalStyleSrcs) {
                String resolved;
                if (src == null || src.isBlank() || (resolved = Loader.resolve(contextPath, src)) == null || resolved.isBlank() || Loader.isRemotePath(resolved)) continue;
                try {
                    ExternalCssCacheKey key = new ExternalCssCacheKey(generation, resolved);
                    ParsedCss parsed = this.preparedExternalCss.get(key);
                    if (parsed == null) {
                        String merged = this.loadCssWithImports(resolved, 0, new HashSet<String>());
                        parsed = this.parseCssCached(merged, resolved, generation);
                        this.preparedExternalCss.put(key, parsed);
                    }
                    CSS.warmUp(parsed.cssText, resolved, viewport);
                    ++warmed;
                }
                catch (IOException | RuntimeException exception) {
                    ApricityUI.LOGGER.warn("[AUI CSS] stylesheet warm-up failed; create will load lazily document={} path={}", new Object[]{AuiLog.source(contextPath), resolved, exception});
                }
            }
        }
        return warmed;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private ParsedCss getGlobalCss(long generation) {
        GlobalCssCache cached = this.globalCssCache;
        if (cached != null && cached.generation == generation) {
            return cached.parsed;
        }
        Object object = this.globalCssCacheLock;
        synchronized (object) {
            cached = this.globalCssCache;
            if (cached != null && cached.generation == generation) {
                return cached.parsed;
            }
            String globalCss = ClientLoader.readGlobalCSS();
            ParsedCss parsed = globalCss == null || globalCss.isBlank() ? null : this.parseCssCached(globalCss, "global.css", generation);
            this.globalCssCache = new GlobalCssCache(generation, parsed);
            return parsed;
        }
    }

    private void rebuildCssCache(Document document, StyleHandle handle) {
        document.CSSCache.clear();
        document.CSSDebugRules.clear();
        int order = 0;
        Size viewport = new Size(document.getViewport().layoutWidth(), document.getViewport().layoutHeight());
        for (Map.Entry<Integer, StyleHandle.CssEntry> entry : handle.snapshotCssEntries()) {
            StyleHandle.CssEntry cssEntry = entry.getValue();
            order = CSS.readCSS(cssEntry.cssText(), document.CSSCache, document.CSSDebugRules, cssEntry.contextPath(), order, viewport);
        }
        document.rebuildSelectorIndex();
    }

    public void handleViewportChange(Document document) {
        if (document == null || document.documentElement == null) {
            return;
        }
        StyleHandle handle = HANDLES.get(document.getUuid());
        if (handle == null || handle.state() == AbstractAsyncHandler.AsyncState.STALE) {
            return;
        }
        this.rebuildCssCache(document, handle);
        document.reapplyStylesFromCache();
    }

    private boolean registerFont(FontTask fontTask) {
        boolean bl;
        ByteArrayInputStream stream = new ByteArrayInputStream(fontTask.bytes);
        try {
            bl = Font.registerFont(fontTask.family, stream);
        }
        catch (Throwable throwable) {
            try {
                try {
                    stream.close();
                }
                catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
            catch (IOException exception) {
                ApricityUI.LOGGER.error("[AUI CSS] failed to close/load web font family={} path={}", new Object[]{fontTask.family, fontTask.path, exception});
                return false;
            }
        }
        stream.close();
        return bl;
    }

    private void enqueueFontLoads(StyleHandle handle, List<FontSource> fontSources) {
        if (fontSources == null || fontSources.isEmpty()) {
            return;
        }
        for (FontSource source : fontSources) {
            String key;
            if (source == null || source.family.isBlank() || source.path.isBlank() || !handle.tryReserveFont(key = source.family + "|" + source.path)) continue;
            handle.queueTask();
            this.submitWorker(() -> {
                try {
                    byte[] bytes = this.fetchBytes(source.path);
                    this.enqueueApplyTask(new FontTask(handle, source.family, source.path, bytes));
                }
                catch (Exception exception) {
                    ApricityUI.LOGGER.error("[AUI CSS] web font resource load failed family={} path={}", new Object[]{source.family, source.path, exception});
                    this.enqueueApplyTask(new FailedTask(handle, source.path, "font", exception));
                }
            }, rejected -> this.enqueueApplyTask(new FailedTask(handle, source.path, "font-worker", (Throwable)rejected)));
        }
    }

    private String loadCssWithImports(String path, int depth, Set<String> visited) throws IOException {
        if (path == null || path.isBlank()) {
            ApricityUI.LOGGER.error("[AUI CSS] @import resolved to an empty path depth={}", (Object)depth);
            return "";
        }
        if (depth > 3) {
            ApricityUI.LOGGER.warn("[AUI CSS] @import depth limit reached path={} depth={}", (Object)path, (Object)depth);
            return "";
        }
        String normalized = path.trim();
        if (!visited.add(normalized)) {
            ApricityUI.LOGGER.warn("[AUI CSS] cyclic @import ignored path={}", (Object)normalized);
            return "";
        }
        byte[] bytes = this.fetchBytes(normalized);
        String css = new String(bytes, StandardCharsets.UTF_8);
        List<String> imports = this.extractImports(css);
        String cssWithoutImports = this.stripImports(css);
        StringBuilder merged = new StringBuilder();
        if (depth < 3) {
            for (String importPath : imports) {
                String resolved = Loader.resolve(normalized, importPath);
                if (resolved == null || resolved.isBlank()) continue;
                ResourceUsageIndex.recordImport(normalized, resolved);
                try {
                    String imported = this.loadCssWithImports(resolved, depth + 1, visited);
                    if (imported.isBlank()) continue;
                    merged.append(imported).append('\n');
                }
                catch (IOException exception) {
                    ApricityUI.LOGGER.error("[AUI CSS] imported stylesheet failed parent={} import={}", new Object[]{normalized, resolved, exception});
                }
            }
        }
        merged.append(cssWithoutImports);
        return merged.toString();
    }

    private byte[] fetchBytes(String path) throws IOException {
        if (Loader.isRemotePath(path)) {
            return NetworkAsyncHandler.INSTANCE.fetchBytes(path);
        }
        try (InputStream stream = ClientLoader.getResourceStream(path);){
            if (stream == null) {
                throw new IOException("stylesheet resource not found: " + path);
            }
            byte[] byArray = stream.readAllBytes();
            return byArray;
        }
    }

    private ParsedCss parseCss(String css, String contextPath) {
        if (css == null || css.isBlank()) {
            ApricityUI.LOGGER.warn("[AUI CSS] stylesheet is empty path={}", (Object)AuiLog.source(contextPath));
            return new ParsedCss("", List.of());
        }
        String clean = COMMENT_PATTERN.matcher(css).replaceAll("");
        Matcher matcher = FONT_FACE_PATTERN.matcher(clean);
        StringBuffer bodyCss = new StringBuffer();
        ArrayList<FontSource> fontSources = new ArrayList<FontSource>();
        while (matcher.find()) {
            FontSource source = this.parseFontFace(matcher.group(1), contextPath);
            if (source != null) {
                fontSources.add(source);
            }
            matcher.appendReplacement(bodyCss, "");
        }
        matcher.appendTail(bodyCss);
        return new ParsedCss(bodyCss.toString(), fontSources);
    }

    private ParsedCss parseCssCached(String css, String contextPath, long generation) {
        ParsedCssCacheKey key = new ParsedCssCacheKey(generation, contextPath == null ? "" : contextPath, css == null ? "" : css);
        return this.parsedCssCache.computeIfAbsent(key, ignored -> this.parseCss(css, contextPath));
    }

    private FontSource parseFontFace(String rules, String contextPath) {
        if (rules == null || rules.isBlank()) {
            return null;
        }
        HashMap<String, String> values = new HashMap<String, String>();
        for (String pair : rules.split(";")) {
            String[] parts = pair.split(":", 2);
            if (parts.length != 2) continue;
            values.put(parts[0].trim().toLowerCase(), parts[1].trim());
        }
        String family = this.cleanQuote((String)values.get("font-family"));
        String src = (String)values.get("src");
        if (family == null || family.isBlank() || src == null || src.isBlank()) {
            ApricityUI.LOGGER.warn("[AUI CSS] invalid @font-face declaration path={} family={} src={}", new Object[]{AuiLog.source(contextPath), family, AuiLog.compact(src)});
            return null;
        }
        Matcher matcher = CSS.URL_EXTRACTOR.matcher(src);
        if (!matcher.find()) {
            ApricityUI.LOGGER.warn("[AUI CSS] @font-face src has no url() path={} family={}", (Object)AuiLog.source(contextPath), (Object)family);
            return null;
        }
        String rawPath = this.cleanQuote(matcher.group(1));
        if (rawPath == null || rawPath.isBlank()) {
            ApricityUI.LOGGER.warn("[AUI CSS] @font-face url() is empty path={} family={}", (Object)AuiLog.source(contextPath), (Object)family);
            return null;
        }
        String resolvedPath = Loader.resolve(contextPath, rawPath);
        if (resolvedPath == null || resolvedPath.isBlank()) {
            ApricityUI.LOGGER.warn("[AUI CSS] @font-face path could not be resolved path={} raw={}", (Object)AuiLog.source(contextPath), (Object)rawPath);
            return null;
        }
        return new FontSource(family, resolvedPath);
    }

    private String cleanQuote(String text) {
        if (text == null) {
            return null;
        }
        return text.replace("\"", "").replace("'", "").trim();
    }

    private List<String> extractImports(String css) {
        if (css == null || css.isBlank()) {
            return List.of();
        }
        ArrayList<String> imports = new ArrayList<String>();
        Matcher matcher = IMPORT_PATTERN.matcher(css);
        while (matcher.find()) {
            String path = matcher.group(1);
            if (path == null || path.isBlank()) continue;
            imports.add(path.trim());
        }
        return imports;
    }

    private String stripImports(String css) {
        if (css == null || css.isBlank()) {
            return "";
        }
        return IMPORT_PATTERN.matcher(css).replaceAll("");
    }

    private record ParsedCss(String cssText, List<FontSource> fontTasks) {
        private ParsedCss {
            cssText = cssText == null ? "" : cssText;
            fontTasks = fontTasks == null ? List.of() : List.copyOf(fontTasks);
        }
    }

    private record ExternalCssCacheKey(long generation, String path) {
    }

    static interface ApplyTask {
        public StyleHandle handle();
    }

    private record CssTask(StyleHandle handle, int order, String contextPath, String cssText, List<FontSource> fontTasks) implements ApplyTask
    {
    }

    private record FontTask(StyleHandle handle, String family, String path, byte[] bytes) implements ApplyTask
    {
    }

    private record FailedTask(StyleHandle handle, String path, String kind, Throwable error) implements ApplyTask
    {
    }

    private record GlobalCssCache(long generation, ParsedCss parsed) {
    }

    private record FontSource(String family, String path) {
    }

    private record ParsedCssCacheKey(long generation, String contextPath, String cssText) {
    }
}

