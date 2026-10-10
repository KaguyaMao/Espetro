/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.forge;

import com.sighs.apricityui.config.ApricityUIConfig;
import com.sighs.apricityui.spi.AuiConfigService;

public final class ConfigService
implements AuiConfigService {
    public static final ConfigService INSTANCE = new ConfigService();

    private ConfigService() {
    }

    private static ApricityUIConfig.Client client() {
        return ApricityUIConfig.CLIENT;
    }

    @Override
    public boolean debugAutoReload() {
        return (Boolean)ConfigService.client().debugAutoReload.get();
    }

    @Override
    public void setDebugAutoReload(boolean value) {
        ConfigService.client().debugAutoReload.set((Object)value);
    }

    @Override
    public boolean aiAutoScreenshot() {
        return (Boolean)ConfigService.client().aiAutoScreenshot.get();
    }

    @Override
    public void setAiAutoScreenshot(boolean value) {
        ConfigService.client().aiAutoScreenshot.set((Object)value);
    }

    @Override
    public boolean frameTimingHud() {
        return (Boolean)ConfigService.client().frameTimingHud.get();
    }

    @Override
    public void setFrameTimingHud(boolean value) {
        ConfigService.client().frameTimingHud.set((Object)value);
    }

    @Override
    public boolean remoteDebug() {
        return (Boolean)ConfigService.client().remoteDebug.get();
    }

    @Override
    public void setRemoteDebug(boolean value) {
        ConfigService.client().remoteDebug.set((Object)value);
    }

    @Override
    public boolean resourceManagerWorldWindow() {
        return (Boolean)ConfigService.client().resourceManagerWorldWindow.get();
    }

    @Override
    public void setResourceManagerWorldWindow(boolean value) {
        ConfigService.client().resourceManagerWorldWindow.set((Object)value);
    }

    @Override
    public boolean viewportZoomPassThrough() {
        return (Boolean)ConfigService.client().viewportZoomPassThrough.get();
    }

    @Override
    public void setViewportZoomPassThrough(boolean value) {
        ConfigService.client().viewportZoomPassThrough.set((Object)value);
    }

    @Override
    public float worldWindowDepthOffsetScale() {
        return ConfigService.client().worldWindowDepthOffsetScale();
    }

    @Override
    public void setWorldWindowDepthOffsetScale(double value) {
        ConfigService.client().worldWindowDepthOffsetScale.set((Object)value);
    }

    @Override
    public int worldWindowMaxDisplayDistance() {
        return (Integer)ConfigService.client().worldWindowMaxDisplayDistance.get();
    }

    @Override
    public void setWorldWindowMaxDisplayDistance(int value) {
        ConfigService.client().worldWindowMaxDisplayDistance.set((Object)value);
    }

    @Override
    public boolean worldWindowLodEnabled() {
        return (Boolean)ConfigService.client().worldWindowLodEnabled.get();
    }

    @Override
    public void setWorldWindowLodEnabled(boolean value) {
        ConfigService.client().worldWindowLodEnabled.set((Object)value);
    }

    @Override
    public int worldWindowFullDetailDistance() {
        return (Integer)ConfigService.client().worldWindowFullDetailDistance.get();
    }

    @Override
    public void setWorldWindowFullDetailDistance(int value) {
        ConfigService.client().worldWindowFullDetailDistance.set((Object)value);
    }

    @Override
    public int worldWindowReducedDetailDistance() {
        return (Integer)ConfigService.client().worldWindowReducedDetailDistance.get();
    }

    @Override
    public void setWorldWindowReducedDetailDistance(int value) {
        ConfigService.client().worldWindowReducedDetailDistance.set((Object)value);
    }

    @Override
    public void save() {
        ApricityUIConfig.CLIENT_SPEC.save();
    }

    @Override
    public void markClientReloadPending() {
        ApricityUIConfig.markClientReloadPending();
    }

    @Override
    public boolean consumeClientReloadPending() {
        return ApricityUIConfig.consumeClientReloadPending();
    }
}

