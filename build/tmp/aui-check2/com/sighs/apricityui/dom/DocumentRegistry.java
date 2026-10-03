/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.dom;

import com.sighs.apricityui.ApricityUI;
import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.parser.HTML;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public final class DocumentRegistry {
    private static final List<Document> documents = new CopyOnWriteArrayList<Document>();
    private static final ThreadLocal<Document> contextDocument = new ThreadLocal();
    private static final Object createTimingLock = new Object();
    private static final Set<String> createdPaths = new HashSet<String>();
    private static boolean createdAnyDocument;

    private DocumentRegistry() {
    }

    public static Document getContext() {
        return contextDocument.get();
    }

    public static void setContext(Document document) {
        if (document == null) {
            contextDocument.remove();
        } else {
            contextDocument.set(document);
        }
    }

    public static void refreshAll() {
        for (Document document : documents) {
            if (document == null || document.isReloadPersistent() || document.isDisposed()) continue;
            document.refresh();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void resetCreateTimingState() {
        Object object = createTimingLock;
        synchronized (object) {
            createdPaths.clear();
            createdAnyDocument = false;
        }
    }

    public static Document create(String path) {
        if (HTML.getTemple(path) == null) {
            ApricityUI.LOGGER.error("[AUI Document] cannot create document: template is missing path={}", (Object)path);
            return null;
        }
        CreateTiming timing = DocumentRegistry.beginCreateTiming(path);
        Document document = new Document(path, false);
        documents.add(document);
        try {
            document.applyViewport(false);
            document.refresh();
            document.applyViewport(false);
            Document document2 = document;
            return document2;
        }
        catch (LinkageError | RuntimeException failure) {
            document.remove();
            throw failure;
        }
        finally {
            DocumentRegistry.logCreateTiming(path, timing);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Document createInWorld(String path) {
        if (HTML.getTemple(path) == null) {
            ApricityUI.LOGGER.error("[AUI Document] cannot create world document: template is missing path={}", (Object)path);
            return null;
        }
        CreateTiming timing = DocumentRegistry.beginCreateTiming(path);
        try {
            Document document = new Document(path, true);
            documents.add(document);
            document.applyViewport(false);
            document.refresh();
            Document document2 = document;
            return document2;
        }
        finally {
            DocumentRegistry.logCreateTiming(path, timing);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static CreateTiming beginCreateTiming(String path) {
        Object object = createTimingLock;
        synchronized (object) {
            boolean firstGlobal = !createdAnyDocument;
            boolean firstPath = createdPaths.add(path == null ? "" : path);
            createdAnyDocument = true;
            return new CreateTiming(System.nanoTime(), firstGlobal, firstPath);
        }
    }

    private static void logCreateTiming(String path, CreateTiming timing) {
        if (timing == null) {
            return;
        }
        long totalMs = (System.nanoTime() - timing.startedNs) / 1000000L;
        if (!timing.firstGlobal && !timing.firstPath && totalMs < 50L) {
            return;
        }
        ApricityUI.LOGGER.info("[AUI Document] create timing path={} total={}ms firstGlobal={} firstPath={}", new Object[]{path, totalMs, timing.firstGlobal, timing.firstPath});
    }

    public static ArrayList<Document> get(String path) {
        ArrayList<Document> result = new ArrayList<Document>();
        for (Document document : documents) {
            if (document.isDisposed() || !document.getPath().equals(path)) continue;
            result.add(document);
        }
        return result;
    }

    public static Document getByUUID(String uuid) {
        for (Document document : documents) {
            if (document.isDisposed() || !document.getUuid().toString().equals(uuid)) continue;
            return document;
        }
        return null;
    }

    public static List<Document> getAll() {
        return documents;
    }

    public static void remove(String path) {
        documents.removeIf(document -> {
            if (!document.is(path)) {
                return false;
            }
            document.disposeLifecycle();
            return true;
        });
    }

    public static void remove(UUID uuid) {
        documents.removeIf(document -> {
            if (!document.is(uuid)) {
                return false;
            }
            document.disposeLifecycle();
            return true;
        });
    }

    public static void applyViewportForPath(String path, boolean relayout) {
        for (Document document : documents) {
            if (document == null || document.inWorld || document.isDisposed() || !document.is(path)) continue;
            document.applyViewport(relayout);
        }
    }

    private record CreateTiming(long startedNs, boolean firstGlobal, boolean firstPath) {
    }
}

