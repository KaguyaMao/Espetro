/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.style;

import com.sighs.apricityui.dom.RenderElement;
import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.layout.Box;
import com.sighs.apricityui.layout.Size;
import com.sighs.apricityui.parser.Color;
import com.sighs.apricityui.style.Animation;
import com.sighs.apricityui.style.Filter;
import com.sighs.apricityui.style.Style;
import com.sighs.apricityui.style.StyleFrameCache;
import com.sighs.apricityui.style.Transform;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

public record Transition(String name, double start, double end, double duration, double delay, long startTime, String timing) {
    private static final Object LOCK = new Object();
    private static final Map<UUID, List<Transition>> workList = new HashMap<UUID, List<Transition>>();
    private static final ThreadLocal<ChangeBuffer> CHANGE_BUFFER = ThreadLocal.withInitial(ChangeBuffer::new);
    private static final Set<String> ANIMATABLE = Set.of("opacity", "width", "height", "filter", "transform", "color", "background-color", "top", "left", "right", "bottom", "margin-top", "margin-right", "margin-bottom", "margin-left", "padding-top", "padding-right", "padding-bottom", "padding-left", "border-top-width", "border-right-width", "border-bottom-width", "border-left-width", "border-radius", "box-shadow");

    public Transition(String name, double start, double end, double duration, double delay, long startTime) {
        this(name, start, end, duration, delay, startTime, "ease");
    }

