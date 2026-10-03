/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.network.chat.Style
 */
package cc.sighs.auratip.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

public final class ResolveUtil {
    private ResolveUtil() {
    }

    public static Component resolveVariables(@Nullable Component component, @Nullable Map<String, ?> variables) {
        if (component == null || variables == null || variables.isEmpty()) {
            return (Component)Objects.requireNonNullElse(component, Component.m_237119_());
        }
        Map<String, Component> componentVars = ResolveUtil.toComponentMap(variables);
        if (componentVars.isEmpty()) {
            return component;
        }
        return ResolveUtil.resolveVariablesInternal(component, componentVars);
    }

    public static Map<String, Component> toComponentMap(@Nullable Map<String, ?> variables) {
        if (variables == null || variables.isEmpty()) {
            return Map.of();
        }
        HashMap<String, MutableComponent> out = new HashMap<String, MutableComponent>();
        for (Map.Entry<String, ?> entry : variables.entrySet()) {
            MutableComponent mutableComponent;
            String key = entry.getKey();
            Object value = entry.getValue();
            if (key == null || key.isEmpty() || value == null) continue;
            if (value instanceof Component) {
                Component c = (Component)value;
                mutableComponent = c;
            } else {
                mutableComponent = Component.m_237113_((String)value.toString());
            }
            out.put(key, mutableComponent);
        }
        return out.isEmpty() ? Map.of() : out;
    }

    @Nonnull
    private static Component resolveVariablesInternal(Component component, Map<String, Component> variables) {
        List flat = component.m_240407_();
        boolean hasPlaceholder = false;
        for (Object part : flat) {
            String text = part.getString();
            if (!text.contains("${")) continue;
            hasPlaceholder = true;
            break;
        }
        if (!hasPlaceholder) {
            return component;
        }
        ArrayList<Object> resultParts = new ArrayList<Object>();
        for (Component part : flat) {
            Style style = part.m_7383_();
            String text = part.getString();
            if (!text.contains("${")) {
                if (text.isEmpty()) continue;
                resultParts.add(Component.m_237113_((String)text).m_130948_(style));
                continue;
            }
            resultParts.addAll(ResolveUtil.resolveInLiteral(text, style, variables, 0));
        }
        if (resultParts.isEmpty()) {
            return Component.m_237119_();
        }
        MutableComponent result = Component.m_237119_();
        for (Component component2 : resultParts) {
            result.m_7220_(component2);
        }
        return result;
    }

    private static List<Component> resolveInLiteral(String text, Style baseStyle, Map<String, Component> variables, int depth) {
        ArrayList<Component> parts = new ArrayList<Component>();
        if (text.isEmpty()) {
            return parts;
        }
        StringBuilder buffer = new StringBuilder();
        int length = text.length();
        int i = 0;
        while (i < length) {
            int endIndex;
            char c = text.charAt(i);
            if (c == '$' && i + 1 < length && text.charAt(i + 1) == '{' && (endIndex = text.indexOf(125, i + 2)) > i + 2) {
                String key;
                Component valueComponent;
                if (!buffer.isEmpty()) {
                    parts.add((Component)Component.m_237113_((String)buffer.toString()).m_130948_(baseStyle));
                    buffer.setLength(0);
                }
                if ((valueComponent = variables.get(key = text.substring(i + 2, endIndex))) != null) {
                    Component resolvedValue = depth < 4 ? ResolveUtil.resolveVariablesInternal(valueComponent, variables) : valueComponent;
                    List valueParts = resolvedValue.m_178405_(baseStyle);
                    parts.addAll(valueParts);
                }
                i = endIndex + 1;
                continue;
            }
            buffer.append(c);
            ++i;
        }
        if (!buffer.isEmpty()) {
            parts.add((Component)Component.m_237113_((String)buffer.toString()).m_130948_(baseStyle));
        }
        return parts;
    }
}

