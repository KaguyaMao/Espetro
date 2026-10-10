/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.style;

import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.layout.Box;
import com.sighs.apricityui.layout.Size;
import com.sighs.apricityui.style.Filter;
import com.sighs.apricityui.style.Style;
import com.sighs.apricityui.style.Transform;
import com.sighs.apricityui.style.Transition;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Animation {
    private static final Map<String, TreeMap<Double, Map<String, String>>> KEYFRAMES = new HashMap<String, TreeMap<Double, Map<String, String>>>();
    private static final Map<String, Set<String>> KEYFRAME_PROPS = new HashMap<String, Set<String>>();
    private static final Map<String, List<AnimationConfig>> PARSED_CONFIGS = new LinkedHashMap<String, List<AnimationConfig>>(32, 0.75f, true){

        @Override
        protected boolean removeEldestEntry(Map.Entry<String, List<AnimationConfig>> eldest) {
            return this.size() > 256;
        }
    };
    private static final Map<UUID, AnimationState> ACTIVE_ANIMATIONS = new HashMap<UUID, AnimationState>();
    private static final Map<String, TimingFunction> TIMING_FUNCTIONS = new ConcurrentHashMap<String, TimingFunction>();
    private static final Pattern STEPS_PATTERN = Pattern.compile("^steps\\(\\s*([1-9][0-9]*)\\s*(?:,\\s*(start|end|jump-start|jump-end|jump-none|jump-both)\\s*)?\\)\\s*$");
    private static final Pattern TIME_PATTERN = Pattern.compile("^[+-]?(?:(?:[0-9]+(?:\\.[0-9]*)?)|(?:\\.[0-9]+))(?:ms|s)$");
    private static final Pattern CUBIC_BEZIER_PATTERN = Pattern.compile("^cubic-bezier\\(\\s*([-+]?(?:\\d*\\.\\d+|\\d+))\\s*,\\s*([-+]?(?:\\d*\\.\\d+|\\d+))\\s*,\\s*([-+]?(?:\\d*\\.\\d+|\\d+))\\s*,\\s*([-+]?(?:\\d*\\.\\d+|\\d+))\\s*\\)\\s*$");
    private static final Set<String> DIRECTION_SET = Set.of("normal", "reverse", "alternate", "alternate-reverse");
    private static final Set<String> FILL_SET = Set.of("none", "forwards", "backwards", "both");
    private static final Set<String> TIMING_SET = Set.of("linear", "ease", "ease-in", "ease-out", "ease-in-out", "step-start", "step-end");
    private static final Set<String> PLAY_STATE_SET = Set.of("running", "paused");
    private static final TimingFunction IDENTITY_TIMING = progress -> progress;
    private static final TimingFunction STEP_START_TIMING = progress -> 1.0;
    private static final TimingFunction STEP_END_TIMING = progress -> progress >= 1.0 ? 1.0 : 0.0;
    private static final TimingFunction EASE_TIMING = new CubicBezierTiming(0.25, 0.1, 0.25, 1.0);
    private static final TimingFunction EASE_IN_TIMING = new CubicBezierTiming(0.42, 0.0, 1.0, 1.0);
    private static final TimingFunction EASE_OUT_TIMING = new CubicBezierTiming(0.0, 0.0, 0.58, 1.0);
    private static final TimingFunction EASE_IN_OUT_TIMING = new CubicBezierTiming(0.42, 0.0, 0.58, 1.0);

    public static void registerKeyframe(String name, double percent, Map<String, String> props) {
        KEYFRAMES.computeIfAbsent(name, k -> new TreeMap()).put(percent, props);
        KEYFRAME_PROPS.computeIfAbsent(name, k -> new HashSet()).addAll(props.keySet());
    }

    public static boolean isActive(Element e) {
        return ACTIVE_ANIMATIONS.containsKey(e.uuid);
    }

    public static void stop(Element e) {
        if (e != null) {
            ACTIVE_ANIMATIONS.remove(e.uuid);
        }
    }

    public static boolean hasAnimationSpec(Style style) {
        if (style == null) {
            return false;
        }
        String spec = style.animation;
        if (spec == null) {
            return false;
        }
        if (spec.isBlank()) {
            return false;
        }
        String s = spec.trim();
        return !"none".equals(s) && !"unset".equals(s);
    }

    public static boolean affectsFilter(Style style) {
        if (!Animation.hasAnimationSpec(style)) {
            return false;
        }
        String spec = style.animation.trim();
        for (AnimationConfig config : Animation.resolve(spec, new AnimationState())) {
            Set<String> props;
            if (config.name == null || config.name.isBlank() || "none".equals(config.name) || (props = KEYFRAME_PROPS.get(config.name)) == null || props.isEmpty() || !props.contains("filter") && !props.contains("opacity")) continue;
            return true;
        }
        return false;
    }

    public static boolean affectsTransform(Style style) {
        if (!Animation.hasAnimationSpec(style)) {
            return false;
        }
        String spec = style.animation.trim();
        for (AnimationConfig config : Animation.resolve(spec, new AnimationState())) {
            Set<String> props;
            if (config.name == null || config.name.isBlank() || "none".equals(config.name) || (props = KEYFRAME_PROPS.get(config.name)) == null || !props.contains("transform")) continue;
            return true;
        }
        return false;
    }

    public static void updateStyle(Element element, Style style) {
        String spec = style.animation;
        if (spec == null || spec.equals("none")) {
            ACTIVE_ANIMATIONS.remove(element.uuid);
            return;
        }
        AnimationState state = ACTIVE_ANIMATIONS.computeIfAbsent(element.uuid, k -> new AnimationState());
        List<AnimationConfig> configs = Animation.resolve(spec, state);
        if (configs.isEmpty()) {
            ACTIVE_ANIMATIONS.remove(element.uuid);
            return;
        }
        long now = System.currentTimeMillis();
        Set<String> live = state.live;
        live.clear();
        for (AnimationConfig config : configs) {
            Animation.apply(state, element, style, config, now, live);
        }
        if (live.isEmpty()) {
            ACTIVE_ANIMATIONS.remove(element.uuid);
        } else {
            state.forgetExcept(live);
        }
    }

    private static void apply(AnimationState state, Element element, Style style, AnimationConfig config, long now, Set<String> live) {
        if ("none".equals(config.name) || !KEYFRAMES.containsKey(config.name)) {
            return;
        }
        live.add(config.name);
        long start = state.starts.computeIfAbsent(config.name, k -> now);
        boolean paused = "paused".equals(config.playState);
        if (paused) {
            state.pausedAt.putIfAbsent(config.name, now);
        } else {
            Long pausedSince = state.pausedAt.remove(config.name);
            if (pausedSince != null) {
                state.starts.put(config.name, start += now - pausedSince);
            }
        }
        double dur = config.durationMs;
        double delay = config.delayMs;
        if (dur <= 0.0) {
            return;
        }
        long sampleTime = paused ? state.pausedAt.getOrDefault(config.name, now) : now;
        long elapsed = sampleTime - start;
        double activeTime = (double)elapsed - delay;
        if (activeTime < 0.0) {
            if (config.fill.equals("backwards") || config.fill.equals("both")) {
                Animation.renderFrame(element, style, config.name, 0.0, state.changes);
            }
            return;
        }
        double count = config.iterationCount;
        if (activeTime >= dur * count) {
            if (config.fill.equals("forwards") || config.fill.equals("both")) {
                Animation.renderFrame(element, style, config.name, 100.0, state.changes);
            }
            return;
        }
        double progress = activeTime % dur / dur;
        long iter = (long)(activeTime / dur);
        if (config.direction.startsWith("alternate") && iter % 2L != 0L) {
            progress = 1.0 - progress;
        } else if (config.direction.equals("reverse")) {
            progress = 1.0 - progress;
        }
        Animation.renderFrame(element, style, config.name, config.timingFunction.apply(progress) * 100.0, state.changes);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void renderFrame(Element element, Style style, String name, double percent, List<Transition.Change> changes) {
        double highP;
        double lowP;
        changes.clear();
        TreeMap<Double, Map<String, String>> timeline = KEYFRAMES.get(name);
        if (timeline == null) {
            return;
        }
        Map.Entry<Double, Map<String, String>> lowEntry = timeline.floorEntry(percent);
        Map.Entry<Double, Map<String, String>> highEntry = timeline.ceilingEntry(percent);
        if (lowEntry == null) {
            lowEntry = timeline.firstEntry();
        }
        if (highEntry == null) {
            highEntry = timeline.lastEntry();
        }
        double fraction = (lowP = lowEntry.getKey().doubleValue()) == (highP = highEntry.getKey().doubleValue()) ? 0.0 : (percent - lowP) / (highP - lowP);
        Set<String> allProps = KEYFRAME_PROPS.get(name);
        if (allProps == null || allProps.isEmpty()) {
            return;
        }
        Size transformBasis = allProps.contains("transform") ? Size.of(element) : null;
        for (String p : allProps) {
            String vS = Animation.findProperty(timeline, percent, p, true, style.get(p));
            String vE = Animation.findProperty(timeline, percent, p, false, vS);
            if (p.equals("transform")) {
                Transform.interpolateTransform(changes, vS, vE, fraction, transformBasis.width(), transformBasis.height());
                continue;
            }
            if (p.equals("filter")) {
                Filter.interpolateFilter(changes, vS, vE, fraction);
                continue;
            }
            if (p.equals("box-shadow")) {
                Box.interpolateShadow(changes, vS, vE, fraction);
                continue;
            }
            double val = Transition.getOffset(p, Animation.parseAnimationStyle(element, p, vS), Animation.parseAnimationStyle(element, p, vE), fraction);
            Transition.addChange(changes, p, val);
        }
        try {
            Transition.applyChanges(style, changes);
        }
        finally {
            changes.clear();
        }
    }

    private static double parseAnimationStyle(Element element, String name, String value) {
        if (value == null || value.equals("unset") || value.isEmpty()) {
            return 0.0;
        }
        if (name.contains("color") || name.equals("opacity")) {
            return Transition.parseStyle(name, value);
        }
        Double resolved = Size.tryResolveLength(value, Animation.animationPercentBasis(element, name));
        if (resolved != null) {
            return resolved;
        }
        return Transition.parseStyle(name, value);
    }

    private static double animationPercentBasis(Element element, String name) {
        Element containing;
        Element element2 = containing = element == null ? null : element.parentElement;
        if (containing == null) {
            Size viewport = Size.getWindowSize();
            return Animation.isVerticalLengthProperty(name) ? viewport.height() : viewport.width();
        }
        if (Animation.isVerticalLengthProperty(name)) {
            return Size.getScaleHeight(containing);
        }
        return Size.getScaleWidth(containing);
    }

    private static boolean isVerticalLengthProperty(String name) {
        if (name == null) {
            return false;
        }
        return name.equals("top") || name.equals("bottom") || name.equals("height") || name.equals("min-height") || name.equals("max-height") || name.equals("margin-top") || name.equals("margin-bottom") || name.equals("padding-top") || name.equals("padding-bottom") || name.equals("border-top-width") || name.equals("border-bottom-width");
    }

    private static String findProperty(TreeMap<Double, Map<String, String>> timeline, double percent, String prop, boolean backward, String fallback) {
        NavigableMap<Double, Map<String, String>> subMap = backward ? timeline.headMap(percent, true).descendingMap() : timeline.tailMap(percent, true);
        for (Map step : subMap.values()) {
            if (!step.containsKey(prop)) continue;
            return (String)step.get(prop);
        }
        return fallback;
    }

    static double applyTiming(double p, String tf) {
        return Animation.compileTiming(tf).apply(p);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static List<AnimationConfig> resolve(String spec, AnimationState state) {
        List<AnimationConfig> configs;
        if (spec.equals(state.lastSpec)) {
            return state.cachedConfigs;
        }
        Map<String, List<AnimationConfig>> map = PARSED_CONFIGS;
        synchronized (map) {
            configs = PARSED_CONFIGS.get(spec);
            if (configs == null) {
                ArrayList<AnimationConfig> parsed = new ArrayList<AnimationConfig>();
                Animation.parseSpec(spec, parsed);
                configs = parsed.isEmpty() ? List.of() : List.copyOf(parsed);
                PARSED_CONFIGS.put(spec, configs);
            }
        }
        state.lastSpec = spec;
        state.cachedConfigs = configs;
        return configs;
    }

    private static void parseSpec(String spec, List<AnimationConfig> configs) {
        int depth = 0;
        int partStart = 0;
        int len = spec.length();
        for (int i = 0; i < len; ++i) {
            char ch = spec.charAt(i);
            if (ch == '(') {
                ++depth;
                continue;
            }
            if (ch == ')' && depth > 0) {
                --depth;
                continue;
            }
            if (ch != ',' || depth != 0) continue;
            Animation.parsePart(spec, partStart, i, configs);
            partStart = i + 1;
        }
        Animation.parsePart(spec, partStart, len, configs);
    }

    private static void parsePart(String spec, int start, int end, List<AnimationConfig> configs) {
        while (start < end && Character.isWhitespace(spec.charAt(start))) {
            ++start;
        }
        while (end > start && Character.isWhitespace(spec.charAt(end - 1))) {
            --end;
        }
        if (start >= end) {
            return;
        }
        AnimationConfig c = new AnimationConfig();
        for (String t : Animation.splitAnimationTokens(spec, start, end)) {
            if (t.isEmpty()) continue;
            String normalized = t.toLowerCase(Locale.ROOT);
            if (Animation.isTimeToken(t)) {
                if (c.duration.equals("0s")) {
                    c.duration = t;
                    continue;
                }
                c.delay = t;
                continue;
            }
            if ("infinite".equals(normalized) || Animation.isNumberToken(t)) {
                c.count = t;
                continue;
            }
            if (DIRECTION_SET.contains(normalized)) {
                c.direction = normalized;
                continue;
            }
            if (FILL_SET.contains(normalized)) {
                c.fill = normalized;
                continue;
            }
            if (PLAY_STATE_SET.contains(normalized)) {
                c.playState = normalized;
                continue;
            }
            if (Animation.isTimingFunctionToken(normalized)) {
                c.timing = normalized;
                continue;
            }
            c.name = t;
        }
        c.durationMs = Transition.parseTime(c.duration);
        c.delayMs = Transition.parseTime(c.delay);
        c.iterationCount = "infinite".equals(c.count) ? Double.MAX_VALUE : Double.parseDouble(c.count);
        c.timingFunction = Animation.compileTiming(c.timing);
        configs.add(c);
    }

    private static TimingFunction compileTiming(String value) {
        String normalized;
        String string = normalized = value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
        if (normalized.isEmpty()) {
            return IDENTITY_TIMING;
        }
        return TIMING_FUNCTIONS.computeIfAbsent(normalized, Animation::parseTiming);
    }

    private static TimingFunction parseTiming(String timing) {
        if ("linear".equals(timing)) {
            return IDENTITY_TIMING;
        }
        if ("step-start".equals(timing)) {
            return STEP_START_TIMING;
        }
        if ("step-end".equals(timing)) {
            return STEP_END_TIMING;
        }
        if ("ease".equals(timing)) {
            return EASE_TIMING;
        }
        if ("ease-in".equals(timing)) {
            return EASE_IN_TIMING;
        }
        if ("ease-out".equals(timing)) {
            return EASE_OUT_TIMING;
        }
        if ("ease-in-out".equals(timing)) {
            return EASE_IN_OUT_TIMING;
        }
        Matcher steps = STEPS_PATTERN.matcher(timing);
        if (steps.matches()) {
            int count = Integer.parseInt(steps.group(1));
            String mode = steps.group(2) == null ? "end" : steps.group(2);
            return new StepsTiming(count, mode);
        }
        Matcher bezier = CUBIC_BEZIER_PATTERN.matcher(timing);
        if (bezier.matches()) {
            return new CubicBezierTiming(Double.parseDouble(bezier.group(1)), Double.parseDouble(bezier.group(2)), Double.parseDouble(bezier.group(3)), Double.parseDouble(bezier.group(4)));
        }
        return IDENTITY_TIMING;
    }

    static boolean isTimingFunctionToken(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }
        if (TIMING_SET.contains(token)) {
            return true;
        }
        if (token.startsWith("steps")) {
            return STEPS_PATTERN.matcher(token).matches();
        }
        Matcher bezier = CUBIC_BEZIER_PATTERN.matcher(token);
        if (!bezier.matches()) {
            return false;
        }
        double x1 = Double.parseDouble(bezier.group(1));
        double x2 = Double.parseDouble(bezier.group(3));
        return x1 >= 0.0 && x1 <= 1.0 && x2 >= 0.0 && x2 <= 1.0;
    }

    private static List<String> splitAnimationTokens(String spec, int start, int end) {
        ArrayList<String> tokens = new ArrayList<String>();
        StringBuilder current = new StringBuilder();
        int depth = 0;
        for (int i = start; i < end; ++i) {
            char ch = spec.charAt(i);
            if (Character.isWhitespace(ch) && depth == 0) {
                if (current.isEmpty()) continue;
                tokens.add(current.toString());
                current.setLength(0);
                continue;
            }
            if (ch == '(') {
                ++depth;
            } else if (ch == ')' && depth > 0) {
                --depth;
            }
            current.append(ch);
        }
        if (!current.isEmpty()) {
            tokens.add(current.toString());
        }
        return tokens;
    }

    private static double cubicBezierAtTime(double x, double x1, double y1, double x2, double y2) {
        double estimate;
        x = Math.max(0.0, Math.min(1.0, x));
        double low = 0.0;
        double high = 1.0;
        double t = x;
        for (int i = 0; i < 12 && !(Math.abs((estimate = Animation.cubicBezierCoord(t, x1, x2)) - x) < 1.0E-5); ++i) {
            if (estimate < x) {
                low = t;
            } else {
                high = t;
            }
            t = (low + high) * 0.5;
        }
        return Animation.cubicBezierCoord(t, y1, y2);
    }

    private static double cubicBezierCoord(double t, double p1, double p2) {
        double omt = 1.0 - t;
        return 3.0 * omt * omt * t * p1 + 3.0 * omt * t * t * p2 + t * t * t;
    }

    static boolean isTimeToken(String t) {
        return t != null && TIME_PATTERN.matcher(t.trim().toLowerCase(Locale.ROOT)).matches();
    }

    private static double applySteps(double progress, int steps, String mode) {
        progress = Math.max(0.0, Math.min(1.0, progress));
        return switch (mode) {
            case "start", "jump-start" -> Math.min(1.0, (Math.floor(progress * (double)steps) + 1.0) / (double)steps);
            case "jump-none" -> {
                if (steps <= 1) {
                    yield progress;
                }
                yield Math.min(1.0, Math.floor(progress * (double)steps) / ((double)steps - 1.0));
            }
            case "jump-both" -> (Math.floor(progress * (double)steps) + 1.0) / ((double)steps + 1.0);
            case "end", "jump-end" -> Math.floor(progress * (double)steps) / (double)steps;
            default -> progress;
        };
    }

    private static boolean isNumberToken(String t) {
        if (t.isEmpty()) {
            return false;
        }
        for (int i = 0; i < t.length(); ++i) {
            char ch = t.charAt(i);
            if (ch >= '0' && ch <= '9' || ch == '.') continue;
            return false;
        }
        return true;
    }

    private static class AnimationState {
        final Map<String, Long> starts = new HashMap<String, Long>();
        final Map<String, Long> pausedAt = new HashMap<String, Long>();
        String lastSpec = null;
        List<AnimationConfig> cachedConfigs = List.of();
        final Set<String> live = new HashSet<String>();
        final Transition.ChangeBuffer changes = new Transition.ChangeBuffer();

        private AnimationState() {
        }

        void forgetExcept(Set<String> names) {
            this.starts.keySet().retainAll(names);
            this.pausedAt.keySet().retainAll(names);
        }
    }

    private static class AnimationConfig {
        String name = "none";
        String duration = "0s";
        String delay = "0s";
        String count = "1";
        String direction = "normal";
        String fill = "none";
        String timing = "ease";
        String playState = "running";
        double durationMs;
        double delayMs;
        double iterationCount = 1.0;
        TimingFunction timingFunction = EASE_TIMING;

        private AnimationConfig() {
        }
    }

    private static interface TimingFunction {
        public double apply(double var1);
    }

    private static final class StepsTiming
    implements TimingFunction {
        private final int steps;
        private final String mode;

        private StepsTiming(int steps, String mode) {
            this.steps = steps;
            this.mode = mode;
        }

        @Override
        public double apply(double progress) {
            return Animation.applySteps(progress, this.steps, this.mode);
        }
    }

    private static final class CubicBezierTiming
    implements TimingFunction {
        private final double x1;
        private final double y1;
        private final double x2;
        private final double y2;

        private CubicBezierTiming(double x1, double y1, double x2, double y2) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
        }

        @Override
        public double apply(double progress) {
            return Animation.cubicBezierAtTime(progress, this.x1, this.y1, this.x2, this.y2);
        }
    }
}

