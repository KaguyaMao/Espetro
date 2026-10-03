/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.style;

import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.loader.Loader;
import com.sighs.apricityui.parser.CssString;
import com.sighs.apricityui.parser.Gradient;
import com.sighs.apricityui.style.Style;
import com.sighs.apricityui.util.MathUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Background {
    public String repeat = "no-repeat";
    public String size = "auto";
    public String position = "0 0";
    public String imagePath = "unset";
    public String color = "unset";
    public Gradient gradient = null;
    private final List<Layer> layers = new ArrayList<Layer>();

    public static Background of(Element element) {
        Background cache = element.getRenderer().background.get();
        if (cache != null) {
            return cache;
        }
        Style style = element.getComputedStyle();
        Background bg = new Background();
        bg.color = style.backgroundColor;
        bg.buildLayers(element.document.getPath(), style.backgroundImage, style.backgroundRepeat, style.backgroundSize, style.backgroundPosition);
        element.getRenderer().background.set(bg);
        return bg;
    }

    private void buildLayers(String contextPath, String image, String repeat, String size, String position) {
        this.repeat = Background.normalizeLayerValue(repeat, "no-repeat");
        this.size = Background.normalizeLayerValue(size, "auto");
        this.position = Background.normalizeLayerValue(position, "0 0");
        this.imagePath = "unset";
        this.gradient = null;
        this.layers.clear();
        List<String> images = Background.splitTopLevelComma(image);
        if (images.isEmpty()) {
            return;
        }
        List<String> repeats = Background.splitTopLevelComma(repeat);
        List<String> sizes = Background.splitTopLevelComma(size);
        List<String> positions = Background.splitTopLevelComma(position);
        for (int i = 0; i < images.size(); ++i) {
            Layer layer = Background.parseImageLayer(contextPath, images.get(i));
            layer.repeat = Background.normalizeLayerValue(Background.pickLayerToken(repeats, i, "no-repeat"), "no-repeat");
            layer.size = Background.normalizeLayerValue(Background.pickLayerToken(sizes, i, "auto"), "auto");
            layer.position = Background.normalizeLayerValue(Background.pickLayerToken(positions, i, "0 0"), "0 0");
            if (layer.intrinsicRepeat) {
                if (layer.intrinsicRepeatValue != null) {
                    layer.repeat = layer.intrinsicRepeatValue;
                }
                if (layer.intrinsicSizeValue != null) {
                    layer.size = layer.intrinsicSizeValue;
                }
            }
            this.layers.add(layer);
            if (this.gradient == null && layer.gradient != null) {
                this.gradient = layer.gradient;
            }
            if (!"unset".equals(this.imagePath) || "unset".equals(layer.imagePath)) continue;
            this.imagePath = layer.imagePath;
        }
    }

    public static List<String> resolveImagePaths(String contextPath, String imageValue) {
        ArrayList<String> result = new ArrayList<String>();
        for (String token : Background.splitTopLevelComma(imageValue)) {
            Layer layer = Background.parseImageLayer(contextPath, token);
            if (layer.imagePath == null || "unset".equals(layer.imagePath) || layer.imagePath.isBlank()) continue;
            result.add(layer.imagePath);
        }
        return result;
    }

    private static Layer parseImageLayer(String contextPath, String token) {
        String path;
        int start;
        int end;
        Layer layer = new Layer();
        if (token == null) {
            return layer;
        }
        String image = token.trim();
        if (image.isEmpty() || "unset".equalsIgnoreCase(image) || "none".equalsIgnoreCase(image)) {
            return layer;
        }
        String lowered = image.toLowerCase(Locale.ROOT);
        if (lowered.startsWith("linear-gradient") || lowered.startsWith("repeating-linear-gradient")) {
            layer.gradient = Gradient.parse(image);
            if (layer.gradient != null && layer.gradient.repeating()) {
                Background.applyRepeatingGradientTile(layer);
            }
            return layer;
        }
        int urlStart = lowered.indexOf("url(");
        if (urlStart >= 0 && (end = image.indexOf(41, start = urlStart + 4)) > start && !(path = image.substring(start, end).replace("\"", "").replace("'", "").trim()).isEmpty() && contextPath != null) {
            layer.imagePath = Loader.resolve(contextPath, path);
        }
        return layer;
    }

    private static void applyRepeatingGradientTile(Layer layer) {
        float repeatLength = layer.gradient.repeatLengthPx();
        if (repeatLength <= 0.0f) {
            return;
        }
        float angle = MathUtil.normalizeAngle(layer.gradient.angle());
        if (Math.abs(angle - 90.0f) < 0.01f || Math.abs(angle - 270.0f) < 0.01f) {
            layer.intrinsicRepeat = true;
            layer.intrinsicRepeatValue = "repeat-x";
            layer.intrinsicSizeValue = Background.trimFloat(repeatLength) + "px 100%";
        } else if (Math.abs(angle) < 0.01f || Math.abs(angle - 180.0f) < 0.01f) {
            layer.intrinsicRepeat = true;
            layer.intrinsicRepeatValue = "repeat-y";
            layer.intrinsicSizeValue = "100% " + Background.trimFloat(repeatLength) + "px";
        }
    }

    private static String trimFloat(float value) {
        if (Math.abs(value - (float)Math.round(value)) < 0.001f) {
            return Integer.toString(Math.round(value));
        }
        return Float.toString(value);
    }

    private static String pickLayerToken(List<String> values, int index, String fallback) {
        if (values == null || values.isEmpty()) {
            return fallback;
        }
        if (index < values.size()) {
            return values.get(index);
        }
        return values.get(values.size() - 1);
    }

    private static String normalizeLayerValue(String raw, String fallback) {
        if (raw == null) {
            return fallback;
        }
        String value = raw.trim();
        if (value.isEmpty() || "unset".equalsIgnoreCase(value)) {
            return fallback;
        }
        return value;
    }

    public static List<String> splitTopLevelComma(String value) {
        ArrayList<String> parts = new ArrayList<String>();
        if (value == null || value.isBlank()) {
            return parts;
        }
        if ("unset".equalsIgnoreCase(value.trim())) {
            return parts;
        }
        for (String raw : CssString.splitTopLevel(value, ',')) {
            String token = raw.trim();
            if (token.isEmpty()) continue;
            parts.add(token);
        }
        return parts;
    }

    public List<Layer> getLayers() {
        return this.layers;
    }

    public static class Layer {
        public String repeat = "no-repeat";
        public String size = "auto";
        public String position = "0 0";
        public String imagePath = "unset";
        public Gradient gradient = null;
        public boolean intrinsicRepeat = false;
        public String intrinsicRepeatValue = null;
        public String intrinsicSizeValue = null;

        public boolean hasDrawableContent() {
            return this.gradient != null || this.imagePath != null && !this.imagePath.isBlank() && !"unset".equals(this.imagePath);
        }
    }
}

