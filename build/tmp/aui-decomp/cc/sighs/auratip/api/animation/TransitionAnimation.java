/*
 * Decompiled with CFR 0.152.
 */
package cc.sighs.auratip.api.animation;

import cc.sighs.auratip.api.animation.Animation;

public interface TransitionAnimation
extends Animation {
    public float easedProgress(long var1, long var3, boolean var5, int var6, int var7);

    public int offsetX(float var1, int var2, int var3);

    public int offsetY(float var1, int var2, int var3);
}

