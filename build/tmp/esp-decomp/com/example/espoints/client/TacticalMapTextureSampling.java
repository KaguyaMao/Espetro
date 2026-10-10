/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.GlStateManager
 *  net.minecraft.client.renderer.texture.DynamicTexture
 */
package com.example.espoints.client;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;

final class TacticalMapTextureSampling {
    private static final int GL_TEXTURE_2D = 3553;
    private static final int GL_TEXTURE_WRAP_S = 10242;
    private static final int GL_TEXTURE_WRAP_T = 10243;
    private static final int GL_CLAMP_TO_EDGE = 33071;

    private TacticalMapTextureSampling() {
    }

    static void apply(DynamicTexture texture, boolean linear) {
        if (texture == null) {
            return;
        }
        texture.m_117960_(linear, false);
        texture.m_117966_();
        GlStateManager._texParameter((int)3553, (int)10242, (int)33071);
        GlStateManager._texParameter((int)3553, (int)10243, (int)33071);
    }
}

