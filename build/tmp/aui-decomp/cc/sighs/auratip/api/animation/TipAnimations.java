/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 */
package cc.sighs.auratip.api.animation;

import cc.sighs.auratip.api.animation.HoverAnimation;
import cc.sighs.auratip.api.animation.TransitionAnimation;
import cc.sighs.auratip.api.util.Params;
import cc.sighs.auratip.data.animation.AnimationType;
import net.minecraft.resources.ResourceLocation;

public final class TipAnimations {
    private TipAnimations() {
    }

    public static void register(ResourceLocation id, TransitionFactory factory) {
        AnimationType.registerInternal(id, raw -> factory.create(new Params(raw)));
    }

    public static void registerHover(ResourceLocation id, HoverFactory factory) {
        AnimationType.registerHoverInternal(id, raw -> factory.create(new Params(raw)));
    }

    @FunctionalInterface
    public static interface TransitionFactory {
        public TransitionAnimation create(Params var1);
    }

    @FunctionalInterface
    public static interface HoverFactory {
        public HoverAnimation create(Params var1);
    }
}

