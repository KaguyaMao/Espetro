/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.util.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/*
 * Uses 'sealed' constructs - enablewith --sealed true
 */
public enum NameConverter {
    CAMEL_CASE{

        @Override
        public String joinWords(List<String> words) {
            if (words.isEmpty()) {
                return "";
            }
            StringBuilder result = new StringBuilder(words.get(0).toLowerCase(Locale.ROOT));
            for (int i = 1; i < words.size(); ++i) {
                String word = words.get(i);
                if (word.isEmpty()) continue;
                result.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1).toLowerCase(Locale.ROOT));
            }
            return result.toString();
        }
    }
    ,
    PASCAL_CASE{

        @Override
        public String joinWords(List<String> words) {
            StringBuilder result = new StringBuilder();
            for (String word : words) {
                if (word.isEmpty()) continue;
                result.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1).toLowerCase(Locale.ROOT));
            }
            return result.toString();
        }
    }
    ,
    SNAKE_CASE{

        @Override
        public String joinWords(List<String> words) {
            return String.join((CharSequence)"_", words).toLowerCase(Locale.ROOT);
        }
    };


    public abstract String joinWords(List<String> var1);

    public static List<String> splitName(String name) {
        ArrayList<String> words = new ArrayList<String>();
        if (name == null || name.isEmpty()) {
            return words;
        }
        if (name.contains("_")) {
            for (String word : name.split("_")) {
                if (word.isEmpty()) continue;
                words.add(word.toLowerCase(Locale.ROOT));
            }
            return words;
        }
        StringBuilder currentWord = new StringBuilder();
        for (int i = 0; i < name.length(); ++i) {
            char c = name.charAt(i);
            if (Character.isUpperCase(c) && i > 0 && !currentWord.isEmpty()) {
                words.add(currentWord.toString().toLowerCase(Locale.ROOT));
                currentWord = new StringBuilder();
            }
            currentWord.append(c);
        }
        if (!currentWord.isEmpty()) {
            words.add(currentWord.toString().toLowerCase(Locale.ROOT));
        }
        return words;
    }
}

