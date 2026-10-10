/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.forge;

import com.sighs.apricityui.registry.Keybindings;
import com.sighs.apricityui.spi.AuiKeyService;

public final class KeyService
implements AuiKeyService {
    public static final KeyService INSTANCE = new KeyService();

    private KeyService() {
    }

    @Override
    public boolean isReleaseMouseDown() {
        return Keybindings.RELEASE_MOUSE.m_90857_();
    }

    @Override
    public int devToolsKey() {
        return Keybindings.DEV_TOOLS.getKey().m_84873_();
    }

    @Override
    public int resourceManagerKey() {
        return Keybindings.RESOURCE_MANAGER.getKey().m_84873_();
    }

    @Override
    public int reloadKey() {
        return Keybindings.RELOAD.getKey().m_84873_();
    }
}

