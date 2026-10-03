/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.spi;

public interface AuiConfigService {
    public boolean debugAutoReload();

    public void setDebugAutoReload(boolean var1);

    public boolean aiAutoScreenshot();

    public void setAiAutoScreenshot(boolean var1);

    public boolean frameTimingHud();

    public void setFrameTimingHud(boolean var1);

    public boolean remoteDebug();

    public void setRemoteDebug(boolean var1);

    public boolean resourceManagerWorldWindow();

    public void setResourceManagerWorldWindow(boolean var1);

    public boolean viewportZoomPassThrough();

    public void setViewportZoomPassThrough(boolean var1);

    public float worldWindowDepthOffsetScale();

    public void setWorldWindowDepthOffsetScale(double var1);

    public int worldWindowMaxDisplayDistance();

    public void setWorldWindowMaxDisplayDistance(int var1);

    public boolean worldWindowLodEnabled();

    public void setWorldWindowLodEnabled(boolean var1);

    public int worldWindowFullDetailDistance();

    public void setWorldWindowFullDetailDistance(int var1);

    public int worldWindowReducedDetailDistance();

    public void setWorldWindowReducedDetailDistance(int var1);

    public void save();

    public void markClientReloadPending();

    public boolean consumeClientReloadPending();
}

