/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 */
package com.sighs.apricityui.render;

import com.sighs.apricityui.render.RenderBatchStats;
import com.sighs.apricityui.spi.AuiServices;
import com.sighs.apricityui.spi.RenderHandle;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

final class TextureRenderQueue {
    private static final int MAX_QUEUED_QUADS = 8192;
    private final List<Draw> draws = new ArrayList<Draw>();

    TextureRenderQueue() {
    }

    void add(RenderHandle renderHandle, boolean depthTest, Matrix4f matrix, float x, float y, float width, float height, float u0, float v0, float u1, float v1) {
        if (this.draws.size() >= 8192) {
            this.flush();
        }
        this.draws.add(new Draw(renderHandle, depthTest, new Matrix4f((Matrix4fc)matrix), x, y, width, height, u0, v0, u1, v1));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void flush() {
        if (this.draws.isEmpty()) {
            return;
        }
        try {
            int segmentStart = 0;
            while (segmentStart < this.draws.size()) {
                int segmentEnd;
                boolean depthTest = this.draws.get(segmentStart).depthTest();
                for (segmentEnd = segmentStart + 1; segmentEnd < this.draws.size() && this.draws.get(segmentEnd).depthTest() == depthTest; ++segmentEnd) {
                }
                if (depthTest) {
                    this.flushDepthTestedSegment(segmentStart, segmentEnd);
                } else {
                    this.flushOverlaySegment(segmentStart, segmentEnd);
                }
                segmentStart = segmentEnd;
            }
        }
        finally {
            this.draws.clear();
        }
    }

    private void flushDepthTestedSegment(int start, int end) {
        ArrayList<Batch> batches = new ArrayList<Batch>();
        IdentityHashMap<RenderHandle, Batch> byRenderHandle = new IdentityHashMap<RenderHandle, Batch>();
        for (int i = start; i < end; ++i) {
            Draw draw = this.draws.get(i);
            Batch batch = (Batch)byRenderHandle.get(draw.renderHandle());
            if (batch == null) {
                batch = new Batch(draw.renderHandle());
                byRenderHandle.put(draw.renderHandle(), batch);
                batches.add(batch);
            }
            batch.draws().add(draw);
        }
        this.flushBatches(batches);
    }

    private void flushOverlaySegment(int start, int end) {
        ArrayList<Batch> batches = new ArrayList<Batch>();
        Batch previous = null;
        for (int i = start; i < end; ++i) {
            Draw draw = this.draws.get(i);
            if (previous == null || previous.renderHandle() != draw.renderHandle()) {
                previous = new Batch(draw.renderHandle());
                batches.add(previous);
            }
            previous.draws().add(draw);
        }
        this.flushBatches(batches);
    }

    private void flushBatches(List<Batch> batches) {
        for (Batch batch : batches) {
            Object token = AuiServices.render().beginTextureBatch(batch.renderHandle());
            for (Draw draw : batch.draws()) {
                AuiServices.render().emitTextureQuad(token, draw.matrix(), draw.x(), draw.y(), draw.width(), draw.height(), draw.u0(), draw.v0(), draw.u1(), draw.v1());
            }
            AuiServices.render().flushTextureBatch(token, batch.renderHandle());
            RenderBatchStats.recordImageFlush();
        }
    }

    private record Draw(RenderHandle renderHandle, boolean depthTest, Matrix4f matrix, float x, float y, float width, float height, float u0, float v0, float u1, float v1) {
    }

    private record Batch(RenderHandle renderHandle, List<Draw> draws) {
        private Batch(RenderHandle renderHandle) {
            this(renderHandle, new ArrayList<Draw>());
        }
    }
}

