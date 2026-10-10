/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.style;

import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.parser.CssString;
import com.sighs.apricityui.style.Style;
import java.lang.reflect.Field;

public final class VarResolver {
    private static final int MAX_DEPTH = 8;

    private VarResolver() {
    }

    public static String normalizeCustomPropertyName(String name) {
        if (name.startsWith("--")) {
            return name;
        }
        return "--" + name;
    }

    public static void resolveReferences(Style style, Element context) {
        for (Field field : Style.STYLE_FIELDS) {
            try {
                String resolved;
                String value = (String)field.get(style);
                if (value == null || !value.contains("var(") || (resolved = VarResolver.resolveVarInValue(style, value, context, 0)).equals(value)) continue;
                field.set(style, resolved);
            }
            catch (IllegalAccessException illegalAccessException) {
                // empty catch block
            }
        }
    }

    private static String resolveVarInValue(Style style, String value, Element context, int depth) {
        if (value == null || !value.contains("var(") || depth >= 8) {
            return value;
        }
        StringBuilder result = new StringBuilder();
        int i = 0;
        int len = value.length();
        while (i < len) {
            String varName;
            int varStart = value.indexOf("var(", i);
            if (varStart < 0) {
                result.append(value, i, len);
                break;
            }
            result.append(value, i, varStart);
            int parenDepth = 0;
            int contentStart = varStart + 4;
            int closeIndex = -1;
            for (int j = varStart; j < len; ++j) {
                char c = value.charAt(j);
                if (c == '(') {
                    ++parenDepth;
                    continue;
                }
                if (c != ')' || --parenDepth != 0) continue;
                closeIndex = j;
                break;
            }
            if (closeIndex < 0) {
                result.append(value, varStart, len);
                break;
            }
            String inner = value.substring(contentStart, closeIndex).trim();
            String fallback = null;
            int commaIndex = CssString.findTopLevelDelimiter(inner, ',');
            if (commaIndex >= 0) {
                varName = inner.substring(0, commaIndex).trim();
                fallback = inner.substring(commaIndex + 1).trim();
            } else {
                varName = inner.trim();
            }
            String resolved = VarResolver.lookupVar(style, varName, context);
            if (resolved != null && !resolved.isBlank()) {
                result.append(VarResolver.resolveVarInValue(style, resolved, context, depth + 1));
            } else if (fallback != null) {
                result.append(VarResolver.resolveVarInValue(style, fallback, context, depth + 1));
            } else {
                result.append(value, varStart, closeIndex + 1);
            }
            i = closeIndex + 1;
        }
        return result.toString();
    }

    private static String lookupVar(Style style, String varName, Element context) {
        if (varName == null || varName.isBlank()) {
            return null;
        }
        String normalized = VarResolver.normalizeCustomPropertyName(varName);
        String local = style.getCustomProperty(normalized);
        if (local != null && !local.isBlank()) {
            return local;
        }
        Element current = context;
        while (current != null) {
            String inherited = current.getRawCustomProperty(normalized);
            if (inherited != null && !inherited.isBlank()) {
                return inherited;
            }
            current = current.parentElement;
        }
        return null;
    }
}

