/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.api.distmarker.OnlyIn
 *  org.joml.Matrix4f
 */
package com.redabysslucia.dragonrise_reforge.tools;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

@OnlyIn(value=Dist.CLIENT)
public class RenderDistanceHelper {
    private static long GUI_RENDER_TIMESTAMP = -1L;

    public static void markGuiRenderTimestamp() {
        GUI_RENDER_TIMESTAMP = System.currentTimeMillis();
    }

    public static boolean isInGui() {
        return System.currentTimeMillis() - GUI_RENDER_TIMESTAMP < 100L;
    }

    public static boolean shouldRenderLOD(PoseStack poseStack, double distance) {
        if (RenderDistanceHelper.isInGui()) {
            return false;
        }
        int globalLODDistance = 32;
        if (distance < (double)globalLODDistance) {
            return false;
        }
        Matrix4f matrix = poseStack.m_85850_().m_252922_();
        double viewDistance = matrix.m30() * matrix.m30() + matrix.m31() * matrix.m31() + matrix.m32() * matrix.m32();
        return viewDistance >= distance * distance;
    }
}

