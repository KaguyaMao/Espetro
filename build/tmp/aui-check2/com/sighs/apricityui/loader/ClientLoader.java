/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 */
package com.sighs.apricityui.loader;

import com.sighs.apricityui.ApricityUI;
import com.sighs.apricityui.dev.DevTools;
import com.sighs.apricityui.dev.ResourceManager;
import com.sighs.apricityui.dom.DocumentRegistry;
import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.layout.Size;
import com.sighs.apricityui.loader.Loader;
import com.sighs.apricityui.parser.CSS;
import com.sighs.apricityui.parser.HTML;
import com.sighs.apricityui.parser.Selector;
import com.sighs.apricityui.render.FontDrawer;
import com.sighs.apricityui.render.ImageDrawer;
import com.sighs.apricityui.resource.Font;
import com.sighs.apricityui.resource.async.image.ImageAsyncHandler;
import com.sighs.apricityui.resource.async.network.NetworkAsyncHandler;
import com.sighs.apricityui.resource.async.style.StyleAsyncHandler;
import com.sighs.apricityui.spi.AuiServices;
import com.sighs.apricityui.style.Style;
import com.sighs.apricityui.style.Text;
import com.sighs.apricityui.task.AbstractAsyncHandler;
import com.sighs.apricityui.task.FrameTaskScheduler;
import com.sighs.apricityui.ui.ToastManager;
import com.sighs.apricityui.viewport.ApricityViewport;
import com.sighs.apricityui.world.WorldWindow;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import net.minecraft.client.Minecraft;

