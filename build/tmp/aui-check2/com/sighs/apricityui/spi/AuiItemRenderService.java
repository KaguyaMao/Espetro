/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.spi;

import com.sighs.apricityui.spi.AuiItemRenderRequest;

@FunctionalInterface
public interface AuiItemRenderService {
    public void render(AuiItemRenderRequest var1);

    default public boolean isEmptyStack(Object stack) {
        return stack == null;
    }
}

