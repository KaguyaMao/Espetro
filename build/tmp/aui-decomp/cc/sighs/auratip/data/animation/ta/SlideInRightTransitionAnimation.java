/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Dynamic
 */
package cc.sighs.auratip.data.animation.ta;

import cc.sighs.auratip.api.animation.TransitionAnimation;
import cc.sighs.auratip.data.animation.ta.BaseSlideTransitionAnimation;
import com.mojang.serialization.Dynamic;
import java.util.Map;

public class SlideInRightTransitionAnimation
extends BaseSlideTransitionAnimation {
    public SlideInRightTransitionAnimation(float extraPadding) {
        super(extraPadding);
    }

    public static TransitionAnimation create(Map<String, Dynamic<?>> params) {
        return BaseSlideTransitionAnimation.create(params, SlideInRightTransitionAnimation::new);
    }

    @Override
    public int offsetX(float eased, int panelWidth, int panelHeight) {
        return (int)(-((float)panelWidth + this.extraPadding) * (1.0f - eased));
    }

    @Override
    public int offsetY(float eased, int panelWidth, int panelHeight) {
        return 0;
    }
}