public class ClientLoader
extends Loader {
    private static final Object STATIC_RESOURCE_CACHE_LOCK = new Object();
    private static List<Loader.StaticResourceEntry> cachedFinalStaticResources = null;
    private static boolean reloadQueued;
    private static boolean reloadRequested;

    public ClientLoader(String extension) {
        super(extension);
    }

    public static void reload() {
        reloadRequested = true;
        if (reloadQueued) {
            return;
        }
        reloadQueued = true;
        String progressToast = ToastManager.show("Reloading...", new ToastManager.ToastOptions(0, false, "", "", "", ""));
        FrameTaskScheduler.scheduleAfterFrames(2, deadlineNs -> {
            try {
                do {
                    reloadRequested = false;
                    long beginNs = System.nanoTime();
                    AuiServices.script().reload();
                    ClientLoader.reloadResourcesInternal(beginNs);
                } while (reloadRequested);
            }
            finally {
                ToastManager.dismiss(progressToast);
                reloadQueued = false;
            }
            return true;
        });
    }

    public static void reloadResources() {
        ClientLoader.reloadResourcesInternal(System.nanoTime());
    }

    private static void reloadResourcesInternal(long beginNs) {
        ClientLoader.invalidateStaticResourceCache();
        ClientLoader.ensureAsyncHandlersInitialized();
        AbstractAsyncHandler.clearAllAndBumpGeneration();
        CSS.clearCompiledStylesheets();
        Selector.clearCompiledCache();
        DocumentRegistry.resetCreateTimingState();
        ImageDrawer.clearRenderTypeCache();
        FontDrawer.clearCache();
        Font.prepareReload();
        ClientLoader.warmUpDocumentInfrastructure();
        long scanStartNs = System.nanoTime();
        HTML.scan();
        long scanCostMs = (System.nanoTime() - scanStartNs) / 1000000L;
        long firstCreateWarmStartNs = System.nanoTime();
        int preparedTemplates = HTML.prepareTemplates();
        int preparedStylesheets = 0;
        for (HTML.TemplateResources template : HTML.preparedTemplateResources()) {
            preparedStylesheets += StyleAsyncHandler.INSTANCE.warmUpTemplateStyles(template.path(), template.externalStyleSrcs(), template.inlineStyles(), ClientLoader.resolveWarmupViewport(template.path()));
        }
        long firstCreateWarmCostMs = (System.nanoTime() - firstCreateWarmStartNs) / 1000000L;
        ApricityUI.LOGGER.info("[AUI Resource] first-create warm-up templates={} stylesheets={} cost={}ms", new Object[]{preparedTemplates, preparedStylesheets, firstCreateWarmCostMs});
        long refreshStartNs = System.nanoTime();
        Document.refreshAll();
        WorldWindow.windows.forEach(worldWindow -> worldWindow.document.refresh());
        DevTools.refresh();
        ResourceManager.refresh();
        long refreshCostMs = (System.nanoTime() - refreshStartNs) / 1000000L;
        long totalCostMs = (System.nanoTime() - beginNs) / 1000000L;
        ToastManager.show("\u91cd\u8f7d\u5b8c\u6210 " + totalCostMs + "ms (\u626b\u63cf " + scanCostMs + "ms, \u5237\u65b0 " + refreshCostMs + "ms)", new ToastManager.ToastOptions(4200, true, "", "", "", ""));
    }

    private static Size resolveWarmupViewport(String path) {
        try {
            ApricityViewport viewport = ApricityViewport.spec(path).createState(path).resolve(Minecraft.m_91087_().m_91268_());
            return new Size(viewport.layoutWidth(), viewport.layoutHeight());
        }
        catch (LinkageError | RuntimeException exception) {
            return new Size(1024.0, 768.0);
        }
    }

    private static void ensureAsyncHandlersInitialized() {
        ImageAsyncHandler.INSTANCE.id();
        StyleAsyncHandler.INSTANCE.id();
        NetworkAsyncHandler.INSTANCE.id();
    }

    private static void warmUpDocumentInfrastructure() {
        try {
            Style.warmUpMetadata();
        }
        catch (LinkageError | RuntimeException exception) {
            ApricityUI.LOGGER.warn("[AUI Resource] style metadata warm-up failed", exception);
        }
        try {
            Text.warmUpFontMetrics();
        }
        catch (LinkageError | RuntimeException exception) {
            ApricityUI.LOGGER.warn("[AUI Resource] font metrics warm-up failed", exception);
        }
        try {
            StyleAsyncHandler.INSTANCE.warmUpGlobalCss();
        }
        catch (LinkageError | RuntimeException exception) {
            ApricityUI.LOGGER.warn("[AUI Resource] global stylesheet warm-up failed", exception);
        }
        try {
            AuiServices.script().warmUp();
        }
        catch (LinkageError | RuntimeException exception) {
            ApricityUI.LOGGER.warn("[AUI Resource] script engine warm-up failed", exception);
        }
    }

    public static InputStream getResourceStream(String path) {
        InputStream filesystemStream = Loader.getResourceStream(path);
        if (filesystemStream != null) {
            return filesystemStream;
        }
        if (path == null || path.isEmpty()) {
            return null;
        }
        try {
            Optional<InputStream> resource = AuiServices.resources().openResource("apricity/" + path);
            return resource.orElse(null);
        }
        catch (LinkageError | RuntimeException exception) {
            ApricityUI.LOGGER.warn("[AUI Resource] failed to open resource-pack resource path={}", (Object)path, (Object)exception);
            return null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static List<Loader.StaticResourceEntry> listFinalStaticResources() {
        Object object = STATIC_RESOURCE_CACHE_LOCK;
        synchronized (object) {
            if (cachedFinalStaticResources != null) {
                return cachedFinalStaticResources;
            }
        }
        LinkedHashMap<String, Loader.StaticResourceEntry> merged = new LinkedHashMap<String, Loader.StaticResourceEntry>();
        ClientLoader.loadResourcePackEntries(merged);
        ClientLoader.loadFilesystemStaticResources(merged);
        List<Loader.StaticResourceEntry> entries = merged.values().stream().sorted(Comparator.comparing(Loader.StaticResourceEntry::path)).toList();
        Object object2 = STATIC_RESOURCE_CACHE_LOCK;
        synchronized (object2) {
            cachedFinalStaticResources = entries;
        }
        return entries;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void invalidateStaticResourceCache() {
        Object object = STATIC_RESOURCE_CACHE_LOCK;
        synchronized (object) {
            cachedFinalStaticResources = null;
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static String readGlobalCSS() {
        try (InputStream stream = ClientLoader.getResourceStream("global.css");){
            if (stream == null) return null;
            String string = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            return string;
        }
        catch (IOException exception) {
            ApricityUI.LOGGER.warn("[AUI Resource] failed to read global.css", (Throwable)exception);
        }
        return null;
    }

    private static void loadResourcePackEntries(Map<String, Loader.StaticResourceEntry> merged) {
        Map<String, String> resources = AuiServices.resources().listResourcePaths("apricity", "");
        for (Map.Entry<String, String> entry : resources.entrySet()) {
            String path = entry.getKey();
            if (path.isBlank()) continue;
            String sourcePack = Loader.safe(entry.getValue());
            merged.put(path, new Loader.StaticResourceEntry(path, Loader.extensionOf(path), Loader.ResourceLayer.RESOURCE_PACK, "resource-pack", sourcePack, -1L));
        }
    }

    public void loadResources(BiConsumer<String, String> handler) {
        this.handler = handler;
        this.loadedResourceCount = 0;
        this.loadFromResourcePack();
        this.loadFromLocalFolder();
        this.loadFromDevFolders();
        ApricityUI.LOGGER.info("[AUI Resource] scanned extension={} loaded={}", (Object)this.extension, (Object)this.loadedResourceCount);
    }

    private void loadFromResourcePack() {
        Map<String, String> paths = AuiServices.resources().listResourcePaths("apricity", "." + this.extension);
        for (String path : paths.keySet()) {
            try {
                InputStream stream = AuiServices.resources().openResource("apricity/" + path).orElse(null);
                try {
                    if (stream == null) continue;
                    this.handler.accept(path, new String(stream.readAllBytes(), StandardCharsets.UTF_8));
                    ++this.loadedResourceCount;
                }
                finally {
                    if (stream == null) continue;
                    stream.close();
                }
            }
            catch (IOException exception) {
                ApricityUI.LOGGER.error("[AUI Resource] failed to read resource-pack {} path={}", new Object[]{this.extension, path, exception});
            }
        }
    }
}

