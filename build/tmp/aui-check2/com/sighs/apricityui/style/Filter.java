/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.style;

import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.parser.Color;
import com.sighs.apricityui.parser.CssString;
import com.sighs.apricityui.style.Style;
import com.sighs.apricityui.style.Transition;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Filter {
    private static final Pattern BLUR = Pattern.compile("blur\\(([^)]+)\\)");
    private static final Pattern BRIGHTNESS = Pattern.compile("brightness\\(([^)]+)\\)");
    private static final Pattern GRAYSCALE = Pattern.compile("grayscale\\(([^)]+)\\)");
    private static final Pattern INVERT = Pattern.compile("invert\\(([^)]+)\\)");
    private static final Pattern HUE = Pattern.compile("hue-rotate\\(([^)]+)\\)");
    private static final Pattern OPACITY = Pattern.compile("opacity\\(([^)]+)\\)");
    private static final Pattern DROP_SHADOW_FN = Pattern.compile("drop-shadow\\s*\\(", 2);

    public static FilterState getFilterOf(Element element) {
        FilterState cache = element.getRenderer().filter.get();
        if (cache != null) {
            return cache;
        }
        FilterState state = Filter.isDisabled(element) ? FilterState.EMPTY : Filter.parse(element.getComputedStyle().filter, Filter.getOpacity(element));
        element.getRenderer().filter.set(state);
        return state;
    }

    public static FilterState getBackdropFilterOf(Element element) {
        FilterState cache = element.getRenderer().backdropFilter.get();
        if (cache != null) {
            return cache;
        }
        Style style = element.getComputedStyle();
        FilterState state = Filter.isDisabled(style.backdropFilter) ? FilterState.EMPTY : Filter.parse(element.getComputedStyle().backdropFilter, 1.0f);
        element.getRenderer().backdropFilter.set(state);
        return state;
    }

    public static FilterState parse(String filterStr, float opacityStyle) {
        float blur = Filter.extractVal(BLUR, filterStr, 0.0f, "px");
        float bright = Filter.extractVal(BRIGHTNESS, filterStr, 1.0f, "%");
        float gray = Filter.extractVal(GRAYSCALE, filterStr, 0.0f, "%");
        float inv = Filter.extractVal(INVERT, filterStr, 0.0f, "%");
        float hue = Filter.extractVal(HUE, filterStr, 0.0f, "deg");
        float filterOpacity = Filter.extractVal(OPACITY, filterStr, 1.0f, "%");
        DropShadow shadow = Filter.parseDropShadow(filterStr);
        return new FilterState(blur, bright, gray, inv, hue, filterOpacity * opacityStyle, shadow.x, shadow.y, shadow.blur, shadow.color);
    }

    private static float extractVal(Pattern p, String source, float def, String unit) {
        if (source == null || source.isBlank()) {
            return def;
        }
        Matcher m = p.matcher(source);
        if (m.find()) {
            String val = m.group(1).trim();
            if (val.endsWith("%") && !unit.equals("deg")) {
                return Float.parseFloat(val.replace("%", "")) / 100.0f;
            }
            if (val.endsWith(unit)) {
                return Float.parseFloat(val.replace(unit, ""));
            }
            if (unit.equals("px") && val.matches("[+-]?[0-9.]+")) {
                return Float.parseFloat(val);
            }
            if (val.matches("[+-]?[0-9.]+")) {
                return Float.parseFloat(val);
            }
        }
        return def;
    }

    public static float getOpacity(String str) {
        if (str == null || str.isBlank()) {
            return 1.0f;
        }
        try {
            return Float.parseFloat(str);
        }
        catch (Exception ignored) {
            return 1.0f;
        }
    }

    public static float getOpacity(Element element) {
        return Filter.getOpacity(element.getComputedStyle().opacity);
    }

    public static boolean isDisabled(Element element) {
        Style style = element.getComputedStyle();
        return Filter.isDisabled(style.filter, style.opacity);
    }

    public static boolean isDisabled(String filterStr, String opacityStr) {
        float opacity = Filter.getOpacity(opacityStr);
        return (filterStr == null || filterStr.equals("none") || filterStr.equals("unset") || filterStr.isEmpty()) && opacity == 1.0f;
    }

    public static boolean isDisabled(String filterStr) {
        return filterStr == null || filterStr.equals("none") || filterStr.equals("unset") || filterStr.isEmpty();
    }

    public static void createTransition(Style startStyle, Style endStyle, List<Transition> result, double duration, double delay) {
        long time = System.currentTimeMillis();
        FilterState start = Filter.parse(startStyle.filter, 1.0f);
        FilterState end = Filter.parse(endStyle.filter, 1.0f);
        if (start.blurRadius() != end.blurRadius()) {
            result.add(new Transition("filter-blur", start.blurRadius(), end.blurRadius(), duration, delay, time));
        }
        if (start.brightness() != end.brightness()) {
            result.add(new Transition("filter-brightness", start.brightness(), end.brightness(), duration, delay, time));
        }
        if (start.grayscale() != end.grayscale()) {
            result.add(new Transition("filter-grayscale", start.grayscale(), end.grayscale(), duration, delay, time));
        }
        if (start.invert() != end.invert()) {
            result.add(new Transition("filter-invert", start.invert(), end.invert(), duration, delay, time));
        }
        if (start.hueRotate() != end.hueRotate()) {
            result.add(new Transition("filter-hue-rotate", start.hueRotate(), end.hueRotate(), duration, delay, time));
        }
        if (start.opacity() != end.opacity()) {
            result.add(new Transition("filter-opacity", start.opacity(), end.opacity(), duration, delay, time));
        }
        if (start.dropShadowX() != end.dropShadowX()) {
            result.add(new Transition("filter-drop-shadow-x", start.dropShadowX(), end.dropShadowX(), duration, delay, time));
        }
        if (start.dropShadowY() != end.dropShadowY()) {
            result.add(new Transition("filter-drop-shadow-y", start.dropShadowY(), end.dropShadowY(), duration, delay, time));
        }
        if (start.dropShadowBlur() != end.dropShadowBlur()) {
            result.add(new Transition("filter-drop-shadow-blur", start.dropShadowBlur(), end.dropShadowBlur(), duration, delay, time));
        }
        if (start.dropShadowColor() != end.dropShadowColor()) {
            result.add(new Transition("filter-drop-shadow-color", start.dropShadowColor(), end.dropShadowColor(), duration, delay, time));
        }
    }

    public static void readTransition(List<Transition.Change> changeList, Style originStyle) {
        boolean hasFilterChange = false;
        for (Transition.Change change : changeList) {
            if (!change.name().startsWith("filter-")) continue;
            hasFilterChange = true;
            break;
        }
        if (!hasFilterChange) {
            return;
        }
        FilterState base = Filter.parse(originStyle.filter, 1.0f);
        float blur = base.blurRadius();
        float brightness = base.brightness();
        float grayscale = base.grayscale();
        float invert = base.invert();
        float hueRotate = base.hueRotate();
        float opacity = base.opacity();
        float shadowX = base.dropShadowX();
        float shadowY = base.dropShadowY();
        float shadowBlur = base.dropShadowBlur();
        int shadowColor = base.dropShadowColor();
        Iterator<Transition.Change> iterator = changeList.iterator();
        while (iterator.hasNext()) {
            Transition.Change change = iterator.next();
            String name = change.name();
            double val = change.value();
            if (!name.startsWith("filter-")) continue;
            switch (name) {
                case "filter-blur": {
                    blur = (float)val;
                    break;
                }
                case "filter-brightness": {
                    brightness = (float)val;
                    break;
                }
                case "filter-grayscale": {
                    grayscale = (float)val;
                    break;
                }
                case "filter-invert": {
                    invert = (float)val;
                    break;
                }
                case "filter-hue-rotate": {
                    hueRotate = (float)val;
                    break;
                }
                case "filter-opacity": {
                    opacity = (float)val;
                    break;
                }
                case "filter-drop-shadow-x": {
                    shadowX = (float)val;
                    break;
                }
                case "filter-drop-shadow-y": {
                    shadowY = (float)val;
                    break;
                }
                case "filter-drop-shadow-blur": {
                    shadowBlur = (float)val;
                    break;
                }
                case "filter-drop-shadow-color": {
                    shadowColor = (int)Math.round(val);
                }
            }
            iterator.remove();
        }
        FilterState merged = new FilterState(blur, brightness, grayscale, invert, hueRotate, opacity, shadowX, shadowY, shadowBlur, shadowColor);
        originStyle.filter = Filter.serialize(merged);
    }

    public static void interpolateFilter(List<Transition.Change> changes, String start, String end, double progress) {
        FilterState s = Filter.parse(start, 1.0f);
        FilterState e = Filter.parse(end, 1.0f);
        Transition.addChange(changes, "filter-blur", Transition.getOffset("blur", s.blurRadius(), e.blurRadius(), progress));
        Transition.addChange(changes, "filter-brightness", Transition.getOffset("bright", s.brightness(), e.brightness(), progress));
        Transition.addChange(changes, "filter-grayscale", Transition.getOffset("gray", s.grayscale(), e.grayscale(), progress));
        Transition.addChange(changes, "filter-invert", Transition.getOffset("inv", s.invert(), e.invert(), progress));
        Transition.addChange(changes, "filter-hue-rotate", Transition.getOffset("hue", s.hueRotate(), e.hueRotate(), progress));
        Transition.addChange(changes, "filter-opacity", Transition.getOffset("op", s.opacity(), e.opacity(), progress));
        Transition.addChange(changes, "filter-drop-shadow-x", Transition.getOffset("drop-shadow-x", s.dropShadowX(), e.dropShadowX(), progress));
        Transition.addChange(changes, "filter-drop-shadow-y", Transition.getOffset("drop-shadow-y", s.dropShadowY(), e.dropShadowY(), progress));
        Transition.addChange(changes, "filter-drop-shadow-blur", Transition.getOffset("drop-shadow-blur", s.dropShadowBlur(), e.dropShadowBlur(), progress));
        Transition.addChange(changes, "filter-drop-shadow-color", Transition.getOffset("drop-shadow-color", s.dropShadowColor(), e.dropShadowColor(), progress));
    }

    private static String serialize(FilterState state) {
        ArrayList<String> parts = new ArrayList<String>();
        if (state.blurRadius() > 1.0E-4f) {
            parts.add(String.format(Locale.ROOT, "blur(%.2fpx)", Float.valueOf(state.blurRadius())));
        }
        if (Math.abs(state.brightness() - 1.0f) > 1.0E-4f) {
            parts.add(String.format(Locale.ROOT, "brightness(%.3f)", Float.valueOf(state.brightness())));
        }
        if (Math.abs(state.grayscale()) > 1.0E-4f) {
            parts.add(String.format(Locale.ROOT, "grayscale(%.3f)", Float.valueOf(state.grayscale())));
        }
        if (Math.abs(state.invert()) > 1.0E-4f) {
            parts.add(String.format(Locale.ROOT, "invert(%.3f)", Float.valueOf(state.invert())));
        }
        if (Math.abs(state.hueRotate()) > 1.0E-4f) {
            parts.add(String.format(Locale.ROOT, "hue-rotate(%.2fdeg)", Float.valueOf(state.hueRotate())));
        }
        if (Math.abs(state.opacity() - 1.0f) > 1.0E-4f) {
            parts.add(String.format(Locale.ROOT, "opacity(%.3f)", Float.valueOf(state.opacity())));
        }
        if (state.hasDropShadow()) {
            parts.add(String.format(Locale.ROOT, "drop-shadow(%.2fpx %.2fpx %.2fpx %s)", Float.valueOf(state.dropShadowX()), Float.valueOf(state.dropShadowY()), Float.valueOf(state.dropShadowBlur()), new Color(state.dropShadowColor()).toRgbaString()));
        }
        return parts.isEmpty() ? "none" : String.join((CharSequence)" ", parts);
    }

    private static DropShadow parseDropShadow(String filterStr) {
        if (filterStr == null || filterStr.isBlank()) {
            return DropShadow.NONE;
        }
        Matcher fn = DROP_SHADOW_FN.matcher(filterStr);
        if (!fn.find()) {
            return DropShadow.NONE;
        }
        int open = filterStr.indexOf(40, fn.start());
        if (open < 0) {
            return DropShadow.NONE;
        }
        int close = Filter.findMatchingParen(filterStr, open);
        if (close <= open) {
            return DropShadow.NONE;
        }
        String rawArgs = filterStr.substring(open + 1, close).trim();
        if (rawArgs.isEmpty()) {
            return DropShadow.NONE;
        }
        List<String> tokens = Filter.splitBySpaceOutsideParens(rawArgs);
        if (tokens.isEmpty()) {
            return DropShadow.NONE;
        }
        float[] lengths = new float[3];
        int lengthCount = 0;
        String colorToken = null;
        for (String token : tokens) {
            Float parsed;
            if (token == null || token.isBlank()) continue;
            if (CssString.isColorToken(token)) {
                colorToken = token.trim();
                continue;
            }
            if (lengthCount >= 3 || (parsed = Filter.parseLength(token)) == null) continue;
            lengths[lengthCount++] = parsed.floatValue();
        }
        if (lengthCount < 2) {
            return DropShadow.NONE;
        }
        int color = new Color(colorToken == null ? "#000000" : colorToken).getValue();
        return new DropShadow(lengths[0], lengths[1], lengthCount >= 3 ? Math.max(0.0f, lengths[2]) : 0.0f, color);
    }

    private static int findMatchingParen(String text, int openIndex) {
        int depth = 0;
        for (int i = openIndex; i < text.length(); ++i) {
            char c = text.charAt(i);
            if (c == '(') {
                ++depth;
                continue;
            }
            if (c != ')' || --depth != 0) continue;
            return i;
        }
        return -1;
    }

    private static List<String> splitBySpaceOutsideParens(String input) {
        ArrayList<String> result = new ArrayList<String>();
        StringBuilder current = new StringBuilder();
        int depth = 0;
        for (int i = 0; i < input.length(); ++i) {
            char c = input.charAt(i);
            if (c == '(') {
                ++depth;
            } else if (c == ')') {
                depth = Math.max(0, depth - 1);
            }
            if (Character.isWhitespace(c) && depth == 0) {
                if (current.isEmpty()) continue;
                result.add(current.toString());
                current.setLength(0);
                continue;
            }
            current.append(c);
        }
        if (!current.isEmpty()) {
            result.add(current.toString());
        }
        return result;
    }

    private static Float parseLength(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }
        String t = token.trim().toLowerCase(Locale.ROOT);
        try {
            if (t.endsWith("px")) {
                t = t.substring(0, t.length() - 2).trim();
            }
            return Float.valueOf(Float.parseFloat(t));
        }
        catch (NumberFormatException ignored) {
            return null;
        }
    }

    public record FilterState(float blurRadius, float brightness, float grayscale, float invert, float hueRotate, float opacity, float dropShadowX, float dropShadowY, float dropShadowBlur, int dropShadowColor) {
        public static final FilterState EMPTY = new FilterState(0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0);

        public boolean isEmpty() {
            return this.blurRadius == 0.0f && this.brightness == 1.0f && this.grayscale == 0.0f && this.invert == 0.0f && this.hueRotate == 0.0f && this.opacity == 1.0f && !this.hasDropShadow();
        }

        public boolean hasDropShadow() {
            return (this.dropShadowColor >>> 24 & 0xFF) > 0;
        }
    }

    private record DropShadow(float x, float y, float blur, int color) {
        private static final DropShadow NONE = new DropShadow(0.0f, 0.0f, 0.0f, 0);
    }
}

