/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.style;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class InlineStyleDeclaration {
    private InlineStyleDeclaration() {
    }

    public static LinkedHashMap<String, String> parse(String source) {
        LinkedHashMap<String, String> declarations = new LinkedHashMap<String, String>();
        for (Entry entry : InlineStyleDeclaration.parseEntries(source)) {
            InlineStyleDeclaration.addDeclaration(declarations, entry.property(), entry.value());
        }
        return declarations;
    }

    public static List<Entry> parseEntries(String source) {
        ArrayList<Entry> entries = new ArrayList<Entry>();
        if (source == null || source.isBlank()) {
            return entries;
        }
        StringBuilder declaration = new StringBuilder();
        char quote = '\u0000';
        boolean escaped = false;
        int parentheses = 0;
        for (int index = 0; index <= source.length(); ++index) {
            char current;
            char c = current = index == source.length() ? (char)';' : (char)source.charAt(index);
            if (escaped) {
                declaration.append(current);
                escaped = false;
                continue;
            }
            if (current == '\\' && quote != '\u0000') {
                declaration.append(current);
                escaped = true;
                continue;
            }
            if (quote != '\u0000') {
                declaration.append(current);
                if (current != quote) continue;
                quote = '\u0000';
                continue;
            }
            if (current == '\'' || current == '\"') {
                quote = current;
                declaration.append(current);
                continue;
            }
            if (current == '(') {
                ++parentheses;
            }
            if (current == ')' && parentheses > 0) {
                --parentheses;
            }
            if (current == ';' && parentheses == 0) {
                InlineStyleDeclaration.addEntry(entries, declaration.toString());
                declaration.setLength(0);
                continue;
            }
            declaration.append(current);
        }
        return entries;
    }

    private static void addEntry(List<Entry> entries, String raw) {
        int colon = InlineStyleDeclaration.findTopLevelColon(raw);
        if (colon < 0) {
            return;
        }
        String property = InlineStyleDeclaration.normalizeProperty(raw.substring(0, colon));
        String value = raw.substring(colon + 1).trim();
        if (!property.isBlank() && !value.isBlank()) {
            entries.add(new Entry(property, value));
        }
    }

    public static String serialize(Map<String, String> declarations) {
        StringBuilder result = new StringBuilder();
        if (declarations == null) {
            return "";
        }
        declarations.forEach((property, value) -> {
            String key = InlineStyleDeclaration.normalizeProperty(property);
            if (key.isBlank() || value == null || value.isBlank()) {
                return;
            }
            if (!result.isEmpty()) {
                result.append(' ');
            }
            result.append(key).append(": ").append(value.trim()).append(';');
        });
        return result.toString();
    }

    public static String serialize(List<Entry> entries) {
        StringBuilder result = new StringBuilder();
        if (entries == null) {
            return "";
        }
        for (Entry entry : entries) {
            if (entry == null || entry.property().isBlank() || entry.value() == null || entry.value().isBlank()) continue;
            if (!result.isEmpty()) {
                result.append(' ');
            }
            result.append(entry.property()).append(": ").append(entry.value().trim()).append(';');
        }
        return result.toString();
    }

    public static String lastValue(List<Entry> entries, String property) {
        if (entries == null) {
            return null;
        }
        for (int index = entries.size() - 1; index >= 0; --index) {
            Entry entry = entries.get(index);
            if (entry == null || !entry.property().equals(property)) continue;
            return entry.value();
        }
        return null;
    }

    public static void removeAll(List<Entry> entries, String property) {
        if (entries == null) {
            return;
        }
        entries.removeIf(entry -> entry != null && entry.property().equals(property));
    }

    public static String normalizeProperty(String property) {
        if (property == null) {
            return "";
        }
        String normalized = property.trim();
        if (normalized.startsWith("--")) {
            return normalized;
        }
        return InlineStyleDeclaration.camelToKebab(normalized).toLowerCase(Locale.ROOT);
    }

    public static String valueWithoutPriority(String value) {
        if (value == null) {
            return "";
        }
        String normalized = value.trim();
        int marker = InlineStyleDeclaration.priorityMarkerIndex(normalized);
        return marker < 0 ? normalized : normalized.substring(0, marker).trim();
    }

    public static String priorityOf(String value) {
        if (value == null) {
            return "";
        }
        return InlineStyleDeclaration.priorityMarkerIndex(value.trim()) < 0 ? "" : "important";
    }

    private static void addDeclaration(LinkedHashMap<String, String> target, String property, String value) {
        String existing = target.get(property);
        if (existing != null && "important".equals(InlineStyleDeclaration.priorityOf(existing)) && !"important".equals(InlineStyleDeclaration.priorityOf(value))) {
            return;
        }
        target.remove(property);
        target.put(property, value);
    }

    private static int findTopLevelColon(String source) {
        char quote = '\u0000';
        boolean escaped = false;
        int parentheses = 0;
        for (int index = 0; index < source.length(); ++index) {
            char current = source.charAt(index);
            if (escaped) {
                escaped = false;
                continue;
            }
            if (current == '\\' && quote != '\u0000') {
                escaped = true;
                continue;
            }
            if (quote != '\u0000') {
                if (current != quote) continue;
                quote = '\u0000';
                continue;
            }
            if (current == '\'' || current == '\"') {
                quote = current;
                continue;
            }
            if (current == '(') {
                ++parentheses;
                continue;
            }
            if (current == ')' && parentheses > 0) {
                --parentheses;
                continue;
            }
            if (current != ':' || parentheses != 0) continue;
            return index;
        }
        return -1;
    }

    private static int priorityMarkerIndex(String value) {
        char quote = '\u0000';
        boolean escaped = false;
        int parentheses = 0;
        int marker = -1;
        for (int index = 0; index < value.length(); ++index) {
            char current = value.charAt(index);
            if (escaped) {
                escaped = false;
                continue;
            }
            if (current == '\\') {
                escaped = true;
                continue;
            }
            if (quote != '\u0000') {
                if (current != quote) continue;
                quote = '\u0000';
                continue;
            }
            if (current == '\'' || current == '\"') {
                quote = current;
                continue;
            }
            if (current == '(') {
                ++parentheses;
                continue;
            }
            if (current == ')' && parentheses > 0) {
                --parentheses;
                continue;
            }
            if (current != '!' || parentheses != 0) continue;
            marker = index;
        }
        if (marker < 0) {
            return -1;
        }
        String suffix = value.substring(marker + 1).trim();
        return "important".equalsIgnoreCase(suffix) ? marker : -1;
    }

    private static String camelToKebab(String input) {
        StringBuilder result = new StringBuilder(input.length() + 8);
        for (int index = 0; index < input.length(); ++index) {
            char current = input.charAt(index);
            if (Character.isUpperCase(current)) {
                result.append('-').append(Character.toLowerCase(current));
                continue;
            }
            result.append(current);
        }
        return result.toString();
    }

    public record Entry(String property, String value) {
    }
}

