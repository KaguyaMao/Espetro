/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.layout;

import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.layout.Flex;
import com.sighs.apricityui.layout.Grid;
import com.sighs.apricityui.layout.NormalFlow;
import com.sighs.apricityui.layout.Position;
import com.sighs.apricityui.layout.Size;
import com.sighs.apricityui.style.Style;
import java.util.ArrayList;
import java.util.List;

public final class Layout {
    private Layout() {
    }

    static List<String> splitTopLevelWhitespace(String value) {
        ArrayList<String> parts = new ArrayList<String>();
        if (value == null || value.isBlank()) {
            return parts;
        }
        int depth = 0;
        int start = -1;
        for (int i = 0; i < value.length(); ++i) {
            char c = value.charAt(i);
            if (c == '(') {
                ++depth;
            } else if (c == ')' && depth > 0) {
                --depth;
            }
            if (Character.isWhitespace(c) && depth == 0) {
                if (start < 0) continue;
                parts.add(value.substring(start, i));
                start = -1;
                continue;
            }
            if (start >= 0) continue;
            start = i;
        }
        if (start >= 0) {
            parts.add(value.substring(start));
        }
        return parts;
    }

    public static Position computeChildPosition(Element element, Element parent, List<Element> siblings) {
        if (parent == null) {
            return Position.ZERO;
        }
        String display = parent.getComputedStyle().display;
        if (Layout.isGridDisplay(display)) {
            return Grid.computeChildPosition(element, parent, siblings);
        }
        if (Layout.isFlexDisplay(display)) {
            return Flex.computeChildPosition(element, parent, siblings);
        }
        return NormalFlow.computeChildPosition(element, parent, siblings);
    }

    public static Size computeContentSize(Element element) {
        if (element == null) {
            return Size.ZERO;
        }
        String display = element.getComputedStyle().display;
        if (Layout.isGridDisplay(display)) {
            return Grid.computeContentSize(element);
        }
        if (Layout.isFlexDisplay(display)) {
            return Flex.computeContentSize(element);
        }
        return NormalFlow.computeContentSize(element);
    }

    public static boolean isFlexDisplay(String display) {
        if (display == null) {
            return false;
        }
        String value = display.trim().toLowerCase();
        return "flex".equals(value) || "inline-flex".equals(value);
    }

    public static boolean isGridDisplay(String display) {
        if (display == null) {
            return false;
        }
        String value = display.trim().toLowerCase();
        return "grid".equals(value) || "inline-grid".equals(value);
    }

    public static boolean isInFlow(Style style) {
        if (style == null) {
            return false;
        }
        if ("none".equals(style.display)) {
            return false;
        }
        return !"absolute".equals(style.position) && !"fixed".equals(style.position);
    }
}

