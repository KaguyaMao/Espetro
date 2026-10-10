/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.style;

import com.sighs.apricityui.layout.Size;
import com.sighs.apricityui.parser.CssString;
import com.sighs.apricityui.style.Style;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

public final class ShorthandParser {
    private static final Pattern TIME_TOKEN_PATTERN = Pattern.compile("[-+]?(?:\\d*\\.\\d+|\\d+)(?:ms|s)");
    private static final Pattern NUMBER_TOKEN_PATTERN = Pattern.compile("[-+]?(?:\\d*\\.\\d+|\\d+)");
    private static final Set<String> ANIMATION_DIRECTIONS = Set.of("normal", "reverse", "alternate", "alternate-reverse");
    private static final Set<String> ANIMATION_FILL_MODES = Set.of("none", "forwards", "backwards", "both");
    private static final Set<String> ANIMATION_TIMING_FUNCTIONS = Set.of("linear", "ease", "ease-in", "ease-out", "ease-in-out", "step-start", "step-end");

    private ShorthandParser() {
    }

    public static void applyBox(Style style, String baseName, String raw) {
        String leftValue;
        String bottomValue;
        String rightValue;
        String topValue;
        String value = raw == null ? "" : raw.trim();
        style.setFieldValue(baseName, value.isEmpty() ? "unset" : value);
        if (ShorthandParser.isCssWideKeyword(value)) {
            topValue = value;
            rightValue = value;
            bottomValue = value;
            leftValue = value;
        } else {
            String[] expanded = ShorthandParser.expandFourSideTokens(value);
            topValue = expanded[0];
            rightValue = expanded[1];
            bottomValue = expanded[2];
            leftValue = expanded[3];
        }
        style.setFieldValue(baseName + "Top", topValue);
        style.setFieldValue(baseName + "Right", rightValue);
        style.setFieldValue(baseName + "Bottom", bottomValue);
        style.setFieldValue(baseName + "Left", leftValue);
    }

    public static void applyBorder(Style style, String raw) {
        String resolved;
        String value = raw == null ? "" : raw.trim();
        style.border = value.isEmpty() ? "unset" : value;
        style.borderTop = resolved = value.isEmpty() ? "unset" : value;
        style.borderRight = resolved;
        style.borderBottom = resolved;
        style.borderLeft = resolved;
    }

    public static void applyBorderWidth(Style style, String raw) {
        String[] stringArray;
        String value;
        String string = value = raw == null ? "" : raw.trim();
        if (value.isEmpty()) {
            return;
        }
        style.borderWidth = value;
        if (ShorthandParser.isCssWideKeyword(value)) {
            String[] stringArray2 = new String[4];
            stringArray2[0] = value;
            stringArray2[1] = value;
            stringArray2[2] = value;
            stringArray = stringArray2;
            stringArray2[3] = value;
        } else {
            stringArray = ShorthandParser.expandFourSideTokens(value);
        }
        String[] widths = stringArray;
        style.borderTop = ShorthandParser.replaceBorderWidth(style, style.borderTop, widths[0]);
        style.borderRight = ShorthandParser.replaceBorderWidth(style, style.borderRight, widths[1]);
        style.borderBottom = ShorthandParser.replaceBorderWidth(style, style.borderBottom, widths[2]);
        style.borderLeft = ShorthandParser.replaceBorderWidth(style, style.borderLeft, widths[3]);
    }

    public static void applyBorderColor(Style style, String raw) {
        String[] stringArray;
        String value;
        String string = value = raw == null ? "" : raw.trim();
        if (value.isEmpty()) {
            return;
        }
        style.borderColor = value;
        if (ShorthandParser.isCssWideKeyword(value)) {
            String[] stringArray2 = new String[4];
            stringArray2[0] = value;
            stringArray2[1] = value;
            stringArray2[2] = value;
            stringArray = stringArray2;
            stringArray2[3] = value;
        } else {
            stringArray = ShorthandParser.expandFourSideTokens(value);
        }
        String[] colors = stringArray;
        style.borderTop = ShorthandParser.replaceBorderColor(style, style.borderTop, colors[0]);
        style.borderRight = ShorthandParser.replaceBorderColor(style, style.borderRight, colors[1]);
        style.borderBottom = ShorthandParser.replaceBorderColor(style, style.borderBottom, colors[2]);
        style.borderLeft = ShorthandParser.replaceBorderColor(style, style.borderLeft, colors[3]);
    }

