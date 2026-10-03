/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 */
package com.sighs.apricityui.resource.async.image;

import com.sighs.apricityui.ApricityUI;
import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.loader.ClientLoader;
import com.sighs.apricityui.loader.Loader;
import com.sighs.apricityui.resource.Image;
import com.sighs.apricityui.resource.async.image.DecodedImage;
import com.sighs.apricityui.resource.async.image.ImageHandle;
import com.sighs.apricityui.resource.async.network.NetworkAsyncHandler;
import com.sighs.apricityui.spi.AuiServices;
import com.sighs.apricityui.style.Background;
import com.sighs.apricityui.style.Style;
import com.sighs.apricityui.task.AbstractAsyncHandler;
import com.sighs.apricityui.util.AuiLog;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.Minecraft;

public final class ImageAsyncHandler
extends AbstractAsyncHandler<ApplyTask> {
    public static final ImageAsyncHandler INSTANCE = new ImageAsyncHandler();
    private static final long FAILED_RETRY_MS = 5000L;
    private static final Map<String, ImageHandle> HANDLES = new ConcurrentHashMap<String, ImageHandle>();

    private ImageAsyncHandler() {
        super("image", 256, 1, 1500000L, "ApricityUI-ImageWorker");
    }

    public ImageHandle request(String path) {
        return this.request(path, null, false);
    }

    public static void prefetchImages(Document document) {
        if (document == null) {
            return;
        }
        HashSet<String> paths = new HashSet<String>();
        for (Element element : document.getElements()) {
            Style style;
            String src = element.getAttribute("src");
            if (src != null && !src.isEmpty() && "IMG".equals(element.tagName)) {
                ImageAsyncHandler.addIfValid(paths, Loader.resolve(document.getPath(), src));
            }
            if ((style = element.getRawComputedStyle()) == null) continue;
            for (String backgroundPath : Background.resolveImagePaths(document.getPath(), style.backgroundImage)) {
                ImageAsyncHandler.addIfValid(paths, backgroundPath);
            }
            String borderSource = ImageAsyncHandler.firstNonUnset(style.borderImageSource, style.borderImage);
            ImageAsyncHandler.addIfValid(paths, ImageAsyncHandler.resolveCssUrl(document.getPath(), borderSource));
        }
        INSTANCE.prefetch(paths);
    }

    private static ImageHandle prepareHandle(ImageHandle existing, String path, long generation, long now) {
        ImageHandle handle = existing;
        if (handle == null || handle.generation() != generation || handle.state() == AbstractAsyncHandler.AsyncState.STALE) {
            if (handle != null) {
                handle.destroyTextureIfPresent();
            }
            return new ImageHandle(path, generation);
        }
        if (handle.state() == AbstractAsyncHandler.AsyncState.FAILED && now - handle.failedAtMs() >= 5000L) {
            handle.reset(generation);
        }
        return handle;
    }

    private static void addIfValid(Set<String> target, String path) {
        if (path == null || path.isBlank() || "unset".equals(path)) {
            return;
        }
        target.add(path);
    }

    private static String firstNonUnset(String first, String second) {
        if (first != null && !first.isBlank() && !"unset".equals(first)) {
            return first;
        }
        if (second != null && !second.isBlank() && !"unset".equals(second)) {
            return second;
        }
        return null;
    }

    private static String resolveCssUrl(String contextPath, String cssValue) {
        if (cssValue == null || cssValue.isBlank() || "unset".equals(cssValue)) {
            return null;
        }
        int start = cssValue.indexOf("url(");
        if (start < 0) {
            return null;
        }
        int end = cssValue.indexOf(41, start + 4);
        if (end < 0) {
            return null;
        }
        String raw = cssValue.substring(start + 4, end).replace("\"", "").replace("'", "").trim();
        if (raw.isBlank()) {
            return null;
        }
        return Loader.resolve(contextPath, raw);
    }

    public ImageHandle request(String path, Element requester, boolean needRelayout) {
        if (path == null || path.isBlank() || "unset".equals(path)) {
            ApricityUI.LOGGER.warn("[AUI Image] ignored empty image request requester={}", (Object)AuiLog.element(requester));
            return null;
        }
        long generation = this.currentGeneration();
        long now = System.currentTimeMillis();
        ImageHandle handle = HANDLES.compute(path, (key, existing) -> ImageAsyncHandler.prepareHandle(existing, key, generation, now));
        if (requester != null && handle.state() != AbstractAsyncHandler.AsyncState.READY) {
            handle.addRequester(requester, needRelayout);
        }
        this.submitDecodeIfNeeded(handle);
        return handle;
    }

    public void prefetch(Collection<String> paths) {
        if (paths == null || paths.isEmpty()) {
            return;
        }
        HashSet<String> uniquePaths = new HashSet<String>(paths);
        for (String path : uniquePaths) {
            this.request(path);
        }
    }

    private void submitDecodeIfNeeded(ImageHandle handle) {
        if (handle == null || !handle.tryEnterLoading()) {
            return;
        }
        this.submitWorker(() -> this.decodeOnWorker(handle), ex -> {
            ApricityUI.LOGGER.error("[AUI Image] decode worker rejected path={}", (Object)handle.path(), ex);
            handle.markFailed((Throwable)ex, System.currentTimeMillis());
        });
    }

    private void decodeOnWorker(ImageHandle handle) {
        DecodedImage decodedImage = null;
        try {
            byte[] bytes = this.readResourceBytes(handle.path());
            decodedImage = Image.decode(handle.path(), bytes);
            if (decodedImage == null) {
                handle.markFailed(new IllegalStateException("\u56fe\u7247\u89e3\u7801\u5931\u8d25: " + handle.path()), System.currentTimeMillis());
                return;
            }
        }
        catch (Exception exception) {
            if (decodedImage != null) {
                decodedImage.close();
            }
            ApricityUI.LOGGER.error("[AUI Image] image load/decode failed path={}", (Object)handle.path(), (Object)exception);
            handle.markFailed(exception, System.currentTimeMillis());
            return;
        }
        if (handle.generation() != this.currentGeneration()) {
            decodedImage.close();
            handle.markStale();
            return;
        }
        if (!handle.tryEnterApplying()) {
            decodedImage.close();
            return;
        }
        this.enqueueApplyTask(new ApplyTask(handle, decodedImage, handle.generation()));
    }

    private byte[] readResourceBytes(String path) throws Exception {
        if (Loader.isRemotePath(path)) {
            return NetworkAsyncHandler.INSTANCE.fetchBytes(path);
        }
        try (InputStream stream = ClientLoader.getResourceStream(path);){
            if (stream == null) {
                throw new IllegalStateException("\u672a\u627e\u5230\u56fe\u7247\u8d44\u6e90: " + path);
            }
            byte[] byArray = stream.readAllBytes();
            return byArray;
        }
    }

    @Override
    protected void applyOnMainThread(ApplyTask task, long currentGeneration) {
        if (!AuiServices.render().isOnRenderThread()) {
            AuiServices.render().recordRenderCall(() -> this.applyOnRenderThread(task));
            return;
        }
        this.applyOnRenderThread(task);
    }

    private void applyOnRenderThread(ApplyTask task) {
        Image.ITexture texture;
        long generationNow = this.currentGeneration();
        if (task.generation != generationNow || task.handle.generation() != task.generation) {
            task.decodedImage.close();
            task.handle.markStale();
            return;
        }
        try {
            texture = Image.uploadDecoded(task.handle.path(), task.decodedImage);
        }
        catch (Exception exception) {
            ApricityUI.LOGGER.error("[AUI Image] texture apply failed path={}", (Object)task.handle.path(), (Object)exception);
            task.handle.markFailed(exception, System.currentTimeMillis());
            return;
        }
        if (texture == null) {
            ApricityUI.LOGGER.error("[AUI Image] texture upload returned null path={}", (Object)task.handle.path());
            task.handle.markFailed(new IllegalStateException("\u4e0a\u4f20\u7eb9\u7406\u5931\u8d25: " + task.handle.path()), System.currentTimeMillis());
            return;
        }
        if (task.handle.state() != AbstractAsyncHandler.AsyncState.APPLYING) {
            texture.destroy();
            return;
        }
        task.handle.markReady(texture);
        List<ImageHandle.RequesterRef> requesters = task.handle.drainRequesters();
        if (!requesters.isEmpty()) {
            Minecraft.m_91087_().execute(() -> {
                for (ImageHandle.RequesterRef requesterRef : requesters) {
                    Element element = requesterRef.getElement();
                    if (element == null || element.document == null) {
                        ApricityUI.LOGGER.debug("[AUI Image] requester disappeared before apply path={}", (Object)task.handle.path());
                        continue;
                    }
                    int dirtyMask = requesterRef.needRelayout() ? 4 : 1;
                    element.document.markDirty(element, dirtyMask);
                }
            });
        }
    }

    @Override
    protected void onBeforeClear(long nextGeneration) {
        for (ImageHandle handle : HANDLES.values()) {
            handle.destroyTextureIfPresent();
            handle.markStale();
        }
        HANDLES.clear();
    }

    @Override
    protected void onDiscardApplyTask(ApplyTask task) {
        if (task == null || task.decodedImage == null) {
            return;
        }
        task.decodedImage.close();
    }

    public record ApplyTask(ImageHandle handle, DecodedImage decodedImage, long generation) {
    }
}

