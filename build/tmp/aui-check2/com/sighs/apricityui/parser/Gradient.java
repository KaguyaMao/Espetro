/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.parser;

import com.sighs.apricityui.parser.Color;
import com.sighs.apricityui.parser.CssString;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Gradient {
    private final float angle;
    private final List<Stop> stops = new ArrayList<Stop>();
    private boolean repeating = false;

    public Gradient(float angle) {
        this.angle = angle;
    }

    public float angle() {
        return this.angle;
    }

    public List<Stop> stops() {
        return Collections.unmodifiableList(this.stops);
    }

    public boolean repeating() {
        return this.repeating;
    }

    public boolean hasHardStops() {
        for (int i = 1; i < this.stops.size(); ++i) {
            if (!(Math.abs(this.stops.get((int)i).position - this.stops.get((int)(i - 1)).position) < 1.0E-4f)) continue;
            return true;
        }
        return false;
    }

    public float repeatLengthPx() {
        if (!this.repeating) {
            return 0.0f;
        }
        float max = 0.0f;
        for (Stop stop : this.stops) {
            if (!stop.absolutePx) continue;
            max = Math.max(max, stop.position);
        }
        return max;
    }

    public int getColorAt(float x, float y, float bx, float by, float bw, float bh) {
        if (this.stops.isEmpty()) {
            return -1;
        }
        if (this.stops.size() == 1) {
            return this.stops.get((int)0).color;
        }
        double angleRad = Math.toRadians(90.0f - this.angle);
        float cx = bx + bw / 2.0f;
        float cy = by + bh / 2.0f;
        float dx = x - cx;
        float dy = y - cy;
        double cos = Math.cos((float)angleRad);
        double sin = Math.sin((float)angleRad);
        float projection = (float)((double)dx * cos + (double)dy * -sin);
        float maxDist = (float)(Math.abs((double)(bw / 2.0f) * cos) + Math.abs((double)(bh / 2.0f) * sin));
        float t = 0.5f + projection / (maxDist * 2.0f);
        return this.getInterpolatedColor(Math.max(0.0f, Math.min(1.0f, t)));
    }

    public Gradient scaledTo(float width, float height) {
        float axisLength = Math.max(1.0f, this.projectedAxisLength(width, height));
        Gradient scaled = new Gradient(this.angle);
        scaled.repeating = this.repeating;
        for (Stop stop : this.stops) {
            float position = stop.position;
            if (stop.absolutePx) {
                position = Math.max(0.0f, Math.min(1.0f, position / axisLength));
            }
            scaled.stops.add(new Stop(position, stop.color));
        }
        scaled.fixStops();
        return scaled;
    }

    private float projectedAxisLength(float bw, float bh) {
        double angleRad = Math.toRadians(90.0f - this.angle);
        double cos = Math.cos((float)angleRad);
        double sin = Math.sin((float)angleRad);
        return (float)(Math.abs((double)bw * cos) + Math.abs((double)bh * sin));
    }

    private int getInterpolatedColor(float t) {
        if (t <= this.stops.get((int)0).position) {
            return this.stops.get((int)0).color;
        }
        if (t >= this.stops.get((int)(this.stops.size() - 1)).position) {
            return this.stops.get((int)(this.stops.size() - 1)).color;
        }
        for (int i = 0; i < this.stops.size() - 1; ++i) {
            Stop s1 = this.stops.get(i);
            Stop s2 = this.stops.get(i + 1);
            if (!(t >= s1.position) || !(t <= s2.position)) continue;
            float localT = (t - s1.position) / (s2.position - s1.position);
            return Gradient.lerpColor(s1.color, s2.color, localT);
        }
        return this.stops.get((int)0).color;
    }

    private static int lerpColor(int c1, int c2, float t) {
        int a1 = c1 >> 24 & 0xFF;
        int r1 = c1 >> 16 & 0xFF;
        int g1 = c1 >> 8 & 0xFF;
        int b1 = c1 & 0xFF;
        int a2 = c2 >> 24 & 0xFF;
        int r2 = c2 >> 16 & 0xFF;
        int g2 = c2 >> 8 & 0xFF;
        int b2 = c2 & 0xFF;
        return (int)((float)a1 + (float)(a2 - a1) * t) << 24 | (int)((float)r1 + (float)(r2 - r1) * t) << 16 | (int)((float)g1 + (float)(g2 - g1) * t) << 8 | (int)((float)b1 + (float)(b2 - b1) * t);
    }

    public static Gradient parse(String css) {
        if (css == null) {
            return null;
        }
        String trimmed = css.trim();
        boolean repeating = trimmed.startsWith("repeating-linear-gradient");
        if (!repeating && !trimmed.startsWith("linear-gradient")) {
            return null;
        }
        String content = trimmed.substring(trimmed.indexOf(40) + 1, trimmed.lastIndexOf(41));
        List<String> parts = CssString.splitTopLevel(content, ',');
        if (parts.size() < 2) {
            return null;
        }
        float angle = 180.0f;
        int startIndex = 0;
        String first = parts.get(0).trim().toLowerCase();
        if (first.endsWith("deg")) {
            try {
                angle = Float.parseFloat(first.replace("deg", ""));
                startIndex = 1;
            }
            catch (NumberFormatException numberFormatException) {}
        } else if (first.startsWith("to ")) {
            angle = Gradient.parseDirection(first);
            startIndex = 1;
        }
        Gradient gradient = new Gradient(angle);
        gradient.repeating = repeating;
        for (int i = startIndex; i < parts.size(); ++i) {
            String part = parts.get(i).trim();
            StopTokens stop = Gradient.splitStop(part);
            int color = Color.parse(stop.colorToken());
            float pos = -1.0f;
            boolean absolutePx = false;
            if (stop.positionToken() != null && stop.positionToken().endsWith("%")) {
                try {
                    pos = Float.parseFloat(stop.positionToken().replace("%", "")) / 100.0f;
                }
                catch (NumberFormatException numberFormatException) {}
            } else if (stop.positionToken() != null && stop.positionToken().endsWith("px")) {
                try {
                    pos = Float.parseFloat(stop.positionToken().replace("px", ""));
                    absolutePx = true;
                }
                catch (NumberFormatException numberFormatException) {
                    // empty catch block
                }
            }
            gradient.stops.add(new Stop(pos, color, absolutePx));
        }
        gradient.fixStops();
        return gradient;
    }

    private void fixStops() {
        if (this.stops.isEmpty()) {
            return;
        }
        boolean absoluteGradient = this.stops.stream().anyMatch(stop -> stop.absolutePx);
        if (this.stops.get((int)0).position < 0.0f) {
            this.stops.get((int)0).position = 0.0f;
            this.stops.get((int)0).absolutePx = absoluteGradient;
        }
        if (this.stops.get((int)(this.stops.size() - 1)).position < 0.0f) {
            this.stops.get((int)(this.stops.size() - 1)).position = absoluteGradient ? this.findPreviousKnownPosition(this.stops.size() - 2, 0.0f) : 1.0f;
            this.stops.get((int)(this.stops.size() - 1)).absolutePx = absoluteGradient;
        }
        for (int i = 0; i < this.stops.size(); ++i) {
            int nextKnown;
            if (!(this.stops.get((int)i).position < 0.0f)) continue;
            for (nextKnown = i + 1; nextKnown < this.stops.size() && this.stops.get((int)nextKnown).position < 0.0f; ++nextKnown) {
            }
            if (nextKnown >= this.stops.size()) break;
            float startPos = i > 0 ? this.stops.get((int)(i - 1)).position : 0.0f;
            float endPos = this.stops.get((int)nextKnown).position;
            float step = (endPos - startPos) / (float)(nextKnown - (i - 1));
            for (int j = i; j < nextKnown; ++j) {
                this.stops.get((int)j).position = startPos + step * (float)(j - (i - 1));
                this.stops.get((int)j).absolutePx = this.stops.get((int)nextKnown).absolutePx;
            }
            i = nextKnown - 1;
        }
        Collections.sort(this.stops);
    }

    private float findPreviousKnownPosition(int start, float fallback) {
        for (int i = Math.min(start, this.stops.size() - 1); i >= 0; --i) {
            if (!(this.stops.get((int)i).position >= 0.0f)) continue;
            return this.stops.get((int)i).position;
        }
        return fallback;
    }

    private static float parseDirection(String dir) {
        return switch (dir) {
            case "to top" -> 0.0f;
            case "to right" -> 90.0f;
            case "to bottom" -> 180.0f;
            case "to left" -> 270.0f;
            case "to top right" -> 45.0f;
            case "to bottom right" -> 135.0f;
            case "to bottom left" -> 225.0f;
            case "to top left" -> 315.0f;
            default -> 180.0f;
        };
    }

    private static StopTokens splitStop(String raw) {
        String part;
        String string = part = raw == null ? "" : raw.trim();
        if (part.isEmpty()) {
            return new StopTokens("", null);
        }
        int parens = 0;
        for (int i = 0; i < part.length(); ++i) {
            char c = part.charAt(i);
            if (c == '(') {
                ++parens;
                continue;
            }
            if (c == ')') {
                parens = Math.max(0, parens - 1);
                continue;
            }
            if (!Character.isWhitespace(c) || parens != 0) continue;
            String colorToken = part.substring(0, i).trim();
            String positionToken = part.substring(i).trim();
            return new StopTokens(colorToken, positionToken.isEmpty() ? null : positionToken);
        }
        return new StopTokens(part, null);
    }

    public static class Stop
    implements Comparable<Stop> {
        public float position;
        public int color;
        public boolean absolutePx;

        public Stop(float position, int color) {
            this(position, color, false);
        }

        public Stop(float position, int color, boolean absolutePx) {
            this.position = position;
            this.color = color;
            this.absolutePx = absolutePx;
        }

        @Override
        public int compareTo(Stop o) {
            return Float.compare(this.position, o.position);
        }
    }

    private record StopTokens(String colorToken, String positionToken) {
    }
}

