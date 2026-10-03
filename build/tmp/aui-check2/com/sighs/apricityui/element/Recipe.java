/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 */
package com.sighs.apricityui.element;

import com.sighs.apricityui.element.MinecraftElement;
import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.registry.annotation.ElementRegister;
import java.util.ArrayList;
import net.minecraft.resources.ResourceLocation;

@ElementRegister(value="RECIPE")
public class Recipe
extends MinecraftElement {
    public static final String TAG_NAME = "RECIPE";

    public Recipe(Document document) {
        super(document, TAG_NAME);
    }

    public ResourceLocation parseRecipeIdFromInnerText() {
        String normalized = Recipe.normalizeRecipeIdLiteral(this.innerText);
        if (normalized.isBlank()) {
            return null;
        }
        return ResourceLocation.m_135820_((String)normalized);
    }

    public boolean clearGeneratedRecipeSlots() {
        boolean changed = false;
        ArrayList childrenSnapshot = new ArrayList(this.children);
        for (Element child : childrenSnapshot) {
            String generatedTag = child.getAttribute("data-generated");
            if (generatedTag == null || generatedTag.isBlank() || !generatedTag.startsWith("recipe")) continue;
            child.remove();
            changed = true;
        }
        return changed;
    }

    public static String normalizeRecipeIdLiteral(String raw) {
        if (raw == null) {
            return "";
        }
        String normalized = raw.trim();
        if (normalized.isBlank()) {
            return "";
        }
        if (normalized.length() >= 2) {
            boolean quoted;
            char first = normalized.charAt(0);
            char last = normalized.charAt(normalized.length() - 1);
            boolean bl = quoted = first == '\"' && last == '\"' || first == '\'' && last == '\'';
            if (quoted) {
                normalized = normalized.substring(1, normalized.length() - 1).trim();
            }
        }
        return normalized;
    }
}

