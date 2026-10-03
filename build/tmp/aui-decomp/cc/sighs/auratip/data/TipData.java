/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cc.sighs.oelib.data.api.DataDriven
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.resources.ResourceLocation
 */
package cc.sighs.auratip.data;

import cc.sighs.auratip.data.validator.TipDataValidator;
import cc.sighs.auratip.util.CodecUtil;
import cc.sighs.auratip.util.ComponentSerialization;
import cc.sighs.oelib.data.api.DataDriven;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;

@DataDriven(modid="auratip", folder="tips", syncToClient=true, supportArray=true, validator=TipDataValidator.class)
public record TipData(ResourceLocation id, Trigger trigger, VisualSettings visualSettings, Behavior behavior, List<Page> pages) {
    public static final Codec<TipData> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)ResourceLocation.f_135803_.fieldOf("id").forGetter(TipData::id), (App)Trigger.CODEC.fieldOf("trigger").forGetter(TipData::trigger), (App)VisualSettings.CODEC.fieldOf("visual_settings").forGetter(TipData::visualSettings), (App)Behavior.CODEC.fieldOf("behavior").forGetter(TipData::behavior), (App)Page.CODEC.listOf().fieldOf("pages").forGetter(TipData::pages)).apply((Applicative)instance, TipData::new));

    public record Trigger(ResourceLocation type, Mode mode, int cooldown) {
        public static final Codec<Trigger> CODEC = RecordCodecBuilder.create(inst -> inst.group((App)ResourceLocation.f_135803_.fieldOf("type").forGetter(Trigger::type), (App)Mode.CODEC.optionalFieldOf("mode", (Object)Mode.ONCE).forGetter(Trigger::mode), (App)Codec.INT.optionalFieldOf("cooldown", (Object)0).forGetter(Trigger::cooldown)).apply((Applicative)inst, Trigger::new));

        public static enum Mode {
            ONCE,
            REPEATABLE;

            public static final Codec<Mode> CODEC;

            static {
                CODEC = CodecUtil.enumCodec(Mode.class);
            }
        }
    }

    public record VisualSettings(ResourceLocation animationStyle, Background background, Optional<String> themeColor, int width, int height, Position position, float animationSpeed, Optional<Position> animationFrom, Optional<Position> animationTo, ResourceLocation hoverAnimationStyle, float hoverAnimationSpeed, boolean hoverOnlyOnHover, int stripeWidth, float stripeLengthFactor, AnimationParams animationParams, LayoutConfig layout) {
        public static final Codec<VisualSettings> CODEC = RecordCodecBuilder.create(inst -> inst.group((App)ResourceLocation.f_135803_.optionalFieldOf("animation_style", (Object)new ResourceLocation("auratip", "fade_and_slide")).forGetter(VisualSettings::animationStyle), (App)Background.CODEC.optionalFieldOf("background", (Object)new Background(BackgroundType.GRADIENT, List.of("#FFE0F7FF", "#FFB3E5FC"), 8, true, Optional.empty(), Optional.empty())).forGetter(VisualSettings::background), (App)Codec.STRING.optionalFieldOf("theme_color").forGetter(VisualSettings::themeColor), (App)Codec.INT.optionalFieldOf("width", (Object)280).forGetter(VisualSettings::width), (App)Codec.INT.optionalFieldOf("height", (Object)180).forGetter(VisualSettings::height), (App)Position.CODEC.optionalFieldOf("position", (Object)new Position("BOTTOM_CENTER", 0, 0, false)).forGetter(VisualSettings::position), (App)Codec.FLOAT.optionalFieldOf("animation_speed", (Object)Float.valueOf(1.0f)).forGetter(VisualSettings::animationSpeed), (App)Position.CODEC.optionalFieldOf("animation_from").forGetter(VisualSettings::animationFrom), (App)Position.CODEC.optionalFieldOf("animation_to").forGetter(VisualSettings::animationTo), (App)ResourceLocation.f_135803_.optionalFieldOf("hover_animation_style", (Object)new ResourceLocation("auratip", "none")).forGetter(VisualSettings::hoverAnimationStyle), (App)Codec.FLOAT.optionalFieldOf("hover_animation_speed", (Object)Float.valueOf(1.0f)).forGetter(VisualSettings::hoverAnimationSpeed), (App)Codec.BOOL.optionalFieldOf("hover_only_on_hover", (Object)false).forGetter(VisualSettings::hoverOnlyOnHover), (App)Codec.INT.optionalFieldOf("stripe_width", (Object)4).forGetter(VisualSettings::stripeWidth), (App)Codec.FLOAT.optionalFieldOf("stripe_length_factor", (Object)Float.valueOf(1.0f)).forGetter(VisualSettings::stripeLengthFactor), (App)AnimationParams.CODEC.optionalFieldOf("animation_params", (Object)AnimationParams.EMPTY).forGetter(VisualSettings::animationParams), (App)LayoutConfig.CODEC.optionalFieldOf("layout", (Object)LayoutConfig.DEFAULT).forGetter(VisualSettings::layout)).apply((Applicative)inst, VisualSettings::new));

        public record Background(BackgroundType type, List<String> colors, int borderRadius, boolean rounded, Optional<String> imagePath, Optional<ShadowConfig> shadow) {
            public static final Codec<Background> CODEC = RecordCodecBuilder.create(inst -> inst.group((App)BackgroundType.CODEC.optionalFieldOf("type", (Object)BackgroundType.GRADIENT).forGetter(Background::type), (App)Codec.STRING.listOf().optionalFieldOf("colors", List.of("#FFE0F7FF", "#FFB3E5FC")).forGetter(Background::colors), (App)Codec.INT.optionalFieldOf("border_radius", (Object)8).forGetter(Background::borderRadius), (App)Codec.BOOL.optionalFieldOf("rounded", (Object)true).forGetter(Background::rounded), (App)Codec.STRING.optionalFieldOf("image_path").forGetter(Background::imagePath), (App)ShadowConfig.CODEC.optionalFieldOf("shadow").forGetter(Background::shadow)).apply((Applicative)inst, Background::new));
        }

        public static enum BackgroundType {
            GRADIENT,
            SOLID,
            IMAGE;

            public static final Codec<BackgroundType> CODEC;

            static {
                CODEC = CodecUtil.enumCodec(BackgroundType.class);
            }
        }

        public record ShadowConfig(boolean enabled, int color, int offsetX, int offsetY, int size) {
            public static final Codec<ShadowConfig> CODEC = RecordCodecBuilder.create(inst -> inst.group((App)Codec.BOOL.optionalFieldOf("enabled", (Object)false).forGetter(ShadowConfig::enabled), (App)CodecUtil.argbOrInt().optionalFieldOf("color", (Object)-1946157056).forGetter(ShadowConfig::color), (App)Codec.INT.optionalFieldOf("offset_x", (Object)2).forGetter(ShadowConfig::offsetX), (App)Codec.INT.optionalFieldOf("offset_y", (Object)2).forGetter(ShadowConfig::offsetY), (App)Codec.INT.optionalFieldOf("size", (Object)4).forGetter(ShadowConfig::size)).apply((Applicative)inst, ShadowConfig::new));
        }
    }

    public record Behavior(int defaultDuration, boolean pauseTimerOnHover, Optional<String> closableByKey, boolean allowPaging, boolean showCloseButton, boolean showPageIndicator) {
        public static final Codec<Behavior> CODEC = RecordCodecBuilder.create(inst -> inst.group((App)Codec.INT.optionalFieldOf("default_duration", (Object)200).forGetter(Behavior::defaultDuration), (App)Codec.BOOL.optionalFieldOf("pause_timer_on_hover", (Object)true).forGetter(Behavior::pauseTimerOnHover), (App)Codec.STRING.optionalFieldOf("closable_by_key").forGetter(Behavior::closableByKey), (App)Codec.BOOL.optionalFieldOf("allow_paging", (Object)true).forGetter(Behavior::allowPaging), (App)Codec.BOOL.optionalFieldOf("show_close_button", (Object)true).forGetter(Behavior::showCloseButton), (App)Codec.BOOL.optionalFieldOf("show_page_indicator", (Object)true).forGetter(Behavior::showPageIndicator)).apply((Applicative)inst, Behavior::new));
    }

    public record Page(int pageIndex, Optional<ComponentSerialization.TextElement> title, Optional<ComponentSerialization.TextElement> subtitle, Optional<ComponentSerialization.TextElement> content, Optional<ImageElement> image, Optional<Badge> badge) {
        public static final Codec<Page> CODEC = RecordCodecBuilder.create(inst -> inst.group((App)Codec.INT.fieldOf("page_index").forGetter(Page::pageIndex), (App)ComponentSerialization.TextElement.CODEC.optionalFieldOf("title").forGetter(Page::title), (App)ComponentSerialization.TextElement.CODEC.optionalFieldOf("subtitle").forGetter(Page::subtitle), (App)ComponentSerialization.TextElement.CODEC.optionalFieldOf("content").forGetter(Page::content), (App)ImageElement.CODEC.optionalFieldOf("image").forGetter(Page::image), (App)Badge.CODEC.optionalFieldOf("badge").forGetter(Page::badge)).apply((Applicative)inst, Page::new));
    }

    public record ImageElement(String path, Position position, int[] size, float scale) {
        public static final Codec<ImageElement> CODEC = RecordCodecBuilder.create(inst -> inst.group((App)Codec.STRING.fieldOf("path").forGetter(ImageElement::path), (App)Position.CODEC.optionalFieldOf("position", (Object)new Position("TOP_CENTER", 0, 0, false)).forGetter(ImageElement::position), (App)Codec.INT.listOf().optionalFieldOf("size", List.of(Integer.valueOf(64), Integer.valueOf(64))).xmap(list -> list.stream().mapToInt(Integer::intValue).toArray(), arr -> List.of(Integer.valueOf(arr[0]), Integer.valueOf(arr[1]))).forGetter(ImageElement::size), (App)Codec.FLOAT.optionalFieldOf("scale", (Object)Float.valueOf(1.0f)).forGetter(ImageElement::scale)).apply((Applicative)inst, ImageElement::new));

        public ImageElement {
            if (size == null || size.length != 2) {
                size = new int[]{64, 64};
            }
            if (scale <= 0.0f) {
                scale = 1.0f;
            }
        }
    }

    public record Badge(ComponentSerialization.TextElement text, int backgroundColor, int radius, Position position) {
        public static final Codec<Badge> CODEC = RecordCodecBuilder.create(inst -> inst.group((App)ComponentSerialization.TextElement.CODEC.fieldOf("text").forGetter(Badge::text), (App)CodecUtil.argbOrInt().optionalFieldOf("background_color", (Object)-872415232).forGetter(Badge::backgroundColor), (App)Codec.INT.optionalFieldOf("radius", (Object)4).forGetter(Badge::radius), (App)Position.CODEC.optionalFieldOf("position", (Object)new Position("BOTTOM_RIGHT", 0, 0, false)).forGetter(Badge::position)).apply((Applicative)inst, Badge::new));
    }

    public record AnimationParams(Map<String, Dynamic<?>> params, Map<String, Dynamic<?>> hoverParams) {
        public static final Codec<AnimationParams> CODEC = RecordCodecBuilder.create(inst -> inst.group((App)Codec.unboundedMap((Codec)Codec.STRING, (Codec)Codec.PASSTHROUGH).optionalFieldOf("params", Map.of()).forGetter(AnimationParams::params), (App)Codec.unboundedMap((Codec)Codec.STRING, (Codec)Codec.PASSTHROUGH).optionalFieldOf("hover_params", Map.of()).forGetter(AnimationParams::hoverParams)).apply((Applicative)inst, AnimationParams::new));
        public static final AnimationParams EMPTY = new AnimationParams(Map.of(), Map.of());
    }

    public record LayoutConfig(Padding padding, int elementSpacing) {
        public static final LayoutConfig DEFAULT = new LayoutConfig(Padding.DEFAULT, 4);
        public static final Codec<LayoutConfig> CODEC = RecordCodecBuilder.create(inst -> inst.group((App)Padding.CODEC.optionalFieldOf("padding", (Object)Padding.DEFAULT).forGetter(LayoutConfig::padding), (App)Codec.INT.optionalFieldOf("element_spacing", (Object)4).forGetter(LayoutConfig::elementSpacing)).apply((Applicative)inst, LayoutConfig::new));
    }

    public record Padding(int top, int right, int bottom, int left) {
        public static final Padding DEFAULT = new Padding(12, 12, 12, 12);
        private static final Codec<Padding> OBJECT_CODEC = RecordCodecBuilder.create(inst -> inst.group((App)Codec.INT.optionalFieldOf("top", (Object)12).forGetter(Padding::top), (App)Codec.INT.optionalFieldOf("right", (Object)12).forGetter(Padding::right), (App)Codec.INT.optionalFieldOf("bottom", (Object)12).forGetter(Padding::bottom), (App)Codec.INT.optionalFieldOf("left", (Object)12).forGetter(Padding::left)).apply((Applicative)inst, Padding::new));
        public static final Codec<Padding> CODEC = CodecUtil.intOrListOrObject(v -> new Padding((int)v, (int)v, (int)v, (int)v), list -> new Padding(!list.isEmpty() ? (Integer)list.get(0) : 12, list.size() > 1 ? (Integer)list.get(1) : 12, list.size() > 2 ? (Integer)list.get(2) : 12, list.size() > 3 ? (Integer)list.get(3) : 12), padding -> padding.top == padding.right && padding.top == padding.bottom && padding.top == padding.left ? padding.top : 0, padding -> List.of(Integer.valueOf(padding.top), Integer.valueOf(padding.right), Integer.valueOf(padding.bottom), Integer.valueOf(padding.left)), OBJECT_CODEC);
    }

    public record Position(String preset, int x, int y, boolean absolute) {
        public static final Codec<Position> CODEC = CodecUtil.stringOrIntList(p -> new Position((String)p, 0, 0, false), list -> new Position(null, list.isEmpty() ? 0 : (Integer)list.get(0), list.size() > 1 ? (Integer)list.get(1) : 0, true), pos -> pos.absolute ? null : pos.preset, pos -> List.of(Integer.valueOf(pos.x), Integer.valueOf(pos.y)));
    }
}

