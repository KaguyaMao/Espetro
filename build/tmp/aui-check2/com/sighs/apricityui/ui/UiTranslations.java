/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.ui;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class UiTranslations {
    private static final Pattern JSON_ENTRY = Pattern.compile("\\\"((?:\\\\.|[^\\\"])*)\\\"\\s*:\\s*\\\"((?:\\\\.|[^\\\"])*)\\\"");
    private static final Map<String, String> FALLBACK_TRANSLATIONS = UiTranslations.loadFallbackTranslations();

    private UiTranslations() {
    }

    public static String translate(String key) {
        String translated = UiTranslations.minecraftTranslation(key);
        return translated == null ? FALLBACK_TRANSLATIONS.getOrDefault(key, key) : translated;
    }

    private static String minecraftTranslation(String key) {
        try {
            Class<?> minecraft = Class.forName("net.minecraft.client.Minecraft");
            if (minecraft.getMethod("getInstance", new Class[0]).invoke(null, new Object[0]) == null) {
                return null;
            }
            Class<?> component = Class.forName("net.minecraft.network.chat.Component");
            Object value = component.getMethod("translatable", String.class, Object[].class).invoke(null, key, new Object[0]);
            return (String)component.getMethod("getString", new Class[0]).invoke(value, new Object[0]);
        }
        catch (LinkageError | ReflectiveOperationException ignored) {
            return null;
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private static Map<String, String> loadFallbackTranslations() {
        try (InputStream input = UiTranslations.class.getResourceAsStream("/assets/apricityui/lang/en_us.json");){
            if (input == null) {
                Map<String, String> map2 = Map.of();
                return map2;
            }
            Matcher matcher = JSON_ENTRY.matcher(new String(input.readAllBytes(), StandardCharsets.UTF_8));
            LinkedHashMap<String, String> translations = new LinkedHashMap<String, String>();
            while (matcher.find()) {
                translations.put(UiTranslations.unescape(matcher.group(1)), UiTranslations.unescape(matcher.group(2)));
            }
            Map<String, String> map = Map.copyOf(translations);
            return map;
        }
        catch (IOException ignored) {
            return Map.of();
        }
    }

    private static String unescape(String value) {
        return value.replace("\\\\\"", "\"").replace("\\\\\\\\", "\\");
    }
}

