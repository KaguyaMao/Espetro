/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.parser;

import com.sighs.apricityui.parser.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public final class CssString {
    private CssString() {
    }

    public static Set<String> parseClassNames(String value) {
        if (value == null) {
            return Collections.emptySet();
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return Collections.emptySet();
        }
        LinkedHashSet<String> classNames = new LinkedHashSet<String>(Arrays.asList(trimmed.split("\\s+")));
        if (classNames.isEmpty()) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(classNames);
    }

    public static boolean isGeneratedPseudoContent(String raw) {
        String content = raw == null ? "" : raw.trim();
        return !content.isEmpty() && !"normal".equalsIgnoreCase(content) && !"none".equalsIgnoreCase(content) && !"unset".equalsIgnoreCase(content);
    }

    public static String parsePseudoContentText(String raw) {
        if (raw == null) {
            return "";
        }
        String value = raw.trim();
        if (value.length() >= 2) {
            char first = value.charAt(0);
            char last = value.charAt(value.length() - 1);
            if (first == '\"' && last == '\"' || first == '\'' && last == '\'') {
                return CssString.unescapeCssString(value.substring(1, value.length() - 1));
            }
        }
        return "";
    }

    public static String unescapeCssString(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }
        StringBuilder result = new StringBuilder(value.length());
        int index = 0;
        while (index < value.length()) {
            char current;
            if ((current = value.charAt(index++)) != '\\') {
                result.append(current);
                continue;
            }
            if (index >= value.length()) {
                result.append('\\');
                break;
            }
            char escaped = value.charAt(index);
            if (CssString.isCssHexDigit(escaped)) {
                int codePoint = 0;
                for (int digits = 0; index < value.length() && digits < 6 && CssString.isCssHexDigit(value.charAt(index)); ++digits) {
                    codePoint = (codePoint << 4) + Character.digit(value.charAt(index++), 16);
                }
                if (index < value.length() && CssString.isCssWhitespace(value.charAt(index))) {
                    index = value.charAt(index) == '\r' && index + 1 < value.length() && value.charAt(index + 1) == '\n' ? (index += 2) : ++index;
                }
                if (codePoint == 0 || codePoint > 0x10FFFF || codePoint >= 55296 && codePoint <= 57343) {
                    codePoint = 65533;
                }
                result.appendCodePoint(codePoint);
                continue;
            }
            if (CssString.isCssNewline(escaped)) {
                if (escaped != '\r' || ++index >= value.length() || value.charAt(index) != '\n') continue;
                ++index;
                continue;
            }
            result.append(escaped);
            ++index;
        }
        return result.toString();
    }

    public static int findTopLevelDelimiter(String value, char delimiter) {
        if (value == null) {
            return -1;
        }
        int bracketDepth = 0;
        int parenDepth = 0;
        char quote = '\u0000';
        for (int i = 0; i < value.length(); ++i) {
            char ch = value.charAt(i);
            if (quote != '\u0000') {
                if (ch != quote || i != 0 && value.charAt(i - 1) == '\\') continue;
                quote = '\u0000';
                continue;
            }
            if (ch == '\'' || ch == '\"') {
                quote = ch;
                continue;
            }
            if (ch == '[') {
                ++bracketDepth;
                continue;
            }
            if (ch == ']') {
                bracketDepth = Math.max(0, bracketDepth - 1);
                continue;
            }
            if (ch == '(') {
                ++parenDepth;
                continue;
            }
            if (ch == ')') {
                parenDepth = Math.max(0, parenDepth - 1);
                continue;
            }
            if (ch != delimiter || bracketDepth != 0 || parenDepth != 0) continue;
            return i;
        }
        return -1;
    }

    public static List<String> splitTopLevel(String value, char delimiter) {
        ArrayList<String> result = new ArrayList<String>();
        if (value == null || value.isEmpty()) {
            return result;
        }
        int start = 0;
        int bracketDepth = 0;
        int parenDepth = 0;
        char quote = '\u0000';
        for (int i = 0; i < value.length(); ++i) {
            char ch = value.charAt(i);
            if (quote != '\u0000') {
                if (ch != quote || i != 0 && value.charAt(i - 1) == '\\') continue;
                quote = '\u0000';
                continue;
            }
            if (ch == '\'' || ch == '\"') {
                quote = ch;
                continue;
            }
            if (ch == '[') {
                ++bracketDepth;
                continue;
            }
            if (ch == ']') {
                --bracketDepth;
                continue;
            }
            if (ch == '(') {
                ++parenDepth;
                continue;
            }
            if (ch == ')') {
                --parenDepth;
                continue;
            }
            if (ch != delimiter || bracketDepth != 0 || parenDepth != 0) continue;
            result.add(value.substring(start, i));
            start = i + 1;
        }
        result.add(value.substring(start));
        return result;
    }

    public static List<String> splitTopLevelTokens(String raw) {
        ArrayList<String> tokens = new ArrayList<String>();
        if (raw == null || raw.isBlank()) {
            return tokens;
        }
        StringBuilder current = new StringBuilder();
        int depth = 0;
        for (int i = 0; i < raw.length(); ++i) {
            char ch = raw.charAt(i);
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
            if (ch == '/' && depth == 0) {
                if (!current.isEmpty()) {
                    tokens.add(current.toString());
                    current.setLength(0);
                }
                tokens.add("/");
                continue;
            }
            current.append(ch);
        }
        if (!current.isEmpty()) {
            tokens.add(current.toString());
        }
        return tokens;
    }

    public static String normalizeDirection(String raw) {
        String value = raw == null ? "" : raw.trim().toLowerCase(Locale.ROOT);
        return "rtl".equals(value) ? "rtl" : "ltr";
    }

    public static String normalizeTextAlign(String raw) {
        String value;
        return switch (value = raw == null ? "" : raw.trim().toLowerCase(Locale.ROOT)) {
            case "left", "right", "center", "justify", "start", "end" -> value;
            default -> "start";
        };
    }

    public static String normalizeVerticalAlign(String raw) {
        String value;
        return switch (value = raw == null ? "" : raw.trim().toLowerCase(Locale.ROOT)) {
            case "baseline", "sub", "super", "top", "middle", "center", "bottom", "text-top", "text-bottom" -> value;
            default -> "baseline";
        };
    }

    public static String normalizeWhiteSpace(String raw) {
        String value;
        return switch (value = raw == null ? "" : raw.trim().toLowerCase(Locale.ROOT)) {
            case "normal", "nowrap", "pre", "pre-wrap", "pre-line", "break-spaces" -> value;
            default -> "normal";
        };
    }

    public static String normalizeTextDecoration(String raw) {
        if (raw == null || raw.isBlank()) {
            return "none";
        }
        String normalized = raw.trim().toLowerCase(Locale.ROOT);
        if (normalized.equals("unset") || normalized.equals("initial")) {
            return "none";
        }
        return normalized;
    }

    public static boolean isColorToken(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }
        String value = token.trim().toLowerCase(Locale.ROOT);
        if (Color.isColorKeyword(value)) {
            return true;
        }
        if (value.startsWith("#")) {
            return true;
        }
        return value.startsWith("rgb(") || value.startsWith("rgba(") || value.startsWith("hsl(") || value.startsWith("hsla(");
    }

    private static boolean isCssHexDigit(char value) {
        return Character.digit(value, 16) >= 0;
    }

    private static boolean isCssWhitespace(char value) {
        return value == ' ' || value == '\t' || value == '\r' || value == '\n' || value == '\f';
    }

    private static boolean isCssNewline(char value) {
        return value == '\r' || value == '\n' || value == '\f';
    }
}

