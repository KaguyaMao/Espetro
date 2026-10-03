/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.resource.async.image;

import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.resource.Image;
import com.sighs.apricityui.task.AbstractAsyncHandler;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ImageHandle {
    private final String path;
    private volatile long generation;
    private volatile AbstractAsyncHandler.AsyncState state = AbstractAsyncHandler.AsyncState.NEW;
    private volatile Image.ITexture texture;
    private volatile Throwable error;
    private volatile long failedAtMs;
    private final ConcurrentHashMap<UUID, RequesterRef> requesters = new ConcurrentHashMap();

    public ImageHandle(String path, long generation) {
        this.path = path;
        this.generation = generation;
    }

    public String path() {
        return this.path;
    }

    public long generation() {
        return this.generation;
    }

    public AbstractAsyncHandler.AsyncState state() {
        return this.state;
    }

    public Image.ITexture texture() {
        return this.texture;
    }

    public Throwable error() {
        return this.error;
    }

    public long failedAtMs() {
        return this.failedAtMs;
    }

    public synchronized void reset(long newGeneration) {
        this.generation = newGeneration;
        this.state = AbstractAsyncHandler.AsyncState.NEW;
        this.error = null;
        this.failedAtMs = 0L;
    }

    public synchronized boolean tryEnterLoading() {
        if (this.state != AbstractAsyncHandler.AsyncState.NEW) {
            return false;
        }
        this.state = AbstractAsyncHandler.AsyncState.LOADING;
        return true;
    }

    public synchronized boolean tryEnterApplying() {
        if (this.state != AbstractAsyncHandler.AsyncState.LOADING) {
            return false;
        }
        this.state = AbstractAsyncHandler.AsyncState.APPLYING;
        return true;
    }

    public synchronized void markReady(Image.ITexture readyTexture) {
        this.texture = readyTexture;
        this.error = null;
        this.state = AbstractAsyncHandler.AsyncState.READY;
    }

    public synchronized void markFailed(Throwable throwable, long nowMs) {
        this.error = throwable;
        this.failedAtMs = nowMs;
        this.state = AbstractAsyncHandler.AsyncState.FAILED;
    }

    public synchronized void markStale() {
        this.state = AbstractAsyncHandler.AsyncState.STALE;
    }

    public synchronized void destroyTextureIfPresent() {
        if (this.texture == null) {
            return;
        }
        this.texture.destroy();
        this.texture = null;
    }

    public void addRequester(Element element, boolean needRelayout) {
        if (element == null) {
            return;
        }
        this.requesters.compute(element.uuid, (uuid, oldValue) -> {
            if (oldValue == null) {
                return new RequesterRef(element, needRelayout);
            }
            oldValue.needRelayout = oldValue.needRelayout || needRelayout;
            return oldValue;
        });
    }

    public List<RequesterRef> drainRequesters() {
        ArrayList<RequesterRef> values = new ArrayList<RequesterRef>(this.requesters.values());
        this.requesters.clear();
        return values;
    }

    public static final class RequesterRef {
        private final WeakReference<Element> elementRef;
        private volatile boolean needRelayout;

        private RequesterRef(Element element, boolean needRelayout) {
            this.elementRef = new WeakReference<Element>(element);
            this.needRelayout = needRelayout;
        }

        public Element getElement() {
            return (Element)this.elementRef.get();
        }

        public boolean needRelayout() {
            return this.needRelayout;
        }
    }
}

