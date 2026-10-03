/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Dynamic
 *  net.minecraft.resources.ResourceLocation
 */
package cc.sighs.auratip.data.animation;

import cc.sighs.auratip.api.animation.HoverAnimation;
import cc.sighs.auratip.api.animation.TransitionAnimation;
import cc.sighs.auratip.data.animation.ha.FloatHoverAnimation;
import cc.sighs.auratip.data.animation.ha.NoneHoverAnimation;
import cc.sighs.auratip.data.animation.ha.ShakeHoverAnimation;
import cc.sighs.auratip.data.animation.ta.FadeAndSlideTransitionAnimation;
import cc.sighs.auratip.data.animation.ta.FadeTransitionAnimation;
import cc.sighs.auratip.data.animation.ta.SlideInLeftTransitionAnimation;
import cc.sighs.auratip.data.animation.ta.SlideInRightTransitionAnimation;
import cc.sighs.auratip.data.animation.ta.SlideInTopTransitionAnimation;
import cc.sighs.auratip.data.animation.ta.SlideTransitionAnimation;
import cc.sighs.auratip.util.SerializationUtil;
import com.mojang.serialization.Dynamic;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;

public final class AnimationType {
    private static final Map<ResourceLocation, AnimationFactory> ANIMATIONS = new HashMap<ResourceLocation, AnimationFactory>();
    private static final Map<ResourceLocation, HoverAnimationFactory> HOVER_ANIMATIONS = new HashMap<ResourceLocation, HoverAnimationFactory>();
    private static final Map<ResourceLocation, Map<String, SerializationUtil.CapturedParam>> ANIMATION_PARAM_SCHEMA = new HashMap<ResourceLocation, Map<String, SerializationUtil.CapturedParam>>();
    private static final Map<ResourceLocation, Map<String, SerializationUtil.CapturedParam>> HOVER_PARAM_SCHEMA = new HashMap<ResourceLocation, Map<String, SerializationUtil.CapturedParam>>();
    private static final ResourceLocation DEFAULT_ID = new ResourceLocation("auratip", "fade_and_slide");
    private static final ResourceLocation DEFAULT_HOVER_ID = new ResourceLocation("auratip", "none");

    private AnimationType() {
    }

    public static TransitionAnimation resolve(ResourceLocation id) {
        return AnimationType.resolve(id, Map.of());
    }

    public static TransitionAnimation resolve(ResourceLocation id, Map<String, Dynamic<?>> params) {
        ResourceLocation key = id == null ? DEFAULT_ID : id;
        AnimationFactory factory = ANIMATIONS.get(key);
        if (factory == null) {
            factory = ANIMATIONS.get(DEFAULT_ID);
        }
        return factory.create(params == null ? Map.of() : params);
    }

    public static HoverAnimation resolveHover(ResourceLocation id) {
        return AnimationType.resolveHover(id, Map.of());
    }

    public static HoverAnimation resolveHover(ResourceLocation id, Map<String, Dynamic<?>> params) {
        ResourceLocation key = id == null ? DEFAULT_HOVER_ID : id;
        HoverAnimationFactory factory = HOVER_ANIMATIONS.get(key);
        if (factory == null) {
            factory = HOVER_ANIMATIONS.get(DEFAULT_HOVER_ID);
        }
        return factory.create(params == null ? Map.of() : params);
    }

    public static void registerInternal(ResourceLocation id, AnimationFactory factory) {
        if (id == null || factory == null) {
            return;
        }
        if (ANIMATIONS.containsKey(id)) {
            throw new IllegalStateException("Duplicate transition animation id: " + String.valueOf(id));
        }
        ANIMATIONS.put(id, factory);
    }

    public static void declareParamsInternal(ResourceLocation id, Map<String, SerializationUtil.CapturedParam> params) {
        if (id == null || params == null || params.isEmpty()) {
            return;
        }
        ANIMATION_PARAM_SCHEMA.put(id, Map.copyOf(params));
    }

    public static void registerOrReplaceInternal(ResourceLocation id, AnimationFactory factory) {
        if (id == null || factory == null) {
            return;
        }
        ANIMATIONS.put(id, factory);
    }

    public static void registerHoverInternal(ResourceLocation id, HoverAnimationFactory factory) {
        if (id == null || factory == null) {
            return;
        }
        if (HOVER_ANIMATIONS.containsKey(id)) {
            throw new IllegalStateException("Duplicate hover animation id: " + String.valueOf(id));
        }
        HOVER_ANIMATIONS.put(id, factory);
    }

    public static void declareHoverParamsInternal(ResourceLocation id, Map<String, SerializationUtil.CapturedParam> params) {
        if (id == null || params == null || params.isEmpty()) {
            return;
        }
        HOVER_PARAM_SCHEMA.put(id, Map.copyOf(params));
    }

    public static void registerOrReplaceHoverInternal(ResourceLocation id, HoverAnimationFactory factory) {
        if (id == null || factory == null) {
            return;
        }
        HOVER_ANIMATIONS.put(id, factory);
    }

    public static Set<ResourceLocation> listTransitionIds() {
        return Set.copyOf(ANIMATIONS.keySet());
    }

    public static Set<ResourceLocation> listHoverIds() {
        return Set.copyOf(HOVER_ANIMATIONS.keySet());
    }

    public static Map<String, SerializationUtil.CapturedParam> getDeclaredParams(ResourceLocation id) {
        if (id == null) {
            return Map.of();
        }
        Map<String, SerializationUtil.CapturedParam> schema = ANIMATION_PARAM_SCHEMA.get(id);
        return schema == null ? Map.of() : schema;
    }

    public static Map<String, SerializationUtil.CapturedParam> getDeclaredHoverParams(ResourceLocation id) {
        if (id == null) {
            return Map.of();
        }
        Map<String, SerializationUtil.CapturedParam> schema = HOVER_PARAM_SCHEMA.get(id);
        return schema == null ? Map.of() : schema;
    }

    static {
        AnimationType.registerInternal(DEFAULT_ID, FadeAndSlideTransitionAnimation::create);
        AnimationType.registerInternal(new ResourceLocation("auratip", "fade"), FadeTransitionAnimation::create);
        AnimationType.registerInternal(new ResourceLocation("auratip", "slide"), SlideTransitionAnimation::create);
        AnimationType.registerInternal(new ResourceLocation("auratip", "slide_in_left"), SlideInLeftTransitionAnimation::create);
        AnimationType.registerInternal(new ResourceLocation("auratip", "slide_in_right"), SlideInRightTransitionAnimation::create);
        AnimationType.registerInternal(new ResourceLocation("auratip", "slide_in_top"), SlideInTopTransitionAnimation::create);
        AnimationType.registerInternal(new ResourceLocation("auratip", "slide_in_bottom"), SlideTransitionAnimation::create);
        AnimationType.registerHoverInternal(DEFAULT_HOVER_ID, params -> NoneHoverAnimation.INSTANCE);
        AnimationType.registerHoverInternal(new ResourceLocation("auratip", "hover_float"), FloatHoverAnimation::create);
        AnimationType.registerHoverInternal(new ResourceLocation("auratip", "hover_shake"), ShakeHoverAnimation::create);
    }

    public static interface AnimationFactory {
        public TransitionAnimation create(Map<String, Dynamic<?>> var1);
    }

    public static interface HoverAnimationFactory {
        public HoverAnimation create(Map<String, Dynamic<?>> var1);
    }
}

