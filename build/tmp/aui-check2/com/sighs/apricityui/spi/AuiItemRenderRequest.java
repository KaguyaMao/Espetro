/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 */
package com.sighs.apricityui.spi;

import com.mojang.blaze3d.vertex.PoseStack;

public record AuiItemRenderRequest(PoseStack poseStack, Object stack, int seed, boolean decorations, String overlayText, float decorationOffsetY, boolean ghost) {
}

