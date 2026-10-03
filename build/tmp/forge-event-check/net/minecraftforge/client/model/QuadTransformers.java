/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.mojang.math.Transformation
 *  net.minecraft.Util
 *  net.minecraft.client.renderer.LightTexture
 *  org.joml.Vector3f
 *  org.joml.Vector4f
 */
package net.minecraftforge.client.model;

import com.google.common.base.Preconditions;
import com.mojang.math.Transformation;
import java.util.Arrays;
import net.minecraft.Util;
import net.minecraft.client.renderer.LightTexture;
import net.minecraftforge.client.model.IQuadTransformer;
import org.joml.Vector3f;
import org.joml.Vector4f;

public final class QuadTransformers {
    private static final IQuadTransformer EMPTY = quad -> {};
    private static final IQuadTransformer[] EMISSIVE_TRANSFORMERS = (IQuadTransformer[])Util.m_137469_((Object)new IQuadTransformer[16], array -> Arrays.setAll(array, i -> QuadTransformers.applyingLightmap(LightTexture.m_109885_((int)i, (int)i))));

    public static IQuadTransformer empty() {
        return EMPTY;
    }

    public static IQuadTransformer applying(Transformation transform) {
        if (transform.isIdentity()) {
            return QuadTransformers.empty();
        }
        return quad -> {
            int offset;
            int i;
            int[] vertices = quad.m_111303_();
            for (i = 0; i < 4; ++i) {
                offset = i * IQuadTransformer.STRIDE + IQuadTransformer.POSITION;
                float x = Float.intBitsToFloat(vertices[offset]);
                float y = Float.intBitsToFloat(vertices[offset + 1]);
                float z = Float.intBitsToFloat(vertices[offset + 2]);
                Vector4f pos = new Vector4f(x, y, z, 1.0f);
                transform.transformPosition(pos);
                pos.div(pos.w);
                vertices[offset] = Float.floatToRawIntBits(pos.x());
                vertices[offset + 1] = Float.floatToRawIntBits(pos.y());
                vertices[offset + 2] = Float.floatToRawIntBits(pos.z());
            }
            for (i = 0; i < 4; ++i) {
                offset = i * IQuadTransformer.STRIDE + IQuadTransformer.NORMAL;
                int normalIn = vertices[offset];
                if ((normalIn & 0xFFFFFF) == 0) continue;
                float x = (float)((byte)(normalIn & 0xFF)) / 127.0f;
                float y = (float)((byte)(normalIn >> 8 & 0xFF)) / 127.0f;
                float z = (float)((byte)(normalIn >> 16 & 0xFF)) / 127.0f;
                Vector3f pos = new Vector3f(x, y, z);
                transform.transformNormal(pos);
                vertices[offset] = (byte)(pos.x() * 127.0f) & 0xFF | ((byte)(pos.y() * 127.0f) & 0xFF) << 8 | ((byte)(pos.z() * 127.0f) & 0xFF) << 16 | normalIn & 0xFF000000;
            }
        };
    }

    public static IQuadTransformer applyingLightmap(int packedLight) {
        return quad -> {
            int[] vertices = quad.m_111303_();
            for (int i = 0; i < 4; ++i) {
                vertices[i * IQuadTransformer.STRIDE + IQuadTransformer.UV2] = packedLight;
            }
        };
    }

    public static IQuadTransformer applyingLightmap(int blockLight, int skyLight) {
        return QuadTransformers.applyingLightmap(LightTexture.m_109885_((int)blockLight, (int)skyLight));
    }

    public static IQuadTransformer settingEmissivity(int emissivity) {
        Preconditions.checkArgument((emissivity >= 0 && emissivity < 16 ? 1 : 0) != 0, (Object)"Emissivity must be between 0 and 15.");
        return EMISSIVE_TRANSFORMERS[emissivity];
    }

    public static IQuadTransformer settingMaxEmissivity() {
        return EMISSIVE_TRANSFORMERS[15];
    }

    public static IQuadTransformer applyingColor(int color) {
        int fixedColor = QuadTransformers.toABGR(color);
        return quad -> {
            int[] vertices = quad.m_111303_();
            for (int i = 0; i < 4; ++i) {
                vertices[i * IQuadTransformer.STRIDE + IQuadTransformer.COLOR] = fixedColor;
            }
        };
    }

    public static IQuadTransformer applyingColor(int red, int green, int blue) {
        return QuadTransformers.applyingColor(255, red, green, blue);
    }

    public static IQuadTransformer applyingColor(int alpha, int red, int green, int blue) {
        return QuadTransformers.applyingColor(alpha << 24 | red << 16 | green << 8 | blue);
    }

    public static int toABGR(int argb) {
        return argb & 0xFF00FF00 | argb >> 16 & 0xFF | argb << 16 & 0xFF0000;
    }

    private QuadTransformers() {
    }
}

