/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.math.Transformation
 *  org.joml.Vector3f
 */
package net.minecraftforge.client.extensions;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Transformation;
import org.joml.Vector3f;

public interface IForgePoseStack {
    private PoseStack self() {
        return (PoseStack)this;
    }

    default public void pushTransformation(Transformation transformation) {
        PoseStack self = this.self();
        self.m_85836_();
        Vector3f trans = transformation.m_252829_();
        self.m_252880_(trans.x(), trans.y(), trans.z());
        self.m_252781_(transformation.m_253244_());
        Vector3f scale = transformation.m_252900_();
        self.m_85841_(scale.x(), scale.y(), scale.z());
        self.m_252781_(transformation.m_252848_());
    }
}

