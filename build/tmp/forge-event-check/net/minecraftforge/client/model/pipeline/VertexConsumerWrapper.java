/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.mojang.blaze3d.vertex.VertexFormatElement
 */
package net.minecraftforge.client.model.pipeline;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormatElement;

public abstract class VertexConsumerWrapper
implements VertexConsumer {
    protected final VertexConsumer parent;

    public VertexConsumerWrapper(VertexConsumer parent) {
        this.parent = parent;
    }

    public VertexConsumer m_5483_(double x, double y, double z) {
        this.parent.m_5483_(x, y, z);
        return this;
    }

    public VertexConsumer m_6122_(int r, int g, int b, int a) {
        this.parent.m_6122_(r, g, b, a);
        return this;
    }

    public VertexConsumer m_7421_(float u, float v) {
        this.parent.m_7421_(u, v);
        return this;
    }

    public VertexConsumer m_7122_(int u, int v) {
        this.parent.m_7122_(u, v);
        return this;
    }

    public VertexConsumer m_7120_(int u, int v) {
        this.parent.m_7120_(u, v);
        return this;
    }

    public VertexConsumer m_5601_(float x, float y, float z) {
        this.parent.m_5601_(x, y, z);
        return this;
    }

    public VertexConsumer misc(VertexFormatElement element, int ... values) {
        this.parent.misc(element, values);
        return this;
    }

    public void m_5752_() {
        this.parent.m_5752_();
    }

    public void m_7404_(int r, int g, int b, int a) {
        this.parent.m_7404_(r, g, b, a);
    }

    public void m_141991_() {
        this.parent.m_141991_();
    }
}

