/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  com.mojang.blaze3d.vertex.VertexFormatElement
 *  net.minecraft.client.renderer.block.model.BakedQuad
 */
package net.minecraftforge.client.model;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.renderer.block.model.BakedQuad;

public interface IQuadTransformer {
    public static final int STRIDE = DefaultVertexFormat.f_85811_.m_86017_();
    public static final int POSITION = IQuadTransformer.findOffset(DefaultVertexFormat.f_85804_);
    public static final int COLOR = IQuadTransformer.findOffset(DefaultVertexFormat.f_85805_);
    public static final int UV0 = IQuadTransformer.findOffset(DefaultVertexFormat.f_85806_);
    public static final int UV1 = IQuadTransformer.findOffset(DefaultVertexFormat.f_85807_);
    public static final int UV2 = IQuadTransformer.findOffset(DefaultVertexFormat.f_85808_);
    public static final int NORMAL = IQuadTransformer.findOffset(DefaultVertexFormat.f_85809_);

    public void processInPlace(BakedQuad var1);

    default public void processInPlace(List<BakedQuad> quads) {
        for (BakedQuad quad : quads) {
            this.processInPlace(quad);
        }
    }

    default public BakedQuad process(BakedQuad quad) {
        BakedQuad copy = IQuadTransformer.copy(quad);
        this.processInPlace(copy);
        return copy;
    }

    default public List<BakedQuad> process(List<BakedQuad> inputs) {
        return inputs.stream().map(IQuadTransformer::copy).peek(this::processInPlace).toList();
    }

    default public IQuadTransformer andThen(IQuadTransformer other) {
        return quad -> {
            this.processInPlace(quad);
            other.processInPlace(quad);
        };
    }

    private static BakedQuad copy(BakedQuad quad) {
        int[] vertices = quad.m_111303_();
        return new BakedQuad(Arrays.copyOf(vertices, vertices.length), quad.m_111305_(), quad.m_111306_(), quad.m_173410_(), quad.m_111307_(), quad.hasAmbientOcclusion());
    }

    private static int findOffset(VertexFormatElement element) {
        int index = DefaultVertexFormat.f_85811_.m_86023_().indexOf((Object)element);
        return index < 0 ? -1 : DefaultVertexFormat.f_85811_.getOffset(index) / 4;
    }
}

