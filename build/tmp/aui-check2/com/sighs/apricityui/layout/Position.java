/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.layout;

import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.layout.Box;
import com.sighs.apricityui.layout.Layout;
import com.sighs.apricityui.layout.Size;
import com.sighs.apricityui.style.Style;
import java.util.List;
import java.util.Locale;

public class Position {
    public static final Position ZERO = new Position(0.0, 0.0);
    public double x;
    public double y;

    public Position(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Position add(Position position) {
        return new Position(this.x + position.x, this.y + position.y);
    }

    public static Position getOffset(Element element) {
        String positionType;
        if (element == null) {
            return ZERO;
        }
        Position cache = element.getRenderer().position.get();
        if (cache != null) {
            return cache;
        }
        Style style = element.getComputedStyle();
        Position resultPosition = ZERO;
        Element parent = element.parentElement;
        String string = positionType = style.position == null ? "static" : style.position;
        if (!"absolute".equals(positionType) && !"fixed".equals(positionType) && parent != null) {
            resultPosition = Position.computeNormalFlowChildPosition(element, parent, parent.getRenderChildren());
        }
        if ("relative".equals(positionType)) {
            resultPosition = resultPosition.add(Position.resolveRelativeShift(element));
        }
        if ("absolute".equals(positionType) || "fixed".equals(positionType)) {
            resultPosition = Position.resolveOutOfFlowOffset(element, positionType);
        }
        element.getRenderer().position.set(resultPosition);
        return resultPosition;
    }

    public static Position of(Element element) {
        if (element == null) {
            return ZERO;
        }
        double x = 0.0;
        double y = 0.0;
        for (Element e : element.getRouteArray()) {
            Position offset = Position.getOffset(e);
            x += offset.x;
            y += offset.y;
            if (!e.uuid.equals(element.uuid)) {
                x -= e.getScrollLeft();
                y -= e.getScrollTop();
            }
            if ("fixed".equals(e.getComputedStyle().position)) break;
        }
        return x == 0.0 && y == 0.0 ? ZERO : new Position(x, y);
    }

    public static Position forRender(Element element) {
        if (element == null) {
            return ZERO;
        }
        double x = 0.0;
        double y = 0.0;
        boolean skippingMargins = false;
        Element resumeMarginAt = null;
        for (Element e : element.getRouteArray()) {
            Position offset = Position.getOffset(e);
            x += offset.x;
            y += offset.y;
            if (e != element) {
                boolean crossesMargin;
                boolean bl = crossesMargin = !skippingMargins || e == resumeMarginAt;
                if (crossesMargin) {
                    Box box = Box.of(e);
                    x += box.getMarginLeft();
                    y += box.getMarginTop();
                }
                if (skippingMargins && e == resumeMarginAt) {
                    skippingMargins = false;
                    resumeMarginAt = null;
                }
                x -= e.getScrollLeft();
                y -= e.getScrollTop();
            }
            if ("absolute".equals(e.getComputedStyle().position)) {
                skippingMargins = true;
                resumeMarginAt = Position.findContainingBlock(e);
            }
            if ("fixed".equals(e.getComputedStyle().position)) break;
        }
        return x == 0.0 && y == 0.0 ? ZERO : new Position(x, y);
    }

    private static Position computeNormalFlowChildPosition(Element element, Element parent, List<Element> siblings) {
        return Layout.computeChildPosition(element, parent, siblings);
    }

    public static double parseSignedNumber(String str) {
        if (str == null || str.isEmpty() || "unset".equals(str)) {
            return 0.0;
        }
        for (int i = 0; i < str.length(); ++i) {
            boolean signStart;
            char c = str.charAt(i);
            boolean bl = signStart = (c == '-' || c == '+') && i + 1 < str.length() && Character.isDigit(str.charAt(i + 1));
            if (!Character.isDigit(c) && !signStart) continue;
            Double number = Size.parseNumber(str.substring(i));
            return number == null ? 0.0 : number;
        }
        return 0.0;
    }

    private static boolean isSet(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }
        String normalized = value.trim().toLowerCase();
        return !"unset".equals(normalized) && !"auto".equals(normalized);
    }

