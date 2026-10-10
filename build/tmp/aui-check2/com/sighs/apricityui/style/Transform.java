/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.style;

import com.sighs.apricityui.layout.Size;
import com.sighs.apricityui.style.Style;
import com.sighs.apricityui.style.Transition;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public interface Transform {
    public static List<Transform> parse(String transform) {
        Size window = Size.getWindowSize();
        return Transform.parse(transform, window.width(), window.height());
    }

    public static List<Transform> parse(String transform, double percentBasisWidth, double percentBasisHeight) {
        ArrayList<Transform> result = new ArrayList<Transform>();
        Translate translate = Translate.DEFAULT;
        Rotate rotate = Rotate.DEFAULT;
        Scale scale = Scale.DEFAULT;
        if (transform == null || transform.isBlank() || "none".equalsIgnoreCase(transform.trim())) {
            return List.of();
        }
        for (FunctionCall call : Transform.extractFunctionCalls(transform)) {
            String func = call.name().toLowerCase(Locale.ENGLISH);
            String argText = call.arguments().trim();
            List<String> args = Transform.splitArgs(argText);
            switch (func) {
                case "translate": 
                case "translate3d": {
                    double x = Transform.parseLength(args, 0, percentBasisWidth, percentBasisHeight);
                    double y = Transform.parseLength(args, 1, percentBasisWidth, percentBasisHeight);
                    double z = Transform.parseLength(args, 2, percentBasisWidth, percentBasisHeight);
                    result.add(new Translate(x, y, z));
                    break;
                }
                case "translatex": {
                    double x = Transform.parseLength(args, 0, percentBasisWidth);
                    result.add(new Translate(x, translate.y(), translate.z()));
                    break;
                }
                case "translatey": {
                    double y = Transform.parseLength(args, 0, percentBasisHeight);
                    result.add(new Translate(translate.x(), y, translate.z()));
                    break;
                }
                case "translatez": {
                    double z = Transform.parseLength(args, 0, percentBasisWidth, percentBasisHeight);
                    result.add(new Translate(translate.x(), translate.y(), z));
                    break;
                }
                case "rotate": 
                case "rotatez": {
                    if (args.isEmpty()) break;
                    double angDeg = Transform.parseAngleToDegrees(args.get(0));
                    result.add(new Rotate(rotate.x(), rotate.y(), angDeg));
                    break;
                }
                case "rotatex": {
                    if (args.isEmpty()) break;
                    double angDeg = Transform.parseAngleToDegrees(args.get(0));
                    result.add(new Rotate(angDeg, rotate.y(), rotate.z()));
                    break;
                }
                case "rotatey": {
                    if (args.isEmpty()) break;
                    double angDeg = Transform.parseAngleToDegrees(args.get(0));
                    result.add(new Rotate(rotate.x(), angDeg, rotate.z()));
                    break;
                }
                case "scale": {
                    if (args.size() == 1) {
                        double s = Transform.parseScale(args.get(0));
                        result.add(new Scale(s, s));
                        break;
                    }
                    if (args.size() < 2) break;
                    double sx = Transform.parseScale(args.get(0));
                    double sy = Transform.parseScale(args.get(1));
                    result.add(new Scale(sx, sy));
                    break;
                }
                case "scalex": {
                    if (args.isEmpty()) break;
                    result.add(new Scale(Transform.parseScale(args.get(0)), scale.y()));
                    break;
                }
                case "scaley": {
                    if (args.isEmpty()) break;
                    result.add(new Scale(scale.x(), Transform.parseScale(args.get(0))));
                }
            }
        }
        return result;
    }

    public static boolean createsStackingContext(String transform) {
        return transform != null && !transform.isBlank() && !"none".equalsIgnoreCase(transform.trim());
    }

    public static boolean affectsXY(String transform) {
        if (!Transform.createsStackingContext(transform)) {
            return false;
        }
        for (Transform item : Transform.parse(transform)) {
            Scale s;
            Rotate r;
            Translate t;
            if (item instanceof Translate && ((t = (Translate)item).x() != 0.0 || t.y() != 0.0)) {
                return true;
            }
            if (item instanceof Rotate && ((r = (Rotate)item).x() != 0.0 || r.y() != 0.0 || r.z() != 0.0)) {
                return true;
            }
            if (!(item instanceof Scale) || (s = (Scale)item).x() == 1.0 && s.y() == 1.0) continue;
            return true;
        }
        return false;
    }

    public static double getTranslateZ(String transform) {
        if (!Transform.createsStackingContext(transform)) {
            return 0.0;
        }
        double z = 0.0;
        for (Transform item : Transform.parse(transform)) {
            if (!(item instanceof Translate)) continue;
            Translate t = (Translate)item;
            z += t.z();
        }
        return z;
    }

    private static List<String> splitArgs(String argText) {
        ArrayList<String> out = new ArrayList<String>();
        if (argText == null || argText.isBlank()) {
            return out;
        }
        StringBuilder current = new StringBuilder();
        int depth = 0;
        for (int i = 0; i < argText.length(); ++i) {
            char c = argText.charAt(i);
            if (c == '(') {
                ++depth;
                current.append(c);
                continue;
            }
            if (c == ')') {
                depth = Math.max(0, depth - 1);
                current.append(c);
                continue;
            }
            if ((c == ',' || Character.isWhitespace(c)) && depth == 0) {
                String token = current.toString().trim();
                if (!token.isEmpty()) {
                    out.add(token);
                }
                current.setLength(0);
                continue;
            }
            current.append(c);
        }
        String token = current.toString().trim();
        if (!token.isEmpty()) {
            out.add(token);
        }
        return out;
    }

    private static List<FunctionCall> extractFunctionCalls(String transform) {
        ArrayList<FunctionCall> calls = new ArrayList<FunctionCall>();
        if (transform == null || transform.isBlank()) {
            return calls;
        }
        int length = transform.length();
        int index = 0;
        while (index < length) {
            while (index < length && Character.isWhitespace(transform.charAt(index))) {
                ++index;
            }
            if (index >= length) break;
            int nameStart = index;
            while (index < length && Character.isLetterOrDigit(transform.charAt(index))) {
                ++index;
            }
            if (index <= nameStart || index >= length || transform.charAt(index) != '(') {
                ++index;
                continue;
            }
            String name = transform.substring(nameStart, index);
            int argsStart = ++index;
            int depth = 1;
            while (index < length && depth > 0) {
                char c = transform.charAt(index);
                if (c == '(') {
                    ++depth;
                } else if (c == ')') {
                    --depth;
                }
                ++index;
            }
            if (depth != 0) break;
            String arguments = transform.substring(argsStart, index - 1);
            calls.add(new FunctionCall(name, arguments));
        }
        return calls;
    }

    private static double parseScale(String token) {
        if (token == null) {
            return 1.0;
        }
        try {
            return Double.parseDouble(token.trim());
        }
        catch (NumberFormatException ex) {
            String cleaned = token.replaceAll("[^0-9+\\-.eE]", "");
            try {
                return Double.parseDouble(cleaned);
            }
            catch (Exception e) {
                return 1.0;
            }
        }
    }

    private static double parseLength(List<String> args, int index, double percentBasisWidth, double percentBasisHeight) {
        if (args == null || index < 0 || index >= args.size()) {
            return 0.0;
        }
        double percentBasis = index == 1 ? percentBasisHeight : percentBasisWidth;
        return Transform.parseLength(args, index, percentBasis);
    }

    private static double parseLength(List<String> args, int index, double percentBasis) {
        if (args == null || index < 0 || index >= args.size()) {
            return 0.0;
        }
        String raw = args.get(index);
        Double parsed = Size.tryResolveLength(raw, percentBasis);
        return parsed == null ? 0.0 : parsed;
    }

    private static double parseAngleToDegrees(String token) {
        if (token == null) {
            return 0.0;
        }
        token = token.trim().toLowerCase(Locale.ROOT);
        try {
            if (token.endsWith("deg")) {
                return Double.parseDouble(token.substring(0, token.length() - 3));
            }
            if (token.endsWith("rad")) {
                return Math.toDegrees(Double.parseDouble(token.substring(0, token.length() - 3)));
            }
            if (token.endsWith("grad")) {
                return Double.parseDouble(token.substring(0, token.length() - 4)) * 0.9;
            }
            if (token.endsWith("turn")) {
                return Double.parseDouble(token.substring(0, token.length() - 4)) * 360.0;
            }
            return Double.parseDouble(token);
        }
        catch (NumberFormatException ex) {
            return 0.0;
        }
    }

    public static void createTransition(Style startStyle, Style endStyle, List<Transition> result, double duration, double delay) {
        long time = System.currentTimeMillis();
        ArrayList<Transform> startTransforms = new ArrayList<Transform>(Transform.parse(startStyle.transform));
        ArrayList<Transform> endTransforms = new ArrayList<Transform>(Transform.parse(endStyle.transform));
        int transformCount = Transform.padWithIdentityTransforms(startTransforms, endTransforms);
        for (int i = 0; i < transformCount; ++i) {
            Transform start = (Transform)startTransforms.get(i);
            Transform end = (Transform)endTransforms.get(i);
            if (start instanceof Translate) {
                Translate startTranslate = (Translate)start;
                if (end instanceof Translate) {
                    Translate endTranslate = (Translate)end;
                    Transform.addTransitionIfChanged(result, "transform-translatex", startTranslate.x(), endTranslate.x(), duration, delay, time);
                    Transform.addTransitionIfChanged(result, "transform-translatey", startTranslate.y(), endTranslate.y(), duration, delay, time);
                    Transform.addTransitionIfChanged(result, "transform-translatez", startTranslate.z(), endTranslate.z(), duration, delay, time);
                    continue;
                }
            }
            if (start instanceof Rotate) {
                Rotate startRotate = (Rotate)start;
                if (end instanceof Rotate) {
                    Rotate endRotate = (Rotate)end;
                    Transform.addTransitionIfChanged(result, "transform-rotatex", startRotate.x(), endRotate.x(), duration, delay, time);
                    Transform.addTransitionIfChanged(result, "transform-rotatey", startRotate.y(), endRotate.y(), duration, delay, time);
                    Transform.addTransitionIfChanged(result, "transform-rotatez", startRotate.z(), endRotate.z(), duration, delay, time);
                    continue;
                }
            }
            if (!(start instanceof Scale)) continue;
            Scale startScale = (Scale)start;
            if (!(end instanceof Scale)) continue;
            Scale endScale = (Scale)end;
            Transform.addTransitionIfChanged(result, "transform-scalex", startScale.x(), endScale.x(), duration, delay, time);
            Transform.addTransitionIfChanged(result, "transform-scaley", startScale.y(), endScale.y(), duration, delay, time);
        }
    }

    private static void addTransitionIfChanged(List<Transition> result, String name, double start, double end, double duration, double delay, long time) {
        if (Math.abs(start - end) <= 1.0E-4) {
            return;
        }
        result.add(new Transition(name, start, end, duration, delay, time));
    }

    public static void readTransition(List<Transition.Change> changeList, Style originStyle) {
        String result;
        HashMap<String, Double> vals = new HashMap<String, Double>();
        Iterator<Transition.Change> it = changeList.iterator();
        while (it.hasNext()) {
            Transition.Change c = it.next();
            if (!c.name().startsWith("transform-")) continue;
            vals.put(c.name(), c.value());
            it.remove();
        }
        if (vals.isEmpty()) {
            return;
        }
        Translate baseTranslate = Translate.DEFAULT;
        Rotate baseRotate = Rotate.DEFAULT;
        Scale baseScale = Scale.DEFAULT;
        boolean hasBaseTranslate = false;
        boolean hasBaseRotate = false;
        boolean hasBaseScale = false;
        for (Transform transform : Transform.parse(originStyle.transform)) {
            Scale value;
            if (transform instanceof Translate) {
                Translate value2;
                baseTranslate = value2 = (Translate)transform;
                hasBaseTranslate = true;
                continue;
            }
            if (transform instanceof Rotate) {
                Rotate value3;
                baseRotate = value3 = (Rotate)transform;
                hasBaseRotate = true;
                continue;
            }
            if (!(transform instanceof Scale)) continue;
            baseScale = value = (Scale)transform;
            hasBaseScale = true;
        }
        StringBuilder sb = new StringBuilder();
        if (hasBaseTranslate || vals.containsKey("transform-translatex") || vals.containsKey("transform-translatey") || vals.containsKey("transform-translatez")) {
            sb.append(String.format("translate3d(%.2fpx, %.2fpx, %.2fpx) ", vals.getOrDefault("transform-translatex", baseTranslate.x()), vals.getOrDefault("transform-translatey", baseTranslate.y()), vals.getOrDefault("transform-translatez", baseTranslate.z())));
        }
        if (hasBaseRotate || vals.containsKey("transform-rotatex") || vals.containsKey("transform-rotatey") || vals.containsKey("transform-rotatez")) {
            sb.append(String.format("rotateX(%.2fdeg) rotateY(%.2fdeg) rotateZ(%.2fdeg) ", vals.getOrDefault("transform-rotatex", baseRotate.x()), vals.getOrDefault("transform-rotatey", baseRotate.y()), vals.getOrDefault("transform-rotatez", baseRotate.z())));
        }
        if (hasBaseScale || vals.containsKey("transform-scalex") || vals.containsKey("transform-scaley")) {
            sb.append(String.format("scale(%.2f, %.2f) ", vals.getOrDefault("transform-scalex", baseScale.x()), vals.getOrDefault("transform-scaley", baseScale.y())));
        }
        if (!(result = sb.toString().trim()).isEmpty()) {
            originStyle.transform = result;
        }
    }

    public static void interpolateTransform(List<Transition.Change> changes, String start, String end, double progress) {
        Size window = Size.getWindowSize();
        Transform.interpolateTransform(changes, start, end, progress, window.width(), window.height());
    }

    public static void interpolateTransform(List<Transition.Change> changes, String start, String end, double progress, double percentBasisWidth, double percentBasisHeight) {
        ArrayList<Transform> sTs = new ArrayList<Transform>(Transform.parse(start, percentBasisWidth, percentBasisHeight));
        ArrayList<Transform> eTs = new ArrayList<Transform>(Transform.parse(end, percentBasisWidth, percentBasisHeight));
        int size = Transform.padWithIdentityTransforms(sTs, eTs);
        for (int i = 0; i < size; ++i) {
            Transform s = (Transform)sTs.get(i);
            Transform e = (Transform)eTs.get(i);
            if (s instanceof Translate) {
                Translate st = (Translate)s;
                if (e instanceof Translate) {
                    Translate et = (Translate)e;
                    Transition.addChange(changes, "transform-translatex", Transition.getOffset("x", st.x(), et.x(), progress));
                    Transition.addChange(changes, "transform-translatey", Transition.getOffset("y", st.y(), et.y(), progress));
                    Transition.addChange(changes, "transform-translatez", Transition.getOffset("z", st.z(), et.z(), progress));
                    continue;
                }
            }
            if (s instanceof Rotate) {
                Rotate sr = (Rotate)s;
                if (e instanceof Rotate) {
                    Rotate er = (Rotate)e;
                    Transition.addChange(changes, "transform-rotatex", Transition.getOffset("x", sr.x(), er.x(), progress));
                    Transition.addChange(changes, "transform-rotatey", Transition.getOffset("y", sr.y(), er.y(), progress));
                    Transition.addChange(changes, "transform-rotatez", Transition.getOffset("z", sr.z(), er.z(), progress));
                    continue;
                }
            }
            if (!(s instanceof Scale)) continue;
            Scale ss = (Scale)s;
            if (!(e instanceof Scale)) continue;
            Scale es = (Scale)e;
            Transition.addChange(changes, "transform-scalex", Transition.getOffset("x", ss.x(), es.x(), progress));
            Transition.addChange(changes, "transform-scaley", Transition.getOffset("y", ss.y(), es.y(), progress));
        }
    }

    private static int padWithIdentityTransforms(List<Transform> start, List<Transform> end) {
        int i;
        int size = Math.max(start.size(), end.size());
        for (i = start.size(); i < size; ++i) {
            start.add(Transform.getIdentity(end.get(i)));
        }
        for (i = end.size(); i < size; ++i) {
            end.add(Transform.getIdentity(start.get(i)));
        }
        return size;
    }

    private static Transform getIdentity(Transform t) {
        if (t instanceof Translate) {
            return Translate.DEFAULT;
        }
        if (t instanceof Rotate) {
            return Rotate.DEFAULT;
        }
        if (t instanceof Scale) {
            return Scale.DEFAULT;
        }
        return t;
    }

    public record Translate(double x, double y, double z) implements Transform
    {
        public static final Translate DEFAULT = new Translate(0.0, 0.0, 0.0);
    }

    public record Rotate(double x, double y, double z) implements Transform
    {
        public static final Rotate DEFAULT = new Rotate(0.0, 0.0, 0.0);
    }

    public record Scale(double x, double y) implements Transform
    {
        public static final Scale DEFAULT = new Scale(1.0, 1.0);
    }

    public record FunctionCall(String name, String arguments) {
    }
}

