/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.task;

import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.init.Window;
import com.sighs.apricityui.resource.async.image.ImageAsyncHandler;
import com.sighs.apricityui.resource.async.style.StyleAsyncHandler;
import com.sighs.apricityui.task.FrameTaskScheduler;
import com.sighs.apricityui.ui.ToastManager;

public final class FrameScheduler {
    private FrameScheduler() {
    }

    public static void tick() {
        ToastManager.tick();
        StyleAsyncHandler.INSTANCE.tickApplyQueue();
        ImageAsyncHandler.INSTANCE.tickApplyQueue();
        FrameTaskScheduler.tick();
        for (Document document : Document.getAll()) {
            if (document == null) continue;
            document.tickFrame();
        }
        Window.window.tickResizeObservers();
    }

    public static void renderBegin() {
    }
}