    public static void applyBorderSidePart(Style style, String styleName, String raw, boolean width) {
        String side = styleName.substring("border".length(), styleName.length() - (width ? "Width".length() : "Color".length()));
        String sideField = "border" + side;
        String current = style.getFieldValue(sideField);
        String value = raw == null ? "" : raw.trim();
        String updated = width ? ShorthandParser.replaceBorderWidth(style, current, value) : ShorthandParser.replaceBorderColor(style, current, value);
        style.setFieldValue(sideField, updated);
    }

    private static String replaceBorderWidth(Style style, String current, String width) {
        String normalizedWidth;
        String string = normalizedWidth = width == null || width.isBlank() ? "unset" : width.trim();
        if (ShorthandParser.isCssWideKeyword(normalizedWidth)) {
            return normalizedWidth;
        }
        String base = current == null || current.isBlank() || "unset".equalsIgnoreCase(current.trim()) ? "0px solid #000000" : current.trim();
        CharSequence[] tokens = (String[])CssString.splitTopLevelTokens(base).toArray(String[]::new);
        if (tokens.length == 0) {
            return normalizedWidth + " solid #000000";
        }
        boolean replaced = false;
        for (int i = 0; i < tokens.length; ++i) {
            if (!ShorthandParser.looksLikeCssLength(tokens[i]) && !ShorthandParser.isBorderWidthVariable((String[])tokens, i)) continue;
            tokens[i] = normalizedWidth;
            replaced = true;
            break;
        }
        if (!replaced) {
            return normalizedWidth + " " + base;
        }
        return String.join((CharSequence)" ", tokens);
    }

    private static String replaceBorderColor(Style style, String current, String color) {
        String normalizedColor;
        String string = normalizedColor = color == null || color.isBlank() ? "unset" : color.trim();
        if (ShorthandParser.isCssWideKeyword(normalizedColor)) {
            return normalizedColor;
        }
        String base = current == null || current.isBlank() || "unset".equalsIgnoreCase(current.trim()) ? "0px solid #000000" : current.trim();
        ArrayList<String> tokens = new ArrayList<String>(CssString.splitTopLevelTokens(base));
        if (tokens.isEmpty()) {
            return "0px solid " + normalizedColor;
        }
        boolean replaced = false;
        for (int i = 0; i < tokens.size(); ++i) {
            if ((!CssString.isColorToken((String)tokens.get(i)) || ShorthandParser.isVarToken((String)tokens.get(i))) && !ShorthandParser.isBorderColorVariable(tokens, i)) continue;
            tokens.set(i, normalizedColor);
            replaced = true;
            break;
        }
        if (!replaced) {
            tokens.add(normalizedColor);
        }
        return String.join((CharSequence)" ", tokens);
    }

    private static boolean isBorderWidthVariable(String[] tokens, int index) {
        if (!ShorthandParser.isVarToken(tokens[index])) {
            return false;
        }
        for (int i = 0; i < index; ++i) {
            if (!ShorthandParser.isBorderStyleToken(tokens[i])) continue;
            return false;
        }
        return true;
    }

    private static boolean isBorderColorVariable(List<String> tokens, int index) {
        if (!ShorthandParser.isVarToken(tokens.get(index))) {
            return false;
        }
        for (int i = 0; i < index; ++i) {
            if (!ShorthandParser.isBorderStyleToken(tokens.get(i))) continue;
            return true;
        }
        return false;
    }

