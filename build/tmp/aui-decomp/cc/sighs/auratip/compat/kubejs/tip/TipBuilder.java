/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 */
package cc.sighs.auratip.compat.kubejs.tip;

import cc.sighs.auratip.api.tip.TipBuilder;
import cc.sighs.auratip.data.TipData;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.resources.ResourceLocation;

public final class TipBuilder {
    private final cc.sighs.auratip.api.tip.TipBuilder delegate;

    public TipBuilder(String id) {
        this.delegate = new cc.sighs.auratip.api.tip.TipBuilder(TipBuilder.normalizeId(id));
    }

    public TipBuilder trigger(String type, String mode, int cooldownTicks) {
        ResourceLocation typeId = TipBuilder.normalizeType(type);
        TipData.Trigger.Mode parsed = TipData.Trigger.Mode.ONCE;
        if (mode != null && !mode.isEmpty()) {
            parsed = TipData.Trigger.Mode.valueOf(mode.toUpperCase(Locale.ROOT));
        }
        this.delegate.trigger(typeId, parsed, cooldownTicks);
        return this;
    }

    public TipBuilder visual(Consumer<VisualBuilder> visual) {
        if (visual == null) {
            return this;
        }
        this.delegate.visual((TipBuilder.VisualBuilder v) -> visual.accept(new VisualBuilder((TipBuilder.VisualBuilder)v)));
        return this;
    }

    public TipBuilder behavior(Consumer<TipBuilder.BehaviorBuilder> behavior) {
        this.delegate.behavior(behavior);
        return this;
    }

    public TipBuilder page(int index, Consumer<TipBuilder.PageBuilder> page) {
        this.delegate.page(index, page);
        return this;
    }

    public TipData build() {
        return this.delegate.build();
    }

    private static ResourceLocation normalizeId(String id) {
        if (id == null || id.isEmpty()) {
            return new ResourceLocation("kubejs", "tip");
        }
        if (id.indexOf(58) < 0) {
            return new ResourceLocation("kubejs", id);
        }
        return new ResourceLocation(id);
    }

    private static ResourceLocation normalizeType(String type) {
        String raw;
        String string = raw = type == null ? "" : type.trim();
        if (raw.isEmpty()) {
            return new ResourceLocation("kubejs", "trigger");
        }
        String lower = raw.toLowerCase(Locale.ROOT);
        if (lower.indexOf(58) >= 0) {
            ResourceLocation parsed = ResourceLocation.m_135820_((String)lower);
            if (parsed == null) {
                throw new IllegalStateException("Invalid trigger type id: " + raw);
            }
            return parsed;
        }
        return new ResourceLocation("kubejs", lower);
    }

    private static ResourceLocation normalizeAnimation(String id) {
        String raw;
        String string = raw = id == null ? "" : id.trim();
        if (raw.isEmpty()) {
            return new ResourceLocation("kubejs", "animation");
        }
        String lower = raw.toLowerCase(Locale.ROOT);
        if (lower.indexOf(58) >= 0) {
            ResourceLocation parsed = ResourceLocation.m_135820_((String)lower);
            if (parsed == null) {
                throw new IllegalStateException("Invalid animation id: " + raw);
            }
            return parsed;
        }
        return new ResourceLocation("kubejs", lower);
    }

    public static final class VisualBuilder {
        private final TipBuilder.VisualBuilder delegate;

        private VisualBuilder(TipBuilder.VisualBuilder delegate) {
            this.delegate = Objects.requireNonNull(delegate, "delegate");
        }

        public VisualBuilder animationStyle(String id) {
            this.delegate.animationStyle(TipBuilder.normalizeAnimation(id));
            return this;
        }

        public VisualBuilder animationSpeed(float speed) {
            this.delegate.animationSpeed(speed);
            return this;
        }

        public VisualBuilder hoverAnimationStyle(String id) {
            this.delegate.hoverAnimationStyle(TipBuilder.normalizeAnimation(id));
            return this;
        }

        public VisualBuilder hoverAnimationSpeed(float speed) {
            this.delegate.hoverAnimationSpeed(speed);
            return this;
        }

        public VisualBuilder hoverOnlyOnHover(boolean value) {
            this.delegate.hoverOnlyOnHover(value);
            return this;
        }

        public VisualBuilder stripeWidth(int width) {
            this.delegate.stripeWidth(width);
            return this;
        }

        public VisualBuilder stripeLengthFactor(float factor) {
            this.delegate.stripeLengthFactor(factor);
            return this;
        }

        public VisualBuilder animParam(String key, Object value) {
            this.delegate.animParam(key, value);
            return this;
        }

        public VisualBuilder animParams(Map<String, ?> params) {
            this.delegate.animParams(params);
            return this;
        }

        public VisualBuilder hoverParam(String key, Object value) {
            this.delegate.hoverParam(key, value);
            return this;
        }

        public VisualBuilder hoverParams(Map<String, ?> params) {
            this.delegate.hoverParams(params);
            return this;
        }

        public VisualBuilder themeColor(String argbHex) {
            this.delegate.themeColor(argbHex);
            return this;
        }

        public VisualBuilder size(int w, int h) {
            this.delegate.size(w, h);
            return this;
        }

        public VisualBuilder position(String preset) {
            this.delegate.positionPreset(preset);
            return this;
        }

        public VisualBuilder position(int x, int y) {
            this.delegate.positionAbsolute(x, y);
            return this;
        }

        public VisualBuilder animationFrom(String preset) {
            this.delegate.animationFromPreset(preset);
            return this;
        }

        public VisualBuilder animationFrom(int x, int y) {
            this.delegate.animationFromAbsolute(x, y);
            return this;
        }

        public VisualBuilder animationTo(String preset) {
            this.delegate.animationToPreset(preset);
            return this;
        }

        public VisualBuilder animationTo(int x, int y) {
            this.delegate.animationToAbsolute(x, y);
            return this;
        }

        public VisualBuilder background(String type, List<String> colors, int radius) {
            TipData.VisualSettings.BackgroundType parsed = TipData.VisualSettings.BackgroundType.valueOf(type.toUpperCase(Locale.ROOT));
            this.delegate.background(parsed, colors, radius);
            return this;
        }

        public VisualBuilder backgroundRounded(boolean value) {
            this.delegate.backgroundRounded(value);
            return this;
        }

        public VisualBuilder backgroundImage(String path) {
            this.delegate.backgroundImage(path);
            return this;
        }

        public VisualBuilder padding(int px) {
            this.delegate.padding(px);
            return this;
        }

        public VisualBuilder padding(int top, int right, int bottom, int left) {
            this.delegate.padding(top, right, bottom, left);
            return this;
        }

        public VisualBuilder elementSpacing(int px) {
            this.delegate.elementSpacing(px);
            return this;
        }

        public VisualBuilder shadow(boolean enabled) {
            this.delegate.shadow(enabled);
            return this;
        }

        public VisualBuilder shadow(boolean enabled, String color, int offsetX, int offsetY, int size) {
            this.delegate.shadow(enabled, color, offsetX, offsetY, size);
            return this;
        }
    }
}

