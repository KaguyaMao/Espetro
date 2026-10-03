/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack$Pose
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.mojang.blaze3d.vertex.VertexFormatElement
 *  net.minecraft.client.renderer.block.model.BakedQuad
 *  org.joml.Matrix3f
 *  org.joml.Matrix3fc
 *  org.joml.Vector3f
 */
package net.minecraftforge.client.extensions;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import java.nio.ByteBuffer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraftforge.client.model.IQuadTransformer;
import org.joml.Matrix3f;
import org.joml.Matrix3fc;
import org.joml.Vector3f;

public interface IForgeVertexConsumer {
    private VertexConsumer self() {
        return (VertexConsumer)this;
    }

    default public VertexConsumer misc(VertexFormatElement element, int ... rawData) {
        return this.self();
    }

    default public void putBulkData(PoseStack.Pose pose, BakedQuad bakedQuad, float red, float green, float blue, float alpha, int packedLight, int packedOverlay, boolean readExistingColor) {
        this.self().putBulkData(pose, bakedQuad, new float[]{1.0f, 1.0f, 1.0f, 1.0f}, red, green, blue, alpha, new int[]{packedLight, packedLight, packedLight, packedLight}, packedOverlay, readExistingColor);
    }

    default public int applyBakedLighting(int packedLight, ByteBuffer data) {
        int bl = packedLight & 0xFFFF;
        int sl = packedLight >> 16 & 0xFFFF;
        int offset = IQuadTransformer.UV2 * 4;
        int blBaked = Short.toUnsignedInt(data.getShort(offset));
        int slBaked = Short.toUnsignedInt(data.getShort(offset + 2));
        bl = Math.max(bl, blBaked);
        sl = Math.max(sl, slBaked);
        return bl | sl << 16;
    }

    default public void applyBakedNormals(Vector3f generated, ByteBuffer data, Matrix3f normalTransform) {
        byte nx = data.get(28);
        byte ny = data.get(29);
        byte nz = data.get(30);
        if (nx != 0 || ny != 0 || nz != 0) {
            generated.set((float)nx / 127.0f, (float)ny / 127.0f, (float)nz / 127.0f);
            generated.mul((Matrix3fc)normalTransform);
        }
    }
}

