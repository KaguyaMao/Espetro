/*
 * Decompiled with CFR 0.152.
 */
package cc.sighs.auratip.data.animation.ha;

import cc.sighs.auratip.api.animation.HoverAnimation;

public class NoneHoverAnimation
implements HoverAnimation {
    public static final HoverAnimation INSTANCE = new NoneHoverAnimation();

    @Override
    public int offsetX(long nowMs, long startMs, int panelWidth, int panelHeight, float speed) {
        return 0;
    }

    @Override
    public int offsetY(long nowMs, long startMs, int panelWidth, int panelHeight, float speed) {
        return 0;
    }
}

