/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.dom;

import com.sighs.apricityui.init.Element;
import java.util.Locale;

public final class TextTransform {
    private TextTransform() {
    }

    public static String apply(String value, Element element) {
        if (element == null) {
            return value;
        }
        String transform = element.getComputedStyle().textTransform;
        return TextTransform.apply(value, transform, TextTransform.languageOf(element));
    }

    public static String apply(String value, String transform, String language) {
        if (value == null || value.isEmpty() || transform == null) {
            return value;
        }
        Locale locale = language == null || language.isBlank() ? Locale.ROOT : Locale.forLanguageTag(language);
        return switch (transform.trim().toLowerCase(Locale.ROOT)) {
            case "uppercase" -> value.toUpperCase(locale);
            case "lowercase" -> value.toLowerCase(locale);
            case "capitalize" -> TextTransform.capitalize(value, locale);
            default -> value;
        };
    }

    private static String languageOf(Element element) {
        Element current = element;
        while (current != null) {
            String language = current.getAttribute("lang");
            if (language != null && !language.isBlank()) {
                return language;
            }
            current = current.parentElement;
        }
        return "";
    }

    private static String capitalize(String value, Locale locale) {
        int codePoint;
        StringBuilder result = new StringBuilder(value.length());
        boolean wordStart = true;
        for (int offset = 0; offset < value.length(); offset += Character.charCount(codePoint)) {
            codePoint = value.codePointAt(offset);
            String character = new String(Character.toChars(codePoint));
            if (wordStart && Character.isLetter(codePoint)) {
                result.append(character.toUpperCase(locale));
                wordStart = false;
            } else {
                result.append(character);
                if (Character.isLetterOrDigit(codePoint)) {
                    wordStart = false;
                }
            }
            if (!Character.isWhitespace(codePoint)) continue;
            wordStart = true;
        }
        return result.toString();
    }
}

