/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.dev.devtools;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class DevToolsTranslations {
    private static final Pattern JSON_ENTRY = Pattern.compile("\\\"((?:\\\\.|[^\\\"])*)\\\"\\s*:\\s*\\\"((?:\\\\.|[^\\\"])*)\\\"");
    private static final Map<String, String> FALLBACK_TRANSLATIONS = DevToolsTranslations.loadFallbackTranslations();

    private DevToolsTranslations() {
    }

    static String translate(String key, Object ... arguments) {
        String translated = DevToolsTranslations.minecraftTranslation(key, arguments);
        if (translated != null) {
            return translated;
        }
        String fallback = FALLBACK_TRANSLATIONS.getOrDefault(key, key);
        try {
            return String.format(Locale.ROOT, fallback, arguments);
        }
        catch (RuntimeException ignored) {
            return fallback;
        }
    }

    private static String minecraftTranslation(String key, Object ... arguments) {
        try {
            Class<?> minecraftClass = Class.forName("net.minecraft.client.Minecraft");
            if (minecraftClass.getMethod("getInstance", new Class[0]).invoke(null, new Object[0]) == null) {
                return null;
            }
            Class<?> componentClass = Class.forName("net.minecraft.network.chat.Component");
            Object component = componentClass.getMethod("translatable", String.class, Object[].class).invoke(null, key, arguments);
            return (String)componentClass.getMethod("getString", new Class[0]).invoke(component, new Object[0]);
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
        try (InputStream input = DevToolsTranslations.class.getResourceAsStream("/assets/apricityui/lang/en_us.json");){
            if (input == null) {
                Map<String, String> map2 = Map.of();
                return map2;
            }
            String json = new String(input.readAllBytes(), StandardCharsets.UTF_8);
            LinkedHashMap<String, String> translations = new LinkedHashMap<String, String>();
            Matcher matcher = JSON_ENTRY.matcher(json);
            while (matcher.find()) {
                translations.put(DevToolsTranslations.unescape(matcher.group(1)), DevToolsTranslations.unescape(matcher.group(2)));
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