    public static void addChange(List<Change> changes, String name, double value) {
        if (changes instanceof ChangeBuffer) {
            ChangeBuffer buffer = (ChangeBuffer)changes;
            buffer.addReusable(name, value);
        } else {
            changes.add(new Change(name, value));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void create(Element element, Style startStyle, Style endStyle) {
        String transitionSpec = Transition.resolveTransitionSpec(startStyle, endStyle);
        if (transitionSpec.equals(Style.DEFAULT.transition)) {
            Transition.cancel(element);
            return;
        }
        List<Transition> parsed = Transition.deferStartTimes(Transition.parseTransitions(element, startStyle, endStyle, transitionSpec));
        if (parsed.isEmpty()) {
            Transition.cancel(element);
            return;
        }
        Object object = LOCK;
        synchronized (object) {
            List<Transition> existing = workList.get(element.uuid);
            if (Transition.hasSameTransitionTargets(existing, parsed)) {
                return;
            }
            Transition.retargetFromActiveTransition(existing, parsed, System.currentTimeMillis());
            workList.put(element.uuid, parsed);
        }
        if (element.document != null) {
            element.document.setTransitionActive(element, true);
        }
        Transition.primeCurrentFrameStyle(element, endStyle);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static boolean isActive(Element element) {
        Object object = LOCK;
        synchronized (object) {
            return workList.containsKey(element.uuid);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void cancel(Element element) {
        boolean cancelledActiveTransition;
        if (element == null) {
            return;
        }
        Object object = LOCK;
        synchronized (object) {
            List<Transition> removed = workList.remove(element.uuid);
            cancelledActiveTransition = removed != null && !removed.isEmpty();
        }
        if (cancelledActiveTransition) {
            Transition.invalidateCancelledTransitionCaches(element);
        }
        if (element.document != null) {
            element.document.setTransitionActive(element, false);
        }
    }

    private static void invalidateCancelledTransitionCaches(Element element) {
        element.forEachRoute(routeElement -> {
            RenderElement renderer = routeElement.getRenderer();
            renderer.invalidateLayoutVersion();
            renderer.size.clear();
            renderer.box.clear();
            renderer.position.clear();
        });
        RenderElement renderer = element.getRenderer();
        renderer.invalidateTransformVersion();
        renderer.transform.clear();
        renderer.opacity.clear();
        renderer.filter.clear();
        renderer.backdropFilter.clear();
        renderer.background.clear();
        renderer.text.clear();
        renderer.wrappedText.clear();
    }

    private static boolean hasSameTransitionTargets(List<Transition> existing, List<Transition> next) {
        if (existing == null || existing.isEmpty() || next == null || existing.size() != next.size()) {
            return false;
        }
        HashMap<String, Transition> byName = new HashMap<String, Transition>();
        for (Transition transition : existing) {
            byName.put(transition.name, transition);
        }
        for (Transition transition : next) {
            Transition old = (Transition)byName.get(transition.name);
            if (old == null) {
                return false;
            }
            if (Math.abs(old.end - transition.end) > 1.0E-4) {
                return false;
            }
            if (Math.abs(old.duration - transition.duration) > 1.0E-4) {
                return false;
            }
            if (Math.abs(old.delay - transition.delay) > 1.0E-4) {
                return false;
            }
            if (Objects.equals(old.timing, transition.timing)) continue;
            return false;
        }
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static boolean affectsFilter(Element element) {
        Object object = LOCK;
        synchronized (object) {
            List<Transition> transitions = workList.get(element.uuid);
            if (transitions == null || transitions.isEmpty()) {
                return false;
            }
            for (Transition transition : transitions) {
                String n = transition.name;
                if (n == null || !n.startsWith("filter-") && !n.equals("opacity")) continue;
                return true;
            }
            return false;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static boolean updateStyle(Element element, Style originStyle) {
        List<Transition> transitions;
        Object object = LOCK;
        synchronized (object) {
            transitions = workList.get(element.uuid);
            if (transitions == null || transitions.isEmpty()) {
                return false;
            }
        }
        long now = System.currentTimeMillis();
        ChangeBuffer changes = CHANGE_BUFFER.get();
        changes.clear();
        try {
            boolean stillActive;
            Object object2 = LOCK;
            synchronized (object2) {
                block19: {
                    transitions = workList.get(element.uuid);
                    if (transitions != null && !transitions.isEmpty()) break block19;
                    boolean bl = false;
                    return bl;
                }
                ListIterator<Transition> it = transitions.listIterator();
                while (it.hasNext()) {
                    Transition t = it.next();
                    if (t.startTime < 0L) {
                        t = new Transition(t.name, t.start, t.end, t.duration, t.delay, now, t.timing);
                        it.set(t);
                    }
                    if (t.duration <= 0.0) {
                        Transition.addChange(changes, t.name, t.end);
                        it.remove();
                        continue;
                    }
                    double progress = ((double)(now - t.startTime) - t.delay) / t.duration;
                    if (progress < 0.0) continue;
                    if (progress > 1.0) {
                        progress = 1.0;
                    }
                    Transition.addChange(changes, t.name, Transition.getOffset(t.name, t.start, t.end, Animation.applyTiming(progress, t.timing)));
                    if (!(progress >= 1.0)) continue;
                    it.remove();
                }
                if (transitions.isEmpty()) {
                    workList.remove(element.uuid);
                    stillActive = false;
                } else {
                    stillActive = true;
                }
            }
            if (!changes.isEmpty()) {
                Transition.applyChanges(originStyle, changes);
            }
            boolean bl = stillActive;
            return bl;
        }
        finally {
            changes.clear();
        }
    }

    public static void applyChanges(Style style, List<Change> changes) {
        Transform.readTransition(changes, style);
        Filter.readTransition(changes, style);
        Box.readShadowTransition(changes, style);
        changes.forEach(c -> {
            if (c.name.equals("opacity")) {
                style.opacity = String.valueOf(c.value);
            } else {
                Transition.merge(style, c.name, c.value);
            }
        });
    }

    public static boolean affectsLayout(Element element) {
        return Transition.anyActiveTransitionMatches(element, Transition::isLayoutProperty);
    }

    public static boolean affectsTransform(Element element) {
        return Transition.anyActiveTransitionMatches(element, name -> name.startsWith("transform-"));
    }

    public static boolean affectsRect(Element element) {
        return Transition.anyActiveTransitionMatches(element, Transition::isRectProperty);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static boolean anyActiveTransitionMatches(Element element, Predicate<String> predicate) {
        if (element == null || predicate == null) {
            return false;
        }
        Object object = LOCK;
        synchronized (object) {
            List<Transition> transitions = workList.get(element.uuid);
            if (transitions == null || transitions.isEmpty()) {
                return false;
            }
            for (Transition transition : transitions) {
                String name = transition.name;
                if (name == null || !predicate.test(name)) continue;
                return true;
            }
            return false;
        }
    }

    private static boolean isLayoutProperty(String name) {
        if (name == null) {
            return false;
        }
        return name.equals("width") || name.equals("height") || name.equals("top") || name.equals("right") || name.equals("bottom") || name.equals("left") || name.startsWith("margin-") || name.startsWith("padding-") || name.startsWith("border-") && name.endsWith("-width");
    }

    private static boolean isRectProperty(String name) {
        if (name == null) {
            return false;
        }
        return Transition.isLayoutProperty(name) || name.equals("background-color") || name.equals("border-radius") || name.startsWith("box-shadow-");
    }

    private static void primeCurrentFrameStyle(Element element, Style endStyle) {
        if (!StyleFrameCache.isActive()) {
            return;
        }
        Style animated = endStyle.clone();
        boolean stillActive = Transition.updateStyle(element, animated);
        StyleFrameCache.put(element, animated);
        element.getRenderer().transform.clear();
        element.getRenderer().filter.clear();
        element.getRenderer().backdropFilter.clear();
        if (!Objects.equals(animated.boxShadow, endStyle.boxShadow)) {
            element.getRenderer().clearVisualBoxCache();
            element.getRenderer().invalidateStyleVersion();
        }
        if (element.document != null && !stillActive) {
            element.document.setTransitionActive(element, false);
        }
    }

    public static double getOffset(String name, double start, double end, double progress) {
        if (name.contains("color")) {
            return Color.mixColors(start, end, progress);
        }
        return (end - start) * progress + start;
    }

    public static double parseTime(String token) {
        if (token == null || token.isEmpty() || "unset".equals(token)) {
            return 0.0;
        }
        String t = token.trim().toLowerCase(Locale.ROOT);
        if (t.isEmpty() || "unset".equals(t)) {
            return 0.0;
        }
        if (t.endsWith("ms")) {
            Double value = Size.parseNumber(t.substring(0, t.length() - 2));
            return value == null ? 0.0 : value;
        }
        if (t.endsWith("s")) {
            Double value = Size.parseNumber(t.substring(0, t.length() - 1));
            return value == null ? 0.0 : value * 1000.0;
        }
        Double value = Size.parseNumber(t);
        return value == null ? 0.0 : value * 1000.0;
    }

    public static double parseStyle(String name, String value) {
        if (value == null || value.equals("unset") || value.isEmpty()) {
            return 0.0;
        }
        if (name.contains("color")) {
            return new Color(value).getValue();
        }
        if (name.equals("opacity")) {
            return Double.parseDouble(value);
        }
        Double parsed = Size.parseNumber(value);
        return parsed == null ? 0.0 : parsed;
    }

    private static double parseStyle(Element element, String name, String value) {
        if (value == null || value.equals("unset") || value.isEmpty()) {
            return 0.0;
        }
        if (name.contains("color") || name.equals("opacity")) {
            return Transition.parseStyle(name, value);
        }
        Double resolved = Size.tryResolveLength(value, Transition.transitionPercentBasis(element, name));
        if (resolved != null) {
            return resolved;
        }
        return Transition.parseStyle(name, value);
    }

    private static double transitionPercentBasis(Element element, String name) {
        Element containing;
        if (element != null && Transition.isInsetProperty(name)) {
            String position = element.getRawComputedStyle().position;
            if ("fixed".equals(position)) {
                Size viewport = Size.getWindowSize();
                return Transition.isVerticalLengthProperty(name) ? viewport.height() : viewport.width();
            }
            if ("absolute".equals(position)) {
                Double containingBlock;
                Double d = containingBlock = Transition.isVerticalLengthProperty(name) ? Size.getContainingBlockPaddingBoxHeight(element) : Size.getContainingBlockPaddingBoxWidth(element);
                if (containingBlock != null) {
                    return containingBlock;
                }
            }
        }
        Element element2 = containing = element == null ? null : element.parentElement;
        if (containing == null) {
            Size viewport = Size.getWindowSize();
            return Transition.isVerticalLengthProperty(name) ? viewport.height() : viewport.width();
        }
        return Transition.isVerticalLengthProperty(name) ? Size.getScaleHeight(containing) : Size.getScaleWidth(containing);
    }

    private static boolean isVerticalLengthProperty(String name) {
        if (name == null) {
            return false;
        }
        return name.equals("top") || name.equals("bottom") || name.equals("height") || name.equals("min-height") || name.equals("max-height") || name.equals("minHeight") || name.equals("maxHeight") || name.equals("margin-top") || name.equals("margin-bottom") || name.equals("marginTop") || name.equals("marginBottom") || name.equals("padding-top") || name.equals("padding-bottom") || name.equals("paddingTop") || name.equals("paddingBottom") || name.equals("border-top-width") || name.equals("border-bottom-width") || name.equals("borderTop") || name.equals("borderBottom");
    }

    private static boolean isInsetProperty(String name) {
        return "top".equals(name) || "right".equals(name) || "bottom".equals(name) || "left".equals(name);
    }

    public static void merge(Style style, String name, double value) {
        if (name.contains("color")) {
            style.update(name, new Color(value).toRgbaString());
        } else if (name.equals("opacity")) {
            style.opacity = String.valueOf(value);
        } else {
            style.update(name, String.format("%.2fpx", value));
        }
    }

    private static String resolveTransitionSpec(Style startStyle, Style endStyle) {
        if (endStyle.transition != null && !endStyle.transition.isBlank() && !endStyle.transition.equals("none")) {
            return endStyle.transition;
        }
        return startStyle.transition == null ? "none" : startStyle.transition;
    }

    private static List<Transition> parseTransitions(Element element, Style startStyle, Style endStyle, String raw) {
        ArrayList<Transition> result = new ArrayList<Transition>();
        if (raw == null || raw.isBlank()) {
            return result;
        }
        for (String part : Transition.splitTransitionParts(raw)) {
            String prop = "all";
            String timing = "ease";
            double dur = 0.0;
            double del = 0.0;
            int timeCount = 0;
            boolean invalid = false;
            for (String token : Transition.splitTransitionTokens(part)) {
                String normalized = token.toLowerCase(Locale.ROOT);
                if (Transition.isTimeToken(normalized)) {
                    if (timeCount++ == 0) {
                        dur = Transition.parseTime(normalized);
                        if (!(dur < 0.0)) continue;
                        invalid = true;
                        continue;
                    }
                    if (timeCount == 2) {
                        del = Transition.parseTime(normalized);
                        continue;
                    }
                    invalid = true;
                    continue;
                }
                if (Transition.isTimingFunctionToken(normalized)) {
                    timing = normalized;
                    continue;
                }
                if (normalized.startsWith("steps(") || normalized.startsWith("cubic-bezier(")) {
                    invalid = true;
                    continue;
                }
                if ("none".equals(normalized)) {
                    prop = "none";
                    continue;
                }
                prop = token;
            }
            if (invalid || "none".equals(prop)) continue;
            if ("all".equals(prop)) {
                double finalDur = dur;
                double finalDel = del;
                String finalTiming = timing;
                ANIMATABLE.forEach(name -> Transition.build(element, startStyle, endStyle, result, name, finalDur, finalDel, finalTiming));
                continue;
            }
            Transition.build(element, startStyle, endStyle, result, prop, dur, del, timing);
        }
        return result;
    }

    private static List<Transition> deferStartTimes(List<Transition> transitions) {
        if (transitions == null || transitions.isEmpty()) {
            return transitions;
        }
        ArrayList<Transition> result = new ArrayList<Transition>(transitions.size());
        for (Transition transition : transitions) {
            result.add(new Transition(transition.name, transition.start, transition.end, transition.duration, transition.delay, -1L, transition.timing));
        }
        return result;
    }

    private static void build(Element element, Style sS, Style eS, List<Transition> res, String name, double dur, double del, String timing) {
        if (!Transition.isAnimatable(name)) {
            return;
        }
        int first = res.size();
        if (name.equals("transform")) {
            Transform.createTransition(sS, eS, res, dur, del);
        } else if (name.equals("filter")) {
            Filter.createTransition(sS, eS, res, dur, del);
        } else if (name.equals("box-shadow")) {
            Box.createShadowTransition(sS, eS, res, dur, del);
        } else if (Box.matchStyleName(name)) {
            Box.createTransition(sS, eS, res, name, dur, del);
        } else {
            Double s = Transition.parseInterpolableStyle(element, name, sS.get(name));
            Double e = Transition.parseInterpolableStyle(element, name, eS.get(name));
            if (s == null || e == null) {
                return;
            }
            if (Math.abs(s - e) > 1.0E-4) {
                res.add(new Transition(name, s, e, dur, del, System.currentTimeMillis()));
            }
        }
        for (int i = first; i < res.size(); ++i) {
            Transition transition = res.get(i);
            res.set(i, new Transition(transition.name, transition.start, transition.end, transition.duration, transition.delay, transition.startTime, timing));
        }
    }

    private static List<String> splitTransitionParts(String raw) {
        ArrayList<String> parts = new ArrayList<String>();
        int depth = 0;
        int start = 0;
        for (int i = 0; i < raw.length(); ++i) {
            char ch = raw.charAt(i);
            if (ch == '(') {
                ++depth;
                continue;
            }
            if (ch == ')' && depth > 0) {
                --depth;
                continue;
            }
            if (ch != ',' || depth != 0) continue;
            parts.add(raw.substring(start, i));
            start = i + 1;
        }
        parts.add(raw.substring(start));
        return parts;
    }

    private static List<String> splitTransitionTokens(String part) {
        ArrayList<String> tokens = new ArrayList<String>();
        StringBuilder current = new StringBuilder();
        int depth = 0;
        for (int i = 0; i < part.length(); ++i) {
            char ch = part.charAt(i);
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

    private static boolean isTimeToken(String token) {
        return Animation.isTimeToken(token);
    }

    private static boolean isTimingFunctionToken(String token) {
        return Animation.isTimingFunctionToken(token);
    }

    private static void retargetFromActiveTransition(List<Transition> existing, List<Transition> next, long now) {
        if (existing == null || existing.isEmpty() || next == null || next.isEmpty()) {
            return;
        }
        HashMap<String, Transition> activeByName = new HashMap<String, Transition>();
        for (Transition transition : existing) {
            activeByName.put(transition.name, transition);
        }
        ListIterator<Transition> it = next.listIterator();
        while (it.hasNext()) {
            double span;
            Transition replacement = it.next();
            Transition active = (Transition)activeByName.get(replacement.name);
            if (active == null) continue;
            double current = Transition.currentValue(active, now);
            double duration = replacement.duration;
            if (Transition.isReversing(active, replacement.end) && (span = Math.abs(active.end - active.start)) > 1.0E-4) {
                duration *= Math.min(1.0, Math.abs(current - active.start) / span);
            }
            it.set(new Transition(replacement.name, current, replacement.end, duration, replacement.delay, replacement.startTime, replacement.timing));
        }
    }

    private static boolean isReversing(Transition active, double newEnd) {
        return Math.abs(newEnd - active.start) <= 1.0E-4;
    }

    private static double currentValue(Transition transition, long now) {
        if (transition.startTime < 0L || (double)now <= (double)transition.startTime + transition.delay) {
            return transition.start;
        }
        if (transition.duration <= 0.0) {
            return transition.end;
        }
        double progress = ((double)(now - transition.startTime) - transition.delay) / transition.duration;
        if (progress <= 0.0) {
            return transition.start;
        }
        if (progress >= 1.0) {
            return transition.end;
        }
        return Transition.getOffset(transition.name, transition.start, transition.end, Animation.applyTiming(progress, transition.timing));
    }

    private static boolean isAnimatable(String name) {
        return "transform".equals(name) || "filter".equals(name) || Box.matchStyleName(name) || ANIMATABLE.contains(name);
    }

    private static Double parseInterpolableStyle(Element element, String name, String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        if (name.contains("color")) {
            return new Color(value).getValue();
        }
        if (name.equals("opacity")) {
            try {
                return Double.parseDouble(value);
            }
            catch (NumberFormatException ignored) {
                return null;
            }
        }
        return Size.tryResolveLength(value, Transition.transitionPercentBasis(element, name));
    }

    static final class ChangeBuffer
    extends ArrayList<Change> {
        private final ArrayList<Change> pool = new ArrayList();

        ChangeBuffer() {
        }

        void addReusable(String name, double value) {
            Change change;
            int index = this.size();
            if (index < this.pool.size()) {
                change = this.pool.get(index);
                change.set(name, value);
            } else {
                change = new Change(name, value);
                this.pool.add(change);
            }
            super.add(change);
        }
    }

    public static final class Change {
        private String name;
        private double value;

        public Change(String name, double value) {
            this.name = name;
            this.value = value;
        }

        public String name() {
            return this.name;
        }

        public double value() {
            return this.value;
        }

        private void set(String name, double value) {
            this.name = name;
            this.value = value;
        }

        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof Change)) {
                return false;
            }
            Change change = (Change)other;
            return Double.compare(this.value, change.value) == 0 && Objects.equals(this.name, change.name);
        }

        public int hashCode() {
            return Objects.hash(this.name, this.value);
        }

        public String toString() {
            return "Change[name=" + this.name + ", value=" + this.value + "]";
        }
    }
}

