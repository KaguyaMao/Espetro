/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.render;

import com.sighs.apricityui.layout.LayoutMeasureCache;
import com.sighs.apricityui.render.RectFrameCache;
import com.sighs.apricityui.style.StyleFrameCache;

public final class GeometryQueryScope
implements AutoCloseable {
    private boolean closed;

    private GeometryQueryScope() {
        RectFrameCache.begin();
        LayoutMeasureCache.begin();
        StyleFrameCache.begin();
    }

    public static GeometryQueryScope open() {
        return new GeometryQueryScope();
    }

    @Override
    public void close() {
        if (this.closed) {
            return;
        }
        this.closed = true;
        StyleFrameCache.end();
        LayoutMeasureCache.end();
        RectFrameCache.end();
    }
}

