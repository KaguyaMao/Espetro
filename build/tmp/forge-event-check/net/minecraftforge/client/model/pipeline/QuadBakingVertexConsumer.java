/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.mojang.blaze3d.vertex.VertexFormatElement
 *  net.minecraft.Util
 *  net.minecraft.client.renderer.block.model.BakedQuad
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
 *  net.minecraft.core.Direction
 */
package net.minecraftforge.client.model.pipeline;

import com.google.common.base.Preconditions;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.Util;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraftforge.client.model.IQuadTransformer;
import net.minecraftforge.client.textures.UnitTextureAtlasSprite;

public class QuadBakingVertexConsumer
implements VertexConsumer {
    private final Map<VertexFormatElement, Integer> ELEMENT_OFFSETS = (Map)Util.m_137469_(new IdentityHashMap(), map -> {
        int i = 0;
        for (VertexFormatElement element : DefaultVertexFormat.f_85811_.m_86023_()) {
            map.put(element, DefaultVertexFormat.f_85811_.getOffset(i++) / 4);
        }
    });
    private static final int QUAD_DATA_SIZE = IQuadTransformer.STRIDE * 4;
    private final Consumer<BakedQuad> quadConsumer;
    int vertexIndex = 0;
    private int[] quadData = new int[QUAD_DATA_SIZE];
    private int tintIndex;
    private Direction direction = Direction.DOWN;
    private TextureAtlasSprite sprite = UnitTextureAtlasSprite.INSTANCE;
    private boolean shade;
    private boolean hasAmbientOcclusion;

    public QuadBakingVertexConsumer(Consumer<BakedQuad> quadConsumer) {
        this.quadConsumer = quadConsumer;
    }

    public VertexConsumer m_5483_(double x, double y, double z) {
        int offset = this.vertexIndex * IQuadTransformer.STRIDE + IQuadTransformer.POSITION;
        this.quadData[offset] = Float.floatToRawIntBits((float)x);
        this.quadData[offset + 1] = Float.floatToRawIntBits((float)y);
        this.quadData[offset + 2] = Float.floatToRawIntBits((float)z);
        return this;
    }

    public VertexConsumer m_5601_(float x, float y, float z) {
        int offset = this.vertexIndex * IQuadTransformer.STRIDE + IQuadTransformer.NORMAL;
        this.quadData[offset] = (int)(x * 127.0f) & 0xFF | ((int)(y * 127.0f) & 0xFF) << 8 | ((int)(z * 127.0f) & 0xFF) << 16;
        return this;
    }

    public VertexConsumer m_6122_(int r, int g, int b, int a) {
        int offset = this.vertexIndex * IQuadTransformer.STRIDE + IQuadTransformer.COLOR;
        this.quadData[offset] = (a & 0xFF) << 24 | (b & 0xFF) << 16 | (g & 0xFF) << 8 | r & 0xFF;
        return this;
    }

    public VertexConsumer m_7421_(float u, float v) {
        int offset = this.vertexIndex * IQuadTransformer.STRIDE + IQuadTransformer.UV0;
        this.quadData[offset] = Float.floatToRawIntBits(u);
        this.quadData[offset + 1] = Float.floatToRawIntBits(v);
        return this;
    }

    public VertexConsumer m_7122_(int u, int v) {
        if (IQuadTransformer.UV1 >= 0) {
            int offset = this.vertexIndex * IQuadTransformer.STRIDE + IQuadTransformer.UV1;
            this.quadData[offset] = u & 0xFFFF | (v & 0xFFFF) << 16;
        }
        return this;
    }

    public VertexConsumer m_7120_(int u, int v) {
        int offset = this.vertexIndex * IQuadTransformer.STRIDE + IQuadTransformer.UV2;
        this.quadData[offset] = u & 0xFFFF | (v & 0xFFFF) << 16;
        return this;
    }

    public VertexConsumer misc(VertexFormatElement element, int ... rawData) {
        Integer baseOffset = this.ELEMENT_OFFSETS.get(element);
        if (baseOffset != null) {
            int offset = this.vertexIndex * IQuadTransformer.STRIDE + baseOffset;
            System.arraycopy(rawData, 0, this.quadData, offset, rawData.length);
        }
        return this;
    }

    public void m_5752_() {
        if (++this.vertexIndex != 4) {
            return;
        }
        this.quadConsumer.accept(new BakedQuad(this.quadData, this.tintIndex, this.direction, this.sprite, this.shade, this.hasAmbientOcclusion));
        this.vertexIndex = 0;
        this.quadData = new int[QUAD_DATA_SIZE];
    }

    public void m_7404_(int r, int g, int b, int a) {
    }

    public void m_141991_() {
    }

    public void setTintIndex(int tintIndex) {
        this.tintIndex = tintIndex;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public void setSprite(TextureAtlasSprite sprite) {
        this.sprite = sprite;
    }

    public void setShade(boolean shade) {
        this.shade = shade;
    }

    public void setHasAmbientOcclusion(boolean hasAmbientOcclusion) {
        this.hasAmbientOcclusion = hasAmbientOcclusion;
    }

    public static class Buffered
    extends QuadBakingVertexConsumer {
        private final BakedQuad[] output;

        public Buffered() {
            this(new BakedQuad[1]);
        }

        private Buffered(BakedQuad[] output) {
            super(q -> {
                output[0] = q;
            });
            this.output = output;
        }

        public BakedQuad getQuad() {
            BakedQuad quad = (BakedQuad)Preconditions.checkNotNull((Object)this.output[0], (Object)("No quad has been emitted. Vertices in buffer: " + this.vertexIndex));
            this.output[0] = null;
            return quad;
        }
    }
}