    private static boolean isBorderStyleToken(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }
        return switch (token.trim().toLowerCase(Locale.ROOT)) {
            case "none", "hidden", "dotted", "dashed", "solid", "double", "groove", "ridge", "inset", "outset" -> true;
            default -> false;
        };
    }

    private static boolean looksLikeCssLength(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }
        String lower = token.trim().toLowerCase(Locale.ROOT);
        return lower.equals("0") || lower.matches("-?\\d+(?:\\.\\d+)?(?:px|rem|em|vw|vh|%)?");
    }

    public static void applyBackground(Style style, String raw) {
        String value = raw == null ? "" : raw.trim();
        style.backgroundColor = "unset";
        style.backgroundImage = "unset";
        style.backgroundRepeat = "unset";
        style.backgroundSize = "unset";
        style.backgroundPosition = "unset";
        if (value.isEmpty() || "unset".equalsIgnoreCase(value)) {
            return;
        }
        if ("none".equalsIgnoreCase(value)) {
            style.backgroundImage = "none";
            return;
        }
        StringBuilder image = new StringBuilder();
        StringBuilder position = new StringBuilder();
        StringBuilder size = new StringBuilder();
        boolean afterSlash = false;
        for (String token : CssString.splitTopLevelTokens(value)) {
            StringBuilder target;
            String lowerToken = token.toLowerCase(Locale.ROOT);
            if ("/".equals(token)) {
                afterSlash = true;
                continue;
            }
            if (CssString.isColorToken(token) || ShorthandParser.isVarToken(token)) {
                style.backgroundColor = token;
                continue;
            }
            if (ShorthandParser.isBackgroundRepeatToken(lowerToken)) {
                style.backgroundRepeat = token;
                continue;
            }
            if (ShorthandParser.isBackgroundImageToken(lowerToken)) {
                if (!image.isEmpty()) {
                    image.append(' ');
                }
                image.append(token);
                continue;
            }
            StringBuilder stringBuilder = target = afterSlash ? size : position;
            if (!target.isEmpty()) {
                target.append(' ');
            }
            target.append(token);
        }
        if (!image.isEmpty()) {
            style.backgroundImage = image.toString();
        }
        if (!position.isEmpty()) {
            style.backgroundPosition = position.toString();
        }
        if (!size.isEmpty()) {
            style.backgroundSize = size.toString();
        }
    }

    public static void applyFlex(Style style, String raw) {
        String value = raw == null ? "" : raw.trim();
        String string = style.flex = value.isEmpty() ? "unset" : value;
        if (value.isEmpty() || value.equalsIgnoreCase("unset") || value.equalsIgnoreCase("initial")) {
            style.flexGrow = "0";
            style.flexShrink = "1";
            style.flexBasis = "auto";
            return;
        }
        if (value.equalsIgnoreCase("none")) {
            style.flexGrow = "0";
            style.flexShrink = "0";
            style.flexBasis = "auto";
            return;
        }
        if (value.equalsIgnoreCase("auto")) {
            style.flexGrow = "1";
            style.flexShrink = "1";
            style.flexBasis = "auto";
            return;
        }
        String[] parts = value.split("\\s+");
        if (parts.length == 1) {
            Double grow = Size.parseNumber(parts[0]);
            if (grow != null) {
                style.flexGrow = ShorthandParser.trimNumber(grow);
                style.flexShrink = "1";
                style.flexBasis = "0%";
                return;
            }
            style.flexGrow = "1";
            style.flexShrink = "1";
            style.flexBasis = parts[0];
            return;
        }
        int numericCount = 0;
        String basis = "auto";
        String growValue = "0";
        String shrinkValue = "1";
        for (String part : parts) {
            Double number = Size.parseNumber(part);
            if (number != null && !Size.isPercent(part) && !part.endsWith("px")) {
                if (numericCount == 0) {
                    growValue = ShorthandParser.trimNumber(number);
                } else if (numericCount == 1) {
                    shrinkValue = ShorthandParser.trimNumber(number);
                } else {
                    basis = part;
                }
                ++numericCount;
                continue;
            }
            basis = part;
        }
        style.flexGrow = growValue;
        style.flexShrink = shrinkValue;
        style.flexBasis = basis;
    }

    public static void applyGap(Style style, String raw) {
        String value = raw == null ? "" : raw.trim();
        String string = style.gap = value.isEmpty() ? "0px" : value;
        if (value.isEmpty()) {
            style.rowGap = "0px";
            style.columnGap = "0px";
            return;
        }
        if (ShorthandParser.isCssWideKeyword(value)) {
            style.rowGap = value;
            style.columnGap = value;
            return;
        }
        String[] parts = value.split("\\s+");
        String rowValue = parts.length > 0 ? parts[0] : "0px";
        String columnValue = parts.length > 1 ? parts[1] : rowValue;
        style.rowGap = rowValue;
        style.columnGap = columnValue;
    }

    public static void applyInset(Style style, String raw) {
        String[] stringArray;
        String value;
        String string = value = raw == null ? "" : raw.trim();
        if (value.isEmpty()) {
            value = "unset";
        }
        if (ShorthandParser.isCssWideKeyword(value)) {
            String[] stringArray2 = new String[4];
            stringArray2[0] = value;
            stringArray2[1] = value;
            stringArray2[2] = value;
            stringArray = stringArray2;
            stringArray2[3] = value;
        } else {
            stringArray = ShorthandParser.expandFourSideTokens(value);
        }
        String[] expanded = stringArray;
        style.top = expanded[0];
        style.right = expanded[1];
        style.bottom = expanded[2];
        style.left = expanded[3];
    }

    public static void applyAnimation(Style style, String raw) {
        String value = raw == null ? "" : raw.trim();
        style.animation = value.isEmpty() ? "unset" : value;
        style.animationName = "unset";
        style.animationDuration = "unset";
        style.animationDelay = "unset";
        style.animationIterationCount = "unset";
        style.animationDirection = "unset";
        style.animationFillMode = "unset";
        style.animationTimingFunction = "unset";
        style.animationPlayState = "unset";
        if (value.isEmpty() || ShorthandParser.isCssWideKeyword(value) || "none".equalsIgnoreCase(value)) {
            String string = style.animation = value.isEmpty() ? "unset" : value;
            if ("none".equalsIgnoreCase(value)) {
                style.animationName = "none";
                style.animationDuration = "0s";
                style.animationDelay = "0s";
                style.animationIterationCount = "1";
                style.animationDirection = "normal";
                style.animationFillMode = "none";
                style.animationTimingFunction = "ease";
                style.animationPlayState = "running";
            }
            return;
        }
        List<String> tokens = ShorthandParser.splitAnimationTokens(value);
        for (String token : tokens) {
            if (ShorthandParser.isTimeToken(token)) {
                if ("unset".equals(style.animationDuration)) {
                    style.animationDuration = token;
                    continue;
                }
                style.animationDelay = token;
                continue;
            }
            String normalized = token.toLowerCase(Locale.ROOT);
            if ("infinite".equals(normalized) || ShorthandParser.isNumberToken(normalized)) {
                style.animationIterationCount = token;
                continue;
            }
            if (ANIMATION_DIRECTIONS.contains(normalized)) {
                style.animationDirection = normalized;
                continue;
            }
            if (ANIMATION_FILL_MODES.contains(normalized)) {
                style.animationFillMode = normalized;
                continue;
            }
            if ("running".equals(normalized) || "paused".equals(normalized)) {
                style.animationPlayState = normalized;
                continue;
            }
            if (ShorthandParser.isTimingFunctionToken(normalized)) {
                style.animationTimingFunction = token;
                continue;
            }
            style.animationName = token;
        }
    }

    public static void applyRotate(Style style, String raw) {
        String currentTransform;
        String value = raw == null ? "" : raw.trim();
        String string = style.rotate = value.isEmpty() ? "none" : value;
        if (style.rotate.isBlank() || "none".equalsIgnoreCase(style.rotate)) {
            return;
        }
        String rotateFn = "rotate(" + style.rotate + ")";
        String string2 = currentTransform = style.transform == null ? "" : style.transform.trim();
        if (currentTransform.isEmpty() || "none".equalsIgnoreCase(currentTransform)) {
            style.transform = rotateFn;
            return;
        }
        style.transform = currentTransform + " " + rotateFn;
    }

    private static String trimNumber(Double value) {
        if (value == null) {
            return "0";
        }
        if (Math.abs(value - Math.rint(value)) < 1.0E-6) {
            return Integer.toString((int)Math.rint(value));
        }
        return Double.toString(value);
    }

    private static boolean isVarToken(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }
        String value = token.trim().toLowerCase(Locale.ROOT);
        return value.startsWith("var(") && value.endsWith(")");
    }

    private static boolean isBackgroundImageToken(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }
        return token.contains("url(") || token.contains("gradient(");
    }

    private static boolean isBackgroundRepeatToken(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }
        return switch (token) {
            case "repeat", "repeat-x", "repeat-y", "no-repeat", "space", "round" -> true;
            default -> false;
        };
    }

    public static boolean isCssWideKeyword(String value) {
        if (value == null) {
            return false;
        }
        String normalized = value.trim().toLowerCase(Locale.ROOT);
        return normalized.equals("inherit") || normalized.equals("initial") || normalized.equals("unset") || normalized.equals("revert") || normalized.equals("revert-layer");
    }

    public static String[] expandFourSideTokens(String raw) {
        String[] stringArray;
        if (raw == null || raw.isBlank()) {
            return new String[]{"unset", "unset", "unset", "unset"};
        }
        String[] tokens = new String[4];
        int count = 0;
        int index = 0;
        while (index < raw.length() && count < tokens.length) {
            while (index < raw.length() && Character.isWhitespace(raw.charAt(index))) {
                ++index;
            }
            if (index >= raw.length()) break;
            int start = index;
            while (index < raw.length() && !Character.isWhitespace(raw.charAt(index))) {
                ++index;
            }
            tokens[count++] = raw.substring(start, index);
        }
        if (count == 0) {
            return new String[]{"unset", "unset", "unset", "unset"};
        }
        switch (count) {
            case 1: {
                String[] stringArray2 = new String[4];
                stringArray2[0] = tokens[0];
                stringArray2[1] = tokens[0];
                stringArray2[2] = tokens[0];
                stringArray = stringArray2;
                stringArray2[3] = tokens[0];
                break;
            }
            case 2: {
                String[] stringArray3 = new String[4];
                stringArray3[0] = tokens[0];
                stringArray3[1] = tokens[1];
                stringArray3[2] = tokens[0];
                stringArray = stringArray3;
                stringArray3[3] = tokens[1];
                break;
            }
            case 3: {
                String[] stringArray4 = new String[4];
                stringArray4[0] = tokens[0];
                stringArray4[1] = tokens[1];
                stringArray4[2] = tokens[2];
                stringArray = stringArray4;
                stringArray4[3] = tokens[1];
                break;
            }
            default: {
                stringArray = tokens;
            }
        }
        return stringArray;
    }

    public static List<String> splitAnimationTokens(String value) {
        ArrayList<String> tokens = new ArrayList<String>();
        StringBuilder current = new StringBuilder();
        int depth = 0;
        for (int i = 0; i < value.length(); ++i) {
            char ch = value.charAt(i);
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

    public static boolean isTimeToken(String token) {
        return token != null && TIME_TOKEN_PATTERN.matcher(token.trim().toLowerCase(Locale.ROOT)).matches();
    }

    public static boolean isNumberToken(String token) {
        return token != null && NUMBER_TOKEN_PATTERN.matcher(token.trim()).matches();
    }

    public static boolean isTimingFunctionToken(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }
        if (ANIMATION_TIMING_FUNCTIONS.contains(token)) {
            return true;
        }
        return token.startsWith("steps(") || token.startsWith("cubic-bezier(");
    }
}