    private static Position resolveRelativeShift(Element element) {
        Style style = element.getComputedStyle();
        double basisW = Size.getScaleWidth(element);
        double basisH = Size.getScaleHeight(element);
        double left = Position.isSet(style.left) ? Size.resolveLength(style.left, basisW, 0.0) : 0.0;
        double right = Position.isSet(style.right) ? Size.resolveLength(style.right, basisW, 0.0) : 0.0;
        double top = Position.isSet(style.top) ? Size.resolveLength(style.top, basisH, 0.0) : 0.0;
        double bottom = Position.isSet(style.bottom) ? Size.resolveLength(style.bottom, basisH, 0.0) : 0.0;
        return new Position(left - right, top - bottom);
    }

    public static Element findContainingBlock(Element element) {
        Element e;
        Element element2 = e = element == null ? null : element.parentElement;
        while (e != null) {
            String value;
            String position = e.getComputedStyle().position;
            if (!(position == null || (value = position.trim().toLowerCase(Locale.ROOT)).isEmpty() || "static".equals(value) || "unset".equals(value))) {
                return e;
            }
            e = e.parentElement;
        }
        return null;
    }

    private static Position containingBlockShift(Element element, Element cb) {
        double x = 0.0;
        double y = 0.0;
        Element e = element.parentElement;
        while (e != null && e != cb) {
            Position offset = Position.getOffset(e);
            x -= offset.x;
            y -= offset.y;
            e = e.parentElement;
        }
        return new Position(x, y);
    }

    private static Position resolveOutOfFlowOffset(Element element, String positionType) {
        double containerH;
        double containerW;
        Style style = element.getComputedStyle();
        Size selfSize = Size.box(element);
        double originX = 0.0;
        double originY = 0.0;
        if ("fixed".equals(positionType)) {
            Size window = Position.viewportContainingBlockSize(element);
            containerW = window.width();
            containerH = window.height();
        } else {
            Element containingBlock = Position.findContainingBlock(element);
            if (containingBlock != null) {
                Box cbBox = Box.of(containingBlock);
                Size cbSize = Size.of(containingBlock);
                containerW = Math.max(0.0, cbSize.width() - cbBox.getBorderHorizontal());
                containerH = Math.max(0.0, cbSize.height() - cbBox.getBorderVertical());
                Position shift = Position.containingBlockShift(element, containingBlock);
                originX = cbBox.getBorderLeft() + shift.x;
                originY = cbBox.getBorderTop() + shift.y;
            } else {
                Size window = Position.viewportContainingBlockSize(element);
                containerW = window.width();
                containerH = window.height();
                Position shift = Position.containingBlockShift(element, null);
                originX = shift.x;
                originY = shift.y;
            }
        }
        boolean hasLeft = Position.isSet(style.left);
        boolean hasRight = Position.isSet(style.right);
        boolean hasTop = Position.isSet(style.top);
        boolean hasBottom = Position.isSet(style.bottom);
        double x = 0.0;
        double y = 0.0;
        if (hasLeft) {
            x = Size.resolveLength(style.left, containerW, 0.0);
        } else if (hasRight) {
            double right = Size.resolveLength(style.right, containerW, 0.0);
            x = containerW - selfSize.width() - right;
        }
        if (hasTop) {
            y = Size.resolveLength(style.top, containerH, 0.0);
        } else if (hasBottom) {
            double bottom = Size.resolveLength(style.bottom, containerH, 0.0);
            y = containerH - selfSize.height() - bottom;
        }
        return new Position(originX + x, originY + y);
    }

    static Size viewportContainingBlockSize(Element element) {
        Size viewport;
        if (element != null && element.document != null && (viewport = new Size(element.document.getViewport().layoutWidth(), element.document.getViewport().layoutHeight())).width() > 1.0 && viewport.height() > 1.0) {
            return viewport;
        }
        return Size.getWindowSize();
    }

    public String toString() {
        return "[" + this.x + "," + this.y + "]";
    }
}

