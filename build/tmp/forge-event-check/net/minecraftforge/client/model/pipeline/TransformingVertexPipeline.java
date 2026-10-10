/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.mojang.math.Transformation
 *  org.joml.Vector3f
 *  org.joml.Vector4f
 */
package net.minecraftforge.client.model.pipeline;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Transformation;
import net.minecraftforge.client.model.pipeline.VertexConsumerWrapper;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class TransformingVertexPipeline
extends VertexConsumerWrapper {
    private final Transformation transformation;

    public TransformingVertexPipeline(VertexConsumer parent, Transformation transformation) {
        super(parent);
        this.transformation = transformation;
    }

    @Override
    public VertexConsumer m_5483_(double x, double y, double z) {
        Vector4f vec = new Vector4f((float)x, (float)y, (float)z, 1.0f);
        this.transformation.transformPosition(vec);
        vec.div(vec.w);
        return super.m_5483_(vec.x(), vec.y(), vec.z());
    }

    @Override
    public VertexConsumer m_5601_(float x, float y, float z) {
        Vector3f vec = new Vector3f(x, y, z);
        this.transformation.transformNormal(vec);
        vec.normalize();
        return super.m_5601_(vec.x(), vec.y(), vec.z());
    }
}

