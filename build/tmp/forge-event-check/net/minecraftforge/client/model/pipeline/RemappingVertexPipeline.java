/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.mojang.blaze3d.vertex.VertexFormat
 *  com.mojang.blaze3d.vertex.VertexFormatElement
 *  com.mojang.blaze3d.vertex.VertexFormatElement$Usage
 *  org.joml.Vector3d
 *  org.joml.Vector3f
 */
package net.minecraftforge.client.model.pipeline;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import org.joml.Vector3d;
import org.joml.Vector3f;

public class RemappingVertexPipeline
implements VertexConsumer {
    private static final Set<VertexFormatElement> KNOWN_ELEMENTS = Set.of(DefaultVertexFormat.f_85804_, DefaultVertexFormat.f_85805_, DefaultVertexFormat.f_166849_, DefaultVertexFormat.f_85807_, DefaultVertexFormat.f_85808_, DefaultVertexFormat.f_85809_, DefaultVertexFormat.f_85810_);
    private static final int[] EMPTY_INT_ARRAY = new int[0];
    private final VertexConsumer parent;
    private final VertexFormat targetFormat;
    private final Vector3d position = new Vector3d();
    private final Vector3f normal = new Vector3f();
    private final int[] color = new int[]{255, 255, 255, 255};
    private final float[] uv0 = new float[]{0.0f, 0.0f};
    private final int[] uv1 = new int[]{0, 10};
    private final int[] uv2 = new int[]{0, 0};
    private final Map<VertexFormatElement, Integer> miscElementIds;
    private final int[][] misc;

    public RemappingVertexPipeline(VertexConsumer parent, VertexFormat targetFormat) {
        this.parent = parent;
        this.targetFormat = targetFormat;
        this.miscElementIds = new IdentityHashMap<VertexFormatElement, Integer>();
        int i = 0;
        for (VertexFormatElement element : targetFormat.m_86023_()) {
            if (element.m_86048_() == VertexFormatElement.Usage.PADDING || KNOWN_ELEMENTS.contains(element)) continue;
            this.miscElementIds.put(element, i++);
        }
        this.misc = new int[i][];
        Arrays.fill((Object[])this.misc, EMPTY_INT_ARRAY);
    }

    public VertexConsumer m_5483_(double x, double y, double z) {
        this.position.set(x, y, z);
        return this;
    }

    public VertexConsumer m_5601_(float x, float y, float z) {
        this.normal.set(x, y, z);
        return this;
    }

    public VertexConsumer m_6122_(int r, int g, int b, int a) {
        this.color[0] = r;
        this.color[1] = g;
        this.color[2] = b;
        this.color[3] = a;
        return this;
    }

    public VertexConsumer m_7421_(float u, float v) {
        this.uv0[0] = u;
        this.uv0[1] = v;
        return this;
    }

    public VertexConsumer m_7122_(int u, int v) {
        this.uv1[0] = u;
        this.uv1[1] = v;
        return this;
    }

    public VertexConsumer m_7120_(int u, int v) {
        this.uv2[0] = u;
        this.uv2[1] = v;
        return this;
    }

    public VertexConsumer misc(VertexFormatElement element, int ... values) {
        Integer id = this.miscElementIds.get(element);
        if (id != null) {
            this.misc[id.intValue()] = Arrays.copyOf(values, values.length);
        }
        return this;
    }

    public void m_5752_() {
        for (VertexFormatElement element : this.targetFormat.m_86023_()) {
            if (element.m_86048_() == VertexFormatElement.Usage.PADDING) continue;
            if (element.equals((Object)DefaultVertexFormat.f_85804_)) {
                this.parent.m_5483_(this.position.x, this.position.y, this.position.z);
                continue;
            }
            if (element.equals((Object)DefaultVertexFormat.f_85809_)) {
                this.parent.m_5601_(this.normal.x(), this.normal.y(), this.normal.z());
                continue;
            }
            if (element.equals((Object)DefaultVertexFormat.f_85805_)) {
                this.parent.m_6122_(this.color[0], this.color[1], this.color[2], this.color[3]);
                continue;
            }
            if (element.equals((Object)DefaultVertexFormat.f_85806_)) {
                this.parent.m_7421_(this.uv0[0], this.uv0[1]);
                continue;
            }
            if (element.equals((Object)DefaultVertexFormat.f_85807_)) {
                this.parent.m_7122_(this.uv1[0], this.uv1[1]);
                continue;
            }
            if (element.equals((Object)DefaultVertexFormat.f_85808_)) {
                this.parent.m_7120_(this.uv2[0], this.uv2[1]);
                continue;
            }
            this.parent.misc(element, this.misc[this.miscElementIds.get(element)]);
        }
        this.parent.m_5752_();
    }

    public void m_7404_(int r, int g, int b, int a) {
        this.parent.m_7404_(r, g, b, a);
    }

    public void m_141991_() {
        this.parent.m_141991_();
    }
}

