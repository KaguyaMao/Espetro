/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 */
package cc.sighs.auratip.api.tip;

import cc.sighs.auratip.data.TipData;
import cc.sighs.auratip.util.ColorUtil;
import cc.sighs.auratip.util.ComponentSerialization;
import cc.sighs.auratip.util.SerializationUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class TipBuilder {
    private final ResourceLocation id;
    private final Map<Integer, PageData> pages = new LinkedHashMap<Integer, PageData>();
    private ResourceLocation triggerType = new ResourceLocation("auratip", "first_join_world");
    private TipData.Trigger.Mode triggerMode = TipData.Trigger.Mode.ONCE;
    private int triggerCooldown;
    private ResourceLocation animationStyle = new ResourceLocation("auratip", "fade_and_slide");
    private float animationSpeed = 1.0f;
    private final Map<String, Object> animationParams = new HashMap<String, Object>();
    private final Map<String, Object> hoverAnimationParams = new HashMap<String, Object>();
    private ResourceLocation hoverAnimationStyle = new ResourceLocation("auratip", "none");
    private float hoverAnimationSpeed = 1.0f;
    private boolean hoverOnlyOnHover;
    private int stripeWidth = 4;
    private float stripeLengthFactor = 1.0f;
    private String themeColor;
    private int paddingTop = 12;
    private int paddingRight = 12;
    private int paddingBottom = 12;
    private int paddingLeft = 12;
    private int elementSpacing = 4;
    private boolean shadowEnabled;
    private int shadowColor = -1946157056;
    private int shadowOffsetX = 2;
    private int shadowOffsetY = 2;
    private int shadowSize = 4;
    private int width = 280;
    private int height = 180;
    private TipData.Position position = new TipData.Position("BOTTOM_CENTER", 0, 0, false);
    private TipData.Position animationFrom;
    private TipData.Position animationTo;
    private TipData.VisualSettings.BackgroundType backgroundType = TipData.VisualSettings.BackgroundType.GRADIENT;
    private List<String> backgroundColors = new ArrayList<String>();
    private int backgroundRadius = 8;
    private boolean backgroundRounded = true;
    private String backgroundImagePath;
    private int defaultDuration = 200;
    private boolean pauseOnHover = true;
    private String closeKey;
    private boolean allowPaging = true;
    private boolean showCloseButton = true;
    private boolean showPageIndicator = true;

    public TipBuilder(ResourceLocation id) {
        this.id = Objects.requireNonNull(id, "id");
    }

    public TipBuilder trigger(ResourceLocation type, TipData.Trigger.Mode mode, int cooldownTicks) {
        this.triggerType = Objects.requireNonNull(type, "type");
        this.triggerMode = mode == null ? TipData.Trigger.Mode.ONCE : mode;
        this.triggerCooldown = Math.max(0, cooldownTicks);
        return this;
    }

    public TipBuilder triggerOnce(ResourceLocation type) {
        return this.trigger(type, TipData.Trigger.Mode.ONCE, 0);
    }

    public TipBuilder triggerRepeatable(ResourceLocation type, int cooldownTicks) {
        return this.trigger(type, TipData.Trigger.Mode.REPEATABLE, cooldownTicks);
    }

    public TipBuilder visual(Consumer<VisualBuilder> visual) {
        if (visual == null) {
            return this;
        }
        VisualBuilder builder = new VisualBuilder();
        visual.accept(builder);
        return this;
    }

    public TipBuilder behavior(Consumer<BehaviorBuilder> behavior) {
        if (behavior == null) {
            return this;
        }
        BehaviorBuilder builder = new BehaviorBuilder();
        behavior.accept(builder);
        return this;
    }

    public TipBuilder page(int index, Consumer<PageBuilder> page) {
        if (page == null) {
            return this;
        }
        PageData data = this.pages.computeIfAbsent(index, i -> new PageData());
        PageBuilder builder = new PageBuilder(data);
        page.accept(builder);
        return this;
    }

    public TipData build() {
        if (this.pages.isEmpty()) {
            throw new IllegalStateException("Tip '" + String.valueOf(this.id) + "' has no pages. TipData.pages must contain at least one page.");
        }
        TipData.Trigger trigger = new TipData.Trigger(this.triggerType, this.triggerMode, this.triggerCooldown);
        List<String> colors = this.backgroundColors.isEmpty() ? List.of("#E0F7FF", "#B3E5FC") : new ArrayList<String>(this.backgroundColors);
        TipData.VisualSettings.ShadowConfig sConfig = this.shadowEnabled || this.shadowColor != -1946157056 || this.shadowOffsetX != 2 || this.shadowOffsetY != 2 || this.shadowSize != 4 ? new TipData.VisualSettings.ShadowConfig(this.shadowEnabled, this.shadowColor, this.shadowOffsetX, this.shadowOffsetY, this.shadowSize) : null;
        TipData.VisualSettings.Background bg = new TipData.VisualSettings.Background(this.backgroundType, colors, this.backgroundRadius, this.backgroundRounded, Optional.ofNullable(this.backgroundImagePath), Optional.ofNullable(sConfig));
        TipData.AnimationParams animParams = new TipData.AnimationParams(SerializationUtil.convertMapToDynamic(this.animationParams), SerializationUtil.convertMapToDynamic(this.hoverAnimationParams));
        TipData.VisualSettings visual = new TipData.VisualSettings(this.animationStyle, bg, Optional.ofNullable(this.themeColor), this.width, this.height, this.position, this.animationSpeed, Optional.ofNullable(this.animationFrom), Optional.ofNullable(this.animationTo), this.hoverAnimationStyle, this.hoverAnimationSpeed, this.hoverOnlyOnHover, this.stripeWidth, this.stripeLengthFactor, animParams, new TipData.LayoutConfig(new TipData.Padding(this.paddingTop, this.paddingRight, this.paddingBottom, this.paddingLeft), this.elementSpacing));
        TipData.Behavior behavior = new TipData.Behavior(this.defaultDuration, this.pauseOnHover, Optional.ofNullable(this.closeKey), this.allowPaging, this.showCloseButton, this.showPageIndicator);
        ArrayList<TipData.Page> pageList = new ArrayList<TipData.Page>();
        for (Map.Entry<Integer, PageData> entry : this.pages.entrySet()) {
            boolean hasContent;
            Integer index = entry.getKey();
            PageData data = entry.getValue();
            boolean bl = hasContent = data.title != null || data.subtitle != null || data.content != null || data.image != null;
            if (!hasContent) {
                throw new IllegalStateException("Tip '" + String.valueOf(this.id) + "' page_index=" + index + " has no content.");
            }
            pageList.add(new TipData.Page(index, Optional.ofNullable(data.title), Optional.ofNullable(data.subtitle), Optional.ofNullable(data.content), Optional.ofNullable(data.image), Optional.ofNullable(data.badge)));
        }
        return new TipData(this.id, trigger, visual, behavior, pageList);
    }

    public class VisualBuilder {
        public VisualBuilder animationStyle(ResourceLocation style) {
            TipBuilder.this.animationStyle = Objects.requireNonNull(style, "style");
            return this;
        }

        public VisualBuilder animationSpeed(float speed) {
            TipBuilder.this.animationSpeed = speed;
            return this;
        }

        public VisualBuilder hoverAnimationStyle(ResourceLocation style) {
            TipBuilder.this.hoverAnimationStyle = Objects.requireNonNull(style, "style");
            return this;
        }

        public VisualBuilder hoverAnimationSpeed(float speed) {
            TipBuilder.this.hoverAnimationSpeed = speed;
            return this;
        }

        public VisualBuilder hoverOnlyOnHover(boolean value) {
            TipBuilder.this.hoverOnlyOnHover = value;
            return this;
        }

        public VisualBuilder stripeWidth(int width) {
            TipBuilder.this.stripeWidth = width;
            return this;
        }

        public VisualBuilder stripeLengthFactor(float factor) {
            TipBuilder.this.stripeLengthFactor = factor;
            return this;
        }

        public VisualBuilder padding(int px) {
            TipBuilder.this.paddingBottom = TipBuilder.this.paddingLeft = px;
            TipBuilder.this.paddingRight = TipBuilder.this.paddingLeft;
            TipBuilder.this.paddingTop = TipBuilder.this.paddingLeft;
            return this;
        }

        public VisualBuilder padding(int top, int right, int bottom, int left) {
            TipBuilder.this.paddingTop = top;
            TipBuilder.this.paddingRight = right;
            TipBuilder.this.paddingBottom = bottom;
            TipBuilder.this.paddingLeft = left;
            return this;
        }

        public VisualBuilder elementSpacing(int px) {
            TipBuilder.this.elementSpacing = px;
            return this;
        }

        public VisualBuilder animParam(String key, Object value) {
            if (key != null && !key.isEmpty() && value != null) {
                TipBuilder.this.animationParams.put(key, value);
            }
            return this;
        }

        public VisualBuilder animParams(Map<String, ?> params) {
            if (params != null && !params.isEmpty()) {
                TipBuilder.this.animationParams.putAll(params);
            }
            return this;
        }

        public VisualBuilder hoverParam(String key, Object value) {
            if (key != null && !key.isEmpty() && value != null) {
                TipBuilder.this.hoverAnimationParams.put(key, value);
            }
            return this;
        }

        public VisualBuilder hoverParams(Map<String, ?> params) {
            if (params != null && !params.isEmpty()) {
                TipBuilder.this.hoverAnimationParams.putAll(params);
            }
            return this;
        }

        public VisualBuilder themeColor(String argbHex) {
            TipBuilder.this.themeColor = argbHex;
            return this;
        }

        public VisualBuilder size(int w, int h) {
            TipBuilder.this.width = w;
            TipBuilder.this.height = h;
            return this;
        }

        public VisualBuilder positionPreset(String preset) {
            TipBuilder.this.position = new TipData.Position(preset, 0, 0, false);
            return this;
        }

        public VisualBuilder positionAbsolute(int x, int y) {
            TipBuilder.this.position = new TipData.Position(null, x, y, true);
            return this;
        }

        public VisualBuilder animationFromPreset(String preset) {
            TipBuilder.this.animationFrom = new TipData.Position(preset, 0, 0, false);
            return this;
        }

        public VisualBuilder animationFromAbsolute(int x, int y) {
            TipBuilder.this.animationFrom = new TipData.Position(null, x, y, true);
            return this;
        }

        public VisualBuilder animationToPreset(String preset) {
            TipBuilder.this.animationTo = new TipData.Position(preset, 0, 0, false);
            return this;
        }

        public VisualBuilder animationToAbsolute(int x, int y) {
            TipBuilder.this.animationTo = new TipData.Position(null, x, y, true);
            return this;
        }

        public VisualBuilder background(TipData.VisualSettings.BackgroundType type, List<String> colors, int radius) {
            TipBuilder.this.backgroundType = type == null ? TipData.VisualSettings.BackgroundType.GRADIENT : type;
            TipBuilder.this.backgroundColors = colors == null ? new ArrayList<String>() : new ArrayList<String>(colors);
            TipBuilder.this.backgroundRadius = radius;
            return this;
        }

        public VisualBuilder shadow(boolean enabled) {
            return this.shadow(enabled, -1946157056, 2, 2, 4);
        }

        public VisualBuilder shadow(boolean enabled, int color, int offsetX, int offsetY, int size) {
            TipBuilder.this.shadowEnabled = enabled;
            TipBuilder.this.shadowColor = color;
            TipBuilder.this.shadowOffsetX = offsetX;
            TipBuilder.this.shadowOffsetY = offsetY;
            TipBuilder.this.shadowSize = size;
            return this;
        }

        public VisualBuilder shadow(boolean enabled, String color, int offsetX, int offsetY, int size) {
            int argb = color != null && !color.isBlank() ? ColorUtil.parseArgb(color) : -1946157056;
            return this.shadow(enabled, argb, offsetX, offsetY, size);
        }

        public VisualBuilder backgroundRounded(boolean value) {
            TipBuilder.this.backgroundRounded = value;
            return this;
        }

        public VisualBuilder backgroundImage(String path) {
            TipBuilder.this.backgroundImagePath = path;
            return this;
        }
    }

    public class BehaviorBuilder {
        public BehaviorBuilder duration(int ticks) {
            TipBuilder.this.defaultDuration = ticks;
            return this;
        }

        public BehaviorBuilder pauseOnHover(boolean pause) {
            TipBuilder.this.pauseOnHover = pause;
            return this;
        }

        public BehaviorBuilder closeKey(String key) {
            TipBuilder.this.closeKey = key;
            return this;
        }

        public BehaviorBuilder allowPaging(boolean allow) {
            TipBuilder.this.allowPaging = allow;
            return this;
        }

        public BehaviorBuilder showCloseButton(boolean show) {
            TipBuilder.this.showCloseButton = show;
            return this;
        }

        public BehaviorBuilder showPageIndicator(boolean show) {
            TipBuilder.this.showPageIndicator = show;
            return this;
        }
    }

    public static class PageData {
        ComponentSerialization.TextElement title;
        ComponentSerialization.TextElement subtitle;
        ComponentSerialization.TextElement content;
        TipData.ImageElement image;
        TipData.Badge badge;
    }

    public static class PageBuilder {
        private final PageData data;

        public PageBuilder(PageData data) {
            this.data = Objects.requireNonNull(data, "data");
        }

        public PageBuilder title(Component text, float scale, int lineSpacing) {
            this.data.title = new ComponentSerialization.TextElement(text, scale, lineSpacing, Optional.empty());
            return this;
        }

        public PageBuilder title(Component text) {
            return this.title(text, 1.0f, 0);
        }

        public PageBuilder subtitle(Component text, float scale, int lineSpacing) {
            this.data.subtitle = new ComponentSerialization.TextElement(text, scale, lineSpacing, Optional.empty());
            return this;
        }

        public PageBuilder subtitle(Component text) {
            return this.subtitle(text, 1.0f, 0);
        }

        public PageBuilder content(Component text, float scale, int lineSpacing) {
            this.data.content = new ComponentSerialization.TextElement(text, scale, lineSpacing, Optional.empty());
            return this;
        }

        public PageBuilder content(Component text) {
            return this.content(text, 1.0f, 0);
        }

        public PageBuilder titleDivider(int thickness, int marginTop, int marginBottom, float length, String colorHex) {
            this.data.title = new ComponentSerialization.TextElement((Component)(this.data.title != null ? this.data.title.text() : Component.m_237119_()), this.data.title != null ? this.data.title.scale() : 1.0f, this.data.title != null ? this.data.title.lineSpacing() : 0, Optional.of(new ComponentSerialization.Divider(thickness, marginTop, marginBottom, length, colorHex == null ? "" : colorHex)));
            return this;
        }

        public PageBuilder titleDivider(int thickness, int marginTop, int marginBottom) {
            return this.titleDivider(thickness, marginTop, marginBottom, 1.0f, null);
        }

        public PageBuilder titleDivider() {
            return this.titleDivider(1, 4, 4, 1.0f, null);
        }

        public PageBuilder image(String path, String preset, int width, int height) {
            TipData.Position pos = new TipData.Position(preset, 0, 0, false);
            this.data.image = new TipData.ImageElement(path, pos, new int[]{width, height}, 1.0f);
            return this;
        }

        public PageBuilder image(String path, int x, int y, int width, int height) {
            TipData.Position pos = new TipData.Position(null, x, y, true);
            this.data.image = new TipData.ImageElement(path, pos, new int[]{width, height}, 1.0f);
            return this;
        }

        public PageBuilder imageScaled(String path, String preset, int width, int height, float scale) {
            TipData.Position pos = new TipData.Position(preset, 0, 0, false);
            this.data.image = new TipData.ImageElement(path, pos, new int[]{width, height}, scale);
            return this;
        }

        public PageBuilder imageScaled(String path, int x, int y, int width, int height, float scale) {
            TipData.Position pos = new TipData.Position(null, x, y, true);
            this.data.image = new TipData.ImageElement(path, pos, new int[]{width, height}, scale);
            return this;
        }

        public PageBuilder badge(Component text) {
            return this.badge(text, 0.7f, 0, -872415232, 4, "BOTTOM_RIGHT");
        }

        public PageBuilder badge(Component text, int bgColor, int radius, String position) {
            this.data.badge = new TipData.Badge(new ComponentSerialization.TextElement(Objects.requireNonNull(text, "text"), 0.7f, 0, Optional.empty()), bgColor, radius, new TipData.Position(position, 0, 0, false));
            return this;
        }

        public PageBuilder badge(Component text, String bgColor, int radius, String position) {
            int argb = bgColor != null && !bgColor.isBlank() ? ColorUtil.parseArgb(bgColor) : -872415232;
            this.data.badge = new TipData.Badge(new ComponentSerialization.TextElement(Objects.requireNonNull(text, "text"), 0.7f, 0, Optional.empty()), argb, radius, new TipData.Position(position, 0, 0, false));
            return this;
        }

        public PageBuilder badge(Component text, float scale, int lineSpacing, int bgColor, int radius, String position) {
            this.data.badge = new TipData.Badge(new ComponentSerialization.TextElement(Objects.requireNonNull(text, "text"), scale, lineSpacing, Optional.empty()), bgColor, radius, new TipData.Position(position, 0, 0, false));
            return this;
        }

        public PageBuilder badge(Component text, float scale, int lineSpacing, String bgColor, int radius, String position) {
            int argb = bgColor != null && !bgColor.isBlank() ? ColorUtil.parseArgb(bgColor) : -872415232;
            return this.badge(text, scale, lineSpacing, argb, radius, position);
        }
    }
}

