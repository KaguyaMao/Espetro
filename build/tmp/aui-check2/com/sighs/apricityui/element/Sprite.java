/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.element;

import com.sighs.apricityui.ApricityUI;
import com.sighs.apricityui.element.Div;
import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.loader.Loader;
import com.sighs.apricityui.registry.annotation.ElementRegister;
import com.sighs.apricityui.resource.async.image.ImageAsyncHandler;
import com.sighs.apricityui.resource.async.image.ImageHandle;
import com.sighs.apricityui.style.Animation;
import com.sighs.apricityui.style.Style;
import com.sighs.apricityui.task.AbstractAsyncHandler;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.regex.Pattern;

@ElementRegister(value="SPRITE")
public class Sprite
extends Div {
    public static final String TAG_NAME = "SPRITE";
    private static final Set<String> MANAGED_STYLE_KEYS = Set.of("background-image", "background-repeat", "background-position", "background-size", "animation", "animation-name", "animation-duration", "animation-delay", "animation-iteration-count", "animation-direction", "animation-fill-mode", "animation-timing-function", "animation-play-state");
    private static final Set<String> SPRITE_ATTRS = Set.of("src", "steps", "direction", "duration", "loop", "steps-mode", "autoplay", "initialframe", "fit");
    private static final Pattern TIME_PATTERN = Pattern.compile("^\\+?([0-9]*\\.?[0-9]+)(s|ms)$");
    private static final String DEFAULT_DURATION = "1s";
    private boolean internalStyleSync = false;
    private String userInlineStyle = "";
    private String managedInlineStyle = "";
    private boolean frameMetricsPending = false;
    private String pendingFrameMetricsSrc = "";
    private final Set<String> invalidMetricsWarnings = new HashSet<String>();

    public Sprite(Document document) {
        super(document);
    }

    @Override
    protected void onInitFromDom(Element origin) {
        this.userInlineStyle = Sprite.sanitizeUserStyle(this.getAttribute("style"));
        this.rebuildSpriteRuntime();
        this.applyManagedStyle();
    }

    @Override
    public void setAttribute(String name, String value) {
        if (this.internalStyleSync) {
            super.setAttribute(name, value);
            return;
        }
        String key = Sprite.normalizeAttr(name);
        if ("style".equals(key)) {
            this.userInlineStyle = Sprite.sanitizeUserStyle(value);
            this.rebuildSpriteRuntime();
            this.applyManagedStyle();
            return;
        }
        super.setAttribute(name, value);
        if (this.shouldRebuildForAttr(key)) {
            this.rebuildSpriteRuntime();
            this.applyManagedStyle();
        }
    }

    @Override
    public void removeAttribute(String name) {
        if (this.internalStyleSync) {
            super.removeAttribute(name);
            return;
        }
        String key = Sprite.normalizeAttr(name);
        if ("style".equals(key)) {
            this.userInlineStyle = "";
            this.rebuildSpriteRuntime();
            this.applyManagedStyle();
            return;
        }
        super.removeAttribute(name);
        if (this.shouldRebuildForAttr(key)) {
            this.rebuildSpriteRuntime();
            this.applyManagedStyle();
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.frameMetricsPending || this.pendingFrameMetricsSrc.isBlank()) {
            return;
        }
        ImageHandle handle = ImageAsyncHandler.INSTANCE.request(this.pendingFrameMetricsSrc, this, false);
        if (!Sprite.isHandleReady(handle)) {
            return;
        }
        this.clearPendingFrameMetrics();
        this.rebuildSpriteRuntime();
        this.applyManagedStyle();
    }

    private boolean shouldRebuildForAttr(String key) {
        return "class".equals(key) || SPRITE_ATTRS.contains(key);
    }

    private void rebuildSpriteRuntime() {
        String resolvedSrc = this.resolveSpriteSource();
        if (resolvedSrc.isEmpty()) {
            this.clearPendingFrameMetrics();
            this.managedInlineStyle = "";
            return;
        }
        SpriteSpec.Direction direction = Sprite.parseDirection(this.getAttr("direction"));
        int initialFrame = Sprite.parseNonNegativeInt(this.getAttr("initialFrame"), 0);
        SpriteSpec.FitMode fitMode = Sprite.parseFitMode(this.getAttr("fit"));
        int steps = Sprite.parsePositiveInt(this.getAttr("steps"), -1);
        if (steps <= 0) {
            this.clearPendingFrameMetrics();
            this.managedInlineStyle = this.buildStaticManagedStyle(resolvedSrc, fitMode, direction, initialFrame, null);
            return;
        }
        FrameMetrics frameMetrics = this.resolveFrameMetrics(resolvedSrc, steps, direction);
        if (frameMetrics == null) {
            this.managedInlineStyle = this.buildStaticManagedStyle(resolvedSrc, fitMode, direction, initialFrame, null);
            return;
        }
        int clampedInitial = Math.min(initialFrame, Math.max(steps - 1, 0));
        SpriteSpec spec = new SpriteSpec(resolvedSrc, steps, direction, Sprite.parseDuration(this.getAttr("duration")), Sprite.parseLoop(this.getAttr("loop")), Sprite.parseStepsMode(this.getAttr("steps-mode")), Sprite.parseAutoplay(this.getAttr("autoplay")), frameMetrics.frameW(), frameMetrics.frameH(), clampedInitial, fitMode);
        Style baseStyle = this.buildBaseStyleWithoutManaged();
        this.managedInlineStyle = this.buildManagedStyle(spec, baseStyle);
    }

    private void applyManagedStyle() {
        String mergedStyle = Sprite.mergeStyle(this.userInlineStyle, this.managedInlineStyle);
        String currentStyle = this.getAttribute("style");
        if (Objects.equals(currentStyle, mergedStyle)) {
            return;
        }
        this.internalStyleSync = true;
        try {
            super.setAttribute("style", mergedStyle);
        }
        finally {
            this.internalStyleSync = false;
        }
    }

    private Style buildBaseStyleWithoutManaged() {
        Style base = new Style();
        base.mergeCascade(this.cssCache, this.userInlineStyle);
        return base;
    }

    private String buildManagedStyle(SpriteSpec spec, Style baseStyle) {
        LinkedHashMap<String, String> managed = new LinkedHashMap<String, String>();
        managed.put("background-image", Sprite.toCssUrl(spec.src()));
        managed.put("background-repeat", "no-repeat");
        managed.put("background-position", Sprite.frameOffset(spec.direction(), spec.initialFrame(), spec.frameW(), spec.frameH()));
        managed.put("background-size", Sprite.toBackgroundSize(spec.fit()));
        if (!spec.autoplay()) {
            return Sprite.toStyleString(managed);
        }
        String spriteName = SpriteKeyframesRegistrar.ensureRegistered(this.document, spec);
        String spriteTiming = "steps(" + spec.steps() + ", " + spec.stepsMode().cssValue() + ")";
        String spriteAnimationSegment = spriteName + " " + spec.duration() + " " + spriteTiming + " " + spec.loop();
        String externalAnimation = Sprite.valid(baseStyle.animation) ? baseStyle.animation.trim() : "";
        managed.put("animation", externalAnimation.isEmpty() ? spriteAnimationSegment : spriteAnimationSegment + ", " + externalAnimation);
        return Sprite.toStyleString(managed);
    }

    private String buildStaticManagedStyle(String resolvedSrc, SpriteSpec.FitMode fitMode, SpriteSpec.Direction direction, int initialFrame, FrameMetrics frameMetrics) {
        LinkedHashMap<String, String> managed = new LinkedHashMap<String, String>();
        managed.put("background-image", Sprite.toCssUrl(resolvedSrc));
        managed.put("background-repeat", "no-repeat");
        managed.put("background-size", Sprite.toBackgroundSize(fitMode));
        if (frameMetrics != null && initialFrame > 0) {
            managed.put("background-position", Sprite.frameOffset(direction, initialFrame, frameMetrics.frameW(), frameMetrics.frameH()));
        } else {
            managed.put("background-position", "0px 0px");
        }
        return Sprite.toStyleString(managed);
    }

    private FrameMetrics resolveFrameMetrics(String resolvedSrc, int steps, SpriteSpec.Direction direction) {
        int frameW;
        ImageHandle handle = ImageAsyncHandler.INSTANCE.request(resolvedSrc, this, false);
        if (!Sprite.isHandleReady(handle)) {
            this.markPendingFrameMetrics(resolvedSrc);
            return null;
        }
        this.clearPendingFrameMetrics();
        int textureW = handle.texture().getWidth();
        int textureH = handle.texture().getHeight();
        if (textureW <= 0 || textureH <= 0) {
            this.warnInvalidFrameMetrics("\u56fe\u7247\u5c3a\u5bf8\u975e\u6cd5", resolvedSrc, steps, direction, textureW, textureH);
            return null;
        }
        int frameH = switch (direction) {
            case SpriteSpec.Direction.DOWN, SpriteSpec.Direction.UP -> {
                frameW = textureW;
                yield textureH / steps;
            }
            default -> {
                frameW = textureW / steps;
                yield textureH;
            }
        };
        if (frameW <= 0 || frameH <= 0) {
            this.warnInvalidFrameMetrics("\u63a8\u5bfc\u540e\u7684\u5e27\u5c3a\u5bf8\u975e\u6cd5", resolvedSrc, steps, direction, textureW, textureH);
            return null;
        }
        return new FrameMetrics(frameW, frameH);
    }

    private static boolean isHandleReady(ImageHandle handle) {
        return handle != null && handle.state() == AbstractAsyncHandler.AsyncState.READY && handle.texture() != null;
    }

    private void markPendingFrameMetrics(String resolvedSrc) {
        this.frameMetricsPending = true;
        this.pendingFrameMetricsSrc = resolvedSrc;
    }

    private void clearPendingFrameMetrics() {
        this.frameMetricsPending = false;
        this.pendingFrameMetricsSrc = "";
    }

    private void warnInvalidFrameMetrics(String reason, String resolvedSrc, int steps, SpriteSpec.Direction direction, int textureW, int textureH) {
        String key = resolvedSrc + "|" + steps + "|" + direction + "|" + textureW + "x" + textureH + "|" + reason;
        if (!this.invalidMetricsWarnings.add(key)) {
            return;
        }
        ApricityUI.LOGGER.warn("Sprite \u5e27\u5c3a\u5bf8\u63a8\u5bfc\u5931\u8d25\uff1a{}\uff0csrc={}\uff0csteps={}\uff0cdirection={}\uff0ctexture={}x{}", new Object[]{reason, resolvedSrc, steps, direction, textureW, textureH});
    }

    private String resolveSpriteSource() {
        String src = this.getAttr("src");
        if (src.isBlank()) {
            return "";
        }
        return Loader.resolve(this.document.getPath(), src);
    }

    private String getAttr(String name) {
        HashMap<String, String> attrs = this.getAttributes();
        String direct = attrs.getOrDefault(name, "");
        if (!direct.isBlank()) {
            return direct.trim();
        }
        for (Map.Entry entry : attrs.entrySet()) {
            if (!Sprite.normalizeAttr((String)entry.getKey()).equals(Sprite.normalizeAttr(name))) continue;
            return entry.getValue() == null ? "" : ((String)entry.getValue()).trim();
        }
        return "";
    }

    private static boolean valid(String value) {
        return value != null && !value.isBlank() && !"unset".equals(value);
    }

    private static String parseDuration(String raw) {
        if (!Sprite.valid(raw)) {
            return DEFAULT_DURATION;
        }
        String duration = raw.trim().toLowerCase(Locale.ROOT);
        if (TIME_PATTERN.matcher(duration).matches()) {
            return duration;
        }
        return DEFAULT_DURATION;
    }

    private static String parseLoop(String raw) {
        if (!Sprite.valid(raw)) {
            return "infinite";
        }
        String value = raw.trim().toLowerCase(Locale.ROOT);
        if ("infinite".equals(value)) {
            return "infinite";
        }
        int count = Sprite.parsePositiveInt(value, -1);
        return count > 0 ? String.valueOf(count) : "infinite";
    }

    private static SpriteSpec.Direction parseDirection(String raw) {
        if (!Sprite.valid(raw)) {
            return SpriteSpec.Direction.RIGHT;
        }
        return switch (raw.trim().toLowerCase(Locale.ROOT)) {
            case "left" -> SpriteSpec.Direction.LEFT;
            case "up" -> SpriteSpec.Direction.UP;
            case "down" -> SpriteSpec.Direction.DOWN;
            default -> SpriteSpec.Direction.RIGHT;
        };
    }

    private static SpriteSpec.StepsMode parseStepsMode(String raw) {
        if (!Sprite.valid(raw)) {
            return SpriteSpec.StepsMode.END;
        }
        return "start".equalsIgnoreCase(raw.trim()) ? SpriteSpec.StepsMode.START : SpriteSpec.StepsMode.END;
    }

    private static boolean parseAutoplay(String raw) {
        if (!Sprite.valid(raw)) {
            return true;
        }
        String value = raw.trim().toLowerCase(Locale.ROOT);
        return !"false".equals(value) && !"0".equals(value) && !"no".equals(value) && !"off".equals(value);
    }

    private static SpriteSpec.FitMode parseFitMode(String raw) {
        if (!Sprite.valid(raw)) {
            return SpriteSpec.FitMode.NONE;
        }
        return switch (raw.trim().toLowerCase(Locale.ROOT)) {
            case "contain" -> SpriteSpec.FitMode.CONTAIN;
            case "cover" -> SpriteSpec.FitMode.COVER;
            case "stretch" -> SpriteSpec.FitMode.STRETCH;
            default -> SpriteSpec.FitMode.NONE;
        };
    }

    private static int parsePositiveInt(String raw, int fallback) {
        try {
            int parsed = Integer.parseInt(raw.trim());
            return parsed > 0 ? parsed : fallback;
        }
        catch (Exception ignored) {
            return fallback;
        }
    }

    private static int parseNonNegativeInt(String raw, int fallback) {
        try {
            int parsed = Integer.parseInt(raw.trim());
            return Math.max(parsed, 0);
        }
        catch (Exception ignored) {
            return fallback;
        }
    }

    private static String frameOffset(SpriteSpec.Direction direction, int frameIndex, int frameW, int frameH) {
        int x = 0;
        int y = 0;
        int safeIndex = Math.max(frameIndex, 0);
        switch (direction) {
            case RIGHT: {
                x = -safeIndex * frameW;
                break;
            }
            case LEFT: {
                x = safeIndex * frameW;
                break;
            }
            case DOWN: {
                y = -safeIndex * frameH;
                break;
            }
            case UP: {
                y = safeIndex * frameH;
            }
        }
        return x + "px " + y + "px";
    }

    private static String toBackgroundSize(SpriteSpec.FitMode fit) {
        return switch (fit) {
            default -> throw new IncompatibleClassChangeError();
            case SpriteSpec.FitMode.CONTAIN -> "contain";
            case SpriteSpec.FitMode.COVER -> "cover";
            case SpriteSpec.FitMode.STRETCH -> "100% 100%";
            case SpriteSpec.FitMode.NONE -> "auto";
        };
    }

    private static String toCssUrl(String resolvedSrc) {
        if (Loader.isRemotePath(resolvedSrc)) {
            return "url(\"" + resolvedSrc + "\")";
        }
        return "url(\"/" + resolvedSrc + "\")";
    }

    private static String sanitizeUserStyle(String rawStyle) {
        LinkedHashMap<String, String> declarations = Sprite.parseStyle(rawStyle);
        for (String key : MANAGED_STYLE_KEYS) {
            declarations.remove(key);
        }
        return Sprite.toStyleString(declarations);
    }

    private static String mergeStyle(String userStyle, String managedStyle) {
        LinkedHashMap<String, String> result = Sprite.parseStyle(userStyle);
        result.putAll(Sprite.parseStyle(managedStyle));
        return Sprite.toStyleString(result);
    }

    private static LinkedHashMap<String, String> parseStyle(String rawStyle) {
        String[] entries;
        LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
        if (rawStyle == null || rawStyle.isBlank()) {
            return result;
        }
        for (String entry : entries = rawStyle.split(";")) {
            int colonIndex;
            String part = entry.trim();
            if (part.isEmpty() || (colonIndex = part.indexOf(58)) <= 0 || colonIndex >= part.length() - 1) continue;
            String key = part.substring(0, colonIndex).trim().toLowerCase(Locale.ROOT);
            String value = part.substring(colonIndex + 1).trim();
            if (key.isEmpty() || value.isEmpty()) continue;
            result.put(key, value);
        }
        return result;
    }

    private static String toStyleString(Map<String, String> declarations) {
        if (declarations == null || declarations.isEmpty()) {
            return "";
        }
        StringBuilder style = new StringBuilder();
        for (Map.Entry<String, String> entry : declarations.entrySet()) {
            style.append(entry.getKey()).append(":").append(entry.getValue()).append(";");
        }
        return style.toString();
    }

    private static String normalizeAttr(String name) {
        if (name == null) {
            return "";
        }
        return name.replace("-", "").toLowerCase(Locale.ROOT);
    }

    static {
        Element.register(TAG_NAME, (document, string) -> new Sprite((Document)document));
    }

    private record SpriteSpec(String src, int steps, Direction direction, String duration, String loop, StepsMode stepsMode, boolean autoplay, int frameW, int frameH, int initialFrame, FitMode fit) {

        private static enum Direction {
            RIGHT,
            LEFT,
            UP,
            DOWN;

        }

        private static enum StepsMode {
            START,
            END;


            String cssValue() {
                return this == START ? "start" : "end";
            }
        }

        private static enum FitMode {
            NONE,
            CONTAIN,
            COVER,
            STRETCH;

        }
    }

    private record FrameMetrics(int frameW, int frameH) {
    }

    private static final class SpriteKeyframesRegistrar {
        private SpriteKeyframesRegistrar() {
        }

        private static String ensureRegistered(Document document, SpriteSpec spec) {
            SpriteKey key = new SpriteKey(spec.src(), spec.steps(), spec.direction(), spec.frameW(), spec.frameH());
            SpriteKeyframesCache cache = SpriteKeyframesCache.of(document);
            String name = cache.resolveName(key);
            if (cache.markInjectedIfNeeded(key)) {
                SpriteKeyframesRegistrar.register(name, spec);
            }
            return name;
        }

        private static void register(String animationName, SpriteSpec spec) {
            int dx = 0;
            int dy = 0;
            int travelX = spec.steps() * spec.frameW();
            int travelY = spec.steps() * spec.frameH();
            switch (spec.direction()) {
                case RIGHT: {
                    dx = -travelX;
                    break;
                }
                case LEFT: {
                    dx = travelX;
                    break;
                }
                case DOWN: {
                    dy = -travelY;
                    break;
                }
                case UP: {
                    dy = travelY;
                }
            }
            HashMap<String, String> start = new HashMap<String, String>();
            start.put("background-position", "0px 0px");
            HashMap<String, String> end = new HashMap<String, String>();
            end.put("background-position", dx + "px " + dy + "px");
            Animation.registerKeyframe(animationName, 0.0, start);
            Animation.registerKeyframe(animationName, 100.0, end);
        }
    }

    private static class SpriteKeyframesCache {
        private static final Map<Document, SpriteKeyframesCache> DOCUMENT_CACHES = Collections.synchronizedMap(new WeakHashMap());
        private final Map<SpriteKey, String> keyToName = new HashMap<SpriteKey, String>();
        private final Map<SpriteKey, Boolean> injected = new HashMap<SpriteKey, Boolean>();

        private SpriteKeyframesCache() {
        }

        static SpriteKeyframesCache of(Document document) {
            return DOCUMENT_CACHES.computeIfAbsent(document, ignored -> new SpriteKeyframesCache());
        }

        synchronized String resolveName(SpriteKey key) {
            return this.keyToName.computeIfAbsent(key, SpriteKeyframesCache::buildName);
        }

        synchronized boolean markInjectedIfNeeded(SpriteKey key) {
            if (Boolean.TRUE.equals(this.injected.get(key))) {
                return false;
            }
            this.injected.put(key, true);
            return true;
        }

        private static String buildName(SpriteKey key) {
            String raw = key.src() + "|" + key.steps() + "|" + key.direction() + "|" + key.frameW() + "|" + key.frameH();
            return "aui_sprite_" + Integer.toUnsignedString(raw.hashCode(), 16);
        }
    }

    private record SpriteKey(String src, int steps, SpriteSpec.Direction direction, int frameW, int frameH) {
    }
}

